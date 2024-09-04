import {
  tmsFetch,
  renderTMSHeader,
  setupPagination,
  convertDate,
  initializeSelect,
  getMajorCategoryCodes,
  getSubCategoryCodes,
  getProgramTypes,
  getProgramStatusList,
  getDevStatusList,
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
  DEFECT_REGISTER: "defectRegisterModal",
  DEFECT_EDIT: "defectEditModal",
  DEFECT_FILE_UPLOAD: "defectFileUploadModal",
  DEFECT_FILE_DOWNLOAD: "defectFileDownloadModal",
};

// 초기화 함수
function init() {
  renderTMSHeader();
  setupEventListeners();
  // initializeFilterForm(); // 폼 메타 데이터 초기 로드
  // renderDefect(); // 결함현황 목록 render
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
  getDefectProps = {
    page: 1,
    majorCategory: "",
    subCategory: "",
    programType: "",
    programId: "",
    programName: "",
    programStatus: "",
    developer: "",
    actualStartDate: "",
    actualEndDate: "",
    pl: "",
    thirdPartyTestMgr: "",
    itMgr: "",
    busiMgr: "",
  }
) {
  const { defectList, totalPages } = await getDefect(getDefectProps);

  if (defectTableBody) {
    defectTableBody.innerHTML = "";

    defectList.forEach((defect) => {
      const {
        seq,
        subCategory,
        programType,
        programId,
        programName,
        screenId,
        screenName,
        programStatus,
        developer,
        pl,
        plannedStartDate,
        plannedEndDate,
        actualStartDate,
        actualEndDate,
        plTestCmpDate,
        itMgr,
        busiMgr,
        devStatus,
      } = defect;

      const row = document.createElement("tr");

      row.innerHTML = `
        <td><input type="checkbox" name="defect" value="${seq}"></td>
        <td class="sub-category">${subCategory}</td>
        <td class="program-type">${programType}</td>
        <td class="program-id">${programId}</td>
        <td class="program-name">${programName}</td>
        <td class="screen-id">${screenId}</td>
        <td class="screen-name">${screenName}</td>
        <td class="program-status">${programStatus}</td>
        <td class="developer">${developer}</th>
        <td class="pl">${pl}</td>
        <td class="planned-start-date">${convertDate(plannedStartDate)}</td>
        <td class="planned-end-date">${convertDate(plannedEndDate)}</td>
        <td class="actual-start-date">${convertDate(actualStartDate)}</td>
        <td class="actual-end-date">${convertDate(actualEndDate)}</td>
        <td class="pl-test-cmp-date">${convertDate(plTestCmpDate)}</td>
        <td class="it-mgr">${itMgr}</td>
        <td class="busi-mgr">${busiMgr}</td>
        <td class="dev-status">${devStatus}</td>
      `;
      defectTableBody.appendChild(row);
    });

    setupPagination({ paginationElementId: "defectPagination", totalPages, currentPage, changePage });
  }
}

// 분류코드 필터링
function submitDefectFilter(event) {
  event.preventDefault(); // 폼 제출 기본 동작 방지

  // 페이지를 1로 초기화하고 테이블 렌더링
  currentPage = 1;

  const getDefectProps = getCurrentFilterValues();

  renderDefect(getDefectProps);
}

// 분류코드 필터 리셋
function resetDefectFilter() {
  this.reset(); // 폼 초기화
}

async function getDefect(
  getDefectProps = {
    page: 1,
    majorCategory: "",
    subCategory: "",
    programType: "",
    programId: "",
    programName: "",
    programStatus: "",
    developer: "",
    actualStartDate: "",
    actualEndDate: "",
    pl: "",
    thirdPartyTestMgr: "",
    itMgr: "",
    busiMgr: "",
  }
) {
  try {
    const query = new URLSearchParams(getDefectProps).toString();
    const { defectList, totalPages } = await tmsFetch(`/defect?${query}`);

    return { defectList, totalPages };
  } catch (error) {
    console.error(error.message, "개발 목록을 불러오지 못 했습니다.");
  }
}

// 페이지 변경
function changePage(page) {
  currentPage = page;

  const getDefectProps = getCurrentFilterValues();

  renderDefect(getDefectProps);
}

