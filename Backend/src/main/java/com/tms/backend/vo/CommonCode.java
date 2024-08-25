package com.tms.backend.vo;

import lombok.Data;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CommonCode {
	private String ParentCode;
    private String Code; 
    private String CodeName; 
    private String parentCodeName;
    private String seq;


    // Getter นื Setter for ParentCode
    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }
    
    public String getParentCode() {
        return ParentCode;
    }

    public void setParentCode(String parentCode) {
        this.ParentCode = parentCode;
    }
    
    public String getparentCodeName() {
        return parentCodeName;
    }

    public void setParentCodeName(String parentCodeName) {
        this.parentCodeName = parentCodeName;
    }

    // Getter นื Setter for Code
    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
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