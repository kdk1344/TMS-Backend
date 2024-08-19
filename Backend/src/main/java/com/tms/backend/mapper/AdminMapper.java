package com.tms.backend.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.tms.backend.vo.Criteria;
import com.tms.backend.vo.FileAttachment;
import com.tms.backend.vo.Notice;
import com.tms.backend.vo.User;

import lombok.extern.log4j.Log4j;

@Mapper
public interface AdminMapper {

    public void insert(User user);
    public void insertUser(User user);
    public int updateUser(User user);
    void deleteUser(String[] ids);
    public List<User> findAll(Criteria criteria);
	public int countUsers(Criteria criteria);
	
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
	public List<FileAttachment> getAttachmentsByNoticeSEQ(@Param("seq") int seq);
	public void insertNotice(Notice notice);
    public void insertAttachment(FileAttachment attachment);
    public Notice getNoticeById(Integer seq);
    public void updateNotice(Notice notice);
    public void deleteAttachmentsByNoticeId(Integer seq);
    public List<FileAttachment> getAttachmentsByNoticeId(Integer seq);
    public FileAttachment getAttachmentById(Integer seq);
    public void deleteNotice(Integer seq);
}
