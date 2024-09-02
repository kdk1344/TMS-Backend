import {
  tmsFetch,
  openModal,
  closeModal,
  setupModalEventListeners,
  renderTMSHeader,
  showSpinner,
  hideSpinner,
  setupPagination,
} from "./common.js";

let currentPage = 1;

// DOM 요소들
const uploadCommonCodeFileInput = document.getElementById("uploadFileInput");
const uploadCommonCodeFileButton = document.getElementById("uploadFileButton");

const commonCodeRegisterForm = document.getElementById("commonCodeRegisterForm");
const commonCodeEditForm = document.getElementById("commonCodeEditForm");
const commonCodeFilterForm = document.getElementById("commonCodeFilterForm");

const parentCodeSelect = document.getElementById("parentCodeForFilter");

const commonCodeTableBody = document.getElementById("commonCodeTableBody");
const selectAllCommonCodeCheckbox = document.getElementById("selectAllCommonCodeCheckbox");

const openCommonCodeRegisterModalButton = document.getElementById("openCommonCodeRegisterModalButton");
const openCommonCodeFileDownloadModalButton = document.getElementById("openCommonCodeFileDownloadModalButton");
const closeCommonCodeFileDownloadModalButton = document.getElementById("closeCommonCodeFileDownloadModalButton");
const closeCommonCodeRegisterModalButton = document.getElementById("closeCommonCodeRegisterModalButton");
const closeCommonCodeEditModalButton = document.getElementById("closeCommonCodeEditModalButton");
const deleteCommonCodeButton = document.getElementById("deleteCommonCodeButton");

// 문서 로드 시 초기화
document.addEventListener("DOMContentLoaded", init);

// 모달 아이디
const MODAL_ID = {
  COMMON_CODE_REGISTER: "commonCodeRegisterModal",
  COMMON_CODE_EDIT: "commonCodeEditModal",
  COMMON_CODE_FILE_UPLOAD: "fileUploadModal",
  COMMON_CODE_FILE_DOWNLOAD: "commonCodeFileDownloadModal",
};

// 초기화 함수
function init() {
  renderTMSHeader();
  setupEventListeners();
  loadInitialCommonCodes();
}

// 이벤트 핸들러 설정
function setupEventListeners() {
  if (parentCodeSelect) {
    parentCodeSelect.addEventListener("change", () => {
      const selectedParentCode = parentCodeSelect.value;

      initializeChildCodes(selectedParentCode);
    });
  }

  // 공통코드 테이블 클릭 이벤트 핸들러
  if (commonCodeTableBody) {
    // 공통코드 테이블에서 클릭된 행의 데이터 로드 (이벤트 위임)
    commonCodeTableBody.addEventListener("click", (event) => {
      const clickedElement = event.target;
      if (clickedElement.type === "checkbox") {
        return;
      }

      const row = event.target.closest("tr");

      if (row) {
        loadCommonCodeDataFromRow(row);
      }
    });
  }

  // 공통코드 등록 폼 제출 이벤트 핸들러
  if (commonCodeRegisterForm) {
    commonCodeRegisterForm.addEventListener("submit", registerCommonCode);
  }

  // 공통코드 수정 폼 제출 이벤트 핸들러
  if (commonCodeEditForm) {
    commonCodeEditForm.addEventListener("submit", editCommonCode);
  }

  // 공통코드 필터 폼 제출 및 리셋 이벤트 핸들러
  if (commonCodeFilterForm) {
    commonCodeFilterForm.addEventListener("submit", submitCommonCodeFilter);
    commonCodeFilterForm.addEventListener("reset", resetCommonCodeFilter);
  }

  // 공통코드 삭제 버튼 클릭 이벤트 핸들러
  if (deleteCommonCodeButton) {
    deleteCommonCodeButton.addEventListener("click", deleteCommonCode);
  }

  // 전체 선택 체크박스 클릭 이벤트 핸들러
  if (selectAllCommonCodeCheckbox) {
    selectAllCommonCodeCheckbox.addEventListener("click", toggleAllCheckboxes);
  }

  // 파일 업로드
  if (uploadCommonCodeFileButton) {
    uploadCommonCodeFileButton.addEventListener("click", uploadCommonCodeFile);
  }

  if (openFileUploadModalButton) {
    openFileUploadModalButton.addEventListener("click", () => openModal(MODAL_ID.COMMON_CODE_FILE_UPLOAD));
  }

  if (closeFileUploadModalButton) {
    closeFileUploadModalButton.addEventListener("click", () => {
      closeModal(MODAL_ID.COMMON_CODE_FILE_UPLOAD);
    });
  }

  // 모달 열기 및 닫기 버튼 이벤트 핸들러
  if (
    openCommonCodeRegisterModalButton &&
    closeCommonCodeRegisterModalButton &&
    openCommonCodeFileDownloadModalButton &&
    closeCommonCodeEditModalButton &&
    closeCommonCodeFileDownloadModalButton
  ) {
    openCommonCodeRegisterModalButton.addEventListener("click", () => {
      initializeParentCodes("parentCodeForRegister");
      openModal(MODAL_ID.COMMON_CODE_REGISTER);
    });
    closeCommonCodeRegisterModalButton.addEventListener("click", () => closeModal(MODAL_ID.COMMON_CODE_REGISTER));

    openCommonCodeFileDownloadModalButton.addEventListener("click", () => {
      copyFilterValuesToDownloadForm(); // 공통코드 필터링 값 복사
      openModal(MODAL_ID.COMMON_CODE_FILE_DOWNLOAD); // 모달 열기
    });

    closeCommonCodeFileDownloadModalButton.addEventListener("click", () =>
      closeModal(MODAL_ID.COMMON_CODE_FILE_DOWNLOAD)
    );

    closeCommonCodeEditModalButton.addEventListener("click", () => closeModal(MODAL_ID.COMMON_CODE_EDIT));
  }

  // 모달 외부 클릭 시 닫기 버튼 이벤트 핸들러
  setupModalEventListeners(Object.values(MODAL_ID));
}

