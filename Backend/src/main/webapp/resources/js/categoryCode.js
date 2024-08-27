import {
  tmsFetch,
  openModal,
  closeModal,
  closeModalOnClickOutside,
  renderTMSHeader,
  showSpinner,
  hideSpinner,
  setupPagination,
} from "./common.js";

let currentPage = 1;

// DOM 요소들
const uploadCategoryCodeFileInput = document.getElementById("uploadCategoryCodeFileInput");
const uploadCategoryCodeFileButton = document.getElementById("uploadCategoryCodeFileButton");

const categoryCodeRegisterForm = document.getElementById("categoryCodeRegisterForm");
const categoryCodeEditForm = document.getElementById("categoryCodeEditForm");
const categoryCodeFilterForm = document.getElementById("categoryCodeFilterForm");

const parentCodeSelect = document.getElementById("parentCodeForFilter");
const parentCodeForRegister = document.getElementById("parentCodeForRegister");
const mainCategoryForRegister = document.getElementById("mainCategoryForRegister");
const subCategoryForRegister = document.getElementById("subCategoryForRegister");

const categoryCodeTableBody = document.getElementById("categoryCodeTableBody");
const selectAllCategoryCodeCheckbox = document.getElementById("selectAllCategoryCodeCheckbox");

const openCategoryCodeRegisterModalButton = document.getElementById("openCategoryCodeRegisterModalButton");
const openCategoryCodeFileDownloadModalButton = document.getElementById("openCategoryCodeFileDownloadModalButton");
const closeCategoryCodeRegisterModalButton = document.getElementById("closeCategoryCodeRegisterModalButton");
const closeCategoryCodeEditModalButton = document.getElementById("closeCategoryCodeEditModalButton");
const closeCategoryCodeFileDownloadModalButton = document.getElementById("closeCategoryCodeFileDownloadModalButton");
const deleteCategoryCodeButton = document.getElementById("deleteCategoryCodeButton");

// 문서 로드 시 초기화
document.addEventListener("DOMContentLoaded", init);

// 모달 아이디
const MODAL_ID = {
  CATEGORY_CODE_REGISTER: "categoryCodeRegisterModal",
  CATEGORY_CODE_EDIT: "categoryCodeEditModal",
  CATEGORY_CODE_FILE_DOWNLOAD: "categoryCodeFileDownloadModal",
};

// 초기화 함수
function init() {
  renderTMSHeader();
  setupEventListeners();
  loadInitialCategoryCodes();
}

