package com.tms.backend.vo;

import lombok.Data;

@Data
public class User {

    private String userID;        // ����� ID
    private String userName;      // ����ڸ�
    private String password;      // �н�����
    private int authorityCode;      // ���� �ڵ�
    private String authorityName;      // ���Ѹ�
}