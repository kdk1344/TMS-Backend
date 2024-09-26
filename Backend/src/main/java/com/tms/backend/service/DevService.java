
package com.tms.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	
	@Autowired
	private AdminMapper adminmapper;
	
	@Autowired
	private TestService testService;

	public List<devProgress> searchDevProgress(String majorCategory, String subCategory, String programType, 
	            String programName, String programId, String programStatus, String developer, 
	            String devStatus, String devStartDate, String devEndDate, 
	            String pl, String thirdPartyTestMgr, String ItMgr, 
	            String BusiMgr, int page, int size) {
		int offset = (page - 1) * size;
		return devMapper.searchDevProgress(majorCategory, subCategory, programType, programName, programId,
		programStatus, developer, devStatus, devStartDate, devEndDate, pl, 
		thirdPartyTestMgr, ItMgr, BusiMgr, offset, size);
	}
	
	public int getTotalDevProgressCount(String majorCategory, String subCategory, String programType, 
	     String programName, String programId, String programStatus, String developer, 
	     String devStatus, String devStartDate, String devEndDate, 
	     String pl, String thirdPartyTestMgr, String ItMgr, 
	     String BusiMgr) {
		return devMapper.getTotalDevProgressCount(majorCategory, subCategory, programType, programName, programId,
		programStatus, developer, devStatus, devStartDate, devEndDate, pl, 
		thirdPartyTestMgr, ItMgr, BusiMgr);
	}
	
	public void updatedevProgress(devProgress devProgress) {
		devMapper.updatedevProgress(devProgress);
	}
	
	//개발 진행현황 목록 삭제
	public void deleteDevProgress(int seq , int type) {
		devMapper.deleteDevProgress(seq);
		adminmapper.deleteAttachmentsByNoticeId(seq, type);
    }
	
	// 개발 진행현황 일괄 저장
    public void saveAllDevProgress(List<devProgress> devProgress) {
        for (devProgress dev : devProgress) {
        	try {
                devMapper.insertdevProgress(dev);
            } catch (DuplicateKeyException e) {
                throw new IllegalArgumentException("프로그램 ID가 중복되었습니다.");
            }
        }
    }
    
    public void insertdevProgress(devProgress devProgress) {
    	devMapper.insertdevProgress(devProgress);
    }
    
    //seq에 따른 개발 진행 현황 정보 추출
    public devProgress getDevById(Integer seq) {
        return devMapper.getDevById(seq);
    }
    
    //프로그램 아이디 중복 확인
    public boolean checkCountProgramId(String programId) {
    	return devMapper.checkCountProgramId(programId) == 0;
    }
    
    //프로그램 아이디 목록 확인
    public List<devProgress> checkProgramId(String programType, String developer, String programId, String programName) {
    	return devMapper.checkProgramId(programType, developer, programId, programName);
    }
    
    //프로그램 상세 정보 확인
    public List<devProgress> getprogramDetail(String programId) {
    	return devMapper.getprogramDetail(programId);
    }
    	
	

    
    
}