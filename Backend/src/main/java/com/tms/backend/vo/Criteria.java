package com.tms.backend.vo;

import org.springframework.web.util.UriComponentsBuilder;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class Criteria {
    private int page;
    private int perPageNum;
    private String userName; // ����ڸ� �˻�
    private String authorityName; // ���Ѹ� �˻�

    public Criteria() { // ����� ������ �� ������ ����
        this.page = 1;
        this.perPageNum = 10;
    }
    
    // ����Ͻ� ����: ������ ���� �ε����� ����ϴ� �޼���� ����
    public int getPageStart() {
        return (this.page - 1) * perPageNum;
    }
}