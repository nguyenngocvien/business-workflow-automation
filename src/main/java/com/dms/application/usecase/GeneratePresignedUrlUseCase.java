package com.dms.application.usecase;

import java.util.ArrayList;
import java.util.List;

import com.dms.application.command.GeneratePresignedUrlCommand;
import com.dms.application.port.out.StorageService;
import com.dms.application.result.PresignedUrlResult;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GeneratePresignedUrlUseCase {
    private final StorageService storageService;

    public List<PresignedUrlResult> execute(GeneratePresignedUrlCommand cmd)
            throws Exception {

        List<PresignedUrlResult> result = new ArrayList<>();

        for (int i = 1; i <= cmd.getTotalParts(); i++) {

            String url = storageService.getPresignedUploadPartUrl(
                    cmd.getBucket(),
                    cmd.getObjectKey(),
                    cmd.getUploadId(),
                    i,
                    3600
            );

            result.add(new PresignedUrlResult(i, url));
        }

        return result;
    }
}
