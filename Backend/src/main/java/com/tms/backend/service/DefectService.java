
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
public class DefectService {
	
	@Autowired
    private DefectMapper defectmapper;

    public List<Defect> searchDefects(String testCategory, String majorCategory, String subCategory, String defectSeverity, String defectSeq, String defectRegistrar, String defectHandler, String defectStatus, int page, int size) {
        int offset = (page - 1) * size;
        return defectmapper.searchDefects(testCategory, majorCategory, subCategory, defectSeverity, defectSeq, defectRegistrar, defectHandler, defectStatus, offset, size);
    }

    public int getTotalDefectsCount(String testCategory, String majorCategory, String subCategory, String defectSeverity, String defectSeq, String defectRegistrar, String defectHandler, String defectStatus) {
        return defectmapper.countDefects(testCategory, majorCategory, subCategory, defectSeverity, defectSeq, defectRegistrar, defectHandler, defectStatus);
    }
    
    public void deleteDefect(int seq) {
    	defectmapper.deleteDefect(seq);
    }
    
    public void saveAllDefect(List<Defect> defect) {
        for (Defect def : defect) {
            defectmapper.insertdefect(def);
        }	
    }
    
    // 특정 아이디 결함 건수
    public int countDefect(String programId, String managerType) {
    	return defectmapper.countDefect(programId, managerType);
    }
    
    // 특정 아이디 결함 조치 완료 건수
    public int countDefectSoultions(String programId, String managerType) {
    	return defectmapper.countDefectSoultions(programId, managerType);
    }	
	

    
    
}