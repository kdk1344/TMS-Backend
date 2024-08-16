<!-- 
<form method="get" action="/tms/notice">
    <input type="text" name="postDate" placeholder="게시일자" value="${param.postDate}" />
    <input type="text" name="title" placeholder="제목" value="${param.title}" />
    <input type="text" name="content" placeholder="내용" value="${param.content}" />
    <button type="submit">검색</button>
</form> -->

<!-- 공지사항 등록/수정 폼 -->
<!-- <form id="noticeForm" method="post" action="/tms/ntwrite" enctype="multipart/form-data">
    <input type="hidden" id="seq" name="seq" value="">
    <input type="hidden" id="formAction" name="formAction" value="/tms/ntwrite">
    <label for="postDate">게시일자:</label>
    <input type="date" id="postDate" name="postDate"><br>
    <label for="title">제목:</label>
    <input type="text" id="title" name="title"><br>
    <label for="content">내용:</label>
    <textarea id="content" name="content"></textarea><br>
    <label for="file">첨부파일:</label>
    <input type="file" id="file" name="file" multiple onchange="renderFileList()"><br>
    <ul id="fileList"></ul>
    <button type="submit" id="formSubmit">등록</button>
</form> -->

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
  <head>
    <%@ include file="./common.jsp" %>
    <meta charset="UTF-8" />
    <link rel="stylesheet" type="text/css" href="../../resources/css/notice.css" />

    <title>TMS</title>
    <script type="module" src="../../resources/js/notice.js"></script>
  </head>

  <body>
    <header class="header">
      <!-- 공통 헤더 정보 동적으로 삽입-->
    </header>
    <h1 class="page-title">공지사항 조회</h1>
    <div class="content">
      <!-- Register Modal -->
      <div id="noticeRegisterModal" class="modal">
        <!-- Modal content -->
        <div class="modal-content">
          <h2>공지사항 등록</h2>
          <form id="noticeRegisterForm" class="modal-form">
            <div class="modal-form-group">
              <label for="postDateForRegister">게시일자</label>
              <input type="date" id="postDateForRegister" name="postDate" required />
            </div>

            <div class="modal-form-group">
              <label for="titleForRegister">제목</label>
              <input type="text" id="titleForRegister" name="title" placeholder="제목을 입력해 주세요." required />
            </div>

            <div class="modal-form-group">
              <label for="contentForRegister">게시내용</label>
              <textarea id="contentForRegister" name="content" placeholder="내용을 입력해 주세요." required></textarea>
            </div>

            <div class="flex-box">
              <button type="submit">저장</button>
              <button type="button" id="closeNoticeRegisterModalButton">닫기</button>
            </div>
          </form>
        </div>
      </div>

      <!-- Edit Modal -->

      <!-- File Download Modal -->

      <!-- 공지사항 조회 필터링 폼 -->
      <form id="noticeFilterForm">
        <div class="filter-container">
          <div class="form-group">
            <label for="startPostDateForFilter">게시일자</label>
            <input id="startPostDateForFilter" name="startPostDate" type="date" />
            <span>-</span>
            <input id="endPostDateForFilter" name="endPostDate" type="date" />
          </div>
          <div class="form-group">
            <label for="titleForFilter">제목</label>
            <input type="text" id="titleForFilter" name="title" />
          </div>
          <div class="form-group">
            <label for="contentForFilter">내용</label>
            <input type="text" id="contentForFilter" name="content" />
          </div>
        </div>
        <div class="flex-box">
          <button type="submit" id="searchNoticeButton">조회</button>
          <button type="reset" id="resetButton">초기화</button>
        </div>
      </form>

      <!-- 버튼 그룹 -->
      <div class="button-group">
        <div class="flex-box">
          <button id="openNoticeRegisterModalButton">등록</button>
          <button id="deleteNoticeButton">삭제</button>
        </div>
        <div class="flex-box">
          <!-- 파일 선택 input -->
          <input type="file" id="uploadNoticeFileInput" name="file" accept=".xlsx, .xls" />

          <button id="uploadNoticeFileButton">파일 업로드</button>
          <button id="openNoticeFileDownloadModalButton">파일 다운로드</button>
        </div>
      </div>

      <!-- 공지사항 목록 테이블 -->
      <table id="noticeTable">
        <thead>
          <tr>
            <th><input type="checkbox" id="selectAllNoticeCheckbox" /></th>
            <th>게시일자</th>
            <th>제목</th>
            <th>게시내용</th>
          </tr>
        </thead>
        <tbody id="noticeTableBody">
          <!-- 공지사항 정보 동적으로 삽입-->
        </tbody>
      </table>

      <!-- 페이지네이션 -->
      <div id="noticePagination" class="pagination">
        <!-- 페이지네이션 버튼 동적으로 삽입 -->
      </div>
    </div>
  </body>
</html>
