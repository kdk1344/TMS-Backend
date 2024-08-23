package com.tms.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tms.backend.service.DefectService;
import com.tms.backend.vo.Defect;

import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@RequestMapping("/tms/*")
public class DefectController {
	
	@Autowired
	private DefectService defectService;
	
	
	@GetMapping("/defectStatus")
	public String defectStatusPage(@RequestParam(value = "testCategory", required = false) String testCategory,
	                               @RequestParam(value = "taskCategory", required = false) String taskCategory,
	                               @RequestParam(value = "taskSubCategory", required = false) String taskSubCategory,
	                               @RequestParam(value = "defectSeverity", required = false) String defectSeverity,
	                               @RequestParam(value = "defectNumber", required = false) String defectNumber,
	                               @RequestParam(value = "defectRegistrar", required = false) String defectRegistrar,
	                               @RequestParam(value = "defectHandler", required = false) String defectHandler,
	                               @RequestParam(value = "defectStatus", required = false) String defectStatus,
	                               @RequestParam(value = "page", defaultValue = "1") int page,
	                               @RequestParam(value = "size", defaultValue = "10") int size,
	                               Model model) {
	    
	    // ���� ��� ��ȸ
	    List<Defect> defects = defectService.searchDefects(testCategory, taskCategory, taskSubCategory, defectSeverity, defectNumber, defectRegistrar, defectHandler, defectStatus, page, size);
	    log.info(defectNumber);
	    
	    
	    // �� ���� �� ��ȸ
	    int totalDefects = defectService.getTotalDefectsCount(testCategory, taskCategory, taskSubCategory, defectSeverity, defectNumber, defectRegistrar, defectHandler, defectStatus);
	    
	    // �� ������ �� ���
	    int totalPages = (int) Math.ceil((double) totalDefects / size);

	    // �𵨿� ������ �߰�
	    model.addAttribute("defects", defects);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", totalPages);
	    model.addAttribute("totalDefects", totalDefects);

	    return "defectStatus"; // defectStatus.jsp�� �̵�
	}

}
