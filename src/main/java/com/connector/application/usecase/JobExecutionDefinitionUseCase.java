package com.connector.application.usecase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.connector.application.command.CreateJobExecutionCommand;
import com.connector.application.command.UpdateJobExecutionCommand;
import com.connector.application.result.JobExecutionResult;

public interface JobExecutionDefinitionUseCase {

    Page<JobExecutionResult> findAll(Pageable pageable);

    JobExecutionResult findById(Long id);

    JobExecutionResult create(CreateJobExecutionCommand command);

    JobExecutionResult update(UpdateJobExecutionCommand command);

    void delete(Long id);
}
