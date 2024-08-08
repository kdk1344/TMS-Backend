package com.tms.backend.vo;

import lombok.Data;

@Data
public class User {

    private String userID;        // 사용자 ID
    private String userName;      // 사용자명
    private String password;      // 패스워드
    private int authorityCode;      // 권한 코드
    private String authorityName;      // 권한명
    
    // Getter 및 Setter
    public String getuserID() {
        return userID;
    }

    public void setuserID(String userID) {
        this.userID = userID;
    }

    public String getuserName() {
        return userName;
    }

    public void setuserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getauthorityCode() {
        return authorityCode;
    }

    public void setauthorityCode(int authorityCode) {
        this.authorityCode = authorityCode;
    }

    public String getauthorityName() {
        return authorityName;
    }

    public void setauthorityName(String authorityName) {
        this.authorityName = authorityName;
    }
}