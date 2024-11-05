
package com.tms.backend.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
    
    //ȸ������ ���
    public void join(User user) {
    	String encodedPassword = passwordEncoder.encode(user.getPassword()); // ��ȣȭ ��й�ȣ
    	user.setPassword(encodedPassword);
		adminmapper.insert(user); // ��ȣȭ ��й�ȣ DB�� �Է�
	}
    
    // ����� ������ ���� 
    @Transactional
    public boolean updateUser(User user, HttpServletRequest request) {
    	log.info(user.getPassword());
    	//��й�ȣ ��ȣȭ
    	String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        
        //DB���� ���� ���� ������Ʈ
        boolean isUpdated = adminmapper.updateUser(user) > 0;
        
        //���� ������ �����ϰ�, �ش� ������ ���� ���ǿ� �α��� ���̸� ���� ���� ������Ʈ
        HttpSession session = request.getSession(false); // ������ ���ٸ� ���� ������ ����
        
        if (session != null && user.getUserID().equals(session.getAttribute("id"))) {
            // ���ǿ� ����� ����� �̸��� �ٸ� ������ ������Ʈ
            session.setAttribute("name", user.getUserName());
            // ���ǿ� ����� ���� �ڵ�� ���� �ڵ�� ������Ʈ
            session.setAttribute("authorityCode", user.getAuthorityCode());
            session.setAttribute("authorityName", user.getAuthorityName());
        }
        
        return isUpdated;
    }
    
    // ����� ������ ����
    public boolean deleteUser(String[] ids) {
    	try {
            adminmapper.deleteUser(ids); // ���� ������
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ����� ������ �� ����
    public int getTotal(Criteria criteria) {
		return adminmapper.countUsers(criteria);
	}
    
    // ����� ������ ���� �ҷ�����
    public List<User> getList(Criteria criteria) {
		return adminmapper.findAll(criteria);
	}
    
    // ���� ����� ������ �����ͺ��̽��� ����
    public void saveAll(List<User> users) {
        for (User user : users) {
        	String encodedPassword = passwordEncoder.encode(user.getPassword());
        	user.setPassword(encodedPassword);
            adminmapper.insert(user);
        }
    }
    
    // ���� �ڵ�� ����� ���� �ҷ�����
    public List<User> findAuthorityCode(int authorityCode) {
    	return adminmapper.findAuthorityCode(authorityCode);
    }
    
    // ����� ���� �ҷ�����
    public List<User> getUserList() {
    	return adminmapper.getUserList();
    }
    
//    // �����, ��, ��Ÿ ����� ���� �ҷ�����
//    public Integer getTesterNumber(String codeName) {
//    	return adminmapper.getTesterNumber(codeName);
//    }
    
    // �������� ����
    
    //�������� ���� �ҷ�����
    public List<Notice> searchNotices(String startDate, String endDate, String title, String content, int page, int size) {
        int offset = (page - 1) * size;
        List<Notice> notices = adminmapper.searchNotices(startDate, endDate, title, content, offset, size);
        log.info(size);
        return notices;
    }
    
    //�������� ���� �� ����
    public int getTotalNoticesCount(String startDate, String endDate, String title, String content) {
        return adminmapper.getTotalNoticesCount(startDate, endDate, title, content);
    }
    
    //�ֱ� �������� ������ �ҷ�����
    public Notice getLatestNotice(List<Notice> noticeList) {
    	if (noticeList == null || noticeList.isEmpty()) {
            return null; // ����Ʈ�� ����ְų� null�� ���
        }
    	Notice maxSeqNotice = noticeList.get(0); // �ʱⰪ���� ù ��° ��� ����
    	for (Notice notice : noticeList) {
            if (notice.getSeq() > maxSeqNotice.getSeq()) {
                maxSeqNotice = notice; // �� ū seq�� ���� Notice�� ������Ʈ
            }
        }
        return maxSeqNotice;
    }
    
    //�������� ���
    public void createNotice(Notice notice) {
        adminmapper.insertNotice(notice); // �������� ����
        }
    
    // seq�� �ش��ϴ� �������� ������ ��������
    public Notice getNoticeById(Integer seq) {
        return adminmapper.getNoticeById(seq);
    }
    
    // �������� ����
    public void updateNotice(Notice notice) {
    	adminmapper.updateNotice(notice);
    }

    // ÷������ ��ü ����
    public void saveAttachments(List<FileAttachment> attachments) {
        for (FileAttachment attachment : attachments) {
        	adminmapper.insertAttachment(attachment);
        }
    }
    
    // ÷������ ����
    public void deleteAttachmentsByNoticeId(Integer seq, int type) { // type�� ���� ���� �Խ��ǿ� �´� ÷�������� ������
    	adminmapper.deleteAttachmentsByNoticeId(seq, type);
    }
    
    // type�� ���� ÷������ ����
    public List<FileAttachment> getAttachments(Integer seq, int type) { // type�� ���� ���� �Խ��ǿ� �´� ÷�������� �����
        return adminmapper.getAttachments(seq, type);
    }
    
    // �Է��� seq�� �´� ÷������ ȣ��
    public FileAttachment getAttachmentById(Integer seq) {
        return adminmapper.getAttachmentById(seq); // DB������ identifier�� �νĵǴ� seq
    }
    
    // �������� ����
    public void deleteNotice(Integer seq, int type) {
        // �������� ����
        adminmapper.deleteNotice(seq);
        adminmapper.deleteAttachmentsByNoticeId(seq, type); // ���������� ÷�����ϵ� ���� ����
    }
    
    //���� �ڵ� Service
    
    //���� �ڵ� ��ȸ
    public List<CommonCode> searchCommonCodes(String parentCode, String code, String codeName, int page, int size) {
        int offset = (page - 1) * size;
        return adminmapper.searchCommonCodes(parentCode, code, codeName, offset, size);
    }
    
    // ���� �ڵ� ���� ����
    public int getTotalCommonCodeCount(String parentCode, String code, String codeName) {
        return adminmapper.getTotalCommonCodeCount(parentCode, code, codeName);
    }
    
    // ��ü �����ڵ� ��ȸ
    public List<CommonCode> getAllCommonCodes() {
        return adminmapper.getAllCommonCodes();
    }

    // ���͸��� �����ڵ� ��ȸ
    public List<CommonCode> getFilteredCommonCodes(String parentCode, String code, String codeName) {
        return adminmapper.getFilteredCommonCodes(parentCode, code, codeName);
    }
    
    // �����ڵ� ��ü ����
    public void saveAllCommonCodes(List<CommonCode> commonCodes) {
        for (CommonCode commonCode : commonCodes) {
            adminmapper.insertCommonCode(commonCode);
        }
    }
    
    //�����ڵ� �߰�
    public void addCommonCode(CommonCode commonCode) {
        adminmapper.insertCommonCode(commonCode);
    }
    
    //�����ڵ� ����
    public boolean updateCommonCode(CommonCode commonCode) {
        return adminmapper.updateCommonCode(commonCode) > 0;
    }
    
    //�����ڵ� ����
    public void deleteCommonCode(String seq) {
    	adminmapper.deleteCommonCode(seq);
    }
    
    //���� �ڵ忡 ���� ���� �ڵ� ���� Ȯ��
    public int checkChildCodesExist(String code) {
    	return adminmapper.checkChildCodesExist(code);
    }
    
    // ���� �ڵ� ��ȸ ����
    public List<CommonCode> getParentCommonCodes() {
        return adminmapper.getParentCommonCodes();
    }
    
    // ���� �ڵ忡 ���� ���� �ڵ� ����� ��ȸ
    public List<CommonCode> getCCCode(String parentCode) {
        return adminmapper.findSubCodesByParentCode(parentCode);
    }
    
    // Ư�� �ڵ��� �����ڵ� �̸� ��ȸ
    public String searchParentCodeName(String parentCode) {
        return adminmapper.searchParentCodeName(parentCode);
    }
    
    //���ϴ� �����ڵ� �̸� ��ȸ
    public String getStageCCodes(String parentCode, String code) {
    	return adminmapper.getStageCCodes(parentCode, code);
    }
    
    // �ߺ� �����ڵ� Ȯ��
    public boolean countdupliCCode(CommonCode commonCode) {
    	return adminmapper.countdupliCCode(commonCode) >0;
    }
    
    // �з��ڵ� Service
    
    // ī�װ� �ڵ� �߰�
    public void addCategoryCode(categoryCode categoryCode) {
        adminmapper.insertCategoryCode(categoryCode);
    }
    
    // ���� ��з� �ڵ� ��������
    public String getParentCode(String parentCode) {
        return adminmapper.getParentCode(parentCode);
    }

    // ī�װ� �ڵ� ����
    public boolean updateCategoryCode(categoryCode categoryCode) {
        return adminmapper.updateCategoryCode(categoryCode) > 0;
    }

    // ī�װ� �ڵ� ����
    public void deleteCategoryCode(String code) {
    	adminmapper.deleteCategoryCodes(code);
    }
    
    public int checkChildCodesExist2(String code) {
    	return adminmapper.checkChildCodesExist2(code);
    }

    // ī�װ� �ڵ� ��ȸ
    public List<categoryCode> searchCategoryCodes(String parentCode, String code, String codeName, int page, int size) {
        int offset = (page - 1) * size;
        return adminmapper.searchCategoryCodes(parentCode, code, codeName, offset, size);
    }

    // �� ī�װ� �ڵ� �� ��ȸ
    public int getTotalCategoryCodeCount(String parentCode, String code, String codeName) {
        return adminmapper.getTotalCategoryCodeCount(parentCode, code, codeName);
    }

    // ��� ī�װ� �ڵ� ��������
    public List<categoryCode> getAllCategoryCodes() {
        return adminmapper.getAllCategoryCodes();
    }

    // ���͸��� ī�װ� �ڵ� ��������
    public List<categoryCode> getFilteredCategoryCodes(String stageType, String code, String codeName) {
        return adminmapper.getFilteredCategoryCodes(stageType, code, codeName);
    }

    // ī�װ� �ڵ� �ϰ� ����
    public void saveAllCategoryCodes(List<categoryCode> categoryCodes) {
        for (categoryCode categoryCode : categoryCodes) {
            adminmapper.insertCategoryCode(categoryCode);
        }
    }
    
    // ��з� �ڵ� ��ȸ ����
    public List<categoryCode> getParentCategoryCodes() {
        return adminmapper.getParentCategoryCodes();
    }
    
    public List<categoryCode> getCTCode(String parentCode) {
        return adminmapper.findMiddleCodesByParentCode(parentCode);
    }
    
    // �ߺз� �ڵ� ��ȸ ����
    public List<categoryCode> getsubCategoryCodes() {
        return adminmapper.getsubCategoryCodes();
    }
    
    //���ϴ� �з� �ڵ� �̸� ��ȸ
    public String getStageCodes(String StageType, String Code) {
    	return adminmapper.getStageCodes(StageType, Code);
    }
    
    // �ߺ� �з��ڵ� Ȯ��
    public boolean countdupliCtCode(categoryCode categoryCode) {
    	return adminmapper.countdupliCtCode(categoryCode) >0;
    }
    
}