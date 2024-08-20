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
    private String parentCode;  // 상위 대분류 코드 필드 (DB에 저장되지 않음)


    // Getter 및 Setter for StageType
    public String getStageType() {
        return StageType;
    }

    public void setStageType(String StageType) {
        this.StageType = StageType;
    }

    // Getter 및 Setter for Code
    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        this.Code = code;
    }

    // Getter 및 Setter for CodeName
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