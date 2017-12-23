package com.ote.test.mapper;

import com.ote.test.persistence.UserEntity;
import com.ote.test.rest.UserPayload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserMapperService {

    public List<UserPayload> convert(List<UserEntity> entity) {

        return entity.stream().
                map(this::convert).
                collect(Collectors.toList());
    }

    public UserPayload convert(UserEntity entity) {

        if (entity == null) {
            return null;
        }

        UserPayload payload = new UserPayload();
        payload.setId(entity.getId());
        payload.setLogin(entity.getLogin());
        return payload;
    }

    public UserEntity convert(UserPayload payload) {

        if (payload == null) {
            return null;
        }
        UserEntity entity = new UserEntity();
        entity.setId(payload.getId());
        entity.setLogin(payload.getLogin());
        return entity;
    }


    public class NotFoundException extends RuntimeException {

    }
}