package com.verify;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "v1/api")
@Slf4j
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DataSourceProperties dataSourceProperties;
    @GetMapping("/users")
    public List<User> findAllUser() {
        log.info(dataSourceProperties.getUrl());
        return userRepository.findAll();
    }



}
