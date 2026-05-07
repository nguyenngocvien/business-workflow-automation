import { navIcons } from '../layout/navIcons';
import { IconButton } from '../ui/IconButton';
import type { ServiceConfig, ServiceFormOptions, SqlParam } from '../../types/service';

type ServiceCreatePanelProps = {
  service: ServiceConfig | null;
  options: ServiceFormOptions | null;
  loading: boolean;
  saving: boolean;
  readOnly?: boolean;
  onChange: (service: ServiceConfig) => void;
  onSave: () => void;
  onEnableEdit?: () => void;
};

function SectionTitle({ title, description }: { title: string; description?: string }) {
  return (
    <div>
      <p className="text-[11px] font-semibold uppercase tracking-[0.18em] text-slate-400">{title}</p>
      {description ? <p className="mt-1 text-xs text-slate-400">{description}</p> : null}
    </div>
  );
}

function FieldLabel({ children }: { children: string }) {
  return <span className="text-xs text-slate-300">{children}</span>;
}

function newSqlParam(paramIndex: number): SqlParam {
  return {
    paramIndex,
    mode: 'IN',
    required: false,
  };
}

export function ServiceCreatePanel({
  service,
  options,
  loading,
  saving,
  readOnly = false,
  onChange,
  onSave,
  onEnableEdit,
}: ServiceCreatePanelProps) {
  if (!service && !loading) {
    return (
      <div className="rounded-xl bg-slate-950 px-6 py-8 text-white">
        <h3 className="text-2xl font-bold">Add Service</h3>
        <p className="mt-3 text-sm text-slate-300">Loading service defaults...</p>
      </div>
    );
  }

  function updateField<K extends keyof ServiceConfig>(key: K, value: ServiceConfig[K]) {
    if (!service) {
      return;
    }

    onChange({
      ...service,
      [key]: value,
    });
  }

  function updateApiField(key: string, value: string | number | undefined) {
    if (!service) {
      return;
    }

    onChange({
      ...service,
      apiConfig: {
        ...service.apiConfig,
        [key]: value,
      },
    });
  }

  function updateSmtpField(key: string, value: string | number | boolean | undefined) {
    if (!service) {
      return;
    }

    onChange({
      ...service,
      smtpConfig: {
        ...service.smtpConfig,
        [key]: value,
      },
    });
  }

  function updateSqlField(key: string, value: string | undefined) {
    if (!service) {
      return;
    }

    onChange({
      ...service,
      sqlConfig: {
        ...service.sqlConfig,
        [key]: value,
      },
    });
  }

  function updateSqlParam(index: number, patch: Partial<SqlParam>) {
    if (!service) {
      return;
    }

    const params = [...(service.sqlConfig?.params ?? [])];
    params[index] = {
      ...params[index],
      ...patch,
    };

    onChange({
      ...service,
      sqlConfig: {
        ...service.sqlConfig,
        params,
      },
    });
  }

  function addSqlParam() {
    if (!service) {
      return;
    }

    const nextIndex = (service.sqlConfig?.params ?? []).length + 1;

    onChange({
      ...service,
      sqlConfig: {
        ...service.sqlConfig,
        params: [...(service.sqlConfig?.params ?? []), newSqlParam(nextIndex)],
      },
    });
  }

  function removeSqlParam(index: number) {
    if (!service) {
      return;
    }

    const params = (service.sqlConfig?.params ?? [])
      .filter((_, itemIndex) => itemIndex !== index)
      .map((param, itemIndex) => ({
        ...param,
        paramIndex: itemIndex + 1,
      }));

    onChange({
      ...service,
      sqlConfig: {
        ...service.sqlConfig,
        params,
      },
    });
  }

  const serviceType = service?.serviceType ?? '';
  const authType = service?.apiConfig?.authType ?? '';
  const sqlExecuteType = service?.sqlConfig?.sqlExecuteType ?? '';

  return (
    <div className="p-4 sm:p-5">
      {loading ? (
        <div className="theme-soft mt-4 rounded-xl px-6 py-10 text-center text-sm">
          Loading service form...
        </div>
      ) : (
        <div className="space-y-5">
          <div className="flex flex-wrap gap-4">
            <label className="flex items-center gap-2 text-xs text-slate-300">
              <input
                type="checkbox"
                checked={Boolean(service?.status)}
                onChange={(event) => updateField('status', event.target.checked)}
                disabled={readOnly}
                className="h-4 w-4 rounded border-slate-500 bg-transparent outline-none focus:border-brand-500"
              />
              Active
            </label>
            <label className="flex items-center gap-2 text-xs text-slate-300">
              <input
                type="checkbox"
                checked={Boolean(service?.logOn)}
                onChange={(event) => updateField('logOn', event.target.checked)}
                disabled={readOnly}
                className="h-4 w-4 rounded border-slate-500 bg-transparent outline-none focus:border-brand-500"
              />
              Enable Logging
            </label>
          </div>

          <div className="grid gap-3 md:grid-cols-2">
            <label className="grid gap-2">
              <FieldLabel>Application</FieldLabel>
              <input
                value={service?.appName ?? ''}
                onChange={(event) => updateField('appName', event.target.value)}
                readOnly={readOnly}
                className="theme-input min-w-0px-3 py-2 text-xs outline-none focus:border-brand-500"
                placeholder="Enter app name"
              />
            </label>
            <label className="grid gap-2">
              <FieldLabel>System Name</FieldLabel>
              <input
                value={service?.systemName ?? ''}
                onChange={(event) => updateField('systemName', event.target.value)}
                readOnly={readOnly}
                className="theme-input min-w-0px-3 py-2 text-xs outline-none focus:border-brand-500"
                placeholder="e.g. CORE"
              />
            </label>
          </div>

          <div className="grid gap-3 md:grid-cols-2 xl:grid-cols-[minmax(0,1fr)_180px_140px]">
            <label className="grid gap-2">
              <FieldLabel>Service Name</FieldLabel>
              <input
                value={service?.serviceName ?? ''}
                onChange={(event) => updateField('serviceName', event.target.value)}
                readOnly={readOnly}
                className="theme-input min-w-0px-3 py-2 text-xs outline-none focus:border-brand-500"
                placeholder="Enter service code"
              />
            </label>
            <label className="grid gap-2">
              <FieldLabel>Service Type</FieldLabel>
              <select
                value={serviceType}
                onChange={(event) => updateField('serviceType', event.target.value as ServiceConfig['serviceType'])}
                disabled={readOnly}
                className="theme-input min-w-0px-3 py-2 text-xs outline-none focus:border-brand-500"
              >
                <option value="">-- Select --</option>
                <option value="API">EXTERNAL API</option>
                <option value="EMAIL">EMAIL</option>
                <option value="SQL">SQL</option>
              </select>
            </label>
            <label className="grid gap-2">
              <FieldLabel>Version</FieldLabel>
              <input
                value={service?.version ?? ''}
                onChange={(event) => updateField('version', event.target.value)}
                readOnly={readOnly}
                className="theme-input min-w-0px-3 py-2 text-xs outline-none focus:border-brand-500"
                placeholder="e.g. 1.0"
              />
            </label>
          </div>

          <label className="grid gap-2">
            <FieldLabel>Description</FieldLabel>
            <textarea
              value={service?.description ?? ''}
              onChange={(event) => updateField('description', event.target.value)}
              readOnly={readOnly}
              rows={2}
              className="theme-input min-w-0px-3 py-2 text-xs outline-none focus:border-brand-500"
              placeholder="Service description"
            />
          </label>

          {serviceType === 'API' ? (
            <section className="space-y-3 rounded-xl border border-[var(--border-subtle)] p-4">
              <SectionTitle title="API Configuration" />

              <div className="grid gap-3 md:grid-cols-2 xl:grid-cols-[150px_170px_minmax(0,1fr)]">
                <label className="grid gap-2">
                  <FieldLabel>Type</FieldLabel>
                  <select
                    value={service?.apiConfig?.apiType ?? ''}
                    onChange={(event) => updateApiField('apiType', event.target.value)}
                    disabled={readOnly}
                    className="theme-input min-w-0px-3 py-2 text-xs outline-none focus:border-brand-500"
                  >
                    <option value="">-- Select --</option>
                    <option value="REST">REST</option>
                    <option value="SOAP">SOAP</option>
                  </select>
                </label>
                <label className="grid gap-2">
                  <FieldLabel>HTTP Method</FieldLabel>
                  <select
                    value={service?.apiConfig?.method ?? ''}
                    onChange={(event) => updateApiField('method', event.target.value)}
                    disabled={readOnly}
                    className="theme-input min-w-0px-3 py-2 text-xs outline-none focus:border-brand-500"
                  >
                    <option value="">-- Select --</option>
                    <option value="GET">GET</option>
                    <option value="POST">POST</option>
                    <option value="PUT">PUT</option>
                    <option value="DELETE">DELETE</option>
                    <option value="PATCH">PATCH</option>
                  </select>
                </label>
                <label className="grid gap-2">
                  <FieldLabel>URL</FieldLabel>
                  <input
                    value={service?.apiConfig?.url ?? ''}
                    onChange={(event) => updateApiField('url', event.target.value)}
                    readOnly={readOnly}
                    className="theme-input min-w-0px-3 py-2 text-xs outline-none focus:border-brand-500"
                    placeholder="api/services/example"
                  />
                </label>
              </div>

              <label className="grid gap-2">
                <FieldLabel>Auth Type</FieldLabel>
                <select
                  value={authType}
                  onChange={(event) => updateApiField('authType', event.target.value)}
                  disabled={readOnly}
                  className="theme-input min-w-0px-3 py-2 text-xs outline-none focus:border-brand-500"
                >
                  <option value="">NONE</option>
                  <option value="BASIC">Basic Authentication</option>
                  <option value="API_KEY">API Key</option>
                  <option value="TOKEN">Bearer Token</option>
                  <option value="OAUTH2">OAuth2 Client Credential</option>
                </select>
              </label>

              {authType === 'BASIC' ? (
                <div className="grid gap-3 md:grid-cols-2">
                  <label className="grid gap-2">
                    <FieldLabel>Username</FieldLabel>
                    <input
                      value={service?.apiConfig?.username ?? ''}
                      onChange={(event) => updateApiField('username', event.target.value)}
                      readOnly={readOnly}
                      className="theme-input min-w-0px-3 py-2 text-xs outline-none focus:border-brand-500"
                      placeholder="Username"
                    />
                  </label>
                  <label className="grid gap-2">
                    <FieldLabel>Password</FieldLabel>
                    <input
                      type="password"
                      value={service?.apiConfig?.password ?? ''}
                      onChange={(event) => updateApiField('password', event.target.value)}
                      readOnly={readOnly}
                      className="theme-input min-w-0px-3 py-2 text-xs outline-none focus:border-brand-500"
                      placeholder="Password"
                    />
                  </label>
                </div>
              ) : null}

              {authType === 'TOKEN' ? (
                <label className="grid gap-2">
                  <FieldLabel>Bearer Token</FieldLabel>
                  <input
                    value={service?.apiConfig?.token ?? ''}
                    onChange={(event) => updateApiField('token', event.target.value)}
                    readOnly={readOnly}
                    className="theme-input min-w-0px-3 py-2 text-xs outline-none focus:border-brand-500"
                    placeholder="Token"
                  />
                </label>
              ) : null}

              {authType === 'API_KEY' ? (
                <div className="grid gap-3 md:grid-cols-2">
                  <label className="grid gap-2">
                    <FieldLabel>API Key Header</FieldLabel>
                    <input
                      value={service?.apiConfig?.apiKeyHeader ?? ''}
                      onChange={(event) => updateApiField('apiKeyHeader', event.target.value)}
                      readOnly={readOnly}
                      className="theme-input min-w-0px-3 py-2 text-xs outline-none focus:border-brand-500"
                      placeholder="X-API-KEY"
                    />
                  </label>
                  <label className="grid gap-2">
                    <FieldLabel>API Key Value</FieldLabel>
                    <input
                      value={service?.apiConfig?.apiKeyValue ?? ''}
                      onChange={(event) => updateApiField('apiKeyValue', event.target.value)}
                      readOnly={readOnly}
                      className="theme-input min-w-0px-3 py-2 text-xs outline-none focus:border-brand-500"
                      placeholder="API key value"
                    />
                  </label>
                </div>
              ) : null}

              {authType === 'OAUTH2' ? (
                <div className="grid gap-3 md:grid-cols-2">
                  <label className="grid gap-2">
                    <FieldLabel>Client ID</FieldLabel>
                    <input
                      value={service?.apiConfig?.oauth2ClientId ?? ''}
                      onChange={(event) => updateApiField('oauth2ClientId', event.target.value)}
                      readOnly={readOnly}
                      className="theme-input min-w-0px-3 py-2 text-xs outline-none focus:border-brand-500"
                      placeholder="Client ID"
                    />
                  </label>
                  <label className="grid gap-2">
                    <FieldLabel>Client Secret</FieldLabel>
                    <input
                      type="password"
                      value={service?.apiConfig?.oauth2ClientSecret ?? ''}
                      onChange={(event) => updateApiField('oauth2ClientSecret', event.target.value)}
                      readOnly={readOnly}
                      className="theme-input min-w-0px-3 py-2 text-xs outline-none focus:border-brand-500"
                      placeholder="Client Secret"
                    />
                  </label>
                  <label className="grid gap-2 md:col-span-2">
                    <FieldLabel>Token URL</FieldLabel>
                    <input
                      value={service?.apiConfig?.oauth2TokenUrl ?? ''}
                      onChange={(event) => updateApiField('oauth2TokenUrl', event.target.value)}
                      readOnly={readOnly}
                      className="theme-input min-w-0px-3 py-2 text-xs outline-none focus:border-brand-500"
                      placeholder="https://..."
                    />
                  </label>
                </div>
              ) : null}

              <label className="grid max-w-[220px] gap-2">
                <FieldLabel>Timeout (ms)</FieldLabel>
                <input
                  type="number"
                  value={service?.apiConfig?.timeout ?? ''}
                  onChange={(event) =>
                    updateApiField('timeout', event.target.value ? Number(event.target.value) : undefined)
                  }
                  readOnly={readOnly}
                  className="theme-input min-w-0px-3 py-2 text-xs outline-none focus:border-brand-500"
                  placeholder="10000"
                />
              </label>

              <label className="grid gap-2">
                <FieldLabel>Request Template</FieldLabel>
                <textarea
                  value={service?.apiConfig?.requestTemplate ?? ''}
                  onChange={(event) => updateApiField('requestTemplate', event.target.value)}
                  readOnly={readOnly}
                  rows={7}
                  className="theme-code-panel min-w-0p-3 text-xs text-emerald-300 outline-none focus:border-brand-500"
                  placeholder="Enter request template here..."
                />
              </label>
            </section>
          ) : null}

          {serviceType === 'EMAIL' ? (
            <section className="space-y-3 rounded-xl border border-[var(--border-subtle)] p-4">
              <SectionTitle title="SMTP Configuration" description="" />

              <div className="grid gap-3 md:grid-cols-[minmax(0,1fr)_140px]">
                <label className="grid gap-2">
                  <FieldLabel>SMTP Host</FieldLabel>
                  <input
                    value={service?.smtpConfig?.host ?? ''}
                    onChange={(event) => updateSmtpField('host', event.target.value)}
                    readOnly={readOnly}
                    className="theme-input min-w-0px-3 py-2 text-xs outline-none focus:border-brand-500"
                    placeholder="smtp.example.com"
                  />
                </label>
                <label className="grid gap-2">
                  <FieldLabel>Port</FieldLabel>
                  <input
                    type="number"
                    value={service?.smtpConfig?.port ?? ''}
                    onChange={(event) =>
                      updateSmtpField('port', event.target.value ? Number(event.target.value) : undefined)
                    }
                    readOnly={readOnly}
                    className="theme-input min-w-0px-3 py-2 text-xs outline-none focus:border-brand-500"
                    placeholder="587"
                  />
                </label>
              </div>

              <div className="grid gap-3 md:grid-cols-2">
                <label className="grid gap-2">
                  <FieldLabel>Username</FieldLabel>
                  <input
                    value={service?.smtpConfig?.username ?? ''}
                    onChange={(event) => updateSmtpField('username', event.target.value)}
                    readOnly={readOnly}
                    className="theme-input min-w-0px-3 py-2 text-xs outline-none focus:border-brand-500"
                  />
                </label>
                <label className="grid gap-2">
                  <FieldLabel>Password</FieldLabel>
                  <input
                    type="password"
                    value={service?.smtpConfig?.password ?? ''}
                    onChange={(event) => updateSmtpField('password', event.target.value)}
                    readOnly={readOnly}
                    className="theme-input min-w-0px-3 py-2 text-xs outline-none focus:border-brand-500"
                  />
                </label>
              </div>

              <div className="flex flex-wrap gap-4">
                <label className="flex items-center gap-2 text-xs text-slate-300">
                  <input
                    type="checkbox"
                    checked={Boolean(service?.smtpConfig?.auth)}
                    onChange={(event) => updateSmtpField('auth', event.target.checked)}
                    disabled={readOnly}
                    className="h-4 w-4 rounded border-slate-500 bg-transparent outline-none focus:border-brand-500"
                  />
                  Auth
                </label>
                <label className="flex items-center gap-2 text-xs text-slate-300">
                  <input
                    type="checkbox"
                    checked={Boolean(service?.smtpConfig?.ssl)}
                    onChange={(event) => updateSmtpField('ssl', event.target.checked)}
                    disabled={readOnly}
                    className="h-4 w-4 rounded border-slate-500 bg-transparent outline-none focus:border-brand-500"
                  />
                  SSL
                </label>
                <label className="flex items-center gap-2 text-xs text-slate-300">
                  <input
                    type="checkbox"
                    checked={Boolean(service?.smtpConfig?.debug)}
                    onChange={(event) => updateSmtpField('debug', event.target.checked)}
                    disabled={readOnly}
                    className="h-4 w-4 rounded border-slate-500 bg-transparent outline-none focus:border-brand-500"
                  />
                  Debug
                </label>
              </div>

              <label className="grid gap-2">
                <FieldLabel>From Address</FieldLabel>
                <input
                  value={service?.smtpConfig?.fromAddress ?? ''}
                  onChange={(event) => updateSmtpField('fromAddress', event.target.value)}
                  readOnly={readOnly}
                  className="theme-input min-w-0px-3 py-2 text-xs outline-none focus:border-brand-500"
                />
              </label>
            </section>
          ) : null}

          {serviceType === 'SQL' ? (
            <section className="space-y-3 rounded-xl border border-[var(--border-subtle)] p-4">
              <SectionTitle title="SQL Configuration"/>

              <div className="grid gap-3 md:grid-cols-2">
                <label className="grid gap-2">
                  <FieldLabel>Datasource</FieldLabel>
                  <select
                    value={service?.sqlConfig?.dataSourceName ?? ''}
                    onChange={(event) => updateSqlField('dataSourceName', event.target.value)}
                    disabled={readOnly}
                    className="theme-input min-w-0px-3 py-2 text-xs outline-none focus:border-brand-500"
                  >
                    <option value="">-- Select --</option>
                    {(options?.datasources ?? []).map((datasource, index) => (
                      <option
                        key={datasource.datasourceName ?? datasource.url ?? `datasource-${index}`}
                        value={datasource.datasourceName ?? ''}
                      >
                        {datasource.label ?? datasource.datasourceName ?? datasource.url ?? `Datasource ${index + 1}`}
                      </option>
                    ))}
                  </select>
                </label>

                <label className="grid gap-2">
                  <FieldLabel>Execute Type</FieldLabel>
                  <select
                    value={sqlExecuteType}
                    onChange={(event) => updateSqlField('sqlExecuteType', event.target.value)}
                    disabled={readOnly}
                    className="theme-input min-w-0px-3 py-2 text-xs outline-none focus:border-brand-500"
                  >
                    <option value="">-- Select --</option>
                    <option value="PROCEDURE">Stored Procedure</option>
                    <option value="QUERY">Query (SELECT)</option>
                    <option value="UPDATE">Update (INSERT / UPDATE / DELETE)</option>
                  </select>
                </label>
              </div>

              {sqlExecuteType === 'PROCEDURE' ? (
                <div className="grid gap-3 md:grid-cols-3">
                  <label className="grid gap-2">
                    <FieldLabel>Schema</FieldLabel>
                    <input
                      value={service?.sqlConfig?.schema ?? ''}
                      onChange={(event) => updateSqlField('schema', event.target.value)}
                      readOnly={readOnly}
                      className="theme-input min-w-0px-3 py-2 text-xs outline-none focus:border-brand-500"
                      placeholder="CORE_APP"
                    />
                  </label>
                  <label className="grid gap-2">
                    <FieldLabel>Package</FieldLabel>
                    <input
                      value={service?.sqlConfig?.packageName ?? ''}
                      onChange={(event) => updateSqlField('packageName', event.target.value)}
                      readOnly={readOnly}
                      className="theme-input min-w-0px-3 py-2 text-xs outline-none focus:border-brand-500"
                      placeholder="PKG_USER"
                    />
                  </label>
                  <label className="grid gap-2">
                    <FieldLabel>Procedure Name</FieldLabel>
                    <input
                      value={service?.sqlConfig?.procedureName ?? ''}
                      onChange={(event) => updateSqlField('procedureName', event.target.value)}
                      readOnly={readOnly}
                      className="theme-input min-w-0px-3 py-2 text-xs outline-none focus:border-brand-500"
                      placeholder="CREATE_USER"
                    />
                  </label>
                </div>
              ) : null}

              {sqlExecuteType === 'QUERY' || sqlExecuteType === 'UPDATE' ? (
                <label className="grid gap-2">
                  <FieldLabel>SQL Statement</FieldLabel>
                  <textarea
                    value={service?.sqlConfig?.sqlStatement ?? ''}
                    onChange={(event) => updateSqlField('sqlStatement', event.target.value)}
                    readOnly={readOnly}
                    rows={5}
                    className="theme-code-panel min-w-0p-3 text-xs text-emerald-300 outline-none focus:border-brand-500"
                    placeholder="SELECT * FROM users WHERE id = :id"
                  />
                </label>
              ) : null}

              <div className="space-y-4">
                <div className="flex items-center justify-between gap-3">
                  <SectionTitle title="SQL Parameters" />
                  {!readOnly ? <IconButton onClick={addSqlParam} icon={navIcons.plus} label="Add SQL parameter" size="sm" /> : null}
                </div>

                <div className="space-y-3">
                  {(service?.sqlConfig?.params ?? []).length === 0 ? (
                    <div className="theme-soft rounded-2xl px-4 py-4 text-sm text-slate-400">
                      No SQL parameters yet.
                    </div>
                  ) : (
                    (service?.sqlConfig?.params ?? []).map((param, index) => {
                      const mode = param.mode ?? 'IN';
                      const showInFields = mode === 'IN' || mode === 'INOUT';
                      const showOutFields = mode === 'OUT' || mode === 'INOUT';

                      return (
                        <div key={`${index}-${param.name ?? 'param'}`} className="grid gap-3 rounded-2xl border border-[var(--border-subtle)] p-3 md:grid-cols-2 xl:grid-cols-12">
                        <label className="grid gap-2 md:col-span-1">
                          <FieldLabel>ID</FieldLabel>
                          <input
                            type="number"
                            value={param.paramIndex ?? index + 1}
                            readOnly
                            className="theme-input min-w-0px-2.5 py-2 text-xs outline-none focus:border-brand-500"
                          />
                        </label>
                        <label className="grid gap-2 md:col-span-2">
                          <FieldLabel>Param</FieldLabel>
                          <input
                            value={param.name ?? ''}
                            onChange={(event) => updateSqlParam(index, { name: event.target.value })}
                            readOnly={readOnly}
                            className="theme-input min-w-0px-2.5 py-2 text-xs outline-none focus:border-brand-500"
                          />
                        </label>
                        <label className="grid gap-2 md:col-span-2">
                          <FieldLabel>Mode</FieldLabel>
                          <select
                            value={param.mode ?? 'IN'}
                            onChange={(event) => updateSqlParam(index, { mode: event.target.value as SqlParam['mode'] })}
                            disabled={readOnly}
                            className="theme-input min-w-0px-2.5 py-2 text-xs outline-none focus:border-brand-500"
                          >
                            <option value="IN">IN</option>
                            <option value="OUT">OUT</option>
                            <option value="INOUT">INOUT</option>
                          </select>
                        </label>
                        <label className="grid gap-2 md:col-span-2">
                          <FieldLabel>Type</FieldLabel>
                          <select
                            value={param.sqlType ?? ''}
                            onChange={(event) => updateSqlParam(index, { sqlType: event.target.value })}
                            disabled={readOnly}
                            className="theme-input min-w-0px-2.5 py-2 text-xs outline-none focus:border-brand-500"
                          >
                            <option value="">-- Type --</option>
                            {(options?.sqlTypes ?? []).map((sqlType) => (
                              <option key={sqlType} value={sqlType}>
                                {sqlType}
                              </option>
                            ))}
                          </select>
                        </label>
                        {showInFields ? (
                          <>
                            <label className="grid gap-2 md:col-span-2">
                              <FieldLabel>Input</FieldLabel>
                              <input
                                value={param.inputMapping ?? ''}
                                onChange={(event) => updateSqlParam(index, { inputMapping: event.target.value })}
                                readOnly={readOnly}
                                className="theme-input min-w-0px-2.5 py-2 text-xs outline-none focus:border-brand-500"
                              />
                            </label>
                            <label className="grid gap-2 md:col-span-2">
                              <FieldLabel>Default</FieldLabel>
                              <input
                                value={
                                  param.defaultValue == null
                                    ? ''
                                    : typeof param.defaultValue === 'string' || typeof param.defaultValue === 'number'
                                      ? param.defaultValue
                                      : String(param.defaultValue)
                                }
                                onChange={(event) => updateSqlParam(index, { defaultValue: event.target.value })}
                                readOnly={readOnly}
                                className="theme-input min-w-0px-2.5 py-2 text-xs outline-none focus:border-brand-500"
                              />
                            </label>
                          </>
                        ) : null}
                        {showOutFields ? (
                          <label className="grid gap-2 md:col-span-2">
                            <FieldLabel>Output</FieldLabel>
                            <input
                              value={param.outputMapping ?? ''}
                              onChange={(event) => updateSqlParam(index, { outputMapping: event.target.value })}
                              readOnly={readOnly}
                              className="theme-input min-w-0px-2.5 py-2 text-xs outline-none focus:border-brand-500"
                            />
                          </label>
                        ) : null}
                        <label className="flex items-center gap-2 md:col-span-1 md:justify-center md:self-end">
                          <input
                            type="checkbox"
                            checked={Boolean(param.required)}
                            onChange={(event) => updateSqlParam(index, { required: event.target.checked })}
                            disabled={readOnly}
                            className="h-4 w-4 rounded border-slate-500 bg-transparent outline-none focus:border-brand-500"
                          />
                          <span className="text-xs text-slate-300 md:hidden">Required</span>
                        </label>
                          {!readOnly ? (
                            <div className="md:col-span-12 flex justify-end">
                              <IconButton
                                onClick={() => removeSqlParam(index)}
                                icon={navIcons.close}
                                label="Remove SQL parameter"
                                size="sm"
                                tone="danger"
                              />
                            </div>
                          ) : null}
                        </div>
                      );
                    })
                  )}
                </div>
              </div>
            </section>
          ) : null}

          {service?.id ? (
            <label className="grid gap-2">
              <FieldLabel>Configuration (JSON)</FieldLabel>
              <textarea
                value={service?.detailJson ?? ''}
                onChange={(event) => updateField('detailJson', event.target.value)}
                readOnly={readOnly}
                rows={14}
                className="theme-code-panel min-w-0p-3 text-xs text-emerald-300 outline-none focus:border-brand-500"
                placeholder="Enter JSON config here..."
              />
            </label>
          ) : null}

          <div className="sticky bottom-0 z-10 -mx-4 mt-1 bg-transparent px-4 py-3 sm:-mx-5 sm:px-5">
            <div className="flex justify-center">
              {readOnly ? (
                <IconButton
                  onClick={onEnableEdit}
                  disabled={loading || !service}
                  icon={navIcons.edit}
                  label="Edit service"
                  tone="neutral"
                  size="sm"
                  className="text-sky-500"
                />
              ) : (
                <IconButton
                  onClick={onSave}
                  disabled={saving || loading || !service}
                  icon={navIcons.save}
                  label="Save service"
                  tone="neutral"
                  size="sm"
                  className="text-emerald-600"
                />
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
