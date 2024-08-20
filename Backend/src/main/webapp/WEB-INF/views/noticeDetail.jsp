<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
  <head>
    <%@ include file="./common.jsp" %>
    <meta charset="UTF-8" />
    <link rel="stylesheet" type="text/css" href="../../resources/css/noticeDetail.css" />

    <title>TMS 공지사항 상세</title>
    <script type="module" src="../../resources/js/noticeDetail.js"></script>
  </head>

  <body>
    <header class="header">
      <!-- 공통 헤더 정보 동적으로 삽입-->
    </header>

    <main class="content">
      <h1 class="page-title">공지사항 상세</h1>
      <dl class="notice-details">
        <div class="notice-field">
          <dt>번호</dt>
          <dd id="notice-seq"></dd>
        </div>
        <div class="notice-field">
          <dt>게시일자</dt>
          <dd id="notice-post-date"></dd>
        </div>
        <div class="notice-field">
          <dt>제목</dt>
          <dd id="notice-title"></dd>
        </div>
        <div class="notice-field">
          <dt>내용</dt>
          <dd id="notice-content"></dd>
        </div>
        <div class="notice-field">
          <dt>첨부파일</dt>
          <dd>
            <ul id="notice-attachments"></ul>
          </dd>
        </div>
      </dl>
    </main>
  </body>
</html>
