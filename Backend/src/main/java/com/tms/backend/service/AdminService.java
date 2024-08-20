
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
import com.tms.backend.mapper.FileAttachmentMapper;
import com.tms.backend.mapper.UserMapper;
import com.tms.backend.vo.CommonCode;
import com.tms.backend.vo.Criteria;
import com.tms.backend.vo.FileAttachment;
import com.tms.backend.vo.Notice;
import com.tms.backend.vo.User;

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
    
    //// 공지사항 서비스

    public List<Notice> searchNotices(String startDate, String endDate, String title, String content, int page, int size) {
        int offset = (page - 1) * size;
        List<Notice> notices = adminmapper.searchNotices(startDate, endDate, title, content, offset, size);
        return notices;
    }

    public int getTotalNoticesCount(String startDate, String endDate, String title, String content) {
        return adminmapper.getTotalNoticesCount(startDate, endDate, title, content);
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
    
    
    
    //공통 코드
    public List<CommonCode> searchCommonCodes(Character parentCode, Character code, String codeName, int page, int size) {
        int offset = (page - 1) * size;
        return adminmapper.searchCommonCodes(parentCode, code, codeName, offset, size);
    }

    public int getTotalCommonCodeCount(Character parentCode, Character code, String codeName) {
        return adminmapper.getTotalCommonCodeCount(parentCode, code, codeName);
    }
    
    // 전체 공통코드 조회
    public List<CommonCode> getAllCommonCodes() {
        return adminmapper.getAllCommonCodes();
    }

    // 필터링된 공통코드 조회
    public List<CommonCode> getFilteredCommonCodes(Character parentCode, Character code, String codeName) {
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

    public boolean deleteCommonCode(String[] codeList) {
        return adminmapper.deleteCommonCode(codeList) > 0;
    }
    
    
    
    
}