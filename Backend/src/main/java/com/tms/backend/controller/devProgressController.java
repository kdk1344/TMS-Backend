package com.tms.backend.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tms.backend.service.AdminService;
import com.tms.backend.service.DevService;
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
@RequestMapping("/tms/*")
public class devProgressController {
	
	@Autowired
    private DevService devservice;
	
	@Autowired
	private AdminService adminService;
	
	//����������� Controller
	
	@GetMapping("/devProgress")
	public String devProgressPage(
	        @RequestParam(value = "majorCategory", required = false) String majorCategory,
	        @RequestParam(value = "subCategory", required = false) String subCategory,
	        @RequestParam(value = "programType", required = false) String programType,
	        @RequestParam(value = "programName", required = false) String programName,
	        @RequestParam(value = "programId", required = false) String programId,
	        @RequestParam(value = "programStatus", required = false) String programStatus,
	        @RequestParam(value = "developer", required = false) String developer,
	        @RequestParam(value = "devStatus", required = false) String devStatus,
	        @RequestParam(value = "actualStartDate", required = false) String actualStartDate,
	        @RequestParam(value = "actualEndDate", required = false) String actualEndDate,
	        @RequestParam(value = "pl", required = false) String pl,
	        @RequestParam(value = "thirdPartyTestMgr", required = false) String thirdPartyTestMgr,
	        @RequestParam(value = "ItMgr", required = false) String ItMgr,
	        @RequestParam(value = "BusiMgr", required = false) String BusiMgr,
	        @RequestParam(value = "page", defaultValue = "1") int page,
	        @RequestParam(value = "size", defaultValue = "10") int size,
	        Model model) {

	    // Service�� ���� �����͸� ��ȸ
	    List<devProgress> devProgressList = devservice.searchDevProgress(
	            majorCategory, subCategory, programType, programName,programId, programStatus, developer,
	            devStatus, actualStartDate, actualEndDate, pl, thirdPartyTestMgr, ItMgr, BusiMgr, page, size
	    );
	    
	    List<User> developers = adminService.findDevlopers();
	    log.info("������ ���"+developers);

	    int totalDevProgress = devservice.getTotalDevProgressCount(
	            majorCategory, subCategory, programType, programName,programId, programStatus, developer,
	            devStatus, actualStartDate, actualEndDate, pl, thirdPartyTestMgr, ItMgr, BusiMgr
	    );

	    int totalPages = (int) Math.ceil((double) totalDevProgress / size);
	    
	    log.info(devProgressList);

	    // �𵨿� �����͸� �߰�
	    model.addAttribute("devProgressList", devProgressList);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", totalPages);
	    model.addAttribute("totalDevProgress", totalDevProgress);

	    return "devProgress"; // JSP �������� �̵�
	}
	
