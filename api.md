POST /api/v1/files
GET /api/v1/files/{fileId}/download
POST /api/v1/files/presigned-upload
POST /api/v1/files/complete-upload
GET /api/v1/files/{fileId}/presigned-download
GET /api/v1/files/{fileId}
GET /api/v1/files
PUT /api/v1/files/{fileId}
DELETE /api/v1/files/{fileId}

POST /api/v1/file-categories
GET /api/v1/file-categories
PUT /api/v1/file-categories/{id}
POST /api/v1/file-categories/{id}/content-types

POST /api/v1/file-attributes
GET /api/v1/file-attributes
POST /api/v1/file-attributes/{id}/options

POST /api/v1/files/{fileId}/attributes
PUT /api/v1/files/{fileId}/attributes

POST /api/v1/files/{fileId}/versions
GET /api/v1/files/{fileId}/versions
GET /api/v1/files/{fileId}/versions
GET /api/v1/files/{fileId}/versions/{version}/download

GET /api/v1/files/{fileId}/access-logs

POST /api/v1/files/search

add validation layer (Spring Validation)
add global exception handler
viết OpenAPI (Swagger)
thêm caching category