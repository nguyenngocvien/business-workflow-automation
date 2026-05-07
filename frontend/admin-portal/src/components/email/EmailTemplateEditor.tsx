import { useMemo, useState } from 'react';
import { navIcons } from '../layout/navIcons';
import { Card } from '../ui/Card';
import { IconButton } from '../ui/IconButton';
import type { EmailTemplateRecord } from '../../types/emailTemplate';

type EmailTemplateEditorProps = {
  template: EmailTemplateRecord | null;
  loading: boolean;
  saving: boolean;
  onChange: (template: EmailTemplateRecord) => void;
  onSave: () => void;
  onEnableEdit: () => void;
  readOnly: boolean;
};

const placeholders = [
  '{CUSTOMER_NAME}',
  '{CIF}',
  '{TRANSACTION_CODE}',
  '{HK_TRANSACTION_CODE}',
  '{TOTAL_MONEY}',
];

export function EmailTemplateEditor({
  template,
  loading,
  saving,
  onChange,
  onSave,
  onEnableEdit,
  readOnly,
}: EmailTemplateEditorProps) {
  const [previewVisible, setPreviewVisible] = useState(false);

  const previewContent = useMemo(() => template?.content || '<p style="color:red">No HTML content</p>', [template]);

  if (!template && !loading) {
    return (
      <Card className="bg-slate-950 text-white">
        <p className="text-sm font-semibold uppercase tracking-[0.25em] text-cyan-300">Template editor</p>
        <h3 className="mt-3 text-2xl font-bold">Select a template</h3>
        <p className="mt-3 text-sm leading-7 text-slate-300">
          This editor mirrors the Thymeleaf modal. Choose an existing template or create a new one to edit metadata,
          subject, and HTML content.
        </p>
      </Card>
    );
  }

  return (
    <Card>
      <div className="flex items-start justify-between gap-3">
        <div>
          <p className="text-sm font-semibold uppercase tracking-[0.25em] text-brand-500">Template editor</p>
          <h3 className="mt-2 text-2xl font-bold text-slate-950">
            {loading ? 'Loading...' : template?.id ? template.templateCode : 'Add Email Template'}
          </h3>
        </div>
        <div className="flex gap-2">
          {template?.id && readOnly ? (
            <IconButton onClick={onEnableEdit} icon={navIcons.edit} label="Edit template" tone="primary" />
          ) : null}
          <IconButton
            onClick={onSave}
            disabled={saving || loading || readOnly}
            icon={navIcons.save}
            label="Save template"
            tone="success"
          />
        </div>
      </div>

      {loading ? (
        <div className="theme-soft mt-8 rounded-3xl px-6 py-10 text-center text-sm">
          Loading template...
        </div>
      ) : (
        <div className="mt-8 space-y-6">
          <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
            <label className="grid gap-2 text-sm">
              <span>Process Code</span>
              <input
                value={template?.processCode || ''}
                onChange={(event) => onChange({ ...template!, processCode: event.target.value })}
                readOnly={readOnly && Boolean(template?.id)}
                className="theme-input rounded-2xl px-4 py-3"
              />
            </label>
            <label className="grid gap-2 text-sm">
              <span>Template Type</span>
              <input
                value={template?.templateType || ''}
                onChange={(event) => onChange({ ...template!, templateType: event.target.value })}
                readOnly={readOnly && Boolean(template?.id)}
                className="theme-input rounded-2xl px-4 py-3"
              />
            </label>
            <label className="grid gap-2 text-sm">
              <span>Template Code</span>
              <input
                value={template?.templateCode || ''}
                onChange={(event) => onChange({ ...template!, templateCode: event.target.value })}
                readOnly={readOnly && Boolean(template?.id)}
                className="theme-input rounded-2xl px-4 py-3"
              />
            </label>
            <label className="grid gap-2 text-sm">
              <span>Active</span>
              <select
                value={String(template?.active ?? true)}
                onChange={(event) => onChange({ ...template!, active: event.target.value === 'true' })}
                disabled={readOnly}
                className="theme-input rounded-2xl px-4 py-3"
              >
                <option value="true">Active</option>
                <option value="false">Inactive</option>
              </select>
            </label>
          </div>

          <label className="grid gap-2 text-sm">
            <span>Email Subject</span>
            <input
              value={template?.title || ''}
              onChange={(event) => onChange({ ...template!, title: event.target.value })}
              readOnly={readOnly}
              className="theme-input rounded-2xl px-4 py-3"
            />
          </label>

          <div className="theme-surface rounded-2xl p-4">
            <div className="flex items-center justify-between gap-3">
              <span className="theme-strong-text text-sm font-semibold">Email HTML Content</span>
              <div className="flex gap-2">
                <IconButton
                  onClick={() => setPreviewVisible((current) => !current)}
                  icon={navIcons.info}
                  label={previewVisible ? 'Hide preview' : 'Show preview'}
                  size="sm"
                />
                <IconButton
                  onClick={() => navigator.clipboard.writeText(template?.content || '')}
                  icon={navIcons.logs}
                  label="Copy template content"
                  size="sm"
                />
              </div>
            </div>

            {previewVisible ? (
              <iframe
                title="Email preview"
                srcDoc={previewContent}
                className="theme-input mt-4 h-[500px] w-full rounded-2xl"
                sandbox="allow-same-origin"
              />
            ) : (
              <textarea
                value={template?.content || ''}
                onChange={(event) => onChange({ ...template!, content: event.target.value })}
                readOnly={readOnly}
                rows={20}
                className="theme-code-panel mt-4 w-full rounded-2xl p-4 font-mono text-sm text-emerald-300"
              />
            )}
          </div>

          <div className="theme-soft rounded-2xl border border-dashed px-4 py-4 text-sm">
            Available placeholders: {placeholders.join(', ')}
          </div>
        </div>
      )}
    </Card>
  );
}
