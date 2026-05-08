package com.workflow.application.port.out;

import java.util.Map;

public interface ProcessService {
    long startProcess(String bpmnProcessId, Map<String, Object> variables);
}
