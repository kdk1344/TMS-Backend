
package com.tms.backend.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.tms.backend.controller.UserController;
import com.tms.backend.mapper.AdminMapper;
import com.tms.backend.mapper.FileAttachmentMapper;
import com.tms.backend.mapper.UserMapper;
import com.tms.backend.vo.CommonCode;
import com.tms.backend.vo.Criteria;
import com.tms.backend.vo.FileAttachment;
import com.tms.backend.vo.Notice;
import com.tms.backend.vo.User;
import com.tms.backend.vo.categoryCode;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class AdminService {
	
	@Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AdminMapper adminmapper;
    
    @Autowired
    private FileAttachmentMapper fileAttachmentMapper;

    public void join(User user) {
    	log.info(user);
    	String encodedPassword = passwordEncoder.encode(user.getPassword());
    	log.info(encodedPassword);
    	user.setPassword(encodedPassword);
		adminmapper.insert(user);
	}
    
    
    
    public void saveUser(User user) {
        adminmapper.insertUser(user);
    }
    
    @Transactional
    public boolean updateUser(User user) {
    	String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        return adminmapper.updateUser(user) > 0;
    }
    
    public boolean deleteUser(String[] ids) {
    	try {
            adminmapper.deleteUser(ids);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public int getTotal(Criteria criteria) {
		return adminmapper.countUsers(criteria);
	}
    
    public List<User> getList(Criteria criteria) {
    	log.info(criteria);
		return adminmapper.findAll(criteria);
	}
    
    // 여러 사용자 정보를 데이터베이스에 저장
    public void saveAll(List<User> users) {
        for (User user : users) {
        	log.info(user);
        	String encodedPassword = passwordEncoder.encode(user.getPassword());
        	log.info(encodedPassword);
        	user.setPassword(encodedPassword);
            adminmapper.insertUser(user);
        }
    }
    
    public List<User> findDevlopers() {
    	return adminmapper.findDevlopers();
    }
    
    //// 공지사항 서비스

    public List<Notice> searchNotices(String startDate, String endDate, String title, String content, int page, int size) {
        int offset = (page - 1) * size;
        List<Notice> notices = adminmapper.searchNotices(startDate, endDate, title, content, offset, size);
        return notices;
    }

    public int getTotalNoticesCount(String startDate, String endDate, String title, String content) {
        return adminmapper.getTotalNoticesCount(startDate, endDate, title, content);
    }
    
    public Notice getLatestNotice() {
        return adminmapper.getLatestNotice();
    }
    
    public void createNotice(Notice notice) {
    	log.info(notice);
        adminmapper.insertNotice(notice); // 공지사항 저장
        }
    
    public Notice getNoticeById(Integer seq) {
        return adminmapper.getNoticeById(seq);
    }
    
    public void updateNotice(Notice notice) {
    	adminmapper.updateNotice(notice);
    }

    public void saveAttachments(List<FileAttachment> attachments) {
        for (FileAttachment attachment : attachments) {
        	adminmapper.insertAttachment(attachment);
        }
    }
    
    public void deleteAttachmentsByNoticeId(Integer seq) {
    	log.info("삭제 진행중");
    	adminmapper.deleteAttachmentsByNoticeId(seq);
    }
    
    public List<FileAttachment> getAttachments(Integer seq) {
        return adminmapper.getAttachmentsByNoticeId(seq);
    }
    
    public FileAttachment getAttachmentById(Integer seq) {
        return adminmapper.getAttachmentById(seq);
    }
    
    public void deleteNotice(Integer seq) {

        // 공지사항 삭제
        adminmapper.deleteNotice(seq);
    }
    
    
    //공통 코드 Service
    
    
    public List<CommonCode> searchCommonCodes(String parentCode, String code, String codeName, int page, int size) {
        int offset = (page - 1) * size;
        log.info(offset);
        return adminmapper.searchCommonCodes(parentCode, code, codeName, offset, size);
    }

    public int getTotalCommonCodeCount(String parentCode, String code, String codeName) {
        return adminmapper.getTotalCommonCodeCount(parentCode, code, codeName);
    }
    
    // 전체 공통코드 조회
    public List<CommonCode> getAllCommonCodes() {
        return adminmapper.getAllCommonCodes();
    }

    // 필터링된 공통코드 조회
    public List<CommonCode> getFilteredCommonCodes(String parentCode, String code, String codeName) {
        return adminmapper.getFilteredCommonCodes(parentCode, code, codeName);
    }
    
 // 공통코드 삽입
    public void saveAllCommonCodes(List<CommonCode> commonCodes) {
        for (CommonCode commonCode : commonCodes) {
            adminmapper.insertCommonCode(commonCode);
        }
    }
    
    public void addCommonCode(CommonCode commonCode) {
        adminmapper.insertCommonCode(commonCode);
    }

    public boolean updateCommonCode(CommonCode commonCode) {
        return adminmapper.updateCommonCode(commonCode) > 0;
    }

    public void deleteCommonCode(String seq) {
    	adminmapper.deleteCommonCode(seq);
    }
    
    public int checkChildCodesExist(String code) {
    	return adminmapper.checkChildCodesExist(code);
    }
    
    // 대분류 코드 조회 서비스
    public List<CommonCode> getParentCommonCodes() {
        return adminmapper.getParentCommonCodes();
    }
    
    public List<CommonCode> getCCCode(String parentCode) {
        return adminmapper.findSubCodesByParentCode(parentCode);
    }
    
    public String searchParentCodeName(String parentCode) {
        return adminmapper.searchParentCodeName(parentCode);
    }
        
    
    
    
    // 분류코드 Service
    
    // 카테고리 코드 추가
    public void addCategoryCode(categoryCode categoryCode) {
        adminmapper.insertCategoryCode(categoryCode);
    }
    
 // 상위 대분류 코드 가져오기
    public String getParentCode(String parentCode) {
        return adminmapper.getParentCode(parentCode);
    }

    // 카테고리 코드 수정
    public boolean updateCategoryCode(categoryCode categoryCode) {
        return adminmapper.updateCategoryCode(categoryCode) > 0;
    }

    // 카테고리 코드 삭제
    public void deleteCategoryCode(String code) {
    	adminmapper.deleteCategoryCodes(code);
    }

    // 카테고리 코드 조회
    public List<categoryCode> searchCategoryCodes(String parentCode, String code, String codeName, int page, int size) {
        int offset = (page - 1) * size;
        return adminmapper.searchCategoryCodes(parentCode, code, codeName, offset, size);
    }

    // 총 카테고리 코드 수 조회
    public int getTotalCategoryCodeCount(String parentCode, String code, String codeName) {
        return adminmapper.getTotalCategoryCodeCount(parentCode, code, codeName);
    }

    // 모든 카테고리 코드 가져오기
    public List<categoryCode> getAllCategoryCodes() {
        return adminmapper.getAllCategoryCodes();
    }

    // 필터링된 카테고리 코드 가져오기
    public List<categoryCode> getFilteredCategoryCodes(String stageType, String code, String codeName) {
        return adminmapper.getFilteredCategoryCodes(stageType, code, codeName);
    }

    // 카테고리 코드 일괄 저장
    public void saveAllCategoryCodes(List<categoryCode> categoryCodes) {
        for (categoryCode categoryCode : categoryCodes) {
            adminmapper.insertCategoryCode(categoryCode);
        }
    }
    
    // 대분류 코드 조회 서비스
    public List<categoryCode> getParentCategoryCodes() {
        return adminmapper.getParentCategoryCodes();
    }
    
    public List<categoryCode> getCTCode(String parentCode) {
        return adminmapper.findMiddleCodesByParentCode(parentCode);
    }
    
}