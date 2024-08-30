package com.tms.backend.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

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
import com.tms.backend.service.FileService;
import com.tms.backend.service.UserService;
import com.tms.backend.vo.CommonCode;
import com.tms.backend.vo.Criteria;
import com.tms.backend.vo.Notice;
import com.tms.backend.vo.PageDTO;
import com.tms.backend.vo.User;
import com.tms.backend.vo.categoryCode;
import com.tms.backend.vo.devProgress;

import java.text.ParseException;

import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@RequestMapping("/tms/*")
public class devProgressController {
	
	@Autowired
    private DevService devservice;
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private FileService fileservice;
	
	//개발진행관리 Controller
	
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
	        @RequestParam(value = "devStartDate", required = false) String devStartDate,
	        @RequestParam(value = "devEndDate", required = false) String devEndDate,
	        @RequestParam(value = "pl", required = false) String pl,
	        @RequestParam(value = "thirdPartyTestMgr", required = false) String thirdPartyTestMgr,
	        @RequestParam(value = "ItMgr", required = false) String ItMgr,
	        @RequestParam(value = "BusiMgr", required = false) String BusiMgr,
	        @RequestParam(value = "page", defaultValue = "1") int page,
	        @RequestParam(value = "size", defaultValue = "15") int size,
	        Model model) {
		
		log.info(programStatus);

	    // Service를 통해 데이터를 조회
	    List<devProgress> devProgressList = devservice.searchDevProgress(
	            majorCategory, subCategory, programType, programName,programId, programStatus, developer,
	            devStatus, devStartDate, devEndDate, pl, thirdPartyTestMgr, ItMgr, BusiMgr, page, size
	    );
	    


	    int totalDevProgress = devservice.getTotalDevProgressCount(
	            majorCategory, subCategory, programType, programName,programId, programStatus, developer,
	            devStatus, devStartDate, devEndDate, pl, thirdPartyTestMgr, ItMgr, BusiMgr
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
	        @RequestParam(value = "programStatus", required = false) String programStatus,
	        @RequestParam(value = "developer", required = false) String developer,
	        @RequestParam(value = "devStatus", required = false) String devStatus,
	        @RequestParam(value = "devStartDate", required = false) String devStartDate,
	        @RequestParam(value = "devEndDate", required = false) String devEndDate,
	        @RequestParam(value = "pl", required = false) String pl,
	        @RequestParam(value = "thirdPartyTestMgr", required = false) String thirdPartyTestMgr,
	        @RequestParam(value = "ItMgr", required = false) String ItMgr,
	        @RequestParam(value = "BusiMgr", required = false) String BusiMgr,
	        @RequestParam(value = "page", defaultValue = "1") int page,
	        @RequestParam(value = "size", defaultValue = "15") int size) {

	    // Service를 통해 데이터를 조회
	    List<devProgress> devProgressList = devservice.searchDevProgress(
	            majorCategory, subCategory, programType, programName, programId, programStatus, developer,
	            devStatus, devStartDate, devEndDate, pl, thirdPartyTestMgr, ItMgr, BusiMgr, page, size
	    );

	    int totalDevProgress = devservice.getTotalDevProgressCount(
	            majorCategory, subCategory, programType, programName, programId, programStatus, developer,
	            devStatus, devStartDate, devEndDate, pl, thirdPartyTestMgr, ItMgr, BusiMgr
	    );

	    int totalPages = (int) Math.ceil((double) totalDevProgress / size);
	    
	    List<User> developers = adminService.findAuthorityCode(5);
	    
	    // 응답 생성
	    Map<String, Object> response = new HashMap<>();
	    response.put("devProgressList", devProgressList);
	    response.put("currentPage", page);
	    response.put("totalPages", totalPages);
	    response.put("totalDevProgress", totalDevProgress);
	    response.put("developers", developers);

	    return ResponseEntity.ok(response); // JSON으로 응답 반환
	}
	
	//개발자 확인
	@GetMapping(value = "api/developer", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDevUser() {
    	Map<String, Object> response = new HashMap<>();
        List<User> developer = adminService.findAuthorityCode(5);
        
        log.info("확인중" + developer);

        // 응답 데이터 생성
        if (developer != null && !developer.isEmpty()) {
            response.put("status", "success");
            response.put("developer", developer);
        } else {
            response.put("status", "failure");
            response.put("message", "No developer found.");
            response.put("developer", developer);
        }

        // 조회된 결과를 반환
        return ResponseEntity.ok(response);
    }
	
//	//대분류 확인
//	@GetMapping(value = "api/major", produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseBody
//    public ResponseEntity<Map<String, Object>> getMajor() {
//    	Map<String, Object> response = new HashMap<>();
//    	List<categoryCode> Major = adminService.getParentCategoryCodes();
//
//        // 응답 데이터 생성
//        if (Major != null && !Major.isEmpty()) {
//            response.put("status", "success");
//            response.put("major", Major);
//        } else {
//            response.put("status", "failure");
//            response.put("message", "대분류 정보를 찾을 수 없습니다");
//        }
//
//        // 조회된 결과를 반환
//        return ResponseEntity.ok(response);
//    }
	
	//프로그램 타입 확인
	@GetMapping(value = "api/programType", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getProgramType() {
    	Map<String, Object> response = new HashMap<>();
    	List<CommonCode> ProgramType= adminService.getCCCode("02");

        // 응답 데이터 생성
        if (ProgramType != null && !ProgramType.isEmpty()) {
            response.put("status", "success");
            response.put("programType", ProgramType);
        } else {
            response.put("status", "failure");
            response.put("message", "프로그램 타입 정보를 찾을 수 없습니다");
        }

        // 조회된 결과를 반환
        return ResponseEntity.ok(response);
    }
	
	//프로그램 상세 구분 확인
	@GetMapping(value = "api/programDtype", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getProgramDType() {
    	Map<String, Object> response = new HashMap<>();
    	List<CommonCode> ProgramDType= adminService.getCCCode("03");

        // 응답 데이터 생성
        if (ProgramDType != null && !ProgramDType.isEmpty()) {
            response.put("status", "success");
            response.put("programDtype", ProgramDType);
        } else {
            response.put("status", "failure");
            response.put("message", "프로그램 상세 구분 정보를 찾을 수 없습니다");
        }

        // 조회된 결과를 반환
        return ResponseEntity.ok(response);
    }
	
	//우선도 및 난이도 확인
	@GetMapping(value = "api/levels", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getlevels() {
    	Map<String, Object> response = new HashMap<>();
    	List<CommonCode> levels= adminService.getCCCode("04");

        // 응답 데이터 생성
        if (levels != null && !levels.isEmpty()) {
            response.put("status", "success");
            response.put("levels", levels);
        } else {
            response.put("status", "failure");
            response.put("message", "우선도 및 난이도 정보를 찾을 수 없습니다");
        }

        // 조회된 결과를 반환
        return ResponseEntity.ok(response);
    }
	
	//프로그램 상태 확인
	@GetMapping(value = "api/programStatus", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getProgramStatus() {
    	Map<String, Object> response = new HashMap<>();
    	List<CommonCode> ProgramStatus= adminService.getCCCode("05");

        // 응답 데이터 생성
        if (ProgramStatus != null && !ProgramStatus.isEmpty()) {
            response.put("status", "success");
            response.put("programStatus", ProgramStatus);
        } else {
            response.put("status", "failure");
            response.put("message", "프로그램 상태 정보를 찾을 수 없습니다");
        }

        // 조회된 결과를 반환
        return ResponseEntity.ok(response);
    }
	
	//사용자 ID 확인
	@GetMapping(value = "api/user", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUserIDLIST() {
    	Map<String, Object> response = new HashMap<>();
    	List<User> User= adminService.getUserList();

        // 응답 데이터 생성
        if (User != null && !User.isEmpty()) {
            response.put("status", "success");
            response.put("user", User);
        } else {
            response.put("status", "failure");
            response.put("message", "사용자 정보를 찾을 수 없습니다");
        }

        // 조회된 결과를 반환
        return ResponseEntity.ok(response);
    }
	
	//우선도 및 난이도 확인
	@GetMapping(value = "api/testResult", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getTestResult() {
    	Map<String, Object> response = new HashMap<>();
    	List<CommonCode> TestResult= adminService.getCCCode("07");

        // 응답 데이터 생성
        if (TestResult != null && !TestResult.isEmpty()) {
            response.put("status", "success");
            response.put("testResult", TestResult);
        } else {
            response.put("status", "failure");
            response.put("message", "테스트 결과를 찾을 수 없습니다");
        }

        // 조회된 결과를 반환
        return ResponseEntity.ok(response);
    }
	
	//IT 담당자 확인
	@GetMapping(value = "api/itMgr", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getITMGR() {
    	Map<String, Object> response = new HashMap<>();
    	List<User> ITMGR= adminService.findAuthorityCode(7);

        // 응답 데이터 생성
        if (ITMGR != null && !ITMGR.isEmpty()) {
            response.put("status", "success");
            response.put("itMgr", ITMGR);
        } else {
            response.put("status", "failure");
            response.put("message", "IT 담당자 정보를 찾을 수 없습니다");
        }

        // 조회된 결과를 반환
        return ResponseEntity.ok(response);
    }
	
	//기업 담당자 확인
	@GetMapping(value = "api/busiMgr", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getBUSIMGR() {
    	Map<String, Object> response = new HashMap<>();
    	List<User> BUSIMGR= adminService.findAuthorityCode(8);

        // 응답 데이터 생성
        if (BUSIMGR != null && !BUSIMGR.isEmpty()) {
            response.put("status", "success");
            response.put("busiMgr", BUSIMGR);
        } else {
            response.put("status", "failure");
            response.put("message", "기업 담당자 정보를 찾을 수 없습니다");
        }

        // 조회된 결과를 반환
        return ResponseEntity.ok(response);
    }
	
	//개발 진행 상태 확인
	@GetMapping(value = "api/devStatus", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDevStatus() {
    	Map<String, Object> response = new HashMap<>();
    	List<CommonCode> devStatus= adminService.getCCCode("06");

        // 응답 데이터 생성
        if (devStatus != null && !devStatus.isEmpty()) {
            response.put("status", "success");
            response.put("devStatus", devStatus);
        } else {
            response.put("status", "failure");
            response.put("message", "대분류 정보를 찾을 수 없습니다");
        }

        // 조회된 결과를 반환
        return ResponseEntity.ok(response);
    }
	
	@GetMapping("/devProgressReg")
	public String devProgressPage() {

	    return "devProgressReg"; // JSP 페이지로 이동
	}
	
	//개발 진행 현황 등록 페이지
	@PostMapping(value="api/devProgressReg" , produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> devProgressReg(HttpServletRequest request,
			@RequestPart("devProgress") devProgress devProgress,
			@RequestPart(value = "file", required = false) MultipartFile[] files) {
		HttpSession session = request.getSession(false); // 세션이 없다면 새로 만들지 않음
		if (session == null || session.getAttribute("authorityCode") == null) {
			// 세션이 없거나 authorityCode가 없으면 401 Unauthorized 반환
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "권한이 없습니다. 로그인하세요."));
			}
		Map<String, Object> response = new HashMap<>();
		try {
			// 필수 항목 체크
			validateRequiredField(devProgress.getMajorCategory(), "업무 대분류");
			validateRequiredField(devProgress.getSubCategory(), "업무 중분류");
			validateRequiredField(devProgress.getProgramId(), "프로그램 ID");
			validateRequiredField(devProgress.getProgramName(), "프로그램명");
			validateRequiredField(devProgress.getDeveloper(), "개발자");
			validateRequiredField(devProgress.getPriority(), "우선순위");
			validateRequiredField(devProgress.getProgramStatus(), "프로그램 상태");
	        if ("삭제".equals(devProgress.getProgramStatus()) &&
	        	(devProgress.getDeletionReason() == null || devProgress.getDeletionReason().trim().isEmpty())) {
	        	throw new IllegalArgumentException("프로그램 상태가 '삭제'인 경우 삭제처리사유 입력은 필수입니다.");
	        }
	        // Date 필드 체크
	        validateDate(devProgress.getPlannedStartDate(), "시작 예정일");
	        validateDate(devProgress.getPlannedEndDate(), "완료 예정일");
	        if ((devProgress.getDevtestendDate() != null) && 
	        	(devProgress.getDevProgAttachment() == null || devProgress.getDevProgAttachment().trim().isEmpty())) {
	        	devProgress.setDevtestendDate(null);
	        	throw new IllegalArgumentException
	        	("단위테스트 증적인 '단위테스트결과서'를 첨부하지 않으면 개발자 단테종료일 등록 값은 미입력 상태로 자동 변경됩니다.");
	        }
	        //테스트 완료일이 있을때 테스트 결과가 null 체크
	        TestEndDateNullCheck(devProgress.getPlTestCmpDate(), devProgress.getPlTestResult(), "PL");
	        TestEndDateNullCheck(devProgress.getThirdPartyConfirmDate(), devProgress.getThirdTestResult(), "제3자");
	        TestEndDateNullCheck(devProgress.getItConfirmDate(), devProgress.getItTestResult(), "고객 IT");
	        TestEndDateNullCheck(devProgress.getBusiConfirmDate(), devProgress.getBusiTestResult(), "고객 현업");        
	        
	        // 데이터 체크 자동 세팅
	        if (devProgress.getPlTestCmpDate() != null && devProgress.getPlTestScdDate() == null) {
	        	devProgress.setPlTestScdDate(devProgress.getPlTestCmpDate());
	        }
	        if (devProgress.getThirdPartyConfirmDate() != null && devProgress.getThirdPartyTestDate() == null) {
	        	devProgress.setThirdPartyTestDate(devProgress.getThirdPartyConfirmDate());
	        }
	        if (devProgress.getItConfirmDate() != null && devProgress.getItTestDate() == null) {
	        	devProgress.setItTestDate(devProgress.getItConfirmDate());
	        }
	        if (devProgress.getBusiConfirmDate() != null && devProgress.getBusiTestDate() == null) {
	        	devProgress.setBusiTestDate(devProgress.getBusiConfirmDate());
	        }
			
        	devservice.insertdevProgress(devProgress);  // 개발 현황 진행 정보 추가
        	
        	// 새로운 파일 업로드 처리
            if (files != null && files.length > 0) {
                fileservice.handleFileUpload(files, "devProgress", devProgress.getSeq());
            }
        	
            response.put("status", "success");
            response.put("message", "개발 진행 현황 정보가 등록되었습니다");
            response.put("devProgress", devProgress);  // 등록된 개발 현황 진행 정보 반환
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.put("status", "failure");
            response.put("message", e.getMessage());  // 예외 메시지를 response에 포함시킴
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "개발 진행 현황 정보 진행 중에 오류가 발생했습니다.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}
	
	@GetMapping("/devProgressEdit")
	public String devProgressEditPage() {

	    return "devProgressEdit"; // JSP 페이지로 이동
	}
	
	//개발 진행 현황 수정 페이지
	@GetMapping(value="api/devProgressEditPage" , produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> devProgressEditPage2(HttpServletRequest request,
			@RequestParam("seq") Integer seq) {
		HttpSession session = request.getSession(false); // 세션이 없다면 새로 만들지 않음
		if (session == null || session.getAttribute("authorityCode") == null) {
			// 세션이 없거나 authorityCode가 없으면 401 Unauthorized 반환
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "권한이 없습니다. 로그인하세요."));
			}
//		Integer authorityCode = (Integer) session.getAttribute("authorityCode");
		
		Map<String, Object> response = new HashMap<>();
		devProgress DevProgressEdit = devservice.getDevById(seq);
		  
	    // 응답 생성
		response.put("status", "success");
        response.put("message", "개발 진행 현황 정보 전달.");
	    response.put("devProgressEdit", DevProgressEdit);
	    
	    return ResponseEntity.ok(response); // JSON으로 응답 반환
	}
	
	//개발 진행 현황 수정
    @PostMapping(value = "api/devProgressEdit", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> devProgressEdit(@RequestPart("devProgress") devProgress devProgress,  
    		@RequestPart(value = "file", required = false) MultipartFile[] files) {
        Map<String, Object> response = new HashMap<>();
        devProgress DevProgressEdit = devservice.getDevById(devProgress.getSeq());
        try {
        	// devProgress의 필드를 DevProgressEdit에 복사
    	    BeanUtils.copyProperties(devProgress, DevProgressEdit);
    	    
    	    // 공지사항에 등록된 기존 첨부파일 전부 삭제
            adminService.deleteAttachmentsByNoticeId(DevProgressEdit.getSeq());

            // 새로운 파일 업로드 처리
            fileservice.handleFileUpload(files, "devProgress", DevProgressEdit.getSeq());
            
            log.info("check "+devProgress);

            // 업데이트된 공지사항을 저장
            devservice.updatedevProgress(DevProgressEdit);

            // 성공 응답 생성
            response.put("status", "success");
            response.put("message", "개발 진행 현황이 성공적으로 수정되었습니다.");
            response.put("DevProgressEdit", DevProgressEdit);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.put("status", "failure");
            response.put("message", "프로그램 개발목록 수정 중에 오류 발생");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "프로그램 개발목록 수정 중에 오류 발생");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	
	
	//개발 진행 현황 삭제
	@DeleteMapping(value= "api/deletedev", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deletedev(@RequestBody List<Integer> seqs) {
        Map<String, Object> response = new HashMap<>();
        
        for (int seq : seqs) {
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
    
    // 액셀 파일 예시를 다운로드
    @GetMapping("/devexampleexcel")
    public void downloadExdev(HttpServletResponse response) throws IOException {
    	List<devProgress> ExampleDEV = devservice.searchDevProgress("NO_VALUE", null, null, null, null, null, null, null, null, null, null, null, null, null, 1, Integer.MAX_VALUE);
    	devexportToExcel(response, ExampleDEV, "example.xlsx");
    }
    
    // 조회된 개발 진행 현황 정보를 엑셀로 다운로드
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
	        @RequestParam(value = "devStartDate", required = false) String devStartDate, //개발완료일 시작일
	        @RequestParam(value = "devEndDate", required = false) String devEndDate, //개발완료일 완료일
	        @RequestParam(value = "pl", required = false) String pl,
	        @RequestParam(value = "thirdPartyTestMgr", required = false) String thirdPartyTestMgr,
	        @RequestParam(value = "ItMgr", required = false) String ItMgr,
	        @RequestParam(value = "BusiMgr", required = false) String BusiMgr,
	        @RequestParam(value = "page", defaultValue = "1") int page,
	        @RequestParam(value = "size", defaultValue = "15") int size,
            HttpServletResponse response) throws IOException {
    	
    	log.info(programId);
    	
    	List<devProgress> filteredDevCodeList = devservice.searchDevProgress(
	            majorCategory, subCategory, programType, programName,programId, programStatus, developer,
	            devStatus, devStartDate, devEndDate, pl, thirdPartyTestMgr, ItMgr, BusiMgr, page, size
	    );

    	log.info("확인중"+filteredDevCodeList);
        devexportToExcel(response, filteredDevCodeList, "filtered_dev_codes.xlsx");
    }

    // 엑셀 파일로 데이터를 내보내는 메서드
    private void devexportToExcel(HttpServletResponse response, List<devProgress> devProgressList, String fileName) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("DevProgress");
        String check = "";
        log.info("체크중");
        if (devProgressList.isEmpty()) {
    		check = "no_value";
    		log.info(check);
    		}
        // 헤더 이름 배열
        String[] headers = {
            "SEQ", "MAJOR_CATEGORY", "SUB_CATEGORY", "MINOR_CATEGORY", 
            "PROGRAM_DETAIL_TYPE", "PROGRAM_TYPE", "PROGRAM_ID", "PROGRAM_NAME", 
            "CLASS_NAME", "SCREEN_ID", "SCREEN_NAME", "SCREEN_MENU_PATH", 
            "PRIORITY", "DIFFICULTY", "ESTIMATED_EFFORT", "program_status", 
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

        // 헤더 생성
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }
        if (check != "") {
        	headerRow.createCell(47).setCellValue("INIT_REGISTRAR");
            headerRow.createCell(48).setCellValue("LAST_MODIFIER");
        }
        else {
	        headerRow.createCell(47).setCellValue("INIT_REG_DATE");
	        headerRow.createCell(48).setCellValue("INIT_REGISTRAR");
	        headerRow.createCell(49).setCellValue("LAST_MODIFIED_DATE");
	        headerRow.createCell(50).setCellValue("LAST_MODIFIER");
        }
        
        // 헤더와 데이터 매핑
        String[] fields = {
            "getSeq", "getMajorCategory", "getSubCategory", "getMinorCategory",
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

        // 데이터 행 생성
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
	            row.createCell(47).setCellValue(devProgress.getInitRegistrar());
	            row.createCell(48).setCellValue(devProgress.getLastModifier());
            }
            else {
            	row.createCell(47).setCellValue(devProgress.getInitRegDate().toString());
	            row.createCell(48).setCellValue(devProgress.getInitRegistrar());
	            row.createCell(49).setCellValue(devProgress.getLastModifiedDate().toString());
	            row.createCell(50).setCellValue(devProgress.getLastModifier());
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
  
    @PostMapping(value = "api/devupload", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> catuploadExcelFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
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
            List<String> expectedHeaders = Arrays.asList("SEQ", "MAJOR_CATEGORY", "SUB_CATEGORY", "MINOR_CATEGORY", 
            		"PROGRAM_DETAIL_TYPE", "PROGRAM_TYPE", "PROGRAM_ID", "PROGRAM_NAME", "CLASS_NAME", "SCREEN_ID", 
            		"SCREEN_NAME", "SCREEN_MENU_PATH", "PRIORITY", "DIFFICULTY", "ESTIMATED_EFFORT", "PROGRAM_STATUS", 
            		"REQ_ID", "DELETION_HANDLER", "DELETION_DATE", "DELETION_REASON", "DEVELOPER", "PLANNED_START_DATE", 
            		"PLANNED_END_DATE", "ACTUAL_START_DATE", "ACTUAL_END_DATE", "DEV_TEST_END_DATE", 
                    "PL", "PL_TEST_SCD_DATE", 
            		"PL_TEST_CMP_DATE", "PL_TEST_RESULT", "PL_TEST_NOTES", "IT_MGR", "IT_TEST_DATE", "IT_CONFIRM_DATE", 
            		"IT_TEST_RESULT", "IT_TEST_NOTES", "BUSI_MGR", "BUSI_TEST_DATE", "BUSI_CONFIRM_DATE", "BUSI_TEST_RESULT", 
            		"BUSI_TEST_NOTES", "THIRD_PARTY_TEST_MGR", "THIRD_PARTY_TEST_DATE", "THIRD_PARTY_CONFIRM_DATE", 
            		"THIRD_TEST_RESULT", "THIRD_PARTY_TEST_NOTES", "DEV_STATUS", "INIT_REGISTRAR", "LAST_MODIFIER");
            if (!isHeaderValid4(headerRow, expectedHeaders)) {
                response.put("status", "error");
                response.put("message", "헤더의 컬럼명이 올바르지 않습니다.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            List<devProgress> devProgress = new ArrayList<>();
            
            // 필드명 배열과 대응되는 셀 타입 배열
            String[] fieldNames = {
                "seq", "majorCategory", "subCategory", "minorCategory", "programDetailType",
                "programType", "programId", "programName", "className", "screenId",
                "screenName", "screenMenuPath", "priority", "difficulty", "estimatedEffort",
                "programStatus", "reqId", "deletionHandler", "deletionDate", "deletionReason",
                "developer", "plannedStartDate", "plannedEndDate", "actualStartDate",
                "actualEndDate", "devtestendDate", "pl", "plTestScdDate", "plTestCmpDate",
                "plTestResult", "plTestNotes", "itMgr", "itTestDate", "itConfirmDate",
                "itTestResult", "itTestNotes", "busiMgr", "busiTestDate", "busiConfirmDate",
                "busiTestResult", "busiTestNotes", "thirdPartyTestMgr", "thirdPartyTestDate",
                "thirdPartyConfirmDate", "thirdTestResult", "thirdPartyTestNotes", "devStatus",
                "initRegistrar", "lastModifier"
            };

            // 첫 번째 행은 헤더이므로 건너뜁니다.
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    devProgress devprogress = new devProgress();

                    try {
                    	for (int j = 0; j < fieldNames.length; j++) {
                            Field field = devProgress.class.getDeclaredField(fieldNames[j]);
                            field.setAccessible(true);

                            switch (field.getType().getSimpleName()) {
                                case "int":
                                    field.set(devprogress, (int) getCellValueAsNumeric(row.getCell(j)));
                                    break;
                                case "Date":
                                    field.set(devprogress, getCellValueAsDate(row.getCell(j)));
                                    break;
                                default:
                                    field.set(devprogress, getCellValueAsString(row.getCell(j)));
                                    break;
                            }
                        }
//                    	devprogress.setSeq((int) getCellValueAsNumeric(row.getCell(0)));
//                        devprogress.setMajorCategory(getCellValueAsString(row.getCell(1)));
//                        devprogress.setSubCategory(getCellValueAsString(row.getCell(2)));
//                        devprogress.setMinorCategory(getCellValueAsString(row.getCell(3)));
//                        devprogress.setProgramDetailType(getCellValueAsString(row.getCell(4)));
//                        devprogress.setProgramType(getCellValueAsString(row.getCell(5)));
//                        devprogress.setProgramId(getCellValueAsString(row.getCell(6)));
//                        devprogress.setProgramName(getCellValueAsString(row.getCell(7)));
//                        devprogress.setClassName(getCellValueAsString(row.getCell(8)));
//                        devprogress.setScreenId(getCellValueAsString(row.getCell(9)));
//                        devprogress.setScreenName(getCellValueAsString(row.getCell(10)));
//                        devprogress.setScreenMenuPath(getCellValueAsString(row.getCell(11)));
//                        devprogress.setPriority(getCellValueAsString(row.getCell(12)));
//                        devprogress.setDifficulty(getCellValueAsString(row.getCell(13)));
//                        devprogress.setEstimatedEffort((int) getCellValueAsNumeric(row.getCell(14)));
//                        devprogress.setProgramStatus(getCellValueAsString(row.getCell(15)));
//                        devprogress.setReqId(getCellValueAsString(row.getCell(16)));
//                        devprogress.setDeletionHandler(getCellValueAsString(row.getCell(17)));
//                        devprogress.setDeletionDate(getCellValueAsDate(row.getCell(18)));
//                        devprogress.setDeletionReason(getCellValueAsString(row.getCell(19)));
//                        devprogress.setDeveloper(getCellValueAsString(row.getCell(20)));
//                        devprogress.setPlannedStartDate(getCellValueAsDate(row.getCell(21)));
//                        devprogress.setPlannedEndDate(getCellValueAsDate(row.getCell(22)));
//                        devprogress.setActualStartDate(getCellValueAsDate(row.getCell(23)));
//                        devprogress.setActualEndDate(getCellValueAsDate(row.getCell(24)));
//                        devprogress.setDevtestendDate(getCellValueAsDate(row.getCell(25)));
//                        devprogress.setPl(getCellValueAsString(row.getCell(26)));
//                        devprogress.setPlTestScdDate(getCellValueAsDate(row.getCell(27)));
//                        devprogress.setPlTestCmpDate(getCellValueAsDate(row.getCell(28)));
//                        devprogress.setPlTestResult(getCellValueAsString(row.getCell(29)));
//                        devprogress.setPlTestNotes(getCellValueAsString(row.getCell(30)));
//                        devprogress.setItMgr(getCellValueAsString(row.getCell(31)));
//                        devprogress.setItTestDate(getCellValueAsDate(row.getCell(32)));
//                        devprogress.setItConfirmDate(getCellValueAsDate(row.getCell(33)));
//                        devprogress.setItTestResult(getCellValueAsString(row.getCell(34)));
//                        devprogress.setItTestNotes(getCellValueAsString(row.getCell(35)));
//                        devprogress.setBusiMgr(getCellValueAsString(row.getCell(36)));
//                        devprogress.setBusiTestDate(getCellValueAsDate(row.getCell(37)));
//                        devprogress.setBusiConfirmDate(getCellValueAsDate(row.getCell(38)));
//                        devprogress.setBusiTestResult(getCellValueAsString(row.getCell(39)));
//                        devprogress.setBusiTestNotes(getCellValueAsString(row.getCell(40)));
//                        devprogress.setThirdPartyTestMgr(getCellValueAsString(row.getCell(41)));
//                        devprogress.setThirdPartyTestDate(getCellValueAsDate(row.getCell(42)));
//                        devprogress.setThirdPartyConfirmDate(getCellValueAsDate(row.getCell(43)));
//                        devprogress.setThirdTestResult(getCellValueAsString(row.getCell(44)));
//                        devprogress.setThirdPartyTestNotes(getCellValueAsString(row.getCell(45)));
//                        devprogress.setDevStatus(getCellValueAsString(row.getCell(46)));
//                        devprogress.setInitRegistrar(getCellValueAsString(row.getCell(47)));
//                        devprogress.setLastModifier(getCellValueAsString(row.getCell(48)));
                        devProgress.add(devprogress);
                    } catch (Exception e) {
                        e.printStackTrace();
                        response.put("status", "error");
                        response.put("message", "파일의 데이터 형식이 올바르지 않습니다: 행 " + (i + 1));
                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }
                }
            }

            // 데이터베이스에 저장
            devservice.saveAllDevProgress(devProgress);
            response.put("status", "success");
            response.put("message", "파일 업로드가 성공적으로 완료되었습니다!");
            response.put("totalUploaded", devProgress.size());

        } catch (DuplicateKeyException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "중복된 개발 현황 정보가 발견되었습니다.");
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
    private boolean isHeaderValid4(Row headerRow, List<String> expectedHeaders) {
        for (int i = 0; i < expectedHeaders.size(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell == null || !cell.getStringCellValue().trim().equalsIgnoreCase(expectedHeaders.get(i))) {
                return false;
            }
        }
        return true;
    }
    
    // 액셀 Cell값 String 변환 
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        return cell.getCellType() == CellType.STRING ? cell.getStringCellValue() : cell.toString();
    }
    
    // 액셀 Cell값 Num 변환
    private double getCellValueAsNumeric(Cell cell) {
        if (cell == null) {
            return 0;
        }
        return cell.getCellType() == CellType.NUMERIC ? cell.getNumericCellValue() : Double.parseDouble(cell.toString());
    }
    
    // 액셀 Cell값 Date 변환
    private Date getCellValueAsDate(Cell cell) {
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
    private void validateRequiredField(String fieldValue, String fieldName) {
        if (fieldValue == null || fieldValue.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + "은(는) 필수 입력 항목입니다.");
        }
    }
    
    // Date Value값 유효성 점검
    private void validateDate(Date dateValue, String dateName) {
        if (dateValue == null) {
            throw new IllegalArgumentException(dateName + "은(는) 필수 입력 항목입니다.");
        }
    }
    
    // 테스트 완료일이 있을때 테스트 결과 null 체크
    private void TestEndDateNullCheck(Date dateValue, String ResultValue, String dataName) {
	    if ((dateValue != null) && 
	        	(ResultValue == null || ResultValue.trim().isEmpty())) {
	        	throw new IllegalArgumentException(dataName + " 단위테스트 수행 결과 '성공' 인지 '실패'인지 등록하시기 바랍니다.");
	        }
    }
    
    
        
    
    
   
    
    



}
