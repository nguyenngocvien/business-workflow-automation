package com.dms.application.usecase;

import org.springframework.data.domain.Page;

import com.dms.application.command.FileSearchCommand;
import com.dms.application.result.FileSearchOptionsResult;
import com.dms.application.result.FileSearchResult;

public interface FileSearchUseCase {

    Page<FileSearchResult> search(FileSearchCommand cmd);

    FileSearchOptionsResult getSearchOptions();
}