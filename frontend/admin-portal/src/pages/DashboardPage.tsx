import { useEffect, useMemo } from 'react';
import { useQuery } from '@tanstack/react-query';
import { getEConnectorAPI, type ConnectionResult, type PageConnectionResult, type PagePipelineResult, type PageScheduleJobResult, type PageServiceResult, type PipelineResult, type ScheduleJobResult, type ServiceResult } from '../api/connector';
import { HealthSection } from '../components/dashboard/HealthSection';
import { MetricCard } from '../components/metric/MetricCard';
import { useNotify } from '../components/ui/NotificationProvider';
import { DEFAULT_CACHE_TIME, DEFAULT_STALE_TIME } from '../lib/queryClient';
import type { Metric } from '../types/dashboard';

type HealthItem = {
  name: string;
  subtitle: string;
  icon: 'database' | 'cloud' | 'folder';
  connected: boolean | null;
  message?: string;
};

const connectorApi = getEConnectorAPI();
const DASHBOARD_SAMPLE_SIZE = 5;

function formatCount(value?: number) {
  return new Intl.NumberFormat('en-US').format(value ?? 0);
}

function mapServiceItem(service: ServiceResult): HealthItem {
  return {
    name: service.serviceName ?? service.serviceCode ?? `Service ${service.id ?? ''}`.trim(),
    subtitle: [service.serviceType, service.appId, service.serviceVersion].filter(Boolean).join(' / ') || 'Service definition',
    icon: 'database',
    connected: Boolean(service.active),
    message: service.logEnable ? 'Logging enabled' : 'Logging disabled',
  };
}

function mapJobItem(job: ScheduleJobResult): HealthItem {
  return {
    name: job.jobName ?? job.jobCode ?? `Job ${job.id ?? ''}`.trim(),
    subtitle: [job.jobType, job.cronExpression, job.nextRunTime].filter(Boolean).join(' / ') || 'Scheduled job',
    icon: 'cloud',
    connected: Boolean(job.enabled),
    message: job.lastRunTime ? `Last run: ${job.lastRunTime}` : 'No execution history',
  };
}

function mapConnectionItem(connection: ConnectionResult): HealthItem {
  return {
    name: connection.connectionName ?? connection.connectionCode ?? `Connection ${connection.id ?? ''}`.trim(),
    subtitle: [connection.connectionType, connection.connectionCode].filter(Boolean).join(' / ') || 'Connection definition',
    icon: 'folder',
    connected: Boolean(connection.active),
    message: connection.active ? 'Connection available' : 'Connection disabled',
  };
}

function mapPipelineItem(pipeline: PipelineResult): HealthItem {
  return {
    name: pipeline.pipelineName ?? pipeline.pipelineCode ?? `Pipeline ${pipeline.id ?? ''}`.trim(),
    subtitle: pipeline.pipelineCode ?? pipeline.description ?? 'Pipeline definition',
    icon: 'folder',
    connected: Boolean(pipeline.active),
    message: pipeline.active ? 'Pipeline active' : 'Pipeline inactive',
  };
}

