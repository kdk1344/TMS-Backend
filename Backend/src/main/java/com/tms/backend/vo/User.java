package com.tms.backend.vo;

import lombok.Data;

@Data
public class User {

    private String userID;        // ����� ID
    private String username;      // ����ڸ�
    private String password;      // �н�����
    private int roleCode;      // ���� �ڵ�
    private String roleName;      // ���Ѹ�
    
    // Getter �� Setter
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