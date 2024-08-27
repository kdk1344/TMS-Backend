package com.tms.backend.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
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
import com.tms.backend.service.UserService;
import com.tms.backend.vo.CommonCode;
import com.tms.backend.vo.Criteria;
import com.tms.backend.vo.Notice;
import com.tms.backend.vo.PageDTO;
import com.tms.backend.vo.User;
import com.tms.backend.vo.categoryCode;

import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@RequestMapping("/tms/*")
public class CodeController {
	
	@Autowired
    private AdminService adminService;
	
	@Autowired
	private UserService userService;
	
	//�����ڵ� Controller
	
	@GetMapping("/commonCode")
    public String commonCodePage(@RequestParam(value = "parentCode", required = false) String parentCode,
	          @RequestParam(value = "code", required = false) String code,
	          @RequestParam(value = "codeName", required = false) String codeName,
	          @RequestParam(value = "page", defaultValue = "1") int page,
	          @RequestParam(value = "size", defaultValue = "10") int size,
	          Model model) {
			// CommonCode ��ȸ

			List<CommonCode> commonCodes = adminService.searchCommonCodes(parentCode, code, codeName, page, size);
			int totalCommonCodes = adminService.getTotalCommonCodeCount(parentCode, code, codeName);
			int totalPages = (int) Math.ceil((double) totalCommonCodes / size);
			
			// ���� ����
			model.addAttribute("commonCodes", commonCodes);
	        model.addAttribute("currentPage", page);
	        model.addAttribute("totalPages", totalPages);
	        model.addAttribute("totalCommonCodes", totalCommonCodes);
        return "commonCode";
        }
    
   
    @PostMapping(value= "api/ccwrite", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> ccWrite(@RequestBody CommonCode commonCode) {
        Map<String, Object> response = new HashMap<>();
        try {
        	adminService.addCommonCode(commonCode);  // �����ڵ� �߰�
            response.put("status", "success");
            response.put("message", "CommonCode successfully registered!");
            response.put("commonCode", commonCode);  // ��ϵ� �����ڵ� ���� ��ȯ
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.put("status", "failure");
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Error occurred while registering common code.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping(value= "api/ccmodify" , produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> ccModify(@RequestBody CommonCode commonCode) {
    	log.info("commonCode"+commonCode);
        Map<String, Object> response = new HashMap<>();
        try {
        	String Seq = commonCode.getSeq();
        	String parentCode = Seq.substring(0, 2);
        	String code = Seq.substring(2, 4);
        	commonCode.setParentCode(parentCode);
        	commonCode.setCode(code);
        	log.info("commonCode"+commonCode);
            boolean success = adminService.updateCommonCode(commonCode);  // �����ڵ� ����
            if (success) {
                response.put("status", "success");
                response.put("message", "CommonCode updated successfully!");
                response.put("commonCode", commonCode);  // ������Ʈ�� �����ڵ� ���� ��ȯ
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("status", "failure");
                response.put("message", "Error updating common code.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Error occurred while updating common code.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @DeleteMapping(value= "api/deletecc", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deletecc(@RequestBody List<String> seqs) {
        Map<String, Object> response = new HashMap<>();

        // CommonCode ����
        for (String seq : seqs) {
        	String parentCode = seq.substring(0, 2);
        	String code = seq.substring(2, 4);
        	log.info(parentCode);
        	if (parentCode.equals("00")) {
        		int child = adminService.checkChildCodesExist(code);
        		log.info("child:"+child);
        		if (child > 0 ) {
        			response.put("status", "failure");
                    response.put("message", "���� �ڵ尡 �����մϴ�. ���� �ڵ带 �̸� �������ּ���.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        		}
        	}
            adminService.deleteCommonCode(seq);
        }
        
        response.put("status", "success");
        response.put("message", "�����ڵ尡 ���������� �����Ǿ����ϴ�.");
        return ResponseEntity.ok(response);
        
    }
   
    @GetMapping(value = "api/commonCode", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> getCommonCodes(@RequestParam(value = "parentCode", required = false) String parentCode,
									          @RequestParam(value = "code", required = false) String code,
									          @RequestParam(value = "codeName", required = false) String codeName,
									          @RequestParam(value = "page", defaultValue = "1") int page,
									          @RequestParam(value = "size", defaultValue = "10") int size) {
		// CommonCode ��ȸ
        List<CommonCode> commonCodes = adminService.searchCommonCodes(parentCode, code, codeName, page, size);
        
        // parentCode ����
        for (CommonCode commonCode : commonCodes) {
        	String parentCodeName = adminService.searchParentCodeName(commonCode.getParentCode());
        	commonCode.setParentCodeName(parentCodeName);
        }
        
        // parentCode�� code�� ���ļ� seq ������ ����
        for (CommonCode commonCode : commonCodes) {
            String seq = commonCode.getParentCode() + commonCode.getCode();
            commonCode.setSeq(seq);
        }
        
        int totalCommonCodes = adminService.getTotalCommonCodeCount(parentCode, code, codeName);
        int totalPages = (int) Math.ceil((double) totalCommonCodes / size);
        
        log.info(commonCodes);

        // ���� ����
        Map<String, Object> response = new HashMap<>();
        response.put("commonCodes", commonCodes);
        response.put("currentPage", page);
        response.put("totalPages", totalPages);
        response.put("totalCommonCodes", totalCommonCodes);

        return response;
    }
    
    @GetMapping(value = "api/ccparentCode", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getParentCommonCodes() {
    	Map<String, Object> response = new HashMap<>();
        
        // CategoryService�� ���� ��з� �ڵ带 ��ȸ
        List<CommonCode> parentCodes = adminService.getParentCommonCodes();
        
        log.info("Ȯ����" + parentCodes);

        // ���� ������ ����
        if (parentCodes != null && !parentCodes.isEmpty()) {
            response.put("status", "success");
            response.put("parentCodes", parentCodes);
        } else {
            response.put("status", "failure");
            response.put("message", "No parent codes found.");
        }

        // ��ȸ�� ����� ��ȯ
        return ResponseEntity.ok(response);
    }
    
    @GetMapping(value = "api/cccode", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCCCommonCodes(@RequestParam("parentCode") String parentCode) {
        List<CommonCode> CCCodes = adminService.getCCCode(parentCode);
    	Map<String, Object> response = new HashMap<>();
        
    	 // ���� �ڵ尡 ������ 204 ���¿� �Բ� �� �迭 ��ȯ
        if (CCCodes.isEmpty()) {
            response.put("status", "no_content");
            response.put("message", "No CCCodes found for the provided parentCode");
            response.put("CCCodes", CCCodes);
            return ResponseEntity.status(204).body(response);
        }

        // ���� �ڵ� ��� ��ȯ
        response.put("status", "success");
        response.put("message", "CCCodes retrieved successfully");
        response.put("CCCodes", CCCodes);
        return ResponseEntity.ok(response); // 200 OK ���� ��ȯ
    }
    
    // ��ü ������ ������ �ٿ�ε�
    @GetMapping("/downloadAllcc")
    public void downloadAllcc(HttpServletResponse response) throws IOException {
    	List<CommonCode> commonCodeList = adminService.getAllCommonCodes();
        ccexportToExcel(response, commonCodeList, "all_common_codes.xlsx");
    }

    // ��ȸ�� ������ ������ �ٿ�ε�
    @GetMapping("/downloadFilteredcc")
    public void downloadFilteredcc(
    		@RequestParam(value = "parentCode", required = false) String parentCode,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "codeName", required = false) String codeName,
            HttpServletResponse response) throws IOException {

    	List<CommonCode> filteredCommonCodeList = adminService.getFilteredCommonCodes(parentCode, code, codeName);
        ccexportToExcel(response, filteredCommonCodeList, "filtered_common_codes.xlsx");
    }

    // ���� ���Ϸ� �����͸� �������� �޼���
    private void ccexportToExcel(HttpServletResponse response, List<CommonCode> commonCodeList, String fileName) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("CommonCodes");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ParentCode");
        headerRow.createCell(1).setCellValue("Code");
        headerRow.createCell(2).setCellValue("CodeName");

        int rowNum = 1;
        for (CommonCode commonCode : commonCodeList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(commonCode.getParentCode());
            row.createCell(1).setCellValue(commonCode.getCode());
            row.createCell(2).setCellValue(commonCode.getCodeName());
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        try (OutputStream os = response.getOutputStream()) {
            workbook.write(os);
        }
        workbook.close();
    }
    
    @PostMapping(value = "api/ccupload", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> ccuploadExcelFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
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
            List<String> expectedHeaders = Arrays.asList("ParentCode", "Code", "CodeName");
            if (!isHeaderValid2(headerRow, expectedHeaders)) {
                response.put("status", "error");
                response.put("message", "����� �÷����� �ùٸ��� �ʽ��ϴ�.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            
            List<CommonCode> commonCodes = new ArrayList<>();

            // ù ��° ���� ����̹Ƿ� �ǳʶݴϴ�.
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                	CommonCode commonCode = new CommonCode();
                    try {
                    	// ParentCode ó��
                        if (row.getCell(0).getCellType() == CellType.NUMERIC) {
                        	String parentCode = String.format("%02d", (int) row.getCell(0).getNumericCellValue());
                            commonCode.setParentCode(parentCode);  // ���ڸ� 2�ڸ� ���ڿ��� ��ȯ
                        } else {
                            commonCode.setParentCode(row.getCell(0).getStringCellValue());
                        }

                        // Code ó��
                        if (row.getCell(1).getCellType() == CellType.NUMERIC) {
                        	String code = String.format("%02d", (int) row.getCell(1).getNumericCellValue());
                            commonCode.setCode(code);  // ���ڸ� ���ڿ��� ��ȯ
                        } else {
                            commonCode.setCode(row.getCell(1).getStringCellValue());
                        }

                        // CodeName�� �⺻������ ���ڿ��� ó��
                        commonCode.setCodeName(row.getCell(2).getStringCellValue());

                        commonCodes.add(commonCode);
                    } catch (Exception e) {
                        e.printStackTrace();
                        response.put("status", "error");
                        response.put("message", "������ ������ ������ �ùٸ��� �ʽ��ϴ�: �� " + (i + 1));
                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }
                }
            }

            // �����ͺ��̽��� ����
            adminService.saveAllCommonCodes(commonCodes);
            response.put("status", "success");
            response.put("message", "���� ���ε尡 ���������� �Ϸ�Ǿ����ϴ�!");
            response.put("totalUploaded", commonCodes.size());

        } catch (DuplicateKeyException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "�ߺ��� ���� �ڵ尡 �߰ߵǾ����ϴ�.");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        } catch (IOException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "���� ó�� �� ������ �߻��߽��ϴ�. �ٽ� �õ��� �ּ���.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    private boolean isHeaderValid2(Row headerRow, List<String> expectedHeaders) {
        for (int i = 0; i < expectedHeaders.size(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell == null || !cell.getStringCellValue().trim().equalsIgnoreCase(expectedHeaders.get(i))) {
                return false;
            }
        }
        return true;
    }
    
    // �з��ڵ� Controller
    
    @GetMapping("/categoryCode")
    public String categoryCodePage(@RequestParam(value = "stageType", required = false) String stageType,
                                   @RequestParam(value = "code", required = false) String code,
                                   @RequestParam(value = "codeName", required = false) String codeName,
                                   @RequestParam(value = "page", defaultValue = "1") int page,
                                   @RequestParam(value = "size", defaultValue = "10") int size,
                                   Model model) {
        // categoryCode ��ȸ
        List<categoryCode> categoryCodes = adminService.searchCategoryCodes(stageType, code, codeName, page, size);
        int totalCategoryCodes = adminService.getTotalCategoryCodeCount(stageType, code, codeName);
        int totalPages = (int) Math.ceil((double) totalCategoryCodes / size);

        log.info("categoryCodes: " + categoryCodes);
        log.info("totalPages: " + totalPages);
        log.info("currentPage: " + page);

        // ���� ����
        model.addAttribute("categoryCodes", categoryCodes);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalCategoryCodes", totalCategoryCodes);
        return "categoryCode";
    }
    
    // ī�װ� �ڵ� ���
    @PostMapping(value = "api/catwrite", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> catWrite(@RequestBody categoryCode categoryCode) {
        Map<String, Object> response = new HashMap<>();
        try {
        	if (categoryCode.getCode().length() > 2) {
        		response.put("status", "failure");
        		response.put("message", "�ڵ�� 2�ڸ� ���Ϸ� �Է��ؾ� �մϴ�.");
        		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        	
        	 // StageType�� ���� �ڵ� ó��
            if ("��".equals(categoryCode.getStageType())) {
                // ���� ��з� �ڵ带 �����´�
            	String parentCode = adminService.getParentCode(categoryCode.getParentCode());

            	if (parentCode == null) {
                    throw new IllegalArgumentException("���� ��з� �ڵ带 ã�� �� �����ϴ�.");
                }

                // �ߺз� �ڵ�� ���� ��з� �ڵ带 �����Ͽ� ���� �ڵ� ����
                categoryCode.setCode(parentCode + categoryCode.getCode());
            }
            // �����ͺ��̽��� ���� �ڵ� ����
            adminService.addCategoryCode(categoryCode);

            // ���� ���� ����
            response.put("status", "success");
            response.put("message", "ī�װ� �ڵ尡 ���������� ��ϵǾ����ϴ�.");
            response.put("categoryCode", categoryCode);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.put("status", "failure");
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Error occurred while registering category code.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value= "api/catmodify" , produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> catModify(@RequestBody categoryCode categoryCode) {
        Map<String, Object> response = new HashMap<>();
        try {
        	if (categoryCode.getCode().length() > 2) {
        		response.put("status", "failure");
        		response.put("message", "�ڵ�� 2�ڸ� ���Ϸ� �Է��ؾ� �մϴ�.");
        		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        	
        	if ("��".equals(categoryCode.getStageType())) {
                // DB���� ���� ��з� �ڵ� ��ȸ
                String parentCode = adminService.getParentCode(categoryCode.getParentCode());

                if (parentCode == null) {
                    response.put("status", "failure");
                    response.put("message", "���� ��з� �ڵ带 ã�� �� �����ϴ�.");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }

                // �ߺз� �ڵ�� ���� ��з� �ڵ带 �����Ͽ� ���� �ڵ� ����
                categoryCode.setCode(parentCode + categoryCode.getCode());
            }
        	
            boolean success = adminService.updateCategoryCode(categoryCode);  // categoryCode ����
            if (success) {
                response.put("status", "success");
                response.put("message", "CategoryCode updated successfully!");
                response.put("categoryCode", categoryCode);  // ������Ʈ�� categoryCode ���� ��ȯ
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("status", "failure");
                response.put("message", "Error updating category code.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Error occurred while updating category code.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value= "api/catdelete", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deletecat(@RequestBody List<String> codeList) {
        Map<String, Object> response = new HashMap<>();
        
        // CategoryCode ����
        for (String code : codeList) {
        	String parentCode = code.substring(0, 2);
        	String subcode = code.substring(2, 4);
        	if (code.length() == 2) {
        		int child = adminService.checkChildCodesExist2(code);
        		log.info("child:"+child);
        		if (child > 0 ) {
        			response.put("status", "failure");
                    response.put("message", "���� �ڵ尡 �����մϴ�. ���� �ڵ带 �̸� �������ּ���.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        	}
        }
            adminService.deleteCategoryCode(code);
        }
        
        response.put("status", "success");
        response.put("message", "�з��ڵ尡 ���������� �����Ǿ����ϴ�.");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping(value = "api/categoryCode", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> getCategoryCodes(@RequestParam(value = "parentCode", required = false) String parentCode,
                                                @RequestParam(value = "code", required = false) String code,
                                                @RequestParam(value = "codeName", required = false) String codeName,
                                                @RequestParam(value = "page", defaultValue = "1") int page,
                                                @RequestParam(value = "size", defaultValue = "10") int size) {
    	Map<String, Object> response = new HashMap<>();
    	
    	// categoryCode ��ȸ
        List<categoryCode> categoryCodes = adminService.searchCategoryCodes(parentCode, code, codeName, page, size);
        int totalCategoryCodes = adminService.getTotalCategoryCodeCount(parentCode, code, codeName);
        int totalPages = (int) Math.ceil((double) totalCategoryCodes / size);

        // ���� ����
        response.put("categoryCodes", categoryCodes);
        response.put("currentPage", page);
        response.put("totalPages", totalPages);
        response.put("totalCategoryCodes", totalCategoryCodes);

        return response;
    }
    
    @GetMapping(value = "api/catparentCode", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getParentCategoryCodes() {
    	Map<String, Object> response = new HashMap<>();
        
        // CategoryService�� ���� ��з� �ڵ带 ��ȸ
        List<categoryCode> parentCodes = adminService.getParentCategoryCodes();

        // ���� ������ ����
        if (parentCodes != null && !parentCodes.isEmpty()) {
            response.put("status", "success");
            response.put("parentCodes", parentCodes);
        } else {
            response.put("status", "failure");
            response.put("message", "No parent codes found.");
        }

        // ��ȸ�� ����� ��ȯ
        return ResponseEntity.ok(response);
    }
    
    @GetMapping(value = "api/catcode", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCTCategoryCodes(@RequestParam("parentCode") String parentCode) {
        List<categoryCode> CTCodes = adminService.getCTCode(parentCode);
        log.info("CTCODE" + CTCodes);
    	Map<String, Object> response = new HashMap<>();
        
    	 // ���� �ڵ尡 ������ 204 ���¿� �Բ� �� �迭 ��ȯ
        if (CTCodes.isEmpty()) {
            response.put("status", "no_content");
            response.put("message", "No CTCodes found for the provided parentCode");
            response.put("CTCodes", CTCodes);
            return ResponseEntity.status(204).body(response);
        }

        // ���� �ڵ� ��� ��ȯ
        response.put("status", "success");
        response.put("message", "CTCodes retrieved successfully");
        response.put("CTCodes", CTCodes);
        return ResponseEntity.ok(response); // 200 OK ���� ��ȯ
    }
    
    // ��ü categoryCode ������ ������ �ٿ�ε�
    @GetMapping("/catdownloadAll")
    public void downloadAllcat(HttpServletResponse response) throws IOException {
        List<categoryCode> categoryCodeList = adminService.getAllCategoryCodes();
        catexportToExcel(response, categoryCodeList, "all_category_codes.xlsx");
    }

    // ��ȸ�� categoryCode ������ ������ �ٿ�ε�
    @GetMapping("/catdownloadFiltered")
    public void downloadFilteredcat(
            @RequestParam(value = "parentCode", required = false) String parentCode,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "codeName", required = false) String codeName,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            HttpServletResponse response) throws IOException {

    	List<categoryCode> filteredCategoryCodeList = adminService.searchCategoryCodes(parentCode, code, codeName, page, size);
        catexportToExcel(response, filteredCategoryCodeList, "filtered_category_codes.xlsx");
    }

    // ���� ���Ϸ� �����͸� �������� �޼���
    private void catexportToExcel(HttpServletResponse response, List<categoryCode> categoryCodeList, String fileName) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("CategoryCodes");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("StageType");
        headerRow.createCell(1).setCellValue("Code");
        headerRow.createCell(2).setCellValue("CodeName");

        int rowNum = 1;
        for (categoryCode categoryCode : categoryCodeList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(categoryCode.getStageType());
            row.createCell(1).setCellValue(categoryCode.getCode());
            row.createCell(2).setCellValue(categoryCode.getCodeName());
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        try (OutputStream os = response.getOutputStream()) {
            workbook.write(os);
        }
        workbook.close();
    }
    
    @PostMapping(value = "api/catupload", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> catuploadExcelFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
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
            List<String> expectedHeaders = Arrays.asList("StageType", "Code", "CodeName");
            if (!isHeaderValid3(headerRow, expectedHeaders)) {
                response.put("status", "error");
                response.put("message", "����� �÷����� �ùٸ��� �ʽ��ϴ�.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            List<categoryCode> categoryCodes = new ArrayList<>();

            // ù ��° ���� ����̹Ƿ� �ǳʶݴϴ�.
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    categoryCode categoryCode = new categoryCode();
                    try {
                    	categoryCode.setStageType(row.getCell(0).getStringCellValue());  // ù ��° �� (StageType)
                        // Code ó��
                        if (row.getCell(1).getCellType() == CellType.NUMERIC) {
                        	if ("��".equals(categoryCode.getStageType())) {
	                        	String code = String.format("%02d", (int) row.getCell(1).getNumericCellValue());
	                        	categoryCode.setCode(code);  // ���ڸ� ���ڿ��� ��ȯ
                        	}
                        	else {
	                    		String code = String.format("%04d", (int) row.getCell(1).getNumericCellValue());
	                        	categoryCode.setCode(code);  // ���ڸ� ���ڿ��� ��ȯ
                        	}
                        } else {
                        	categoryCode.setCode(row.getCell(1).getStringCellValue());
                        }                        
                        categoryCode.setCode(row.getCell(1).getStringCellValue());       // �� ��° �� (Code)
                        categoryCode.setCodeName(row.getCell(2).getStringCellValue());   // �� ��° �� (CodeName)
                        categoryCodes.add(categoryCode);
                    } catch (Exception e) {
                        e.printStackTrace();
                        response.put("status", "error");
                        response.put("message", "������ ������ ������ �ùٸ��� �ʽ��ϴ�: �� " + (i + 1));
                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }
                }
            }

            // �����ͺ��̽��� ����
            adminService.saveAllCategoryCodes(categoryCodes);
            response.put("status", "success");
            response.put("message", "���� ���ε尡 ���������� �Ϸ�Ǿ����ϴ�!");
            response.put("totalUploaded", categoryCodes.size());

        } catch (DuplicateKeyException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "�ߺ��� ī�װ� �ڵ尡 �߰ߵǾ����ϴ�.");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        } catch (IOException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "���� ó�� �� ������ �߻��߽��ϴ�. �ٽ� �õ��� �ּ���.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    private boolean isHeaderValid3(Row headerRow, List<String> expectedHeaders) {
        for (int i = 0; i < expectedHeaders.size(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell == null || !cell.getStringCellValue().trim().equalsIgnoreCase(expectedHeaders.get(i))) {
                return false;
            }
        }
        return true;
    }
    

    



}