// 이벤트 핸들러 설정
function setupEventListeners() {
  if (parentCodeSelect) {
    parentCodeSelect.addEventListener("change", () => {
      const selectedParentCode = parentCodeSelect.value;

      initializeChildCodes(selectedParentCode);
    });
  }

  // 분류코드 파일 업로드 버튼 클릭 이벤트 핸들러
  if (uploadCategoryCodeFileButton) {
    uploadCategoryCodeFileButton.addEventListener("click", () => {
      if (uploadCategoryCodeFileInput) {
        uploadCategoryCodeFileInput.click(); // 파일 선택 창 열기
      } else {
        console.error("File input element not found.");
      }
    });
  }

  // 분류코드 파일 업로드 인풋 change 이벤트 핸들러
  if (uploadCategoryCodeFileInput) {
    uploadCategoryCodeFileInput.addEventListener("change", uploadCategoryCodeFile);
  }

  // 분류코드 테이블 클릭 이벤트 핸들러
  if (categoryCodeTableBody) {
    // 분류코드 테이블에서 클릭된 행의 데이터 로드 (이벤트 위임)
    categoryCodeTableBody.addEventListener("click", (event) => {
      const clickedElement = event.target;
      if (clickedElement.type === "checkbox") {
        return;
      }

      const row = event.target.closest("tr");

      if (row) {
        loadCategoryCodeDataFromRow(row);
      }
    });
  }

  // 분류코드 등록 폼 이벤트 핸들러
  if (categoryCodeRegisterForm) {
    categoryCodeRegisterForm.addEventListener("submit", registerCategoryCode);

    mainCategoryForRegister.addEventListener("change", () => {
      // 대분류 선택 시 대분류 목록 잠금
      if (mainCategoryForRegister.checked) {
        parentCodeForRegister.value = "";
        parentCodeForRegister.disabled = true;

        document.getElementById("partialCodeForRegister").textContent = "";
      }
    });

    subCategoryForRegister.addEventListener("change", () => {
      // 중분류 선택 시 대분류 목록 fetch
      if (subCategoryForRegister.checked) {
        parentCodeForRegister.disabled = false;
        initializeParentCodes("parentCodeForRegister");
      }
    });

    parentCodeForRegister.addEventListener("change", () => {
      // 대분류 선택 시 코드 앞2자리 render
      document.getElementById("partialCodeForRegister").textContent = parentCodeForRegister.value;
    });
  }

  // 분류코드 수정 폼 제출 이벤트 핸들러
  if (categoryCodeEditForm) {
    categoryCodeEditForm.addEventListener("submit", editCategoryCode);
  }

  // 분류코드 필터 폼 제출 및 리셋 이벤트 핸들러
  if (categoryCodeFilterForm) {
    categoryCodeFilterForm.addEventListener("submit", submitCategoryCodeFilter);
    categoryCodeFilterForm.addEventListener("reset", resetCategoryCodeFilter);
  }

  // 분류코드 삭제 버튼 클릭 이벤트 핸들러
  if (deleteCategoryCodeButton) {
    deleteCategoryCodeButton.addEventListener("click", deleteCategoryCode);
  }

  // 전체 선택 체크박스 클릭 이벤트 핸들러
  if (selectAllCategoryCodeCheckbox) {
    selectAllCategoryCodeCheckbox.addEventListener("click", toggleAllCheckboxes);
  }

  // 모달 열기 및 닫기 버튼 이벤트 핸들러
  if (
    openCategoryCodeRegisterModalButton &&
    closeCategoryCodeRegisterModalButton &&
    openCategoryCodeFileDownloadModalButton &&
    closeCategoryCodeEditModalButton &&
    closeCategoryCodeFileDownloadModalButton
  ) {
    openCategoryCodeRegisterModalButton.addEventListener("click", () => {
      openModal(MODAL_ID.CATEGORY_CODE_REGISTER);
    });
    closeCategoryCodeRegisterModalButton.addEventListener("click", () => closeModal(MODAL_ID.CATEGORY_CODE_REGISTER));

    openCategoryCodeFileDownloadModalButton.addEventListener("click", () => {
      copyFilterValuesToDownloadForm(); // 분류코드 필터링 값 복사
      openModal(MODAL_ID.CATEGORY_CODE_FILE_DOWNLOAD); // 모달 열기
    });

    closeCategoryCodeFileDownloadModalButton.addEventListener("click", () =>
      closeModal(MODAL_ID.CATEGORY_CODE_FILE_DOWNLOAD)
    );

    closeCategoryCodeEditModalButton.addEventListener("click", () => closeModal(MODAL_ID.CATEGORY_CODE_EDIT));
  }

  // 모달 외부 클릭 시 닫기 버튼 이벤트 핸들러
  setupModalEventListeners();
}

function setupModalEventListeners() {
  const modals = Object.values(MODAL_ID); // 모든 모달 ID 배열

  modals.forEach((modalId) => {
    // 모달 외부 클릭 시 닫기 설정
    window.addEventListener("click", (event) => closeModalOnClickOutside(event, modalId));
  });
}

// 대분류 목록 조회
async function getCTParentCodes() {
  try {
    const { parentCodes = [] } = await tmsFetch("/catparentCode");

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
    const { CTCodes = [] } = await tmsFetch(`/catcode?${query}`);

    return { codes: CTCodes };
  } catch (error) {
    console.error("Error fetching CC Codes", error.message);
  }
}

