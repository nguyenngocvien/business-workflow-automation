import { api } from './api';
import type {
  EmailTemplatePageResult,
  EmailTemplateRecord,
  EmailTemplateSearchFilters,
} from '../types/emailTemplate';

type ActionResponse = {
  success?: boolean;
};

export async function fetchEmailTemplates(filters: EmailTemplateSearchFilters) {
  const { data } = await api.get<EmailTemplatePageResult>('/email-templates', {
    params: {
      keyword: filters.keyword || undefined,
      processCode: filters.processCode || undefined,
      templateType: filters.templateType || undefined,
      active: filters.active || undefined,
      page: filters.page,
      size: filters.size,
    },
  });
  return data;
}

export async function fetchEmailTemplate(id: number) {
  const { data } = await api.get<EmailTemplateRecord>(`/email-templates/${id}`);
  return data;
}

export async function fetchNewEmailTemplate() {
  const { data } = await api.get<EmailTemplateRecord>('/email-templates/new');
  return data;
}

export async function saveEmailTemplate(template: EmailTemplateRecord) {
  const { data } = await api.post<ActionResponse>('/email-templates', template);
  return data;
}

export async function updateEmailTemplateStatus(id: number, active: boolean) {
  const { data } = await api.post<ActionResponse>(`/email-templates/${id}/status`, undefined, {
    params: { active },
  });
  return data;
}
