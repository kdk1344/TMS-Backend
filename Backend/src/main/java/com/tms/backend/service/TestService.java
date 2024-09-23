
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
import com.tms.backend.mapper.TestMapper;
import com.tms.backend.mapper.UserMapper;
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

@Service
@Log4j
public class TestService {
	
	@Autowired
    private TestMapper testmapper;
	
	@Autowired
	private AdminMapper adminmapper;

	public List<testProgress> searchTestProgress(String testStage, String majorCategory, String subCategory,
										        String programType, String testId, String screenId, String screenName,
										        String programName, String programId,
										        String developer, String testStatus, String pl, 
										        String ItMgr, String BusiMgr, String execCompanyMgr, String thirdPartyTestMgr,
										        int page, int size) {
		int offset = (page - 1) * size;
		return testmapper.searchTestProgress(testStage, majorCategory, subCategory, programType, testId, screenId, screenName, programName, 
	            programId, developer, testStatus, pl, ItMgr, BusiMgr, execCompanyMgr, thirdPartyTestMgr, offset, size);
	}
	
	public int getTotalTestCount(String testStage, String majorCategory, String subCategory,
						        String programType, String testId, String screenId, String screenName,
						        String programName, String programId,
						        String developer, String testStatus, String pl, 
						        String ItMgr, String BusiMgr, String execCompanyMgr, String thirdPartyTestMgr) {
	    
	    return testmapper.getTotalTestCount(
	            testStage, majorCategory, subCategory, programType, testId, screenId, screenName, programName, 
	            programId, developer, testStatus, pl, ItMgr, BusiMgr, execCompanyMgr, thirdPartyTestMgr);
	}
	
//	//테스트 진행 현황 수정
//	public void updatetestProgress(testProgress testProgress) {
//		testmapper.updatedevProgress(testProgress);
//	}
//	
	//개발 진행현황 목록 삭제
	public void deleteTestProgress(int seq , int type) {
		testmapper.deleteTestProgress(seq);
		adminmapper.deleteAttachmentsByNoticeId(seq, type);
    }
	

    
    
}