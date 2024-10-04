package com.tms.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tms.backend.controller.UserController;
import com.tms.backend.mapper.UserMapper;
import com.tms.backend.vo.Criteria;
import com.tms.backend.vo.User;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class UserService {
	
	@Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;
    
    // 로그인 확인을 위한 아이디, 비밀번호 매칭
    public User authenticateUser(String userID, String Password) {
        User user = userMapper.getUserByUserID(userID);
        if(user == null) {
        	return null;
        }
        if(passwordEncoder.matches(Password, user.getPassword())) { // 비밀번호 암호화 시킨 것과 매칭
        	return user;
        } else {
        	return null;
        }
    }
    
    // 모든 사용자 데이터 추출
    public List<User> getAllUser() {
        return userMapper.UfindAll();
    }
    
    // 모든 사용자 데이터 추출
    public List<User> getAllUsers() {
        return userMapper.findAll();
    }
    
    // 조건에 맞는 사용자 정보 가져오기
    public List<User> getFilteredUsers(Criteria criteria) {
        return userMapper.findUsersByCriteria(criteria);
    }
}