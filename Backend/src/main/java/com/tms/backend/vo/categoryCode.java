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
}