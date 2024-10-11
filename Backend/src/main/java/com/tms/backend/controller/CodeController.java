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
import com.tms.backend.service.FileService;
import com.tms.backend.service.UserService;
import com.tms.backend.vo.CommonCode;
import com.tms.backend.vo.Criteria;
import com.tms.backend.vo.Notice;
import com.tms.backend.vo.PageDTO;
import com.tms.backend.vo.User;
import com.tms.backend.vo.categoryCode;
import com.tms.backend.vo.devProgress;

import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@RequestMapping("/*")
public class CodeController {
	
	@Autowired
    private AdminService adminService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private FileService fileService;
	
	//�����ڵ� Controller
	
	//�����ڵ� ������
	@GetMapping("/commonCode")
    public String commonCodePage() {
        return "commonCode";
        }
    
	//�����ڵ� �߰�
    @PostMapping(value= "api/ccwrite", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> ccWrite(@RequestBody CommonCode commonCode) {
    	//json �����Ϳ� response
        Map<String, Object> response = new HashMap<>();
        try {
        	adminService.addCommonCode(commonCode);  // �����ڵ� �߰�
            response.put("status", "success");
            response.put("message", "CommonCode successfully registered!");
            response.put("commonCode", commonCode);  // ��ϵ� �����ڵ� ���� ��ȯ
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) { 
            response.put("status", "failure");
            response.put("message", "���� �ڵ� ����߿� ������ �߻��߽��ϴ�");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (DuplicateKeyException e) { //�����ڵ� �ߺ� �޼�����
            response.put("status", "failure");
            response.put("message", "���� �ڵ尡 �ߺ��Ǿ����ϴ�.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) { // ��Ÿ ���� �޽��� ��
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "���� �ڵ� ����߿� ������ �߻��߽��ϴ�");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    //�����ڵ� ����
    @PostMapping(value= "api/ccmodify" , produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> ccModify(@RequestBody CommonCode commonCode) {
        Map<String, Object> response = new HashMap<>();
        try {
        	String Seq = commonCode.getSeq(); // �����ڵ� seq ����
        	String parentCode = Seq.substring(0, 2); // seq�� �� 2�ڸ��� parentCode�� ����
        	String code = Seq.substring(2, 4); // seq�� �� 2�ڸ��� �Ϲ� code�� ����
        	commonCode.setParentCode(parentCode); // �����ڵ� VO�� parentCode �߰�
        	commonCode.setCode(code);
            boolean success = adminService.updateCommonCode(commonCode);  // �����ڵ� ����
            if (success) {
                response.put("status", "success");
                response.put("message", "���� �ڵ尡 ���������� ��ϵƽ��ϴ�");
                response.put("commonCode", commonCode);  // ������Ʈ�� �����ڵ� ���� ��ȯ
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("status", "failure");
                response.put("message", "���� �ڵ� ��� �߿� ������ �߻��߽��ϴ�.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "���� �ڵ� ��� �߿� ������ �߻��߽��ϴ�.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    //�����ڵ� ����
    @DeleteMapping(value= "api/deletecc", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deletecc(@RequestBody List<String> seqs) {
        Map<String, Object> response = new HashMap<>();

        // CommonCode ����
        for (String seq : seqs) {
        	String parentCode = seq.substring(0, 2);// seq�� �� 2�ڸ��� parentCode�� ����
        	String code = seq.substring(2, 4); // seq�� �� 2�ڸ��� �Ϲ� code�� ����
        	if (parentCode.equals("00")) { // ���� �ڵ尡 ���� ���
        		int child = adminService.checkChildCodesExist(code); //�����ڵ� ���� ���� Ȯ��
        		if (child > 0 ) {
        			response.put("status", "failure");
                    response.put("message", "���� �ڵ尡 �����մϴ�. ���� �ڵ带 �̸� �������ּ���.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        		}
        	}
            adminService.deleteCommonCode(seq); //�����ڵ� ����
        }
        
        response.put("status", "success");
        response.put("message", "�����ڵ尡 ���������� �����Ǿ����ϴ�.");
        return ResponseEntity.ok(response);
        
    }
   
    //�����ڵ� ��ȸ
    @GetMapping(value = "api/commonCode", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> getCommonCodes(@RequestParam(value = "parentCode", required = false) String parentCode,
									          @RequestParam(value = "code", required = false) String code,
									          @RequestParam(value = "codeName", required = false) String codeName,
									          @RequestParam(value = "page", defaultValue = "1") int page,
									          @RequestParam(value = "size", defaultValue = "15") int size) {
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
        
        int totalCommonCodes = adminService.getTotalCommonCodeCount(parentCode, code, codeName); //�����ڵ� �� ����
        int totalPages = (int) Math.ceil((double) totalCommonCodes / size); // �� ��ȸ ������
        
        // ���� ����
        Map<String, Object> response = new HashMap<>();
        response.put("commonCodes", commonCodes);
        response.put("currentPage", page);
        response.put("totalPages", totalPages);
        response.put("totalCommonCodes", totalCommonCodes);

        return response;
    }
    
    //�����ڵ� ���� ��������
    @GetMapping(value = "api/ccparentCode", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getParentCommonCodes() {
    	//json ���� ����
    	Map<String, Object> response = new HashMap<>();
        
        // CategoryService�� ���� ��з� �ڵ带 ��ȸ
        List<CommonCode> parentCodes = adminService.getParentCommonCodes();
        
        if (parentCodes != null && !parentCodes.isEmpty()) { // �����ڵ尡 ���� ��
            response.put("status", "success");
            response.put("parentCodes", parentCodes);
        } else { // �����ڵ尡 ���� ��
            response.put("status", "failure");
            response.put("message", "���� �ڵ带 ã�� �� �����ϴ�.");
        }

        // ��ȸ�� ����� ��ȯ
        return ResponseEntity.ok(response);
    }
    
    //�����ڵ忡 ���� �����ڵ� ��ȸ
    @GetMapping(value = "api/cccode", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCCCommonCodes(@RequestParam("parentCode") String parentCode) {
        List<CommonCode> CCCodes = adminService.getCCCode(parentCode); // �����ڵ� �Է��ؼ� ���� �����ڵ� ����Ʈ ��������
    	Map<String, Object> response = new HashMap<>(); // ���� ����
        
    	 // ���� �ڵ尡 ������ 204 ���¿� �Բ� �� �迭 ��ȯ
        if (CCCodes.isEmpty()) {
            response.put("status", "no_content");
            response.put("message", "���� �ڵ忡 �´� ���� ���� �ڵ尡 �����ϴ�");
            response.put("CCCodes", CCCodes);
            return ResponseEntity.status(204).body(response);
        }

        // ���� �ڵ� ��� ��ȯ
        response.put("status", "success");
        response.put("message", "���� ���� �ڵ带 ȣ���߽��ϴ�.");
        response.put("CCCodes", CCCodes);
        return ResponseEntity.ok(response); // 200 OK ���� ��ȯ
    }
    
    // ��ü ������ ������ �ٿ�ε�
    @GetMapping("/downloadAllcc")
    public void downloadAllcc(HttpServletResponse response) throws IOException {
    	List<CommonCode> commonCodeList = adminService.getAllCommonCodes();
        ccexportToExcel(response, commonCodeList, "all_common_codes.xlsx");
    }
    
    // �׼� ���� ���ø� �ٿ�ε�
    @GetMapping("/ccexampleexcel")
    public void downloadExcc(HttpServletResponse response) throws IOException {
    	// �ƹ��� �����͵� �� �������� ���� ����
    	List<CommonCode> ExampleDEV = adminService.getFilteredCommonCodes(null, null, "No_value");
    	ccexportToExcel(response, ExampleDEV, "example.xlsx");
    }

    // ��ȸ�� ������ ������ �ٿ�ε�
    @GetMapping("/downloadFilteredcc")
    public void downloadFilteredcc(
    		@RequestParam(value = "parentCode", required = false) String parentCode,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "codeName", required = false) String codeName,
            HttpServletResponse response) throws IOException {
    	// ���� ��ȸ ������ ��������
    	List<CommonCode> filteredCommonCodeList = adminService.getFilteredCommonCodes(parentCode, code, codeName);
        ccexportToExcel(response, filteredCommonCodeList, "filtered_common_codes.xlsx");
    }

    // ���� ���Ϸ� �����͸� �������� �޼���
    private void ccexportToExcel(HttpServletResponse response, List<CommonCode> commonCodeList, String fileName) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("CommonCodes");
        //�׼� Row �����
        Row headerRow = sheet.createRow(0);
        //�÷� �����
        headerRow.createCell(0).setCellValue("ParentCode");
        headerRow.createCell(1).setCellValue("Code");
        headerRow.createCell(2).setCellValue("CodeName");

        int rowNum = 1;
        for (CommonCode commonCode : commonCodeList) {
            Row row = sheet.createRow(rowNum++);
            //�׼��� ������ �Է�
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
    
    //�׼� ���ε�
    @PostMapping(value = "api/ccupload", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> ccuploadExcelFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
    	Map<String, Object> response = new HashMap<>();
        if (file.isEmpty()) { //���ε��� �׼� ������ ���� ���
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
            if (!fileService.isHeaderValid(headerRow, expectedHeaders)) { //�÷� ��
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
                        String parentCode;
                        if (row.getCell(0).getCellType() == CellType.NUMERIC) { // �Է°��� ������ ���
                            parentCode = String.format("%02d", (int) row.getCell(0).getNumericCellValue());
                        } else {
                            parentCode = row.getCell(0).getStringCellValue();
                        }

                        // Code ó��
                        String code;
                        if (row.getCell(1).getCellType() == CellType.NUMERIC) {
                            code = String.format("%02d", (int) row.getCell(1).getNumericCellValue());
                        } else {
                            code = row.getCell(1).getStringCellValue();
                        }

                        // CodeName ó��
                        String codeName = row.getCell(2).getStringCellValue();

                        // Ư�� ���� �˻� (��: �� ���ڿ� �Ǵ� null ���� �ƴ� ��쿡�� �߰�)
                        if (parentCode != null && !parentCode.trim().isEmpty() &&
                            code != null && !code.trim().isEmpty() &&
                            codeName != null && !codeName.trim().isEmpty()) {

                            // CommonCode ��ü�� �� ����
                            commonCode.setParentCode(parentCode);
                            commonCode.setCode(code);
                            commonCode.setCodeName(codeName);
                            
                            //�ߺ� Ȯ��
                            boolean Count = adminService.countdupliCCode(commonCode);
	                        if (!Count) {
	                        	commonCodes.add(commonCode);
	                        }
	                        else {
	                        	}
                            }
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
         
        } catch (DuplicateKeyException e) { // �׼��� �Է��� �����ڵ尡 �ߺ��� ���
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "���� �ڵ尡 �ߺ��Ǿ����ϴ�.");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        } catch (IOException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "���� ó�� �� ������ �߻��߽��ϴ�. �ٽ� �õ��� �ּ���.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    // �з��ڵ� Controller
    
    //�з��ڵ� ������
    @GetMapping("/categoryCode")
    public String categoryCodePage() {
        return "categoryCode";
    }
    
    // �з� �ڵ� ���
    @PostMapping(value = "api/catwrite", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> catWrite(@RequestBody categoryCode categoryCode) {
        Map<String, Object> response = new HashMap<>(); //���� ����
        try {
        	if (categoryCode.getCode().length() > 2) { // �з��ڵ� �ڵ尡 2�ڸ� �̻��� ���
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
            response.put("message", "�з� �ڵ带 ����߿� ������ �߻��߽��ϴ�.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (DuplicateKeyException e) {
            response.put("status", "failure");
            response.put("message", "�з� �ڵ尡 �ߺ��Ǿ����ϴ�.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "�з� �ڵ带 ����߿� ������ �߻��߽��ϴ�.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    //�з��ڵ� ����
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
                response.put("message", "�з� �ڵ尡 ���������� �����ƽ��ϴ�");
                response.put("categoryCode", categoryCode);  // ������Ʈ�� categoryCode ���� ��ȯ
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("status", "failure");
                response.put("message", "�з� �ڵ� ������ �����߽��ϴ�");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "�з� �ڵ� �����߿� ������ �߻��߽��ϴ�.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    //�з��ڵ� ����
    @DeleteMapping(value= "api/catdelete", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deletecat(@RequestBody List<String> codeList) {
        Map<String, Object> response = new HashMap<>();        
        // CategoryCode ����
        for (String code : codeList) {
        	if (code.length() == 2) { //�з��ڵ尡 ���� �ڵ� ����� ������ ���� �ڵ��� ���
        		int child = adminService.checkChildCodesExist2(code); // ���� �ڵ� ���� Ȯ��
        		if (child > 0 ) {
        			response.put("status", "failure");
                    response.put("message", "���� �ڵ尡 �����մϴ�. ���� �ڵ带 �̸� �������ּ���.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        	}
        }
            adminService.deleteCategoryCode(code); //�з��ڵ� ����
        }
        
        response.put("status", "success");
        response.put("message", "�з��ڵ尡 ���������� �����Ǿ����ϴ�.");
        return ResponseEntity.ok(response);
    }
    
    //�з��ڵ� ��ȸ
    @GetMapping(value = "api/categoryCode", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> getCategoryCodes(@RequestParam(value = "parentCode", required = false) String parentCode,
                                                @RequestParam(value = "code", required = false) String code,
                                                @RequestParam(value = "codeName", required = false) String codeName,
                                                @RequestParam(value = "page", defaultValue = "1") int page,
                                                @RequestParam(value = "size", defaultValue = "15") int size) {
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
    
    // �з��ڵ� �θ��ڵ� ��������
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
            response.put("message", "�����ڵ尡 �����ϴ�");
        }

        // ��ȸ�� ����� ��ȯ
        return ResponseEntity.ok(response);
    }
    
    //���� �ڵ忡 ���� ���� �з��ڵ� ��������
    @GetMapping(value = "api/catcode", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCTCategoryCodes(@RequestParam("parentCode") String parentCode) {
        List<categoryCode> CTCodes = adminService.getCTCode(parentCode); //�Է��� �����ڵ忡 ���� ���� �з��ڵ�
    	Map<String, Object> response = new HashMap<>();
        
    	 // ���� �ڵ尡 ������ 204 ���¿� �Բ� �� �迭 ��ȯ
        if (CTCodes.isEmpty()) {
            response.put("status", "no_content");
            response.put("message", "�ش��ϴ� �ߺз��� �����ϴ�");
            response.put("CTCodes", CTCodes);
            return ResponseEntity.ok(response);
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
    
    // �׼� ���� ���ø� �ٿ�ε�
    @GetMapping("/catexampleexcel")
    public void downloadExcat(HttpServletResponse response) throws IOException {
    	List<categoryCode> ExampleDEV = adminService.searchCategoryCodes(null, null, "NO_VALUE", 1, 10); //���� �ޱ� ���� ����  	
    	catexportToExcel(response, ExampleDEV, "example.xlsx");
    }

    // ��ȸ�� categoryCode ������ ������ �ٿ�ε�
    @GetMapping("/catdownloadFiltered")
    public void downloadFilteredcat(
            @RequestParam(value = "parentCode", required = false) String parentCode,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "codeName", required = false) String codeName,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "15") int size,
            HttpServletResponse response) throws IOException {

    	List<categoryCode> filteredCategoryCodeList = adminService.searchCategoryCodes(parentCode, code, codeName, page, size);
        catexportToExcel(response, filteredCategoryCodeList, "filtered_category_codes.xlsx");
    }

    // ���� ���Ϸ� �����͸� �������� �޼���
    private void catexportToExcel(HttpServletResponse response, List<categoryCode> categoryCodeList, String fileName) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("CategoryCodes");
        
        //�ο� �� ����
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("StageType");
        headerRow.createCell(1).setCellValue("Code");
        headerRow.createCell(2).setCellValue("CodeName");

        int rowNum = 1;
        for (categoryCode categoryCode : categoryCodeList) {
            Row row = sheet.createRow(rowNum++);
            // ������ �Է�
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
    
    // �׼� ���ε�
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
            if (!fileService.isHeaderValid(headerRow, expectedHeaders)) {
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
                    	// StageType ó��
                    	String stageType;
                    	stageType = row.getCell(0).getStringCellValue();
                        // Code ó��
                    	String code;
                        if (row.getCell(1).getCellType() == CellType.NUMERIC) {
                        	if ("��".equals(stageType)) {
	                        	code = String.format("%02d", (int) row.getCell(1).getNumericCellValue());
                        	}
                        	else {
	                    		code = String.format("%04d", (int) row.getCell(1).getNumericCellValue());
                        	}
                        } else {
                        	code = row.getCell(1).getStringCellValue();
                        }
                        // �ڵ���� ó��
                        String codeName;
                        codeName = row.getCell(2).getStringCellValue();
                        // Ư�� ���� �˻� (��: �� ���ڿ� �Ǵ� null ���� �ƴ� ��쿡�� �߰�)
                        if (stageType != null && !stageType.trim().isEmpty() &&
                            code != null && !code.trim().isEmpty() &&
                            codeName != null && !codeName.trim().isEmpty()) {
                        	categoryCode.setStageType(stageType);
	                        categoryCode.setCode(code);       // �� ��° �� (Code)
	                        categoryCode.setCodeName(codeName);   // �� ��° �� (CodeName)
	                        boolean Count = adminService.countdupliCtCode(categoryCode);
	                        if (!Count) {
	                        	categoryCodes.add(categoryCode);
	                        }
	                        else {
	                        	}
                        }
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
            response.put("message", "�з� �ڵ尡 �ߺ��Ǿ����ϴ�.");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        } catch (IOException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "���� ó�� �� ������ �߻��߽��ϴ�. �ٽ� �õ��� �ּ���.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    

    



}
