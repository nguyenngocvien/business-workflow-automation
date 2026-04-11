package com.workflow.infrastructure.camunda;

import io.camunda.client.annotation.JobWorker;
import io.camunda.client.annotation.Variable;
import io.camunda.client.api.response.ActivatedJob;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CamundaServiceTaskService {

    /**
     * Xử lý Service Task có type là "process-payment" trong BPMN
     */
    @JobWorker(type = "initialize-process")
    public Map<String, Object> initializeProcess(final ActivatedJob job, @Variable String orderId) {
        System.out.println("--- Service Task Started ---");
        System.out.println("Processing payment for Order: " + orderId);

        // Giả lập logic nghiệp vụ
        boolean isSuccess = true; 

        // Trả về kết quả để cập nhật vào Process Variables
        Map<String, Object> output = new HashMap<>();
        output.put("paymentTimestamp", System.currentTimeMillis());
        output.put("status", isSuccess ? "PAID" : "FAILED");
        
        return output; 
    }

    @JobWorker(type = "complete-process")
    public Map<String, Object> completeProcess(@Variable Map<String, Object> orderId) {
        System.out.println("Completing process for Order: " + orderId);
        return Map.of("processCompleted", true);
    }
}