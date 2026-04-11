# E-Workflow

Workflow Service built with Spring Boot for workflow definition, workflow runtime, step/task processing, task data storage, and audit history.

## Tech Stack

- Java 17
- Spring Boot 4
- Spring Data JPA
- Flyway
- PostgreSQL
- Lombok
- Spring Validation
- Spring MVC
- Springdoc OpenAPI / Swagger UI

## Architecture

The project follows a simple clean architecture style with clear separation of responsibilities.

- `com.workflow.domain.entity`
  JPA entities mapped directly to the PostgreSQL schema.

- `com.workflow.domain.repository`
  Spring Data `JpaRepository` interfaces for persistence access.

- `com.workflow.application.usecase`
  Use case interfaces, command models, and result models for business operations.

- `com.workflow.application.service`
  Application service implementations where workflow orchestration happens.

- `com.workflow.interfaces.rest`
  REST controllers, request DTOs, exception handling, and API configuration.

Dependency flow:

`REST Controller -> Use Case -> Application Service -> Repository -> Database`

## Current Modules

### 1. Workflow Definition

This module manages workflow templates:

- deploy a new workflow definition
- auto-increment version by `workflowKey`
- manage step definitions

Main tables:

- `wf_definition`
- `wf_step_definition`

Use case:

- `WorkflowDefinitionUseCase`

Implementation:

- `WorkflowDefinitionApplicationService`

### 2. Workflow Runtime

This module manages workflow runtime execution:

- start workflow from the latest active definition
- create `wf_instance`
- create the first `wf_step_instance`
- create `wf_task` when the step type is `USER_TASK`
- claim task
- reassign task
- complete task
- move to next step by `next_step_code`
- complete workflow when there is no next step
- search workflow runtime data

Main tables:

- `wf_instance`
- `wf_step_instance`
- `wf_task`
- `wf_task_assignment_history`
- `wf_task_data`
- `wf_task_data_history`
- `wf_history`
- `wf_attachment`
- `wf_timer`

Use case:

- `WorkflowRuntimeUseCase`

Implementation:

- `WorkflowRuntimeApplicationService`

## Database

Schema is managed by Flyway:

- [V1__init_workflow_schema.sql](/d:/projects/e-workflow/src/main/resources/db/migration/V1__init_workflow_schema.sql)

Key mappings:

- `BIGSERIAL -> Long`
- `TIMESTAMP -> LocalDateTime`
- `JSONB -> Map<String, Object>`
- all relationships use `FetchType.LAZY`

## Package Structure

```text
src/main/java/com/workflow
|-- application
|   |-- exception
|   |-- service
|   `-- usecase
|       |-- command
|       `-- result
|-- domain
|   |-- entity
|   `-- repository
`-- interfaces
    `-- rest
        `-- request
```

## Business Flow

### Deploy Workflow Definition

1. Client sends a new workflow definition.
2. Service finds the highest version by `workflowKey`.
3. Service creates a new definition with `version + 1`.
4. Service creates step definitions and attaches them to the workflow definition.
5. The aggregate is saved through JPA.

### Start Workflow

1. Client calls the start API with a `workflowKey`.
2. Service loads the latest active definition.
3. Service finds the first step by `stepOrder`.
4. Service creates `wf_instance`.
5. Service creates the first `wf_step_instance`.
6. If the step type is `USER_TASK`, service creates `wf_task`.
7. Audit history is written.

### Claim Task

1. Find task by `taskId`.
2. Set `assignee`.
3. Change task status to `CLAIMED`.
4. Write assignment history.
5. Write workflow history.
6. Remove all `CANDIDATE` identity links of the task after the claim succeeds.

### Claim Task By Candidate

1. Find task by `taskId`.
2. Validate that the user exists in `wf_user`.
3. Validate that the user is a direct candidate or belongs to a candidate group of the task.
4. Claim the task for that same username.
5. Write assignment history and workflow history.
6. Remove all `CANDIDATE` identity links of the task after the claim succeeds.

### Reassign Task

1. Find task by `taskId`.
2. Validate task is not completed.
3. Change assignee to a different user.
4. Write assignment history with `REASSIGN`.
5. Write workflow history with `TASK_REASSIGNED`.

### Complete Task

1. Find task by `taskId`.
2. Save task data and task data history if request contains data.
3. Complete the current task.
4. Complete the current step instance.
5. Find next step by `next_step_code`.
6. If next step exists, create the next step instance and next task if needed.
7. If no next step exists, complete the workflow instance.

## REST API

Base path:

`/api/v1`

### Workflow Definition APIs

#### `POST /api/v1/workflow-definitions`

Create a new workflow definition.

#### `GET /api/v1/workflow-definitions/{definitionId}`

Get workflow definition details by id.

