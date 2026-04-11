package com.workflow.domain.enums;

import lombok.Getter;

/**
 * Value Object - ProcessType
 * Defines all supported workflow process types (10 processes, extensible)
 */
@Getter
public enum ProcessType {

        // P01 - Leave Request
        LEAVE_REQUEST("leave-request", "P01", "Yêu cầu nghỉ phép", "LEAVE",
                        "hr-management", new String[] { "HR_MANAGER", "DIRECT_MANAGER" }),

        // P02 - Purchase Request
        PURCHASE_REQUEST("purchase-request", "P02", "Yêu cầu mua sắm", "PUR",
                        "procurement", new String[] { "PROCUREMENT_MANAGER", "FINANCE_MANAGER" }),

        // P03 - Expense Claim
        EXPENSE_CLAIM("expense-claim", "P03", "Thanh toán chi phí", "EXP",
                        "finance", new String[] { "FINANCE_MANAGER", "DIRECT_MANAGER" }),

        // P04 - Recruitment
        RECRUITMENT("recruitment", "P04", "Tuyển dụng nhân sự", "REC",
                        "hr-management", new String[] { "HR_MANAGER", "DEPARTMENT_HEAD" }),

        // P05 - Employee Onboarding
        ONBOARDING("onboarding", "P05", "Onboarding nhân viên", "ONB",
                        "hr-management", new String[] { "HR_MANAGER", "IT_MANAGER" }),

        // P06 - Document Approval
        DOCUMENT_APPROVAL("document-approval", "P06", "Phê duyệt tài liệu", "DOC",
                        "general", new String[] { "MANAGER", "DIRECTOR" }),

        // P07 - Contract Management
        CONTRACT_MANAGEMENT("contract-management", "P07", "Quản lý hợp đồng", "CON",
                        "legal", new String[] { "LEGAL_MANAGER", "DIRECTOR" }),

        // P08 - Training Request
        TRAINING_REQUEST("training-request", "P08", "Yêu cầu đào tạo", "TRN",
                        "hr-management", new String[] { "HR_MANAGER", "TRAINING_MANAGER" }),

        // P09 - Incident Report
        INCIDENT_REPORT("incident-report", "P09", "Báo cáo sự cố", "INC",
                        "operations", new String[] { "OPERATIONS_MANAGER", "SAFETY_OFFICER" }),

        // P10 - Asset Request
        ASSET_REQUEST("asset-request", "P10", "Yêu cầu tài sản", "AST",
                        "asset-management", new String[] { "IT_MANAGER", "ASSET_MANAGER" });

        private final String processDefinitionKey;
        private final String prefix;
        private final String displayName;
        private final String code;
        private final String category;
        private final String[] approverGroups;

        ProcessType(String processDefinitionKey, String prefix, String displayName,
                        String code, String category, String[] approverGroups) {
                this.processDefinitionKey = processDefinitionKey;
                this.prefix = prefix;
                this.displayName = displayName;
                this.code = code;
                this.category = category;
                this.approverGroups = approverGroups;
        }

        public static ProcessType fromKey(String key) {
                for (ProcessType type : values()) {
                        if (type.processDefinitionKey.equals(key)) {
                                return type;
                        }
                }
                throw new IllegalArgumentException("Unknown process type: " + key);
        }
}