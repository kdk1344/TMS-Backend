<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
  <head>
    <%@ include file="./common.jsp" %>
    <meta charset="UTF-8" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/testProgress.css" />

    <title>TMS 테스트 진행관리</title>
    <script type="module" src="${pageContext.request.contextPath}/resources/js/testProgress.js"></script>
  </head>

  <body>
    <header class="header">
      <!-- 공통 헤더 정보 동적으로 삽입-->
    </header>

    <main class="content">
      <h1 class="page-title">테스트 진행현황</h1>

      <!-- File Upload Modal -->
      <div id="testProgressFileUploadModal" class="modal">
        <!-- Modal content -->
        <div class="modal-content">
          <h2>엑셀 업로드</h2>
          <div class="excel-template-download-box">
            <pre>
엑셀로 업로드하여 다수의 목록을 한번에 입력할 수 있습니다. 
엑셀 양식에 데이터를 입력하여 업로드하세요. (파일 형식: xls, xlsx)</pre
            >
            <form id="downloadTemplateForm" action="testexampleexcel" method="get" class="flex-box align-center">
              <button type="submit" class="excel-button">
                <img src="${pageContext.request.contextPath}/resources/images/download_icon.png" />엑셀 양식 다운로드
              </button>
            </form>
          </div>

          <!-- 파일 선택 input -->
          <input
            type="file"
            id="uploadTestProgressFileInput"
            class="excel-upload-input"
            name="file"
            accept=".xlsx, .xls"
          />

          <div class="flex-box justify-end">
            <button type="button" class="cancel-button" id="closeTestProgressFileUploadModalButton">취소</button>
            <button type="button" class="save-button" id="uploadTestProgressFileButton">저장</button>
          </div>
        </div>
      </div>

      <!-- File Download Modal -->
      <div id="testProgressFileDownloadModal" class="modal">
        <!-- Modal content -->
        <div class="modal-content">
          <h2>엑셀 다운로드</h2>
          <div class="download-box">
            <form id="downloadAllTestProgressForm" action="testdownloadAll" method="get" class="flex-box align-center">
              <button type="submit" id="downloadAllTestProgressButton" class="excel-button">
                <img src="${pageContext.request.contextPath}/resources/images/download_icon.png" />전체자료 다운로드
              </button>
              전체 데이터를 주별로 다운로드 받아 실적 집계로 활용
            </form>

            <form
              id="downloadFilteredTestProgressForm"
              action="testdownloadFiltered"
              method="get"
              id="downloadFilteredForm"
              class="flex-box align-center"
            >
              <!-- 숨겨진 input 필드 -->
              <input type="hidden" id="testStageForDownload" name="testStage" />
              <input type="hidden" id="majorCategoryForDownload" name="majorCategory" />
              <input type="hidden" id="subCategoryForDownload" name="subCategory" />
              <input type="hidden" id="programTypeForDownload" name="programType" />
              <input type="hidden" id="testIdForDownload" name="testId" />
              <input type="hidden" id="screenIdForDownload" name="screenId" />
              <input type="hidden" id="screenNameForDownload" name="screenName" />
              <input type="hidden" id="programIdForDownload" name="programId" />
              <input type="hidden" id="programNameForDownload" name="programName" />
              <input type="hidden" id="developerForDownload" name="developer" />
              <input type="hidden" id="plForDownload" name="pl" />
              <input type="hidden" id="execCompanyMgrForDownload" name="execCompanyMgr" />
              <input type="hidden" id="thirdPartyTestMgrForDownload" name="thirdPartyTestMgr" />
              <input type="hidden" id="itMgrForDownload" name="itMgr" />
              <input type="hidden" id="busiMgrForDownload" name="busiMgr" />
              <input type="hidden" id="testStatusForDownload" name="testStatus" />

              <button type="submit" id="downloadFilteredTestProgressButton" class="excel-button">
                <img src="${pageContext.request.contextPath}/resources/images/download_icon.png" />
                조회결과 다운로드
              </button>
              조회조건에 의한 결과만 다운로드
            </form>
          </div>

          <div class="flex-box justify-end">
            <button type="button" class="cancel-button" id="closeTestProgressFileDownloadModalButton">닫기</button>
          </div>
        </div>
      </div>

      <!-- 테스트 진행현황 조회 필터링 폼 -->
      <form id="testProgressFilterForm">
        <div class="filter-container">
          <div class="form-group">
            <label for="testStageForFilter">테스트 단계</label>
            <select id="testStageForFilter" name="testStage">
              <option value="" selected>전체</option>
            </select>
          </div>

          <div class="form-group">
            <label for="majorCategoryForFilter">업무 대분류</label>
            <select id="majorCategoryForFilter" name="majorCategory">
              <option value="" selected>전체</option>
            </select>
          </div>

          <div class="form-group">
            <label for="subCategoryForFilter">업무 중분류</label>
            <select id="subCategoryForFilter" name="subCategory">
              <option value="" selected>전체</option>
            </select>
          </div>

          <div class="form-group">
            <label for="programTypeForFilter">프로그램 구분</label>
            <select id="programTypeForFilter" name="programType">
              <option value="" selected>전체</option>
            </select>
          </div>

          <div class="form-group">
            <select id="testKeySelect" name="testKey">
              <option value="testId" selected>테스트ID</option>
              <option value="screenId">화면ID</option>
              <option value="screenName">화면명</option>
              <option value="programId">호출프로그램ID</option>
              <option value="programName">호출프로그램명</option>
            </select>
            <input type="text" id="testValueInput" name="testValue" />
          </div>

          <div class="form-group">
            <select id="testRoleKeySelect" name="testRoleKey">
              <option value="developer" selected>개발자</option>
              <option value="pl">PL</option>
              <option value="execCompanyMgr">수행사담당자</option>
              <option value="thirdPartyTestMgr">제3자테스터</option>
              <option value="itMgr">고객IT담당자</option>
              <option value="busiMgr">고객현업담당자</option>
            </select>
            <input type="text" id="testRoleValueInput" name="testRoleValue" placeholder="이름" />
          </div>

          <div class="form-group">
            <label for="testStatusForFilter">진행상태</label>
            <select id="testStatusForFilter" name="testStatus">
              <option value="" selected>전체</option>
            </select>
          </div>
        </div>

        <div class="flex-box justify-end">
          <button type="reset" id="resetButton" class="reset-button">초기화</button>
          <button type="submit" id="searchTestProgressButton">조회</button>
        </div>
      </form>

      <!-- 버튼 그룹 -->
      <div class="button-group">
        <div class="flex-box">
          <button id="goTestProgressRegisterPageButton">등록</button>
          <button id="deleteTestProgressButton" class="delete-button">삭제</button>
        </div>
        <div class="flex-box">
          <button id="openTestProgressFileUploadModalButton" class="excel-button">
            <img src="${pageContext.request.contextPath}/resources/images/upload_icon.png" />엑셀 업로드
          </button>
          <button id="openTestProgressFileDownloadModalButton" class="excel-button">
            <img src="${pageContext.request.contextPath}/resources/images/download_icon.png" />엑셀 다운로드
          </button>
        </div>
      </div>

      <!-- 테스트 진행현황 테이블 -->
      <div class="table-container">
        <table id="testProgressTable">
          <thead id="testProgressTableHead">
            <!-- 테스트 진행 정보 헤더 동적으로 삽입 -->
          </thead>
          <tbody id="testProgressTableBody">
            <!-- 테스트 진행 정보 동적으로 삽입-->
          </tbody>
        </table>
      </div>

      <div id="totalCount"></div>

      <!-- 페이지네이션 -->
      <div id="testProgressPagination" class="pagination">
        <!-- 페이지네이션 버튼 동적으로 삽입 -->
      </div>
    </main>
  </body>
</html>
