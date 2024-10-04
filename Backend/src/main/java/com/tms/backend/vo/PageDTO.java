package com.tms.backend.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PageDTO {
    private int total; // ������ �� ����
    private Criteria criteria; // ������ ������ ���� Criteria

    public PageDTO(int total, Criteria criteria) { // ������ ����
        this.total = total;
        this.criteria = criteria;
    }
}