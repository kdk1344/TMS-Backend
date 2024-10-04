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
}