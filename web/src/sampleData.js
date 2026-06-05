export const sampleData = {
  generatedAt: '2026-05-19T20:00:00',
  stats: {
    Pacientes: 3,
    Consultas: 2,
    'Alertas abertos': 3,
    Planos: 2
  },
  patients: [
    {
      id: 1,
      name: 'Ana Maria Souza',
      clinicalNotes: 'Ansiedade alimentar relatada e episódios de baixa energia durante semanas de estresse.',
      eatingHistory: 'Relata preocupação geral com alimentação. Sem padrão específico confirmado nesta ficha inicial.',
      consultations: [
        {
          id: 101,
          patientName: 'Ana Maria Souza',
          date: '2026-05-19',
          summary: 'IA identificou relato alimentar inespecífico e recomendou aprofundamento clínico antes de classificar padrões comportamentais.',
          alerts: [
            { type: 'Necessidade de aprofundamento', severity: 'LEVE', message: 'Relato geral sobre dificuldade com alimentação precisa de perguntas de triagem.' }
          ],
          report: 'PARTE 1 - Identificação e achados da IA\nPaciente: Ana Maria Souza\nConsulta: 2026-05-19\nObjetivo: análise comportamental assistida por IA.\n\nPARTE 2 - Relato e impressão clínica\nA IA destacou ansiedade alimentar moderada, culpa após refeições e baixa energia. A decisão clínica permanece com o nutricionista.\n\nPARTE 3 - Recomendações e plano\nRefeições menores e mais frequentes, hidratação, monitoramento de sinais de culpa e revisão semanal do plano.\n\nLimitações\nA IA apoia a decisão, mas não substitui anamnese nem avaliação presencial.'
        }
      ],
      plans: [
        {
          objective: 'Reduzir ansiedade alimentar',
          status: 'EM_REVISAO',
          description: 'Plano inicial com café da manhã simples, lanche intermediário e diário alimentar emocional.'
        }
      ]
    },
    {
      id: 2,
      name: 'Rafael Costa',
      clinicalNotes: 'Busca reeducação alimentar e controle de episódios de excesso noturno.',
      eatingHistory: 'Alterna restrição durante o dia e excesso no período noturno.',
      consultations: [
        {
          id: 102,
          patientName: 'Rafael Costa',
          date: '2026-05-18',
          summary: 'IA detectou risco grave de compulsão alimentar associado a restrição diurna e perda de controle à noite.',
          alerts: [
            { type: 'Compulsão alimentar', severity: 'GRAVE', message: 'Relato de perda de controle no período noturno.' }
          ],
          report: 'PARTE 1 - Identificação e achados da IA\nPaciente: Rafael Costa\nAchado principal: compulsão alimentar grave.\n\nPARTE 2 - Relato e impressão clínica\nO padrão de restrição e excesso sugere necessidade de intervenção e possível encaminhamento.\n\nPARTE 3 - Recomendações e plano\nRegularidade alimentar, acompanhamento próximo e avaliação de equipe multiprofissional.\n\nLimitações\nA IA não fecha diagnóstico.'
        }
      ],
      plans: [
        {
          objective: 'Regularidade alimentar',
          status: 'APROVADO',
          description: 'Organização de refeições principais, lanche noturno planejado e monitoramento de episódios.'
        }
      ]
    },
    {
      id: 3,
      name: 'Julia Fernandes',
      clinicalNotes: 'Acompanhamento preventivo.',
      eatingHistory: 'Boa adesão, com queixas pontuais de baixa energia.',
      consultations: [],
      plans: []
    }
  ]
};
