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

	// �˻� ���Ϳ� ���� ���� ���� ������ ��ȸ
    public List<devProgress> searchDevProgress(
            @Param("majorCategory") String majorCategory,
            @Param("subCategory") String subCategory,
            @Param("programType") String programType,
            @Param("programName") String programName,
            @Param("programId") String programId,
            @Param("programstatus") String programstatus,
            @Param("developer") String developer,
            @Param("devStatus") String devStatus,
            @Param("actualStartDate") String actualStartDate,
            @Param("actualEndDate") String actualEndDate,
            @Param("pl") String pl,
            @Param("thirdPartyTestMgr") String thirdPartyTestMgr,
            @Param("ItMgr") String ItMgr,
            @Param("BusiMgr") String BusiMgr,
            @Param("offset") int offset,
            @Param("size") int size
    );

    // ���� ���� ������ �� ����
    public int getTotalDevProgressCount(
            @Param("majorCategory") String majorCategory,
            @Param("subCategory") String subCategory,
            @Param("programType") String programType,
            @Param("programName") String programName,
            @Param("programId") String programId,
            @Param("programstatus") String programstatus,
            @Param("developer") String developer,
            @Param("devStatus") String devStatus,
            @Param("actualStartDate") String actualStartDate,
            @Param("actualEndDate") String actualEndDate,
            @Param("pl") String pl,
            @Param("thirdPartyTestMgr") String thirdPartyTestMgr,
            @Param("ItMgr") String ItMgr,
            @Param("BusiMgr") String BusiMgr
    );
    public void deleteDevProgress(String seq);
    public void insertdevProgress(devProgress devProgress);
}
