<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
  <head>
    <%@ include file="./common.jsp" %>
    <meta charset="UTF-8" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/commonCode.css" />

    <title>TMS 공통코드 관리</title>
    <script type="module" src="${pageContext.request.contextPath}/resources/js/commonCode.js"></script>
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
            <form id="downloadTemplateForm" action="ccexampleexcel" method="get" class="flex-box align-center">
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
      <div id="commonCodeFileDownloadModal" class="modal">
        <!-- Modal content -->
        <div class="modal-content">
          <h2>엑셀 다운로드</h2>
          <div class="download-box">
            <form id="downloadAllCommonCodeForm" action="downloadAllcc" method="get" class="flex-box align-center">
              <button type="submit" id="downloadAllCommonCodeButton" class="excel-button">
                <img src="${pageContext.request.contextPath}/resources/images/download_icon.png" />전체자료 다운로드
              </button>
              전체 데이터를 주별로 다운로드 받아 실적 집계로 활용
            </form>

            <form
              id="downloadFilteredCommonCodeForm"
              action="downloadFilteredcc"
              method="get"
              id="downloadFilteredForm"
              class="flex-box align-center"
            >
              <!-- 숨겨진 input 필드 -->
              <input type="hidden" id="parentCodeForDownload" name="parentCode" />
              <input type="hidden" id="codeForDownload" name="code" />
              <input type="hidden" id="codeNameForDownload" name="codeName" />

              <button type="submit" id="downloadFilteredCommonCodeButton" class="excel-button">
                <img src="${pageContext.request.contextPath}/resources/images/download_icon.png" />
                조회결과 다운로드
              </button>
              조회조건에 의한 결과만 다운로드
            </form>
          </div>

          <div class="flex-box justify-end">
            <button type="button" class="cancel-button" id="closeCommonCodeFileDownloadModalButton">닫기</button>
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
          <button type="reset" id="resetButton" class="reset-button">초기화</button>
          <button type="submit" id="searchCommonCodeButton">조회</button>
        </div>
      </form>

      <!-- 버튼 그룹 -->
      <div class="button-group">
        <div class="flex-box">
          <button id="openCommonCodeRegisterModalButton">등록</button>
          <button id="deleteCommonCodeButton" class="delete-button">삭제</button>
        </div>
        <div class="flex-box">
          <button id="openFileUploadModalButton" class="excel-button">
            <img src="${pageContext.request.contextPath}/resources/images/upload_icon.png" />엑셀 업로드
          </button>
          <button id="openCommonCodeFileDownloadModalButton" class="excel-button">
            <img src="${pageContext.request.contextPath}/resources/images/download_icon.png" />엑셀 다운로드
          </button>
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

      <div id="totalCount"></div>

      <!-- 페이지네이션 -->
      <div id="commonCodePagination" class="pagination">
        <!-- 페이지네이션 버튼 동적으로 삽입 -->
      </div>
    </main>
  </body>
</html>
