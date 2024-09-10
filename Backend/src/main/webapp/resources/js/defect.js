import {
  tmsFetch,
  renderTMSHeader,
  setupPagination,
  convertDate,
  initializeSelect,
  getTestStageList,
  getMajorCategoryCodes,
  getSubCategoryCodes,
  getDefectSeverityList,
  getDefectStatusList,
  openModal,
  closeModal,
  setupModalEventListeners,
  showSpinner,
  hideSpinner,
} from "./common.js";

let currentPage = 1;

// DOM 요소들
const defectTableBody = document.getElementById("defectTableBody");

const defectFilterForm = document.getElementById("defectFilterForm");
const majorCategorySelect = document.getElementById("majorCategoryForFilter");

const goDefectRegisterPageButton = document.getElementById("goDefectRegisterPageButton");
const selectAllDefectCheckbox = document.getElementById("selectAllDefectCheckbox");
const deleteDefectButton = document.getElementById("deleteDefectButton");

const uploadDefectFileButton = document.getElementById("uploadDefectFileButton");
const uploadDefectFileInput = document.getElementById("uploadDefectFileInput");

const openDefectFileUploadModalButton = document.getElementById("openDefectFileUploadModalButton");
const closeDefectFileUploadModalButton = document.getElementById("closeDefectFileUploadModalButton");
const openDefectFileDownloadModalButton = document.getElementById("openDefectFileDownloadModalButton");
const closeDefectFileDownloadModalButton = document.getElementById("closeDefectFileDownloadModalButton");

// 문서 로드 시 초기화
document.addEventListener("DOMContentLoaded", init);

// 모달 아이디
const MODAL_ID = {
  DEFECT_EDIT: "defectEditModal",
  DEFECT_FILE_UPLOAD: "defectFileUploadModal",
  DEFECT_FILE_DOWNLOAD: "defectFileDownloadModal",
};

// 초기화 함수
function init() {
  renderTMSHeader();
  setupEventListeners();
  initializeFilterForm(); // 폼 메타 데이터 초기 로드
  renderDefect(); // 결함현황 목록 render
}

// 이벤트 핸들러 설정
function setupEventListeners() {
  // 결함현황 등록 페이지로 이동
  if (goDefectRegisterPageButton) {
    goDefectRegisterPageButton.addEventListener("click", () => {
      window.location.href = "/tms/defectReg";
    });
  }

  // 결함현황 목록 테이블 클릭 이벤트 핸들러
  if (defectTableBody) {
    defectTableBody.addEventListener("click", (event) => {
      const clickedElement = event.target;

      if (clickedElement.type === "checkbox") {
        return;
      }

      const row = event.target.closest("tr");

      if (row) {
        const checkbox = row.querySelector('input[type="checkbox"]'); // tr 내부의 첫 번째 체크박스 요소 선택
        const defectId = checkbox.value;
        window.location.href = `/tms/defectEdit?seq=${defectId}`;
      }
    });
  }

  // 결함현황 필터 폼 제출 및 리셋 이벤트 핸들러
  if (defectFilterForm) {
    defectFilterForm.addEventListener("submit", submitDefectFilter);
    defectFilterForm.addEventListener("reset", resetDefectFilter);
  }

  if (majorCategorySelect) {
    majorCategorySelect.addEventListener("change", () => initializeSubCategorySelect(majorCategorySelect.value));
  }

  // 전체 선택 체크박스 클릭 이벤트 핸들러
  if (selectAllDefectCheckbox) {
    selectAllDefectCheckbox.addEventListener("click", toggleAllCheckboxes);
  }

  // 결함현황 정보 삭제
  if (deleteDefectButton) {
    deleteDefectButton.addEventListener("click", deleteDefect);
  }

  // 엑셀 업로드 이벤트 핸들러
  if (openDefectFileUploadModalButton) {
    openDefectFileUploadModalButton.addEventListener("click", () => openModal(MODAL_ID.DEFECT_FILE_UPLOAD));
  }

  if (closeDefectFileUploadModalButton) {
    closeDefectFileUploadModalButton.addEventListener("click", () => closeModal(MODAL_ID.DEFECT_FILE_UPLOAD));
  }

  if (uploadDefectFileButton) {
    uploadDefectFileButton.addEventListener("click", uploadDefectFile);
  }

  // 엑셀 다운로드 이벤트 핸들러
  if (openDefectFileDownloadModalButton) {
    openDefectFileDownloadModalButton.addEventListener("click", () => {
      copyFilterValuesToDownloadForm(); // 필터링 값 복사
      openModal(MODAL_ID.DEFECT_FILE_DOWNLOAD);
    });
  }

  if (closeDefectFileDownloadModalButton) {
    closeDefectFileDownloadModalButton.addEventListener("click", () => closeModal(MODAL_ID.DEFECT_FILE_DOWNLOAD));
  }

  // 모달 외부 클릭 시 닫기 버튼 이벤트 핸들러
  setupModalEventListeners(Object.values(MODAL_ID));
}

