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
@RequestMapping("/*")
public class CodeController {
	
	@Autowired
    private AdminService adminService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private FileService fileService;
	
	//공통코드 Controller
	
	//공통코드 페이지
	@GetMapping("/commonCode")
    public String commonCodePage() {
        return "commonCode";
        }
    
	//공통코드 추가
    @PostMapping(value= "api/ccwrite", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> ccWrite(@RequestBody CommonCode commonCode) {
    	//json 데이터용 response
        Map<String, Object> response = new HashMap<>();
        try {
        	adminService.addCommonCode(commonCode);  // 공통코드 추가
            response.put("status", "success");
            response.put("message", "CommonCode successfully registered!");
            response.put("commonCode", commonCode);  // 등록된 공통코드 정보 반환
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) { 
            response.put("status", "failure");
            response.put("message", "공통 코드 등록중에 오류가 발생했습니다");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (DuplicateKeyException e) { //공통코드 중복 메세지용
            response.put("status", "failure");
            response.put("message", "공통 코드가 중복되었습니다.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) { // 기타 오류 메시지 용
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "공통 코드 등록중에 오류가 발생했습니다");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    //공통코드 수정
    @PostMapping(value= "api/ccmodify" , produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> ccModify(@RequestBody CommonCode commonCode) {
        Map<String, Object> response = new HashMap<>();
        try {
        	String Seq = commonCode.getSeq(); // 공통코드 seq 추출
        	String parentCode = Seq.substring(0, 2); // seq의 앞 2자리를 parentCode로 설정
        	String code = Seq.substring(2, 4); // seq의 뒤 2자리를 일반 code로 설정
        	commonCode.setParentCode(parentCode); // 공통코드 VO에 parentCode 추가
        	commonCode.setCode(code);
            boolean success = adminService.updateCommonCode(commonCode);  // 공통코드 수정
            if (success) {
                response.put("status", "success");
                response.put("message", "공통 코드가 성공적으로 등록됐습니다");
                response.put("commonCode", commonCode);  // 업데이트된 공통코드 정보 반환
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("status", "failure");
                response.put("message", "공통 코드 등록 중에 오류가 발생했습니다.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "공통 코드 등록 중에 오류가 발생했습니다.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    //공통코드 삭제
    @DeleteMapping(value= "api/deletecc", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deletecc(@RequestBody List<String> seqs) {
        Map<String, Object> response = new HashMap<>();

        // CommonCode 삭제
        for (String seq : seqs) {
        	String parentCode = seq.substring(0, 2);// seq의 앞 2자리를 parentCode로 설정
        	String code = seq.substring(2, 4); // seq의 뒤 2자리를 일반 code로 설정
        	if (parentCode.equals("00")) { // 상위 코드가 없을 경우
        		int child = adminService.checkChildCodesExist(code); //하위코드 존재 여부 확인
        		if (child > 0 ) {
        			response.put("status", "failure");
                    response.put("message", "하위 코드가 존재합니다. 하위 코드를 미리 삭제해주세요.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        		}
        	}
            adminService.deleteCommonCode(seq); //공통코드 삭제
        }
        
        response.put("status", "success");
        response.put("message", "공통코드가 성공적으로 삭제되었습니다.");
        return ResponseEntity.ok(response);
        
    }
   
    //공통코드 조회
    @GetMapping(value = "api/commonCode", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> getCommonCodes(@RequestParam(value = "parentCode", required = false) String parentCode,
									          @RequestParam(value = "code", required = false) String code,
									          @RequestParam(value = "codeName", required = false) String codeName,
									          @RequestParam(value = "page", defaultValue = "1") int page,
									          @RequestParam(value = "size", defaultValue = "15") int size) {
		// CommonCode 조회
        List<CommonCode> commonCodes = adminService.searchCommonCodes(parentCode, code, codeName, page, size);
        
        // parentCode 설정
        for (CommonCode commonCode : commonCodes) {
        	String parentCodeName = adminService.searchParentCodeName(commonCode.getParentCode());
        	commonCode.setParentCodeName(parentCodeName);
        }
        
        // parentCode와 code를 합쳐서 seq 값으로 설정
        for (CommonCode commonCode : commonCodes) {
            String seq = commonCode.getParentCode() + commonCode.getCode();
            commonCode.setSeq(seq);
        }
        
        int totalCommonCodes = adminService.getTotalCommonCodeCount(parentCode, code, codeName); //공통코드 총 갯수
        int totalPages = (int) Math.ceil((double) totalCommonCodes / size); // 총 조회 페이지
        
        // 응답 생성
        Map<String, Object> response = new HashMap<>();
        response.put("commonCodes", commonCodes);
        response.put("currentPage", page);
        response.put("totalPages", totalPages);
        response.put("totalCommonCodes", totalCommonCodes);

        return response;
    }
    
    //상위코드 정보 가져오기
    @GetMapping(value = "api/ccparentCode", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getParentCommonCodes() {
    	//json 반응 생성
    	Map<String, Object> response = new HashMap<>();
        
        // CategoryService를 통해 대분류 코드를 조회
        List<CommonCode> parentCodes = adminService.getParentCommonCodes();
        
        if (parentCodes != null && !parentCodes.isEmpty()) { // 상위코드가 있을 시
            response.put("status", "success");
            response.put("parentCodes", parentCodes);
        } else { // 상위코드가 없을 시
            response.put("status", "failure");
            response.put("message", "상위 코드를 찾을 수 없습니다.");
        }

        // 조회된 결과를 반환
        return ResponseEntity.ok(response);
    }
    
    //상위코드에 따른 공통코드 조회
    @GetMapping(value = "api/cccode", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCCCommonCodes(@RequestParam("parentCode") String parentCode) {
        List<CommonCode> CCCodes = adminService.getCCCode(parentCode); // 상위코드 입력해서 하위 공통코드 리스트 가져오기
    	Map<String, Object> response = new HashMap<>(); // 반응 생성
        
    	 // 하위 코드가 없으면 204 상태와 함께 빈 배열 반환
        if (CCCodes.isEmpty()) {
            response.put("status", "no_content");
            response.put("message", "상위 코드에 맞는 하위 공통 코드가 없습니다");
            response.put("CCCodes", CCCodes);
            return ResponseEntity.status(204).body(response);
        }

        // 하위 코드 목록 반환
        response.put("status", "success");
        response.put("message", "하위 공통 코드를 호출했습니다.");
        response.put("CCCodes", CCCodes);
        return ResponseEntity.ok(response); // 200 OK 상태 반환
    }
    
    // 전체 정보를 엑셀로 다운로드
    @GetMapping("/downloadAllcc")
    public void downloadAllcc(HttpServletResponse response) throws IOException {
    	List<CommonCode> commonCodeList = adminService.getAllCommonCodes();
        ccexportToExcel(response, commonCodeList, "all_common_codes.xlsx");
    }
    
    // 액셀 파일 예시를 다운로드
    @GetMapping("/ccexampleexcel")
    public void downloadExcc(HttpServletResponse response) throws IOException {
    	// 아무런 데이터도 안 가져오기 위한 변수
    	List<CommonCode> ExampleDEV = adminService.getFilteredCommonCodes(null, null, "No_value");
    	ccexportToExcel(response, ExampleDEV, "example.xlsx");
    }

    // 조회된 정보를 엑셀로 다운로드
    @GetMapping("/downloadFilteredcc")
    public void downloadFilteredcc(
    		@RequestParam(value = "parentCode", required = false) String parentCode,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "codeName", required = false) String codeName,
            HttpServletResponse response) throws IOException {
    	// 조건 조회 데이터 가져오기
    	List<CommonCode> filteredCommonCodeList = adminService.getFilteredCommonCodes(parentCode, code, codeName);
        ccexportToExcel(response, filteredCommonCodeList, "filtered_common_codes.xlsx");
    }

    // 엑셀 파일로 데이터를 내보내는 메서드
    private void ccexportToExcel(HttpServletResponse response, List<CommonCode> commonCodeList, String fileName) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("CommonCodes");
        //액셀 Row 만들기
        Row headerRow = sheet.createRow(0);
        //컬럼 만들기
        headerRow.createCell(0).setCellValue("ParentCode");
        headerRow.createCell(1).setCellValue("Code");
        headerRow.createCell(2).setCellValue("CodeName");

        int rowNum = 1;
        for (CommonCode commonCode : commonCodeList) {
            Row row = sheet.createRow(rowNum++);
            //액셀에 데이터 입력
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
    
    //액셀 업로드
    @PostMapping(value = "api/ccupload", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> ccuploadExcelFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
    	Map<String, Object> response = new HashMap<>();
        if (file.isEmpty()) { //업로드한 액셀 파일이 없을 경우
        	response.put("status", "failure");
            response.put("message", "파일이 없습니다. 다시 시도해 주세요.");
        	return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            
            // 예상하는 컬럼명 리스트
            List<String> expectedHeaders = Arrays.asList("ParentCode", "Code", "CodeName");
            if (!fileService.isHeaderValid(headerRow, expectedHeaders)) { //컬럼 비교
                response.put("status", "error");
                response.put("message", "헤더의 컬럼명이 올바르지 않습니다.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            
            List<CommonCode> commonCodes = new ArrayList<>();

            // 첫 번째 행은 헤더이므로 건너뜁니다.
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                	CommonCode commonCode = new CommonCode();
                    try {
                    	// ParentCode 처리
                        String parentCode;
                        if (row.getCell(0).getCellType() == CellType.NUMERIC) { // 입력값이 정수일 경우
                            parentCode = String.format("%02d", (int) row.getCell(0).getNumericCellValue());
                        } else {
                            parentCode = row.getCell(0).getStringCellValue();
                        }

                        // Code 처리
                        String code;
                        if (row.getCell(1).getCellType() == CellType.NUMERIC) {
                            code = String.format("%02d", (int) row.getCell(1).getNumericCellValue());
                        } else {
                            code = row.getCell(1).getStringCellValue();
                        }

                        // CodeName 처리
                        String codeName = row.getCell(2).getStringCellValue();

                        // 특정 조건 검사 (예: 빈 문자열 또는 null 값이 아닌 경우에만 추가)
                        if (parentCode != null && !parentCode.trim().isEmpty() &&
                            code != null && !code.trim().isEmpty() &&
                            codeName != null && !codeName.trim().isEmpty()) {

                            // CommonCode 객체에 값 설정
                            commonCode.setParentCode(parentCode);
                            commonCode.setCode(code);
                            commonCode.setCodeName(codeName);
                            
                            //중복 확인
                            boolean Count = adminService.countdupliCCode(commonCode);
	                        if (!Count) {
	                        	commonCodes.add(commonCode);
	                        }
	                        else {
	                        	}
                            }
                    } catch (Exception e) {
                        e.printStackTrace();
                        response.put("status", "error");
                        response.put("message", "파일의 데이터 형식이 올바르지 않습니다: 행 " + (i + 1));
                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }
                }
            }

            // 데이터베이스에 저장
            adminService.saveAllCommonCodes(commonCodes);
            response.put("status", "success");
            response.put("message", "파일 업로드가 성공적으로 완료되었습니다!");
            response.put("totalUploaded", commonCodes.size());
         
        } catch (DuplicateKeyException e) { // 액셀로 입력한 공통코드가 중복일 경우
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "공통 코드가 중복되었습니다.");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        } catch (IOException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "파일 처리 중 오류가 발생했습니다. 다시 시도해 주세요.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    // 분류코드 Controller
    
    //분류코드 페이지
    @GetMapping("/categoryCode")
    public String categoryCodePage() {
        return "categoryCode";
    }
    
    // 분류 코드 등록
    @PostMapping(value = "api/catwrite", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> catWrite(@RequestBody categoryCode categoryCode) {
        Map<String, Object> response = new HashMap<>(); //반응 생성
        try {
        	if (categoryCode.getCode().length() > 2) { // 분류코드 코드가 2자리 이상일 경우
        		response.put("status", "failure");
        		response.put("message", "코드는 2자리 이하로 입력해야 합니다.");
        		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        	
        	 // StageType에 따른 코드 처리
            if ("중".equals(categoryCode.getStageType())) {
                // 상위 대분류 코드를 가져온다
            	String parentCode = adminService.getParentCode(categoryCode.getParentCode());

            	if (parentCode == null) {
                    throw new IllegalArgumentException("상위 대분류 코드를 찾을 수 없습니다.");
                }

                // 중분류 코드와 상위 대분류 코드를 결합하여 최종 코드 생성
                categoryCode.setCode(parentCode + categoryCode.getCode());
            }
            // 데이터베이스에 최종 코드 저장
            adminService.addCategoryCode(categoryCode);

            // 성공 응답 생성
            response.put("status", "success");
            response.put("message", "카테고리 코드가 성공적으로 등록되었습니다.");
            response.put("categoryCode", categoryCode);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.put("status", "failure");
            response.put("message", "분류 코드를 등록중에 오류가 발생했습니다.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (DuplicateKeyException e) {
            response.put("status", "failure");
            response.put("message", "분류 코드가 중복되었습니다.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "분류 코드를 등록중에 오류가 발생했습니다.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    //분류코드 수정
    @PostMapping(value= "api/catmodify" , produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> catModify(@RequestBody categoryCode categoryCode) {
        Map<String, Object> response = new HashMap<>();
        try {
        	if (categoryCode.getCode().length() > 2) {
        		response.put("status", "failure");
        		response.put("message", "코드는 2자리 이하로 입력해야 합니다.");
        		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        	
        	if ("중".equals(categoryCode.getStageType())) {
                // DB에서 상위 대분류 코드 조회
                String parentCode = adminService.getParentCode(categoryCode.getParentCode());

                if (parentCode == null) {
                    response.put("status", "failure");
                    response.put("message", "상위 대분류 코드를 찾을 수 없습니다.");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }

                // 중분류 코드와 상위 대분류 코드를 결합하여 최종 코드 생성
                categoryCode.setCode(parentCode + categoryCode.getCode());
            }
        	
            boolean success = adminService.updateCategoryCode(categoryCode);  // categoryCode 수정
            if (success) {
                response.put("status", "success");
                response.put("message", "분류 코드가 성공적으로 수정됐습니다");
                response.put("categoryCode", categoryCode);  // 업데이트된 categoryCode 정보 반환
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("status", "failure");
                response.put("message", "분류 코드 수정에 실패했습니다");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "분류 코드 수정중에 오류가 발생했습니다.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    //분류코드 삭제
    @DeleteMapping(value= "api/catdelete", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deletecat(@RequestBody List<String> codeList) {
        Map<String, Object> response = new HashMap<>();        
        // CategoryCode 삭제
        for (String code : codeList) {
        	if (code.length() == 2) { //분류코드가 하위 코드 등록이 가능한 상위 코드일 경우
        		int child = adminService.checkChildCodesExist2(code); // 하위 코드 여부 확인
        		if (child > 0 ) {
        			response.put("status", "failure");
                    response.put("message", "하위 코드가 존재합니다. 하위 코드를 미리 삭제해주세요.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        	}
        }
            adminService.deleteCategoryCode(code); //분류코드 삭제
        }
        
        response.put("status", "success");
        response.put("message", "분류코드가 성공적으로 삭제되었습니다.");
        return ResponseEntity.ok(response);
    }
    
    //분류코드 조회
    @GetMapping(value = "api/categoryCode", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> getCategoryCodes(@RequestParam(value = "parentCode", required = false) String parentCode,
                                                @RequestParam(value = "code", required = false) String code,
                                                @RequestParam(value = "codeName", required = false) String codeName,
                                                @RequestParam(value = "page", defaultValue = "1") int page,
                                                @RequestParam(value = "size", defaultValue = "15") int size) {
    	Map<String, Object> response = new HashMap<>();
    	
    	// categoryCode 조회
        List<categoryCode> categoryCodes = adminService.searchCategoryCodes(parentCode, code, codeName, page, size);
        int totalCategoryCodes = adminService.getTotalCategoryCodeCount(parentCode, code, codeName);
        int totalPages = (int) Math.ceil((double) totalCategoryCodes / size);

        // 응답 생성
        response.put("categoryCodes", categoryCodes);
        response.put("currentPage", page);
        response.put("totalPages", totalPages);
        response.put("totalCategoryCodes", totalCategoryCodes);

        return response;
    }
    
    // 분류코드 부모코드 가져오기
    @GetMapping(value = "api/catparentCode", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getParentCategoryCodes() {
    	Map<String, Object> response = new HashMap<>();
        
        // CategoryService를 통해 대분류 코드를 조회
        List<categoryCode> parentCodes = adminService.getParentCategoryCodes();

        // 응답 데이터 생성
        if (parentCodes != null && !parentCodes.isEmpty()) {
            response.put("status", "success");
            response.put("parentCodes", parentCodes);
        } else {
            response.put("status", "failure");
            response.put("message", "상위코드가 없습니다");
        }

        // 조회된 결과를 반환
        return ResponseEntity.ok(response);
    }
    
    //상위 코드에 맞춘 하위 분류코드 가져오기
    @GetMapping(value = "api/catcode", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCTCategoryCodes(@RequestParam("parentCode") String parentCode) {
        List<categoryCode> CTCodes = adminService.getCTCode(parentCode); //입력한 상위코드에 맞춘 하위 분류코드
    	Map<String, Object> response = new HashMap<>();
        
    	 // 하위 코드가 없으면 204 상태와 함께 빈 배열 반환
        if (CTCodes.isEmpty()) {
            response.put("status", "no_content");
            response.put("message", "해당하는 중분류가 없습니다");
            response.put("CTCodes", CTCodes);
            return ResponseEntity.ok(response);
        }

        // 하위 코드 목록 반환
        response.put("status", "success");
        response.put("message", "CTCodes retrieved successfully");
        response.put("CTCodes", CTCodes);
        return ResponseEntity.ok(response); // 200 OK 상태 반환
    }
    
    // 전체 categoryCode 정보를 엑셀로 다운로드
    @GetMapping("/catdownloadAll")
    public void downloadAllcat(HttpServletResponse response) throws IOException {
        List<categoryCode> categoryCodeList = adminService.getAllCategoryCodes();
        catexportToExcel(response, categoryCodeList, "all_category_codes.xlsx");
    }
    
    // 액셀 파일 예시를 다운로드
    @GetMapping("/catexampleexcel")
    public void downloadExcat(HttpServletResponse response) throws IOException {
    	List<categoryCode> ExampleDEV = adminService.searchCategoryCodes(null, null, "NO_VALUE", 1, 10); //빈값을 받기 위한 변수  	
    	catexportToExcel(response, ExampleDEV, "example.xlsx");
    }

    // 조회된 categoryCode 정보를 엑셀로 다운로드
    @GetMapping("/catdownloadFiltered")
    public void downloadFilteredcat(
            @RequestParam(value = "parentCode", required = false) String parentCode,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "codeName", required = false) String codeName,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "15") int size,
            HttpServletResponse response) throws IOException {

    	List<categoryCode> filteredCategoryCodeList = adminService.searchCategoryCodes(parentCode, code, codeName, page, size);
        catexportToExcel(response, filteredCategoryCodeList, "filtered_category_codes.xlsx");
    }

    // 엑셀 파일로 데이터를 내보내는 메서드
    private void catexportToExcel(HttpServletResponse response, List<categoryCode> categoryCodeList, String fileName) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("CategoryCodes");
        
        //로우 값 생성
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("StageType");
        headerRow.createCell(1).setCellValue("Code");
        headerRow.createCell(2).setCellValue("CodeName");

        int rowNum = 1;
        for (categoryCode categoryCode : categoryCodeList) {
            Row row = sheet.createRow(rowNum++);
            // 데이터 입력
            row.createCell(0).setCellValue(categoryCode.getStageType());
            row.createCell(1).setCellValue(categoryCode.getCode());
            row.createCell(2).setCellValue(categoryCode.getCodeName());
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        try (OutputStream os = response.getOutputStream()) {
            workbook.write(os);
        }
        workbook.close();
    }
    
    // 액셀 업로드
    @PostMapping(value = "api/catupload", produces = MediaType.APPLICATION_JSON_VALUE)
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
            List<String> expectedHeaders = Arrays.asList("StageType", "Code", "CodeName");
            if (!fileService.isHeaderValid(headerRow, expectedHeaders)) {
                response.put("status", "error");
                response.put("message", "헤더의 컬럼명이 올바르지 않습니다.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            List<categoryCode> categoryCodes = new ArrayList<>();

            // 첫 번째 행은 헤더이므로 건너뜁니다.
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    categoryCode categoryCode = new categoryCode();
                    try {
                    	// StageType 처리
                    	String stageType;
                    	stageType = row.getCell(0).getStringCellValue();
                        // Code 처리
                    	String code;
                        if (row.getCell(1).getCellType() == CellType.NUMERIC) {
                        	if ("대".equals(stageType)) {
	                        	code = String.format("%02d", (int) row.getCell(1).getNumericCellValue());
                        	}
                        	else {
	                    		code = String.format("%04d", (int) row.getCell(1).getNumericCellValue());
                        	}
                        } else {
                        	code = row.getCell(1).getStringCellValue();
                        }
                        // 코드네임 처리
                        String codeName;
                        codeName = row.getCell(2).getStringCellValue();
                        // 특정 조건 검사 (예: 빈 문자열 또는 null 값이 아닌 경우에만 추가)
                        if (stageType != null && !stageType.trim().isEmpty() &&
                            code != null && !code.trim().isEmpty() &&
                            codeName != null && !codeName.trim().isEmpty()) {
                        	categoryCode.setStageType(stageType);
	                        categoryCode.setCode(code);       // 두 번째 셀 (Code)
	                        categoryCode.setCodeName(codeName);   // 세 번째 셀 (CodeName)
	                        boolean Count = adminService.countdupliCtCode(categoryCode);
	                        if (!Count) {
	                        	categoryCodes.add(categoryCode);
	                        }
	                        else {
	                        	}
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        response.put("status", "error");
                        response.put("message", "파일의 데이터 형식이 올바르지 않습니다: 행 " + (i + 1));
                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }
                }
            }

            // 데이터베이스에 저장
            adminService.saveAllCategoryCodes(categoryCodes);
            response.put("status", "success");
            response.put("message", "파일 업로드가 성공적으로 완료되었습니다!");
            response.put("totalUploaded", categoryCodes.size());

        } catch (DuplicateKeyException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "분류 코드가 중복되었습니다.");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        } catch (IOException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "파일 처리 중 오류가 발생했습니다. 다시 시도해 주세요.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    

    



}
