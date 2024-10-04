<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
  <head>
    <%@ include file="./common.jsp" %>
    <meta charset="UTF-8" />
    <link rel="stylesheet" type="text/css" href="../../resources/css/defectEdit.css" />

    <title>TMS 결함진행 관리 - 결함 수정</title>
    <script type="module" src="../../resources/js/defectEdit.js"></script>
  </head>

  <body>
    <header class="header">
      <!-- 공통 헤더 정보 동적으로 삽입-->
    </header>

    <main class="content">
      <h1 class="page-title">결함 수정</h1>

      <!-- 프로그램 검색 모달 -->
      <div id="programSearchModal" class="modal">
        <div class="modal-content">
          <div class="flex-box justify-between">
            <h2>프로그램 조회</h2>
            <button type="button" id="closeProgramSearchModalButton" class="modal-close-button">
              <img src="../../resources/images/close_icon.png" alt="닫기" />
            </button>
          </div>

          <!-- 프로그램 필터링 -->
          <form id="programFilterForm">
            <div class="form-group-row">
              <div class="form-group">
                <label for="programTypeForPrgoram">프로그램 구분</label>
                <select id="programTypeForPrgoram" name="programType">
                  <option value="" selected>전체</option>
                </select>
              </div>

              <div class="form-group">
                <label for="developerForPrgoram">개발자</label>
                <input id="developerForPrgoram" name="developer" />
              </div>

              <div class="form-group">
                <label for="programIdForPrgoram">프로그램ID</label>
                <input id="programIdForPrgoram" name="programId" />
              </div>

              <div class="form-group">
                <label for="programNameForPrgoram">프로그램명</label>
                <input id="programNameForPrgoram" name="programName" />
              </div>
            </div>

            <button>조회</button>
          </form>
          <div id="programTableWrapper" class="hidden-scroll">
            <table id="programTable">
              <thead>
                <tr>
                  <th>번호</th>
                  <th>프로그램 구분</th>
                  <th>프로그램ID</th>
                  <th>프로그램명</th>
                  <th>상태</th>
                  <th>개발자</th>
                  <th>PL</th>
                </tr>
              </thead>
              <tbody id="programTableBody" class="hidden-scroll"></tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- 기발생 결함번호 검색 모달 -->
      <div id="defectNumberSearchModal" class="modal">
        <div class="modal-content">
          <div class="flex-box justify-between">
            <h2>기발생 결함번호 조회</h2>
            <button type="button" id="closeDefectNumberSearchModalButton" class="modal-close-button">
              <img src="../../resources/images/close_icon.png" alt="닫기" />
            </button>
          </div>
          <dl class="definition-container">
            <div class="definition-item">
              <dt>프로그램ID:</dt>
              <dd id="programIdBox"></dd>
            </div>

            <div class="definition-item">
              <dt>프로그램명:</dt>
              <dd id="programNameBox"></dd>
            </div>
          </dl>
          <div id="defectNumberTableWrapper" class="hidden-scroll">
            <table id="defectNumberTable">
              <thead>
                <th class="seq">결함번호</th>
                <th>프로그램ID</th>
                <th>프로그램명</th>
                <th>결함등록자</th>
                <th>결함심각도</th>
                <th>결함내용</th>
              </thead>
              <tbody id="defectNumberTableBody"></tbody>
            </table>
          </div>
        </div>
      </div>

      <form id="defectEditForm">
        <fieldset>
          <div class="form-group-container">
            <div class="form-group-row">
              <div class="form-group">
                <label for="seq">결함번호</label>
                <input id="seq" name="seq" readonly disabled placeholder="결함번호" />
              </div>

              <div class="form-group">
                <label for="majorCategory">업무 대분류<span class="required-indicator">*</span></label>
                <select id="majorCategory" name="majorCategory" required class="readonly"></select>
              </div>

              <div class="form-group">
                <label for="subCategory">업무 중분류<span class="required-indicator">*</span></label>
                <select id="subCategory" name="subCategory" required class="readonly"></select>
              </div>
            </div>
          </div>
        </fieldset>

        <fieldset>
          <legend>▣ 결함 기본정보</legend>
          <div class="form-group-container">
            <div class="form-group-row">
              <div class="form-group">
                <label for="testStage">테스트 단계<span class="required-indicator">*</span></label>
                <select id="testStage" name="testStage" required class="readonly"></select>
              </div>

              <div class="form-group">
                <label for="testId">테스트ID<span class="required-indicator">*</span></label>
                <input id="testId" name="testId" required placeholder="UT-프로그램ID" readonly />
              </div>
            </div>

            <div class="form-group-row">
              <div class="form-group">
                <label for="defectDiscoveryDate">결함발생일<span class="required-indicator">*</span></label>
                <input id="defectDiscoveryDate" name="defectDiscoveryDate" type="date" required readonly />
              </div>

              <div class="form-group">
                <label for="defectType">결함유형<span class="required-indicator">*</span></label>
                <select id="defectType" name="defectType" required class="readonly"></select>
              </div>

              <div class="form-group">
                <label for="defectSeverity">결함심각도<span class="required-indicator">*</span></label>
                <select id="defectSeverity" name="defectSeverity" required class="readonly"></select>
              </div>

              <div class="form-group">
                <label for="defectRegistrar">결함등록자<span class="required-indicator">*</span></label>
                <input id="defectRegistrar" name="defectRegistrar" required readonly />
              </div>
            </div>

            <div class="form-group">
              <label for="defectDescription">결함내용<span class="required-indicator">*</span></label>
              <textarea
                id="defectDescription"
                name="defectDescription"
                required
                class="hidden-scroll"
                readonly
              ></textarea>
            </div>

            <div class="form-group">
              <label for="defectFileInput">첨부파일</label>
              <div class="file-box">
                <button type="button" class="file-button" id="defectFileSelectButton" hidden>파일 선택</button>

                <div class="file-preview-container">
                  <input type="file" id="defectFileInput" class="hidden" name="defectAttachments" multiple />
                  <div id="defectFileOutput" class="file-preview custom-scroll"></div>
                </div>
              </div>
            </div>

            <div class="form-group-row">
              <div class="form-group">
                <label for="programId">프로그램ID<span class="required-indicator">*</span></label>
                <div class="search-input-wrapper">
                  <input id="programId" name="programId" required readonly />

                  <button type="button" class="search-button" id="programSearchButton" disabled>
                    <img src="../../resources/images/search_icon.png" alt="프로그램 검색" />
                  </button>
                </div>
              </div>

              <div class="form-group">
                <label for="programName">프로그램명<span class="required-indicator">*</span></label>
                <input id="programName" name="programName" required readonly />
              </div>

              <div class="form-group">
                <label for="programType">프로그램 구분<span class="required-indicator">*</span></label>
                <input id="programType" name="programType" required readonly />
              </div>
            </div>
          </div>
        </fieldset>

        <fieldset>
          <legend>▣ 개발자 조치결과</legend>
          <div class="form-group-container">
            <div class="form-group-row">
              <div class="form-group">
                <label for="defectHandler">조치담당자<span class="required-indicator">*</span></label>
                <input id="defectHandler" name="defectHandler" required placeholder="개발자 이름" readonly />
              </div>

              <div class="form-group">
                <label for="defectScheduledDate">조치예정일</label>
                <input id="defectScheduledDate" name="defectScheduledDate" type="date" readonly />
              </div>

              <div class="form-group">
                <label for="defectCompletionDate">조치완료일</label>
                <input id="defectCompletionDate" name="defectCompletionDate" type="date" readonly />
              </div>
            </div>

            <div class="form-group">
              <label for="defectResolutionDetails">조치내역</label>
              <textarea
                id="defectResolutionDetails"
                name="defectResolutionDetails"
                class="hidden-scroll"
                readonly
              ></textarea>
            </div>

            <div class="form-group">
              <label for="defectFixFileInput">첨부파일</label>
              <div class="file-box">
                <button type="button" class="file-button" id="defectFixFileSelectButton" hidden>파일 선택</button>

                <div class="file-preview-container">
                  <input type="file" id="defectFixFileInput" class="hidden" name="defectFixAttachments" multiple />
                  <div id="defectFixFileOutput" class="file-preview custom-scroll"></div>
                </div>
              </div>
            </div>
          </div>
        </fieldset>

        <fieldset>
          <legend>▣ PL 확인결과</legend>
          <div class="form-group-container">
            <div class="form-group-row">
              <div class="form-group">
                <label for="pl">PL<span class="required-indicator">*</span></label>
                <input id="pl" name="pl" required placeholder="이름" readonly />
              </div>

              <div class="form-group">
                <label for="plConfirmDate">PL 확인일</label>
                <input id="plConfirmDate" name="plConfirmDate" type="date" readonly />
              </div>

              <div class="form-group">
                <label for="plDefectJudgeClassAccepted">PL 결함 판단구분</label>
                <div id="plDefectJudgeClassRadioButtonGroup" class="radio-group readonly">
                  <label>
                    <input
                      type="radio"
                      id="plDefectJudgeClassAccepted"
                      name="plDefectJudgeClass"
                      value="결함수용"
                      checked
                    />
                    결함수용
                  </label>
                  <label>
                    <input type="radio" id="plDefectJudgeClassRejected" name="plDefectJudgeClass" value="결함아님" />
                    결함아님
                  </label>
                </div>
              </div>
            </div>

            <div class="form-group">
              <label for="plComments">PL의견</label>
              <textarea
                id="plComments"
                name="plComments"
                class="hidden-scroll"
                placeholder="'결함아님'인 경우 필수로 입력해 주세요."
                readonly
              ></textarea>
            </div>
          </div>
        </fieldset>

        <fieldset>
          <legend>▣ 결함 등록자 재테스트 결과</legend>
          <div class="form-group-container">
            <div class="form-group-row">
              <div class="form-group">
                <label for="defectRegConfirmDate">등록자 확인일</label>
                <input id="defectRegConfirmDate" name="defectRegConfirmDate" type="date" readonly />
              </div>
            </div>

            <div class="form-group">
              <label for="defectRegistrarComment">등록자 의견</label>
              <textarea
                id="defectRegistrarComment"
                name="defectRegistrarComment"
                class="hidden-scroll"
                readonly
              ></textarea>
            </div>

            <p id="defectRegistrarInfo" class="notice-info"></p>
          </div>
        </fieldset>

        <fieldset>
          <legend>▣ 결함 처리상태</legend>
          <div class="form-group-container">
            <div class="form-group-row">
              <div class="form-group">
                <label for="defectStatus">처리상태</label>
                <input id="defectStatus" name="defectStatus" readonly />
              </div>

              <div class="form-group">
                <label for="originalDefectNumber">기발생 결함번호</label>
                <div class="search-input-wrapper">
                  <input id="originalDefectNumber" name="originalDefectNumber" readonly />

                  <button type="button" class="search-button" id="defectNumberSearchButton" disabled>
                    <img src="../../resources/images/search_icon.png" alt="기발생 결함번호 검색" />
                  </button>
                </div>
              </div>
            </div>

            <p class="notice-info">
              ※ [ 유의사항 ] 다수 테스터가 동일한 결함을 등록하는 경우 "기발생 결함번호"를 선택해 주세요.
            </p>
          </div>
        </fieldset>

        <div class="button-container">
          <button id="goBackButton" class="cancel-button" type="button">취소</button>
          <button class="save-button">저장</button>
        </div>
      </form>
    </main>
  </body>
</html>
