package com.tms.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.tms.backend.vo.User;

import lombok.extern.log4j.Log4j;

@Mapper
public interface AdminMapper {

    public void insert(User user);
    public void insertUser(User user);
    public int updateUser(User user);
    public int deleteUser(String id);
}
