package com.ote.test;

import com.ote.test.mapper.UserMapperService;
import com.ote.test.persistence.UserEntity;
import com.ote.test.persistence.UserJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.UUID;
import java.util.stream.LongStream;

@SpringBootApplication
@Slf4j
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    @Autowired
    private UserMapperService userMapperService;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @PostConstruct
    public void init() {
        long max = Math.max(userJpaRepository.count(), 15000);

        LongStream.range(userJpaRepository.count(), max).
                parallel().
                forEach(i -> {
                    UUID login = UUID.randomUUID();
                    UserEntity userEntity = new UserEntity();
                    userEntity.setLogin(login.toString());
                    userJpaRepository.save(userEntity);
                });
        log.info("#### Number of elements created: " + userJpaRepository.count());
    }
}
