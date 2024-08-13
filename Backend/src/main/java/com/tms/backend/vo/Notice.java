package com.tms.backend.vo;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class Notice {
	private int seq; // 공지사항의 고유 식별자
    private Date postDate; // 게시일자
    private String title; // 공지사항 제목
    private String content; // 공지사항 내용
    private Date createdDate; // 최초 생성일시
    private Date lastModifiedDate; // 최종 변경일시
    private List<FileAttachment> attachments; // 첨부파일 목록

    // Getters and Setters

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public List<FileAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<FileAttachment> attachments) {
        this.attachments = attachments;
    }
}