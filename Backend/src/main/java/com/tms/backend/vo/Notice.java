package com.tms.backend.vo;

import lombok.Data;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class Notice {
	private Integer seq; // 공지사항의 고유 식별자
    private Date postDate; // 게시일자
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title; // 공지사항 제목
    @NotBlank(message = "Title is required")
    @Size(max = 10000, message = "Title must not exceed 10000 characters")
    private String content; // 공지사항 내용
    private Date createdDate; // 최초 생성일시
    private Date lastModifiedDate; // 최종 변경일시
    private List<FileAttachment> attachments; // 첨부파일 목록
}