#### `GET /api/v1/workflow-definitions?workflowKey=PURCHASE_APPROVAL`

Get workflow definitions by workflow key.

### Workflow Runtime APIs

#### `POST /api/v1/workflows/start`

Start a new workflow instance.

Example request:

```json
{
  "workflowKey": "PURCHASE_APPROVAL",
  "businessKey": "PO-2026-0001",
  "startedBy": "alice"
}
```

#### `GET /api/v1/workflows/{workflowInstanceId}`

Get workflow instance details including steps and tasks.

#### `GET /api/v1/workflows/search`

Search workflow runtime data by:

- `application_name`
- `workflow_key`
- `current_step_code`
- `status`
- `business_key`
- `assignee`

Response fields:

- `wfInstanceId`
- `businessKey`
- `workflowStatus`
- `currentStepCode`
- `assignee`
- `taskStatus`
- `reminderDate`
- `taskCreatedAt`
- `completedAt`

#### `PATCH /api/v1/workflows/tasks/{taskId}/claim`

Claim a task for a user.

Behavior note:

- after a successful claim, the service removes all `CANDIDATE` links of that task from `wf_task_identity_link`

Example request:

```json
{
  "assignee": "bob",
  "actionBy": "team-lead",
  "comment": "Assign task to Bob"
}
```

#### `PATCH /api/v1/workflows/tasks/{taskId}/claim-by-candidate`

Allow a task to be claimed only by a valid candidate user or a member of a valid candidate group.

Behavior note:

- the username claims the task for themselves
- direct candidate user and candidate group membership are both supported
- after a successful claim, the service removes all `CANDIDATE` links of that task from `wf_task_identity_link`

Example request:

```json
{
  "username": "alice",
  "comment": "candidate claim"
}
```

#### `PATCH /api/v1/workflows/tasks/{taskId}/reassign`

Reassign a task to another user.

Example request:

```json
{
  "assignee": "charlie",
  "actionBy": "manager",
  "comment": "Move task to Charlie"
}
```

#### `PATCH /api/v1/workflows/tasks/{taskId}/complete`

Complete a task and optionally store business data.

Example request:

```json
{
  "actionBy": "bob",
  "comment": "Approved",
  "data": {
    "approved": true,
    "approvalLevel": "MANAGER"
  }
}
```

#### `PATCH /api/v1/workflows/tasks/{taskId}/data`

Save task data without completing the task.

Example request:

```json
{
  "changedBy": "bob",
  "data": {
    "remark": "Need supporting document"
  }
}
```

## Swagger UI

Swagger/OpenAPI is enabled in the application.

Useful URLs after starting the app:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

### Login for Swagger

Current setup uses HTTP Basic authentication for business APIs.

Sample credentials from `application.properties`:

- username: `admin`
- password: `admin123`

Swagger endpoints are public, but when you execute secured APIs from Swagger UI you should use the `Authorize` button and enter the Basic credentials above.

## Error Handling

The project uses `@RestControllerAdvice` for API error formatting:

- `ResourceNotFoundException -> 404`
- `BusinessException -> 400`
- `validation error -> 400`

Implementation:

- [WorkflowExceptionHandler.java](/d:/projects/e-workflow/src/main/java/com/workflow/interfaces/rest/WorkflowExceptionHandler.java)

## Testing

There are service tests for the main business flows:

- [WorkflowDefinitionApplicationServiceTest.java](/d:/projects/e-workflow/src/test/java/com/workflow/application/service/WorkflowDefinitionApplicationServiceTest.java)
- [WorkflowRuntimeApplicationServiceTest.java](/d:/projects/e-workflow/src/test/java/com/workflow/application/service/WorkflowRuntimeApplicationServiceTest.java)

Covered scenarios:

- deploy definition
- auto increment version
- sort steps by `stepOrder`
- start workflow
- claim task
- claim task by candidate
- reassign task
- reject complete task when task is already completed

## Run Project

### 1. Configure database

Set PostgreSQL values in `src/main/resources/application.properties` or override them with environment variables:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/e_workflow
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.flyway.enabled=true
```

### 2. Run application locally

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

### 3. Run with Docker Compose

```bash
docker compose up --build
```

## Current Limitations

The project has a solid workflow foundation, but these areas are still good next steps:

- pagination for search APIs
- optimistic locking or concurrency control for claim/reassign/complete
- `@WebMvcTest` for controllers
- `@DataJpaTest` with PostgreSQL/Testcontainers for real JSONB verification
- richer OpenAPI schemas for response models
- CI pipeline

## Suggested Next Steps

- add Docker Compose quick-start section with sample curl commands
- add OpenAPI annotations for response models
- add integration tests with PostgreSQL
- add role-based authorization
- add attachment/timer application services
