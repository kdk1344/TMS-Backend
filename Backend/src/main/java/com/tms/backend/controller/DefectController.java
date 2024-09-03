package com.tms.backend.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private void defectexportToExcel(HttpServletResponse response, List<Defect> Defects, String fileName) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Defect");
        String check = "";
        log.info("üũ��");
        if (Defects.isEmpty()) {
    		check = "no_value";
    		log.info(check);
    		}
        // ��� �̸� �迭
        String[] headers = {
        	    "DEFECT_SEQ", "TEST_STAGE", "MAJOR_CATEGORY", "SUB_CATEGORY", "TEST_ID", 
        	    "PROGRAM_TYPE", "PROGRAM_ID", "PROGRAM_NAME", "DEFECT_TYPE", 
        	    "DEFECT_SEVERITY", "DEFECT_DESCRIPTION", "DEFECT_REGISTRAR", "DEFECT_REG_DATE", 
        	    "DEFECT_HANDLER", "DEFECT_SCHEDULED_DATE", "DEFECT_COMPLETION_DATE", 
        	    "DEFECT_RESOLUTION_DETAILS", "PL", "PL_CONFIRM_DATE", "ORIGINAL_DEFECT_NUMBER", 
        	    "PL_DEFECT_JUDGE_CLASS", "PL_COMMENTS", "DEFECT_REG_CONFIRM_DATE", 
        	    "DEFECT_REGISTRAR_COMMENT", "DEFECT_STATUS", "INIT_CREATED_DATE", 
        	    "INIT_CREATER", "LAST_MODIFIED_DATE", "LAST_MODIFIER"
        	};

        // ��� ����
        Row headerRow = sheet.createRow(0);
        if (!check.equals("")) {
        	// "defect_SEQ"�� �����ϰ� ���� ������� ����
        	log.info("check"+check);
            for (int i = 1; i < headers.length; i++) {
                headerRow.createCell(i - 1).setCellValue(headers[i]);
            }
            headerRow.createCell(24).setCellValue("INIT_CREATER");
            headerRow.createCell(25).setCellValue("LAST_MODIFIER");
        }
        else {
        	// "defect_SEQ"�� �����Ͽ� ��� ��� ����
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }
	        headerRow.createCell(25).setCellValue("INIT_CREATED_DATE");
	        headerRow.createCell(26).setCellValue("INIT_CREATER");
	        headerRow.createCell(27).setCellValue("LAST_MODIFIED_DATE");
	        headerRow.createCell(28).setCellValue("LAST_MODIFIER");
        }
        
        // ����� ������ ����
        String[] fields = {
        	    "getDefectSeq", "getTestStage", "getMajorCategory", "getSubCategory", 
        	    "getTestId", "getProgramType", "getProgramId", "getProgramName", 
        	    "getDefectType", "getDefectSeverity", "getDefectDescription", 
        	    "getDefectRegistrar", "getDefectRegDate", "getDefectHandler", 
        	    "getDefectScheduledDate", "getDefectCompletionDate", 
        	    "getDefectResolutionDetails", "getPl", "getPlConfirmDate", 
        	    "getOriginalDefectNumber", "getPlDefectJudgeClass", "getPlComments", 
        	    "getDefectRegConfirmDate", "getDefectRegistrarComment", 
        	    "getDefectStatus", "getInitCreatedDate", "getInitCreater", 
        	    "getLastModifiedDate", "getLastModifier"
        	};

        // ������ �� ����
        int rowNum = 1;
        for (Defect Defect : Defects) {
            Row row = sheet.createRow(rowNum++);
            for (int i = 0; i < fields.length; i++) {
                try {
                    Method method = Defect.getClass().getMethod(fields[i]);
                    Object value = method.invoke(Defect);
                    row.createCell(i).setCellValue(value != null ? value.toString() : "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
		
    
    @PostMapping(value = "api/defectupload", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> defectuploadExcelFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        Map<String, Object> response = new HashMap<>();
        if (file.isEmpty()) {
            response.put("status", "failure");
            response.put("message", "������ �����ϴ�. �ٽ� �õ��� �ּ���.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            // �����ϴ� �÷��� ����Ʈ
            List<String> expectedHeaders = Arrays.asList("DEFECT_SEQ", "TEST_STAGE", "MAJOR_CATEGORY", "SUB_CATEGORY", "TEST_ID", 
            	    "PROGRAM_TYPE", "PROGRAM_ID", "PROGRAM_NAME", "DEFECT_TYPE", 
            	    "DEFECT_SEVERITY", "DEFECT_DESCRIPTION", "DEFECT_REGISTRAR", "DEFECT_REG_DATE", 
            	    "DEFECT_HANDLER", "DEFECT_SCHEDULED_DATE", "DEFECT_COMPLETION_DATE", 
            	    "DEFECT_RESOLUTION_DETAILS", "PL", "PL_CONFIRM_DATE", "ORIGINAL_DEFECT_NUMBER", 
            	    "PL_DEFECT_JUDGE_CLASS", "PL_COMMENTS", "DEFECT_REG_CONFIRM_DATE", 
            	    "DEFECT_REGISTRAR_COMMENT", "DEFECT_STATUS", "INIT_CREATED_DATE", 
            	    "INIT_CREATER", "LAST_MODIFIED_DATE", "LAST_MODIFIER");
            if (!isHeaderValid5(headerRow, expectedHeaders)) {
                response.put("status", "error");
                response.put("message", "����� �÷����� �ùٸ��� �ʽ��ϴ�.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            List<Defect> defect = new ArrayList<>();
            
            // �ʵ�� �迭�� �����Ǵ� �� Ÿ�� �迭
            String[] fieldNames = {
            	    "defectSeq", "testStage", "majorCategory", "subCategory", 
            	    "testId", "programType", "programId", "programName", 
            	    "defectType", "defectSeverity", "defectDescription", 
            	    "defectRegistrar", "defectRegDate", "defectHandler", 
            	    "defectScheduledDate", "defectCompletionDate", 
            	    "defectResolutionDetails", "pl", "plConfirmDate", 
            	    "originalDefectNumber", "plDefectJudgeClass", "plComments", 
            	    "defectRegConfirmDate", "defectRegistrarComment", 
            	    "defectStatus", "initCreatedDate", "initCreater", 
            	    "lastModifiedDate", "lastModifier"
            	};

            // ù ��° ���� ����̹Ƿ� �ǳʶݴϴ�.
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Defect deFect = new Defect();

                    try {
                    	for (int j = 0; j < fieldNames.length; j++) {
                            Field field = Defect.class.getDeclaredField(fieldNames[j]);
                            field.setAccessible(true);

                            switch (field.getType().getSimpleName()) {
                                case "int":
                                    field.set(deFect, (int) getCellValueAsNumeric2(row.getCell(j)));
                                    break;
                                case "Date":
                                    field.set(deFect, getCellValueAsDate2(row.getCell(j)));
                                    break;
                                default:
                                    field.set(deFect, getCellValueAsString2(row.getCell(j)));
                                    break;
                            }
                        }
                    	defect.add(deFect);
                    } catch (Exception e) {
                        e.printStackTrace();
                        response.put("status", "error");
                        response.put("message", "�ùٸ��� ���� ���� �ԷµǾ����ϴ�.");
                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }
                }
            }

            // �����ͺ��̽��� ����
            try {
                defectService.saveAllDefect(defect);
                response.put("status", "success");
                response.put("message", "���� ���ε尡 ���������� �Ϸ�Ǿ����ϴ�!");
                response.put("totalUploaded", defect.size());
            } catch (Exception e) {
                e.printStackTrace();
                response.put("status", "error");
                response.put("message", "�ܷ� Ű ���� ������ �����߽��ϴ�. �����Ͱ� �ùٸ��� �ʽ��ϴ�.");
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }

        } catch (DuplicateKeyException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "�ߺ��� ���� ������ �߰ߵǾ����ϴ�.");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        } catch (IOException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "���� ó�� �� ������ �߻��߽��ϴ�. �ٽ� �õ��� �ּ���.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    
    // �׼� ��ȿ��� Ȯ��
    private boolean isHeaderValid5(Row headerRow, List<String> expectedHeaders) {
        for (int i = 0; i < expectedHeaders.size(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell == null || !cell.getStringCellValue().trim().equalsIgnoreCase(expectedHeaders.get(i))) {
                return false;
            }
        }
        return true;
    }
    
    // �׼� Cell�� String ��ȯ 
    private String getCellValueAsString2(Cell cell) {
        if (cell == null) {
            return null;
        }
        return cell.getCellType() == CellType.STRING ? cell.getStringCellValue() : cell.toString();
    }
    
    // �׼� Cell�� Num ��ȯ
    private double getCellValueAsNumeric2(Cell cell) {
        if (cell == null) {
            return 0;
        }
        return cell.getCellType() == CellType.NUMERIC ? cell.getNumericCellValue() : Double.parseDouble(cell.toString());
    }
    
    // �׼� Cell�� Date ��ȯ
    private Date getCellValueAsDate2(Cell cell) {
        if (cell == null) {
            return null;
        }

        if (cell.getCellType() == CellType.NUMERIC) {
            // ��¥�� ���� ���·� ����Ǿ� ���� ��, ��¥�� ��ȯ
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue();
            } else {
                return null;
            }
        } else if (cell.getCellType() == CellType.STRING) {
            // ���ڿ��� �Ǿ� �ִ� ���, "YYYY-MM-DD" ���� ���� ó��
            String dateStr = cell.getStringCellValue();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
				return dateFormat.parse(dateStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
        }

        return null; // �ٸ� ������ ���� null ��ȯ
    }
}
