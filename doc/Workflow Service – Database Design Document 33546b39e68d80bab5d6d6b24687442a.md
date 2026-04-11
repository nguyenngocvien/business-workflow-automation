# Workflow Service – Database Design Document

## 1. Overview

Workflow Service database được thiết kế để quản lý:

- Workflow definition
- Workflow instance
- Steps và tasks
- Task assignment / reassign / claim
- Task form data
- Audit history
- Attachment
- SLA / Timer

---

# 2. Database Schema Overview

## Nhóm bảng chính

| Module | Tables |
| --- | --- |
| Workflow Definition | wf_definition, wf_step_definition |
| Workflow Runtime | wf_instance, wf_step_instance |
| Task Management | wf_task, wf_task_assignment_history |
| Task Form Data | wf_task_data, wf_task_data_history |
| Audit | wf_history |
| Comment/Attachment | wf_attachment |
| SLA | wf_timer |

---

# 3. Workflow Definition Tables

## 3.1 wf_definition

Lưu thông tin workflow definition

| Column | Type | Description |
| --- | --- | --- |
| id | PK | ID |
| application_name | varchar | Tên application |
| workflow_name | varchar | Tên workflow |
| workflow_key | varchar | Key để start workflow |
| version | int | Version |
| description | varchar | Mô tả |
| active | boolean | Active flag |
| created_at | timestamp | Ngày tạo |
| created_by | varchar | Người tạo |
| updated_at | timestamp | Ngày update |

---

## 3.2 wf_step_definition

Danh sách các step trong workflow

| Column | Type | Description |
| --- | --- | --- |
| id | PK | ID |
| workflow_definition_id | FK | Workflow |
| step_name | varchar | Tên step |
| step_code | varchar | Code step |
| step_type | varchar | START, USER_TASK, SERVICE_TASK, END |
| step_order | int | Thứ tự |
| next_step_code | varchar | Step tiếp theo |
| condition_expression | varchar | Điều kiện |
| sla_minutes | int | SLA |
| created_at | timestamp | Ngày tạo |

---

# 4. Workflow Runtime Tables

## 4.1 wf_instance

Mỗi lần chạy workflow tạo một instance

| Column | Type | Description |
| --- | --- | --- |
| id | PK | ID |
| workflow_definition_id | FK | Workflow |
| business_key | varchar | Business key |
| status | varchar | RUNNING, COMPLETED, FAILED |
| current_step_code | varchar | Step hiện tại |
| started_by | varchar | Người start |
| started_at | timestamp | Start time |
| ended_at | timestamp | End time |

---

## 4.2 wf_step_instance

Runtime của step

| Column | Type | Description |
| --- | --- | --- |
| id | PK | ID |
| wf_instance_id | FK | Workflow instance |
| step_definition_id | FK | Step definition |
| step_code | varchar | Step code |
| status | varchar | PENDING, RUNNING, COMPLETED |
| started_at | timestamp | Start |
| ended_at | timestamp | End |
| processed_by | varchar | Người xử lý |

---

# 5. Task Management Tables

## 5.1 wf_task

Task user cần xử lý

| Column | Type | Description |
| --- | --- | --- |
| id | PK | ID |
| wf_instance_id | FK | Workflow |
| step_instance_id | FK | Step instance |
| task_name | varchar | Task name |
| task_code | varchar | Task code |
| assignee | varchar | Người xử lý |
| owner | varchar | Owner |
| status | varchar | CREATED, CLAIMED, COMPLETED |
| priority | int | Priority |
| due_date | timestamp | Deadline |
| created_at | timestamp | Created |
| claimed_at | timestamp | Claimed |
| completed_at | timestamp | Completed |

---

## 5.2 wf_task_assignment_history

Lịch sử assign / reassign / claim

| Column | Type |
| --- | --- |
| id | PK |
| task_id | FK |
| action | CLAIM, REASSIGN |
| from_user | varchar |
| to_user | varchar |
| action_by | varchar |
| action_at | timestamp |
| comment | varchar |

---

---

# 6. Task Form Data

## 6.1 wf_task_data

| Column | Type |
| --- | --- |
| id | PK |
| task_id | FK |
| data_json | json |
| created_at | timestamp |
| updated_at | timestamp |

## 6.2 wf_task_data_history

| Column | Type |
| --- | --- |
| id | PK |
| task_id | FK |
| data_json | json |
| changed_by | varchar |
| changed_at | timestamp |
| version | int |

---

# 7. Workflow History / Audit Log

## wf_history

| Column | Type |
| --- | --- |
| id | PK |
| wf_instance_id | FK |
| step_instance_id | FK |
| task_id | FK |
| action | varchar |
| action_by | varchar |
| action_at | timestamp |
| note | varchar |

---

# 8. Attachment

## wf_attachment

| Column | Type |
| --- | --- |
| id | PK |
| task_id | FK |
| file_name | varchar |
| file_path | varchar |
| uploaded_by | varchar |
| uploaded_at | timestamp |

---

# 9. SLA / Timer

## wf_timer

| Column | Type |
| --- | --- |
| id | PK |
| task_id | FK |
| due_date | timestamp |
| reminder_date | timestamp |
| escalation_user | varchar |
| status | varchar |

---

# 11. ERD Relationship (Logical)

```
wf_definition
    |
    |--- wf_step_definition
    |
wf_instance
    |
    |--- wf_step_instance
            |
            |--- wf_task
                    |
                    |--- wf_task_assignment_history
                    |--- wf_task_data
                    |--- wf_task_data_history
                    |--- wf_attachment
                    |--- wf_timer
    |
    |--- wf_history
```

---

# 12. Recommended Indexes

```
CREATE INDEX idx_wf_instance_statusON wf_instance(status);
CREATE INDEX idx_wf_instance_business_keyON wf_instance(business_key);
CREATE INDEX idx_wf_task_assigneeON wf_task(assignee);
CREATE INDEX idx_wf_task_statusON wf_task(status);
CREATE INDEX idx_wf_step_instance_wfON wf_step_instance(wf_instance_id);
```

---

# 13. Estimated Table Count

| Module | Tables |
| --- | --- |
| Definition | 2 |
| Runtime | 2 |
| Task | 2 |
| Data | 2 |
| History | 1 |
| Attachment | 1 |
| SLA | 1 |
| **Total** | **13 tables** |