function getCurrentFilterValues() {
  let getDefectProps = { page: currentPage };

  const programKey = document.getElementById("programKeySelect").value;
  const programValue = document.getElementById("programValueInput").value.trim();

  const roleKey = document.getElementById("roleKeySelect").value;
  const roleValue = document.getElementById("roleValueInput").value.trim();

  getDefectProps[programKey] = programValue;
  getDefectProps[roleKey] = roleValue;

  const majorCategory = document.getElementById("majorCategoryForFilter").value;
  const subCategory = document.getElementById("subCategoryForFilter").value;
  const programType = document.getElementById("programTypeForFilter").value;
  const programStatus = document.getElementById("programStatusForFilter").value;
  const developer = document.getElementById("developerForFilter").value;
  const devStatus = document.getElementById("devStatusForFilter").value;
  const devStartDate = document.getElementById("actualEndDateFromForFilter").value;
  const devEndDate = document.getElementById("actualEndDateToForFilter").value;

  getDefectProps = {
    ...getDefectProps,
    majorCategory,
    subCategory,
    programType,
    programStatus,
    developer,
    devStatus,
    devStartDate,
    devEndDate,
  };

  return getDefectProps;
}

async function initializeFilterForm() {
  const [{ majorCategoryCodes }, { programTypes }, { programStatusList }, { devStatusList }] = await Promise.all([
    getMajorCategoryCodes(),
    getProgramTypes(),
    getProgramStatusList(),
    getDevStatusList(),
  ]);

  const SELECT_ID = {
    MAJOR_CATEGORY: "majorCategoryForFilter",
    PROGRAM_TYPE: "programTypeForFilter",
    PROGRAM_STATUS: "programStatusForFilter",
    DEV_STATUS: "devStatusForFilter",
  };

  const SELECT_DATA = {
    [SELECT_ID.MAJOR_CATEGORY]: majorCategoryCodes,
    [SELECT_ID.PROGRAM_TYPE]: programTypes,
    [SELECT_ID.PROGRAM_STATUS]: programStatusList,
    [SELECT_ID.DEV_STATUS]: devStatusList,
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

/** 결함현황  필터링 폼의 값을 다운로드 폼으로 복사하는 함수 */
function copyFilterValuesToDownloadForm() {
  // 조회 필터링 폼의 값을 가져옴
  const {
    majorCategory,
    subCategory,
    programType,
    programId = "",
    programName = "",
    programStatus,
    developer,
    pl = "",
    thirdPartyTestMgr = "",
    itMgr = "",
    busiMgr = "",
    devStatus,
    devStartDate,
    devEndDate,
  } = getCurrentFilterValues();

  // 숨겨진 다운로드 폼의 input 필드에 값을 설정
  document.getElementById("majorCategoryForDownload").value = majorCategory;
  document.getElementById("subCategoryForDownload").value = subCategory;
  document.getElementById("programTypeForDownload").value = programType;
  document.getElementById("programIdForDownload").value = programId;
  document.getElementById("programNameForDownload").value = programName;
  document.getElementById("programStatusForDownload").value = programStatus;
  document.getElementById("developerForDownload").value = developer;
  document.getElementById("plForDownload").value = pl;
  document.getElementById("thirdPartyTestMgrForDownload").value = thirdPartyTestMgr;
  document.getElementById("itMgrForDownload").value = itMgr;
  document.getElementById("busiMgrForDownload").value = busiMgr;
  document.getElementById("devStatusForDownload").value = devStatus;
  document.getElementById("actualEndDateFromForDownload").value = devStartDate;
  document.getElementById("actualEndDateToForDownload").value = devEndDate;
}

// 체크박스 전체 선택/해제
function toggleAllCheckboxes() {
  const checkboxes = document.querySelectorAll('input[name="defect"]');
  checkboxes.forEach((checkbox) => (checkbox.checked = selectAllDefectCheckbox.checked));
}

// API 함수

// 개발자 목록 조회
export async function getDevelopers() {
  try {
    const { developer: developers } = await tmsFetch(`/developer`);

    return { developers };
  } catch (error) {
    console.error(error.message, "개발자 목록을 불러오지 못 했습니다.");
  }
}

// 개발  파일 업로드
async function uploadDefectFile() {
  if (uploadDefectFileInput.files.length <= 0) return;

  const formData = new FormData();

  // key 이름의 경우 서버와 협의
  formData.append("file", uploadDefectFileInput.files[0]);

  try {
    showSpinner();

    const response = await tmsFetch("/devupload", {
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
    const confirmed = confirm("결함현황 정보를 삭제하시겠습니까?");

    if (!confirmed) return;

    const selectedDefectIds = Array.from(document.querySelectorAll('input[name="defect"]:checked')).map(
      (checkbox) => checkbox.value
    );

    if (selectedDefectIds.length === 0) {
      alert("삭제할 결함현황 정보를 선택해주세요.");
      return;
    }

    showSpinner();

    const { status } = await tmsFetch("/deletedev", {
      method: "DELETE",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(selectedDefectIds),
    });

    const success = status === "success";

    if (success) {
      alert(`결함현황 정보 삭제가 완료되었습니다.`);
      location.reload(); // 페이지 새로고침
    }
  } catch (error) {
    alert(error.message + "\n다시 시도해주세요.");
  } finally {
    hideSpinner();
  }
}
