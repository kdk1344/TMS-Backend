package com.tms.backend.mapper;


import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.tms.backend.vo.FileAttachment;

@Mapper
public interface FileAttachmentMapper {
	public List<FileAttachment> getAttachmentsByNoticeId(Long seq); // seq�� ���� ÷������ ��������
    public void insertFileAttachment(FileAttachment fileAttachment); // ÷������ �Է�
    public void deleteAttachmentsByNoticeId(Long seq); // ÷������ ����

}
