
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
	
	@Autowired
	private AdminMapper adminmapper;

    public List<Defect> searchDefects(String testStage, String majorCategory, String subCategory, String defectSeverity, Integer seq,
    		String defectRegistrar, String defectHandler, String pl, String defectStatus, String programId, String testId, String programName,
    		String programType, int page, int size) {
    	int offset = (page - 1) * size;
    	log.info("check"+offset+"size:"+size);
    	log.info(programName);
    	log.info(programId);
    	
        return defectmapper.searchDefects(testStage, majorCategory, subCategory, defectSeverity, seq, defectRegistrar, defectHandler, pl,
        		programId, testId, programName, programType, defectStatus, offset, size);
    }

    public int getTotalDefectsCount(String testStage, String majorCategory, String subCategory, String defectSeverity, Integer seq,
    		String defectRegistrar, String defectHandler, String pl, String defectStatus, String programId,
    		String testId, String programName, String programType) {
        return defectmapper.countDefects(testStage, majorCategory, subCategory, defectSeverity, seq, defectRegistrar, defectHandler, pl,
        		defectStatus, programId, testId, programName, programType);
    }
    
    public List<Defect> searchDefectOriginal(String programId, String programName){
    	return defectmapper.searchDefectOriginal(programId, programName);
    }
    
    //���� ������Ʈ
    public void updateDefect(Defect defect) {
    	defectmapper.updateDefect(defect);
    }
    
    //���� ���
    public void insertdefect(Defect defect) {
    	defectmapper.insertdefect(defect);
    }
    
    //���� ����
    public void deleteDefect(Integer seq, int type) {
    	defectmapper.deleteDefect(seq);
    	adminmapper.deleteAttachmentsByNoticeId(seq, type);
    }
    
    //seq�� ���� ���� ��Ȳ ��������
    public Defect getDefectById(Integer seq) {
    	return defectmapper.getDefectById(seq);
    }
    
    //��ü ���� ����
    public List<Defect> searchAllDefects() {
    	return defectmapper.searchAllDefects();
    }
    
    // ���� ������ ���
    public void saveAllDefect(List<Defect> defect) {
        for (Defect def : defect) {
            defectmapper.insertdefect(def);
        }	
    }
    
    // Ư�� ���̵� ���� �Ǽ�
    public int countDefect(String programId, String managerType) {
    	return defectmapper.countDefect(programId, managerType);
    }
    
    // Ư�� ���̵� ���� ��ġ �Ϸ� �Ǽ�
    public int countDefectSoultions(String programId, String managerType) {
    	return defectmapper.countDefectSoultions(programId, managerType);
    }
    
//    //��߻� ���Թ�ȣ ��ȸ
//    public List<Defect> getdefectNumberList(String testStage, String testId, String programName, String programType){
//    	return defectmapper.getdefectNumberList(testStage, testId, programName, programType);
//    }
	

    
    
}