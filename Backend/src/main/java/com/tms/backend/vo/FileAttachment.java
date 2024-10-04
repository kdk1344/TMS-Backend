package com.tms.backend.vo;

import java.util.Date;

import lombok.Data;

@Data
public class FileAttachment {
	
	private Integer type; // 파일의 저장 게시판
    private Integer identifier; // 파일의 고유 식별자
    private Integer seq; // 공지사항의 SEQ와 연관
    private String storageLocation; // 파일 저장 경로
    private String fileName; // 파일명
    private Date createdDate; // 파일 생성일시

}
