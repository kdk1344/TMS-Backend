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

import lombok.extern.log4j.Log4j;

@Mapper
public interface DefectMapper {

	public List<Defect> searchDefects(@Param("testStage") String testStage, 
            @Param("majorCategory") String majorCategory, 
            @Param("subCategory") String subCategory, 
            @Param("defectSeverity") String defectSeverity,
            @Param("seq") Integer seq,
            @Param("defectRegistrar") String defectRegistrar,
            @Param("defectHandler") String defectHandler,
            @Param("pl") String pl,
            @Param("defectStatus") String defectStatus,
            @Param("programId") String programId,
            @Param("testId") String testId,
            @Param("programName") String programName,
            @Param("programType") String programType,
            @Param("offset") int offset, 
            @Param("size") int size); // 결함 현황 조회
	public int countDefects(@Param("testStage") String testStage, 
		  @Param("majorCategory") String majorCategory, 
		  @Param("subCategory") String subCategory, 
		  @Param("defectSeverity") String defectSeverity,
		  @Param("seq") Integer seq,
		  @Param("defectRegistrar") String defectRegistrar,
		  @Param("defectHandler") String defectHandler,
		  @Param("pl") String pl,
		  @Param("defectStatus") String defectStatus,
		  @Param("programId") String programId,
		  @Param("testId") String testId,
		  @Param("programName") String programName,
		  @Param("programType") String programType); // 결함 현황 총 숫자
	public List<Defect> searchDefectOriginal(@Param("testStage") String testStage,
											 @Param("testId") String testId,
											 @Param("programId") String programId,
            								 @Param("programName") String programName); // 결함 수정을 위한 결함 내용 조회
	public void deleteDefect(Integer seq); // 결함 현황 삭제
	public void insertdefect(Defect def); // 결함 현황 입력
	public void updateDefect(Defect def); // 결함 현황 수정
	public Defect getDefectById(Integer seq);  // seq에 따른 결함 현황 추출
	public List<Defect> searchAllDefects(); // 모든 결함 현황 가져오기
	public int totalcountDefect(String programId); // 프로그램 개발 목록 총 결함 갯수 추출
	public int countDefect(@Param("programId") String programId, @Param("managerType") String managerType); // 여러 종류 결함 갯수 숫자
	public int countDefectSoultions(@Param("programId") String programId, @Param("managerType") String managerType); //조치완료 건수 숫자

}
