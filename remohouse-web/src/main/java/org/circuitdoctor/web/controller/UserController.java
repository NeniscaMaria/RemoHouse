package org.circuitdoctor.web.controller;

import org.circuitdoctor.core.model.User;
import org.circuitdoctor.core.repository.Repository;
import org.circuitdoctor.web.converter.UserConverter;
import org.circuitdoctor.web.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicBoolean;

@RestController
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private Repository<User,Long> userRepository;
    @Autowired
    private UserConverter userConverter;

    @RequestMapping(value = "user/login", method = RequestMethod.PUT)
    boolean login(@RequestBody UserDto userDto){
        log.trace("login - method entered user={}",userDto);
        User user = userConverter.convertDtoToModel(userDto);
        AtomicBoolean result = new AtomicBoolean(false);

        log.trace("login - method finished result={}",result.get());
        return result.get();
    }
}
