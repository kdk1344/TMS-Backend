
package com.tms.backend.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.tms.backend.vo.testProgress;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class DefectService {
	
	@Autowired
    private DefectMapper defectmapper;
	
	@Autowired
	private AdminMapper adminmapper;
	
	// 결함 조회
    public List<Defect> searchDefects(String testStage, String majorCategory, String subCategory, String defectSeverity, Integer seq,
    		String defectRegistrar, String defectHandler, String pl, String defectStatus, String programId, String testId, String programName,
    		String programType, int page, int size) {
    	int offset = (page - 1) * size;
    	
        return defectmapper.searchDefects(testStage, majorCategory, subCategory, defectSeverity, seq, defectRegistrar, defectHandler, pl, defectStatus,
        		programId, testId, programName, programType, offset, size);
    }
    
    // 결함 총 갯수
    public int getTotalDefectsCount(String testStage, String majorCategory, String subCategory, String defectSeverity, Integer seq,
    		String defectRegistrar, String defectHandler, String pl, String defectStatus, String programId,
    		String testId, String programName, String programType) {
        return defectmapper.countDefects(testStage, majorCategory, subCategory, defectSeverity, seq, defectRegistrar, defectHandler, pl,
        		defectStatus, programId, testId, programName, programType);
    }
    
    // 결함 수정을 위한 결함 내용 조회
    public List<Defect> searchDefectOriginal(String testStage, String testId, String programId, String programName){
    	return defectmapper.searchDefectOriginal(testStage, testId, programId, programName);
    }
    
    //결함 수정
    public void updateDefect(Defect defect) {
    	defectmapper.updateDefect(defect);
    }
    
    //결함 등록
    public void insertdefect(Defect defect) {
    	DefectStatusCheck(defect);
    	defectmapper.insertdefect(defect);
    }
    
    //결함 삭제
    public void deleteDefect(Integer seq, int type) {
    	defectmapper.deleteDefect(seq);
    	adminmapper.deleteAttachmentsByNoticeId(seq, type);
    }
    
    //seq에 따른 결함 현황 가져오기
    public Defect getDefectById(Integer seq) {
    	return defectmapper.getDefectById(seq);
    }
    
    //전체 결함 보기
    public List<Defect> searchAllDefects() {
    	return defectmapper.searchAllDefects();
    }
    
    // 결함 여러개 등록
    public void saveAllDefect(List<Defect> defect) {
        for (Defect def : defect) {
        	DefectStatusCheck(def);
        	if (def.getPlDefectJudgeClass()== null && def.getPlDefectJudgeClass().isEmpty()) {
        		def.setPlDefectJudgeClass("결함수용");
        	}
            defectmapper.insertdefect(def);
        }	
    }
    
    // 특정 아이디 결함 건수
    public int countDefect(String programId, String managerType) {
    	return defectmapper.countDefect(programId, managerType);
    }
    
    //총 결함 건수
    public int totalcountDefect(String programId) {
    	return defectmapper.totalcountDefect(programId);
    }
    
    // 특정 아이디 결함 조치 완료 건수
    public int countDefectSoultions(String programId, String managerType) {
    	return defectmapper.countDefectSoultions(programId, managerType);
    }
    
    // 조치완료일, PL 확인일에 따른 결함 처리 상태 자동 세팅
    public void DefectStatusCheck(Defect defect) {    	
		if(defect.getDefectCompletionDate() == null && defect.getPlConfirmDate() == null) {
			defect.setDefectStatus("등록완료");}
		if(defect.getDefectCompletionDate() != null && defect.getPlConfirmDate() == null) {
			defect.setDefectStatus("조치완료");}
		if(defect.getDefectCompletionDate() != null && defect.getPlConfirmDate() != null && defect.getDefectRegConfirmDate() == null) {
			defect.setDefectStatus("PL 확인완료");}
		if(defect.getDefectCompletionDate() != null && defect.getPlConfirmDate() != null && defect.getDefectRegConfirmDate() != null) {
			defect.setDefectStatus("등록자 확인완료");
			}
		}
        
}