package com.tms.backend.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tms.backend.service.AdminService;
import com.tms.backend.service.DefectService;
import com.tms.backend.vo.Defect;
import com.tms.backend.vo.devProgress;

import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@RequestMapping("/tms/*")
public class DefectController {
	
	@Autowired
	private DefectService defectService;
	
	@Autowired
	private AdminService adminService;
	
	
	@GetMapping("/defectStatus")
	public String defectStatusPage(@RequestParam(value = "testCategory", required = false) String testCategory,
            @RequestParam(value = "majorCategory", required = false) String majorCategory,
            @RequestParam(value = "subCategory", required = false) String subCategory,
            @RequestParam(value = "defectSeverity", required = false) String defectSeverity,
            @RequestParam(value = "defectseq", required = false) String defectseq,
            @RequestParam(value = "defectRegistrar", required = false) String defectRegistrar,
            @RequestParam(value = "defectHandler", required = false) String defectHandler,
            @RequestParam(value = "Pl", required = false) String pl,
            @RequestParam(value = "defectStatus", required = false) String defectStatus,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
	                               Model model) {
	    
		// ���� ��� ��ȸ
	    List<Defect> defects = defectService.searchDefects(testCategory, majorCategory, subCategory, defectSeverity, defectseq, defectRegistrar, defectHandler, defectStatus, page, size);

	    // �� ���� �� ��ȸ
	    int totalDefects = defectService.getTotalDefectsCount(testCategory, majorCategory, subCategory, defectSeverity, defectseq, defectRegistrar, defectHandler, defectStatus);
	    
	    // �� ������ �� ���
	    int totalPages = (int) Math.ceil((double) totalDefects / size);

	    // �𵨿� ������ �߰�
	    model.addAttribute("defects", defects);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", totalPages);
	    model.addAttribute("totalDefects", totalDefects);

	    return "defectStatus"; // defectStatus.jsp�� �̵�
	}
	
	
	@GetMapping(value="api/defectStatus" , produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> APIdevProgressPage(HttpServletRequest request,
			@RequestParam(value = "testCategory", required = false) String testCategory,
            @RequestParam(value = "majorCategory", required = false) String majorCategory,
            @RequestParam(value = "subCategory", required = false) String subCategory,
            @RequestParam(value = "defectSeverity", required = false) String defectSeverity,
            @RequestParam(value = "defectseq", required = false) String defectseq,
            @RequestParam(value = "defectRegistrar", required = false) String defectRegistrar,
            @RequestParam(value = "defectHandler", required = false) String defectHandler,
            @RequestParam(value = "Pl", required = false) String pl,
            @RequestParam(value = "defectStatus", required = false) String defectStatus,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
		
//		HttpSession session = request.getSession(false); // ������ ���ٸ� ���� ������ ����
//		if (session == null || session.getAttribute("authorityCode") == null) {
//			// ������ ���ų� authorityCode�� ������ 401 Unauthorized ��ȯ
//			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "������ �����ϴ�. �α����ϼ���."));
//			}
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		log.info(simpleDateFormat.format(new Date()));

		majorCategory = adminService.getStageCodes("��", majorCategory);
		subCategory = adminService.getStageCodes("��", subCategory);
		if (defectSeverity != null && !defectSeverity.isEmpty()) {
			defectSeverity = adminService.getStageCCodes("14", defectSeverity);}
		if (defectStatus != null && !defectStatus.isEmpty()) {
			defectStatus = adminService.getStageCCodes("15", defectStatus);}
		
			// ���� ��� ��ȸ
		    List<Defect> defects = defectService.searchDefects(testCategory, majorCategory, subCategory, defectSeverity, defectseq, defectRegistrar, defectHandler, defectStatus, page, size);

		    // �� ���� �� ��ȸ
		    int totalDefects = defectService.getTotalDefectsCount(testCategory, majorCategory, subCategory, defectSeverity, defectseq, defectRegistrar, defectHandler, defectStatus);
		    
		    // �� ������ �� ���
		    int totalPages = (int) Math.ceil((double) totalDefects / size);
	    
	    // ���� ����
	    Map<String, Object> response = new HashMap<>();
	    response.put("defects", defects);
	    response.put("currentPage", page);
	    response.put("totalPages", totalPages);
	    response.put("totalDefects", totalDefects);

	    return ResponseEntity.ok(response); // JSON���� ���� ��ȯ
	}

}
