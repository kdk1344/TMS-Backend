package com.tms.backend.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.tms.backend.vo.User;

import lombok.extern.log4j.Log4j;

@Mapper
public interface UserMapper {

    public User getUserByUserID(String userID);
    public List<User> findAll();
}