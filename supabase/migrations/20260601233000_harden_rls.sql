create schema if not exists private;
revoke all on schema private from public;
grant usage on schema private to authenticated;

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

drop policy "profiles_read_own_or_admin" on public.profiles;
create policy "profiles_read_own_or_admin" on public.profiles
  for select using (id = (select auth.uid()) or (select private.is_admin()));

drop policy "patients_read_own_or_admin" on public.patients;
create policy "patients_read_own_or_admin" on public.patients
  for select using (owner_id = (select auth.uid()) or (select private.is_admin()));
drop policy "patients_insert_own" on public.patients;
create policy "patients_insert_own" on public.patients
  for insert with check (owner_id = (select auth.uid()));
drop policy "patients_update_own" on public.patients;
create policy "patients_update_own" on public.patients
  for update using (owner_id = (select auth.uid())) with check (owner_id = (select auth.uid()));

drop policy "consultations_read_own_or_admin" on public.consultations;
create policy "consultations_read_own_or_admin" on public.consultations
  for select using (nutritionist_id = (select auth.uid()) or (select private.is_admin()));
drop policy "consultations_insert_own" on public.consultations;
create policy "consultations_insert_own" on public.consultations
  for insert with check (nutritionist_id = (select auth.uid()));

drop policy "alerts_read_linked_consultation" on public.alerts;
create policy "alerts_read_linked_consultation" on public.alerts
  for select using (
    exists (
      select 1 from public.consultations
      where consultations.id = alerts.consultation_id
        and (consultations.nutritionist_id = (select auth.uid()) or (select private.is_admin()))
    )
  );
drop policy "alerts_insert_linked_consultation" on public.alerts;
create policy "alerts_insert_linked_consultation" on public.alerts
  for insert with check (
    exists (
      select 1 from public.consultations
      where consultations.id = alerts.consultation_id
        and consultations.nutritionist_id = (select auth.uid())
    )
  );
drop policy "alerts_update_linked_consultation" on public.alerts;
create policy "alerts_update_linked_consultation" on public.alerts
  for update using (
    exists (
      select 1 from public.consultations
      where consultations.id = alerts.consultation_id
        and consultations.nutritionist_id = (select auth.uid())
    )
  );

drop policy "reports_read_linked_consultation" on public.reports;
create policy "reports_read_linked_consultation" on public.reports
  for select using (
    exists (
      select 1 from public.consultations
      where consultations.id = reports.consultation_id
        and (consultations.nutritionist_id = (select auth.uid()) or (select private.is_admin()))
    )
  );
drop policy "reports_insert_linked_consultation" on public.reports;
create policy "reports_insert_linked_consultation" on public.reports
  for insert with check (
    exists (
      select 1 from public.consultations
      where consultations.id = reports.consultation_id
        and consultations.nutritionist_id = (select auth.uid())
    )
  );

drop policy "meal_plans_read_own_or_admin" on public.meal_plans;
create policy "meal_plans_read_own_or_admin" on public.meal_plans
  for select using (
    exists (
      select 1 from public.patients
      where patients.id = meal_plans.patient_id
        and (patients.owner_id = (select auth.uid()) or (select private.is_admin()))
    )
  );
drop policy "meal_plans_insert_own" on public.meal_plans;
create policy "meal_plans_insert_own" on public.meal_plans
  for insert with check (
    exists (
      select 1 from public.patients
      where patients.id = meal_plans.patient_id and patients.owner_id = (select auth.uid())
    )
  );
drop policy "meal_plans_update_own" on public.meal_plans;
create policy "meal_plans_update_own" on public.meal_plans
  for update using (
    exists (
      select 1 from public.patients
      where patients.id = meal_plans.patient_id and patients.owner_id = (select auth.uid())
    )
  );

drop policy "audit_read_own_or_admin" on public.audit_logs;
create policy "audit_read_own_or_admin" on public.audit_logs
  for select using (user_id = (select auth.uid()) or (select private.is_admin()));
drop policy "audit_insert_own" on public.audit_logs;
create policy "audit_insert_own" on public.audit_logs
  for insert with check (user_id = (select auth.uid()));

drop function public.is_admin();

create index if not exists meal_plans_consultation_id_idx on public.meal_plans(consultation_id);
create index if not exists meal_plans_approved_by_idx on public.meal_plans(approved_by);
create index if not exists audit_logs_user_id_idx on public.audit_logs(user_id);
