package com.tms.backend.vo;

import lombok.Data;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class Notice {
	private Integer seq; // ���������� ���� �ĺ���
    private Date postDate; // �Խ�����
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title; // �������� ����
    @NotBlank(message = "Title is required")
    @Size(max = 10000, message = "Title must not exceed 10000 characters")
    private String content; // �������� ����
    private Date createdDate; // ���� �����Ͻ�
    private Date lastModifiedDate; // ���� �����Ͻ�
    private List<FileAttachment> attachments; // ÷������ ���
}