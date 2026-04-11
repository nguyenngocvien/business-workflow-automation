package com.workflow.application.usecase;

import java.util.List;

import com.workflow.application.usecase.command.CreateProcessVersionCommand;
import com.workflow.application.usecase.result.ProcessDeployResult;
import com.workflow.application.usecase.result.ProcessVersionResult;
public interface ProcessVersionUseCase {

    ProcessVersionResult createVersion(CreateProcessVersionCommand command);

    ProcessDeployResult deployVersion(Long id);

    ProcessDeployResult rollback(String processKey, Integer targetVersion);

    List<ProcessVersionResult> getVersionsByProcessKey(String processKey);

    ProcessVersionResult getActiveVersion(String processKey);

    List<ProcessVersionResult> getAllVersions();

    ProcessVersionResult updateBpmnXml(Long id, String bpmnXml);

    void deleteVersion(Long id);
}
