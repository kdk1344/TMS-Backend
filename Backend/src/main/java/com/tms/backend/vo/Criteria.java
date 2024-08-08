package com.tms.backend.vo;

import org.springframework.web.util.UriComponentsBuilder;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class Criteria {
    private int page;
    private int perPageNum;
    private String userName; // 사용자명 검색
    private String authorityName; // 권한명 검색

    public Criteria() {
        this.page = 1;
        this.perPageNum = 10;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPerPageNum() {
        return perPageNum;
    }

    public void setPerPageNum(int perPageNum) {
        this.perPageNum = perPageNum;
    }

    public int getPageStart() {
        return (this.page - 1) * perPageNum;
    }
    
    public String getuserName() {
        return userName;
    }

    public void setuserName(String userName) {
        this.userName = userName;
    }
    
    public String getauthorityName() {
        return authorityName;
    }

    public void setauthorityName(String authorityName) {
        this.authorityName = authorityName;
    }
}