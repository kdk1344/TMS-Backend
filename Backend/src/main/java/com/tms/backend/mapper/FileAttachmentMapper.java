package com.tms.backend.mapper;


import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.tms.backend.vo.FileAttachment;

@Mapper
public interface FileAttachmentMapper {
	public List<FileAttachment> getAttachmentsByNoticeId(Long seq); // seq에 따른 첨부파일 가져오기
    public void insertFileAttachment(FileAttachment fileAttachment); // 첨부파일 입력
    public void deleteAttachmentsByNoticeId(Long seq); // 첨부파일 삭제

}
