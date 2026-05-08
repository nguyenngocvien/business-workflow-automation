package com.connector.application.usecase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.connector.application.command.CreateScheduleJobCommand;
import com.connector.application.command.UpdateScheduleJobCommand;
import com.connector.application.result.ScheduleJobResult;

public interface ScheduleJobDefinitionUseCase {

    Page<ScheduleJobResult> findAll(Pageable pageable);

    ScheduleJobResult findById(Long id);

    ScheduleJobResult create(CreateScheduleJobCommand command);

    ScheduleJobResult update(UpdateScheduleJobCommand command);

    void delete(Long id);
}
