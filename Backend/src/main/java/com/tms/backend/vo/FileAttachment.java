package com.tms.backend.vo;

import java.util.Date;

public class FileAttachment {
	
	private Integer type; // ������ ���� �Խ���
    private Integer identifier; // ������ ���� �ĺ���
    private Integer seq; // ���������� SEQ�� ����
    private String storageLocation; // ���� ���� ���
    private String fileName; // ���ϸ�
    private Date createdDate; // ���� �����Ͻ�

    // Getters and Setters

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
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
