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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tms.backend.service.AdminService;
import com.tms.backend.service.DefectService;
import com.tms.backend.service.DevService;
import com.tms.backend.service.FileService;
import com.tms.backend.service.TestService;
import com.tms.backend.vo.CommonCode;
import com.tms.backend.vo.Defect;
import com.tms.backend.vo.FileAttachment;
import com.tms.backend.vo.User;
import com.tms.backend.vo.devProgress;
import com.tms.backend.vo.testProgress;

import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@RequestMapping("/tms/*")
public class TestController {
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private FileService fileservice;
	
	@Autowired
	private TestService testService;
	
	@Autowired
	private DevService devservice;
	
	@GetMapping("/testProgress")
	public String TestProgressPage() {
			
			return "testProgress"; // JSP 페이지 이동
	}
	
	
	@GetMapping(value= "api/testProgress", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> TestProgress(HttpServletRequest request,
			@RequestParam(value = "testStage", required = false) String testStage,
            @RequestParam(value = "majorCategory", required = false) String majorCategory,
            @RequestParam(value = "subCategory", required = false) String subCategory,
            @RequestParam(value = "programType", required = false) String programType,
            @RequestParam(value = "testId", required = false) String testId,
            @RequestParam(value = "screenId", required = false) String screenId,
            @RequestParam(value = "screenName", required = false) String screenName,
            @RequestParam(value = "programName", required = false) String programName,
	        @RequestParam(value = "programId", required = false) String programId,
	        @RequestParam(value = "developer", required = false) String developer,
	        @RequestParam(value = "ItMgr", required = false) String ItMgr,
	        @RequestParam(value = "BusiMgr", required = false) String BusiMgr,
            @RequestParam(value = "Pl", required = false) String pl,
            @RequestParam(value = "execCompanyMgr", required = false) String execCompanyMgr,
            @RequestParam(value = "thirdPartyTestMgr", required = false) String thirdPartyTestMgr,
            @RequestParam(value = "testStatus", required = false) String testStatus,
            @RequestParam(value = "page", defaultValue = "1") int page,
	        @RequestParam(value = "size", defaultValue = "15") int size) {
		
		Map<String, Object> response = new HashMap<>();
		
		majorCategory = adminService.getStageCodes("대", majorCategory);
		subCategory = adminService.getStageCodes("중", subCategory);
		testStatus = adminService.getStageCCodes("12", testStatus);
		testStage = adminService.getStageCCodes("11", testStage);
		programType = adminService.getStageCCodes("05", programType);
		
		// 결함 목록 조회
		 List<testProgress> testProgressList = testService.searchTestProgress(testStage, majorCategory, subCategory, programType, testId, screenId, screenName,
				 programName, programId, developer, testStatus, pl, ItMgr, BusiMgr, execCompanyMgr, thirdPartyTestMgr, page, size);
		 
		 log.info("테스트 명단:"+testProgressList);

	    // 총 결함 수 조회
	    int totalTest = testService.getTotalTestCount(testStage, majorCategory, subCategory, programType, testId, screenId, screenName,
				 programName, programId, developer, testStatus, pl, ItMgr, BusiMgr, execCompanyMgr, thirdPartyTestMgr);
	    // 총 페이지 수 계산
	    int totalPages = (int) Math.ceil((double) totalTest / size);
	    

		// 응답 생성
	    response.put("testProgress", testProgressList);
	    response.put("currentPage", page);
	    response.put("totalPages", totalPages);
	    response.put("totalTest", totalTest);

	    return ResponseEntity.ok(response); // JSON으로 응답 반환
	}
	
	//개발자 확인
	@GetMapping(value = "api/programDetail", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getprogramDetail(@RequestParam(value = "programId", required = false) String programId) {
    	Map<String, Object> response = new HashMap<>();
        List<devProgress> programDList = devservice.getprogramDetail(programId);
        
        log.info("확인중" + programDList);

        // 응답 데이터 생성
        if (programDList != null && !programDList.isEmpty()) {
            response.put("status", "success");
            response.put("programDList", programDList);
        } else {
            response.put("status", "failure");
            response.put("message", "프로그램 상세 정보를 찾을 수 없습니다.");
        }

        // 조회된 결과를 반환
        return ResponseEntity.ok(response);
    }
	
	//테스트 진행 상태 조회
	@GetMapping(value = "api/testStatus", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> gettestStatus() {
    	Map<String, Object> response = new HashMap<>();
    	List<CommonCode> testStatus= adminService.getCCCode("12");

        // 응답 데이터 생성
        if (testStatus != null && !testStatus.isEmpty()) {
            response.put("status", "success");
            response.put("testStatus", testStatus);
        } else {
            response.put("status", "failure");
            response.put("message", "테스트 단계 정보를 찾을 수 없습니다");
        }

        // 조회된 결과를 반환
        return ResponseEntity.ok(response);
    }
	
	//테스트 진행 현황 삭제
	@DeleteMapping(value= "api/deletetest", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deletetest(@RequestBody List<Integer> seqs) {
        Map<String, Object> response = new HashMap<>();
        
        for (int seq : seqs) {
            testService.deleteTestProgress(seq, 1);
        }
        
        response.put("status", "success");
        response.put("message", "테스트 진행 현황 정보가 성공적으로 삭제되었습니다.");
        return ResponseEntity.ok(response);
    }
	
	// 전체 개발 진행 현황 정보를 엑셀로 다운로드
    @GetMapping("/testdownloadAll")
    public void downloadAlltest(HttpServletResponse response) throws IOException {
        List<testProgress> TestCodeList = testService.searchTestProgress(null, null, null, null, null, null, null, 
        		null, null, null, null, null, null, null, null, null, 1, Integer.MAX_VALUE);
        log.info(TestCodeList);
        testexportToExcel(response, TestCodeList, "all_test_codes.xlsx");
    }
	
    // 액셀 파일 예시를 다운로드
    @GetMapping("/testexampleexcel")
    public void downloadExtest(HttpServletResponse response) throws IOException {
    	List<testProgress> ExampleTEST = testService.searchTestProgress("NO_VALUE", null, null, null, null, null, null, 
        		null, null, null, null, null, null, null, null, null, 1, Integer.MAX_VALUE);
    	testexportToExcel(response, ExampleTEST, "example.xlsx");
    }

    // 조회된 개발 진행 현황 정보를 엑셀로 다운로드
    @GetMapping("/testdownloadFiltered")
    public void downloadFilteredtest(
    		@RequestParam(value = "testStage", required = false) String testStage,
            @RequestParam(value = "majorCategory", required = false) String majorCategory,
            @RequestParam(value = "subCategory", required = false) String subCategory,
            @RequestParam(value = "programType", required = false) String programType,
            @RequestParam(value = "testId", required = false) String testId,
            @RequestParam(value = "screenId", required = false) String screenId,
            @RequestParam(value = "screenName", required = false) String screenName,
            @RequestParam(value = "programName", required = false) String programName,
	        @RequestParam(value = "programId", required = false) String programId,
	        @RequestParam(value = "developer", required = false) String developer,
	        @RequestParam(value = "ItMgr", required = false) String ItMgr,
	        @RequestParam(value = "BusiMgr", required = false) String BusiMgr,
            @RequestParam(value = "Pl", required = false) String pl,
            @RequestParam(value = "execCompanyMgr", required = false) String execCompanyMgr,
            @RequestParam(value = "thirdPartyTestMgr", required = false) String thirdPartyTestMgr,
            @RequestParam(value = "testStatus", required = false) String testStatus,
            @RequestParam(value = "page", defaultValue = "1") int page,
	        @RequestParam(value = "size", defaultValue = "15") int size,
            HttpServletResponse response) throws IOException {
    			
		majorCategory = adminService.getStageCodes("대", majorCategory);
		subCategory = adminService.getStageCodes("중", subCategory);
		testStatus = adminService.getStageCCodes("12", testStatus);
		testStage = adminService.getStageCCodes("11", testStage);
		programType = adminService.getStageCCodes("05", programType);
		
		// 결함 목록 조회
		 List<testProgress> testProgressList = testService.searchTestProgress(testStage, majorCategory, subCategory, programType, testId, screenId, screenName,
				 programName, programId, developer, testStatus, pl, ItMgr, BusiMgr, execCompanyMgr, thirdPartyTestMgr, page, size);
		 

    	log.info("확인중"+testProgressList);
        testexportToExcel(response, testProgressList, "filtered_test_codes.xlsx");
    }
	
    // 엑셀 파일로 데이터를 내보내는 메서드
    private void testexportToExcel(HttpServletResponse response, List<testProgress> testProgressList, String fileName) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("TestProgress");
        String check = "";
        log.info("체크중");
        if (testProgressList.isEmpty()) {
    		check = "no_value";
    		log.info(check);
    		}
        // 헤더 이름 배열
        String[] headers = {
        	    "SEQ", "TEST_STAGE", "MAJOR_CATEGORY", "SUB_CATEGORY", 
        	    "TEST_ID", "PROGRAM_TYPE", "PROGRAM_ID", "PROGRAM_NAME", 
        	    "DEFECT_TYPE", "DEFECT_SEVERITY", "DEFECT_DESCRIPTION", "DEFECT_REGISTRAR", 
        	    "DEFECT_DISCOVERY_DATE", "DEFECT_HANDLER", "DEFECT_SCHEDULED_DATE", 
        	    "DEFECT_COMPLETION_DATE", "DEFECT_RESOLUTION_DETAILS", "PL", 
        	    "PL_CONFIRM_DATE", "ORIGINAL_DEFECT_NUMBER", "PL_DEFECT_JUDGE_CLASS", 
        	    "PL_COMMENTS", "DEFECT_REG_CONFIRM_DATE", "DEFECT_REGISTRAR_COMMENT", 
        	    "DEFECT_STATUS", "INIT_CREATED_DATE", "INIT_CREATER", 
        	    "LAST_MODIFIED_DATE", "LAST_MODIFIER"
        	};

        // 헤더 생성
        Row headerRow = sheet.createRow(0);
        if (!check.equals("")) {
        	// "SEQ"를 무시하고 다음 헤더부터 시작
        	log.info("check"+check);
            for (int i = 1; i < headers.length; i++) {
                headerRow.createCell(i - 1).setCellValue(headers[i]);
            }
            headerRow.createCell(25).setCellValue("INIT_CREATER");
            headerRow.createCell(26).setCellValue("LAST_MODIFIER");
        }
        else {
        	// "SEQ"를 포함하여 모든 헤더 생성
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }
	        headerRow.createCell(25).setCellValue("INIT_CREATED_DATE");
	        headerRow.createCell(26).setCellValue("INIT_CREATER");
	        headerRow.createCell(27).setCellValue("LAST_MODIFIED_DATE");
	        headerRow.createCell(28).setCellValue("LAST_MODIFIER");
        }
        
        // 헤더와 데이터 매핑
        String[] fields = {
        	    "getSeq", "getTestStage", "getMajorCategory", "getSubCategory", "getMinorCategory",
        	    "getTestId", "getTestScenarioName", "getTestCaseName", "getTestStepName",
        	    "getScreenId", "getScreenName", "getProgramType", "getProgramId", "getProgramName",
        	    "getScreenMenuPath", "getExecuteProcedure", "getInputData", "getExpectedResult", "getActualResult",
        	    "getDeveloper", "getPl", "getExecCompanyMgr", "getExecCompanyTestDate", "getExecCompanyConfirmDate", 
        	    "getExecCompanyTestResult", "getExecCompanyTestNotes", "getThirdPartyTestMgr", 
        	    "getThirdPartyTestDate", "getThirdPartyConfirmDate", "getThirdTestResult", 
        	    "getThirdPartyTestNotes", "getItMgr", "getItTestDate", "getItConfirmDate", 
        	    "getItTestResult", "getItTestNotes", "getBusiMgr", "getBusiTestDate", 
        	    "getBusiConfirmDate", "getBusiTestResult", "getBusiTestNotes", 
        	    "getTestStatus", "getInitRegDate", "getInitRegistrar", 
        	    "getLastModifiedDate", "getLastModifier"
        	};

        // 데이터 행 생성
        int rowNum = 1;
        for (testProgress testProgress : testProgressList) {
            Row row = sheet.createRow(rowNum++);
            for (int i = 0; i < fields.length; i++) {
                try {
                    Method method = testProgress.getClass().getMethod(fields[i]);
                    Object value = method.invoke(testProgress);
                    row.createCell(i).setCellValue(value != null ? value.toString() : "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (check != "") {
	            row.createCell(25).setCellValue(testProgress.getInitRegistrar());
	            row.createCell(26).setCellValue(testProgress.getLastModifier());
            }
            else {
            	row.createCell(25).setCellValue(testProgress.getInitRegDate().toString());
	            row.createCell(26).setCellValue(testProgress.getInitRegistrar());
	            row.createCell(27).setCellValue(testProgress.getLastModifiedDate().toString());
	            row.createCell(28).setCellValue(testProgress.getLastModifier());
            }
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
