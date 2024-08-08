<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
  <head>
    <%@ include file="./common.jsp" %>
    <meta charset="UTF-8" />
    <link rel="stylesheet" type="text/css" href="../../resources/css/adminuser.css" />

    <title>System Administor</title>
    <script type="module" src="../../resources/js/adminuser.js?ver=1" defer></script>
  </head>

  <body>
    <div class="container">
      <div class="header">
        <h1>TMS</h1>
        <div class="search-bar">
          <select name="category" id="category">
            <option value="option1">카테고리1</option>
            <option value="option2">카테고리2</option>
          </select>
          <input type="text" id="search-input" placeholder="조회 조건 입력" />
          <button onclick="search()">조회</button>
          <button onclick="reset()">초기화</button>
        </div>
      </div>
      <div class="content">
        <div class="buttons">
          <!-- Trigger the modal with a button -->
          <button id="openModalButton">등록</button>

          <!-- Modal -->
          <div id="userRegisterModal" class="modal">
            <!-- Modal content -->
            <div class="modal-content">
              <h2>사용자 등록/수정</h2>
              <form action="join" method="post">
                <div class="form-group">
                  <label for="userID">아이디</label>
                  <input type="text" id="userID" name="userID" placeholder="수정 시 변경불가" required />
                </div>

                <div class="form-group">
                  <label for="username">사용자명</label>
                  <input type="text" id="username" name="username" required />
                </div>

                <div class="form-group">
                  <label for="password">비밀번호</label>
                  <input type="password" id="password" name="password" required />
                </div>

                <div class="form-group">
                  <label for="authorityName">직무</label>
                  <select id="authorityName" name="authorityName">
                    <option value="type1">직무 1</option>
                    <option value="type2">직무 2</option>
                    <option value="type3">직무 3</option>
                    <option value="type4">직무 4</option>
                  </select>
                </div>

                <div class="button-container">
                  <button type="submit">저장</button>
                  <button type="button" id="closeModalButton">닫기</button>
                </div>
              </form>
            </div>
          </div>

          <button onclick="deleteSelected()">삭제</button>
        </div>
        <ul class="list" id="userList">
          <!-- 리스트 데이터 표시 영역 -->
        </ul>
      </div>
      <div class="footer">
        <span>총회 리스팅 영역</span>
      </div>
    </div>
  </body>
  <script type="text/javasript"></script>
</html>
