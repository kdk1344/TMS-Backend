<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
  <head>
    <%@ include file="./common.jsp" %>
    <meta charset="UTF-8" />
    <link rel="stylesheet" type="text/css" href="../../resources/css/commonCode.css" />

    <title>TMS 공통코드 관리</title>
    <script type="module" src="../../resources/js/commonCode.js"></script>
  </head>

  <body>
    <header class="header">
      <!-- 공통 헤더 정보 동적으로 삽입-->
    </header>

    <main class="content">
      <h1 class="page-title">공통코드 관리</h1>

      <!-- Register Modal -->
      <div id="commonCodeRegisterModal" class="modal">
        <!-- Modal content -->
        <div class="modal-content">
          <h2>공통코드 등록</h2>
          <form id="commonCodeRegisterForm" class="modal-form">
            <div class="modal-form-group">
              <label for="parentCodeForRegister">상위코드</label>
              <select id="parentCodeForRegister" name="parentCode">
                <option value="00" selected>상위코드 등록</option>
                <!-- 상위코드 목록 동적 삽입 -->
              </select>
            </div>

            <div class="modal-form-group">
              <label for="codeForRegister">코드</label>
              <input
                type="text"
                id="codeForRegister"
                name="code"
                placeholder="숫자 2자리 입력(추후 변경불가)"
                required
                pattern="\d{2}"
                maxlength="2"
              />
            </div>

            <div class="modal-form-group">
              <label for="codeNameForRegister">코드명</label>
              <input
                type="text"
                id="codeNameForRegister"
                name="codeName"
                required
                placeholder="코드명을 입력해주세요"
              />
            </div>

            <div class="flex-box justify-end">
              <button type="button" id="closeCommonCodeRegisterModalButton" class="cancel-button">취소</button>
              <button type="submit" class="save-button">저장</button>
            </div>
          </form>
        </div>
      </div>

      <!-- Edit Modal -->
      <div id="commonCodeEditModal" class="modal">
        <!-- Modal content -->
        <div class="modal-content">
          <h2>공통코드 수정</h2>
          <form id="commonCodeEditForm" class="modal-form">
            <div class="modal-form-group">
              <label for="parentCodeForEdit">상위코드</label>
              <span id="parentCodeForEdit"></span>
              <span id="parentCodeNameForEdit"></span>
            </div>

            <div class="modal-form-group">
              <label for="codeForEdit">코드</label>
              <span id="codeForEdit"></span>
            </div>

            <div class="modal-form-group">
              <label for="codeNameForEdit">코드명</label>
              <input type="text" id="codeNameForEdit" name="codeName" required />
            </div>

            <div class="flex-box justify-end">
              <button type="button" id="closeCommonCodeEditModalButton" class="cancel-button">취소</button>
              <button type="submit" class="save-button">저장</button>
            </div>
          </form>
        </div>
      </div>

      <!-- File Download Modal -->
      <div id="commonCodeFileDownloadModal" class="modal">
        <!-- Modal content -->
        <div class="modal-content">
          <h2>엑셀 다운로드</h2>
          <div class="flex-box">
            <form id="downloadAllCommonCodeForm" action="downloadAllcc" method="get">
              <button type="submit" id="downloadAllCommonCodeButton" class="excel-button">전체자료 다운로드</button>
            </form>

            <form
              id="downloadFilteredCommonCodeForm"
              action="downloadFilteredcc"
              method="get"
              id="downloadFilteredForm"
            >
              <!-- 숨겨진 input 필드 -->
              <input type="hidden" id="parentCodeForDownload" name="parentCode" />
              <input type="hidden" id="codeForDownload" name="code" />
              <input type="hidden" id="codeNameForDownload" name="codeName" />

              <button type="submit" id="downloadFilteredCommonCodeButton" class="excel-button">
                조회결과 다운로드
              </button>
            </form>
          </div>
        </div>
      </div>

      <!-- 공통코드 조회 필터링 폼 -->
      <form id="commonCodeFilterForm">
        <div class="filter-container">
          <div class="form-group">
            <label for="parentCodeForFilter">상위코드</label>
            <select id="parentCodeForFilter" name="parentCode">
              <option value="" selected>전체</option>
            </select>
          </div>

          <div class="form-group">
            <label for="codeForFilter">코드</label>
            <select id="codeForFilter" name="code">
              <option value="" selected>전체</option>
            </select>
          </div>

          <div class="form-group">
            <label for="codeNameForFilter">코드명</label>
            <input type="text" id="codeNameForFilter" name="codeName" placeholder="코드명을 입력하세요" />
          </div>
        </div>
        <div class="flex-box align-end">
          <button type="submit" id="searchCommonCodeButton">조회</button>
          <button type="reset" id="resetButton" class="reset-button">초기화</button>
        </div>
      </form>

      <!-- 버튼 그룹 -->
      <div class="button-group">
        <div class="flex-box">
          <button id="openCommonCodeRegisterModalButton">등록</button>
          <button id="deleteCommonCodeButton" class="delete-button">삭제</button>
        </div>
        <div class="flex-box">
          <!-- 파일 선택 input -->
          <input type="file" id="uploadCommonCodeFileInput" name="file" accept=".xlsx, .xls" />

          <button id="uploadCommonCodeFileButton" class="excel-button">엑셀 업로드</button>
          <button id="openCommonCodeFileDownloadModalButton" class="excel-button">엑셀 다운로드</button>
        </div>
      </div>

      <!-- 공통코드 목록 테이블 -->
      <div class="table-container">
        <table id="commonCodeTable">
          <thead>
            <tr>
              <th><input type="checkbox" id="selectAllCommonCodeCheckbox" /></th>
              <th>상위코드</th>
              <th>상위코드명</th>
              <th>코드</th>
              <th>코드명</th>
            </tr>
          </thead>
          <tbody id="commonCodeTableBody">
            <!-- 공통코드 정보 동적으로 삽입-->
          </tbody>
        </table>
      </div>

      <!-- 페이지네이션 -->
      <div id="commonCodePagination" class="pagination">
        <!-- 페이지네이션 버튼 동적으로 삽입 -->
      </div>
    </main>
  </body>
</html>
