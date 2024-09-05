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
            @Param("offset") int offset, 
            @Param("size") int size);
	public int countDefects(@Param("testStage") String testStage, 
	  @Param("majorCategory") String majorCategory, 
	  @Param("subCategory") String subCategory, 
	  @Param("defectSeverity") String defectSeverity,
	  @Param("seq") Integer seq,
	  @Param("defectRegistrar") String defectRegistrar,
	  @Param("defectHandler") String defectHandler,
	  @Param("pl") String pl,
	  @Param("defectStatus") String defectStatus);
	public void deleteDefect(int seq);
	public void insertdefect(Defect def);
	public void updateDefect(Defect def);
	public Defect getDefectById(int seq); 
	public List<Defect> searchAllDefects();
	public int countDefect(@Param("programId") String programId, @Param("managerType") String managerType);
	public int countDefectSoultions(@Param("programId") String programId, @Param("managerType") String managerType);

}
