package com.ote.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

@Service
@Slf4j
public class RemoteUserService {

    @Autowired
    protected RestTemplate restTemplate;

    @Value("${remote.server.uri}")
    private String serverUri;

    @Autowired
    private Environment environment;

    @Async
    public Future<Optional<UserPayload>> findOne(int id) {
        log.info("Find user #" + id);
        ResponseEntity<UserPayload> res = restTemplate.getForEntity(serverUri + "/api/v1/users/" + id, UserPayload.class);
        return new AsyncResult<>(Optional.ofNullable(res).map(p -> p.getBody()));
    }

    // used by BulkUserRestController4
    @SuppressWarnings("unchecked")
    public List<UserPayload> findMany4(Params params) {
        String key = restTemplate.postForEntity(serverUri + "/api/v1/users/post4", params, String.class).getBody();
        return restTemplate.getForEntity(serverUri + "/api/v1/users/get4/" + key, List.class).getBody();
    }

    @SuppressWarnings("unchecked")
    public void findMany5(Params params, String callbackUri) throws Exception {

        String host = InetAddress.getLocalHost().getHostAddress();
        String port = environment.getProperty("server.port");

        String url = "http://" + host + ":" + port + callbackUri;

        restTemplate.postForEntity(serverUri + "/api/v1/users/post5?callback=" + url, params, Void.class);
    }

    public void findMany6(Params params, String queue) {

        restTemplate.postForEntity(serverUri + "/api/v1/users/post6?queue=" + queue, params, Void.class);
    }

    public void findMany6b(Params params, String queue) {

        restTemplate.postForEntity(serverUri + "/api/v1/users/post6b?queue=" + queue, params, Void.class);
    }
}
