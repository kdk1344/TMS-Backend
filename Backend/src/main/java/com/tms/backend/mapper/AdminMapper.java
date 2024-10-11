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
	
	// �����
    public void insert(User user); // ����� ������ �Է�
    public int updateUser(User user); // ����� ������ ����
    void deleteUser(String[] ids); // ����� ������ ����
    public List<User> findAll(Criteria criteria); // ����� ������ ���� ����
	public int countUsers(Criteria criteria); // ����� ������ ����
	public List<User> findAuthorityCode(int authorityCode); // ���� �ڵ忡 ���� ����� ������ ����
	public List<User> getUserList(); // ����� ������ ����
//	public Integer getTesterNumber(String codeName); // �����, ��, ��Ÿ ����� ���� �ҷ�����
	
	//��������
	public List<Notice> searchNotices(@Param("startDate") String postDate,
			@Param("endDate") String endDate,
            @Param("title") String title,
            @Param("content") String content,
            @Param("offset") int offset,
            @Param("size") int size); // �������� ��ȸ
	public int getTotalNoticesCount(@Param("startDate") String postDate,
			@Param("endDate") String endDate,
	        @Param("title") String title,
	        @Param("content") String content); // �������� �� ����
	public Notice getLatestNotice(); // �ֱ� �������� ��ȸ
	public List<FileAttachment> getAttachmentsByNoticeSEQ(@Param("seq") int seq); // seq�� ���� ÷������ ����
	public void insertNotice(Notice notice); // �������� �Է�
    public void insertAttachment(FileAttachment attachment); // ÷������ �Է�
    public Notice getNoticeById(Integer seq); // seq�� ���� �������� ����
    public void updateNotice(Notice notice); // �������� ����
    public void deleteAttachmentsByNoticeId(@Param("seq") int seq,
    										@Param("type") int type); // �Խ��ǿ� ���� ÷������ ����
    public List<FileAttachment> getAttachments(@Param("seq") int seq,
												@Param("type") int type); // �Խ��ǿ� ���� ÷������ ����
    public FileAttachment getAttachmentById(Integer seq); // seq�� ���� ÷������ ����
    public void deleteNotice(Integer seq); // �������� ����
    
    //�����ڵ� mapper
    public List<CommonCode> searchCommonCodes(@Param("parentCode") String parentCode, 
            @Param("code") String code, 
            @Param("codeName") String codeName, 
            @Param("offset") int offset, 
            @Param("size") int size); //�����ڵ� ��ȸ
	public int getTotalCommonCodeCount(@Param("parentCode") String parentCode, 
	     @Param("code") String code, 
	     @Param("codeName") String codeName); //�����ڵ� �� ����
	public List<CommonCode> getAllCommonCodes(); // ��� �����ڵ� ����
    public List<CommonCode> getFilteredCommonCodes(@Param("parentCode") String parentCode, 
    										@Param("code") String code, 
                                            @Param("codeName") String codeName); // ���͸��� �����ڵ� ��ȸ
	public void insertCommonCode(CommonCode commonCode); // �����ڵ� ����
    public int updateCommonCode(CommonCode commonCode); // �����ڵ� ����
    public void deleteCommonCode(String seq); // �����ڵ� ����
    public int checkChildCodesExist(String code); // ���� �ڵ忡 ���� ���� �ڵ� ���� Ȯ��
    public List<CommonCode> getParentCommonCodes(); // ��з� �ڵ� �����
    public List<CommonCode> findSubCodesByParentCode(String parentCode); // ���� �ڵ忡 ���� ���� �ڵ� ����� ��ȸ�ϴ� ����
    public String searchParentCodeName(String parentCode); // Ư�� �ڵ��� �����ڵ� �̸� ��ȸ
    public String getStageCCodes(@Param("parentCode") String parentCode, 
    							@Param("code") String code); // ���� �ڵ忡 ���� ���� �ڵ� �̸� ��ȸ
    public int countdupliCCode(CommonCode commonCode); //�ߺ� �����ڵ� Ȯ��
    
    
    // �з��ڵ� mapper
    public void insertCategoryCode(categoryCode categoryCode); // �з��ڵ� �Է�
    public String getParentCode(String parentCode); // ���� ��з� �ڵ� ��������
    public int updateCategoryCode(categoryCode categoryCode); // �з��ڵ� ����
    public void deleteCategoryCodes(String code); // �з��ڵ� ����
    public int checkChildCodesExist2(String code); // ���� �ڵ忡 ���� ���� �з� �ڵ� ���� Ȯ��
    public List<categoryCode> searchCategoryCodes(@Param("parentCode") String parentCode, 
	                                              @Param("code") String code, 
	                                              @Param("codeName") String codeName, 
	                                              @Param("offset") int offset, 
	                                              @Param("size") int size); //  �з��ڵ� ��ȸ
    public int getTotalCategoryCodeCount(@Param("parentCode") String parentCode,
    									 @Param("code") String code,
    									 @Param("codeName") String codeName); // �з��ڵ� �� ����
    public List<categoryCode> getAllCategoryCodes(); // ��� �з��ڵ� ������ ��������
    public List<categoryCode> getFilteredCategoryCodes(@Param("stageType") String stageType, 
                                                	   @Param("code") String code, 
                                                	   @Param("codeName") String codeName); // ���͸��� �����ڵ� ������ ��������
    public List<categoryCode> getParentCategoryCodes(); // ��з� �ڵ常 ��������
    public List<categoryCode> findMiddleCodesByParentCode(String parentCode); //���� �ڵ忡 ���� ���� �з� �ڵ� ����� ��ȸ�ϴ� ����
    public List<categoryCode> getsubCategoryCodes(); //�ߺз� �ڵ常 ��������
    public String getStageCodes(@Param("stageType") String stageType, 
     	   						@Param("code") String code); // Ư�� �ڵ� �̸� ��������
    public int countdupliCtCode(categoryCode categoryCode); // �ߺ� �з��ڵ� Ȯ��
}