// 상위코드 목록 조회
async function getCCParentCodes() {
  try {
    const { parentCodes = [] } = await tmsFetch("/ccparentCode");

    return { parentCodes };
  } catch (error) {
    console.error("Error fetching CC Parent Codes", error.message);
  }
}

// 코드 목록 조회
async function getCCCodes(parentCode = "") {
  try {
    if (parentCode === "") return [];

    const query = new URLSearchParams({ parentCode }).toString();
    const { CCCodes = [] } = await tmsFetch(`/cccode?${query}`);

    return { codes: CCCodes };
  } catch (error) {
    console.error("Error fetching CC Codes", error.message);
  }
}

// 상위코드 목록을 가져와 select 요소에 옵션을 설정하는 함수
async function initializeParentCodes(selectElementId) {
  const { parentCodes } = await getCCParentCodes();
  const parentCodeSelect = document.getElementById(selectElementId);

  // 기존 옵션을 모두 삭제하고, 기본값이 되는 옵션은 따로 저장
  const defaultOption = parentCodeSelect.querySelector("option[selected]");
  parentCodeSelect.innerHTML = ""; // 기존 옵션들 삭제

  // 기본값 옵션을 다시 추가
  if (defaultOption) {
    parentCodeSelect.appendChild(defaultOption);
  }

  // 새 옵션 생성 및 추가
  parentCodes.forEach((parentCode) => {
    const option = document.createElement("option");

    option.value = parentCode.code;
    option.textContent = parentCode.codeName;
    parentCodeSelect.appendChild(option);
  });
}

// 상위코드 선택 시, 코드 목록을 가져와 select 요소에 옵션을 설정하는 함수
async function initializeChildCodes(selectedParentCode) {
  const codeSelect = document.getElementById("codeForFilter");

  // 기존 옵션 초기화하고 "전체" 옵션은 남겨두기
  codeSelect.innerHTML = '<option value="">전체</option>';

  if (selectedParentCode === "") return; // 상위코드 '전체'를 선택한 경우

  const { codes } = await getCCCodes(selectedParentCode);

  // 새 옵션 생성 및 추가
  codes.forEach((code) => {
    const option = document.createElement("option");

    option.value = code.code;
    option.textContent = code.codeName;
    codeSelect.appendChild(option);
  });
}

