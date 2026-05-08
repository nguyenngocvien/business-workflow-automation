package com.workflow.interfaces.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "ibm.bpm")
public class IbmBpmProperties {

    @NotBlank
    private String baseUrl = "http://localhost:9080/bpm";

    private String username;

    private String password;

    private boolean refreshGroups = false;

    private long requestedLifetimeSeconds = 7200L;

    private String defaultTaskStates = "ready";

    private String defaultOptionalParts = "all";

    private Duration requestTimeout = Duration.ofSeconds(30);

    @Valid
    private QueryDefaults queryDefaults = new QueryDefaults();

    @Getter
    @Setter
    public static class QueryDefaults {
        private String taskModel;
        private String processModel;
        private String processId;
        private String taskProcessId;
    }
}
