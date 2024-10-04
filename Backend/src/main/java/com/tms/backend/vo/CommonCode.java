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
}