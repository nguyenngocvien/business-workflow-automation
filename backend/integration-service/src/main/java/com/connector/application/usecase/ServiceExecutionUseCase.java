package com.connector.application.usecase;

import com.connector.application.command.ExecuteServiceCommand;
import com.connector.application.result.ExecuteServiceResult;

public interface ServiceExecutionUseCase {

    ExecuteServiceResult execute(ExecuteServiceCommand command);
}
