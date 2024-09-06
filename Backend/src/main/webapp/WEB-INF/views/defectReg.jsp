<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
  <head>
    <%@ include file="./common.jsp" %>
    <meta charset="UTF-8" />
    <link rel="stylesheet" type="text/css" href="../../resources/css/defectReg.css" />

    <title>TMS 결함진행 관리 - 결함 등록</title>
    <script type="module" src="../../resources/js/defectReg.js"></script>
  </head>

  <body>
    <header class="header">
      <!-- 공통 헤더 정보 동적으로 삽입-->
    </header>

    <main class="content">
      <h1 class="page-title">결함 등록</h1>
      <form id="defectRegisterForm">
        <fieldset>
          <div class="form-group-container">
            <div class="form-group-row">
              <!-- <div class="form-group">
                <label for="seq">결함번호</label>
                <input id="seq" name="seq" readonly disabled placeholder="결함번호" />
              </div> -->

              <div class="form-group">
                <label for="majorCategory">업무 대분류<span class="required-indicator">*</span></label>
                <select id="majorCategory" name="majorCategory" required>
                  <option value="" disabled selected>대분류 선택</option>
                </select>
              </div>

              <div class="form-group">
                <label for="subCategory">업무 중분류<span class="required-indicator">*</span></label>
                <select id="subCategory" name="subCategory" required>
                  <option value="" disabled selected>중분류 선택</option>
                </select>
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
                <select id="testStage" name="testStage" required>
                  <option value="" disabled selected>테스트 단계 선택</option>
                </select>
              </div>

              <div class="form-group">
                <label for="testId">테스트ID<span class="required-indicator">*</span></label>
                <input id="testId" name="testId" required placeholder="테스트ID 입력" />
              </div>
            </div>

            <div class="form-group-row">
              <div class="form-group">
                <label for="defectDiscoveryDate">결함발생일<span class="required-indicator">*</span></label>
                <input id="defectDiscoveryDate" name="defectDiscoveryDate" type="date" required />
              </div>

              <div class="form-group">
                <label for="defectType">결함유형<span class="required-indicator">*</span></label>
                <select id="defectType" name="defectType" required>
                  <option value="" disabled selected>결함유형 선택</option>
                </select>
              </div>

              <div class="form-group">
                <label for="defectSeverity">결함심각도<span class="required-indicator">*</span></label>
                <select id="defectSeverity" name="defectSeverity" required>
                  <option value="" disabled selected>결함심각도 선택</option>
                </select>
              </div>

              <div class="form-group">
                <label for="defectRegistrar">결함등록자<span class="required-indicator">*</span></label>
                <input id="defectRegistrar" name="defectRegistrar" required readonly />
              </div>
            </div>

            <div class="form-group">
              <label for="defectDescription">결함내용<span class="required-indicator">*</span></label>
              <textarea id="defectDescription" name="defectDescription" required class="hidden-scroll"></textarea>
            </div>

            <div class="form-group">
              <label for="defectFileInput">첨부파일</label>
              <div class="file-box">
                <button type="button" class="file-button" id="defectFileSelectButton">파일 선택</button>

                <div class="file-preview-container">
                  <input type="file" id="defectFileInput" class="hidden" name="defectAttachments" multiple />
                  <div id="defectFileOutput" class="file-preview custom-scroll"></div>
                </div>
              </div>
            </div>

            <div class="form-group-row">
              <div class="form-group">
                <label for="programId">프로그램ID<span class="required-indicator">*</span></label>
                <input id="programId" name="programId" required placeholder="프로그램ID 입력" />
              </div>

              <div class="form-group">
                <label for="programName">프로그램명<span class="required-indicator">*</span></label>
                <input id="programName" name="programName" required placeholder="프로그램명 입력" />
              </div>

              <div class="form-group">
                <label for="programType">프로그램 구분<span class="required-indicator">*</span></label>
                <select id="programType" name="programType" required>
                  <option value="" disabled selected>프로그램 구분 선택</option>
                </select>
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
                <input id="defectHandler" name="defectHandler" required placeholder="이름" />
              </div>

              <div class="form-group">
                <label for="defectScheduledDate">조치예정일</label>
                <input id="defectScheduledDate" name="defectScheduledDate" type="date" />
              </div>

              <div class="form-group">
                <label for="defectCompletionDate">조치완료일</label>
                <input id="defectCompletionDate" name="defectCompletionDate" type="date" />
              </div>
            </div>

            <div class="form-group">
              <label for="defectResolutionDetails">조치내역</label>
              <textarea id="defectResolutionDetails" name="defectResolutionDetails" class="hidden-scroll"></textarea>
            </div>

            <div class="form-group">
              <label for="defectFixFileInput">첨부파일</label>
              <div class="file-box">
                <button type="button" class="file-button" id="defectFixFileSelectButton">파일 선택</button>

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
                <input id="pl" name="pl" required placeholder="이름" />
              </div>

              <div class="form-group">
                <label for="plConfirmDate">PL 확인일</label>
                <input id="plConfirmDate" name="plConfirmDate" type="date" />
              </div>

              <div class="form-group">
                <label for="plDefectJudgeClassAccepted">PL 결함 판단구분</label>
                <div class="radio-group">
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
              <textarea id="plComments" name="plComments" class="hidden-scroll"></textarea>
            </div>
          </div>
        </fieldset>

        <fieldset>
          <legend>▣ 결함 등록자 재테스트 결과</legend>
          <div class="form-group-container">
            <div class="form-group-row">
              <div class="form-group">
                <label for="defectRegConfirmDate">등록자 확인일</label>
                <input id="defectRegConfirmDate" name="defectRegConfirmDate" type="date" />
              </div>
            </div>

            <div class="form-group">
              <label for="defectRegistrarComment">등록자 의견</label>
              <textarea id="defectRegistrarComment" name="defectRegistrarComment" class="hidden-scroll"></textarea>
            </div>

            <p class="notice-info">
              ※ [ 유의사항 ] 결함조치결과 재테스트 후 이상이 없는 경우 “등록자 확인일” 입력 - 재결함 발생한 경우
              결함내용과 첨부파일에 추가
            </p>
          </div>
        </fieldset>

        <fieldset>
          <legend>▣ 결함 처리상태</legend>
          <div class="form-group-container">
            <div class="form-group-row">
              <div class="form-group">
                <label for="defectStatus">처리상태</label>
                <select id="defectStatus" name="defectStatus">
                  <option value="" selected>처리상태 선택</option>
                </select>
              </div>

              <div class="form-group">
                <label for="originalDefectNumber">기발생 결함번호</label>
                <input id="originalDefectNumber" name="originalDefectNumber" readonly />
              </div>
            </div>

            <p class="notice-info">※ [ 유의사항 ] 재결함이 발생한 경우 처리상태를 "재결함"으로 선택해 주세요.</p>
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
