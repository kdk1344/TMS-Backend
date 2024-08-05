package com.tms.backend.vo;

import lombok.Data;

@Data
public class User {

    private String userID;        // 사용자 ID
    private String username;      // 사용자명
    private String password;      // 패스워드
    private int roleCode;      // 권한 코드
    private String roleName;      // 권한명
    
    // Getter 및 Setter
    public String getuserID() {
        return userID;
    }

    public void setuserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(int roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}