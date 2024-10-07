<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
  <head>
    <%@ include file="./common.jsp" %>
    <meta charset="UTF-8" />
    <link rel="stylesheet" type="text/css" href="../../resources/css/login.css" />

    <title>TMS 로그인</title>
    <script type="module" src="../../resources/js/login.js"></script>
  </head>

  <body>
    <header class="flex-box title">
      <h1 class="logo">TMS</h1>
      <span class="sub-title">Test Management System</span>
    </header>

    <main class="login-container">
      <section id="testGuideSection">
        <div class="test-guide-title-box">
          <h2>
            테스트 참여자<br />
            절차안내
          </h2>
        </div>

        <div class="test-guide-button-container">
          <button class="test-guide-button" type="button">
            <img src="../../resources/images/test_icon.png" />개발 진행관리
          </button>
          <button class="test-guide-button" type="button">
            <img src="../../resources/images/test_icon.png" />제3자 테스트
          </button>
          <button class="test-guide-button" type="button">
            <img src="../../resources/images/test_icon.png" />통합테스트
          </button>
          <button class="test-guide-button" type="button">
            <img src="../../resources/images/test_icon.png" />결함관리
          </button>
        </div>
      </section>

      <div class="notice-login-container">
        <section id="noticeSection">
          <div class="notice-header">
            <h2>공지사항</h2>
            <buttoon type="button" class="tooltip">
              ➕
              <span class="tooltip-text">로그인 후 공지사항 화면에서 상세내용을 확인하실 수 있습니다.</span></buttoon
            >
          </div>
          <ul id="noticeList" class="custom-scroll"></ul>
        </section>

        <section id="loginSection">
          <!-- 로그인 폼 -->
          <form id="loginForm">
            <h2>로그인</h2>
            <div class="form-group">
              <label for="userID">아이디</label>
              <input type="text" id="userID" name="userID" placeholder="아이디" required autofocus />
            </div>
            <div class="form-group">
              <label for="password">비밀번호</label>
              <input type="password" id="password" name="password" placeholder="비밀번호" required autocomplete />
            </div>
            <button type="submit">로그인</button>
          </form>
          <div class="login-info">
            <pre>
TMS시스템 사용자 등록은 
관리자에게 문의하시기 바랍니다.

- 관리자: 홍길동
- 전화번호: 02-1234-5678</pre
            >
          </div>
        </section>
      </div>
    </main>
  </body>
</html>
