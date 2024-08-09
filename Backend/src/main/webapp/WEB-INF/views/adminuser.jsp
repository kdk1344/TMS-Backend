<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
  <head>
    <%@ include file="./common.jsp" %>
    <meta charset="UTF-8" />
    <link rel="stylesheet" type="text/css" href="../../resources/css/adminuser.css" />

    <title>System Administor</title>
    <script type="module" src="../../resources/js/adminuser.js?ver=1"></script>
  </head>

  <body>
    <div class="container">
      <div class="header">
        <h1>TMS</h1>
        <!-- <div class="search-bar">
          <select name="category" id="category">
            <option value="option1">카테고리1</option>
            <option value="option2">카테고리2</option>
          </select>
          <input type="text" id="search-input" placeholder="조회 조건 입력" />
          <button onclick="search()">조회</button>
          <button onclick="reset()">초기화</button>
        </div> -->
      </div>
      <div class="content">
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
                <label for="userName">사용자명</label>
                <input type="text" id="userName" name="userName" required />
              </div>

              <div class="form-group">
                <label for="password">비밀번호</label>
                <input type="password" id="password" name="password" required autocomplete />
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

              <div class="flex-box">
                <button type="submit">저장</button>
                <button type="button" id="closeModalButton">닫기</button>
              </div>
            </form>
          </div>
        </div>

        <!-- 버튼 그룹 -->
        <div class="button-group">
          <div class="flex-box">
            <button id="openModalButton">등록</button>
            <button onclick="deleteUserButton">삭제</button>
          </div>
          <div class="flex-box">
            <button id="uploadUserFileButton">파일 업로드</button>
            <button id="downloadUserFileButton">파일 다운로드</button>
          </div>
        </div>

        <!-- 사용자 목록 테이블 -->
        <table id="userTable">
          <thead>
            <tr>
              <th><input type="checkbox" id="selectAllUserCheckbox" /></th>
              <th>아이디</th>
              <th>이름</th>
              <th>직무</th>
            </tr>
          </thead>
          <tbody id="userTableBody">
            <!-- 사용자 정보 동적으로 삽입-->
          </tbody>
        </table>

        <!-- 페이지네이션 -->
        <div id="userPagination" class="pagination">
          <!-- 페이지네이션 버튼 동적으로 삽입 -->
        </div>
      </div>
      <div class="footer">
        <span>총회 리스팅 영역</span>
      </div>
    </div>
  </body>
  <script type="text/javasript"></script>
</html>
