package com.ote.test.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//@Component
@Slf4j
public class RestTemplateLogger extends RestTemplate {

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public <T> ResponseEntity<T> postForEntity(URI url, Object request, Class<T> responseType) throws RestClientException {
        logger.info("postForEntity: " + url + ", " + request);
        return restTemplate.postForEntity(url, request, responseType);
    }

    @Override
    public <T> ResponseEntity<T> postForEntity(String url, Object request, Class<T> responseType, Object... uriVariables) throws RestClientException {
        logger.info("postForEntity: " + url + ", " + request + ", " + uriVariables);
        return restTemplate.postForEntity(url, request, responseType, Stream.of(uriVariables).map(p -> p.toString()).collect(Collectors.joining()));
    }

    @Override
    public <T> ResponseEntity<T> postForEntity(String url, Object request, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        logger.info("postForEntity: " + url + ", " + request + ", " + uriVariables);
        return restTemplate.postForEntity(url, request, responseType, uriVariables);
    }

    @Override
    public void put(URI url, Object request) throws RestClientException {
        logger.info("put: " + url + ", " + request);
        restTemplate.put(url, request);
    }

    @Override
    public void put(String url, Object request, Object... uriVariables) throws RestClientException {
        logger.info("put: " + url + ", " + request + ", " + Stream.of(uriVariables).map(p -> p.toString()).collect(Collectors.joining()));
        restTemplate.put(url, request, uriVariables);
    }

    @Override
    public void put(String url, Object request, Map<String, ?> uriVariables) throws RestClientException {
        logger.info("put: " + url + ", " + request + ", " + uriVariables);
        restTemplate.put(url, request, uriVariables);
    }
}
