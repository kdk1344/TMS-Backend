package com.tms.backend.vo;

import lombok.Data;

@Data
public class User {

    private String userID;        // 사용자 ID
    private String userName;      // 사용자명
    private String password;      // 패스워드
    private int authorityCode;      // 권한 코드
    private String authorityName;      // 권한명
}