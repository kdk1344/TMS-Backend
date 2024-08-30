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
          <h2>개발자 조회</h2>
          <ul class="developer-list" id="developerList">
            <!-- 개발자 목록 동적 삽입 -->
          </ul>
        </div>
      </div>

      <!-- Register Modal -->
      <div id="devProgressRegisterModal" class="modal">
        <!-- Modal content -->
        <div class="modal-content">
          <h2>개발진행 등록</h2>
          <form id="devProgressRegisterForm" class="modal-form">
            <div class="modal-form-group">
              <label for="stageTypeForRegister">구분</label>
              <div class="flex-box">
                <label>
                  <input type="radio" id="mainCategoryForRegister" name="stageType" value="대" checked />
                  대분류
                </label>
                <label>
                  <input type="radio" id="subCategoryForRegister" name="stageType" value="중" />
                  중분류
                </label>
              </div>
            </div>
            <div class="modal-form-group">
              <label for="parentCodeForRegister">대분류</label>
              <select id="parentCodeForRegister" name="parentCode" disabled>
                <option value="" selected disabled>대분류를 선택해주세요</option>
                <!-- 대분류 목록 동적 삽입 -->
              </select>
            </div>

            <div class="modal-form-group">
              <label for="codeForRegister">코드</label>
              <div class="code-display">
                <span id="partialCodeForRegister"></span>
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
              <button type="button" id="closeDevProgressRegisterModalButton" class="cancel-button">취소</button>
              <button type="submit" class="save-button">저장</button>
            </div>
          </form>
        </div>
      </div>

      <!-- Edit Modal -->
      <div id="devProgressEditModal" class="modal">
        <!-- Modal content -->
        <div class="modal-content">
          <h2>개발진행 수정</h2>
          <form id="devProgressEditForm" class="modal-form">
            <div class="modal-form-group">
              <label for="stageTypeForEdit">구분</label>
              <span id="stageTypeForEdit"></span>
            </div>

            <div class="modal-form-group">
              <label for="codeForEdit">코드</label>
              <span id="codeForEdit"></span>
            </div>

            <div class="modal-form-group">
              <label for="codeNameForEdit">코드명</label>
              <input type="text" id="codeNameForEdit" name="codeName" required placeholder="코드명을 입력해주세요" />
            </div>

            <div class="flex-box justify-end">
              <button type="button" id="closeDevProgressEditModalButton" class="cancel-button">취소</button>
              <button type="submit" class="save-button">저장</button>
            </div>
          </form>
        </div>
      </div>

      <!-- File Download Modal -->
      <div id="devProgressFileDownloadModal" class="modal">
        <!-- Modal content -->
        <div class="modal-content">
          <h2>엑셀 다운로드</h2>
          <div class="download-box">
            <form id="downloadAllDevProgressForm" action="catdownloadAll" method="get" class="flex-box align-center">
              <button type="submit" id="downloadAllDevProgressButton" class="excel-button">
                <img src="../../resources/images/download_icon.png" />전체자료 다운로드
              </button>
              전체 데이터를 주별로 다운로드 받아 실적 집계로 활용
            </form>

            <form
              id="downloadFilteredDevProgressForm"
              action="catdownloadFiltered"
              method="get"
              id="downloadFilteredForm"
              class="flex-box align-center"
            >
              <!-- 숨겨진 input 필드 -->
              <input type="hidden" id="parentCodeForDownload" name="parentCode" />
              <input type="hidden" id="codeForDownload" name="code" />
              <input type="hidden" id="codeNameForDownload" name="codeName" />

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
              <input type="text" id="developerForFilter" name="developer" placeholder="이름" readonly />
              <button type="button" id="developerSearchButton">
                <img src="../../resources/images/search_icon.png" alt="개발자 검색" />
              </button>
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

<!-- 
    <form id="searchForm" method="get" action="/devProgress">
	    <div class="filter-row">
	        <div class="form-group">
	            <label for="majorCategory">업무 대분류</label>
	            <input type="text" id="majorCategory" name="majorCategory" value="${param.majorCategory}">
	        </div>
	        <div class="form-group">
	            <label for="subCategory">업무 중분류</label>
	            <input type="text" id="subCategory" name="subCategory" value="${param.subCategory}">
	        </div>
	        <div class="form-group">
	            <label for="programType">프로그램 구분</label>
	            <input type="text" id="programType" name="programType" value="${param.programType}">
	        </div>
	        <div class="form-group">
	            <label for="programName">프로그램명</label>
	            <input type="text" id="programName" name="programName" value="${param.programName}">
	        </div>
	    
	        <div class="form-group">
	            <label for="actualStartDate">실제 시작일</label>
	            <input type="date" id="actualStartDate" name="actualStartDate" value="${param.actualStartDate}">
	        </div>
	        <div class="form-group">
	            <label for="actualEndDate">실제 종료일</label>
	            <input type="date" id="actualEndDate" name="actualEndDate" value="${param.actualEndDate}">
	        </div>
	        <div class="form-group">
	            <label for="pl">PL</label>
	            <input type="text" id="pl" name="pl" value="${param.pl}">
	        </div>
	 
	    </div>
	    <button type="submit">조회</button>
	</form>
	 -->
