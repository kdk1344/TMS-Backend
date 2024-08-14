package com.tms.backend.vo;

import java.util.Date;

public class FileAttachment {
	
	private String type; // 파일의 구분 (예: 이미지, 문서)
    private Integer identifier; // 파일의 고유 식별자
    private Integer seq; // 공지사항의 SEQ와 연관
    private String storageLocation; // 파일 저장 경로
    private String fileName; // 파일명
    private Date createdDate; // 파일 생성일시

    // Getters and Setters

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Integer identifier) {
        this.identifier = identifier;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

}
