import { useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import {
  getOpenAPIDefinition,
  type GroupResponse,
  type RoleResponse,
  type UserGroupResponse,
  type UserResponse,
  type UserRoleResponse,
} from '../api/identity';
import { UsersTable } from '../components/users/UsersTable';
import { useNotify } from '../components/ui/NotificationProvider';
import { DEFAULT_CACHE_TIME, DEFAULT_STALE_TIME } from '../lib/queryClient';
import type { User } from '../types/dashboard';

const identityApi = getOpenAPIDefinition();

const usersQueryKey = ['identity-users'] as const;

function formatName(user: UserResponse) {
  return (
    user.fullName?.trim() ||
    [user.firstName, user.lastName].filter(Boolean).join(' ').trim() ||
    user.username?.trim() ||
    user.externalId?.trim() ||
    `User ${user.id ?? ''}`.trim()
  );
}

function mapStatus(user: UserResponse): User['status'] {
  if (user.deleted) {
    return 'Offline';
  }

  if (user.status === 'INACTIVE' || user.status === 'LOCKED') {
    return 'Pending';
  }

  return 'Active';
}

function formatLastLogin(user: UserResponse) {
  const rawDate = user.updatedAt || user.createdAt;

  if (!rawDate) {
    return 'Never';
  }

  const parsedDate = new Date(rawDate);
  if (Number.isNaN(parsedDate.getTime())) {
    return 'Never';
  }

  return new Intl.DateTimeFormat('en-US', {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(parsedDate);
}

function getRoleLabel(
  userId: number | undefined,
  roleAssignmentsByUser: Map<number, UserRoleResponse[]>,
  rolesById: Map<number, RoleResponse>,
) {
  if (!userId) {
    return 'Unassigned';
  }

  const assignments = roleAssignmentsByUser.get(userId) ?? [];
  const primaryRoleId = assignments.find((item) => typeof item.roleId === 'number')?.roleId;

  if (!primaryRoleId) {
    return 'Unassigned';
  }

  const role = rolesById.get(primaryRoleId);
  return role?.name ?? role?.code ?? 'Unassigned';
}

function getTeamLabel(
  userId: number | undefined,
  groupAssignmentsByUser: Map<number, UserGroupResponse[]>,
  groupsById: Map<number, GroupResponse>,
) {
  if (!userId) {
    return 'Unassigned';
  }

  const assignments = groupAssignmentsByUser.get(userId) ?? [];
  const primaryGroupId = assignments.find((item) => typeof item.groupId === 'number')?.groupId;

  if (!primaryGroupId) {
    return 'Unassigned';
  }

  const group = groupsById.get(primaryGroupId);
  return group?.name ?? group?.code ?? 'Unassigned';
}

export function UsersPage() {
  const notify = useNotify();

  const usersQuery = useQuery({
    queryKey: usersQueryKey,
    queryFn: async () => {
      const [users, roles, groups] = await Promise.all([
        identityApi.findAll(),
        identityApi.findAll1(),
        identityApi.findAll3(),
      ]);

      const roleAssignmentsByUser = new Map<number, UserRoleResponse[]>();
      const groupAssignmentsByUser = new Map<number, UserGroupResponse[]>();

      await Promise.all(
        users
          .filter((user): user is UserResponse & { id: number } => typeof user.id === 'number')
          .map(async (user) => {
            const [roleAssignments, groupAssignments] = await Promise.all([
              identityApi.findByUserId(user.id),
              identityApi.findByUserId1(user.id),
            ]);

            roleAssignmentsByUser.set(user.id, roleAssignments);
            groupAssignmentsByUser.set(user.id, groupAssignments);
          }),
      );

      const rolesById = new Map(
        roles.filter((role): role is RoleResponse & { id: number } => typeof role.id === 'number').map((role) => [role.id, role]),
      );
      const groupsById = new Map(
        groups.filter((group): group is GroupResponse & { id: number } => typeof group.id === 'number').map((group) => [group.id, group]),
      );

      return users.map((user, index) => ({
        id: user.id ?? index + 1,
        name: formatName(user),
        email: user.email ?? '-',
        role: getRoleLabel(user.id, roleAssignmentsByUser, rolesById),
        team: getTeamLabel(user.id, groupAssignmentsByUser, groupsById),
        status: mapStatus(user),
        lastLogin: formatLastLogin(user),
      })) satisfies User[];
    },
    staleTime: DEFAULT_STALE_TIME,
    gcTime: DEFAULT_CACHE_TIME,
  });

  useEffect(() => {
    if (!usersQuery.error) {
      return;
    }

    notify.error(usersQuery.error instanceof Error ? usersQuery.error.message : 'Failed to load users.');
  }, [notify, usersQuery.error]);

  const users = usersQuery.data ?? [];

  return (
    <div className="flex h-full min-h-0 flex-col overflow-hidden">
      {usersQuery.isLoading ? (
        <div className="flex min-h-full items-center justify-center text-sm theme-muted-text">
          Loading users...
        </div>
      ) : (
        <UsersTable users={users} />
      )}
    </div>
  );
}
