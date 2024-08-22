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
    <main class="login-container">
      <div class="flex-box title">
        <h1 class="logo">TMS</h1>
        <span class="sub-title">Test Management System</span>
      </div>

      <!-- 로그인 폼 -->
      <form id="loginForm">
        <div class="form-group">
          <label for="userID">아이디</label>
          <input type="text" id="userID" name="userID" placeholder="아이디" required />
        </div>
        <div class="form-group">
          <label for="password">비밀번호</label>
          <input type="password" id="password" name="password" placeholder="비밀번호" required autocomplete />
        </div>
        <button type="submit">로그인</button>
      </form>
    </main>
  </body>
</html>