export function DashboardPage() {
  const notify = useNotify();

  const servicesQuery = useQuery({
    queryKey: ['connector', 'dashboard', 'services'] as const,
    queryFn: () =>
      connectorApi.findAll({
        page: 0,
        size: DASHBOARD_SAMPLE_SIZE,
        sort: ['createdAt,desc'],
      }),
    staleTime: DEFAULT_STALE_TIME,
    gcTime: DEFAULT_CACHE_TIME,
  });

  const jobsQuery = useQuery({
    queryKey: ['connector', 'dashboard', 'jobs'] as const,
    queryFn: () =>
      connectorApi.findAll1({
        page: 0,
        size: DASHBOARD_SAMPLE_SIZE,
        sort: ['createdAt,desc'],
      }),
    staleTime: DEFAULT_STALE_TIME,
    gcTime: DEFAULT_CACHE_TIME,
  });

  const pipelinesQuery = useQuery({
    queryKey: ['connector', 'dashboard', 'pipelines'] as const,
    queryFn: () =>
      connectorApi.findAll2({
        page: 0,
        size: DASHBOARD_SAMPLE_SIZE,
        sort: ['createdAt,desc'],
      }),
    staleTime: DEFAULT_STALE_TIME,
    gcTime: DEFAULT_CACHE_TIME,
  });

  const connectionsQuery = useQuery({
    queryKey: ['connector', 'dashboard', 'connections'] as const,
    queryFn: () =>
      connectorApi.findAll6({
        page: 0,
        size: DASHBOARD_SAMPLE_SIZE,
        sort: ['createdAt,desc'],
      }),
    staleTime: DEFAULT_STALE_TIME,
    gcTime: DEFAULT_CACHE_TIME,
  });

  const emailTemplatesQuery = useQuery({
    queryKey: ['connector', 'dashboard', 'email-templates'] as const,
    queryFn: () =>
      connectorApi.findAll5({
        page: 0,
        size: 1,
        sort: ['createdAt,desc'],
      }),
    staleTime: DEFAULT_STALE_TIME,
    gcTime: DEFAULT_CACHE_TIME,
  });

  const logsQuery = useQuery({
    queryKey: ['connector', 'dashboard', 'logs'] as const,
    queryFn: () =>
      connectorApi.findAll7({
        page: 0,
        size: 1,
        sort: ['createdAt,desc'],
      }),
    staleTime: DEFAULT_STALE_TIME,
    gcTime: DEFAULT_CACHE_TIME,
  });

  useEffect(() => {
    const queries = [
      { error: servicesQuery.error, label: 'services' },
      { error: jobsQuery.error, label: 'scheduled jobs' },
      { error: pipelinesQuery.error, label: 'pipelines' },
      { error: connectionsQuery.error, label: 'connections' },
      { error: emailTemplatesQuery.error, label: 'email templates' },
      { error: logsQuery.error, label: 'logs' },
    ] as const;

    for (const query of queries) {
      if (query.error) {
        notify.error(query.error instanceof Error ? query.error.message : `Failed to load ${query.label}.`);
      }
    }
  }, [connectionsQuery.error, emailTemplatesQuery.error, jobsQuery.error, logsQuery.error, notify, pipelinesQuery.error, servicesQuery.error]);

  const serviceItems = useMemo(
    () => (servicesQuery.data?.content ?? []).map(mapServiceItem),
    [servicesQuery.data],
  );

  const jobItems = useMemo(
    () => (jobsQuery.data?.content ?? []).map(mapJobItem),
    [jobsQuery.data],
  );

  const pipelineItems = useMemo(
    () => (pipelinesQuery.data?.content ?? []).map(mapPipelineItem),
    [pipelinesQuery.data],
  );

  const connectionItems = useMemo(
    () => (connectionsQuery.data?.content ?? []).map(mapConnectionItem),
    [connectionsQuery.data],
  );

  const serviceTotal = servicesQuery.data?.totalElements ?? 0;
  const activeServices = (servicesQuery.data?.content ?? []).filter((item) => item.active).length;
  const activeJobs = (jobsQuery.data?.content ?? []).filter((item) => item.enabled).length;
  const activePipelines = (pipelinesQuery.data?.content ?? []).filter((item) => item.active).length;
  const activeConnections = (connectionsQuery.data?.content ?? []).filter((item) => item.active).length;

  const metrics: Metric[] = [
    {
      id: 'services',
      label: 'Service Definitions',
      value: formatCount(serviceTotal),
      change: `${formatCount(activeServices)} active`,
      trend: activeServices > 0 ? 'up' : 'down',
      description: 'Registered services exposed by the connector API.',
    },
    {
      id: 'jobs',
      label: 'Scheduled Jobs',
      value: formatCount(jobsQuery.data?.totalElements),
      change: `${formatCount(activeJobs)} enabled`,
      trend: activeJobs > 0 ? 'up' : 'down',
      description: 'Scheduled executions currently configured.',
    },
    {
      id: 'pipelines',
      label: 'Pipelines',
      value: formatCount(pipelinesQuery.data?.totalElements),
      change: `${formatCount(activePipelines)} active`,
      trend: activePipelines > 0 ? 'up' : 'down',
      description: 'Active process pipelines in the platform.',
    },
    {
      id: 'connections',
      label: 'Connections',
      value: formatCount(connectionsQuery.data?.totalElements),
      change: `${formatCount(activeConnections)} active`,
      trend: activeConnections > 0 ? 'up' : 'down',
      description: 'Connection definitions available to services.',
    },
    {
      id: 'templates',
      label: 'Email Templates',
      value: formatCount(emailTemplatesQuery.data?.totalElements),
      change: 'Latest catalog',
      trend: 'up',
      description: 'Templates managed through the connector API.',
    },
    {
      id: 'logs',
      label: 'Recent Logs',
      value: formatCount(logsQuery.data?.totalElements),
      change: 'Latest events',
      trend: 'up',
      description: 'Tracked log entries reported by the platform.',
    },
  ];

  return (
    <div className="flex h-full min-h-0 flex-col gap-4 overflow-hidden p-4">
      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
        {metrics.map((metric) => (
          <MetricCard key={metric.id} metric={metric} />
        ))}
      </div>

      <div className="grid min-h-0 flex-1 gap-4 overflow-auto md:grid-cols-3">
        <HealthSection
          title="Services"
          items={serviceItems}
          loading={servicesQuery.isLoading}
          loadingLabel="Loading services..."
        />
        <HealthSection
          title="Scheduled Jobs"
          items={jobItems}
          loading={jobsQuery.isLoading}
          loadingLabel="Loading jobs..."
        />
        <HealthSection
          title="Connections"
          items={connectionItems}
          loading={connectionsQuery.isLoading}
          loadingLabel="Loading connections..."
        />
      </div>

      <div className="grid gap-4 xl:grid-cols-2">
        <HealthSection
          title="Pipelines"
          items={pipelineItems}
          loading={pipelinesQuery.isLoading}
          loadingLabel="Loading pipelines..."
        />
        <HealthSection
          title="Connector Summary"
          items={[
            {
              name: 'Email Templates',
              subtitle: `${formatCount(emailTemplatesQuery.data?.totalElements)} templates`,
              icon: 'folder',
              connected: true,
              message: 'Managed by /api/email-templates',
            },
            {
              name: 'Recent Logs',
              subtitle: `${formatCount(logsQuery.data?.totalElements)} logs`,
              icon: 'cloud',
              connected: true,
              message: 'Managed by /api/logs',
            },
          ]}
          loading={emailTemplatesQuery.isLoading || logsQuery.isLoading}
          loadingLabel="Loading connector summary..."
        />
      </div>
    </div>
  );
}
