
package com.tms.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.tms.backend.controller.UserController;
import com.tms.backend.mapper.AdminMapper;
import com.tms.backend.mapper.UserMapper;
import com.tms.backend.vo.User;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class AdminService {

    @Autowired
    private AdminMapper adminmapper;

    public void join(User user) {
		adminmapper.insert(user);
	}
}