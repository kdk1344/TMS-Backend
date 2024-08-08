package com.tms.backend.vo;

import lombok.Data;

@Data
public class User {

    private String userID;        // ����� ID
    private String userName;      // ����ڸ�
    private String password;      // �н�����
    private int roleCode;      // ���� �ڵ�
    private String authorityName;      // ���Ѹ�
    
    // Getter �� Setter
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

    public int getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(int roleCode) {
        this.roleCode = roleCode;
    }

    public String getauthorityName() {
        return authorityName;
    }

    public void setauthorityName(String authorityName) {
        this.authorityName = authorityName;
    }
}