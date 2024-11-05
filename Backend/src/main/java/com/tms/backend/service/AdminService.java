
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
    
    //회원가입 기능
    public void join(User user) {
    	String encodedPassword = passwordEncoder.encode(user.getPassword()); // 암호화 비밀번호
    	user.setPassword(encodedPassword);
		adminmapper.insert(user); // 암호화 비밀번호 DB에 입력
	}
    
    // 사용자 데이터 수정 
    @Transactional
    public boolean updateUser(User user, HttpServletRequest request) {
    	log.info(user.getPassword());
    	//비밀번호 암호화
    	String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        
        //DB에서 유저 정보 업데이트
        boolean isUpdated = adminmapper.updateUser(user) > 0;
        
        //세션 정보가 존재하고, 해당 유저가 현재 세션에 로그인 중이면 세션 정보 업데이트
        HttpSession session = request.getSession(false); // 세션이 없다면 새로 만들지 않음
        
        if (session != null && user.getUserID().equals(session.getAttribute("id"))) {
            // 세션에 저장된 사용자 이름과 다른 정보도 업데이트
            session.setAttribute("name", user.getUserName());
            // 세션에 저장된 권한 코드와 권한 코드명 업데이트
            session.setAttribute("authorityCode", user.getAuthorityCode());
            session.setAttribute("authorityName", user.getAuthorityName());
        }
        
        return isUpdated;
    }
    
    // 사용자 데이터 삭제
    public boolean deleteUser(String[] ids) {
    	try {
            adminmapper.deleteUser(ids); // 삭제 성공시
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 사용자 데이터 총 숫자
    public int getTotal(Criteria criteria) {
		return adminmapper.countUsers(criteria);
	}
    
    // 사용자 데이터 전부 불러오기
    public List<User> getList(Criteria criteria) {
		return adminmapper.findAll(criteria);
	}
    
    // 여러 사용자 정보를 데이터베이스에 저장
    public void saveAll(List<User> users) {
        for (User user : users) {
        	String encodedPassword = passwordEncoder.encode(user.getPassword());
        	user.setPassword(encodedPassword);
            adminmapper.insert(user);
        }
    }
    
    // 권한 코드로 사용자 정보 불러오기
    public List<User> findAuthorityCode(int authorityCode) {
    	return adminmapper.findAuthorityCode(authorityCode);
    }
    
    // 사용자 정보 불러오기
    public List<User> getUserList() {
    	return adminmapper.getUserList();
    }
    
//    // 수행사, 고객, 기타 사용자 숫자 불러오기
//    public Integer getTesterNumber(String codeName) {
//    	return adminmapper.getTesterNumber(codeName);
//    }
    
    // 공지사항 서비스
    
    //공지사항 정보 불러오기
    public List<Notice> searchNotices(String startDate, String endDate, String title, String content, int page, int size) {
        int offset = (page - 1) * size;
        List<Notice> notices = adminmapper.searchNotices(startDate, endDate, title, content, offset, size);
        log.info(size);
        return notices;
    }
    
    //공지사항 정보 총 숫자
    public int getTotalNoticesCount(String startDate, String endDate, String title, String content) {
        return adminmapper.getTotalNoticesCount(startDate, endDate, title, content);
    }
    
    //최근 공지사항 데이터 불러오기
    public Notice getLatestNotice(List<Notice> noticeList) {
    	if (noticeList == null || noticeList.isEmpty()) {
            return null; // 리스트가 비어있거나 null인 경우
        }
    	Notice maxSeqNotice = noticeList.get(0); // 초기값으로 첫 번째 요소 설정
    	for (Notice notice : noticeList) {
            if (notice.getSeq() > maxSeqNotice.getSeq()) {
                maxSeqNotice = notice; // 더 큰 seq를 가진 Notice로 업데이트
            }
        }
        return maxSeqNotice;
    }
    
    //공지사항 등록
    public void createNotice(Notice notice) {
        adminmapper.insertNotice(notice); // 공지사항 저장
        }
    
    // seq에 해당하는 공지사항 데이터 가져오기
    public Notice getNoticeById(Integer seq) {
        return adminmapper.getNoticeById(seq);
    }
    
    // 공지사항 수정
    public void updateNotice(Notice notice) {
    	adminmapper.updateNotice(notice);
    }

    // 첨부파일 단체 저장
    public void saveAttachments(List<FileAttachment> attachments) {
        for (FileAttachment attachment : attachments) {
        	adminmapper.insertAttachment(attachment);
        }
    }
    
    // 첨부파일 삭제
    public void deleteAttachmentsByNoticeId(Integer seq, int type) { // type에 따라서 상위 게시판에 맞는 첨부파일이 삭제됨
    	adminmapper.deleteAttachmentsByNoticeId(seq, type);
    }
    
    // type에 따른 첨부파일 저장
    public List<FileAttachment> getAttachments(Integer seq, int type) { // type에 따라서 상위 게시판에 맞는 첨부파일이 저장됨
        return adminmapper.getAttachments(seq, type);
    }
    
    // 입력한 seq에 맞는 첨부파일 호출
    public FileAttachment getAttachmentById(Integer seq) {
        return adminmapper.getAttachmentById(seq); // DB에서는 identifier로 인식되는 seq
    }
    
    // 공지사항 삭제
    public void deleteNotice(Integer seq, int type) {
        // 공지사항 삭제
        adminmapper.deleteNotice(seq);
        adminmapper.deleteAttachmentsByNoticeId(seq, type); // 공지사항의 첨부파일도 같이 삭제
    }
    
    //공통 코드 Service
    
    //공통 코드 조회
    public List<CommonCode> searchCommonCodes(String parentCode, String code, String codeName, int page, int size) {
        int offset = (page - 1) * size;
        return adminmapper.searchCommonCodes(parentCode, code, codeName, offset, size);
    }
    
    // 공통 코드 갯수 총합
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
    
    // 공통코드 단체 삽입
    public void saveAllCommonCodes(List<CommonCode> commonCodes) {
        for (CommonCode commonCode : commonCodes) {
            adminmapper.insertCommonCode(commonCode);
        }
    }
    
    //공통코드 추가
    public void addCommonCode(CommonCode commonCode) {
        adminmapper.insertCommonCode(commonCode);
    }
    
    //공통코드 수정
    public boolean updateCommonCode(CommonCode commonCode) {
        return adminmapper.updateCommonCode(commonCode) > 0;
    }
    
    //공통코드 삭제
    public void deleteCommonCode(String seq) {
    	adminmapper.deleteCommonCode(seq);
    }
    
    //상위 코드에 따른 하위 코드 갯수 확인
    public int checkChildCodesExist(String code) {
    	return adminmapper.checkChildCodesExist(code);
    }
    
    // 공통 코드 조회 서비스
    public List<CommonCode> getParentCommonCodes() {
        return adminmapper.getParentCommonCodes();
    }
    
    // 상위 코드에 따른 하위 코드 목록을 조회
    public List<CommonCode> getCCCode(String parentCode) {
        return adminmapper.findSubCodesByParentCode(parentCode);
    }
    
    // 특정 코드의 상위코드 이름 조회
    public String searchParentCodeName(String parentCode) {
        return adminmapper.searchParentCodeName(parentCode);
    }
    
    //원하는 공통코드 이름 조회
    public String getStageCCodes(String parentCode, String code) {
    	return adminmapper.getStageCCodes(parentCode, code);
    }
    
    // 중복 공통코드 확인
    public boolean countdupliCCode(CommonCode commonCode) {
    	return adminmapper.countdupliCCode(commonCode) >0;
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
    
    public int checkChildCodesExist2(String code) {
    	return adminmapper.checkChildCodesExist2(code);
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
    
    // 중분류 코드 조회 서비스
    public List<categoryCode> getsubCategoryCodes() {
        return adminmapper.getsubCategoryCodes();
    }
    
    //원하는 분류 코드 이름 조회
    public String getStageCodes(String StageType, String Code) {
    	return adminmapper.getStageCodes(StageType, Code);
    }
    
    // 중복 분류코드 확인
    public boolean countdupliCtCode(categoryCode categoryCode) {
    	return adminmapper.countdupliCtCode(categoryCode) >0;
    }
    
}