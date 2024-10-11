package com.tms.backend.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLIntegrityConstraintViolationException;

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
import com.tms.backend.service.DefectService;
import com.tms.backend.service.DevService;
import com.tms.backend.service.FileService;
import com.tms.backend.service.UserService;
import com.tms.backend.vo.CommonCode;
import com.tms.backend.vo.Criteria;
import com.tms.backend.vo.Defect;
import com.tms.backend.vo.FileAttachment;
import com.tms.backend.vo.Notice;
import com.tms.backend.vo.PageDTO;
import com.tms.backend.vo.User;
import com.tms.backend.vo.categoryCode;
import com.tms.backend.vo.devProgress;
import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@RequestMapping("/*")
public class devProgressController {
	
	@Autowired
    private DevService devservice;
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private FileService fileservice;
	
	@Autowired
	private DefectService defectservice;
	
	//����������� Controller
	
	@GetMapping("/devProgress")
	public String devProgressPage() {

	    return "devProgress"; // JSP �������� �̵�
	}
	
	//���� ���� ��Ȳ ��ȸ
	@GetMapping(value="api/devProgress" , produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> APIdevProgressPage(HttpServletRequest request,
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
		
//		HttpSession session = request.getSession(false); // ������ ���ٸ� ���� ������ ����
//		if (session == null || session.getAttribute("authorityCode") == null) {
//			// ������ ���ų� authorityCode�� ������ 401 Unauthorized ��ȯ
//			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "������ �����ϴ�. �α����ϼ���."));
//			}
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd"); // ��¥ ������ ����

		majorCategory = adminService.getStageCodes("��", majorCategory); // ���ڷ� �� ������ ���ڷ� ��ȯ
		subCategory = adminService.getStageCodes("��", subCategory);
		if (programType != null && !programType.isEmpty()) {
			programType = adminService.getStageCCodes("02", programType);}
		if (programStatus != null && !programStatus.isEmpty()) {
			programStatus = adminService.getStageCCodes("05", programStatus);}
		if (devStatus != null && !devStatus.isEmpty()) {
			devStatus = adminService.getStageCCodes("06", devStatus);}
		
	    List<devProgress> devProgressList = devservice.searchDevProgress(
	            majorCategory, subCategory, programType, programName, programId, programStatus, developer,
	            devStatus, devStartDate, devEndDate, pl, thirdPartyTestMgr, ItMgr, BusiMgr, page, size
	    );

	    int totalDevProgress = devservice.getTotalDevProgressCount(
	            majorCategory, subCategory, programType, programName, programId, programStatus, developer,
	            devStatus, devStartDate, devEndDate, pl, thirdPartyTestMgr, ItMgr, BusiMgr
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
	
	//������ Ȯ��
	@GetMapping(value = "api/developer", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDevUser() {
    	Map<String, Object> response = new HashMap<>();
        List<User> developer = adminService.findAuthorityCode(5);
        
        // ���� ������ ����
        if (developer != null && !developer.isEmpty()) {
            response.put("status", "success");
            response.put("developer", developer);
        } else {
            response.put("status", "failure");
            response.put("message", "No developer found.");
            response.put("developer", developer);
        }

        // ��ȸ�� ����� ��ȯ
        return ResponseEntity.ok(response);
    }
	
	//���α׷� ID Ȯ��
	@GetMapping(value = "api/programList", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getprogramList(
    		@RequestParam(value = "programType", required = false) String programType,
            @RequestParam(value = "developer", required = false) String developer,
            @RequestParam(value = "programId", required = false) String programId,
            @RequestParam(value = "programName", required = false) String programName) {
		if (programType != null && !programType.isEmpty()) {
			programType = adminService.getStageCCodes("02", programType);}
		
    	Map<String, Object> response = new HashMap<>();
    	List<devProgress> programList = devservice.checkProgramId(programType, developer, programId, programName);

        // ���� ������ ����
        if (programList != null && !programList.isEmpty()) {
            response.put("status", "success");
            response.put("programList", programList);
        } else {
            response.put("status", "failure");
            response.put("message", "���α׷� ID ������ ã�� �� �����ϴ�");
        }

        // ��ȸ�� ����� ��ȯ
        return ResponseEntity.ok(response);
    }
	
	//���α׷� Ÿ�� Ȯ��
	@GetMapping(value = "api/programType", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getProgramType() {
    	Map<String, Object> response = new HashMap<>();
    	List<CommonCode> ProgramType= adminService.getCCCode("02");

        // ���� ������ ����
        if (ProgramType != null && !ProgramType.isEmpty()) {
            response.put("status", "success");
            response.put("programType", ProgramType);
        } else {
            response.put("status", "failure");
            response.put("message", "���α׷� Ÿ�� ������ ã�� �� �����ϴ�");
        }

        // ��ȸ�� ����� ��ȯ
        return ResponseEntity.ok(response);
    }
	
	//���α׷� �� ���� Ȯ��
	@GetMapping(value = "api/programDtype", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getProgramDType() {
    	Map<String, Object> response = new HashMap<>();
    	List<CommonCode> ProgramDType= adminService.getCCCode("03");

        // ���� ������ ����
        if (ProgramDType != null && !ProgramDType.isEmpty()) {
            response.put("status", "success");
            response.put("programDtype", ProgramDType);
        } else {
            response.put("status", "failure");
            response.put("message", "���α׷� �� ���� ������ ã�� �� �����ϴ�");
        }

        // ��ȸ�� ����� ��ȯ
        return ResponseEntity.ok(response);
    }
	
	//�켱�� �� ���̵� Ȯ��
	@GetMapping(value = "api/levels", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getlevels() {
    	Map<String, Object> response = new HashMap<>();
    	List<CommonCode> levels= adminService.getCCCode("04");

        // ���� ������ ����
        if (levels != null && !levels.isEmpty()) {
            response.put("status", "success");
            response.put("levels", levels);
        } else {
            response.put("status", "failure");
            response.put("message", "�켱�� �� ���̵� ������ ã�� �� �����ϴ�");
        }

        // ��ȸ�� ����� ��ȯ
        return ResponseEntity.ok(response);
    }
	
	//���α׷� ���� Ȯ��
	@GetMapping(value = "api/programStatus", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getProgramStatus() {
    	Map<String, Object> response = new HashMap<>();
    	List<CommonCode> ProgramStatus= adminService.getCCCode("05");

        // ���� ������ ����
        if (ProgramStatus != null && !ProgramStatus.isEmpty()) {
            response.put("status", "success");
            response.put("programStatus", ProgramStatus);
        } else {
            response.put("status", "failure");
            response.put("message", "���α׷� ���� ������ ã�� �� �����ϴ�");
        }

        // ��ȸ�� ����� ��ȯ
        return ResponseEntity.ok(response);
    }
	
	//����� ID Ȯ��
	@GetMapping(value = "api/user", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUserIDLIST() {
    	Map<String, Object> response = new HashMap<>();
    	List<User> User= adminService.getUserList();

        // ���� ������ ����
        if (User != null && !User.isEmpty()) {
            response.put("status", "success");
            response.put("user", User);
        } else {
            response.put("status", "failure");
            response.put("message", "����� ������ ã�� �� �����ϴ�");
        }

        // ��ȸ�� ����� ��ȯ
        return ResponseEntity.ok(response);
    }
	
	//�켱�� �� ���̵� Ȯ��
	@GetMapping(value = "api/testResult", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getTestResult() {
    	Map<String, Object> response = new HashMap<>();
    	List<CommonCode> TestResult= adminService.getCCCode("07");

        // ���� ������ ����
        if (TestResult != null && !TestResult.isEmpty()) {
            response.put("status", "success");
            response.put("testResult", TestResult);
        } else {
            response.put("status", "failure");
            response.put("message", "�׽�Ʈ ����� ã�� �� �����ϴ�");
        }

        // ��ȸ�� ����� ��ȯ
        return ResponseEntity.ok(response);
    }
	
	//IT ����� Ȯ��
	@GetMapping(value = "api/itMgr", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getITMGR() {
    	Map<String, Object> response = new HashMap<>();
    	List<User> ITMGR= adminService.findAuthorityCode(7);

        // ���� ������ ����
        if (ITMGR != null && !ITMGR.isEmpty()) {
            response.put("status", "success");
            response.put("itMgr", ITMGR);
        } else {
            response.put("status", "failure");
            response.put("message", "IT ����� ������ ã�� �� �����ϴ�");
        }

        // ��ȸ�� ����� ��ȯ
        return ResponseEntity.ok(response);
    }
	
	//��� ����� Ȯ��
	@GetMapping(value = "api/busiMgr", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getBUSIMGR() {
    	Map<String, Object> response = new HashMap<>();
    	List<User> BUSIMGR= adminService.findAuthorityCode(8);

        // ���� ������ ����
        if (BUSIMGR != null && !BUSIMGR.isEmpty()) {
            response.put("status", "success");
            response.put("busiMgr", BUSIMGR);
        } else {
            response.put("status", "failure");
            response.put("message", "��� ����� ������ ã�� �� �����ϴ�");
        }

        // ��ȸ�� ����� ��ȯ
        return ResponseEntity.ok(response);
    }
	
	//���� ���� ���� Ȯ��
	@GetMapping(value = "api/devStatus", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDevStatus() {
    	Map<String, Object> response = new HashMap<>();
    	List<CommonCode> devStatus= adminService.getCCCode("06");

        // ���� ������ ����
        if (devStatus != null && !devStatus.isEmpty()) {
            response.put("status", "success");
            response.put("devStatus", devStatus);
        } else {
            response.put("status", "failure");
            response.put("message", "��з� ������ ã�� �� �����ϴ�");
        }

        // ��ȸ�� ����� ��ȯ
        return ResponseEntity.ok(response);
    }
	
	@GetMapping("/devProgressReg")
	public String devProgressRegPage() {

	    return "devProgressReg"; // JSP �������� �̵�
	}
	
	//���� ���� ��Ȳ ��� ������
	@PostMapping(value="api/devProgressReg" , produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> devProgressReg(HttpServletRequest request,
			@RequestPart("devProgress") devProgress devProgress,
			@RequestPart(value = "file", required = false) MultipartFile[] files) {
		HttpSession session = request.getSession(false); // ������ ���ٸ� ���� ������ ����
		if (session == null || session.getAttribute("authorityCode") == null) {
			// ������ ���ų� authorityCode�� ������ 401 Unauthorized ��ȯ
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "������ �����ϴ�. �α����ϼ���."));
			}
		String UserID = (String) session.getAttribute("id");
		String UserName = (String) session.getAttribute("name");
		Map<String, Object> response = new HashMap<>();
		try {
			//�ڵ�� ������ �����͸� �ڵ������ ����
			devProgress.setMajorCategory(adminService.getStageCodes("��", devProgress.getMajorCategory()));
			devProgress.setSubCategory(adminService.getStageCodes("��", devProgress.getSubCategory()));
			devProgress.setProgramType(adminService.getStageCCodes("02", devProgress.getProgramType()));
			devProgress.setProgramDetailType(adminService.getStageCCodes("03", devProgress.getProgramDetailType()));
			devProgress.setPriority(adminService.getStageCCodes("04", devProgress.getPriority()));
			devProgress.setDifficulty(adminService.getStageCCodes("04", devProgress.getDifficulty()));
			devProgress.setProgramStatus(adminService.getStageCCodes("05", devProgress.getProgramStatus()));
			devProgress.setPlTestResult(adminService.getStageCCodes("07", devProgress.getPlTestResult()));
			devProgress.setItTestResult(adminService.getStageCCodes("07", devProgress.getItTestResult()));
			devProgress.setBusiTestResult(adminService.getStageCCodes("07", devProgress.getBusiTestResult()));
			devProgress.setThirdTestResult(adminService.getStageCCodes("07", devProgress.getThirdTestResult()));
			devProgress.setDevStatus(adminService.getStageCCodes("06", devProgress.getDevStatus()));
			
			// �ʼ� �׸� üũ
			fileservice.validateRequiredField(devProgress.getMajorCategory(), "���� ��з�");
			fileservice.validateRequiredField(devProgress.getSubCategory(), "���� �ߺз�");
			fileservice.validateRequiredField(devProgress.getProgramId(), "���α׷� ID");
			fileservice.validateRequiredField(devProgress.getProgramName(), "���α׷���");
			fileservice.validateRequiredField(devProgress.getDeveloper(), "������");
			fileservice.validateRequiredField(devProgress.getPriority(), "�켱����");
			fileservice.validateRequiredField(devProgress.getProgramStatus(), "���α׷� ����");
	        if ("����".equals(devProgress.getProgramStatus()) &&
	        	(devProgress.getDeletionReason() == null || devProgress.getDeletionReason().trim().isEmpty())) {
	        	throw new IllegalArgumentException("���α׷� ���°� '����'�� ��� ����ó������ �Է��� �ʼ��Դϴ�.");
	        }
	        // Date �ʼ��� �ʵ� üũ
	        fileservice.validateDate(devProgress.getPlannedStartDate(), "���� ������");
	        fileservice.validateDate(devProgress.getPlannedEndDate(), "�Ϸ� ������");
	        // ���ۿ������� ���Ό���Ϻ��� ���� ��� ����
 			if (devProgress.getPlannedStartDate() != null && devProgress.getPlannedEndDate() != null) {
 			    if (devProgress.getPlannedStartDate().after(devProgress.getPlannedEndDate())) {
 			        throw new IllegalArgumentException("���� ���� �������� �������� �����Ϻ��� �ռ��� �մϴ�.");
 			    }
 			}
	        //�׽�Ʈ �Ϸ����� ������ �׽�Ʈ ����� null üũ
	        TestEndDateNullCheck(devProgress.getPlTestCmpDate(), devProgress.getPlTestResult(), "PL");
	        TestEndDateNullCheck(devProgress.getThirdPartyConfirmDate(), devProgress.getThirdTestResult(), "��3��");
	        TestEndDateNullCheck(devProgress.getItConfirmDate(), devProgress.getItTestResult(), "�� IT");
	        TestEndDateNullCheck(devProgress.getBusiConfirmDate(), devProgress.getBusiTestResult(), "�� ����");        
	        
	        // ������ üũ �ڵ� ���� - �׽�Ʈ ������ Null�� ��� �׽�Ʈ �Ϸ����� ���� 
	        fileservice.setIfNullDate(devProgress.getPlTestCmpDate(), devProgress.getPlTestScdDate(), devProgress::setPlTestScdDate);
	        fileservice.setIfNullDate(devProgress.getThirdPartyConfirmDate(), devProgress.getThirdPartyTestDate(), 
	        			devProgress::setThirdPartyTestDate);
	        fileservice.setIfNullDate(devProgress.getItConfirmDate(), devProgress.getItTestDate(), devProgress::setItTestDate);
	        fileservice.setIfNullDate(devProgress.getBusiConfirmDate(), devProgress.getBusiTestDate(), devProgress::setBusiTestDate);
	        
	        //���α׷� ���°� �����϶� ����ó���ڴ� �ڵ����� ����� ID, ����ó������ ���� ����
	        if (("����".equals(devProgress.getProgramStatus())) && (devProgress.getDeletionHandler() == null)) {
	        	devProgress.setDeletionHandler(UserName);
	        	devProgress.setDeletionDate(new Date());
	        }
	        // ������ ���������� +2�� �׽�Ʈ �������� Null�� ��� ����
	        if (devProgress.getDevtestendDate() != null && devProgress.getPlTestScdDate() == null) {
	        	Date DevtestDate = devProgress.getDevtestendDate();
	        	long twoDaysInMillis = 2 * 24 * 60 * 60 * 1000L;
	        	Date newDate = new Date(DevtestDate.getTime() + twoDaysInMillis);
	        	devProgress.setPlTestScdDate(newDate);
	        }
	        // ���������Ͽ� ���� �������� ���� ���� ó��
	        if (devProgress.getActualStartDate() == null && devProgress.getActualEndDate() == null) {
	        	devProgress.setDevStatus("������");
	        } else if(devProgress.getActualStartDate() != null && devProgress.getActualEndDate() == null) {
	        	devProgress.setDevStatus("������");
	        } else if(devProgress.getActualStartDate() != null && devProgress.getActualEndDate() != null) {
	        	devProgress.setDevStatus("���߿Ϸ�");
	        } else if(devProgress.getActualStartDate() != null && devProgress.getActualEndDate() != null
	        		&& devProgress.getDevProgAttachment().size() >= 1 && devProgress.getPlTestCmpDate() == null) {
	        	devProgress.setDevStatus("������ ���׿Ϸ�");
	        } else if(devProgress.getPlTestCmpDate() != null && devProgress.getItConfirmDate() == null) {
	        	devProgress.setDevStatus("PL Ȯ�οϷ�");
	        } else if(devProgress.getPlTestCmpDate() != null && devProgress.getItConfirmDate() != null
	        		&& devProgress.getBusiConfirmDate() == null) {
	        	devProgress.setDevStatus("��IT Ȯ�οϷ�");
	        } else if(devProgress.getPlTestCmpDate() != null && devProgress.getItConfirmDate() != null
	        		&& devProgress.getBusiConfirmDate() != null) {
	        	devProgress.setDevStatus("�� ���� Ȯ�οϷ�");
	        }
	        //���α׷� ID �ߺ�üũ
	        boolean IdCheck = devservice.checkCountProgramId(devProgress.getProgramId());
	        if(!IdCheck) {
	        	response.put("status", "failure");
	            response.put("message", "���α׷� ID�� �ߺ��Ǿ����ϴ�.");
	            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	        }
	        //���� �����, ������ �α��� ID ����
	        devProgress.setInitRegistrar(UserName);
	        devProgress.setLastModifier(UserName);
        	devservice.insertdevProgress(devProgress);  // ���� ��Ȳ ���� ���� �߰�
        	// ���ο� ���� ���ε� ó��
            if (files != null && files.length > 0) {
                fileservice.handleFileUpload(files, "devProgress", devProgress.getSeq());
            }
            List<FileAttachment> attachments = adminService.getAttachments(devProgress.getSeq(),1);
            devProgress.setDevProgAttachment(attachments);
            
            // ������ �����׽�Ʈ �Ϸ����� ÷�������� ���� �Էµɶ��� �Է� ����
            if (devProgress.getDevtestendDate() != null && (devProgress.getDevProgAttachment() == null || devProgress.getDevProgAttachment().isEmpty())) {
	            devProgress.setDevtestendDate(null);
	        	throw new IllegalArgumentException
	        	("�����׽�Ʈ ������ '�����׽�Ʈ�����'�� ÷������ ������ ������ ���������� ��� ���� ���Է� ���·� �ڵ� ����˴ϴ�.");
	        }
        	
            response.put("status", "success");
            response.put("message", "���� ���� ��Ȳ ������ ��ϵǾ����ϴ�");
            response.put("devProgress", devProgress);  // ��ϵ� ���� ��Ȳ ���� ���� ��ȯ
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.put("status", "failure");
            response.put("message", e.getMessage());  // ���� �޽����� response�� ���Խ�Ŵ
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "���� ���� ��Ȳ ���� ��� �߿� ������ �߻��߽��ϴ�.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}
	
	@GetMapping("/devProgressEdit")
	public String devProgressEditPage() {

	    return "devProgressEdit"; // JSP �������� �̵�
	}
	
	//���� ���� ��Ȳ ���� ������
	@GetMapping(value="api/devProgressDetail" , produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> devProgressEditPage2(HttpServletRequest request,
			@RequestParam("seq") Integer seq) {
		HttpSession session = request.getSession(false); // ������ ���ٸ� ���� ������ ����
		if (session == null || session.getAttribute("authorityCode") == null) {
			// ������ ���ų� authorityCode�� ������ 401 Unauthorized ��ȯ
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "������ �����ϴ�. �α����ϼ���."));
			}
//		Integer authorityCode = (Integer) session.getAttribute("authorityCode");
		
		
		Map<String, Object> response = new HashMap<>();
		//���� ���� ���� ��������
		devProgress DevProgressEdit = devservice.getDevById(seq);
		
		//���� ���� ��ư������ ���� defectCounts
		Map<String, Integer> defectCounts = new HashMap<>();
		defectCounts.put("totalDefectCount", defectservice.totalcountDefect(DevProgressEdit.getProgramId()));
		String[] types = { "thirdParty", "it", "busi" };
		for (String type : types) {
		    defectCounts.put(type + "DefectCount", defectservice.countDefect(DevProgressEdit.getProgramId(), type));
		    defectCounts.put(type + "SolutionCount", defectservice.countDefectSoultions(DevProgressEdit.getProgramId(), type));
		}
		//÷������ ��������
		List<FileAttachment> attachments = adminService.getAttachments(seq, 1);
		DevProgressEdit.setDevProgAttachment(attachments);

		defectCounts.put("thirdPartyDefectCount", defectservice.countDefect(DevProgressEdit.getProgramId(), "thirdParty"));
		defectCounts.put("itDefectCount", defectservice.countDefect(DevProgressEdit.getProgramId(), "it"));
		defectCounts.put("busiDefectCount", defectservice.countDefect(DevProgressEdit.getProgramId(), "busi"));
		defectCounts.put("thirdPartySolutionCount", defectservice.countDefectSoultions(DevProgressEdit.getProgramId(), "thirdParty"));
		defectCounts.put("itSolutionCount", defectservice.countDefectSoultions(DevProgressEdit.getProgramId(), "it"));
		defectCounts.put("busiSolutionCount", defectservice.countDefectSoultions(DevProgressEdit.getProgramId(), "busi"));

	    // ���� ����
		response.put("status", "success");
        response.put("message", "���� ���� ��Ȳ ���� ����.");
	    response.put("devProgress", DevProgressEdit);
	    response.put("defectCounts", defectCounts);
	    response.put("attachments", attachments);
	    
	    return ResponseEntity.ok(response); // JSON���� ���� ��ȯ
	}
	
	//���� ���� ��Ȳ ����
    @PostMapping(value = "api/devProgressEdit", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> devProgressEdit(HttpServletRequest request,
			@RequestPart("devProgress") devProgress devProgress,
			@RequestPart(value = "file", required = false) MultipartFile[] files) {
		HttpSession session = request.getSession(false); // ������ ���ٸ� ���� ������ ����
//		if (session == null || session.getAttribute("authorityCode") == null) {
//			// ������ ���ų� authorityCode�� ������ 401 Unauthorized ��ȯ
//			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "������ �����ϴ�. �α����ϼ���."));
//			}
		String UserID = (String) session.getAttribute("id");
		String UserName = (String) session.getAttribute("name");
		Map<String, Object> response = new HashMap<>();
				
		//�ڵ�� ������ �����͸� �ڵ������ ����
		devProgress.setMajorCategory(adminService.getStageCodes("��", devProgress.getMajorCategory()));
		devProgress.setSubCategory(adminService.getStageCodes("��", devProgress.getSubCategory()));
		devProgress.setProgramType(adminService.getStageCCodes("02", devProgress.getProgramType()));
		devProgress.setProgramDetailType(adminService.getStageCCodes("03", devProgress.getProgramDetailType()));
		devProgress.setPriority(adminService.getStageCCodes("04", devProgress.getPriority()));
		devProgress.setDifficulty(adminService.getStageCCodes("04", devProgress.getDifficulty()));
		devProgress.setProgramStatus(adminService.getStageCCodes("05", devProgress.getProgramStatus()));
		devProgress.setPlTestResult(adminService.getStageCCodes("07", devProgress.getPlTestResult()));
		devProgress.setItTestResult(adminService.getStageCCodes("07", devProgress.getItTestResult()));
		devProgress.setBusiTestResult(adminService.getStageCCodes("07", devProgress.getBusiTestResult()));
		devProgress.setThirdTestResult(adminService.getStageCCodes("07", devProgress.getThirdTestResult()));
		devProgress.setDevStatus(adminService.getStageCCodes("06", devProgress.getDevStatus()));
		
		try {
			// �ʼ� �׸� üũ
			fileservice.validateRequiredField(devProgress.getMajorCategory(), "���� ��з�");
			fileservice.validateRequiredField(devProgress.getSubCategory(), "���� �ߺз�");
			fileservice.validateRequiredField(devProgress.getProgramId(), "���α׷� ID");
			fileservice.validateRequiredField(devProgress.getProgramName(), "���α׷���");
			fileservice.validateRequiredField(devProgress.getDeveloper(), "������");
			fileservice.validateRequiredField(devProgress.getPriority(), "�켱����");
			fileservice.validateRequiredField(devProgress.getProgramStatus(), "���α׷� ����");
	        if ("����".equals(devProgress.getProgramStatus()) &&
	        	(devProgress.getDeletionReason() == null || devProgress.getDeletionReason().trim().isEmpty())) {
	        	throw new IllegalArgumentException("���α׷� ���°� '����'�� ��� ����ó������ �Է��� �ʼ��Դϴ�.");
	        }
	        // Date �ʵ� üũ
	        fileservice.validateDate(devProgress.getPlannedStartDate(), "���� ������");
	        fileservice.validateDate(devProgress.getPlannedEndDate(), "�Ϸ� ������");
	        // ���ۿ������� ���Ό���Ϻ��� ���� ��� ����
			if (devProgress.getPlannedStartDate() != null && devProgress.getPlannedEndDate() != null) {
			    if (devProgress.getPlannedStartDate().after(devProgress.getPlannedEndDate())) {
			        throw new IllegalArgumentException("���� ���� �������� �������� �����Ϻ��� �ռ��� �մϴ�.");
			    }
			}
	        //�׽�Ʈ �Ϸ����� ������ �׽�Ʈ ����� null üũ
	        TestEndDateNullCheck(devProgress.getPlTestCmpDate(), devProgress.getPlTestResult(), "PL");
	        TestEndDateNullCheck(devProgress.getThirdPartyConfirmDate(), devProgress.getThirdTestResult(), "��3��");
	        TestEndDateNullCheck(devProgress.getItConfirmDate(), devProgress.getItTestResult(), "�� IT");
	        TestEndDateNullCheck(devProgress.getBusiConfirmDate(), devProgress.getBusiTestResult(), "�� ����");        
	        	        
	        // ������ üũ �ڵ� ���� - �׽�Ʈ ������ Null�� ��� �׽�Ʈ �Ϸ����� ���� 
	        fileservice.setIfNullDate(devProgress.getPlTestCmpDate(), devProgress.getPlTestScdDate(), devProgress::setPlTestScdDate);
	        fileservice.setIfNullDate(devProgress.getThirdPartyConfirmDate(), devProgress.getThirdPartyTestDate(), 
	        			devProgress::setThirdPartyTestDate);
	        fileservice.setIfNullDate(devProgress.getItConfirmDate(), devProgress.getItTestDate(), devProgress::setItTestDate);
	        fileservice.setIfNullDate(devProgress.getBusiConfirmDate(), devProgress.getBusiTestDate(), devProgress::setBusiTestDate);
	        
	        //���α׷� ���°� �����϶� ����ó���ڴ� �ڵ����� ����� ID, ����ó������ ���� ����
	        if (("����".equals(devProgress.getProgramStatus())) && (devProgress.getDeletionHandler() == null)) {
	        	devProgress.setDeletionHandler(UserName);
	        	devProgress.setDeletionDate(new Date());
	        }
	        // ������ ���������� +2�� �׽�Ʈ �������� Null�� ��� ����
	        if (devProgress.getDevtestendDate() != null && devProgress.getPlTestScdDate() == null) {
	        	Date DevtestDate = devProgress.getDevtestendDate();
	        	long twoDaysInMillis = 2 * 24 * 60 * 60 * 1000L;
	        	Date newDate = new Date(DevtestDate.getTime() + twoDaysInMillis);
	        	devProgress.setPlTestScdDate(newDate);
	        }
	        // ���������Ͽ� ���� �������� ���� ���� ó��
	        if (devProgress.getActualStartDate() == null && devProgress.getActualEndDate() == null) {
	        	devProgress.setDevStatus("������");
	        } else if(devProgress.getActualStartDate() != null && devProgress.getActualEndDate() == null) {
	        	devProgress.setDevStatus("������");
	        } else if(devProgress.getActualStartDate() != null && devProgress.getActualEndDate() != null) {
	        	devProgress.setDevStatus("���߿Ϸ�");
	        } else if(devProgress.getActualStartDate() != null && devProgress.getActualEndDate() != null
	        		&& devProgress.getDevProgAttachment().size() >= 1 && devProgress.getPlTestCmpDate() == null) {
	        	devProgress.setDevStatus("������ ���׿Ϸ�");
	        } else if(devProgress.getPlTestCmpDate() != null && devProgress.getItConfirmDate() == null) {
	        	devProgress.setDevStatus("PL Ȯ�οϷ�");
	        } else if(devProgress.getPlTestCmpDate() != null && devProgress.getItConfirmDate() != null
	        		&& devProgress.getBusiConfirmDate() == null) {
	        	devProgress.setDevStatus("��IT Ȯ�οϷ�");
	        } else if(devProgress.getPlTestCmpDate() != null && devProgress.getItConfirmDate() != null
	        		&& devProgress.getBusiConfirmDate() != null) {
	        	devProgress.setDevStatus("�� ���� Ȯ�οϷ�");
	        }
	        
	        devProgress DevProgressEdit = devservice.getDevById(devProgress.getSeq()); // �Էµ��� ���� ������ �Է��ϱ� ���� DB���� ���� ������ ������
	        devProgress.setInitRegistrar(DevProgressEdit.getInitRegistrar()); // ���� ����� �Է�
	        //���α׷� ID �ߺ�üũ
	        if(!devProgress.getProgramId().equals(DevProgressEdit.getProgramId())) { // ���α׷� ID �ߺ� Ȯ��
		        boolean IdCheck = devservice.checkCountProgramId(devProgress.getProgramId());
		        if(!IdCheck) {
		        	response.put("status", "failure");
		            response.put("message", "���α׷� ID�� �ߺ��Ǿ����ϴ�.");
		            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		        }
	        }
	        
        	// devProgress�� �ʵ带 DevProgressEdit�� ����
    	    BeanUtils.copyProperties(devProgress, DevProgressEdit);
    	    
    	    // �������׿� ��ϵ� ���� ÷������ ���� ����
            adminService.deleteAttachmentsByNoticeId(DevProgressEdit.getSeq(), 1);

            // ���ο� ���� ���ε� ó��
            fileservice.handleFileUpload(files, "devProgress", DevProgressEdit.getSeq());
            List<FileAttachment> attachments = adminService.getAttachments(DevProgressEdit.getSeq(), 1);
            devProgress.setDevProgAttachment(attachments);
            
            // ������ �����׽�Ʈ �Ϸ����� ÷�������� ���� �Էµɶ��� �Է� ����
            if (devProgress.getDevtestendDate() != null && (devProgress.getDevProgAttachment() == null || devProgress.getDevProgAttachment().isEmpty())) {
	            devProgress.setDevtestendDate(null);
	        	throw new IllegalArgumentException
	        	("�����׽�Ʈ ������ '�����׽�Ʈ�����'�� ÷������ ������ ������ ���������� ��� ���� ���Է� ���·� �ڵ� ����˴ϴ�.");
	        }

            // ������Ʈ�� ���������� ����
            devservice.updatedevProgress(DevProgressEdit);

            // ���� ���� ����
            response.put("status", "success");
            response.put("message", "���� ���� ��Ȳ�� ���������� �����Ǿ����ϴ�.");
            response.put("DevProgressEdit", DevProgressEdit);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.put("status", "failure");
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "���α׷� ���߸�� ���� �߿� ���� �߻�");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	
	
	//���� ���� ��Ȳ ����
	@DeleteMapping(value= "api/deletedev", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deletedev(@RequestBody List<Integer> seqs) {
        Map<String, Object> response = new HashMap<>();
        
        for (int seq : seqs) { //�Էµ� seq ������� ����
            devservice.deleteDevProgress(seq, 1); // ÷������ ������ ���� type 1�� �Է�
        }
        
        response.put("status", "success");
        response.put("message", "���� ���� ��Ȳ ������ ���������� �����Ǿ����ϴ�.");
        return ResponseEntity.ok(response);
    }
	
	// ��ü ���� ���� ��Ȳ ������ ������ �ٿ�ε�
    @GetMapping("/devdownloadAll")
    public void downloadAlldev(HttpServletResponse response) throws IOException {
        List<devProgress> DevCodeList = devservice.searchDevProgress(null, null, null, null, null, null, null, null, null, null, null, null, null, null, 1, Integer.MAX_VALUE);
        devexportToExcel(response, DevCodeList, "all_dev_codes.xlsx");
    }
    
    // �׼� ���� ���ø� �ٿ�ε�
    @GetMapping("/devexampleexcel")
    public void downloadExdev(HttpServletResponse response) throws IOException {
    	List<devProgress> ExampleDEV = devservice.searchDevProgress("NO_VALUE", null, null, null, null, null, null, null, null, null, null, null, null, null, 1, Integer.MAX_VALUE);
    	devexportToExcel(response, ExampleDEV, "example.xlsx");
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
	        @RequestParam(value = "devStartDate", required = false) String devStartDate, //���߿Ϸ��� ������
	        @RequestParam(value = "devEndDate", required = false) String devEndDate, //���߿Ϸ��� �Ϸ���
	        @RequestParam(value = "pl", required = false) String pl,
	        @RequestParam(value = "thirdPartyTestMgr", required = false) String thirdPartyTestMgr,
	        @RequestParam(value = "ItMgr", required = false) String ItMgr,
	        @RequestParam(value = "BusiMgr", required = false) String BusiMgr,
	        @RequestParam(value = "page", defaultValue = "1") int page,
	        @RequestParam(value = "size", defaultValue = "15") int size,
            HttpServletResponse response) throws IOException {
    	
    	majorCategory = adminService.getStageCodes("��", majorCategory);
		subCategory = adminService.getStageCodes("��", subCategory);
		if (programType != null && !programType.isEmpty()) {
			programType = adminService.getStageCCodes("02", programType);}
		if (programStatus != null && !programStatus.isEmpty()) {
			programStatus = adminService.getStageCCodes("05", programStatus);}
		if (devStatus != null && !devStatus.isEmpty()) {
			devStatus = adminService.getStageCCodes("06", devStatus);}
    	
    	List<devProgress> filteredDevCodeList = devservice.searchDevProgress(
	            majorCategory, subCategory, programType, programName,programId, programStatus, developer,
	            devStatus, devStartDate, devEndDate, pl, thirdPartyTestMgr, ItMgr, BusiMgr, page, size
	    );

        devexportToExcel(response, filteredDevCodeList, "filtered_dev_codes.xlsx");
    }

    // ���� ���Ϸ� �����͸� �������� �޼���
    private void devexportToExcel(HttpServletResponse response, List<devProgress> devProgressList, String fileName) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("DevProgress");
        String check = "";
        if (devProgressList.isEmpty()) { // ���ε� ���� ���� �ٿ�ε� �� ���
    		check = "no_value";
    		}
        // ��� �̸� �迭
        String[] headers = {
            "SEQ","MAJOR_CATEGORY", "SUB_CATEGORY", "MINOR_CATEGORY", 
            "PROGRAM_DETAIL_TYPE", "PROGRAM_TYPE", "PROGRAM_ID", "PROGRAM_NAME", 
            "CLASS_NAME", "SCREEN_ID", "SCREEN_NAME", "SCREEN_MENU_PATH", 
            "PRIORITY", "DIFFICULTY", "ESTIMATED_EFFORT", "PROGRAM_STATUS", 
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

        // ��� ����
        Row headerRow = sheet.createRow(0);
        if (!check.equals("")) { // ���� ���� �ٿ�ε��ϴ� ���
        	// "SEQ"�� �����ϰ� ���� ������� ����
            for (int i = 1; i < headers.length; i++) {
                headerRow.createCell(i - 1).setCellValue(headers[i]);
            }
            headerRow.createCell(46).setCellValue("INIT_REGISTRAR");
            headerRow.createCell(47).setCellValue("LAST_MODIFIER");
        }
        else {
        	// "SEQ"�� �����Ͽ� ��� ��� ����
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }
	        headerRow.createCell(47).setCellValue("INIT_REG_DATE");
	        headerRow.createCell(48).setCellValue("INIT_REGISTRAR");
	        headerRow.createCell(49).setCellValue("LAST_MODIFIED_DATE");
	        headerRow.createCell(50).setCellValue("LAST_MODIFIER");
        }
        
        // ����� ������ ����
        String[] fields = {
            "getSeq","getMajorCategory", "getSubCategory", "getMinorCategory",
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

        // ������ �� ����
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
	            row.createCell(46).setCellValue(devProgress.getInitRegistrar());
	            row.createCell(47).setCellValue(devProgress.getLastModifier());
            } // �Ϲ����� �׼� ���� �ٿ�ε�
            else {
            	row.createCell(47).setCellValue(devProgress.getInitRegDate().toString());
	            row.createCell(48).setCellValue(devProgress.getInitRegistrar());
	            row.createCell(49).setCellValue(devProgress.getLastModifiedDate().toString());
	            row.createCell(50).setCellValue(devProgress.getLastModifier());
            }
        }
        // ���� ������ HTTP �������� ��������
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        try (OutputStream os = response.getOutputStream()) {
            workbook.write(os);
        }
        workbook.close();
    }
    
    // �׼� ���ε�
    @PostMapping(value = "api/devupload", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> devuploadExcelFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
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
            List<String> expectedHeaders = Arrays.asList("MAJOR_CATEGORY", "SUB_CATEGORY", "MINOR_CATEGORY", 
            		"PROGRAM_DETAIL_TYPE", "PROGRAM_TYPE", "PROGRAM_ID", "PROGRAM_NAME", "CLASS_NAME", "SCREEN_ID", 
            		"SCREEN_NAME", "SCREEN_MENU_PATH", "PRIORITY", "DIFFICULTY", "ESTIMATED_EFFORT", "PROGRAM_STATUS", 
            		"REQ_ID", "DELETION_HANDLER", "DELETION_DATE", "DELETION_REASON", "DEVELOPER", "PLANNED_START_DATE", 
            		"PLANNED_END_DATE", "ACTUAL_START_DATE", "ACTUAL_END_DATE", "DEV_TEST_END_DATE", 
                    "PL", "PL_TEST_SCD_DATE", 
            		"PL_TEST_CMP_DATE", "PL_TEST_RESULT", "PL_TEST_NOTES", "IT_MGR", "IT_TEST_DATE", "IT_CONFIRM_DATE", 
            		"IT_TEST_RESULT", "IT_TEST_NOTES", "BUSI_MGR", "BUSI_TEST_DATE", "BUSI_CONFIRM_DATE", "BUSI_TEST_RESULT", 
            		"BUSI_TEST_NOTES", "THIRD_PARTY_TEST_MGR", "THIRD_PARTY_TEST_DATE", "THIRD_PARTY_CONFIRM_DATE", 
            		"THIRD_TEST_RESULT", "THIRD_PARTY_TEST_NOTES", "DEV_STATUS", "INIT_REGISTRAR", "LAST_MODIFIER");
            if (!fileservice.isHeaderValid(headerRow, expectedHeaders)) { // �÷��� ��
                response.put("status", "error");
                response.put("message", "����� �÷����� �ùٸ��� �ʽ��ϴ�.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            List<devProgress> devProgress = new ArrayList<>();
            
            // �ʵ�� �迭�� �����Ǵ� �� Ÿ�� �迭
            String[] fieldNames = {
                "majorCategory", "subCategory", "minorCategory", "programDetailType",
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

            // ù ��° ���� ����̹Ƿ� �ǳʶݴϴ�.
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
                                    field.set(devprogress, (int) fileservice.getCellValueAsNumeric(row.getCell(j)));
                                    break;
                                case "Date":
                                    field.set(devprogress, fileservice.getCellValueAsDate(row.getCell(j)));
                                    break;
                                default:
                                    field.set(devprogress, fileservice.getCellValueAsString(row.getCell(j)));
                                    break;
                            }
                        }                    	
                        devProgress.add(devprogress);
                    } catch (Exception e) {
                        e.printStackTrace();
                        response.put("status", "error");
                        response.put("message", "�ùٸ��� ���� ���� �ԷµǾ����ϴ�.");
                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }
                }
            }

            // �����ͺ��̽��� ����
            try {
                devservice.saveAllDevProgress(devProgress);
                response.put("status", "success");
                response.put("message", "���� ���ε尡 ���������� �Ϸ�Ǿ����ϴ�!");
                response.put("totalUploaded", devProgress.size());
            } catch (Exception e) {
                e.printStackTrace();
                response.put("status", "error");
                response.put("message", e.getMessage());
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }

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
    
    // �׽�Ʈ �Ϸ����� ������ �׽�Ʈ ��� null üũ
    private void TestEndDateNullCheck(Date dateValue, String ResultValue, String dataName) {
	    if ((dateValue != null) && 
	        	(ResultValue == null || ResultValue.trim().isEmpty())) {
	        	throw new IllegalArgumentException(dataName + " �����׽�Ʈ ���� ��� '����' ���� '����'���� ����Ͻñ� �ٶ��ϴ�.");
	        }
    }
    
    
    

    

    

        
    
    
   
    
    



}
