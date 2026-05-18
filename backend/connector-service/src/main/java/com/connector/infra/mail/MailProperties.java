package com.connector.infra.mail;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailProperties {

    private String host;
    private int port;
    private String username;
    private String password;

    private boolean auth = true;
    private boolean starttls = true;
}