<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="ko">
  <head>
    <%@ include file="./common.jsp" %>
    <meta charset="UTF-8" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/dashboard.css" />

    <title>TMS 대시보드</title>
    <script type="module" src="${pageContext.request.contextPath}/resources/js/dashboard.js"></script>
  </head>

  <body>
    <header class="header">
      <!-- 공통 헤더 정보 동적으로 삽입-->
    </header>
    

    <main class="content">
      <div class="top-line">
	      <h1 class="page-title">메인화면</h1>
	      <button id="admin-notice" type="button" onclick="location.href='/tms/notice'">공지사항</button>
      </div>
      <!-- 화면 진입 시 클릭한 페이지 최신 및 공지사항 게시 내용 -->
	    <div class="main-display">
	    	<dl class="nt-details">
		    <div class="nt-field">
		      <dt>번호</dt>
		      <c:if test="${latestNotice.seq != 77777}">
			  	<dd id="nt-seq">${latestNotice.seq}</dd>
			  </c:if>
			  <c:if test="${latestNotice.seq == 77777}">
			  	<dd id="nt-seq">-</dd>
			  </c:if>
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
            <th class="notice-seq" style="width: 100px;">번호</th>
            <th class="notice-post-date">게시일자</th>
            <th class="notice-title">제목</th>
            <th class="notice-content">게시내용</th>
          </tr>
        </thead>
        <tbody id="noticeTableBody">
          	<c:forEach var="notice" items="${notices}">
                <tr onclick="loadNotice(${notice.seq})" style="cursor: pointer;">
                    <td class="notice-seq">
                        ${notice.seq}
	                </td>
                    <td class="notice-post-date">
                        <fmt:formatDate value="${notice.postDate}" pattern="yyyy-MM-dd" />
	                </td>
                    <td class="notice-title">
                        ${notice.title}
                    </td>
                    <td class="notice-contents">
                        ${notice.content}
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
		        var anchor = element.querySelector('a'); // 링크 태그를 선택
		        if (anchor) {
		            var text = anchor.textContent.trim(); // 공백 제거
		            
		            if (text.length > maxLength) {
		                // 링크 태그의 텍스트만 자르고, 링크 구조는 유지
		                anchor.textContent = text.substring(0, maxLength) + "...";
		            }
		        }
		    });
		  }
	
		  // 페이지 로드 시 모든 "notice-contents" 클래스 요소의 텍스트를 자른다
		  window.onload = function() {
		    truncateTextByClass("notice-contents", 50); // 최대 50글자까지만 표시
		  };
	  function loadNotice(seq) {
		    const fetchURL = '/tms/getNotice?seq=' + seq; // api 주소
		    fetch(fetchURL)
			    .then(response => {
		            if (!response.ok) {
		                throw new Error('Network response was not ok');
		            }
		            return response.json();
		        })
		        .then(data => {
		        	//날짜값 정리
		        	const postDate = new Date(data.postDate);
					const options = { year: 'numeric', month: '2-digit', day: '2-digit' };
					const formattedDate = new Intl.DateTimeFormat('en-CA', options).format(postDate);
		            // latestNotice 내용 업데이트
		            document.getElementById('nt-seq').textContent = data.seq;
		            document.getElementById('nt-post-date').textContent = formattedDate;
		            document.getElementById('nt-title').textContent = data.title;
		            document.querySelector('.notice-content').textContent = data.content;
		            
		            console.log('Fetched data:', data);

		            // 첨부파일 목록 업데이트
		            const attachmentsList = document.getElementById('nt-attachments');
		            attachmentsList.innerHTML = ''; // 기존 목록 초기화
		            data.attachments.forEach(attachment => {
		                const li = document.createElement('li');
		                const fileName = attachment.fileName;
		                console.log(fileName);
		                const url = '/tms/downloadAttachment?seq=' + attachment.seq; // URL 변수 정의
		                const linkHtml = '<a href="' + url + '">' + fileName + '</a>'; // linkHtml 변수 정의
		                console.log(linkHtml);
		                li.innerHTML = linkHtml;
		                attachmentsList.appendChild(li);
		            });
		        })
		        .catch(error => console.error('Error fetching notice:', error));
		}
  </script>
</html>
