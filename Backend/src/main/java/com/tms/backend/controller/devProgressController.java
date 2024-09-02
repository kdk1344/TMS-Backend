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

	    // Service�� ���� �����͸� ��ȸ
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
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		log.info(simpleDateFormat.format(new Date()));

		majorCategory = adminService.getStageCodes("��", majorCategory);
		subCategory = adminService.getStageCodes("��", subCategory);
		if (programType != null && !programType.isEmpty()) {
			programType = adminService.getStageCCodes("02", programType);}
		if (programStatus != null && !programStatus.isEmpty()) {
			programStatus = adminService.getStageCCodes("05", programStatus);}
		if (devStatus != null && !devStatus.isEmpty()) {
			log.info("major ������" + devStatus);
			devStatus = adminService.getStageCCodes("06", devStatus);}
			log.info(devStatus);
		
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
        
        log.info("Ȯ����" + developer);

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
	
//	//��з� Ȯ��
//	@GetMapping(value = "api/major", produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseBody
//    public ResponseEntity<Map<String, Object>> getMajor() {
//    	Map<String, Object> response = new HashMap<>();
//    	List<categoryCode> Major = adminService.getParentCategoryCodes();
//
//        // ���� ������ ����
//        if (Major != null && !Major.isEmpty()) {
//            response.put("status", "success");
//            response.put("major", Major);
//        } else {
//            response.put("status", "failure");
//            response.put("message", "��з� ������ ã�� �� �����ϴ�");
//        }
//
//        // ��ȸ�� ����� ��ȯ
//        return ResponseEntity.ok(response);
//    }
	
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
	public String devProgressPage() {

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
		Map<String, Object> response = new HashMap<>();
		try {
			// �ʼ� �׸� üũ
			validateRequiredField(devProgress.getMajorCategory(), "���� ��з�");
			validateRequiredField(devProgress.getSubCategory(), "���� �ߺз�");
			validateRequiredField(devProgress.getProgramId(), "���α׷� ID");
			validateRequiredField(devProgress.getProgramName(), "���α׷���");
			validateRequiredField(devProgress.getDeveloper(), "������");
			validateRequiredField(devProgress.getPriority(), "�켱����");
			validateRequiredField(devProgress.getProgramStatus(), "���α׷� ����");
	        if ("����".equals(devProgress.getProgramStatus()) &&
	        	(devProgress.getDeletionReason() == null || devProgress.getDeletionReason().trim().isEmpty())) {
	        	throw new IllegalArgumentException("���α׷� ���°� '����'�� ��� ����ó������ �Է��� �ʼ��Դϴ�.");
	        }
	        // Date �ʵ� üũ
	        validateDate(devProgress.getPlannedStartDate(), "���� ������");
	        validateDate(devProgress.getPlannedEndDate(), "�Ϸ� ������");
	        if ((devProgress.getDevtestendDate() != null) && (devProgress.getDevProgAttachment() == null)) {
	        	devProgress.setDevtestendDate(null);
	        	throw new IllegalArgumentException
	        	("�����׽�Ʈ ������ '�����׽�Ʈ�����'�� ÷������ ������ ������ ���������� ��� ���� ���Է� ���·� �ڵ� ����˴ϴ�.");
	        }
	        //�׽�Ʈ �Ϸ����� ������ �׽�Ʈ ����� null üũ
	        TestEndDateNullCheck(devProgress.getPlTestCmpDate(), devProgress.getPlTestResult(), "PL");
	        TestEndDateNullCheck(devProgress.getThirdPartyConfirmDate(), devProgress.getThirdTestResult(), "��3��");
	        TestEndDateNullCheck(devProgress.getItConfirmDate(), devProgress.getItTestResult(), "�� IT");
	        TestEndDateNullCheck(devProgress.getBusiConfirmDate(), devProgress.getBusiTestResult(), "�� ����");        
	        
	        // ������ üũ �ڵ� ���� - �׽�Ʈ ������ Null�� ��� �׽�Ʈ �Ϸ����� ���� 
	        setIfNullDate(devProgress.getPlTestCmpDate(), devProgress.getPlTestScdDate(), devProgress::setPlTestScdDate);
	        setIfNullDate(devProgress.getThirdPartyConfirmDate(), devProgress.getThirdPartyTestDate(), 
	        			devProgress::setThirdPartyTestDate);
	        setIfNullDate(devProgress.getItConfirmDate(), devProgress.getItTestDate(), devProgress::setItTestDate);
	        setIfNullDate(devProgress.getBusiConfirmDate(), devProgress.getBusiTestDate(), devProgress::setBusiTestDate);
	        
	        //���α׷� ���°� �����϶� ����ó���ڴ� �ڵ����� ����� ID, ����ó������ ���� ����
	        if (("����".equals(devProgress.getProgramStatus())) && (devProgress.getDeletionHandler() == null)) {
	        	devProgress.setDeletionHandler(UserID);
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
	        if(IdCheck) {
	        	response.put("status", "failure");
	            response.put("message", "���α׷� ID�� �ߺ��Ǿ����ϴ�.");
	            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	        }
	        //���� ����� �α��� ID ����
	        devProgress.setInitRegistrar(UserID);
        	devservice.insertdevProgress(devProgress);  // ���� ��Ȳ ���� ���� �߰�
        	
        	// ���ο� ���� ���ε� ó��
            if (files != null && files.length > 0) {
                fileservice.handleFileUpload(files, "devProgress", devProgress.getSeq());
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
            response.put("message", "���� ���� ��Ȳ ���� ���� �߿� ������ �߻��߽��ϴ�.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}
	
	@GetMapping("/devProgressEdit")
	public String devProgressEditPage() {

	    return "devProgressEdit"; // JSP �������� �̵�
	}
	
//	//���� ���� ��Ȳ ���� ������
//	@GetMapping(value="api/devProgressEditPage" , produces = MediaType.APPLICATION_JSON_VALUE)
//	@ResponseBody
//	public ResponseEntity<Map<String, Object>> devProgressEditPage2(HttpServletRequest request,
//			@RequestParam("seq") Integer seq) {
//		HttpSession session = request.getSession(false); // ������ ���ٸ� ���� ������ ����
//		if (session == null || session.getAttribute("authorityCode") == null) {
//			// ������ ���ų� authorityCode�� ������ 401 Unauthorized ��ȯ
//			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "������ �����ϴ�. �α����ϼ���."));
//			}
////		Integer authorityCode = (Integer) session.getAttribute("authorityCode");
//		
//		Map<String, Object> response = new HashMap<>();
//		devProgress DevProgressEdit = devservice.getDevById(seq);
//		  
//	    // ���� ����
//		response.put("status", "success");
//        response.put("message", "���� ���� ��Ȳ ���� ����.");
//	    response.put("devProgressEdit", DevProgressEdit);
//	    
//	    return ResponseEntity.ok(response); // JSON���� ���� ��ȯ
//	}
	
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
		Map<String, Object> response = new HashMap<>();
		try {
			// �ʼ� �׸� üũ
			validateRequiredField(devProgress.getMajorCategory(), "���� ��з�");
			validateRequiredField(devProgress.getSubCategory(), "���� �ߺз�");
			validateRequiredField(devProgress.getProgramId(), "���α׷� ID");
			validateRequiredField(devProgress.getProgramName(), "���α׷���");
			validateRequiredField(devProgress.getDeveloper(), "������");
			validateRequiredField(devProgress.getPriority(), "�켱����");
			validateRequiredField(devProgress.getProgramStatus(), "���α׷� ����");
	        if ("����".equals(devProgress.getProgramStatus()) &&
	        	(devProgress.getDeletionReason() == null || devProgress.getDeletionReason().trim().isEmpty())) {
	        	throw new IllegalArgumentException("���α׷� ���°� '����'�� ��� ����ó������ �Է��� �ʼ��Դϴ�.");
	        }
	        // Date �ʵ� üũ
	        validateDate(devProgress.getPlannedStartDate(), "���� ������");
	        validateDate(devProgress.getPlannedEndDate(), "�Ϸ� ������");
	        if ((devProgress.getDevtestendDate() != null) && (devProgress.getDevProgAttachment() == null)) {
	        	devProgress.setDevtestendDate(null);
	        	throw new IllegalArgumentException
	        	("�����׽�Ʈ ������ '�����׽�Ʈ�����'�� ÷������ ������ ������ ���������� ��� ���� ���Է� ���·� �ڵ� ����˴ϴ�.");
	        }
	        //�׽�Ʈ �Ϸ����� ������ �׽�Ʈ ����� null üũ
	        TestEndDateNullCheck(devProgress.getPlTestCmpDate(), devProgress.getPlTestResult(), "PL");
	        TestEndDateNullCheck(devProgress.getThirdPartyConfirmDate(), devProgress.getThirdTestResult(), "��3��");
	        TestEndDateNullCheck(devProgress.getItConfirmDate(), devProgress.getItTestResult(), "�� IT");
	        TestEndDateNullCheck(devProgress.getBusiConfirmDate(), devProgress.getBusiTestResult(), "�� ����");        
	        
	        // ������ üũ �ڵ� ���� - �׽�Ʈ ������ Null�� ��� �׽�Ʈ �Ϸ����� ���� 
	        setIfNullDate(devProgress.getPlTestCmpDate(), devProgress.getPlTestScdDate(), devProgress::setPlTestScdDate);
	        setIfNullDate(devProgress.getThirdPartyConfirmDate(), devProgress.getThirdPartyTestDate(), 
	        			devProgress::setThirdPartyTestDate);
	        setIfNullDate(devProgress.getItConfirmDate(), devProgress.getItTestDate(), devProgress::setItTestDate);
	        setIfNullDate(devProgress.getBusiConfirmDate(), devProgress.getBusiTestDate(), devProgress::setBusiTestDate);
	        
	        //���α׷� ���°� �����϶� ����ó���ڴ� �ڵ����� ����� ID, ����ó������ ���� ����
	        if (("����".equals(devProgress.getProgramStatus())) && (devProgress.getDeletionHandler() == null)) {
	        	devProgress.setDeletionHandler(UserID);
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
	        if(IdCheck) {
	        	response.put("status", "failure");
	            response.put("message", "���α׷� ID�� �ߺ��Ǿ����ϴ�.");
	            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	        }
	        devProgress DevProgressEdit = devservice.getDevById(devProgress.getSeq());
        	// devProgress�� �ʵ带 DevProgressEdit�� ����
    	    BeanUtils.copyProperties(devProgress, DevProgressEdit);
    	    
    	    // �������׿� ��ϵ� ���� ÷������ ���� ����
            adminService.deleteAttachmentsByNoticeId(DevProgressEdit.getSeq());

            // ���ο� ���� ���ε� ó��
            fileservice.handleFileUpload(files, "devProgress", DevProgressEdit.getSeq());
            
            log.info("check "+devProgress);

            // ������Ʈ�� ���������� ����
            devservice.updatedevProgress(DevProgressEdit);

            // ���� ���� ����
            response.put("status", "success");
            response.put("message", "���� ���� ��Ȳ�� ���������� �����Ǿ����ϴ�.");
            response.put("DevProgressEdit", DevProgressEdit);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.put("status", "failure");
            response.put("message", "���α׷� ���߸�� ���� �߿� ���� �߻�");
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
        
        for (int seq : seqs) {
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
			log.info("major ������" + devStatus);
			devStatus = adminService.getStageCCodes("06", devStatus);}
			log.info(devStatus);
    	
    	List<devProgress> filteredDevCodeList = devservice.searchDevProgress(
	            majorCategory, subCategory, programType, programName,programId, programStatus, developer,
	            devStatus, devStartDate, devEndDate, pl, thirdPartyTestMgr, ItMgr, BusiMgr, page, size
	    );

    	log.info("Ȯ����"+filteredDevCodeList);
        devexportToExcel(response, filteredDevCodeList, "filtered_dev_codes.xlsx");
    }

    // ���� ���Ϸ� �����͸� �������� �޼���
    private void devexportToExcel(HttpServletResponse response, List<devProgress> devProgressList, String fileName) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("DevProgress");
        String check = "";
        log.info("üũ��");
        if (devProgressList.isEmpty()) {
    		check = "no_value";
    		log.info(check);
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
        for (int i = 0; i < headers.length; i++) {
        	if (check.equals("") || i > 0) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }
        }
        if (check != "") {
        	headerRow.createCell(45).setCellValue("INIT_REGISTRAR");
            headerRow.createCell(46).setCellValue("LAST_MODIFIER");
        }
        else {
	        headerRow.createCell(45).setCellValue("INIT_REG_DATE");
	        headerRow.createCell(46).setCellValue("INIT_REGISTRAR");
	        headerRow.createCell(47).setCellValue("LAST_MODIFIED_DATE");
	        headerRow.createCell(48).setCellValue("LAST_MODIFIER");
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
            }
            else {
            	row.createCell(46).setCellValue(devProgress.getInitRegDate().toString());
	            row.createCell(47).setCellValue(devProgress.getInitRegistrar());
	            row.createCell(48).setCellValue(devProgress.getLastModifiedDate().toString());
	            row.createCell(49).setCellValue(devProgress.getLastModifier());
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
            if (!isHeaderValid4(headerRow, expectedHeaders)) {
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
                response.put("message", "�ܷ� Ű ���� ������ �����߽��ϴ�. �����Ͱ� �ùٸ��� �ʽ��ϴ�.");
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
    
    
    // �׼� ��ȿ��� Ȯ��
    private boolean isHeaderValid4(Row headerRow, List<String> expectedHeaders) {
        for (int i = 0; i < expectedHeaders.size(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell == null || !cell.getStringCellValue().trim().equalsIgnoreCase(expectedHeaders.get(i))) {
                return false;
            }
        }
        return true;
    }
    
    // �׼� Cell�� String ��ȯ 
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        return cell.getCellType() == CellType.STRING ? cell.getStringCellValue() : cell.toString();
    }
    
    // �׼� Cell�� Num ��ȯ
    private double getCellValueAsNumeric(Cell cell) {
        if (cell == null) {
            return 0;
        }
        return cell.getCellType() == CellType.NUMERIC ? cell.getNumericCellValue() : Double.parseDouble(cell.toString());
    }
    
    // �׼� Cell�� Date ��ȯ
    private Date getCellValueAsDate(Cell cell) {
        if (cell == null) {
            return null;
        }

        if (cell.getCellType() == CellType.NUMERIC) {
            // ��¥�� ���� ���·� ����Ǿ� ���� ��, ��¥�� ��ȯ
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue();
            } else {
                return null;
            }
        } else if (cell.getCellType() == CellType.STRING) {
            // ���ڿ��� �Ǿ� �ִ� ���, "YYYY-MM-DD" ���� ���� ó��
            String dateStr = cell.getStringCellValue();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
				return dateFormat.parse(dateStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
        }

        return null; // �ٸ� ������ ���� null ��ȯ
    }
    
    // String Value�� �ʼ��� ��ȿ�� ����
    private void validateRequiredField(String fieldValue, String fieldName) {
        if (fieldValue == null || fieldValue.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + "��(��) �ʼ� �Է� �׸��Դϴ�.");
        }
    }
    
    // Date Value�� ��ȿ�� ����
    private void validateDate(Date dateValue, String dateName) {
        if (dateValue == null) {
            throw new IllegalArgumentException(dateName + "��(��) �ʼ� �Է� �׸��Դϴ�.");
        }
    }
    
    // �׽�Ʈ �Ϸ����� ������ �׽�Ʈ ��� null üũ
    private void TestEndDateNullCheck(Date dateValue, String ResultValue, String dataName) {
	    if ((dateValue != null) && 
	        	(ResultValue == null || ResultValue.trim().isEmpty())) {
	        	throw new IllegalArgumentException(dataName + " �����׽�Ʈ ���� ��� '����' ���� '����'���� ����Ͻñ� �ٶ��ϴ�.");
	        }
    }
    
    
    private void setIfNullDate(Date DateValue1, Date DateValue2, Consumer<Date> setDate) {
        if (DateValue1 != null && DateValue2 == null) {
            setDate.accept(DateValue1);
        }
    }
    

    

    

        
    
    
   
    
    



}
