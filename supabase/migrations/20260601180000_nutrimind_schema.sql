create extension if not exists pgcrypto;
create schema if not exists private;
revoke all on schema private from public;
grant usage on schema private to authenticated;

create table if not exists public.profiles (
  id uuid primary key references auth.users(id) on delete cascade,
  full_name text not null,
  role text not null default 'NUTRITIONIST' check (role in ('NUTRITIONIST', 'ADMIN')),
  crn text,
  created_at timestamptz not null default now()
);

create table if not exists public.patients (
  id uuid primary key default gen_random_uuid(),
  owner_id uuid not null references public.profiles(id),
  name text not null,
  cpf text not null,
  birth_date date,
  phone text default '',
  email text default '',
  clinical_notes text default '',
  eating_history text default '',
  active boolean not null default true,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists public.consultations (
  id uuid primary key default gen_random_uuid(),
  patient_id uuid not null references public.patients(id) on delete cascade,
  nutritionist_id uuid not null references public.profiles(id),
  consent_audio boolean not null default false,
  consent_video boolean not null default false,
  clinical_notes text default '',
  manual_transcript text default '',
  transcript text default '',
  visual_notes text default '',
  status text not null default 'ANALYZED',
  ai_summary text not null,
  ai_model text not null,
  created_at timestamptz not null default now()
);

create table if not exists public.alerts (
  id uuid primary key default gen_random_uuid(),
  consultation_id uuid not null references public.consultations(id) on delete cascade,
  type text not null,
  severity text not null check (severity in ('LEVE', 'MODERADO', 'GRAVE')),
  justification text not null,
  message text not null,
  status text not null default 'OPEN' check (status in ('OPEN', 'DECIDED')),
  decision text,
  decision_notes text,
  decided_at timestamptz,
  created_at timestamptz not null default now()
);

create table if not exists public.reports (
  id uuid primary key default gen_random_uuid(),
  consultation_id uuid not null unique references public.consultations(id) on delete cascade,
  identification_section text not null,
  clinical_section text not null,
  recommendations_section text not null,
  limitations_section text not null,
  created_at timestamptz not null default now()
);

create table if not exists public.meal_plans (
  id uuid primary key default gen_random_uuid(),
  patient_id uuid not null references public.patients(id) on delete cascade,
  consultation_id uuid references public.consultations(id) on delete set null,
  objective text not null,
  description text not null,
  status text not null default 'IN_REVIEW' check (status in ('IN_REVIEW', 'APPROVED')),
  approved_by uuid references public.profiles(id),
  approved_at timestamptz,
  created_at timestamptz not null default now()
);

create table if not exists public.audit_logs (
  id bigint generated always as identity primary key,
  user_id uuid references public.profiles(id),
  action text not null,
  details jsonb not null default '{}'::jsonb,
  created_at timestamptz not null default now()
);

create or replace function public.handle_new_user()
returns trigger
language plpgsql
security definer set search_path = public
as $$
begin
  insert into public.profiles (id, full_name, crn)
  values (
    new.id,
    coalesce(new.raw_user_meta_data ->> 'full_name', split_part(new.email, '@', 1)),
    new.raw_user_meta_data ->> 'crn'
  )
  on conflict (id) do nothing;
  return new;
end;
$$;

drop trigger if exists on_auth_user_created on auth.users;
create trigger on_auth_user_created
  after insert on auth.users
  for each row execute procedure public.handle_new_user();

create or replace function private.is_admin()
returns boolean
language sql
stable
security definer set search_path = public
as $$
  select exists (
    select 1
    from public.profiles
    where id = auth.uid() and role = 'ADMIN'
  );
$$;

revoke all on function public.handle_new_user() from public, anon, authenticated;
revoke all on function private.is_admin() from public;
grant execute on function private.is_admin() to authenticated;

alter table public.profiles enable row level security;
alter table public.patients enable row level security;
alter table public.consultations enable row level security;
alter table public.alerts enable row level security;
alter table public.reports enable row level security;
alter table public.meal_plans enable row level security;
alter table public.audit_logs enable row level security;

create policy "profiles_read_own_or_admin" on public.profiles
  for select using (id = (select auth.uid()) or (select private.is_admin()));

create policy "patients_read_own_or_admin" on public.patients
  for select using (owner_id = (select auth.uid()) or (select private.is_admin()));
create policy "patients_insert_own" on public.patients
  for insert with check (owner_id = (select auth.uid()));
create policy "patients_update_own" on public.patients
  for update using (owner_id = (select auth.uid())) with check (owner_id = (select auth.uid()));

create policy "consultations_read_own_or_admin" on public.consultations
  for select using (nutritionist_id = (select auth.uid()) or (select private.is_admin()));
create policy "consultations_insert_own" on public.consultations
  for insert with check (nutritionist_id = (select auth.uid()));

create policy "alerts_read_linked_consultation" on public.alerts
  for select using (
    exists (
      select 1 from public.consultations
      where consultations.id = alerts.consultation_id
        and (consultations.nutritionist_id = (select auth.uid()) or (select private.is_admin()))
    )
  );
create policy "alerts_insert_linked_consultation" on public.alerts
  for insert with check (
    exists (
      select 1 from public.consultations
      where consultations.id = alerts.consultation_id
        and consultations.nutritionist_id = (select auth.uid())
    )
  );
create policy "alerts_update_linked_consultation" on public.alerts
  for update using (
    exists (
      select 1 from public.consultations
      where consultations.id = alerts.consultation_id
        and consultations.nutritionist_id = (select auth.uid())
    )
  );

create policy "reports_read_linked_consultation" on public.reports
  for select using (
    exists (
      select 1 from public.consultations
      where consultations.id = reports.consultation_id
        and (consultations.nutritionist_id = (select auth.uid()) or (select private.is_admin()))
    )
  );
create policy "reports_insert_linked_consultation" on public.reports
  for insert with check (
    exists (
      select 1 from public.consultations
      where consultations.id = reports.consultation_id
        and consultations.nutritionist_id = (select auth.uid())
    )
  );

create policy "meal_plans_read_own_or_admin" on public.meal_plans
  for select using (
    exists (
      select 1 from public.patients
      where patients.id = meal_plans.patient_id
        and (patients.owner_id = (select auth.uid()) or (select private.is_admin()))
    )
  );
create policy "meal_plans_insert_own" on public.meal_plans
  for insert with check (
    exists (
      select 1 from public.patients
      where patients.id = meal_plans.patient_id and patients.owner_id = (select auth.uid())
    )
  );
create policy "meal_plans_update_own" on public.meal_plans
  for update using (
    exists (
      select 1 from public.patients
      where patients.id = meal_plans.patient_id and patients.owner_id = (select auth.uid())
    )
  );

create policy "audit_read_own_or_admin" on public.audit_logs
  for select using (user_id = (select auth.uid()) or (select private.is_admin()));
create policy "audit_insert_own" on public.audit_logs
  for insert with check (user_id = (select auth.uid()));

create index if not exists patients_owner_id_idx on public.patients(owner_id);
create index if not exists consultations_patient_id_idx on public.consultations(patient_id);
create index if not exists consultations_nutritionist_id_idx on public.consultations(nutritionist_id);
create index if not exists alerts_consultation_id_idx on public.alerts(consultation_id);
create index if not exists meal_plans_patient_id_idx on public.meal_plans(patient_id);
create index if not exists meal_plans_consultation_id_idx on public.meal_plans(consultation_id);
create index if not exists meal_plans_approved_by_idx on public.meal_plans(approved_by);
create index if not exists audit_logs_user_id_idx on public.audit_logs(user_id);
