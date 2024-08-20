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

import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@RequestMapping("/tms/*")
public class CodeController {
	
	@Autowired
    private AdminService adminService;
	
	@Autowired
	private UserService userService;
	
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
			
			log.info("commonCodes: " + commonCodes);
			log.info("totalPages: " + totalPages);
			log.info("currentPage: " + page);
			
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
        Map<String, Object> response = new HashMap<>();
        try {
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
    public ResponseEntity<Map<String, Object>> deletecc(@RequestBody List<String> codeList) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = adminService.deleteCommonCode(codeList.toArray(new String[0]));  // �����ڵ� ����

            if (success) {
                response.put("status", "success");
                response.put("message", "CommonCodes deleted successfully!");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("status", "failure");
                response.put("message", "Error deleting common codes.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Error occurred while deleting common codes.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
        int totalCommonCodes = adminService.getTotalCommonCodeCount(parentCode, code, codeName);
        int totalPages = (int) Math.ceil((double) totalCommonCodes / size);

        // ���� ����
        Map<String, Object> response = new HashMap<>();
        response.put("commonCodes", commonCodes);
        response.put("currentPage", page);
        response.put("totalPages", totalPages);
        response.put("totalCommonCodes", totalCommonCodes);

        return response;
    }
    
    // ��ü ����� ������ ������ �ٿ�ε�
    @GetMapping("/downloadAllcc")
    public void downloadAllcc(HttpServletResponse response) throws IOException {
    	List<CommonCode> commonCodeList = adminService.getAllCommonCodes();
        ccexportToExcel(response, commonCodeList, "all_common_codes.xlsx");
    }

    // ��ȸ�� ����� ������ ������ �ٿ�ε�
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
                        commonCode.setParentCode(row.getCell(0).getStringCellValue());  // ù ��° �� (ParentCode)
                        commonCode.setCode(row.getCell(1).getStringCellValue());       // �� ��° �� (Code)
                        commonCode.setCodeName(row.getCell(2).getStringCellValue());             // �� ��° �� (CodeName)
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
    
    



}
