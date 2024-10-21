<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
  <head>
    <%@ include file="./common.jsp" %>
    <meta charset="UTF-8" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/noticeDetail.css" />

    <title>TMS 공지사항 상세</title>
    <script type="module" src="${pageContext.request.contextPath}/resources/js/noticeDetail.js"></script>
  </head>

  <body>
    <header class="header">
      <!-- 공통 헤더 정보 동적으로 삽입-->
    </header>
    <!-- 공지사항 수정 모달 -->
    <div id="noticeEditModal" class="modal">
      <!-- Modal content -->
      <div class="modal-content">
        <h2>공지사항 수정</h2>
        <form id="noticeEditForm" class="modal-form">
          <div class="modal-form-group">
            <label for="postDateForEdit">게시일자</label>
            <input type="date" id="postDateForEdit" name="postDate" readonly />
          </div>

          <div class="modal-form-group">
            <label for="titleForEdit">제목</label>
            <input
              type="text"
              id="titleForEdit"
              name="title"
              placeholder="제목을 입력해 주세요."
              required
              maxlength="100"
            />
          </div>

          <div class="modal-form-group">
            <label for="contentForEdit">게시내용</label>
            <textarea
              id="contentForEdit"
              name="content"
              placeholder="내용을 입력해 주세요."
              required
              maxlength="10000"
            ></textarea>
          </div>

          <div class="modal-form-group">
            <label for="fileInputForEdit">첨부파일</label>
            <div class="file-preview-container">
              <button type="button" class="file-button" id="fileSelectButtonForEdit">파일 선택</button>
              <input type="file" id="fileInputForEdit" class="hidden" name="file" multiple />
              <div id="fileOutputForEdit" class="file-preview custom-scroll"></div>
            </div>
          </div>

          <div class="flex-box justify-end">
            <button type="button" id="closeNoticeEditModalButton" class="cancel-button">취소</button>
            <button type="submit" class="save-button">저장</button>
          </div>
        </form>
      </div>
    </div>

    <main class="content">
      <h1 class="page-title">공지사항 상세</h1>
      <dl class="notice-details">
        <div class="notice-field">
          <dt>번호</dt>
          <dd id="noticeSeq"></dd>
        </div>
        <div class="notice-field">
          <dt>게시일자</dt>
          <dd id="noticePostDate"></dd>
        </div>
        <div class="notice-field">
          <dt>제목</dt>
          <dd id="noticeTitle"></dd>
        </div>
        <div class="notice-field">
          <dt>내용</dt>
          <dd><pre id="noticeContent"></pre></dd>
        </div>
        <div class="notice-field">
          <dt>첨부파일</dt>
          <dd class="file-preview-container">
            <input type="file" id="fileInputForRead" class="hidden" name="file" multiple readonly />
            <div id="fileOutputForRead" class="file-preview custom-scroll"></div>
          </dd>
        </div>
      </dl>

      <!-- 버튼 그룹 -->
      <div class="notice-buttons">
        <button id="openNoticeEditModalButton" type="button" class="save-button">수정</button>
        <button id="deleteNoticeButton" type="button" class="delete-button">삭제</button>
        <button id="goNoticeListButton" type="button" class="list-button">목록</button>
      </div>
    </main>
  </body>
</html>