// 대분류 목록을 가져와 select 요소에 옵션을 설정하는 함수
async function initializeParentCodes(selectElementId) {
  const { parentCodes } = await getCTParentCodes();
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

// 대분류 선택 시, 코드 목록을 가져와 select 요소에 옵션을 설정하는 함수
async function initializeChildCodes(selectedParentCode) {
  const codeSelect = document.getElementById("codeForFilter");

  // 기존 옵션 초기화하고 "전체" 옵션은 남겨두기
  codeSelect.innerHTML = '<option value="">전체</option>';

  if (selectedParentCode === "") return; // 대분류 '전체'를 선택한 경우

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
 * 분류코드 데이터를 API에서 비동기적으로 가져옵니다.
 * 이 함수는 선택적 필터와 페이지 번호를 사용하여 분류코드 목록을 가져오고 표시합니다.
 *
 * @param {Object} params - 분류코드 데이터를 가져오기 위한 필터 및 페이지 정보.
 * @param {number} [params.page=1] - 가져올 페이지 번호 (기본값: 1).
 * @param {string} [params.categoryCodeName=""] - 분류코드 이름으로 필터링할 문자열 (기본값: 빈 문자열).
 * @param {string} [params.authorityName=""] - 권한 이름으로 필터링할 문자열 (기본값: 빈 문자열).
 *
 * @throws {Error} API 요청 실패 시 오류를 발생시킵니다.
 *
 * @returns {Promise<void>} 이 함수는 결과를 반환하지 않습니다. 성공적으로 데이터를 가져온 후,
 *                          `displayCategoryCodes` 함수를 호출하여 분류코드 목록을 페이지에 표시합니다.
 */
async function getCategoryCodes({ page = 1, parentCode = "", code = "", codeName = "" } = {}) {
  try {
    const query = new URLSearchParams({ page, parentCode, code, codeName }).toString();
    const { categoryCodes, totalPages } = await tmsFetch(`/categoryCode?${query}`);

    return { categoryCodes, totalPages };
  } catch (error) {
    console.error("Error fetching categoryCodes:", error);
  }
}

// 분류코드 목록 테이블 렌더링
async function renderCategoryCodes({ page = 1, parentCode = "", code = "", codeName = "" } = {}) {
  const { categoryCodes, totalPages } = await getCategoryCodes({ page, parentCode, code, codeName });

  if (categoryCodeTableBody) {
    categoryCodeTableBody.innerHTML = "";

    categoryCodes.forEach((categoryCode) => {
      const row = document.createElement("tr");

      row.innerHTML = `
        <td><input type="checkbox" name="categoryCode" value="${categoryCode.code}"></td>
        <td class="stage-type">${categoryCode.stageType}</td>
        <td class="code">${categoryCode.code}</td>
        <td class="code-name">${categoryCode.codeName}</td>
      `;
      categoryCodeTableBody.appendChild(row);
    });

    setupPagination({ paginationElementId: "categoryCodePagination", totalPages, currentPage, changePage });
  }
}

// 페이지 변경
function changePage(page) {
  currentPage = page;

  const parentCode = document.getElementById("parentCodeForFilter").value;
  const code = document.getElementById("codeForFilter").value;
  const codeName = document.getElementById("codeNameForFilter").value.trim();

  renderCategoryCodes({ page: currentPage, parentCode, code, codeName });
}

// 분류코드 필터링
function submitCategoryCodeFilter(event) {
  event.preventDefault(); // 폼 제출 기본 동작 방지

  // 페이지를 1로 초기화하고 renderCategoryCodes 호출
  currentPage = 1;

  const parentCode = document.getElementById("parentCodeForFilter").value;
  const code = document.getElementById("codeForFilter").value;
  const codeName = document.getElementById("codeNameForFilter").value.trim();

  renderCategoryCodes({ page: currentPage, parentCode, code, codeName });
}

// 분류코드 필터 리셋
function resetCategoryCodeFilter() {
  this.reset(); // 폼 초기화
}

// HTML 요소에서 분류코드 정보를 가져와서 분류코드 수정 폼에 미리 채우기
async function loadCategoryCodeDataFromRow(row) {
  const stageType = row.querySelector(".stage-type").textContent.trim();
  const code = row.querySelector(".code").textContent.trim();
  const codeName = row.querySelector(".code-name").textContent.trim();

  // 폼에 분류코드 정보 입력
  document.getElementById("stageTypeForEdit").textContent = stageType;
  document.getElementById("codeForEdit").textContent = code;
  document.getElementById("codeNameForEdit").value = codeName;

  // 모달 열기
  openModal(MODAL_ID.CATEGORY_CODE_EDIT);
}

// 분류코드 등록
async function registerCategoryCode(event) {
  event.preventDefault(); // 폼 제출 기본 동작 방지

  const confirmed = confirm("분류코드를 등록하시겠습니까?");

  if (!confirmed) return;

  const formData = new FormData(event.target);

  // FormData 객체를 JSON 객체로 변환
  const formDataObj = Object.fromEntries(formData.entries());

  try {
    showSpinner();

    const { categoryCode, status } = await tmsFetch("/catwrite", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(formDataObj),
    });

    const success = status === "success";

    if (success) {
      alert(`분류코드(코드명: ${categoryCode.codeName}) 등록이 완료되었습니다.`);
      event.target.reset(); // 폼 초기화
      closeModal(MODAL_ID.CATEGORY_CODE_REGISTER); // 모달 닫기
      window.location.reload();
    }
  } catch (error) {
    alert(error.message + "\n다시 시도해주세요.");
  } finally {
    hideSpinner();
  }
}

// 분류코드 수정
async function editCategoryCode(event) {
  event.preventDefault(); // 폼 제출 기본 동작 방지

  const formData = new FormData(event.target);

  // FormData 객체를 JSON 객체로 변환
  const formDataObj = Object.fromEntries(formData.entries());
  const code = document.getElementById("codeForEdit").textContent;
  const stageType = document.getElementById("stageTypeForEdit").textContent;

  formDataObj.code = code;
  formDataObj.stageType = stageType;

  try {
    showSpinner();
    const { categoryCode, status } = await tmsFetch("/catmodify", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(formDataObj),
    });

    const success = status === "success";

    if (success) {
      alert(`분류코드(코드명: ${categoryCode.codeName}) 수정이 완료되었습니다.`);
      event.target.reset(); // 폼 초기화
      closeModal(MODAL_ID.CATEGORY_CODE_EDIT); // 모달 닫기
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
  const checkboxes = document.querySelectorAll('input[name="categoryCode"]');
  checkboxes.forEach((checkbox) => (checkbox.checked = selectAllCategoryCodeCheckbox.checked));
}

// 분류코드 삭제
async function deleteCategoryCode() {
  try {
    const confirmed = confirm("분류코드를 삭제하시겠습니까?");

    if (!confirmed) return;

    const selectedCategoryCodeIDs = Array.from(document.querySelectorAll('input[name="categoryCode"]:checked')).map(
      (checkbox) => checkbox.value
    );

    if (selectedCategoryCodeIDs.length === 0) {
      alert("삭제할 분류코드를 선택해주세요.");
      return;
    }

    showSpinner();

    const { status } = await tmsFetch("/catdelete", {
      method: "DELETE",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(selectedCategoryCodeIDs),
    });

    const success = status === "success";

    if (success) {
      alert(`분류코드 삭제가 완료되었습니다.`);
      location.reload(); // 페이지 새로고침
    }
  } catch (error) {
    alert(error.message + "\n다시 시도해주세요.");
  } finally {
    hideSpinner();
  }
}

// 분류코드 파일 업로드
async function uploadCategoryCodeFile() {
  if (uploadCategoryCodeFileInput.files.length <= 0) return;

  const formData = new FormData();

  // key 이름의 경우 서버와 협의
  formData.append("file", uploadCategoryCodeFileInput.files[0]);

  try {
    showSpinner();

    const response = await tmsFetch("/catupload", {
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

// 분류코드 필터링 폼의 값을 다운로드 폼으로 복사하는 함수
function copyFilterValuesToDownloadForm() {
  // 분류코드 조회 필터링 폼의 값을 가져옴
  const parentCode = document.getElementById("parentCodeForFilter").value;
  const code = document.getElementById("codeForFilter").value;
  const codeName = document.getElementById("codeNameForFilter").value;

  // 숨겨진 다운로드 폼의 input 필드에 값을 설정
  document.getElementById("parentCodeForDownload").value = parentCode;
  document.getElementById("codeForDownload").value = code;
  document.getElementById("codeNameForDownload").value = codeName;
}

// 초기 분류코드 목록 로드
function loadInitialCategoryCodes() {
  initializeParentCodes("parentCodeForFilter"); // 필터링 폼 select 요소에 대분류 render
  renderCategoryCodes(); // 분류코드 목록 render
}
