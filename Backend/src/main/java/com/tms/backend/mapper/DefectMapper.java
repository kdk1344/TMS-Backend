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
            @Param("size") int size); // ���� ��Ȳ ��ȸ
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
		  @Param("programType") String programType); // ���� ��Ȳ �� ����
	public List<Defect> searchDefectOriginal(@Param("testStage") String testStage,
											 @Param("testId") String testId,
											 @Param("programId") String programId,
            								 @Param("programName") String programName); // ���� ������ ���� ���� ���� ��ȸ
	public void deleteDefect(Integer seq); // ���� ��Ȳ ����
	public void insertdefect(Defect def); // ���� ��Ȳ �Է�
	public void updateDefect(Defect def); // ���� ��Ȳ ����
	public Defect getDefectById(Integer seq);  // seq�� ���� ���� ��Ȳ ����
	public List<Defect> searchAllDefects(); // ��� ���� ��Ȳ ��������
	public int totalcountDefect(String programId); // ���α׷� ���� ��� �� ���� ���� ����
	public int countDefect(@Param("programId") String programId, @Param("managerType") String managerType); // ���� ���� ���� ���� ����
	public int countDefectSoultions(@Param("programId") String programId, @Param("managerType") String managerType); //��ġ�Ϸ� �Ǽ� ����

}
