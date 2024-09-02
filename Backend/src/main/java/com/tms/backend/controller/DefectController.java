package com.tms.backend.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tms.backend.service.AdminService;
import com.tms.backend.service.DefectService;
import com.tms.backend.vo.Defect;
import com.tms.backend.vo.User;
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
            @RequestParam(value = "defectSeq", required = false) String defectSeq,
            @RequestParam(value = "defectRegistrar", required = false) String defectRegistrar,
            @RequestParam(value = "defectHandler", required = false) String defectHandler,
            @RequestParam(value = "Pl", required = false) String pl,
            @RequestParam(value = "defectStatus", required = false) String defectStatus,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
	                               Model model) {
	    
		// ���� ��� ��ȸ
	    List<Defect> defects = defectService.searchDefects(testCategory, majorCategory, subCategory, defectSeverity, defectSeq, defectRegistrar, defectHandler, defectStatus, page, size);

	    // �� ���� �� ��ȸ
	    int totalDefects = defectService.getTotalDefectsCount(testCategory, majorCategory, subCategory, defectSeverity, defectSeq, defectRegistrar, defectHandler, defectStatus);
	    
	    // �� ������ �� ���
	    int totalPages = (int) Math.ceil((double) totalDefects / size);

	    // �𵨿� ������ �߰�
	    model.addAttribute("defects", defects);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", totalPages);
	    model.addAttribute("totalDefects", totalDefects);

	    return "defectStatus"; // defectStatus.jsp�� �̵�
	}
	
	
	@GetMapping(value="api/defect" , produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> APIdefectPage(HttpServletRequest request,
			@RequestParam(value = "testCategory", required = false) String testCategory,
            @RequestParam(value = "majorCategory", required = false) String majorCategory,
            @RequestParam(value = "subCategory", required = false) String subCategory,
            @RequestParam(value = "defectSeverity", required = false) String defectSeverity,
            @RequestParam(value = "defectSeq", required = false) String defectSeq,
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
		    List<Defect> defects = defectService.searchDefects(testCategory, majorCategory, subCategory, defectSeverity, defectSeq, defectRegistrar, defectHandler, defectStatus, page, size);

		    // �� ���� �� ��ȸ
		    int totalDefects = defectService.getTotalDefectsCount(testCategory, majorCategory, subCategory, defectSeverity, defectSeq, defectRegistrar, defectHandler, defectStatus);
		    
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
	
	//���� ���� Ȯ��
	@GetMapping(value = "api/defectType", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDefectType() {
    	Map<String, Object> response = new HashMap<>();
    	List<User> defectType= adminService.findAuthorityCode(13);

        // ���� ������ ����
        if (defectType != null && !defectType.isEmpty()) {
            response.put("status", "success");
            response.put("defectType", defectType);
        } else {
            response.put("status", "failure");
            response.put("message", "���� ���� ������ ã�� �� �����ϴ�");
        }

        // ��ȸ�� ����� ��ȯ
        return ResponseEntity.ok(response);
    }
		
	//���� �ɰ��� Ȯ��
	@GetMapping(value = "api/defectSeverity", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getdefectSeverity() {
    	Map<String, Object> response = new HashMap<>();
    	List<User> defectSeverity= adminService.findAuthorityCode(14);

        // ���� ������ ����
        if (defectSeverity != null && !defectSeverity.isEmpty()) {
            response.put("status", "success");
            response.put("defectSeverity", defectSeverity);
        } else {
            response.put("status", "failure");
            response.put("message", "���� �ɰ��� ������ ã�� �� �����ϴ�");
        }

        // ��ȸ�� ����� ��ȯ
        return ResponseEntity.ok(response);
    }
		
	//���� ó�� ���� Ȯ��
	@GetMapping(value = "api/defectStatus", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getdefectStatus() {
    	Map<String, Object> response = new HashMap<>();
    	List<User> defectStatus= adminService.findAuthorityCode(15);

        // ���� ������ ����
        if (defectStatus != null && !defectStatus.isEmpty()) {
            response.put("status", "success");
            response.put("defectStatus", defectStatus);
        } else {
            response.put("status", "failure");
            response.put("message", "���� ó�� ���� ������ ã�� �� �����ϴ�");
        }

        // ��ȸ�� ����� ��ȯ
        return ResponseEntity.ok(response);
    }
		
		
		
	//���� ��Ȳ ����
	@DeleteMapping(value= "api/deleteDefect", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteDefect(@RequestBody List<Integer> defectSeqs) {
        Map<String, Object> response = new HashMap<>();
        
        for (int seq : defectSeqs) {
            defectService.deleteDefect(seq);
        }
        
        response.put("status", "success");
        response.put("message", "������ ���������� �����Ǿ����ϴ�.");
        return ResponseEntity.ok(response);
    }
	
	// ��ü ���� ���� ��Ȳ ������ ������ �ٿ�ε�
    @GetMapping("/defectdownloadAll")
    public void downloadAlldefect(HttpServletResponse response) throws IOException {
    	List<Defect> defects = defectService.searchDefects(null, null, null, null, null, null, null, null, 1, 15);;
        log.info(defects);
        defectexportToExcel(response, defects, "all_defects_codes.xlsx");
    }
    
    // �׼� ���� ���ø� �ٿ�ε�
    @GetMapping("/defectsexampleexcel")
    public void downloadExdefects(HttpServletResponse response) throws IOException {
    	List<Defect> defects = defectService.searchDefects("no_value", null, null, null, null, null, null, null, 1, 15);;    	
    	defectexportToExcel(response, defects, "example.xlsx");
    }
    
    // ��ȸ�� ���� ���� ��Ȳ ������ ������ �ٿ�ε�
    @GetMapping("/defectdownloadFiltered")
    public void downloadFiltereddefects(
    		@RequestParam(value = "testCategory", required = false) String testCategory,
            @RequestParam(value = "majorCategory", required = false) String majorCategory,
            @RequestParam(value = "subCategory", required = false) String subCategory,
            @RequestParam(value = "defectSeverity", required = false) String defectSeverity,
            @RequestParam(value = "defectSeq", required = false) String defectSeq,
            @RequestParam(value = "defectRegistrar", required = false) String defectRegistrar,
            @RequestParam(value = "defectHandler", required = false) String defectHandler,
            @RequestParam(value = "Pl", required = false) String pl,
            @RequestParam(value = "defectStatus", required = false) String defectStatus,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            HttpServletResponse response) throws IOException {
    	
    	majorCategory = adminService.getStageCodes("��", majorCategory);
		subCategory = adminService.getStageCodes("��", subCategory);
		if (defectSeverity != null && !defectSeverity.isEmpty()) {
			defectSeverity = adminService.getStageCCodes("14", defectSeverity);}
		if (defectStatus != null && !defectStatus.isEmpty()) {
			defectStatus = adminService.getStageCCodes("15", defectStatus);}
		
		// ���� ��� ��ȸ
	    List<Defect> defects = defectService.searchDefects(testCategory, majorCategory, subCategory, defectSeverity, defectSeq, defectRegistrar, defectHandler, defectStatus, page, size);


    	log.info("Ȯ����"+defects);
    	defectexportToExcel(response, defects, "filtered_defects_codes.xlsx");
    }
    
 // ���� ���Ϸ� �����͸� �������� �޼���
    private void defectexportToExcel(HttpServletResponse response, List<Defect> Defect, String fileName) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Defect");
        String check = "";
        log.info("üũ��");
        if (Defect.isEmpty()) {
    		check = "no_value";
    		log.info(check);
    		}
        // ��� �̸� �迭
        String[] headers = {
            "SEQ","MAJOR_CATEGORY", "SUB_CATEGORY", "MINOR_CATEGORY", 
            "PROGRAM_DETAIL_TYPE", "PROGRAM_TYPE", "PROGRAM_ID", "PROGRAM_NAME", 
            "CLASS_NAME", "SCREEN_ID", "SCREEN_NAME", "SCREEN_MENU_PATH", 
            "PRIORITY", "DIFFICULTY", "ESTIMATED_EFFORT", "PROGRAM_STATUS", 
            "REQ_ID", "DELETION_HANDLER", "DELETION_DATE", "DELETION_REASON", 
            "DEVELOPER", "PLANNED_START_DATE", "PLANNED_END_DATE", 
            "ACTUAL_START_DATE", "ACTUAL_END_DATE", "DEV_TEST_END_DATE", 
            "PL", "PL_TEST_SCD_DATE", "PL_TEST_CMP_DATE", 
            "PL_TEST_RESULT", "PL_TEST_NOTES", "IT_MGR", 
            "IT_TEST_DATE", "IT_CONFIRM_DATE", "IT_TEST_RESULT", 
            "IT_TEST_NOTES", "BUSI_MGR", "BUSI_TEST_DATE", 
            "BUSI_CONFIRM_DATE", "BUSI_TEST_RESULT", "BUSI_TEST_NOTES", 
            "THIRD_PARTY_TEST_MGR", "THIRD_PARTY_TEST_DATE", 
            "THIRD_PARTY_CONFIRM_DATE", "THIRD_TEST_RESULT", 
            "THIRD_PARTY_TEST_NOTES", "DEV_STATUS"
        };

        // ��� ����
        Row headerRow = sheet.createRow(0);
        if (!check.equals("")) {
        	// "SEQ"�� �����ϰ� ���� ������� ����
        	log.info("check"+check);
            for (int i = 1; i < headers.length; i++) {
                headerRow.createCell(i - 1).setCellValue(headers[i]);
            }
            headerRow.createCell(46).setCellValue("INIT_REGISTRAR");
            headerRow.createCell(47).setCellValue("LAST_MODIFIER");
        }
        else {
        	// "SEQ"�� �����Ͽ� ��� ��� ����
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }
	        headerRow.createCell(47).setCellValue("INIT_REG_DATE");
	        headerRow.createCell(48).setCellValue("INIT_REGISTRAR");
	        headerRow.createCell(49).setCellValue("LAST_MODIFIED_DATE");
	        headerRow.createCell(50).setCellValue("LAST_MODIFIER");
        }
        
        // ����� ������ ����
        String[] fields = {
            "getSeq","getMajorCategory", "getSubCategory", "getMinorCategory",
            "getProgramDetailType", "getProgramType", "getProgramId", "getProgramName",
            "getClassName", "getScreenId", "getScreenName", "getScreenMenuPath",
            "getPriority", "getDifficulty", "getEstimatedEffort", "getProgramStatus",
            "getReqId", "getDeletionHandler", "getDeletionDate", "getDeletionReason",
            "getDeveloper", "getPlannedStartDate", "getPlannedEndDate",
            "getActualStartDate", "getActualEndDate", "getDevtestendDate",
            "getPl", "getPlTestScdDate", "getPlTestCmpDate", 
            "getPlTestResult", "getPlTestNotes", "getItMgr", 
            "getItTestDate", "getItConfirmDate", "getItTestResult", 
            "getItTestNotes", "getBusiMgr", "getBusiTestDate", 
            "getBusiConfirmDate", "getBusiTestResult", "getBusiTestNotes", 
            "getThirdPartyTestMgr", "getThirdPartyTestDate", 
            "getThirdPartyConfirmDate", "getThirdTestResult", 
            "getThirdPartyTestNotes", "getDevStatus"
        };

        // ������ �� ����
        int rowNum = 1;
        for (devProgress devProgress : devProgressList) {
            Row row = sheet.createRow(rowNum++);
            for (int i = 0; i < fields.length; i++) {
                try {
                    Method method = devProgress.getClass().getMethod(fields[i]);
                    Object value = method.invoke(devProgress);
                    row.createCell(i).setCellValue(value != null ? value.toString() : "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (check != "") {
	            row.createCell(46).setCellValue(devProgress.getInitRegistrar());
	            row.createCell(47).setCellValue(devProgress.getLastModifier());
            }
            else {
            	row.createCell(47).setCellValue(devProgress.getInitRegDate().toString());
	            row.createCell(48).setCellValue(devProgress.getInitRegistrar());
	            row.createCell(49).setCellValue(devProgress.getLastModifiedDate().toString());
	            row.createCell(50).setCellValue(devProgress.getLastModifier());
            }
        }
        // ���� ������ HTTP �������� ��������
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        try (OutputStream os = response.getOutputStream()) {
            workbook.write(os);
        }
        workbook.close();
    }
		

}
