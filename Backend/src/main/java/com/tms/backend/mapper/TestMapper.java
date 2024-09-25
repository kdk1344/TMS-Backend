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
import com.tms.backend.vo.testProgress;

import lombok.extern.log4j.Log4j;

@Mapper
public interface TestMapper {

	// 검색 필터에 따른 테스트 시나리오 데이터 조회
    public List<testProgress> searchTestProgress(
            @Param("testStage") String testStage,
            @Param("majorCategory") String majorCategory,
            @Param("subCategory") String subCategory,
            @Param("programType") String programType,
            @Param("testId") String testId,
            @Param("screenId") String screenId,
            @Param("screenName") String screenName,
            @Param("programName") String programName,
            @Param("programId") String programId,
            @Param("developer") String developer,
            @Param("testStatus") String testStatus,
            @Param("pl") String pl,
            @Param("ItMgr") String ItMgr,
            @Param("BusiMgr") String BusiMgr,
            @Param("execCompanyMgr") String execCompanyMgr,
            @Param("thirdPartyTestMgr") String thirdPartyTestMgr,
            @Param("offset") int offset,
            @Param("size") int size
    );
    // 테스트 현황 수정
    public void updatetestProgress(testProgress testProgress);
    // 테스트 진행 데이터 총 개수
    public int getTotalTestCount(
            @Param("testStage") String testStage,
            @Param("majorCategory") String majorCategory,
            @Param("subCategory") String subCategory,
            @Param("programType") String programType,
            @Param("testId") String testId,
            @Param("screenId") String screenId,
            @Param("screenName") String screenName,
            @Param("programName") String programName,
            @Param("programId") String programId,
            @Param("developer") String developer,
            @Param("testStatus") String testStatus,
            @Param("pl") String pl,
            @Param("ItMgr") String ItMgr,
            @Param("BusiMgr") String BusiMgr,
            @Param("execCompanyMgr") String execCompanyMgr,
            @Param("thirdPartyTestMgr") String thirdPartyTestMgr
    );
    public void deleteTestProgress(int seq);//테스트 삭제
    public void inserttestProgress(testProgress testProgress); //테스트 저장
    public List<devProgress> getscreenList(); //화면 정보 가져오기
    public String getProgramType(String programId); //프로그램 구분 가져오기
    public testProgress getTestById(int seq); //테스트 시나리오 정보 가져오기
}
