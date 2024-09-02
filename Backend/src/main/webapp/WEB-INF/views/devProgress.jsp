<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
  <head>
    <%@ include file="./common.jsp" %>
    <meta charset="UTF-8" />
    <link rel="stylesheet" type="text/css" href="../../resources/css/devProgress.css" />

    <title>TMS 개발진행 관리</title>
    <script type="module" src="../../resources/js/devProgress.js"></script>
  </head>

  <body>
    <header class="header">
      <!-- 공통 헤더 정보 동적으로 삽입-->
    </header>

    <main class="content">
      <h1 class="page-title">프로그램 개발 진행현황</h1>

      <!-- 개발자 검색 모달 -->
      <div id="developerSearchModal" class="modal">
        <!-- Modal content -->
        <div class="modal-content">
          <div class="flex-box justify-between">
            <h2>개발자 조회</h2>
            <button type="button" id="closeDeveloperSearchModalButton" class="modal-close-button">
              <img src="../../resources/images/close_icon.png" alt="닫기" />
            </button>
          </div>
          <div class="developer-list-header">
            <p>아이디</p>
            <p>이름</p>
          </div>
          <ul class="developer-list" id="developerList">
            <!-- 개발자 목록 동적 삽입 -->
          </ul>
        </div>
      </div>

      <!-- File Download Modal -->
      <div id="devProgressFileDownloadModal" class="modal">
        <!-- Modal content -->
        <div class="modal-content">
          <h2>엑셀 다운로드</h2>
          <div class="download-box">
            <form id="downloadAllDevProgressForm" action="devdownloadAll" method="get" class="flex-box align-center">
              <button type="submit" id="downloadAllDevProgressButton" class="excel-button">
                <img src="../../resources/images/download_icon.png" />전체자료 다운로드
              </button>
              전체 데이터를 주별로 다운로드 받아 실적 집계로 활용
            </form>

            <form
              id="downloadFilteredDevProgressForm"
              action="devdownloadFiltered"
              method="get"
              id="downloadFilteredForm"
              class="flex-box align-center"
            >
              <!-- 숨겨진 input 필드 -->
              <input type="hidden" id="majorCategoryForDownload" name="majorCategory" />
              <input type="hidden" id="subCategoryForDownload" name="subCategory" />
              <input type="hidden" id="programTypeForDownload" name="programType" />
              <input type="hidden" id="programIdForDownload" name="programId" />
              <input type="hidden" id="programNameForDownload" name="programName" />
              <input type="hidden" id="programStatusForDownload" name="programStatus" />
              <input type="hidden" id="developerForDownload" name="developer" />
              <input type="hidden" id="plForDownload" name="pl" />
              <input type="hidden" id="thirdPartyTestMgrForDownload" name="thirdPartyTestMgr" />
              <input type="hidden" id="itMgrForDownload" name="itMgr" />
              <input type="hidden" id="busiMgrForDownload" name="busiMgr" />
              <input type="hidden" id="devStatusForDownload" name="devStatus" />
              <input type="hidden" id="actualEndDateFromForDownload" name="devStartDate" />
              <input type="hidden" id="actualEndDateToForDownload" name="devEndDate" />

              <button type="submit" id="downloadFilteredDevProgressButton" class="excel-button">
                <img src="../../resources/images/download_icon.png" />
                조회결과 다운로드
              </button>
              조회조건에 의한 결과만 다운로드
            </form>
          </div>

          <div class="flex-box justify-end">
            <button type="button" class="cancel-button" id="closeDevProgressFileDownloadModalButton">닫기</button>
          </div>
        </div>
      </div>

      <!-- 개발진행 조회 필터링 폼 -->
      <form id="devProgressFilterForm">
        <div class="filter-container">
          <div class="filter-container-row">
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
          </div>

          <div class="filter-container-row">
            <div class="form-group">
              <select id="programKeySelect" name="programKey">
                <option value="programId">프로그램 ID</option>
                <option value="programName">프로그램명</option>
              </select>
              <input type="text" id="programValueInput" name="programValue" />
            </div>

            <div class="form-group">
              <label for="programStatusForFilter">프로그램 상태</label>
              <select id="programStatusForFilter" name="programStatus">
                <option value="" selected>전체</option>
              </select>
            </div>

            <div class="form-group">
              <label for="developerForFilter">개발자</label>
              <input type="text" id="developerForFilter" name="developer" />
            </div>
          </div>

          <div class="filter-container-row">
            <div class="form-group">
              <select id="roleKeySelect" name="role">
                <option value="pl">PL</option>
                <option value="thirdPartyTestMgr">제3자 테스터</option>
                <option value="itMgr">IT 담당자</option>
                <option value="busiMgr">현업 담당자</option>
              </select>
              <input type="text" id="roleValueInput" name="roleValue" placeholder="이름을 입력하세요" />
            </div>

            <div class="form-group">
              <label for="devStatusForFilter">개발진행 상태</label>
              <select id="devStatusForFilter" name="devStatus">
                <option value="" selected>전체</option>
              </select>
            </div>

            <div class="form-group">
              <label for="actualEndDateFromForFilter">개발 완료일</label>
              <input id="actualEndDateFromForFilter" name="devStartDate" type="date" />
              <span class="dash">-</span>
              <input id="actualEndDateToForFilter" name="devEndDate" type="date" />
            </div>
          </div>
        </div>

        <div class="flex-box justify-end">
          <button type="reset" id="resetButton" class="reset-button">초기화</button>
          <button type="submit" id="searchDevProgressButton">조회</button>
        </div>
      </form>

      <!-- 버튼 그룹 -->
      <div class="button-group">
        <div class="flex-box">
          <button id="openDevProgressRegisterModalButton">등록</button>
          <button id="deleteDevProgressButton" class="delete-button">삭제</button>
        </div>
        <div class="flex-box">
          <!-- 파일 선택 input -->
          <input type="file" id="uploadDevProgressFileInput" name="file" accept=".xlsx, .xls" />

          <button id="uploadDevProgressFileButton" class="excel-button">
            <img src="../../resources/images/upload_icon.png" />엑셀 업로드
          </button>
          <button id="openDevProgressFileDownloadModalButton" class="excel-button">
            <img src="../../resources/images/download_icon.png" />엑셀 다운로드
          </button>
        </div>
      </div>

      <!-- 개발진행 목록 테이블 -->
      <div class="table-container">
        <table id="devProgressTable">
          <thead>
            <tr>
              <th><input type="checkbox" id="selectAllDevProgressCheckbox" /></th>
              <th class="sub-category">업무<br />중분류</th>
              <th class="program-type">프로그램<br />구분</th>
              <th class="program-id">프로그램 ID</th>
              <th class="program-name">프로그램명</th>
              <th class="screen-id">화면ID</th>
              <th class="screen-name">화면명</th>
              <th class="program-status">프로그램<br />상태</th>
              <th class="developer">개발자</th>
              <th class="pl">PL</th>
              <th class="planned-start-date">시작<br />예정일</th>
              <th class="planned-end-date">완료<br />예정일</th>
              <th class="actual-start-date">시작일</th>
              <th class="actual-end-date">완료일</th>
              <th class="pl-test-cmp-date">PL테스트<br />완료일</th>
              <th class="it-mgr">IT<br />담당자</th>
              <th class="busi-mgr">협업<br />담당자</th>
              <th class="dev-status">개발<br />진행상태</th>
            </tr>
          </thead>
          <tbody id="devProgressTableBody">
            <!-- 개발진행 정보 동적으로 삽입-->
          </tbody>
        </table>
      </div>

      <!-- 페이지네이션 -->
      <div id="devProgressPagination" class="pagination">
        <!-- 페이지네이션 버튼 동적으로 삽입 -->
      </div>
    </main>
  </body>
</html>
