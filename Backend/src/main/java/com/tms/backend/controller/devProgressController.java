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
	
	//개발진행관리 Controller
	
	@GetMapping("/devProgress")
	public String devProgressPage(
	        @RequestParam(value = "majorCategory", required = false) String majorCategory,
	        @RequestParam(value = "subCategory", required = false) String subCategory,
	        @RequestParam(value = "programType", required = false) String programType,
	        @RequestParam(value = "programName", required = false) String programName,
	        @RequestParam(value = "programId", required = false) String programId,
	        @RequestParam(value = "regStatus", required = false) String regStatus,
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

	    // Service를 통해 데이터를 조회
	    List<devProgress> devProgressList = devservice.searchDevProgress(
	            majorCategory, subCategory, programType, programName,programId, regStatus, developer,
	            devStatus, actualStartDate, actualEndDate, pl, thirdPartyTestMgr, ItMgr, BusiMgr, page, size
	    );
	    
	    List<User> developers = adminService.findDevlopers();
	    log.info("개발자 목록"+developers);

	    int totalDevProgress = devservice.getTotalDevProgressCount(
	            majorCategory, subCategory, programType, programName,programId, regStatus, developer,
	            devStatus, actualStartDate, actualEndDate, pl, thirdPartyTestMgr, ItMgr, BusiMgr
	    );

	    int totalPages = (int) Math.ceil((double) totalDevProgress / size);
	    
	    log.info(devProgressList);

	    // 모델에 데이터를 추가
	    model.addAttribute("devProgressList", devProgressList);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", totalPages);
	    model.addAttribute("totalDevProgress", totalDevProgress);

	    return "devProgress"; // JSP 페이지로 이동
	}
	
	//개발 진행 현황 조회
	@GetMapping(value="api/devProgress" , produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> APIdevProgressPage(
	        @RequestParam(value = "majorCategory", required = false) String majorCategory,
	        @RequestParam(value = "subCategory", required = false) String subCategory,
	        @RequestParam(value = "programType", required = false) String programType,
	        @RequestParam(value = "programName", required = false) String programName,
	        @RequestParam(value = "programId", required = false) String programId,
	        @RequestParam(value = "regStatus", required = false) String regStatus,
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

	    // Service를 통해 데이터를 조회
	    List<devProgress> devProgressList = devservice.searchDevProgress(
	            majorCategory, subCategory, programType, programName, programId, regStatus, developer,
	            devStatus, actualStartDate, actualEndDate, pl, thirdPartyTestMgr, ItMgr, BusiMgr, page, size
	    );

	    int totalDevProgress = devservice.getTotalDevProgressCount(
	            majorCategory, subCategory, programType, programName, programId, regStatus, developer,
	            devStatus, actualStartDate, actualEndDate, pl, thirdPartyTestMgr, ItMgr, BusiMgr
	    );

	    int totalPages = (int) Math.ceil((double) totalDevProgress / size);
	    
	    // 응답 생성
	    Map<String, Object> response = new HashMap<>();
	    response.put("devProgressList", devProgressList);
	    response.put("currentPage", page);
	    response.put("totalPages", totalPages);
	    response.put("totalDevProgress", totalDevProgress);

	    return ResponseEntity.ok(response); // JSON으로 응답 반환
	}
	
	@GetMapping(value = "api/devuser", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDevUser() {
    	Map<String, Object> response = new HashMap<>();
        
        // CategoryService를 통해 대분류 코드를 조회
        List<User> developer = adminService.findDevlopers();
        
        log.info("확인중" + developer);

        // 응답 데이터 생성
        if (developer != null && !developer.isEmpty()) {
            response.put("status", "success");
            response.put("developer", developer);
        } else {
            response.put("status", "failure");
            response.put("message", "No developer found.");
        }

        // 조회된 결과를 반환
        return ResponseEntity.ok(response);
    }
	
	//개발 진행 현황 수정
	@PostMapping(value= "api/devmodify" , produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> DevModify(@RequestBody devProgress devprogress,
    		@RequestPart(value = "file", required = false) MultipartFile[] files,  // 파일
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
//        // 개발 진행 현황 존재 여부 확인
//        devProgress devprog = devservice.get(devprogress.getSeq());
//        if (devprog == null) {
//            response.put("message", "해당 개발 진행 현황이 존재하지 않습니다.");
//            return ResponseEntity.status(404).body(response);
//        }
//
//        // 기존 공지사항 업데이트
//        devprog.setTitle(devprogress.get());
//        devprog.setContent(devprogress.getContent());
//        devprog.setPostDate(devprogress.getPostDate());
//
//        // 공지사항에 등록된 기존 첨부파일 전부 삭제
//        adminService.deleteAttachmentsByNoticeId(existingNotice.getSeq());
//
//        // 새로운 파일 업로드 처리
//        handleFileUpload(files, existingNotice, boardType);
//        
//        log.info("check "+existingNotice);
//
//        // 업데이트된 공지사항을 저장
//        adminService.updateNotice(existingNotice);
//        
//
//        // 응답 생성
//        response.put("status", "success");
//        response.put("message", "게시글이 성공적으로 수정되었습니다.");
//        response.put("notice", existingNotice);

        return ResponseEntity.ok(response);
    }
	
	
	//개발 진행 현황 삭제
	@DeleteMapping(value= "api/deletedev", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deletedev(@RequestBody List<String> seqs) {
        Map<String, Object> response = new HashMap<>();
        
        for (String seq : seqs) {
            devservice.deleteDevProgress(seq);
        }
        
        response.put("status", "success");
        response.put("message", "공통코드가 성공적으로 삭제되었습니다.");
        return ResponseEntity.ok(response);
    }
	
	// 전체 개발 진행 현황 정보를 엑셀로 다운로드
    @GetMapping("/devdownloadAll")
    public void downloadAlldev(HttpServletResponse response) throws IOException {
        List<devProgress> DevCodeList = devservice.searchDevProgress(null, null, null, null, null, null, null, null, null, null, null, null, null, null, 1, Integer.MAX_VALUE);
        log.info(DevCodeList);
        devexportToExcel(response, DevCodeList, "all_dev_codes.xlsx");
    }
    
    // 조회된 개발 진행 현황 정보를 엑셀로 다운로드
    @GetMapping("/devdownloadFiltered")
    public void downloadFiltereddev(
    		@RequestParam(value = "majorCategory", required = false) String majorCategory,
	        @RequestParam(value = "subCategory", required = false) String subCategory,
	        @RequestParam(value = "programType", required = false) String programType,
	        @RequestParam(value = "programName", required = false) String programName,
	        @RequestParam(value = "programId", required = false) String programId,
	        @RequestParam(value = "regStatus", required = false) String regStatus,
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
    	
    	log.info("확인중"+subCategory);
    	List<devProgress> filteredDevCodeList = devservice.searchDevProgress(
	            majorCategory, subCategory, programType, programName,programId, regStatus, developer,
	            devStatus, actualStartDate, actualEndDate, pl, thirdPartyTestMgr, ItMgr, BusiMgr, page, size
	    );
    	log.info("확인중"+filteredDevCodeList);
        devexportToExcel(response, filteredDevCodeList, "filtered_dev_codes.xlsx");
    }

    // 엑셀 파일로 데이터를 내보내는 메서드
    private void devexportToExcel(HttpServletResponse response, List<devProgress> devProgressList, String fileName) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("DevProgress");

        // 헤더 생성
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
        headerRow.createCell(15).setCellValue("REG_STATUS");
        headerRow.createCell(16).setCellValue("REQ_ID");
        headerRow.createCell(17).setCellValue("DELETION_HANDLER");
        headerRow.createCell(18).setCellValue("DELETION_DATE");
        headerRow.createCell(19).setCellValue("DELETION_REASON");
        headerRow.createCell(20).setCellValue("DEVELOPER");
        headerRow.createCell(21).setCellValue("PLANNED_START_DATE");
        headerRow.createCell(22).setCellValue("PLANNED_END_DATE");
        headerRow.createCell(23).setCellValue("ACTUAL_START_DATE");
        headerRow.createCell(24).setCellValue("ACTUAL_END_DATE");
        headerRow.createCell(25).setCellValue("DEV_PROG_ATTACHMENT");
        headerRow.createCell(26).setCellValue("PL");
        headerRow.createCell(27).setCellValue("PL_TEST_SCD_DATE");
        headerRow.createCell(28).setCellValue("PL_TEST_CMP_DATE");
        headerRow.createCell(29).setCellValue("PL_TEST_RESULT");
        headerRow.createCell(30).setCellValue("PL_TEST_NOTES");
        headerRow.createCell(31).setCellValue("IT_MGR");
        headerRow.createCell(32).setCellValue("IT_TEST_DATE");
        headerRow.createCell(33).setCellValue("IT_CONFIRM_DATE");
        headerRow.createCell(34).setCellValue("IT_TEST_RESULT");
        headerRow.createCell(35).setCellValue("IT_TEST_NOTES");
        headerRow.createCell(36).setCellValue("BUSI_MGR");
        headerRow.createCell(37).setCellValue("BUSI_TEST_DATE");
        headerRow.createCell(38).setCellValue("BUSI_CONFIRM_DATE");
        headerRow.createCell(39).setCellValue("BUSI_TEST_RESULT");
        headerRow.createCell(40).setCellValue("BUSI_TEST_NOTES");
        headerRow.createCell(41).setCellValue("THIRD_PARTY_TEST_MGR");
        headerRow.createCell(42).setCellValue("THIRD_PARTY_TEST_DATE");
        headerRow.createCell(43).setCellValue("THIRD_PARTY_CONFIRM_DATE");
        headerRow.createCell(44).setCellValue("THIRD_TEST_RESULT");
        headerRow.createCell(45).setCellValue("THIRD_PARTY_TEST_NOTES");
        headerRow.createCell(46).setCellValue("DEV_STATUS");
        headerRow.createCell(47).setCellValue("INIT_REG_DATE");
        headerRow.createCell(48).setCellValue("INIT_REGISTRAR");
        headerRow.createCell(49).setCellValue("LAST_MODIFIED_DATE");
        headerRow.createCell(50).setCellValue("LAST_MODIFIER");

        // 데이터 행 생성
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
            row.createCell(15).setCellValue(devProgress.getRegStatus());
            row.createCell(16).setCellValue(devProgress.getReqId());
            row.createCell(17).setCellValue(devProgress.getDeletionHandler());
            row.createCell(18).setCellValue(devProgress.getDeletionDate());
            row.createCell(19).setCellValue(devProgress.getDeletionReason());
            row.createCell(20).setCellValue(devProgress.getDeveloper());
            row.createCell(21).setCellValue(devProgress.getPlannedStartDate());
            row.createCell(22).setCellValue(devProgress.getPlannedEndDate());
            row.createCell(23).setCellValue(devProgress.getActualStartDate());
            row.createCell(24).setCellValue(devProgress.getActualEndDate());
            row.createCell(25).setCellValue(devProgress.getDevProgAttachment());
            row.createCell(26).setCellValue(devProgress.getPl());
            row.createCell(27).setCellValue(devProgress.getPlTestScdDate());
            row.createCell(28).setCellValue(devProgress.getPlTestCmpDate());
            row.createCell(29).setCellValue(devProgress.getPlTestResult());
            row.createCell(30).setCellValue(devProgress.getPlTestNotes());
            row.createCell(31).setCellValue(devProgress.getItMgr());
            row.createCell(32).setCellValue(devProgress.getItTestDate());
            row.createCell(33).setCellValue(devProgress.getItConfirmDate());
            row.createCell(34).setCellValue(devProgress.getItTestResult());
            row.createCell(35).setCellValue(devProgress.getItTestNotes());
            row.createCell(36).setCellValue(devProgress.getBusiMgr());
            row.createCell(37).setCellValue(devProgress.getBusiTestDate());
            row.createCell(38).setCellValue(devProgress.getBusiConfirmDate());
            row.createCell(39).setCellValue(devProgress.getBusiTestResult());
            row.createCell(40).setCellValue(devProgress.getBusiTestNotes());
            row.createCell(41).setCellValue(devProgress.getThirdPartyTestMgr());
            row.createCell(42).setCellValue(devProgress.getThirdPartyTestDate());
            row.createCell(43).setCellValue(devProgress.getThirdPartyConfirmDate());
            row.createCell(44).setCellValue(devProgress.getThirdTestResult());
            row.createCell(45).setCellValue(devProgress.getThirdPartyTestNotes());
            row.createCell(46).setCellValue(devProgress.getDevStatus());
            row.createCell(47).setCellValue(devProgress.getInitRegDate().toString());
            row.createCell(48).setCellValue(devProgress.getInitRegistrar());
            row.createCell(49).setCellValue(devProgress.getLastModifiedDate().toString());
            row.createCell(50).setCellValue(devProgress.getLastModifier());
        }

        // 엑셀 파일을 HTTP 응답으로 내보내기
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        try (OutputStream os = response.getOutputStream()) {
            workbook.write(os);
        }
        workbook.close();
    }
    
    
    
   
    
    



}
