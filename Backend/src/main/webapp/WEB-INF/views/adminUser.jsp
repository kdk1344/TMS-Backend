<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
  <head>
    <%@ include file="./common.jsp" %>
    <meta charset="UTF-8" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/adminUser.css" />

    <title>TMS 사용자 관리</title>
    <script type="module" src="${pageContext.request.contextPath}/resources/js/adminUser.js"></script>
  </head>

  <body>
    <header class="header">
      <!-- 공통 헤더 정보 동적으로 삽입-->
    </header>

    <main class="content">
      <h1 class="page-title">사용자 관리</h1>

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
              <label for="authorityName">직무</label>
              <select id="authorityName" name="authorityName">
                <option value="관리자">관리자</option>
                <option value="PM,사업/품질">PM,사업/품질</option>
                <option value="테스트관리자">테스트관리자</option>
                <option value="PL">PL</option>
                <option value="개발자">개발자</option>
                <option value="테스터">테스터</option>
                <option value="고객 IT 담당자">고객 IT 담당자</option>
                <option value="고객 현업 담당자">고객 현업 담당자</option>
              </select>
            </div>

            <div class="flex-box justify-end">
              <button type="button" id="closeUserRegisterModalButton" class="cancel-button">취소</button>
              <button type="submit" class="save-button">저장</button>
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
                <option value="관리자">관리자</option>
                <option value="PM,사업/품질">PM,사업/품질</option>
                <option value="테스트관리자">테스트관리자</option>
                <option value="PL">PL</option>
                <option value="개발자">개발자</option>
                <option value="테스터">테스터</option>
                <option value="고객 IT 담당자">고객 IT 담당자</option>
                <option value="고객 현업 담당자">고객 현업 담당자</option>
              </select>
            </div>

            <div class="flex-box justify-end">
              <button type="button" id="closeUserEditModalButton" class="cancel-button">취소</button>
              <button type="submit" class="save-button">저장</button>
            </div>
          </form>
        </div>
      </div>

      <!-- File Upload Modal -->
      <div id="fileUploadModal" class="modal">
        <!-- Modal content -->
        <div class="modal-content">
          <h2>엑셀 업로드</h2>
          <div class="excel-template-download-box">
            <pre>
엑셀로 업로드하여 다수의 목록을 한번에 입력할 수 있습니다. 
엑셀 양식에 데이터를 입력하여 업로드하세요. (파일 형식: xls, xlsx)</pre
            >
            <form id="downloadTemplateForm" action="userexampleexcel" method="get" class="flex-box align-center">
              <button type="submit" class="excel-button">
                <img src="${pageContext.request.contextPath}/resources/images/download_icon.png" />엑셀 양식 다운로드
              </button>
            </form>
          </div>

          <!-- 파일 선택 input -->
          <input type="file" id="uploadFileInput" class="excel-upload-input" name="file" accept=".xlsx, .xls" />

          <div class="flex-box justify-end">
            <button type="button" class="cancel-button" id="closeFileUploadModalButton">취소</button>
            <button type="button" class="save-button" id="uploadFileButton">저장</button>
          </div>
        </div>
      </div>

      <!-- File Download Modal -->
      <div id="userFileDownloadModal" class="modal">
        <!-- Modal content -->
        <div class="modal-content">
          <h2>엑셀 다운로드</h2>
          <div class="download-box">
            <form id="downloadAllUserForm" action="downloadAll" method="get" class="flex-box align-center">
              <button type="submit" id="downloadAllUserButton" class="excel-button">
                <img src="${pageContext.request.contextPath}/resources/images/download_icon.png" />전체자료 다운로드
              </button>
              전체 데이터를 주별로 다운로드 받아 실적 집계로 활용
            </form>

            <form
              id="downloadFilteredUserForm"
              action="downloadFiltered"
              method="get"
              id="downloadFilteredForm"
              class="flex-box align-center"
            >
              <!-- 숨겨진 input 필드 -->
              <input type="hidden" id="downloadUserName" name="userName" />
              <input type="hidden" id="downloadAuthorityName" name="authorityName" />

              <button type="submit" id="downloadFilteredUserButton" class="excel-button">
                <img src="${pageContext.request.contextPath}/resources/images/download_icon.png" />조회결과 다운로드
              </button>
              조회조건에 의한 결과만 다운로드
            </form>
          </div>
          <div class="flex-box justify-end">
            <button type="button" class="cancel-button" id="closeUserFileDownloadModalButton">닫기</button>
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
              <option value="관리자">관리자</option>
              <option value="PM,사업/품질">PM,사업/품질</option>
              <option value="테스트관리자">테스트관리자</option>
              <option value="PL">PL</option>
              <option value="개발자">개발자</option>
              <option value="테스터">테스터</option>
              <option value="고객 IT 담당자">고객 IT 담당자</option>
              <option value="고객 현업 담당자">고객 현업 담당자</option>
            </select>
          </div>
        </div>
        <div class="flex-box align-end">
          <button type="reset" id="resetButton" class="reset-button">초기화</button>
          <button type="submit" id="searchUserButton">조회</button>
        </div>
      </form>

      <!-- 버튼 그룹 -->
      <div class="button-group">
        <div class="flex-box">
          <button id="openUserRegisterModalButton">등록</button>
          <button id="deleteUserButton" class="delete-button">삭제</button>
        </div>
        <div class="flex-box">
          <button id="openFileUploadModalButton" class="excel-button">
            <img src="${pageContext.request.contextPath}/resources/images/upload_icon.png" />엑셀 업로드
          </button>
          <button id="openUserFileDownloadModalButton" class="excel-button">
            <img src="${pageContext.request.contextPath}/resources/images/download_icon.png" />엑셀 다운로드
          </button>
        </div>
      </div>

      <!-- 사용자 목록 테이블 -->
      <div class="table-container">
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
      </div>

      <div id="totalCount"></div>

      <!-- 페이지네이션 -->
      <div id="userPagination" class="pagination">
        <!-- 페이지네이션 버튼 동적으로 삽입 -->
      </div>
    </main>
  </body>
</html>
