package com.tms.backend.vo;

import lombok.Data;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CommonCode {
	private Character ParentCode;
    private Character Code; 
    private String CodeName; 


    // Getter นื Setter for ParentCode
    public Character getParentCode() {
        return ParentCode;
    }

    public void setParentCode(Character parentCode) {
        this.ParentCode = parentCode;
    }

    // Getter นื Setter for Code
    public Character getCode() {
        return Code;
    }

    public void setCode(Character code) {
        this.Code = code;
    }

    // Getter นื Setter for CodeName
    public String getCodeName() {
        return CodeName;
    }

    public void setCodeName(String codeName) {
        this.CodeName = codeName;
    }
}