/**
 * 공통코드 데이터를 API에서 비동기적으로 가져옵니다.
 * 이 함수는 선택적 필터와 페이지 번호를 사용하여 공통코드 목록을 가져오고 표시합니다.
 *
 * @param {Object} params - 공통코드 데이터를 가져오기 위한 필터 및 페이지 정보.
 * @param {number} [params.page=1] - 가져올 페이지 번호 (기본값: 1).
 * @param {string} [params.commonCodeName=""] - 공통코드 이름으로 필터링할 문자열 (기본값: 빈 문자열).
 * @param {string} [params.authorityName=""] - 권한 이름으로 필터링할 문자열 (기본값: 빈 문자열).
 *
 * @throws {Error} API 요청 실패 시 오류를 발생시킵니다.
 *
 * @returns {Promise<void>} 이 함수는 결과를 반환하지 않습니다. 성공적으로 데이터를 가져온 후,
 *                          `displayCommonCodes` 함수를 호출하여 공통코드 목록을 페이지에 표시합니다.
 */
async function getCommonCodes({ page = 1, parentCode = "", code = "", codeName = "" } = {}) {
  try {
    const query = new URLSearchParams({ page, parentCode, code, codeName }).toString();
    const { commonCodes, totalPages } = await tmsFetch(`/commonCode?${query}`);

    return { commonCodes, totalPages };
  } catch (error) {
    console.error("Error fetching commonCodes:", error);
  }
}

// 공통코드 목록 테이블 렌더링
async function renderCommonCodes({ page = 1, parentCode = "", code = "", codeName = "" } = {}) {
  const { commonCodes, totalPages } = await getCommonCodes({ page, parentCode, code, codeName });

  if (commonCodeTableBody) {
    commonCodeTableBody.innerHTML = "";

    commonCodes.forEach((commonCode) => {
      const row = document.createElement("tr");

      row.innerHTML = `
        <td><input class="common-code-id-input" type="checkbox" name="commonCode" value="${commonCode.seq}"></td>
        <td class="parent-code">${commonCode.parentCode}</td>
        <td class="parent-code-name">${commonCode.parentCodeName === null ? "코드그룹" : commonCode.parentCodeName}</td>
        <td class="code">${commonCode.code}</td>
        <td class="code-name">${commonCode.codeName}</td>
      `;
      commonCodeTableBody.appendChild(row);
    });

    setupPagination({ paginationElementId: "commonCodePagination", totalPages, currentPage, changePage });
  }
}

// 페이지 변경
function changePage(page) {
  currentPage = page;

  const parentCode = document.getElementById("parentCodeForFilter").value;
  const code = document.getElementById("codeForFilter").value;
  const codeName = document.getElementById("codeNameForFilter").value.trim();

  renderCommonCodes({ page: currentPage, parentCode, code, codeName });
}

// 공통코드 필터링
function submitCommonCodeFilter(event) {
  event.preventDefault(); // 폼 제출 기본 동작 방지

  // 페이지를 1로 초기화하고 renderCommonCodes 호출
  currentPage = 1;

  const parentCode = document.getElementById("parentCodeForFilter").value;
  const code = document.getElementById("codeForFilter").value;
  const codeName = document.getElementById("codeNameForFilter").value.trim();

  renderCommonCodes({ page: currentPage, parentCode, code, codeName });
}

// 공통코드 필터 리셋
function resetCommonCodeFilter() {
  this.reset(); // 폼 초기화
}

// HTML 요소에서 공통코드 정보를 가져와서 공통코드 수정 폼에 미리 채우기
async function loadCommonCodeDataFromRow(row) {
  const commonCodeID = row.querySelector(".common-code-id-input").value;
  const code = row.querySelector(".code").textContent.trim();
  const codeName = row.querySelector(".code-name").textContent.trim();
  const parentCode = row.querySelector(".parent-code").textContent.trim();
  const parentCodeName = row.querySelector(".parent-code-name").textContent.trim();

  // 폼에 id값 넣기
  commonCodeEditForm.setAttribute("data-id", commonCodeID);

  // 폼에 공통코드 정보 입력
  document.getElementById("parentCodeForEdit").textContent = parentCode;
  document.getElementById("parentCodeNameForEdit").textContent = parentCodeName;
  document.getElementById("codeForEdit").textContent = code;
  document.getElementById("codeNameForEdit").value = codeName;

  // 모달 열기
  openModal(MODAL_ID.COMMON_CODE_EDIT);
}

