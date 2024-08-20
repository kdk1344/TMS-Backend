package com.tms.backend.vo;

import lombok.Data;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class categoryCode {
	private String StageType;
    private String Code; 
    private String CodeName;
    private String parentCode;  // ���� ��з� �ڵ� �ʵ� (DB�� ������� ����)


    // Getter �� Setter for StageType
    public String getStageType() {
        return StageType;
    }

    public void setStageType(String StageType) {
        this.StageType = StageType;
    }

    // Getter �� Setter for Code
    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        this.Code = code;
    }

    // Getter �� Setter for CodeName
    public String getCodeName() {
        return CodeName;
    }

    public void setCodeName(String codeName) {
        this.CodeName = codeName;
    }
    
    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }
}