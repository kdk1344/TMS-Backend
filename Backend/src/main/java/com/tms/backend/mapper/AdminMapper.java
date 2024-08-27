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

    public void insert(User user);
    public void insertUser(User user);
    public int updateUser(User user);
    void deleteUser(String[] ids);
    public List<User> findAll(Criteria criteria);
	public int countUsers(Criteria criteria);
	public List<User> findDevlopers();
	
	//공지사항
	public List<Notice> searchNotices(@Param("startDate") String postDate,
			@Param("endDate") String endDate,
            @Param("title") String title,
            @Param("content") String content,
            @Param("offset") int offset,
            @Param("size") int size);
	public int getTotalNoticesCount(@Param("startDate") String postDate,
			@Param("endDate") String endDate,
	        @Param("title") String title,
	        @Param("content") String content);
	public Notice getLatestNotice();
	public List<FileAttachment> getAttachmentsByNoticeSEQ(@Param("seq") int seq);
	public void insertNotice(Notice notice);
    public void insertAttachment(FileAttachment attachment);
    public Notice getNoticeById(Integer seq);
    public void updateNotice(Notice notice);
    public void deleteAttachmentsByNoticeId(Integer seq);
    public List<FileAttachment> getAttachmentsByNoticeId(Integer seq);
    public FileAttachment getAttachmentById(Integer seq);
    public void deleteNotice(Integer seq);
    
    //공통코드 mapper
    public List<CommonCode> searchCommonCodes(@Param("parentCode") String parentCode, 
            @Param("code") String code, 
            @Param("codeName") String codeName, 
            @Param("offset") int offset, 
            @Param("size") int size);
	public int getTotalCommonCodeCount(@Param("parentCode") String parentCode, 
	     @Param("code") String code, 
	     @Param("codeName") String codeName);
	public List<CommonCode> getAllCommonCodes();
	// 필터링된 공통코드 조회
    public List<CommonCode> getFilteredCommonCodes(@Param("parentCode") String parentCode, 
    										@Param("code") String code, 
                                            @Param("codeName") String codeName);
	// 공통코드 삽입
	public void insertCommonCode(CommonCode commonCode);
    public int updateCommonCode(CommonCode commonCode);
    public void deleteCommonCode(String seq);
    public int checkChildCodesExist(String code);
    public List<CommonCode> getParentCommonCodes();
    public List<CommonCode> findSubCodesByParentCode(String parentCode);
    public String searchParentCodeName(String parentCode);
    
    
    // 분류코드 mapper
    public void insertCategoryCode(categoryCode categoryCode);
    public String getParentCode(String parentCode);
    public int updateCategoryCode(categoryCode categoryCode);
    public void deleteCategoryCodes(String code);
    public int checkChildCodesExist2(String code);
    public List<categoryCode> searchCategoryCodes(@Param("parentCode") String parentCode, 
	                                              @Param("code") String code, 
	                                              @Param("codeName") String codeName, 
	                                              @Param("offset") int offset, 
	                                              @Param("size") int size);
    public int getTotalCategoryCodeCount(@Param("parentCode") String parentCode,
    									 @Param("code") String code,
    									 @Param("codeName") String codeName);
    public List<categoryCode> getAllCategoryCodes();
    public List<categoryCode> getFilteredCategoryCodes(@Param("stageType") String stageType, 
                                                	   @Param("code") String code, 
                                                	   @Param("codeName") String codeName);
    public List<categoryCode> getParentCategoryCodes();
    public List<categoryCode> findMiddleCodesByParentCode(String parentCode);
}
