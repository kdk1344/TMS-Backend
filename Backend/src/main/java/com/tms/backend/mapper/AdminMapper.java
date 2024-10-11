package com.tms.backend.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.tms.backend.vo.CommonCode;
import com.tms.backend.vo.Criteria;
import com.tms.backend.vo.FileAttachment;
import com.tms.backend.vo.Notice;
import com.tms.backend.vo.User;
import com.tms.backend.vo.categoryCode;

import lombok.extern.log4j.Log4j;

@Mapper
public interface AdminMapper {
	
	// 사용자
    public void insert(User user); // 사용자 데이터 입력
    public int updateUser(User user); // 사용자 데이터 수정
    void deleteUser(String[] ids); // 사용자 데이터 삭제
    public List<User> findAll(Criteria criteria); // 사용자 데이터 전부 추출
	public int countUsers(Criteria criteria); // 사용자 데이터 숫자
	public List<User> findAuthorityCode(int authorityCode); // 권한 코드에 따른 사용자 데이터 추출
	public List<User> getUserList(); // 사용자 데이터 추출
//	public Integer getTesterNumber(String codeName); // 수행사, 고객, 기타 사용자 숫자 불러오기
	
	//공지사항
	public List<Notice> searchNotices(@Param("startDate") String postDate,
			@Param("endDate") String endDate,
            @Param("title") String title,
            @Param("content") String content,
            @Param("offset") int offset,
            @Param("size") int size); // 공지사항 조회
	public int getTotalNoticesCount(@Param("startDate") String postDate,
			@Param("endDate") String endDate,
	        @Param("title") String title,
	        @Param("content") String content); // 공지사항 총 갯수
	public Notice getLatestNotice(); // 최근 공지사항 조회
	public List<FileAttachment> getAttachmentsByNoticeSEQ(@Param("seq") int seq); // seq에 따른 첨부파일 추출
	public void insertNotice(Notice notice); // 공지사항 입력
    public void insertAttachment(FileAttachment attachment); // 첨부파일 입력
    public Notice getNoticeById(Integer seq); // seq에 따른 공지사항 추출
    public void updateNotice(Notice notice); // 공지사항 수정
    public void deleteAttachmentsByNoticeId(@Param("seq") int seq,
    										@Param("type") int type); // 게시판에 따른 첨부파일 삭제
    public List<FileAttachment> getAttachments(@Param("seq") int seq,
												@Param("type") int type); // 게시판에 따른 첨부파일 추출
    public FileAttachment getAttachmentById(Integer seq); // seq에 따른 첨부파일 추출
    public void deleteNotice(Integer seq); // 공지사항 삭제
    
    //공통코드 mapper
    public List<CommonCode> searchCommonCodes(@Param("parentCode") String parentCode, 
            @Param("code") String code, 
            @Param("codeName") String codeName, 
            @Param("offset") int offset, 
            @Param("size") int size); //공통코드 조회
	public int getTotalCommonCodeCount(@Param("parentCode") String parentCode, 
	     @Param("code") String code, 
	     @Param("codeName") String codeName); //공통코드 총 숫자
	public List<CommonCode> getAllCommonCodes(); // 모든 공통코드 추출
    public List<CommonCode> getFilteredCommonCodes(@Param("parentCode") String parentCode, 
    										@Param("code") String code, 
                                            @Param("codeName") String codeName); // 필터링된 공통코드 조회
	public void insertCommonCode(CommonCode commonCode); // 공통코드 삽입
    public int updateCommonCode(CommonCode commonCode); // 공통코드 수정
    public void deleteCommonCode(String seq); // 공통코드 삭제
    public int checkChildCodesExist(String code); // 상위 코드에 따른 하위 코드 갯수 확인
    public List<CommonCode> getParentCommonCodes(); // 대분류 코드 추출용
    public List<CommonCode> findSubCodesByParentCode(String parentCode); // 상위 코드에 따른 하위 코드 목록을 조회하는 쿼리
    public String searchParentCodeName(String parentCode); // 특정 코드의 상위코드 이름 조회
    public String getStageCCodes(@Param("parentCode") String parentCode, 
    							@Param("code") String code); // 상위 코드에 따른 공통 코드 이름 조회
    public int countdupliCCode(CommonCode commonCode); //중복 공통코드 확인
    
    
    // 분류코드 mapper
    public void insertCategoryCode(categoryCode categoryCode); // 분류코드 입력
    public String getParentCode(String parentCode); // 상위 대분류 코드 가져오기
    public int updateCategoryCode(categoryCode categoryCode); // 분류코드 수정
    public void deleteCategoryCodes(String code); // 분류코드 삭제
    public int checkChildCodesExist2(String code); // 상위 코드에 따른 하위 분류 코드 갯수 확인
    public List<categoryCode> searchCategoryCodes(@Param("parentCode") String parentCode, 
	                                              @Param("code") String code, 
	                                              @Param("codeName") String codeName, 
	                                              @Param("offset") int offset, 
	                                              @Param("size") int size); //  분류코드 조회
    public int getTotalCategoryCodeCount(@Param("parentCode") String parentCode,
    									 @Param("code") String code,
    									 @Param("codeName") String codeName); // 분류코드 총 갯수
    public List<categoryCode> getAllCategoryCodes(); // 모든 분류코드 데이터 가져오기
    public List<categoryCode> getFilteredCategoryCodes(@Param("stageType") String stageType, 
                                                	   @Param("code") String code, 
                                                	   @Param("codeName") String codeName); // 필터링된 공통코드 데이터 가져오기
    public List<categoryCode> getParentCategoryCodes(); // 대분류 코드만 가져오기
    public List<categoryCode> findMiddleCodesByParentCode(String parentCode); //상위 코드에 따른 하위 분류 코드 목록을 조회하는 쿼리
    public List<categoryCode> getsubCategoryCodes(); //중분류 코드만 가져오기
    public String getStageCodes(@Param("stageType") String stageType, 
     	   						@Param("code") String code); // 특정 코드 이름 가져오기
    public int countdupliCtCode(categoryCode categoryCode); // 중복 분류코드 확인
}
