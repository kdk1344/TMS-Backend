package com.tms.backend.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.tms.backend.vo.Criteria;
import com.tms.backend.vo.User;

import lombok.extern.log4j.Log4j;

@Mapper
public interface UserMapper {
    public User getUserByUserID(String userID); // 사용자 아이디에 맞춘 사용자 데이터 가져오기
    public List<User> findAll(); // 사용자 데이터 추출
    public List<User> UfindAll(); // 사용자 데이터 추출
    public List<User> findUsersByCriteria(Criteria criteria); // Criteria 데이터에 맞춘 사용자 데이터 추출
}