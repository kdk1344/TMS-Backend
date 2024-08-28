<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="ko">
  <head>
    <%@ include file="./common.jsp" %>
    <meta charset="UTF-8" />

    <title>프로그램 개발 진행 현황</title>
  </head>

  <body>
    <header class="header">
      <!-- 공통 헤더 정보 동적으로 삽입-->
    </header>

    <!-- 엑셀 업로드 버튼 추가 -->
    <form id="uploadForm" method="post" action="/tms/api/devupload" enctype="multipart/form-data">
        <input type="file" name="file" accept=".xlsx, .xls" required />
        <button type="submit">업로드</button>
    </form>
    <form method="get" action="/tms/devexampleexcel">
	    <button type="submit">전체 엑셀 다운로드</button>
	</form>

	    
	
	<table>
	    <thead>
	        <tr>
	            <th>SEQ</th>
	            <th>업무 중분류</th>
	            <th>프로그램 구분</th>
	            <th>프로그램명</th>
	            <!-- 더 많은 컬럼들 -->
	        </tr>
	    </thead>
	    <tbody>
	        <c:forEach var="dev" items="${devProgressList}">
	            <tr>
	                <td>${dev.seq}</td>
	                <td>${dev.subCategory}</td>
	                <td>${dev.programType}</td>
	                <td>${dev.programName}</td>
	                <!-- 더 많은 컬럼들 -->
	            </tr>
	        </c:forEach>
	    </tbody>
	</table>
	
	<!-- 페이지네이션 -->
	<div>
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
  </body>
</html>
