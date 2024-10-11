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
import com.tms.backend.vo.CommonCode;
import com.tms.backend.vo.Defect;
import com.tms.backend.vo.FileAttachment;
import com.tms.backend.vo.User;
import com.tms.backend.vo.devProgress;

import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@RequestMapping("/*")
public class DefectController {
	
	@Autowired
	private DefectService defectService;
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private FileService fileservice;
	
	//결함 페이지
	@GetMapping("/defect")
	public String defectStatusPage() {
	    return "defect"; // defect.jsp로 이동
	}
	
	//결함 페이지 조회 및 결함 테이블 조회 사용
	@GetMapping(value="api/defect" , produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> APIdefectPage(HttpServletRequest request,
			@RequestParam(value = "testStage", required = false) String testStage,
            @RequestParam(value = "majorCategory", required = false) String majorCategory,
            @RequestParam(value = "subCategory", required = false) String subCategory,
            @RequestParam(value = "defectSeverity", required = false) String defectSeverity,
            @RequestParam(value = "seq", required = false) Integer seq,
            @RequestParam(value = "defectRegistrar", required = false) String defectRegistrar,
            @RequestParam(value = "defectHandler", required = false) String defectHandler,
            @RequestParam(value = "Pl", required = false) String pl,
            @RequestParam(value = "defectStatus", required = false) String defectStatus,
            @RequestParam(value = "programId", required = false) String programId,
            @RequestParam(value = "testId", required = false) String testId,
            @RequestParam(value = "programName", required = false) String programName,
            @RequestParam(value = "programType", required = false) String programType,
            @RequestParam(value = "page" , defaultValue = "1") int page,
            @RequestParam(value = "size" , defaultValue = "99999") int size) {
		
//		HttpSession session = request.getSession(false); // 세션이 없다면 새로 만들지 않음
//		if (session == null || session.getAttribute("authorityCode") == null) {
//			// 세션이 없거나 authorityCode가 없으면 401 Unauthorized 반환
//			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "권한이 없습니다. 로그인하세요."));
//			}
		Map<String, Object> response = new HashMap<>();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
				
		// 숫자로 프론트엔드에서 오는 것들 DB에 입력하는 문자로 변환
		majorCategory = adminService.getStageCodes("대", majorCategory);
		subCategory = adminService.getStageCodes("중", subCategory);
		defectSeverity = adminService.getStageCCodes("14", defectSeverity);
		defectStatus = adminService.getStageCCodes("15", defectStatus);
		testStage = adminService.getStageCCodes("11", testStage);
				
		List<Defect> defects;
								
		if (size == 99999) { // 결함 조회를 위해서는 size가 디폴트값에서 변하기에 기본 검색이 아니게 만들기 위한 조건으로 성립
		    defects = defectService.searchDefectOriginal(testStage, testId, programId, programName);
		} else { // 기본 검색
				
		// 결함 목록 조회
		defects = defectService.searchDefects(testStage, majorCategory, subCategory, defectSeverity, seq, defectRegistrar, 
				defectHandler, pl,  defectStatus, programId, testId, programName, programType, page, size);
		}
	    
	    // 총 결함 수 조회
	    int totalDefects = defectService.getTotalDefectsCount(testStage, majorCategory, subCategory, defectSeverity, seq, defectRegistrar, 
	    		defectHandler, pl, defectStatus, programId, testId, programName, programType);
	    
	    // 총 페이지 수 계산
	    int totalPages = (int) Math.ceil((double) totalDefects / size);
	    
	    log.info(defects);
	    	    
	    // 응답 생성
	    response.put("defects", defects);
	    response.put("currentPage", page);
	    response.put("totalPages", totalPages);
	    response.put("totalDefects", totalDefects);

	    return ResponseEntity.ok(response); // JSON으로 응답 반환

	}
	
	//결함 유형 확인
	@GetMapping(value = "api/defectType", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDefectType() {
    	Map<String, Object> response = new HashMap<>();
    	List<CommonCode> defectType= adminService.getCCCode("13"); // 결함 유형 숫자로 보내서 문자로 반환

        // 응답 데이터 생성
        if (defectType != null && !defectType.isEmpty()) {
            response.put("status", "success");
            response.put("defectType", defectType);
        } else {
            response.put("status", "failure");
            response.put("message", "결함 유형 정보를 찾을 수 없습니다");
        }

        // 조회된 결과를 반환
        return ResponseEntity.ok(response);
    }
		
	//결함 심각도 확인
	@GetMapping(value = "api/defectSeverity", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getdefectSeverity() {
    	Map<String, Object> response = new HashMap<>();
    	List<CommonCode> defectSeverity= adminService.getCCCode("14");

        // 응답 데이터 생성
        if (defectSeverity != null && !defectSeverity.isEmpty()) {
            response.put("status", "success");
            response.put("defectSeverity", defectSeverity);
        } else {
            response.put("status", "failure");
            response.put("message", "결함 심각도 정보를 찾을 수 없습니다");
        }

        // 조회된 결과를 반환
        return ResponseEntity.ok(response);
    }
	
	//테스트 단계 상태 확인
	@GetMapping(value = "api/testStage", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> gettestStage() {
    	Map<String, Object> response = new HashMap<>();
    	List<CommonCode> testStage= adminService.getCCCode("11");

        // 응답 데이터 생성
        if (testStage != null && !testStage.isEmpty()) {
            response.put("status", "success");
            response.put("testStage", testStage);
        } else {
            response.put("status", "failure");
            response.put("message", "테스트 단계 정보를 찾을 수 없습니다");
        }

        // 조회된 결과를 반환
        return ResponseEntity.ok(response);
    }
		
	//결함 처리 상태 확인
	@GetMapping(value = "api/defectStatus", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getdefectStatus() {
    	Map<String, Object> response = new HashMap<>();
    	List<CommonCode> defectStatus= adminService.getCCCode("15");

        // 응답 데이터 생성
        if (defectStatus != null && !defectStatus.isEmpty()) {
            response.put("status", "success");
            response.put("defectStatus", defectStatus);
        } else {
            response.put("status", "failure");
            response.put("message", "결함 처리 상태 정보를 찾을 수 없습니다");
        }

        // 조회된 결과를 반환
        return ResponseEntity.ok(response);
    }
	
	//결함 등록 페이지
	@GetMapping("/defectReg")
	public String defectRegPage() {
		return "defectReg";
	}
	
	//결함 수정 페이지
	@GetMapping("/defectEdit")
	public String defectEditPage() {
	    return "defectEdit"; // JSP 페이지로 이동
	}
		
	//개발 진행 현황 등록
    @PostMapping(value = "api/defectReg", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> defectReg(HttpServletRequest request,
			@RequestPart("defect") Defect defect,
			@RequestPart(value = "file", required = false) MultipartFile[] files,
			@RequestPart(value = "fixfile", required = false) MultipartFile[] fixfiles) {
		HttpSession session = request.getSession(false); // 세션이 없다면 새로 만들지 않음
		String UserName = (String) session.getAttribute("name"); //세션에서 유저명 가져오기
		Map<String, Object> response = new HashMap<>();
		
		//코드로 들어오는 데이터를 코드명으로 변경
		defect.setMajorCategory(adminService.getStageCodes("대", defect.getMajorCategory()));
		defect.setSubCategory(adminService.getStageCodes("중", defect.getSubCategory()));
		defect.setDefectType(adminService.getStageCCodes("13", defect.getDefectType()));
		defect.setDefectSeverity(adminService.getStageCCodes("14", defect.getDefectSeverity()));
		defect.setDefectStatus(adminService.getStageCCodes("15", defect.getDefectStatus()));
		defect.setTestStage(adminService.getStageCCodes("11", defect.getTestStage()));
		
		try {
			// 테스트 ID 비어있을 경우 프로그램 ID 입력
			if(defect.getTestId() == null || defect.getTestId().trim().isEmpty()) {
				defect.setTestId(defect.getProgramId());
			}
			//테스트 결함등록자, 최초 결함 등록자 로그인 ID 세팅
			defect.setDefectRegistrar(UserName);
			defect.setInitCreater(UserName);
			defect.setLastModifier(UserName);
			// 조치완료일, PL 확인일에 따른 결함 처리 상태 자동 세팅
			if(defect.getDefectCompletionDate() == null && defect.getPlConfirmDate() == null) {
				defect.setDefectStatus("등록완료");}
			if(defect.getDefectCompletionDate() != null && defect.getPlConfirmDate() == null) {
				defect.setDefectStatus("조치완료");}
			if(defect.getDefectCompletionDate() != null && defect.getPlConfirmDate() != null && defect.getDefectRegConfirmDate() == null) {
				defect.setDefectStatus("PL 확인완료");}
			if(defect.getDefectCompletionDate() != null && defect.getPlConfirmDate() != null && defect.getDefectRegConfirmDate() != null) {
				defect.setDefectStatus("등록자 확인완료");}
			
			// 시작예정일이 종료예정일보다 뒤일 경우 에러
			if (defect.getDefectScheduledDate() != null && defect.getDefectCompletionDate() != null) {
			    if (defect.getDefectScheduledDate().after(defect.getDefectCompletionDate())) {
			    	log.info("Error!!!!!");
			        throw new IllegalArgumentException("결함 조치 예정일의 시작일은 종료일보다 앞서야 합니다.");
			    }
			}
			
        	// 필수 항목 체크
			validateRequiredField(defect.getMajorCategory(), "업무 대분류");
			validateRequiredField(defect.getSubCategory(), "업무 중분류");
			validateRequiredField(defect.getTestStage(), "테스트 단계");
			validateRequiredField(defect.getTestId(), "테스트ID");
			validateRequiredField(defect.getDefectType(), "결함유형");
			validateRequiredField(defect.getDefectSeverity(), "결함심각도");
			validateRequiredField(defect.getDefectDescription(), "결함 내용");
			validateRequiredField(defect.getProgramId(), "프로그램ID");
			validateRequiredField(defect.getDefectHandler(), "조치담당자");
			validateRequiredField(defect.getPl(), "PL");
			if(defect.getDefectDiscoveryDate() == null) {
				throw new IllegalArgumentException("결함발생일은 필수 입력 항목입니다.");
			}
			//결함 정보 등록
        	defectService.insertdefect(defect);
        	// 새로운 파일 업로드 처리
            if (files != null && files.length > 0) {
                fileservice.handleFileUpload(files, "defect", defect.getSeq());
            }
            if (fixfiles != null && fixfiles.length > 0) {
                fileservice.handleFileUpload(fixfiles, "defectFix", defect.getSeq());
            }
            
            //첨부파일 등록
            List<FileAttachment> attachments = adminService.getAttachments(defect.getSeq(),31);
            defect.setDefectAttachment(attachments);
            List<FileAttachment> fixAttachments = adminService.getAttachments(defect.getSeq(),32);
            defect.setDefectFixAttachments(fixAttachments);
                    	
            response.put("status", "success");
            response.put("message", "결함 정보가 등록되었습니다");
            response.put("defect", defect);  // 등록된 개발 현황 진행 정보 반환
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.put("status", "failure");
            response.put("message", e.getMessage());  // 예외 메시지를 response에 포함시킴
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "결함 정보 등록 중에 오류가 발생했습니다.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}
		
    //결함 현황 수정 페이지
  	@GetMapping(value="api/defectDetail" , produces = MediaType.APPLICATION_JSON_VALUE)
  	@ResponseBody
  	public ResponseEntity<Map<String, Object>> defectEditPage2(HttpServletRequest request,
  			@RequestParam("seq") Integer seq) {
  		HttpSession session = request.getSession(false); // 세션이 없다면 새로 만들지 않음
  		if (session == null || session.getAttribute("authorityCode") == null) {
  			// 세션이 없거나 authorityCode가 없으면 401 Unauthorized 반환
  			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "권한이 없습니다. 로그인하세요."));
  			}
//  		Integer authorityCode = (Integer) session.getAttribute("authorityCode");
  		
  		Map<String, Object> response = new HashMap<>();
  		
  		Defect defectDetail = defectService.getDefectById(seq); // seq를 입력해서 해당 결함 데이터 가져오기
  		List<FileAttachment> attachments = adminService.getAttachments(seq, 31); // 첨부 파일 저장
  		defectDetail.setDefectAttachment(attachments);
  		List<FileAttachment> fixAttachments = adminService.getAttachments(seq, 32);
  		defectDetail.setDefectFixAttachments(fixAttachments);

  	    // 응답 생성
  		response.put("status", "success");
        response.put("message", "개발 진행 현황 정보 전달.");
  	    response.put("defectDetail", defectDetail);
  	    response.put("attachments", attachments);
  	    response.put("fixAttachments", fixAttachments);
  	    
  	    return ResponseEntity.ok(response); // JSON으로 응답 반환
  	}	
  	
  	//개발 진행 현황 수정
    @PostMapping(value = "api/defectEdit", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> defectEdit(HttpServletRequest request,
			@RequestPart("defect") Defect defect,
			@RequestPart(value = "file", required = false) MultipartFile[] files,
			@RequestPart(value = "fixfile", required = false) MultipartFile[] fixfiles) {
		HttpSession session = request.getSession(false); // 세션이 없다면 새로 만들지 않음
//		if (session == null || session.getAttribute("authorityCode") == null) {
//			// 세션이 없거나 authorityCode가 없으면 401 Unauthorized 반환
//			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "권한이 없습니다. 로그인하세요."));
//			}
		String UserID = (String) session.getAttribute("id");
		String UserName = (String) session.getAttribute("name");
		Map<String, Object> response = new HashMap<>();
		
		//코드로 들어오는 데이터를 코드명으로 변경
		defect.setMajorCategory(adminService.getStageCodes("대", defect.getMajorCategory()));
		defect.setSubCategory(adminService.getStageCodes("중", defect.getSubCategory()));
		defect.setDefectType(adminService.getStageCCodes("13", defect.getDefectType()));
		defect.setDefectSeverity(adminService.getStageCCodes("14", defect.getDefectSeverity()));
		defect.setDefectStatus(adminService.getStageCCodes("15", defect.getDefectStatus()));
		defect.setTestStage(adminService.getStageCCodes("11", defect.getTestStage()));
				
		try {
			// 테스트 ID 비어있을 경우 프로그램 ID 입력
			if(defect.getTestId() == null || defect.getTestId().trim().isEmpty()) {
				defect.setTestId(defect.getProgramId());
			}
			//최초 결함 등록자 로그인 ID 세팅
			defect.setDefectRegistrar(UserName);
			
			// 시작예정일이 종료예정일보다 뒤일 경우 에러
			if (defect.getDefectScheduledDate() != null && defect.getDefectCompletionDate() != null) {
			    if (defect.getDefectScheduledDate().after(defect.getDefectCompletionDate())) {
			        throw new IllegalArgumentException("결함 조치 예정일의 시작일은 종료일보다 앞서야 합니다.");
			    }
			}
			
			// 조치완료일, PL 확인일에 따른 결함 처리 상태 자동 세팅
			if(defect.getDefectCompletionDate() == null && defect.getPlConfirmDate() == null) {
				defect.setDefectStatus("등록완료");}
			if(defect.getDefectCompletionDate() != null && defect.getPlConfirmDate() == null) {
				defect.setDefectStatus("조치완료");}
			if(defect.getDefectCompletionDate() != null && defect.getPlConfirmDate() != null && defect.getDefectRegConfirmDate() == null) {
				defect.setDefectStatus("PL 확인완료");}
			if(defect.getDefectCompletionDate() != null && defect.getPlConfirmDate() != null && defect.getDefectRegConfirmDate() != null) {
				defect.setDefectStatus("등록자 확인완료");}
			
			//등록된 결함 파일 가져오기
			Defect DefectEdit = defectService.getDefectById(defect.getSeq());
			//최초변경자 가져오기
			defect.setInitCreater(DefectEdit.getInitCreater());
			// defect의 필드를 DefectEdit에 복사
		    BeanUtils.copyProperties(defect, DefectEdit);
		    //최초등록자, 최종변경자 세팅
			DefectEdit.setLastModifier(UserName);
		    // 공지사항에 등록된 기존 첨부파일 전부 삭제
	        adminService.deleteAttachmentsByNoticeId(defect.getSeq(),31);
	        adminService.deleteAttachmentsByNoticeId(defect.getSeq(),32);
	        // 업데이트된 공지사항을 저장
	        defectService.updateDefect(DefectEdit);
	        
	        validateRequiredField(defect.getMajorCategory(), "업무 대분류");
			validateRequiredField(defect.getSubCategory(), "업무 중분류");
			validateRequiredField(defect.getTestStage(), "테스트 단계");
			validateRequiredField(defect.getTestId(), "테스트ID");
			validateRequiredField(defect.getDefectType(), "결함유형");
			validateRequiredField(defect.getDefectSeverity(), "결함심각도");
			validateRequiredField(defect.getDefectDescription(), "결함 내용");
			validateRequiredField(defect.getProgramId(), "프로그램ID");
			validateRequiredField(defect.getDefectHandler(), "조치담당자");
			validateRequiredField(defect.getPl(), "PL");
			if(defect.getDefectDiscoveryDate() == null) {
				throw new IllegalArgumentException("결함발생일은 필수 입력 항목입니다.");
			}
        	
        	// 새로운 파일 업로드 처리
            if (files != null && files.length > 0) {
            	fileservice.handleFileUpload(files, "defect", defect.getSeq());
            }
            if (fixfiles != null && fixfiles.length > 0) {
            	fileservice.handleFileUpload(fixfiles, "defectFix", defect.getSeq());
            }
            List<FileAttachment> attachments = adminService.getAttachments(defect.getSeq(),31);
            DefectEdit.setDefectAttachment(attachments);
            List<FileAttachment> fixAttachments = adminService.getAttachments(defect.getSeq(),32);
            DefectEdit.setDefectFixAttachments(fixAttachments);
	
	        // 성공 응답 생성
	        response.put("status", "success");
	        response.put("message", "결함 현황이 성공적으로 수정되었습니다.");
	        response.put("DefectEdit", DefectEdit);
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    } catch (IllegalArgumentException e) {
	        response.put("status", "failure");
	        response.put("message", e.getMessage());
	        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("status", "error");
	        response.put("message", "결함 현황 수정 중에 오류 발생했습니다");
	        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
		
	//결함 현황 삭제
	@DeleteMapping(value= "api/deleteDefect", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteDefect(@RequestBody List<Integer> seqs) {
        Map<String, Object> response = new HashMap<>();
        
        for (Integer seq : seqs) {
            defectService.deleteDefect(seq,31); // 첨부파일도 같이 삭제하기 위해 첨부파일 type을 입력
            defectService.deleteDefect(seq,32);
        }
        
        response.put("status", "success");
        response.put("message", "결함이 성공적으로 삭제되었습니다.");
        return ResponseEntity.ok(response);
    }
	
	// 전체 개발 진행 현황 정보를 엑셀로 다운로드
    @GetMapping("/defectdownloadAll")
    public void downloadAlldefect(HttpServletResponse response) throws IOException {
    	List<Defect> defects = defectService.searchAllDefects();
        defectexportToExcel(response, defects, "all_defects_codes.xlsx");
    }
    
    // 액셀 파일 예시를 다운로드
    @GetMapping("/defectsexampleexcel")
    public void downloadExdefects(HttpServletResponse response) throws IOException {
    	List<Defect> defects = defectService.searchDefects("no_value", null, null, null, 999999, null, null, null, null, null,null, null, null, 1, 15);  	
    	defectexportToExcel(response, defects, "example.xlsx");
    }
    
    // 조회된 결함 정보를 엑셀로 다운로드
    @GetMapping("/defectdownloadFiltered")
    public void downloadFiltereddefects(
    		@RequestParam(value = "testStage", required = false) String testStage,
            @RequestParam(value = "majorCategory", required = false) String majorCategory,
            @RequestParam(value = "subCategory", required = false) String subCategory,
            @RequestParam(value = "defectSeverity", required = false) String defectSeverity,
            @RequestParam(value = "seq", required = false) Integer seq,
            @RequestParam(value = "defectRegistrar", required = false) String defectRegistrar,
            @RequestParam(value = "defectHandler", required = false) String defectHandler,
            @RequestParam(value = "Pl", required = false) String pl,
            @RequestParam(value = "defectStatus", required = false) String defectStatus,
            @RequestParam(value = "programId", required = false) String programId,
            @RequestParam(value = "testId", required = false) String testId,
            @RequestParam(value = "programName", required = false) String programName,
            @RequestParam(value = "programType", required = false) String programType,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "15") int size,
            HttpServletResponse response) throws IOException {
    	
    	majorCategory = adminService.getStageCodes("대", majorCategory);
		subCategory = adminService.getStageCodes("중", subCategory);
		defectSeverity = adminService.getStageCCodes("14", defectSeverity);
		defectStatus = adminService.getStageCCodes("15", defectStatus);
		testStage = adminService.getStageCCodes("11", testStage);
		
		// 결함 목록 조회
	    List<Defect> defects = defectService.searchDefects(testStage, majorCategory, subCategory, defectSeverity, seq, defectRegistrar, 
				defectHandler, pl,  defectStatus, programId, testId, programName, programType, page, size);

    	defectexportToExcel(response, defects, "filtered_defects_codes.xlsx");
    }
    
    // 엑셀 파일로 데이터를 내보내는 메서드
    private void defectexportToExcel(HttpServletResponse response, List<Defect> Defects, String fileName) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Defect");
        String check = "";
        if (Defects.isEmpty()) {
    		check = "no_value";
    		}
        // 헤더 이름 배열
        String[] headers = {
        	    "SEQ", "TEST_STAGE", "MAJOR_CATEGORY", "SUB_CATEGORY", "TEST_ID", 
        	    "PROGRAM_TYPE", "PROGRAM_ID", "PROGRAM_NAME", "DEFECT_TYPE", 
        	    "DEFECT_SEVERITY", "DEFECT_DESCRIPTION", "DEFECT_REGISTRAR", "DEFECT_DISCOVERY_DATE", 
        	    "DEFECT_HANDLER", "DEFECT_SCHEDULED_DATE", "DEFECT_COMPLETION_DATE", 
        	    "DEFECT_RESOLUTION_DETAILS", "PL", "PL_CONFIRM_DATE", "ORIGINAL_DEFECT_NUMBER", 
        	    "PL_DEFECT_JUDGE_CLASS", "PL_COMMENTS", "DEFECT_REG_CONFIRM_DATE", 
        	    "DEFECT_REGISTRAR_COMMENT", "DEFECT_STATUS", "INIT_CREATED_DATE", 
        	    "INIT_CREATER", "LAST_MODIFIED_DATE", "LAST_MODIFIER"
        	};

        // 헤더 생성
        Row headerRow = sheet.createRow(0);
        if (!check.equals("")) {
        	// "SEQ"를 무시하고 다음 헤더부터 시작
        	log.info("check"+check);
            for (int i = 1; i < headers.length-4; i++) {
                headerRow.createCell(i - 1).setCellValue(headers[i]);
            }
            headerRow.createCell(24).setCellValue("INIT_CREATER");
            headerRow.createCell(25).setCellValue("LAST_MODIFIER");
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
        	    "getseq", "getTestStage", "getMajorCategory", "getSubCategory", 
        	    "getTestId", "getProgramType", "getProgramId", "getProgramName", 
        	    "getDefectType", "getDefectSeverity", "getDefectDescription", 
        	    "getDefectRegistrar", "getdefectDiscoveryDate", "getDefectHandler", 
        	    "getDefectScheduledDate", "getDefectCompletionDate", 
        	    "getDefectResolutionDetails", "getPl", "getPlConfirmDate", 
        	    "getOriginalDefectNumber", "getPlDefectJudgeClass", "getPlComments", 
        	    "getDefectRegConfirmDate", "getDefectRegistrarComment", 
        	    "getDefectStatus", "getInitCreatedDate", "getInitCreater", 
        	    "getLastModifiedDate", "getLastModifier"
        	};

        // 데이터 행 생성
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
        // 엑셀 파일을 HTTP 응답으로 내보내기
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
            response.put("message", "파일이 없습니다. 다시 시도해 주세요.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            // 예상하는 컬럼명 리스트
            List<String> expectedHeaders = Arrays.asList("TEST_STAGE", "MAJOR_CATEGORY", "SUB_CATEGORY", "TEST_ID", 
            	    "PROGRAM_TYPE", "PROGRAM_ID", "PROGRAM_NAME", "DEFECT_TYPE", 
            	    "DEFECT_SEVERITY", "DEFECT_DESCRIPTION", "DEFECT_REGISTRAR", "DEFECT_DISCOVERY_DATE", 
            	    "DEFECT_HANDLER", "DEFECT_SCHEDULED_DATE", "DEFECT_COMPLETION_DATE", 
            	    "DEFECT_RESOLUTION_DETAILS", "PL", "PL_CONFIRM_DATE", "ORIGINAL_DEFECT_NUMBER", 
            	    "PL_DEFECT_JUDGE_CLASS", "PL_COMMENTS", "DEFECT_REG_CONFIRM_DATE", 
            	    "DEFECT_REGISTRAR_COMMENT", "DEFECT_STATUS", "INIT_CREATER", "LAST_MODIFIER");
            if (!fileservice.isHeaderValid(headerRow, expectedHeaders)) { // 컬럼명 비교
                response.put("status", "error");
                response.put("message", "헤더의 컬럼명이 올바르지 않습니다.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            List<Defect> defect = new ArrayList<>();
            
            // 필드명 배열과 대응되는 셀 타입 배열
            String[] fieldNames = {
            	    "testStage", "majorCategory", "subCategory", 
            	    "testId", "programType", "programId", "programName", 
            	    "defectType", "defectSeverity", "defectDescription", 
            	    "defectRegistrar", "defectDiscoveryDate", "defectHandler", 
            	    "defectScheduledDate", "defectCompletionDate", 
            	    "defectResolutionDetails", "pl", "plConfirmDate", 
            	    "originalDefectNumber", "plDefectJudgeClass", "plComments", 
            	    "defectRegConfirmDate", "defectRegistrarComment", 
            	    "defectStatus", "initCreater", "lastModifier"
            	};

            // 첫 번째 행은 헤더이므로 건너뜁니다.
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
                                field.set(deFect, (int) fileservice.getCellValueAsNumeric(row.getCell(j)));
                                break;
                            case "Date":
                                field.set(deFect, fileservice.getCellValueAsDate(row.getCell(j)));
                                break;
                            default:
                            	log.info(field.getType().getSimpleName());
                                field.set(deFect, fileservice.getCellValueAsString(row.getCell(j)));
                                break;
                        }
                        }
                    	defect.add(deFect);
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
                defectService.saveAllDefect(defect);
                response.put("status", "success");
                response.put("message", "파일 업로드가 성공적으로 완료되었습니다!");
                response.put("totalUploaded", defect.size());
            } catch (Exception e) {
                e.printStackTrace();
                response.put("status", "error");
                response.put("message", e.getMessage());
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }

        } catch (DuplicateKeyException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "중복된 결함 정보가 발견되었습니다.");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        } catch (IOException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "파일 처리 중 오류가 발생했습니다. 다시 시도해 주세요.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    // String Value값 필수값 유효성 점검
    private void validateRequiredField(String fieldValue, String fieldName) {
        if (fieldValue == null || fieldValue.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + "은(는) 필수 입력 항목입니다.");
        }
    }
}
