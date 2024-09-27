<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="ko">
  <head>
    <%@ include file="./common.jsp" %>
    <meta charset="UTF-8" />
    <link rel="stylesheet" type="text/css" href="../../resources/css/dashboard.css" />

    <title>TMS 대시보드</title>
    <script type="module" src="../../resources/js/dashboard.js"></script>
  </head>

  <body>
    <header class="header">
      <!-- 공통 헤더 정보 동적으로 삽입-->
    </header>

    <main class="content">
      <h1 class="page-title">공지사항</h1>
      <!-- 화면 진입 시 최신 게시 내용 -->
	    <div class="main-display">
	    	<dl class="nt-details">
		    <div class="nt-field">
		      <dt>번호</dt>
		      <dd id="nt-seq">${latestNotice.seq}</dd>
		    </div>
		    <div class="nt-field">
		      <dt>게시일자</dt>
		      <dd id="nt-post-date">
		        <fmt:formatDate value="${latestNotice.postDate}" pattern="yyyy-MM-dd" />
		      </dd>
		    </div>
		    <div class="nt-field">
		      <dt>제목</dt>
		      <dd id="nt-title">${latestNotice.title}</dd>
		    </div>
		    <div class="nt-field">
		      <dt>내용</dt>
		      <dd class="notice-content">${latestNotice.content}</dd>
		    </div>
		    <div class="nt-field">
		      <dt>첨부파일</dt>
		      <dd>
		        <ul id="nt-attachments">
		          <c:forEach var="attachment" items="${latestNotice.attachments}">
		            <li><a href="/tms/downloadAttachment?seq=${attachment.seq}">${attachment.fileName}</a></li>
		          </c:forEach>
		        </ul>
		      </dd>
		    </div>
		  </dl>
		</div>

      <!-- 공지사항 목록 테이블 -->
      <table id="noticeTable">
        <thead>
          <tr>
            <th class="notice-seq">번호</th>
            <th class="notice-post-date">게시일자</th>
            <th class="notice-title">제목</th>
            <th class="notice-content">게시내용</th>
          </tr>
        </thead>
        <tbody id="noticeTableBody">
          <c:forEach var="notice" items="${notices}">
                <tr>
                    <td>
	                    <a href="/tms/ntdetail?seq=${notice.seq}">
	                        ${notice.seq}
	                    </a>
	                </td>
                    <td>
	                    <a href="/tms/ntdetail?seq=${notice.seq}">
	                        <fmt:formatDate value="${notice.postDate}" pattern="yyyy-MM-dd" />
	                    </a>
	                </td>
                    <td>
                    	<a href="/tms/ntdetail?seq=${notice.seq}">
                        ${notice.title}
                    	</a>
                    </td>
                    <td class="notice-contents">
	                    <a href="/tms/ntdetail?seq=${notice.seq}">
	                        ${notice.content}
	                    </a>
	                </td>
                </tr>
            </c:forEach>
        </tbody>
      </table>

      <!-- 페이지네이션 -->
      <div id="noticePagination" class="pagination">
	    <!-- 첫 페이지로 이동하는 << 링크 -->
	    <c:choose>
	        <c:when test="${currentPage > 1}">
	            <a href="?page=1&size=${size}">&lt;&lt;</a>
	        </c:when>
	        <c:otherwise>
	            <span class="disabled">&lt;&lt;</span>
	        </c:otherwise>
	    </c:choose>
	
	    <!-- 이전 페이지로 이동하는 < 링크 -->
	    <c:choose>
	        <c:when test="${currentPage > 1}">
	            <a href="?page=${currentPage - 1}&size=${size}">&lt;</a>
	        </c:when>
	        <c:otherwise>
	            <span class="disabled">&lt;</span>
	        </c:otherwise>
	    </c:choose>
	
	    <!-- 페이지 번호 출력 -->
	    <c:forEach begin="${startPage}" end="${endPage}" var="i">
	        <c:choose>
	            <c:when test="${i == currentPage}">
	                <strong class="active">${i}</strong>
	            </c:when>
	            <c:otherwise>
	                <a href="?page=${i}&size=${size}">${i}</a>
	            </c:otherwise>
	        </c:choose>
	    </c:forEach>
	
	    <!-- 다음 페이지로 이동하는 > 링크 -->
	    <c:choose>
	        <c:when test="${currentPage < totalPages}">
	            <a href="?page=${currentPage + 1}&size=${size}">&gt;</a>
	        </c:when>
	        <c:otherwise>
	            <span class="disabled">&gt;</span>
	        </c:otherwise>
	    </c:choose>
	
	    <!-- 마지막 페이지로 이동하는 >> 링크 -->
	    <c:choose>
	        <c:when test="${currentPage < totalPages}">
	            <a href="?page=${totalPages}&size=${size}">&gt;&gt;</a>
	        </c:when>
	        <c:otherwise>
	            <span class="disabled">&gt;&gt;</span>
	        </c:otherwise>
	    </c:choose>
	</div>
      
    </main>
  </body>
  <script>
	  function truncateTextByClass(className, maxLength) {
		    // 클래스 이름으로 모든 요소를 가져온다
		    var elements = document.getElementsByClassName(className);
		    
		    // 각 요소마다 텍스트를 자르고 "..."을 추가
		    Array.prototype.forEach.call(elements, function(element) {
    		  var text = element.textContent.trim(); // 공백 제거
		      
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