// 결함현황 목록 테이블 렌더링
async function renderDefect(
  getDefectsProps = {
    page: 1,
    testStage: "",
    majorCategory: "",
    subCategory: "",
    defectSeverity: "",
    seq: "",
    defectStatus: "",
    defectRegistrar: "",
    defectHandler: "",
    pl: "",
  }
) {
  const { defects, totalPages } = await getDefects(getDefectsProps);

  if (defectTableBody) {
    defectTableBody.innerHTML = "";

    defects.forEach((defect) => {
      const {
        seq,
        subCategory,
        programId,
        programName,
        defectRegistrar,
        defectDiscoveryDate,
        defectSeverity,
        defectDescription,
        defectHandler,
        defectScheduledDate,
        defectCompletionDate,
        pl,
        plConfirmDate,
        defectRegConfirmDate,
        defectStatus,
      } = defect;

      const row = document.createElement("tr");

      row.innerHTML = `
        <td><input type="checkbox" name="defect" value="${seq}"></td>
        <td class="defect-number">${seq}</td>
        <td class="sub-category">${subCategory}</td>
        <td class="program-id">${programId}</td>
        <td class="program-name">${programName}</td>
        <td class="defect-registrar">${defectRegistrar}</td>
        <td class="defect-discovery-date">${convertDate(defectDiscoveryDate)}</td>
        <td class="defect-severity">${defectSeverity}</td>
        <td class="defect-description ellipsis">${defectDescription}</th>
        <td class="defect-handler">${defectHandler}</td>
        <td class="defect-scheduled-date">${convertDate(defectScheduledDate)}</td>
        <td class="defect-completion-date">${convertDate(defectCompletionDate)}</td>
        <td class="pl">${pl}</td>
        <td class="pl-confirm-date">${convertDate(plConfirmDate)}</td>
        <td class="defect-reg-confirm-date">${convertDate(defectRegConfirmDate)}</td>
        <td class="defect-status">${defectStatus}</td>
      `;
      defectTableBody.appendChild(row);
    });

    setupPagination({ paginationElementId: "defectPagination", totalPages, currentPage, changePage });
  }
}

// 결함현황 필터링
function submitDefectFilter(event) {
  event.preventDefault(); // 폼 제출 기본 동작 방지

  // 페이지를 1로 초기화하고 테이블 렌더링
  currentPage = 1;

  const getDefectsProps = getCurrentFilterValues();

  renderDefect(getDefectsProps);
}

// 결함현황 필터 리셋
function resetDefectFilter() {
  this.reset(); // 폼 초기화
}

async function getDefects(
  getDefectsProps = {
    page: 1,
    testStage,
    majorCategory,
    subCategory,
    defectSeverity,
    defectNumber,
    defectStatus,
    defectRegistrar: "",
    defectHandler: "",
    pl: "",
  }
) {
  try {
    const query = new URLSearchParams(getDefectsProps).toString();
    const { defects, totalPages } = await tmsFetch(`/defect?${query}`);

    return { defects, totalPages };
  } catch (error) {
    console.error(error.message, "결함현황 목록을 불러오지 못 했습니다.");
  }
}

// 페이지 변경
function changePage(page) {
  currentPage = page;

  const getDefectsProps = getCurrentFilterValues();

  renderDefect(getDefectsProps);
}

function getCurrentFilterValues() {
  let getDefectsProps = { page: currentPage };

  const defectRoleKey = document.getElementById("defectRoleKeySelect").value;
  const defectRoleValue = document.getElementById("defectRoleValueInput").value.trim();

  getDefectsProps[defectRoleKey] = defectRoleValue;

  const testStage = document.getElementById("testStageForFilter").value;
  const majorCategory = document.getElementById("majorCategoryForFilter").value;
  const subCategory = document.getElementById("subCategoryForFilter").value;
  const defectSeverity = document.getElementById("defectSeverityForFilter").value;
  const defectNumber = document.getElementById("defectNumberForFilter").value;
  const defectStatus = document.getElementById("defectStatusForFilter").value;

  getDefectsProps = {
    ...getDefectsProps,
    testStage,
    majorCategory,
    subCategory,
    defectSeverity,
    seq: defectNumber,
    defectStatus,
  };

  return getDefectsProps;
}

