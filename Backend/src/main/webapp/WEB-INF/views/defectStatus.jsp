<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="ko">
  <head>
    <%@ include file="./common.jsp" %>
    <meta charset="UTF-8" />
    <link rel="stylesheet" type="text/css" href="../../resources/css/defectStatus.css" />

    <title>결함 현황 조회</title>
    <script type="module" src="../../resources/js/defectStatus.js"></script>
  </head>

  <body>
    <header class="header">
      <!-- 공통 헤더 정보 동적으로 삽입-->
    </header>

    <main class="content">
      <h1 class="page-title">결함 현황 조회</h1>        

      <!-- 결함 현황 조회 필터링 폼 -->
      <form id="defectFilterForm">
	    <div class="filter-container">
	        <!-- 첫 번째 줄 -->
	        <div class="filter-row">
	            <div class="form-group">
	                <label for="testCategory">테스트 구분</label>
	                <select id="testCategory" name="testCategory" class="select-wide">
	                    <option value="">선택</option>
	                    <!-- 옵션들 -->
	                </select>
	            </div>
	
	            <div class="form-group">
	                <label for="taskCategory">업무 대분류</label>
	                <select id="taskCategory" name="taskCategory" class="select-medium">
	                    <option value="">선택</option>
	                    <!-- 옵션들 -->
	                </select>
	            </div>
	
	            <div class="form-group">
	                <label for="taskSubCategory">업무 중분류</label>
	                <select id="taskSubCategory" name="taskSubCategory" class="select-medium">
	                    <option value="">선택</option>
	                    <!-- 옵션들 -->
	                </select>
	            </div>
	
	            <div class="form-group">
	                <label for="defectSeverity">결함 심각도</label>
	                <select id="defectSeverity" name="defectSeverity" class="select-medium">
	                    <option value="">선택</option>
	                    <!-- 옵션들 -->
	                </select>
	            </div>
	        </div>
	
	        <!-- 두 번째 줄 -->
	        <div class="filter-row">
	            <div class="form-group">
	                <label for="defectNumber">결함 번호</label>
	                <input type="text" id="defectNumber" name="defectNumber" />
	            </div>
	
	            <div class="form-group">
	                <label for="defectRegistrar">결함 등록자</label>
	                <input type="text" id="defectRegistrar" name="defectRegistrar" />
	            </div>
	
	            <div class="form-group">
	                <label for="defectHandler">결함 조치자</label>
	                <input type="text" id="defectHandler" name="defectHandler" />
	            </div>
	
	            <div class="form-group">
	                <label for="defectStatus">처리 상태</label>
	                <select id="defectStatus" name="defectStatus" class="select-medium">
	                    <option value="">선택</option>
	                    <!-- 옵션들 -->
	                </select>
	            </div>
	        </div>
	
	        <!-- 세 번째 줄 (버튼들) -->
	        <div class="button-row">
	            <button type="submit" id="searchNoticeButton">조회</button>
	            <button type="reset" id="resetButton">초기화</button>
	        </div>
	    </div>
	  </form>

      <!-- 버튼 그룹 -->
      <div class="button-group">
        <div class="flex-box">
          <button id="openNoticeRegisterModalButton">등록</button>
          <button id="deleteNoticeButton">삭제</button>
        </div>
      </div>

      <!-- 결함 사항 목록 테이블 -->
      <table id="defectTable" class="styled-table">
	    <thead>
	        <tr>
	            <th class="defect-checkbox"><input type="checkbox" /></th>
	            <th class="defect-seq">결함 번호</th>
	            <th class="defect-subCategory">업무 중분류</th>
	            <th class="defect-severity">결함 심각도</th>
	            <th class="defect-testId">테스트 ID</th>
	            <th class="defect-programName">프로그램 명</th>
	            <th class="defect-registrar">결함 등록자</th>
	            <th class="defect-regDate">결함 등록일</th>
	            <th class="defect-handler">결함 조치자</th>
	            <th class="defect-scheduledDate">조치 예정일</th>
	            <th class="defect-completionDate">결함 조치일</th>
	            <th class="defect-pl">PL</th>
	            <th class="defect-plConfirmDate">PL 확인일</th>
	            <th class="defect-regConfirmDate">등록자 확인일</th>
	            <th class="defect-status">처리상태</th>
	        </tr>
	    </thead>
	    <tbody>
	        <c:forEach var="defect" items="${defects}">
	            <tr>
	                <td><input type="checkbox" /></td>
	                <td><a href="/tms/defectDetail?seq=${defect.seq}">${defect.seq}</a></td>
	                <td>${defect.subCategory}</td>
	                <td>${defect.severity}</td>
	                <td>${defect.testId}</td>
	                <td>${defect.programName}</td>
	                <td>${defect.registrar}</td>
	                <td><fmt:formatDate value="${defect.regDate}" pattern="yyyy-MM-dd" /></td>
	                <td>${defect.handler}</td>
	                <td><fmt:formatDate value="${defect.scheduledDate}" pattern="yyyy-MM-dd" /></td>
	                <td><fmt:formatDate value="${defect.completionDate}" pattern="yyyy-MM-dd" /></td>
	                <td>${defect.pl}</td>
	                <td><fmt:formatDate value="${defect.plConfirmDate}" pattern="yyyy-MM-dd" /></td>
	                <td><fmt:formatDate value="${defect.regConfirmDate}" pattern="yyyy-MM-dd" /></td>
	                <td>${defect.status}</td>
	            </tr>
	        </c:forEach>
	    </tbody>
	  </table>

      <!-- 페이지네이션 -->
      <div id="noticePagination" class="pagination">
      	<c:forEach var="i" begin="1" end="${totalPages}">
            <c:choose>
                <c:when test="${i == currentPage}">
                    <strong>${i}</strong>
                </c:when>
                <c:otherwise>
                    <a href="?page=${i}&size=${size}">${i}</a>
                </c:otherwise>
            </c:choose>
        </c:forEach>
      </div>
      
    </main>
  </body>
  <script>
	  function truncateTextByClass(className, maxLength) {
		    // 클래스 이름으로 모든 요소를 가져온다
		    var elements = document.getElementsByClassName(className);
		    
		    // 각 요소마다 텍스트를 자르고 "..."을 추가
		    Array.prototype.forEach.call(elements, function(element) {
		      var text = element.textContent;
		      
		      if (text.length > maxLength) {
		        element.textContent = text.substring(0, maxLength) + "...";
		      }
		    });
		  }
	
		  // 페이지 로드 시 모든 "notice-contents" 클래스 요소의 텍스트를 자른다
		  window.onload = function() {
		    truncateTextByClass("notice-contents", 50); // 최대 50글자까지만 표시
		  };
  </script>
</html>
