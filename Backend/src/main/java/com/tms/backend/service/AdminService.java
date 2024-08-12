
package com.tms.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tms.backend.controller.UserController;
import com.tms.backend.mapper.AdminMapper;
import com.tms.backend.mapper.FileAttachmentMapper;
import com.tms.backend.mapper.UserMapper;
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
    
    public List<Notice> getAllNotices() {
        List<Notice> notices = adminmapper.getAllNotices();
        for (Notice notice : notices) {
            notice.setAttachments(fileAttachmentMapper.getAttachmentsByNoticeId(notice.getSeq()));
        }
        return notices;
    }

    public Notice getNoticeById(Long id) {
        Notice notice = adminmapper.getNoticeById(id);
        if (notice != null) {
            notice.setAttachments(fileAttachmentMapper.getAttachmentsByNoticeId(id));
        }
        return notice;
    }

    public void createNotice(Notice notice) {
        adminmapper.insertNotice(notice);
        if (notice.getAttachments() != null) {
            for (FileAttachment attachment : notice.getAttachments()) {
                attachment.setSeq(notice.getSeq());
                fileAttachmentMapper.insertFileAttachment(attachment);
            }
        }
    }

    public void updateNotice(Notice notice) {
        adminmapper.updateNotice(notice);
        fileAttachmentMapper.deleteAttachmentsByNoticeId(notice.getSeq());
        if (notice.getAttachments() != null) {
            for (FileAttachment attachment : notice.getAttachments()) {
                attachment.setSeq(notice.getSeq());
                fileAttachmentMapper.insertFileAttachment(attachment);
            }
        }
    }

    public void deleteNotice(Long id) {
        fileAttachmentMapper.deleteAttachmentsByNoticeId(id);
        adminmapper.deleteNotice(id);
    }
}