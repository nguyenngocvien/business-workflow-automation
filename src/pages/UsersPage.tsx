import { useEffect, useState } from 'react';
import { UsersTable } from '../components/users/UsersTable';
import { useNotify } from '../components/ui/NotificationProvider';
import { fetchUsers } from '../services/api';
import type { User } from '../types/dashboard';

export function UsersPage() {
  const [users, setUsers] = useState<User[]>([]);
  const notify = useNotify();

  useEffect(() => {
    let active = true;

    void fetchUsers()
      .then((response) => {
        if (!active) {
          return;
        }
        setUsers(response);
      })
      .catch((requestError) => {
        if (!active) {
          return;
        }
        notify.error(requestError instanceof Error ? requestError.message : 'Failed to load users.');
      });

    return () => {
      active = false;
    };
  }, [notify]);

  return (
    <div className="flex h-full min-h-0 flex-col overflow-hidden">
      <UsersTable users={users} />
    </div>
  );
}
