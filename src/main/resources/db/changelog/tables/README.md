# Liquibase Table Migrations

This directory contains the schema migrations for the identity service, split into small, ordered files.

## Ordering Rules

- Files are numbered to reflect the intended execution order.
- Base tables come first.
- Tables with foreign keys to earlier tables come after their dependencies.
- Join tables and dependent tables come after the entities they reference.

## Migration Index

1. `001-users.sql`
   - Creates the `users` table and the `idx_users_username` index.
   - This is the core identity entity and is referenced by many later tables.

2. `002-identity-providers.sql`
   - Creates `identity_providers`.
   - Supports external identity source mappings and sync jobs.

3. `003-groups.sql`
   - Creates `groups`.
   - Includes the self-referencing parent group relationship.

4. `004-roles.sql`
   - Creates `roles`.
   - Used by role assignments and role-permission mappings.

5. `005-permissions.sql`
   - Creates `permissions`.
   - Used by `role_permissions`.

6. `006-departments.sql`
   - Creates `departments` and the `idx_department_manager` index.
   - References `users` for the department manager.

7. `007-user-external-mappings.sql`
   - Creates `user_external_mappings`.
   - Links internal users to external identity provider accounts.

8. `008-user-groups.sql`
   - Creates `user_groups` and the `idx_user_groups_user` index.
   - Maps users to groups.

9. `009-role-permissions.sql`
   - Creates `role_permissions`.
   - Maps roles to permissions.

10. `010-user-roles.sql`
    - Creates `user_roles` and the `idx_user_roles_user` index.
    - Assigns roles directly to users.

11. `011-group-roles.sql`
    - Creates `group_roles` and the `idx_group_roles_group` index.
    - Assigns roles to groups.

12. `012-user-departments.sql`
    - Creates `user_departments`.
    - Maps users to departments.

13. `013-delegations.sql`
    - Creates `delegations` and the delegation indexes.
    - Stores temporary user-to-user delegation rules.

14. `014-sync-jobs.sql`
    - Creates `sync_jobs`.
    - Tracks provisioning or synchronization runs for identity providers.

15. `015-audit-logs.sql`
    - Creates `audit_logs`.
    - Stores audit trail records for entity activity.

## Notes

- Keep new migrations numbered after the last existing file unless you intentionally insert one between two existing migrations.
- If a new table depends on an existing one, place it after that table's migration and update the master changelog if needed.
- Prefer one table or tightly related set of objects per file to keep review and rollback simple.
