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

	public List<Defect> searchDefects(@Param("testCategory") String testCategory, 
            @Param("taskCategory") String taskCategory, 
            @Param("taskSubCategory") String taskSubCategory, 
            @Param("defectSeverity") String defectSeverity,
            @Param("defectNumber") String defectNumber,
            @Param("defectRegistrar") String defectRegistrar,
            @Param("defectHandler") String defectHandler,
            @Param("defectStatus") String defectStatus,
            @Param("offset") int offset, 
            @Param("size") int size);
	public int countDefects(@Param("testCategory") String testCategory, 
	  @Param("taskCategory") String taskCategory, 
	  @Param("taskSubCategory") String taskSubCategory, 
	  @Param("defectSeverity") String defectSeverity,
	  @Param("defectNumber") String defectNumber,
	  @Param("defectRegistrar") String defectRegistrar,
	  @Param("defectHandler") String defectHandler,
	  @Param("defectStatus") String defectStatus);
}
