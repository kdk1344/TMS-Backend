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
import com.tms.backend.vo.deleteHistory;
import com.tms.backend.vo.devProgress;

import lombok.extern.log4j.Log4j;

@Mapper
public interface DevMapper {
    public List<devProgress> searchDevProgress(
            @Param("majorCategory") String majorCategory,
            @Param("subCategory") String subCategory,
            @Param("programType") String programType,
            @Param("programName") String programName,
            @Param("programId") String programId,
            @Param("programStatus") String programStatus,
            @Param("developer") String developer,
            @Param("devStatus") String devStatus,
            @Param("devStartDate") String devStartDate,
            @Param("devEndDate") String devEndDate,
            @Param("pl") String pl,
            @Param("thirdPartyTestMgr") String thirdPartyTestMgr,
            @Param("ItMgr") String ItMgr,
            @Param("BusiMgr") String BusiMgr,
            @Param("offset") int offset,
            @Param("size") int size
    ); // 프로그램 개발 진행 현황 조회
    public int getTotalDevProgressCount(
            @Param("majorCategory") String majorCategory,
            @Param("subCategory") String subCategory,
            @Param("programType") String programType,
            @Param("programName") String programName,
            @Param("programId") String programId,
            @Param("programStatus") String programStatus,
            @Param("developer") String developer,
            @Param("devStatus") String devStatus,
            @Param("devStartDate") String devStartDate,
            @Param("devEndDate") String devEndDate,
            @Param("pl") String pl,
            @Param("thirdPartyTestMgr") String thirdPartyTestMgr,
            @Param("ItMgr") String ItMgr,
            @Param("BusiMgr") String BusiMgr
    ); // 프로그램 개발 진행 현황 데이터 총 개수
    public void updatedevProgress(devProgress devProgress); // 프로그램 개발 진행 현황 수정
    public void deleteDevProgress(int seq); // 프로그램 개발 진행 현황 삭제
    public void insertdevProgress(devProgress devProgress); // 프로그램 개발 진행 현황 입력
    public devProgress getDevById(int Seq); // seq에 따른 프로그램 개발 진행 현황 가져오기
    public int checkCountProgramId(String programId); // 프로그램 ID 갯수 확인
    public List<devProgress> checkProgramId(@Param("programType") String programType,
											@Param("developer") String developer,
											@Param("programId") String programId,
											@Param("programName") String programName,
											@Param("screenId") String screenId); //프로그램 ID 확보를 위한 프로그램 목록 조회
//    public int checkProgramIdCounts(@Param("programType") String programType,
//	    							@Param("developer") String developer,
//	    							@Param("programId") String programId,
//	    							@Param("programName") String programName);
    public List<devProgress> getprogramDetail(@Param("programId") String programId); // 프로그램 상세 정보 확인
    public void insertDeleteHistory(deleteHistory deletehistory); // deleteHistory DB에 삽입
    public int checkDeleteHistory(int seq); // 삭제 기록 존재 여부 확인
    public void updateDeleteHistory(deleteHistory deletehistory);
}