async function initializeFilterForm() {
  const [{ testStageList }, { majorCategoryCodes }, { defectSeverityList }, { defectStatusList }] = await Promise.all([
    getTestStageList(),
    getMajorCategoryCodes(),
    getDefectSeverityList(),
    getDefectStatusList(),
  ]);

  const SELECT_ID = {
    TEST_STAGE: "testStageForFilter",
    MAJOR_CATEGORY: "majorCategoryForFilter",
    DEFECT_SEVERITY: "defectSeverityForFilter",
    DEFECT_STATUS: "defectStatusForFilter",
  };

  const SELECT_DATA = {
    [SELECT_ID.TEST_STAGE]: testStageList,
    [SELECT_ID.MAJOR_CATEGORY]: majorCategoryCodes,
    [SELECT_ID.DEFECT_SEVERITY]: defectSeverityList,
    [SELECT_ID.DEFECT_STATUS]: defectStatusList,
  };

  Object.values(SELECT_ID).forEach((selectId) => initializeSelect(selectId, SELECT_DATA[selectId]));
}

async function initializeSubCategorySelect(selectedMajorCategoryCode) {
  const SUB_CATEGORY_SELECT_ID = "subCategoryForFilter";

  // 업무 대분류 '전체'를 선택한 경우
  if (selectedMajorCategoryCode === "") initializeSelect(SUB_CATEGORY_SELECT_ID, []);
  else {
    const { subCategoryCodes } = await getSubCategoryCodes(selectedMajorCategoryCode);

    initializeSelect(SUB_CATEGORY_SELECT_ID, subCategoryCodes);
  }
}

/** 결함현황 필터링 폼의 값을 다운로드 폼으로 복사하는 함수 */
function copyFilterValuesToDownloadForm() {
  // 조회 필터링 폼의 값을 가져옴
  const {
    testStage,
    majorCategory,
    subCategory,
    defectSeverity,
    seq,
    defectStatus,
    defectRegistrar = "",
    defectHandler = "",
    pl = "",
  } = getCurrentFilterValues();

  // 숨겨진 다운로드 폼의 input 필드에 값을 설정
  document.getElementById("testStageForDownload").value = testStage;
  document.getElementById("majorCategoryForDownload").value = majorCategory;
  document.getElementById("subCategoryForDownload").value = subCategory;
  document.getElementById("defectSeverityForDownload").value = defectSeverity;
  document.getElementById("defectRegistrarForDownload").value = defectRegistrar;
  document.getElementById("defectHandlerForDownload").value = defectHandler;
  document.getElementById("plForDownload").value = pl;
  document.getElementById("defectNumberForDownload").value = seq;
  document.getElementById("defectStatusForDownload").value = defectStatus;
}

// 체크박스 전체 선택/해제
function toggleAllCheckboxes() {
  const checkboxes = document.querySelectorAll('input[name="defect"]');
  checkboxes.forEach((checkbox) => (checkbox.checked = selectAllDefectCheckbox.checked));
}

// API 함수

// 결함현황 파일 업로드
async function uploadDefectFile() {
  if (uploadDefectFileInput.files.length <= 0) return;

  const formData = new FormData();

  // key 이름의 경우 서버와 협의
  formData.append("file", uploadDefectFileInput.files[0]);

  try {
    showSpinner();

    const response = await tmsFetch("/defectupload", {
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

// 결함현황 정보 삭제
async function deleteDefect() {
  try {
    const confirmed = confirm("결함 정보를 삭제하시겠습니까?");

    if (!confirmed) return;

    const selectedDefectIds = Array.from(document.querySelectorAll('input[name="defect"]:checked')).map(
      (checkbox) => checkbox.value
    );

    if (selectedDefectIds.length === 0) {
      alert("삭제할 결함 정보를 선택해주세요.");
      return;
    }

    showSpinner();

    const { status } = await tmsFetch("/deleteDefect", {
      method: "DELETE",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(selectedDefectIds),
    });

    const success = status === "success";

    if (success) {
      alert(`결함 정보 삭제가 완료되었습니다.`);
      location.reload(); // 페이지 새로고침
    }
  } catch (error) {
    alert(error.message + "\n다시 시도해주세요.");
  } finally {
    hideSpinner();
  }
}
