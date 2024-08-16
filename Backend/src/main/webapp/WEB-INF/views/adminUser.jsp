<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
  <head>
    <%@ include file="./common.jsp" %>
    <meta charset="UTF-8" />
    <link rel="stylesheet" type="text/css" href="../../resources/css/adminUser.css" />

    <title>TMS</title>
    <script type="module" src="../../resources/js/adminUser.js"></script>
  </head>

  <body>
    <header class="header">
      <!-- 공통 헤더 정보 동적으로 삽입-->
    </header>
    <h1 class="page-title">사용자 관리</h1>
    <div class="content">
      <!-- Register Modal -->
      <div id="userRegisterModal" class="modal">
        <!-- Modal content -->
        <div class="modal-content">
          <h2>사용자 등록</h2>
          <form id="userRegisterForm" class="modal-form">
            <div class="modal-form-group">
              <label for="userID">아이디</label>
              <input type="text" id="userID" name="userID" placeholder="수정 시 변경불가" required />
            </div>

            <div class="modal-form-group">
              <label for="userName">사용자명</label>
              <input type="text" id="userName" name="userName" required />
            </div>

            <div class="modal-form-group">
              <label for="password">비밀번호</label>
              <input type="password" id="password" name="password" required autocomplete />
            </div>

            <div class="modal-form-group">
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
              <button type="button" id="closeUserRegisterModalButton">닫기</button>
            </div>
          </form>
        </div>
      </div>

      <!-- Edit Modal -->
      <div id="userEditModal" class="modal">
        <!-- Modal content -->
        <div class="modal-content">
          <h2>사용자 수정</h2>
          <form id="userEditForm" class="modal-form">
            <div class="modal-form-group">
              <label for="userIDForEdit">아이디</label>
              <input type="text" id="userIDForEdit" name="userID" readonly required />
            </div>

            <div class="modal-form-group">
              <label for="userNameForEdit">사용자명</label>
              <input type="text" id="userNameForEdit" name="userName" required />
            </div>

            <div class="modal-form-group">
              <label for="passwordForEdit">비밀번호</label>
              <input type="password" id="passwordForEdit" name="password" required autocomplete />
            </div>

            <div class="modal-form-group">
              <label for="authorityNameForEdit">직무</label>
              <select id="authorityNameForEdit" name="authorityName">
                <option value="type1">직무 1</option>
                <option value="type2">직무 2</option>
                <option value="type3">직무 3</option>
                <option value="type4">직무 4</option>
              </select>
            </div>

            <div class="flex-box">
              <button type="submit">저장</button>
              <button type="button" id="closeUserEditModalButton">닫기</button>
            </div>
          </form>
        </div>
      </div>

      <!-- File Download Modal -->
      <div id="userFileDownloadModal" class="modal">
        <!-- Modal content -->
        <div class="modal-content">
          <h2>엑셀 파일 다운로드</h2>
          <div class="flex-box">
            <form id="downloadAllUserForm" action="downloadAll" method="get">
              <button type="submit" id="downloadAllUserButton">전체 사용자 목록 다운로드</button>
            </form>

            <form id="downloadFilteredUserForm" action="downloadFiltered" method="get" id="downloadFilteredForm">
              <!-- 숨겨진 input 필드 -->
              <input type="hidden" id="downloadUserName" name="userName" />
              <input type="hidden" id="downloadAuthorityName" name="authorityName" />

              <button type="submit" id="downloadFilteredUserButton">조회된 사용자 목록 다운로드</button>
            </form>
          </div>
        </div>
      </div>

      <!-- 사용자 조회 필터링 폼 -->
      <form id="userFilterForm">
        <div class="filter-container">
          <div class="form-group">
            <label for="userName">이름</label>
            <input type="text" id="userNameForFilter" name="userName" placeholder="이름을 입력하세요" />
          </div>
          <div class="form-group">
            <label for="authorityNameForFilter">직무</label>
            <select id="authorityNameForFilter" name="authorityName">
              <option value="">전체</option>
              <option value="type1">직무 1</option>
              <option value="type2">직무 2</option>
              <option value="type3">직무 3</option>
              <option value="type4">직무 4</option>
            </select>
          </div>
        </div>
        <div class="flex-box">
          <button type="submit" id="searchUserButton">조회</button>
          <button type="reset" id="resetButton">초기화</button>
        </div>
      </form>

      <!-- 버튼 그룹 -->
      <div class="button-group">
        <div class="flex-box">
          <button id="openUserRegisterModalButton">등록</button>
          <button id="deleteUserButton">삭제</button>
        </div>
        <div class="flex-box">
          <!-- 파일 선택 input -->
          <input type="file" id="uploadUserFileInput" name="file" accept=".xlsx, .xls" />

          <button id="uploadUserFileButton">파일 업로드</button>
          <button id="openUserFileDownloadModalButton">파일 다운로드</button>
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
  </body>
</html>
