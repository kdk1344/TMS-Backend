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
		
		// 결함 목록 조회
		 List<testProgress> testProgressList = testService.searchTestProgress(testStage, majorCategory, subCategory, programType, testId, programName, programId, developer, testStatus, pl, ItMgr, BusiMgr, execCompanyMgr, thirdPartyTestMgr, page, size);

	    // 총 결함 수 조회
	    int totalDefects = testService.getTotalTestCount(testStage, majorCategory, subCategory, programType, testId,
				 programName, programId, developer, testStatus, pl, ItMgr, BusiMgr, execCompanyMgr, thirdPartyTestMgr);
	    // 총 페이지 수 계산
	    int totalPages = (int) Math.ceil((double) totalDefects / size);
	    

		// 응답 생성
	    response.put("testProgress", testProgressList);
	    response.put("currentPage", page);
	    response.put("totalPages", totalPages);
	    response.put("totalDefects", totalDefects);

	    return ResponseEntity.ok(response); // JSON으로 응답 반환
	}
	
	

	
	
}
