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
import java.util.function.Consumer;

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
		programType = adminService.getStageCCodes("02", programType);
		
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
	
	//테스트 현황 등록
	@GetMapping("/testProgressReg")
	public String testProgressPage() {

	    return "testProgressReg"; // JSP 페이지로 이동
	}
	

	//개발 진행 현황 등록 페이지
	@PostMapping(value="api/testProgressReg" , produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> testProgressReg(HttpServletRequest request,
			@RequestPart("testProgress") testProgress testProgress,
			@RequestPart(value = "execfile", required = false) MultipartFile[] execfiles,
			@RequestPart(value = "thirdfile", required = false) MultipartFile[] thirdfiles) {
		HttpSession session = request.getSession(false); // 세션이 없다면 새로 만들지 않음
		if (session == null || session.getAttribute("authorityCode") == null) {
			// 세션이 없거나 authorityCode가 없으면 401 Unauthorized 반환
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "권한이 없습니다. 로그인하세요."));
			}
		String UserName = (String) session.getAttribute("name");
		Map<String, Object> response = new HashMap<>();
		try {
			//완료일 등록 시 테스트결과 미등록 체크
			EndDateResultCheck(testProgress.getExecCompanyConfirmDate(), testProgress.getExecCompanyTestResult(), "수행사");
			EndDateResultCheck(testProgress.getThirdPartyConfirmDate(), testProgress.getThirdTestResult(), "제3자");
			EndDateResultCheck(testProgress.getItConfirmDate(), testProgress.getItTestResult(), "고객IT");
			EndDateResultCheck(testProgress.getBusiConfirmDate(), testProgress.getBusiTestResult(), "고객현업");
			
			//테스트 완료일 등록인데 테스트 시작일 미등록인 경우
			if ((testProgress.getItConfirmDate() != null) && (testProgress.getItTestDate() == null)){
	        	testProgress.setItTestDate(testProgress.getItConfirmDate());
	        }
			if ((testProgress.getBusiConfirmDate() != null) && (testProgress.getBusiTestDate() == null)){
	        	testProgress.setBusiTestDate(testProgress.getBusiConfirmDate());
	        }
			
			// 데이터 체크 자동 세팅 - 테스트 예정이 Null일 경우 테스트 완료일을 대입 
	        setIfNullDate2(testProgress.getExecCompanyConfirmDate(), testProgress.getExecCompanyTestDate(), testProgress::setExecCompanyTestDate);
	        setIfNullDate2(testProgress.getThirdPartyConfirmDate(), testProgress.getThirdPartyTestDate(), 
	        			testProgress::setThirdPartyTestDate);
	        setIfNullDate2(testProgress.getItConfirmDate(), testProgress.getItTestDate(), testProgress::setItTestDate);
	        setIfNullDate2(testProgress.getBusiConfirmDate(), testProgress.getBusiTestDate(), testProgress::setBusiTestDate);
			
			//코드로 들어오는 데이터를 코드명으로 변경
	        testProgress.setMajorCategory(adminService.getStageCodes("대", testProgress.getMajorCategory()));
			testProgress.setSubCategory(adminService.getStageCodes("중", testProgress.getSubCategory()));
			testProgress.setProgramType(adminService.getStageCCodes("02", testProgress.getProgramType()));
			testProgress.setTestStatus(adminService.getStageCCodes("12", testProgress.getTestStatus()));
			testProgress.setTestStage(adminService.getStageCCodes("11", testProgress.getTestStage()));
			testProgress.setBusiTestResult(adminService.getStageCCodes("07", testProgress.getBusiTestResult()));
			testProgress.setExecCompanyTestResult(adminService.getStageCCodes("07", testProgress.getExecCompanyTestResult()));
			testProgress.setItTestResult(adminService.getStageCCodes("07", testProgress.getItTestResult()));
			testProgress.setThirdTestResult(adminService.getStageCCodes("07", testProgress.getThirdTestResult()));
			
	        //최초 등록자, 변경자 로그인 ID 세팅
			testProgress.setInitRegistrar(UserName);
			testProgress.setLastModifier(UserName);
			testService.inserttestProgress(testProgress);  // 개발 현황 진행 정보 추가
			
			// 새로운 파일 업로드 처리
            if (execfiles != null && execfiles.length > 0) {
            	log.info("첨부중");
            	fileservice.handleFileUpload(execfiles, "testProgress", testProgress.getSeq());
            }
            if (thirdfiles != null && thirdfiles.length > 0) {
            	log.info("첨부중");
                fileservice.handleFileUpload(thirdfiles, "testProgressThird", testProgress.getSeq());
            }
            
            //첨부파일 등록
            List<FileAttachment> attachments = adminService.getAttachments(testProgress.getSeq(),21);
            testProgress.setExecCompanyAttachments(attachments);
            List<FileAttachment> fixAttachments = adminService.getAttachments(testProgress.getSeq(),22);
            testProgress.setThirdAttachments(fixAttachments);
        	
            response.put("status", "success");
            response.put("message", "개발 진행 현황 정보가 등록되었습니다");
            response.put("testProgress", testProgress);  // 등록된 개발 현황 진행 정보 반환
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.put("status", "failure");
            response.put("message", e.getMessage());  // 예외 메시지를 response에 포함시킴
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "테스트 현황 정보 등록 중에 오류가 발생했습니다.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
		programType = adminService.getStageCCodes("02", programType);
		
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
        		"SEQ", "TEST_STAGE", "MAJOR_CATEGORY", "SUB_CATEGORY", "MINOR_CATEGORY", 
        	    "TEST_ID", "TEST_SCENARIO_NAME", "TEST_CASE_NAME", "TEST_STEP_NAME", 
        	    "SCREEN_ID", "SCREEN_NAME", "PROGRAM_TYPE", "PROGRAM_ID", "PROGRAM_NAME", 
        	    "SCREEN_MENU_PATH", "EXECUTE_PROCEDURE", "INPUT_DATA", "PRECONDITIONS", "EXPECTED_RESULT", 
        	    "ACTUAL_RESULT", "DEVELOPER", "PL", "EXEC_COMPANY_MGR", "EXEC_COMPANY_TEST_DATE", 
        	    "EXEC_COMPANY_CONFIRM_DATE", "EXEC_COMPANY_TEST_RESULT", "EXEC_COMPANY_TEST_NOTES", 
        	    "THIRD_PARTY_TEST_MGR", "THIRD_PARTY_TEST_DATE", "THIRD_PARTY_CONFIRM_DATE", 
        	    "THIRD_TEST_RESULT", "THIRD_PARTY_TEST_NOTES", "IT_MGR", "IT_TEST_DATE", 
        	    "IT_CONFIRM_DATE", "IT_TEST_RESULT", "IT_TEST_NOTES", "BUSI_MGR", 
        	    "BUSI_TEST_DATE", "BUSI_CONFIRM_DATE", "BUSI_TEST_RESULT", "BUSI_TEST_NOTES", 
        	    "TEST_STATUS", "INIT_REG_DATE", "INIT_REGISTRAR", "LAST_MODIFIED_DATE", 
        	    "LAST_MODIFIER"
        	};

        // 헤더 생성
        Row headerRow = sheet.createRow(0);
        if (!check.equals("")) {
        	// "SEQ"를 무시하고 다음 헤더부터 시작
        	log.info("check"+check);
            for (int i = 1; i < headers.length-2; i++) {
                headerRow.createCell(i - 1).setCellValue(headers[i]);
            }
            headerRow.createCell(41).setCellValue("INIT_REGISTRAR");
            headerRow.createCell(42).setCellValue("LAST_MODIFIER");
        }
        else {
        	// "SEQ"를 포함하여 모든 헤더 생성
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }
	        headerRow.createCell(42).setCellValue("INIT_REG_DATE");
	        headerRow.createCell(43).setCellValue("INIT_REGISTRAR");
	        headerRow.createCell(44).setCellValue("LAST_MODIFIED_DATE");
	        headerRow.createCell(45).setCellValue("LAST_MODIFIER");
        }
        
        // 헤더와 데이터 매핑
        String[] fields = {
        	    "getSeq", "getTestStage", "getMajorCategory", "getSubCategory", "getMinorCategory",
        	    "getTestId", "getTestScenarioName", "getTestCaseName", "getTestStepName",
        	    "getScreenId", "getScreenName", "getProgramType", "getProgramId", "getProgramName",
        	    "getScreenMenuPath", "getExecuteProcedure", "getInputData", "getpreConditions", "getExpectedResult", "getActualResult",
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
            log.info(fields.length);
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
	            row.createCell(41).setCellValue(testProgress.getInitRegistrar());
	            row.createCell(42).setCellValue(testProgress.getLastModifier());
            }
            else {
            	row.createCell(42).setCellValue(testProgress.getInitRegDate().toString());
	            row.createCell(43).setCellValue(testProgress.getInitRegistrar());
	            row.createCell(44).setCellValue(testProgress.getLastModifiedDate().toString());
	            row.createCell(45).setCellValue(testProgress.getLastModifier());
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
    
    @PostMapping(value = "api/testupload", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> testuploadExcelFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        Map<String, Object> response = new HashMap<>();
        if (file.isEmpty()) {
            response.put("status", "failure");
            response.put("message", "파일이 없습니다. 다시 시도해 주세요.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            // 예상하는 컬럼명 리스트
            List<String> expectedHeaders = Arrays.asList("TEST_STAGE", "MAJOR_CATEGORY", "SUB_CATEGORY", "MINOR_CATEGORY", 
            	    "TEST_ID", "TEST_SCENARIO_NAME", "TEST_CASE_NAME", "TEST_STEP_NAME", 
            	    "SCREEN_ID", "SCREEN_NAME", "PROGRAM_TYPE", "PROGRAM_ID", "PROGRAM_NAME", 
            	    "SCREEN_MENU_PATH", "EXECUTE_PROCEDURE", "INPUT_DATA", "PRECONDITIONS", "EXPECTED_RESULT", 
            	    "ACTUAL_RESULT", "DEVELOPER", "PL", "EXEC_COMPANY_MGR", "EXEC_COMPANY_TEST_DATE", 
            	    "EXEC_COMPANY_CONFIRM_DATE", "EXEC_COMPANY_TEST_RESULT", "EXEC_COMPANY_TEST_NOTES", 
            	    "THIRD_PARTY_TEST_MGR", "THIRD_PARTY_TEST_DATE", "THIRD_PARTY_CONFIRM_DATE", 
            	    "THIRD_TEST_RESULT", "THIRD_PARTY_TEST_NOTES", "IT_MGR", "IT_TEST_DATE", 
            	    "IT_CONFIRM_DATE", "IT_TEST_RESULT", "IT_TEST_NOTES", "BUSI_MGR", 
            	    "BUSI_TEST_DATE", "BUSI_CONFIRM_DATE", "BUSI_TEST_RESULT", "BUSI_TEST_NOTES", 
            	    "TEST_STATUS", "INIT_REGISTRAR", "LAST_MODIFIER");
            if (!isHeaderValid5(headerRow, expectedHeaders)) {
                response.put("status", "error");
                response.put("message", "헤더의 컬럼명이 올바르지 않습니다.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            List<testProgress> Testprogress = new ArrayList<>();
            
            // 필드명 배열과 대응되는 셀 타입 배열
            String[] fieldNames = {
            		"testStage", "majorCategory", "subCategory", "minorCategory",
            	    "testId", "testScenarioName", "testCaseName", "testStepName",
            	    "screenId", "screenName", "programType", "programId", "programName",
            	    "screenMenuPath", "executeProcedure", "inputData", "preConditions", "expectedResult", "actualResult",
            	    "developer", "pl", "execCompanyMgr", "execCompanyTestDate", "execCompanyConfirmDate",
            	    "execCompanyTestResult", "execCompanyTestNotes", "thirdPartyTestMgr",
            	    "thirdPartyTestDate", "thirdPartyConfirmDate", "thirdTestResult",
            	    "thirdPartyTestNotes", "itMgr", "itTestDate", "itConfirmDate",
            	    "itTestResult", "itTestNotes", "busiMgr", "busiTestDate",
            	    "busiConfirmDate", "busiTestResult", "busiTestNotes",
            	    "testStatus", "initRegistrar", "lastModifier"
            };

            // 첫 번째 행은 헤더이므로 건너뜁니다.
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    testProgress testprogress = new testProgress();

                    try {
                    	for (int j = 0; j < fieldNames.length; j++) {
                            Field field = testProgress.class.getDeclaredField(fieldNames[j]);
                            field.setAccessible(true);

                            switch (field.getType().getSimpleName()) {
                                case "int":
                                    field.set(testprogress, (int) getCellValueAsNumeric3(row.getCell(j)));
                                    break;
                                case "Date":
                                    field.set(testprogress, getCellValueAsDate3(row.getCell(j)));
                                    break;
                                default:
                                	log.info(field.getType().getSimpleName());
                                    field.set(testprogress, getCellValueAsString3(row.getCell(j)));
                                    break;
                            }
                        }                    	
                        Testprogress.add(testprogress);
                    } catch (Exception e) {
                        e.printStackTrace();
                        response.put("status", "error");
                        response.put("message", "올바르지 않은 값이 입력되었습니다.");
                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }
                }
            }

            // 데이터베이스에 저장
            try {
                testService.saveAllTestProgress(Testprogress);
                response.put("status", "success");
                response.put("message", "파일 업로드가 성공적으로 완료되었습니다!");
                response.put("totalUploaded", Testprogress.size());
            } catch (Exception e) {
                e.printStackTrace();
                response.put("status", "error");
                response.put("message", e.getMessage());
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }

        } catch (DuplicateKeyException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "중복된 테스트 현황 정보가 발견되었습니다.");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        } catch (IOException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "파일 처리 중 오류가 발생했습니다. 다시 시도해 주세요.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
 // 액셀 유효헤더 확인
    private boolean isHeaderValid5(Row headerRow, List<String> expectedHeaders) {
        for (int i = 0; i < expectedHeaders.size(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell == null || !cell.getStringCellValue().trim().equalsIgnoreCase(expectedHeaders.get(i))) {
                return false;
            }
        }
        return true;
    }
    
    // 액셀 Cell값 String 변환 
    private String getCellValueAsString3(Cell cell) {
        if (cell == null) {
            return null;
        }
        return cell.getCellType() == CellType.STRING ? cell.getStringCellValue() : cell.toString();
    }
    
    // 액셀 Cell값 Num 변환
    private double getCellValueAsNumeric3(Cell cell) {
        if (cell == null) {
            return 0;
        }
        return cell.getCellType() == CellType.NUMERIC ? cell.getNumericCellValue() : Double.parseDouble(cell.toString());
    }
    
    // 액셀 Cell값 Date 변환
    private Date getCellValueAsDate3(Cell cell) {
        if (cell == null) {
            return null;
        }

        if (cell.getCellType() == CellType.NUMERIC) {
            // 날짜가 숫자 형태로 저장되어 있을 때, 날짜로 변환
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue();
            } else {
                return null;
            }
        } else if (cell.getCellType() == CellType.STRING) {
            // 문자열로 되어 있는 경우, "YYYY-MM-DD" 형식 등을 처리
            String dateStr = cell.getStringCellValue();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
				return dateFormat.parse(dateStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
        }

        return null; // 다른 유형의 셀은 null 반환
    }
    
    // String Value값 필수값 유효성 점검
    private void validateRequiredField3(String fieldValue, String fieldName) {
        if (fieldValue == null || fieldValue.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + "은(는) 필수 입력 항목입니다.");
        }
    }
    
    // Date Value값 유효성 점검
    private void validateDate3(Date dateValue, String dateName) {
        if (dateValue == null) {
            throw new IllegalArgumentException(dateName + "은(는) 필수 입력 항목입니다.");
        }
    }
    
    // 테스트 완료일이 있을때 테스트 결과 null 체크
    private void TestEndDateNullCheck3(Date dateValue, String ResultValue, String dataName) {
	    if ((dateValue != null) && 
	        	(ResultValue == null || ResultValue.trim().isEmpty())) {
	        	throw new IllegalArgumentException(dataName + " 단위테스트 수행 결과 '성공' 인지 '실패'인지 등록하시기 바랍니다.");
	        }
    }
    
    
    private void setIfNullDate3(Date DateValue1, Date DateValue2, Consumer<Date> setDate) {
        if (DateValue1 != null && DateValue2 == null) {
            setDate.accept(DateValue1);
        }
    }
    
    // 완료일이 등록되었는데 테스트 결과가 미등록된 경우 메세지
    private void EndDateResultCheck(Date dateValue, String ResultValue, String dataName) {
	    if ((dateValue != null) && 
	        	(ResultValue == null || ResultValue.trim().isEmpty())) {
	        	throw new IllegalArgumentException(dataName + " 테스트의 테스트 결과 항목이 입력되지 않았습니다.");
	        }
    }
    
    //테스트 완료일 체크해서 테스트 예정일 Null일 경우 자동 셋팅
    private void setIfNullDate2(Date DateValue1, Date DateValue2, Consumer<Date> setDate) {
        if (DateValue1 != null && DateValue2 == null) {
            setDate.accept(DateValue1);
        }
    }
}
