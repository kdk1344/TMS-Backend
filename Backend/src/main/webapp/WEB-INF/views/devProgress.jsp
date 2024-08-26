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

    <form id="searchForm" method="get" action="/devProgress">
	    <div class="filter-row">
	        <div class="form-group">
	            <label for="majorCategory">업무 대분류</label>
	            <input type="text" id="majorCategory" name="majorCategory" value="${param.majorCategory}">
	        </div>
	        <div class="form-group">
	            <label for="subCategory">업무 중분류</label>
	            <input type="text" id="subCategory" name="subCategory" value="${param.subCategory}">
	        </div>
	        <div class="form-group">
	            <label for="programType">프로그램 구분</label>
	            <input type="text" id="programType" name="programType" value="${param.programType}">
	        </div>
	        <div class="form-group">
	            <label for="programName">프로그램명</label>
	            <input type="text" id="programName" name="programName" value="${param.programName}">
	        </div>
	        <!-- 추가 필드들 -->
	        <div class="form-group">
	            <label for="actualStartDate">실제 시작일</label>
	            <input type="date" id="actualStartDate" name="actualStartDate" value="${param.actualStartDate}">
	        </div>
	        <div class="form-group">
	            <label for="actualEndDate">실제 종료일</label>
	            <input type="date" id="actualEndDate" name="actualEndDate" value="${param.actualEndDate}">
	        </div>
	        <div class="form-group">
	            <label for="pl">PL</label>
	            <input type="text" id="pl" name="pl" value="${param.pl}">
	        </div>
	        <!-- 더 많은 필터 옵션들 추가 -->
	    </div>
	    <button type="submit">조회</button>
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
