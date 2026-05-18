package com.connector.application.port.out;

import com.connector.application.port.out.model.HttpRequest;
import com.connector.application.port.out.model.HttpResponse;

public interface HttpClient {

    HttpResponse execute(HttpRequest request);

}