package com.tms.backend.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.tms.backend.vo.CommonCode;
import com.tms.backend.vo.Criteria;
import com.tms.backend.vo.Defect;
import com.tms.backend.vo.FileAttachment;
import com.tms.backend.vo.Notice;
import com.tms.backend.vo.User;
import com.tms.backend.vo.categoryCode;
import com.tms.backend.vo.devProgress;

import lombok.extern.log4j.Log4j;

@Mapper
public interface DevMapper {

	// 검색 필터에 따른 개발 진행 데이터 조회
    public List<devProgress> searchDevProgress(
            @Param("majorCategory") String majorCategory,
            @Param("subCategory") String subCategory,
            @Param("programType") String programType,
            @Param("programName") String programName,
            @Param("regStatus") String regStatus,
            @Param("developer") String developer,
            @Param("devStatus") String devStatus,
            @Param("actualStartDate") String actualStartDate,
            @Param("actualEndDate") String actualEndDate,
            @Param("pl") String pl,
            @Param("thirdPartyTestMgr") String thirdPartyTestMgr,
            @Param("custItMgr") String custItMgr,
            @Param("custBusiMgr") String custBusiMgr,
            @Param("offset") int offset,
            @Param("size") int size
    );

    // 개발 진행 데이터 총 개수
    public int getTotalDevProgressCount(
            @Param("majorCategory") String majorCategory,
            @Param("subCategory") String subCategory,
            @Param("programType") String programType,
            @Param("programName") String programName,
            @Param("regStatus") String regStatus,
            @Param("developer") String developer,
            @Param("devStatus") String devStatus,
            @Param("actualStartDate") String actualStartDate,
            @Param("actualEndDate") String actualEndDate,
            @Param("pl") String pl,
            @Param("thirdPartyTestMgr") String thirdPartyTestMgr,
            @Param("custItMgr") String custItMgr,
            @Param("custBusiMgr") String custBusiMgr
    );
    public void deleteDevProgress(String seq);
}