// 공통코드 등록
async function registerCommonCode(event) {
  event.preventDefault(); // 폼 제출 기본 동작 방지

  const confirmed = confirm("공통코드를 등록하시겠습니까?");

  if (!confirmed) return;

  const formData = new FormData(event.target);

  // FormData 객체를 JSON 객체로 변환
  const formDataObj = Object.fromEntries(formData.entries());

  try {
    showSpinner();

    const { commonCode, status } = await tmsFetch("/ccwrite", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(formDataObj),
    });

    const success = status === "success";

    if (success) {
      alert(`공통코드(코드명: ${commonCode.codeName}) 등록이 완료되었습니다.`);
      event.target.reset(); // 폼 초기화
      closeModal(MODAL_ID.COMMON_CODE_REGISTER); // 모달 닫기
      window.location.reload();
    }
  } catch (error) {
    alert(error.message + "\n다시 시도해주세요.");
  } finally {
    hideSpinner();
  }
}

// 공통코드 수정
async function editCommonCode(event) {
  event.preventDefault(); // 폼 제출 기본 동작 방지

  const formData = new FormData(event.target);

  // FormData 객체를 JSON 객체로 변환
  const formDataObj = Object.fromEntries(formData.entries());

  // 공통코드 id값 꺼내기
  const commonCodeID = commonCodeEditForm.getAttribute("data-id");
  formDataObj.seq = commonCodeID;

  try {
    showSpinner();
    const { commonCode, status } = await tmsFetch("/ccmodify", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(formDataObj),
    });

    const success = status === "success";

    if (success) {
      alert(`공통코드(코드명: ${commonCode.codeName}) 수정이 완료되었습니다.`);
      event.target.reset(); // 폼 초기화
      closeModal(MODAL_ID.COMMON_CODE_EDIT); // 모달 닫기
      location.reload(); // 페이지 새로고침
    }
  } catch (error) {
    alert(error.message + "\n다시 시도해주세요.");
  } finally {
    hideSpinner();
  }
}

// 체크박스 전체 선택/해제
function toggleAllCheckboxes() {
  const checkboxes = document.querySelectorAll('input[name="commonCode"]');
  checkboxes.forEach((checkbox) => (checkbox.checked = selectAllCommonCodeCheckbox.checked));
}

// 공통코드 삭제
async function deleteCommonCode() {
  try {
    const confirmed = confirm("공통코드를 삭제하시겠습니까?");

    if (!confirmed) return;

    const selectedCommonCodeIDs = Array.from(document.querySelectorAll('input[name="commonCode"]:checked')).map(
      (checkbox) => checkbox.value
    );

    if (selectedCommonCodeIDs.length === 0) {
      alert("삭제할 공통코드를 선택해주세요.");
      return;
    }

    showSpinner();

    const { status } = await tmsFetch("/deletecc", {
      method: "DELETE",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(selectedCommonCodeIDs),
    });

    const success = status === "success";

    if (success) {
      alert(`공통코드 삭제가 완료되었습니다.`);
      location.reload(); // 페이지 새로고침
    }
  } catch (error) {
    alert(error.message + "\n다시 시도해주세요.");
  } finally {
    hideSpinner();
  }
}

// 공통코드 파일 업로드
async function uploadCommonCodeFile() {
  if (uploadCommonCodeFileInput.files.length <= 0) return;

  const formData = new FormData();

  // key 이름의 경우 서버와 협의
  formData.append("file", uploadCommonCodeFileInput.files[0]);

  try {
    showSpinner();

    const response = await tmsFetch("/ccupload", {
      method: "POST",
      body: formData,
    });

    const success = response ? response.status === "success" : false;

    if (success) {
      alert("파일 업로드가 완료되었습니다.");
      location.reload(); // 페이지 새로고침
    }
  } catch (error) {
    alert(error.message + "\n다시 시도해주세요.");
  } finally {
    hideSpinner();
  }
}

// 공통코드 필터링 폼의 값을 다운로드 폼으로 복사하는 함수
function copyFilterValuesToDownloadForm() {
  // 공통코드 조회 필터링 폼의 값을 가져옴
  const parentCode = document.getElementById("parentCodeForFilter").value;
  const code = document.getElementById("codeForFilter").value;
  const codeName = document.getElementById("codeNameForFilter").value;

  // 숨겨진 다운로드 폼의 input 필드에 값을 설정
  document.getElementById("parentCodeForDownload").value = parentCode;
  document.getElementById("codeForDownload").value = code;
  document.getElementById("codeNameForDownload").value = codeName;
}

// 초기 공통코드 목록 로드
function loadInitialCommonCodes() {
  initializeParentCodes("parentCodeForFilter"); // 필터링 폼 select 요소에 상위코드 render
  renderCommonCodes(); // 공통코드 목록 render
}
