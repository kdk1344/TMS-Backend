
package com.tms.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.tms.backend.controller.UserController;
import com.tms.backend.mapper.AdminMapper;
import com.tms.backend.mapper.DefectMapper;
import com.tms.backend.mapper.DevMapper;
import com.tms.backend.mapper.FileAttachmentMapper;
import com.tms.backend.mapper.UserMapper;
import com.tms.backend.vo.CommonCode;
import com.tms.backend.vo.Criteria;
import com.tms.backend.vo.Defect;
import com.tms.backend.vo.FileAttachment;
import com.tms.backend.vo.Notice;
import com.tms.backend.vo.User;
import com.tms.backend.vo.categoryCode;
import com.tms.backend.vo.devProgress;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class DevService {
	
	@Autowired
    private DevMapper devMapper;

	public List<devProgress> searchDevProgress(String majorCategory, String subCategory, String programType, 
	            String programName, String programId, String programstatus, String developer, 
	            String devStatus, String actualStartDate, String actualEndDate, 
	            String pl, String thirdPartyTestMgr, String ItMgr, 
	            String BusiMgr, int page, int size) {
		int offset = (page - 1) * size;
		return devMapper.searchDevProgress(majorCategory, subCategory, programType, programName, programId,
		programstatus, developer, devStatus, actualStartDate, actualEndDate, pl, 
		thirdPartyTestMgr, ItMgr, BusiMgr, offset, size);
	}
	
	public int getTotalDevProgressCount(String majorCategory, String subCategory, String programType, 
	     String programName, String programId, String programstatus, String developer, 
	     String devStatus, String actualStartDate, String actualEndDate, 
	     String pl, String thirdPartyTestMgr, String ItMgr, 
	     String BusiMgr) {
		return devMapper.getTotalDevProgressCount(majorCategory, subCategory, programType, programName, programId,
		programstatus, developer, devStatus, actualStartDate, actualEndDate, pl, 
		thirdPartyTestMgr, ItMgr, BusiMgr);
	}
	
	public void deleteDevProgress(String seq) {
		devMapper.deleteDevProgress(seq);
    }
	
	// 카테고리 코드 일괄 저장
    public void saveAllDevProgress(List<devProgress> devProgress) {
        for (devProgress dev : devProgress) {
            devMapper.insertdevProgress(dev);
        }
    }
	
	
	

    
    
}