	//���� ���� ��Ȳ ��ȸ
	@GetMapping(value="api/devProgress" , produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> APIdevProgressPage(
	        @RequestParam(value = "majorCategory", required = false) String majorCategory,
	        @RequestParam(value = "subCategory", required = false) String subCategory,
	        @RequestParam(value = "programType", required = false) String programType,
	        @RequestParam(value = "programName", required = false) String programName,
	        @RequestParam(value = "programId", required = false) String programId,
	        @RequestParam(value = "programStatus", required = false) String programStatus,
	        @RequestParam(value = "developer", required = false) String developer,
	        @RequestParam(value = "devStatus", required = false) String devStatus,
	        @RequestParam(value = "actualStartDate", required = false) String actualStartDate,
	        @RequestParam(value = "actualEndDate", required = false) String actualEndDate,
	        @RequestParam(value = "pl", required = false) String pl,
	        @RequestParam(value = "thirdPartyTestMgr", required = false) String thirdPartyTestMgr,
	        @RequestParam(value = "ItMgr", required = false) String ItMgr,
	        @RequestParam(value = "BusiMgr", required = false) String BusiMgr,
	        @RequestParam(value = "page", defaultValue = "1") int page,
	        @RequestParam(value = "size", defaultValue = "10") int size,
	        Model model) {

	    // Service�� ���� �����͸� ��ȸ
	    List<devProgress> devProgressList = devservice.searchDevProgress(
	            majorCategory, subCategory, programType, programName, programId, programStatus, developer,
	            devStatus, actualStartDate, actualEndDate, pl, thirdPartyTestMgr, ItMgr, BusiMgr, page, size
	    );

	    int totalDevProgress = devservice.getTotalDevProgressCount(
	            majorCategory, subCategory, programType, programName, programId, programStatus, developer,
	            devStatus, actualStartDate, actualEndDate, pl, thirdPartyTestMgr, ItMgr, BusiMgr
	    );

	    int totalPages = (int) Math.ceil((double) totalDevProgress / size);
	    
	    // ���� ����
	    Map<String, Object> response = new HashMap<>();
	    response.put("devProgressList", devProgressList);
	    response.put("currentPage", page);
	    response.put("totalPages", totalPages);
	    response.put("totalDevProgress", totalDevProgress);

	    return ResponseEntity.ok(response); // JSON���� ���� ��ȯ
	}
	
	@GetMapping(value = "api/devuser", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDevUser() {
    	Map<String, Object> response = new HashMap<>();
        
        // CategoryService�� ���� ��з� �ڵ带 ��ȸ
        List<User> developer = adminService.findDevlopers();
        
        log.info("Ȯ����" + developer);

        // ���� ������ ����
        if (developer != null && !developer.isEmpty()) {
            response.put("status", "success");
            response.put("developer", developer);
        } else {
            response.put("status", "failure");
            response.put("message", "No developer found.");
        }

        // ��ȸ�� ����� ��ȯ
        return ResponseEntity.ok(response);
    }
	
	//���� ���� ��Ȳ ����
	@PostMapping(value= "api/devmodify" , produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> DevModify(@RequestBody devProgress devprogress,
    		@RequestPart(value = "file", required = false) MultipartFile[] files,  // ����
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
//        // ���� ���� ��Ȳ ���� ���� Ȯ��
//        devProgress devprog = devservice.get(devprogress.getSeq());
//        if (devprog == null) {
//            response.put("message", "�ش� ���� ���� ��Ȳ�� �������� �ʽ��ϴ�.");
//            return ResponseEntity.status(404).body(response);
//        }
//
//        // ���� �������� ������Ʈ
//        devprog.setTitle(devprogress.get());
//        devprog.setContent(devprogress.getContent());
//        devprog.setPostDate(devprogress.getPostDate());
//
//        // �������׿� ��ϵ� ���� ÷������ ���� ����
//        adminService.deleteAttachmentsByNoticeId(existingNotice.getSeq());
//
//        // ���ο� ���� ���ε� ó��
//        handleFileUpload(files, existingNotice, boardType);
//        
//        log.info("check "+existingNotice);
//
//        // ������Ʈ�� ���������� ����
//        adminService.updateNotice(existingNotice);
//        
//
//        // ���� ����
//        response.put("status", "success");
//        response.put("message", "�Խñ��� ���������� �����Ǿ����ϴ�.");
//        response.put("notice", existingNotice);

        return ResponseEntity.ok(response);
    }
	
	
	//���� ���� ��Ȳ ����
	@DeleteMapping(value= "api/deletedev", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deletedev(@RequestBody List<String> seqs) {
        Map<String, Object> response = new HashMap<>();
        
        for (String seq : seqs) {
            devservice.deleteDevProgress(seq);
        }
        
        response.put("status", "success");
        response.put("message", "�����ڵ尡 ���������� �����Ǿ����ϴ�.");
        return ResponseEntity.ok(response);
    }
	
	// ��ü ���� ���� ��Ȳ ������ ������ �ٿ�ε�
    @GetMapping("/devdownloadAll")
    public void downloadAlldev(HttpServletResponse response) throws IOException {
        List<devProgress> DevCodeList = devservice.searchDevProgress(null, null, null, null, null, null, null, null, null, null, null, null, null, null, 1, Integer.MAX_VALUE);
        log.info(DevCodeList);
        devexportToExcel(response, DevCodeList, "all_dev_codes.xlsx");
    }
    
    // ��ȸ�� ���� ���� ��Ȳ ������ ������ �ٿ�ε�
    @GetMapping("/devdownloadFiltered")
    public void downloadFiltereddev(
    		@RequestParam(value = "majorCategory", required = false) String majorCategory,
	        @RequestParam(value = "subCategory", required = false) String subCategory,
	        @RequestParam(value = "programType", required = false) String programType,
	        @RequestParam(value = "programName", required = false) String programName,
	        @RequestParam(value = "programId", required = false) String programId,
	        @RequestParam(value = "programStatus", required = false) String programStatus,
	        @RequestParam(value = "developer", required = false) String developer,
	        @RequestParam(value = "devStatus", required = false) String devStatus,
	        @RequestParam(value = "actualStartDate", required = false) String actualStartDate,
	        @RequestParam(value = "actualEndDate", required = false) String actualEndDate,
	        @RequestParam(value = "pl", required = false) String pl,
	        @RequestParam(value = "thirdPartyTestMgr", required = false) String thirdPartyTestMgr,
	        @RequestParam(value = "ItMgr", required = false) String ItMgr,
	        @RequestParam(value = "BusiMgr", required = false) String BusiMgr,
	        @RequestParam(value = "page", defaultValue = "1") int page,
	        @RequestParam(value = "size", defaultValue = "10") int size,
            HttpServletResponse response) throws IOException {
    	
    	log.info("Ȯ����"+subCategory);
    	log.info("Ȯ����"+actualStartDate);
    	log.info("Ȯ����"+actualEndDate);
    	List<devProgress> filteredDevCodeList = devservice.searchDevProgress(
	            majorCategory, subCategory, programType, programName, programId, programStatus, developer,
	            devStatus, actualStartDate, actualEndDate, pl, thirdPartyTestMgr, ItMgr, BusiMgr, page, size
	    );

    	log.info("Ȯ����"+filteredDevCodeList);
        devexportToExcel(response, filteredDevCodeList, "filtered_dev_codes.xlsx");
    }

    // ���� ���Ϸ� �����͸� �������� �޼���
    private void devexportToExcel(HttpServletResponse response, List<devProgress> devProgressList, String fileName) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("DevProgress");

        // ��� ����
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("SEQ");
        headerRow.createCell(1).setCellValue("MAJOR_CATEGORY");
        headerRow.createCell(2).setCellValue("SUB_CATEGORY");
        headerRow.createCell(3).setCellValue("MINOR_CATEGORY");
        headerRow.createCell(4).setCellValue("PROGRAM_DETAIL_TYPE");
        headerRow.createCell(5).setCellValue("PROGRAM_TYPE");
        headerRow.createCell(6).setCellValue("PROGRAM_ID");
        headerRow.createCell(7).setCellValue("PROGRAM_NAME");
        headerRow.createCell(8).setCellValue("CLASS_NAME");
        headerRow.createCell(9).setCellValue("SCREEN_ID");
        headerRow.createCell(10).setCellValue("SCREEN_NAME");
        headerRow.createCell(11).setCellValue("SCREEN_MENU_PATH");
        headerRow.createCell(12).setCellValue("PRIORITY");
        headerRow.createCell(13).setCellValue("DIFFICULTY");
        headerRow.createCell(14).setCellValue("ESTIMATED_EFFORT");
        headerRow.createCell(15).setCellValue("program_status");
        headerRow.createCell(16).setCellValue("REQ_ID");
        headerRow.createCell(17).setCellValue("DELETION_HANDLER");
        headerRow.createCell(18).setCellValue("DELETION_DATE");
        headerRow.createCell(19).setCellValue("DELETION_REASON");
        headerRow.createCell(20).setCellValue("DEVELOPER");
        headerRow.createCell(21).setCellValue("PLANNED_START_DATE");
        headerRow.createCell(22).setCellValue("PLANNED_END_DATE");
        headerRow.createCell(23).setCellValue("ACTUAL_START_DATE");
        headerRow.createCell(24).setCellValue("ACTUAL_END_DATE");
        headerRow.createCell(25).setCellValue("PL");
        headerRow.createCell(26).setCellValue("PL_TEST_SCD_DATE");
        headerRow.createCell(27).setCellValue("PL_TEST_CMP_DATE");
        headerRow.createCell(28).setCellValue("PL_TEST_RESULT");
        headerRow.createCell(29).setCellValue("PL_TEST_NOTES");
        headerRow.createCell(30).setCellValue("IT_MGR");
        headerRow.createCell(31).setCellValue("IT_TEST_DATE");
        headerRow.createCell(32).setCellValue("IT_CONFIRM_DATE");
        headerRow.createCell(33).setCellValue("IT_TEST_RESULT");
        headerRow.createCell(34).setCellValue("IT_TEST_NOTES");
        headerRow.createCell(35).setCellValue("BUSI_MGR");
        headerRow.createCell(36).setCellValue("BUSI_TEST_DATE");
        headerRow.createCell(37).setCellValue("BUSI_CONFIRM_DATE");
        headerRow.createCell(38).setCellValue("BUSI_TEST_RESULT");
        headerRow.createCell(39).setCellValue("BUSI_TEST_NOTES");
        headerRow.createCell(40).setCellValue("THIRD_PARTY_TEST_MGR");
        headerRow.createCell(41).setCellValue("THIRD_PARTY_TEST_DATE");
        headerRow.createCell(42).setCellValue("THIRD_PARTY_CONFIRM_DATE");
        headerRow.createCell(43).setCellValue("THIRD_TEST_RESULT");
        headerRow.createCell(44).setCellValue("THIRD_PARTY_TEST_NOTES");
        headerRow.createCell(45).setCellValue("DEV_STATUS");
        headerRow.createCell(46).setCellValue("INIT_REG_DATE");
        headerRow.createCell(47).setCellValue("INIT_REGISTRAR");
        headerRow.createCell(48).setCellValue("LAST_MODIFIED_DATE");
        headerRow.createCell(49).setCellValue("LAST_MODIFIER");

        // ������ �� ����
        int rowNum = 1;
        for (devProgress devProgress : devProgressList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(devProgress.getSeq());
            row.createCell(1).setCellValue(devProgress.getMajorCategory());
            row.createCell(2).setCellValue(devProgress.getSubCategory());
            row.createCell(3).setCellValue(devProgress.getMinorCategory());
            row.createCell(4).setCellValue(devProgress.getProgramDetailType());
            row.createCell(5).setCellValue(devProgress.getProgramType());
            row.createCell(6).setCellValue(devProgress.getProgramId());
            row.createCell(7).setCellValue(devProgress.getProgramName());
            row.createCell(8).setCellValue(devProgress.getClassName());
            row.createCell(9).setCellValue(devProgress.getScreenId());
            row.createCell(10).setCellValue(devProgress.getScreenName());
            row.createCell(11).setCellValue(devProgress.getScreenMenuPath());
            row.createCell(12).setCellValue(devProgress.getPriority());
            row.createCell(13).setCellValue(devProgress.getDifficulty());
            row.createCell(14).setCellValue(devProgress.getEstimatedEffort());
            row.createCell(15).setCellValue(devProgress.getProgramStatus());
            row.createCell(16).setCellValue(devProgress.getReqId());
            row.createCell(17).setCellValue(devProgress.getDeletionHandler());
            row.createCell(18).setCellValue(devProgress.getDeletionDate());
            row.createCell(19).setCellValue(devProgress.getDeletionReason());
            row.createCell(20).setCellValue(devProgress.getDeveloper());
            row.createCell(21).setCellValue(devProgress.getPlannedStartDate());
            row.createCell(22).setCellValue(devProgress.getPlannedEndDate());
            row.createCell(23).setCellValue(devProgress.getActualStartDate());
            row.createCell(24).setCellValue(devProgress.getActualEndDate());
            row.createCell(25).setCellValue(devProgress.getPl());
            row.createCell(26).setCellValue(devProgress.getPlTestScdDate());
            row.createCell(27).setCellValue(devProgress.getPlTestCmpDate());
            row.createCell(28).setCellValue(devProgress.getPlTestResult());
            row.createCell(29).setCellValue(devProgress.getPlTestNotes());
            row.createCell(30).setCellValue(devProgress.getItMgr());
            row.createCell(31).setCellValue(devProgress.getItTestDate());
            row.createCell(32).setCellValue(devProgress.getItConfirmDate());
            row.createCell(33).setCellValue(devProgress.getItTestResult());
            row.createCell(34).setCellValue(devProgress.getItTestNotes());
            row.createCell(35).setCellValue(devProgress.getBusiMgr());
            row.createCell(36).setCellValue(devProgress.getBusiTestDate());
            row.createCell(37).setCellValue(devProgress.getBusiConfirmDate());
            row.createCell(38).setCellValue(devProgress.getBusiTestResult());
            row.createCell(39).setCellValue(devProgress.getBusiTestNotes());
            row.createCell(40).setCellValue(devProgress.getThirdPartyTestMgr());
            row.createCell(41).setCellValue(devProgress.getThirdPartyTestDate());
            row.createCell(42).setCellValue(devProgress.getThirdPartyConfirmDate());
            row.createCell(43).setCellValue(devProgress.getThirdTestResult());
            row.createCell(44).setCellValue(devProgress.getThirdPartyTestNotes());
            row.createCell(45).setCellValue(devProgress.getDevStatus());
            row.createCell(46).setCellValue(devProgress.getInitRegDate().toString());
            row.createCell(47).setCellValue(devProgress.getInitRegistrar());
            row.createCell(48).setCellValue(devProgress.getLastModifiedDate().toString());
            row.createCell(49).setCellValue(devProgress.getLastModifier());
        }

        // ���� ������ HTTP �������� ��������
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        try (OutputStream os = response.getOutputStream()) {
            workbook.write(os);
        }
        workbook.close();
    }
  
    @PostMapping(value = "api/devupload", produces = MediaType.APPLICATION_JSON_VALUE)
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
            List<String> expectedHeaders = Arrays.asList("SEQ", "MAJOR_CATEGORY", "SUB_CATEGORY", "MINOR_CATEGORY", 
            		"PROGRAM_DETAIL_TYPE", "PROGRAM_TYPE", "PROGRAM_ID", "PROGRAM_NAME", "CLASS_NAME", "SCREEN_ID", 
            		"SCREEN_NAME", "SCREEN_MENU_PATH", "PRIORITY", "DIFFICULTY", "ESTIMATED_EFFORT", "REG_STATUS", 
            		"REQ_ID", "DELETION_HANDLER", "DELETION_DATE", "DELETION_REASON", "DEVELOPER", "PLANNED_START_DATE", 
            		"PLANNED_END_DATE", "ACTUAL_START_DATE", "ACTUAL_END_DATE", "PL", "PL_TEST_SCD_DATE", 
            		"PL_TEST_CMP_DATE", "PL_TEST_RESULT", "PL_TEST_NOTES", "IT_MGR", "IT_TEST_DATE", "IT_CONFIRM_DATE", 
            		"IT_TEST_RESULT", "IT_TEST_NOTES", "BUSI_MGR", "BUSI_TEST_DATE", "BUSI_CONFIRM_DATE", "BUSI_TEST_RESULT", 
            		"BUSI_TEST_NOTES", "THIRD_PARTY_TEST_MGR", "THIRD_PARTY_TEST_DATE", "THIRD_PARTY_CONFIRM_DATE", 
            		"THIRD_TEST_RESULT", "THIRD_PARTY_TEST_NOTES", "DEV_STATUS", "INIT_REG_DATE", "INIT_REGISTRAR", 
            		"LAST_MODIFIED_DATE", "LAST_MODIFIER");
            if (!isHeaderValid4(headerRow, expectedHeaders)) {
                response.put("status", "error");
                response.put("message", "����� �÷����� �ùٸ��� �ʽ��ϴ�.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            List<devProgress> devProgress = new ArrayList<>();

            // ù ��° ���� ����̹Ƿ� �ǳʶݴϴ�.
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    devProgress devprogress = new devProgress();
                    try {
                    	devprogress.setSeq((int) row.getCell(0).getNumericCellValue());
                    	devprogress.setMajorCategory(row.getCell(1).getStringCellValue());
                    	devprogress.setSubCategory(row.getCell(2).getStringCellValue());
                    	devprogress.setMinorCategory(row.getCell(3).getStringCellValue());
                    	devprogress.setProgramDetailType(row.getCell(4).getStringCellValue());
                    	devprogress.setProgramType(row.getCell(5).getStringCellValue());
                    	devprogress.setProgramId(row.getCell(6).getStringCellValue());
                    	devprogress.setProgramName(row.getCell(7).getStringCellValue());
                    	devprogress.setClassName(row.getCell(8).getStringCellValue());
                    	devprogress.setScreenId(row.getCell(9).getStringCellValue());
                    	devprogress.setScreenName(row.getCell(10).getStringCellValue());
                    	devprogress.setScreenMenuPath(row.getCell(11).getStringCellValue());
                    	devprogress.setPriority(row.getCell(12).getStringCellValue());
                    	devprogress.setDifficulty(row.getCell(13).getStringCellValue());
                    	devprogress.setEstimatedEffort((int) row.getCell(14).getNumericCellValue());
                    	devprogress.setProgramStatus(row.getCell(15).getStringCellValue());
                    	devprogress.setReqId(row.getCell(16).getStringCellValue());
                    	devprogress.setDeletionHandler(row.getCell(17).getStringCellValue());
                    	devprogress.setDeletionDate(row.getCell(18).getStringCellValue());
                    	devprogress.setDeletionReason(row.getCell(19).getStringCellValue());
                    	devprogress.setDeveloper(row.getCell(20).getStringCellValue());
                    	devprogress.setPlannedStartDate(row.getCell(21).getStringCellValue());
                    	devprogress.setPlannedEndDate(row.getCell(22).getStringCellValue());
                    	devprogress.setActualStartDate(row.getCell(23).getStringCellValue());
                    	devprogress.setActualEndDate(row.getCell(24).getStringCellValue());
                    	devprogress.setPl(row.getCell(25).getStringCellValue());
                    	devprogress.setPlTestScdDate(row.getCell(26).getStringCellValue());
                    	devprogress.setPlTestCmpDate(row.getCell(27).getStringCellValue());
                    	devprogress.setPlTestResult(row.getCell(28).getStringCellValue());
                    	devprogress.setPlTestNotes(row.getCell(29).getStringCellValue());
                    	devprogress.setItMgr(row.getCell(30).getStringCellValue());
                    	devprogress.setItTestDate(row.getCell(31).getStringCellValue());
                    	devprogress.setItConfirmDate(row.getCell(32).getStringCellValue());
                    	devprogress.setItTestResult(row.getCell(33).getStringCellValue());
                    	devprogress.setItTestNotes(row.getCell(34).getStringCellValue());
                    	devprogress.setBusiMgr(row.getCell(35).getStringCellValue());
                    	devprogress.setBusiTestDate(row.getCell(36).getStringCellValue());
                    	devprogress.setBusiConfirmDate(row.getCell(37).getStringCellValue());
                    	devprogress.setBusiTestResult(row.getCell(38).getStringCellValue());
                    	devprogress.setBusiTestNotes(row.getCell(39).getStringCellValue());
                    	devprogress.setThirdPartyTestMgr(row.getCell(40).getStringCellValue());
                    	devprogress.setThirdPartyTestDate(row.getCell(41).getStringCellValue());
                    	devprogress.setThirdPartyConfirmDate(row.getCell(42).getStringCellValue());
                    	devprogress.setThirdTestResult(row.getCell(43).getStringCellValue());
                    	devprogress.setThirdPartyTestNotes(row.getCell(44).getStringCellValue());
                    	devprogress.setDevStatus(row.getCell(45).getStringCellValue());
                    	devprogress.setInitRegDate(row.getCell(46).getStringCellValue());
                    	devprogress.setInitRegistrar(row.getCell(47).getStringCellValue());
                    	devprogress.setLastModifiedDate(row.getCell(48).getStringCellValue());
                    	devprogress.setLastModifier(row.getCell(49).getStringCellValue());
                        devProgress.add(devprogress);
                    } catch (Exception e) {
                        e.printStackTrace();
                        response.put("status", "error");
                        response.put("message", "������ ������ ������ �ùٸ��� �ʽ��ϴ�: �� " + (i + 1));
                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }
                }
            }

            // �����ͺ��̽��� ����
            devservice.saveAllDevProgress(devProgress);
            response.put("status", "success");
            response.put("message", "���� ���ε尡 ���������� �Ϸ�Ǿ����ϴ�!");
            response.put("totalUploaded", devProgress.size());

        } catch (DuplicateKeyException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "�ߺ��� ���� ��Ȳ ������ �߰ߵǾ����ϴ�.");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        } catch (IOException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "���� ó�� �� ������ �߻��߽��ϴ�. �ٽ� �õ��� �ּ���.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    
    
    private boolean isHeaderValid4(Row headerRow, List<String> expectedHeaders) {
        for (int i = 0; i < expectedHeaders.size(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell == null || !cell.getStringCellValue().trim().equalsIgnoreCase(expectedHeaders.get(i))) {
                return false;
            }
        }
        return true;
    }
    
//    @PostMapping(value = "/devupload")
//    public String catuploadExcelFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, Model model) {
//        if (file.isEmpty()) {
//        	model.addAttribute("status", "failure");
//            model.addAttribute("message", "������ �����ϴ�. �ٽ� �õ��� �ּ���.");
//            return "devProgress";  // ����� ������ JSP ������ �̸�
//        }
//
//        try (InputStream inputStream = file.getInputStream()) {
//            Workbook workbook = WorkbookFactory.create(inputStream);
//            Sheet sheet = workbook.getSheetAt(0);
//            Row headerRow = sheet.getRow(0);
//
//            // �����ϴ� �÷��� ����Ʈ
//            List<String> expectedHeaders = Arrays.asList("SEQ", "MAJOR_CATEGORY", "SUB_CATEGORY", "MINOR_CATEGORY", 
//            		"PROGRAM_DETAIL_TYPE", "PROGRAM_TYPE", "PROGRAM_ID", "PROGRAM_NAME", "CLASS_NAME", "SCREEN_ID", 
//            		"SCREEN_NAME", "SCREEN_MENU_PATH", "PRIORITY", "DIFFICULTY", "ESTIMATED_EFFORT", "REG_STATUS", 
//            		"REQ_ID", "DELETION_HANDLER", "DELETION_DATE", "DELETION_REASON", "DEVELOPER", "PLANNED_START_DATE", 
//            		"PLANNED_END_DATE", "ACTUAL_START_DATE", "ACTUAL_END_DATE", "PL", "PL_TEST_SCD_DATE", 
//            		"PL_TEST_CMP_DATE", "PL_TEST_RESULT", "PL_TEST_NOTES", "IT_MGR", "IT_TEST_DATE", "IT_CONFIRM_DATE", 
//            		"IT_TEST_RESULT", "IT_TEST_NOTES", "BUSI_MGR", "BUSI_TEST_DATE", "BUSI_CONFIRM_DATE", "BUSI_TEST_RESULT", 
//            		"BUSI_TEST_NOTES", "THIRD_PARTY_TEST_MGR", "THIRD_PARTY_TEST_DATE", "THIRD_PARTY_CONFIRM_DATE", 
//            		"THIRD_TEST_RESULT", "THIRD_PARTY_TEST_NOTES", "DEV_STATUS", "INIT_REG_DATE", "INIT_REGISTRAR", 
//            		"LAST_MODIFIED_DATE", "LAST_MODIFIER");
//            if (!isHeaderValid4(headerRow, expectedHeaders)) {
//            	model.addAttribute("status", "error");
//                model.addAttribute("message", "����� �÷����� �ùٸ��� �ʽ��ϴ�.");
//                return "devProgress";  // ����� ������ JSP ������ �̸�
//            }
//
//            List<devProgress> devProgress = new ArrayList<>();
//
//            // ù ��° ���� ����̹Ƿ� �ǳʶݴϴ�.
//            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
//                Row row = sheet.getRow(i);
//                if (row != null) {
//                    devProgress devprogress = new devProgress();
//                    try {
//                    	devprogress.setSeq((int) row.getCell(0).getNumericCellValue());
//                    	devprogress.setMajorCategory(row.getCell(1).getStringCellValue());
//                    	devprogress.setSubCategory(row.getCell(2).getStringCellValue());
//                    	devprogress.setMinorCategory(row.getCell(3).getStringCellValue());
//                    	devprogress.setProgramDetailType(row.getCell(4).getStringCellValue());
//                    	devprogress.setProgramType(row.getCell(5).getStringCellValue());
//                    	devprogress.setProgramId(row.getCell(6).getStringCellValue());
//                    	devprogress.setProgramName(row.getCell(7).getStringCellValue());
//                    	devprogress.setClassName(row.getCell(8).getStringCellValue());
//                    	devprogress.setScreenId(row.getCell(9).getStringCellValue());
//                    	devprogress.setScreenName(row.getCell(10).getStringCellValue());
//                    	devprogress.setScreenMenuPath(row.getCell(11).getStringCellValue());
//                    	devprogress.setPriority(row.getCell(12).getStringCellValue());
//                    	devprogress.setDifficulty(row.getCell(13).getStringCellValue());
//                    	devprogress.setEstimatedEffort((int) row.getCell(14).getNumericCellValue());
//                    	devprogress.setprogramStatus(row.getCell(15).getStringCellValue());
//                    	devprogress.setReqId(row.getCell(16).getStringCellValue());
//                    	devprogress.setDeletionHandler(row.getCell(17).getStringCellValue());
//                    	devprogress.setDeletionDate(row.getCell(18).getStringCellValue());
//                    	devprogress.setDeletionReason(row.getCell(19).getStringCellValue());
//                    	devprogress.setDeveloper(row.getCell(20).getStringCellValue());
//                    	devprogress.setPlannedStartDate(row.getCell(21).getStringCellValue());
//                    	devprogress.setPlannedEndDate(row.getCell(22).getStringCellValue());
//                    	devprogress.setActualStartDate(row.getCell(23).getStringCellValue());
//                    	devprogress.setActualEndDate(row.getCell(24).getStringCellValue());
//                    	devprogress.setPl(row.getCell(25).getStringCellValue());
//                    	devprogress.setPlTestScdDate(row.getCell(26).getStringCellValue());
//                    	devprogress.setPlTestCmpDate(row.getCell(27).getStringCellValue());
//                    	devprogress.setPlTestResult(row.getCell(28).getStringCellValue());
//                    	devprogress.setPlTestNotes(row.getCell(29).getStringCellValue());
//                    	devprogress.setItMgr(row.getCell(30).getStringCellValue());
//                    	devprogress.setItTestDate(row.getCell(31).getStringCellValue());
//                    	devprogress.setItConfirmDate(row.getCell(32).getStringCellValue());
//                    	devprogress.setItTestResult(row.getCell(33).getStringCellValue());
//                    	devprogress.setItTestNotes(row.getCell(34).getStringCellValue());
//                    	devprogress.setBusiMgr(row.getCell(35).getStringCellValue());
//                    	devprogress.setBusiTestDate(row.getCell(36).getStringCellValue());
//                    	devprogress.setBusiConfirmDate(row.getCell(37).getStringCellValue());
//                    	devprogress.setBusiTestResult(row.getCell(38).getStringCellValue());
//                    	devprogress.setBusiTestNotes(row.getCell(39).getStringCellValue());
//                    	devprogress.setThirdPartyTestMgr(row.getCell(40).getStringCellValue());
//                    	devprogress.setThirdPartyTestDate(row.getCell(41).getStringCellValue());
//                    	devprogress.setThirdPartyConfirmDate(row.getCell(42).getStringCellValue());
//                    	devprogress.setThirdTestResult(row.getCell(43).getStringCellValue());
//                    	devprogress.setThirdPartyTestNotes(row.getCell(44).getStringCellValue());
//                    	devprogress.setDevStatus(row.getCell(45).getStringCellValue());
//                    	devprogress.setInitRegDate(row.getCell(46).getStringCellValue());
//                    	devprogress.setInitRegistrar(row.getCell(47).getStringCellValue());
//                    	devprogress.setLastModifiedDate(row.getCell(48).getStringCellValue());
//                    	devprogress.setLastModifier(row.getCell(49).getStringCellValue());
//                        devProgress.add(devprogress);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        model.addAttribute("status", "error");
//                        model.addAttribute("message", "������ ������ ������ �ùٸ��� �ʽ��ϴ�: �� " + (i + 1));
//                        return "devProgress";  // ����� ������ JSP ������ �̸�
//                    }
//                }
//            }
//
//            // �����ͺ��̽��� ����
//            devservice.saveAllDevProgress(devProgress);
//            model.addAttribute("status", "sucess");
//            model.addAttribute("message", "����");
//            model.addAttribute("totalUploaded", devProgress.size());
//
//        } catch (DuplicateKeyException e) {
//            e.printStackTrace();
//            model.addAttribute("status", "error");
//            model.addAttribute("message", "error");
//            return "devProgress";  // ����� ������ JSP ������ �̸�
//        } catch (IOException e) {
//            e.printStackTrace();
//            model.addAttribute("status", "error");
//            model.addAttribute("message", "error");
//            return "devProgress";  // ����� ������ JSP ������ �̸�
//        }
//
//        return "devProgress";
//    }
    
   
    
    



}
