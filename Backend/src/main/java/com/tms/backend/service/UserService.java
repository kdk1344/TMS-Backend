package com.tms.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tms.backend.controller.UserController;
import com.tms.backend.mapper.UserMapper;
import com.tms.backend.vo.User;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User authenticateUser(String userID, String Password) {
        User user = userMapper.getUserByUserID(userID);
        if(user == null) {
        	return null;
        }
        if(user.getPassword().equals(Password)) {
        	return user;
        } else {
        	return null;
        }
    }
}