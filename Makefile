.DEFAULT_GOAL := help

.PHONY: help pull-api-gateway pull-service-discovery pull-identity-service pull-workflow-service pull-connector-service pull-document-service

help:
	@echo "Available targets:"
	@echo "  make pull-api-gateway            Pull api-gateway into backend/workflow-service"
	@echo "  make pull-service-discovery      Pull service-discovery into backend/workflow-service"
	@echo "  make pull-identity-service       Pull identity-service into backend/workflow-service"
	@echo "  make pull-workflow-service       Pull e-workflow into backend/workflow-service"
	@echo "  make pull-connector-service    Pull e-connector into backend/connector-service"
	@echo "  make pull-document-service       Pull e-document into backend/document-service"

pull-discovery-server:
	git subtree pull --prefix backend/discovery-server https://github.com/nguyenngocvien/discovery-server.git main --squash

pull-api-gateway:
	git subtree pull --prefix backend/api-gateway https://github.com/nguyenngocvien/api-gateway.git main --squash

pull-identity-service:
	git subtree pull --prefix backend/identity-service https://github.com/nguyenngocvien/identity-service.git main --squash

pull-workflow-service:
	git subtree pull --prefix backend/workflow-service https://github.com/nguyenngocvien/e-workflow.git main --squash

pull-connector-service:
	git subtree pull --prefix backend/connector-service https://github.com/nguyenngocvien/e-connector.git Java17 --squash

pull-document-service:
	git subtree pull --prefix backend/document-service https://github.com/nguyenngocvien/e-document.git Java17 --squash

pull-notification-service:
	git subtree pull --prefix backend/notification-service https://github.com/nguyenngocvien/notification-service.git main --squash
