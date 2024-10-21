
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
import com.tms.backend.vo.deleteHistory;
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
	
	// 프로그램 개발 진행 현황 조회
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
	
	// 프로그램 개발 진행 현황 총 갯수
	public int getTotalDevProgressCount(String majorCategory, String subCategory, String programType, 
	     String programName, String programId, String programStatus, String developer, 
	     String devStatus, String devStartDate, String devEndDate, 
	     String pl, String thirdPartyTestMgr, String ItMgr, 
	     String BusiMgr) {
		return devMapper.getTotalDevProgressCount(majorCategory, subCategory, programType, programName, programId,
		programStatus, developer, devStatus, devStartDate, devEndDate, pl, 
		thirdPartyTestMgr, ItMgr, BusiMgr);
	}
	
	// 프로그램 개발 진행 현황 수정
	public void updatedevProgress(devProgress devProgress) {
		if((devProgress.getDeletionHandler() != null && !devProgress.getDeletionHandler().isEmpty()) &&
	    		(devProgress.getDeletionDate() != null) &&
	    		(devProgress.getDeletionReason() != null && !devProgress.getDeletionReason().isEmpty())) {
	    		insertDeleteHistory(devProgress);
	    	}
		devMapper.updatedevProgress(devProgress);
	}
	
	//프로그램 개발 진행현황 목록 삭제
	public void deleteDevProgress(int seq , int type) {
		devMapper.deleteDevProgress(seq);
		adminmapper.deleteAttachmentsByNoticeId(seq, type);
    }
	
	// 프로그램 개발 진행현황 일괄 저장
    public void saveAllDevProgress(List<devProgress> devProgress) {
        for (devProgress dev : devProgress) {
        	try {
                devMapper.insertdevProgress(dev);
            } catch (DuplicateKeyException e) {
                throw new IllegalArgumentException("프로그램 ID가 중복되었습니다.");
            }
        }
    }
    
    // 프로그램 개발 진행 현황 입력
    public void insertdevProgress(devProgress devProgress) {
    	devMapper.insertdevProgress(devProgress);
    }
    
    //seq에 따른 프로그램 개발 진행 현황 정보 추출
    public devProgress getDevById(Integer seq) {
        return devMapper.getDevById(seq);
    }
    
    //프로그램 아이디 중복 확인
    public boolean checkCountProgramId(String programId) {
    	return devMapper.checkCountProgramId(programId) == 0;
    }
    
    //프로그램 아이디 목록 확인
    public List<devProgress> checkProgramId(String programType, String developer, String programId, String programName, String screenId) {
    	return devMapper.checkProgramId(programType, developer, programId, programName, screenId);
    }
    
    //프로그램 상세 정보 확인
    public List<devProgress> getprogramDetail(String programId) {
    	return devMapper.getprogramDetail(programId);
    }
    
    //삭제 기록 deleteHistory 테이블에 전송
    private void insertDeleteHistory(devProgress devProgress) {
    	deleteHistory deletehistory = new deleteHistory(); // 메서드 내에서 deleteHistory 객체 로컬 변수로 선언 및 초기화
    	log.info(devProgress.getSeq());
    	// deleteHistory 속성 설정
    	deletehistory.setSeq(devProgress.getSeq());
    	deletehistory.setProgramName(devProgress.getProgramName());
    	deletehistory.setProgramId(devProgress.getProgramId());
    	deletehistory.setDeletionDate(devProgress.getDeletionDate());
    	deletehistory.setDeletionHandler(devProgress.getDeletionHandler());
    	deletehistory.setDeletionReason(devProgress.getDeletionHandler());
    	if (checkDeleteHistory(devProgress.getSeq()) == 0) {
    		devMapper.insertDeleteHistory(deletehistory);} // deleteHistory DB에 삽입
    	else{
    		devMapper.updateDeleteHistory(deletehistory); // deleteHistory DB 내용 수정
    	}
    }
    
    // 삭제 기록 존재 확인
    private int checkDeleteHistory(int seq) {
    	return devMapper.checkDeleteHistory(seq);
    }
    
}