import {
  tmsFetch,
  renderTMSHeader,
  setupPagination,
  convertDate,
  initializeSelect,
  getMajorCategoryCodes,
  getSubCategoryCodes,
  openModal,
  closeModal,
  closeModalOnClickOutside,
} from "./common.js";

let currentPage = 1;

// DOM 요소들
const devProgressFilterForm = document.getElementById("devProgressFilterForm");
const majorCategorySelect = document.getElementById("majorCategoryForFilter");
const developerSearchButton = document.getElementById("developerSearchButton");
const developerSearchModal = document.getElementById("developerSearchModal");
const developerList = document.getElementById("developerList");

// 문서 로드 시 초기화
document.addEventListener("DOMContentLoaded", init);

// 모달 아이디
const MODAL_ID = {
  DEV_PROGRESS_DEVELOPER_SEARCH: "developerSearchModal",
  DEV_PROGRESS_REGISTER: "devProgressRegisterModal",
  DEV_PROGRESS_EDIT: "devProgressEditModal",
  DEV_PROGRESS_FILE_DOWNLOAD: "devProgressFileDownloadModal",
};

// 초기화 함수
function init() {
  renderTMSHeader();
  setupEventListeners();
  loadInitialDevProgress();
}

// 이벤트 핸들러 설정
function setupEventListeners() {
  // 프로그램 개발 진행현황 필터 폼 제출 및 리셋 이벤트 핸들러
  if (devProgressFilterForm) {
    devProgressFilterForm.addEventListener("submit", submitDevProgressFilter);
    devProgressFilterForm.addEventListener("reset", resetDevProgressFilter);
  }

  if (majorCategorySelect) {
    majorCategorySelect.addEventListener("change", () => initializeSubCategorySelect(majorCategorySelect.value));
  }

  if (developerSearchButton) {
    developerSearchButton.addEventListener("click", () => {
      renderDevelopers();
      openModal(MODAL_ID.DEV_PROGRESS_DEVELOPER_SEARCH);
    });
  }

  if (developerList) {
    developerList.addEventListener("click", onDeveloperItemClick);
  }

  if (developerSearchModal) {
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

// 초기 프로그램 개발 진행 현황 목록 로드
function loadInitialDevProgress() {
  initializeFilterForm(); // 폼 메타 데이터 초기 로드
  renderDevProgress(); // 분류코드 목록 render
}

// 프로그램 개발 진행 현황 목록 테이블 렌더링
async function renderDevProgress(
  getDevProgressProps = {
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
  const { devProgressList, totalPages } = await getDevProgress(getDevProgressProps);

  if (devProgressTableBody) {
    devProgressTableBody.innerHTML = "";

    devProgressList.forEach((devProgress) => {
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
      } = devProgress;

      const row = document.createElement("tr");

      row.innerHTML = `
        <td><input type="checkbox" name="devProgress" value="${seq}"></td>
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
      devProgressTableBody.appendChild(row);
    });

    setupPagination({ paginationElementId: "devProgressPagination", totalPages, currentPage, changePage });
  }
}

// 분류코드 필터링
function submitDevProgressFilter(event) {
  event.preventDefault(); // 폼 제출 기본 동작 방지

  // 페이지를 1로 초기화하고 테이블 렌더링
  currentPage = 1;

  let getDevProgressProps = { page: currentPage };

  const programKey = document.getElementById("programKeySelect").value;
  const programValue = document.getElementById("programValueInput").value.trim();

  const roleKey = document.getElementById("roleKeySelect").value;
  const roleValue = document.getElementById("roleValueInput").value.trim();

  getDevProgressProps[programKey] = programValue;
  getDevProgressProps[roleKey] = roleValue;

  const majorCategory = document.getElementById("majorCategoryForFilter").value;
  const subCategory = document.getElementById("subCategoryForFilter").value;
  const programType = document.getElementById("programTypeForFilter").value;
  const programStatus = document.getElementById("programStatusForFilter").value;
  const developer = document.getElementById("developerForFilter").value;
  const devStatus = document.getElementById("devStatusForFilter").value;
  const devStartDate = document.getElementById("actualEndDateFromForFilter").value;
  const devEndDate = document.getElementById("actualEndDateToForFilter").value;

  getDevProgressProps = {
    ...getDevProgressProps,
    majorCategory,
    subCategory,
    programType,
    programStatus,
    developer,
    devStatus,
    devStartDate,
    devEndDate,
  };

  renderDevProgress(getDevProgressProps);
}

// 분류코드 필터 리셋
function resetDevProgressFilter() {
  this.reset(); // 폼 초기화
}

async function getDevProgress(
  getDevProgressProps = {
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
    const query = new URLSearchParams(getDevProgressProps).toString();
    const { devProgressList, totalPages } = await tmsFetch(`/devProgress?${query}`);

    return { devProgressList, totalPages };
  } catch (error) {
    console.error(error.message, "개발 진행현황 목록을 불러오지 못 했습니다.");
  }
}

// 페이지 변경
function changePage(page) {
  currentPage = page;

  // 필터링 조건을 가져오기 (추후 수정 필요)
  const getDevProgressProps = {
    page: currentPage,
    majorCategory: document.getElementById("majorCategory").value || "",
    subCategory: document.getElementById("subCategory").value || "",
    programType: document.getElementById("programType").value || "",
    programId: document.getElementById("programId").value || "",
    programName: document.getElementById("programName").value || "",
    programStatus: document.getElementById("programStatus").value || "",
    developer: document.getElementById("developer").value || "",
    actualStartDate: document.getElementById("actualStartDate").value || "",
    actualEndDate: document.getElementById("actualEndDate").value || "",
    pl: document.getElementById("pl").value || "",
    thirdPartyTestMgr: document.getElementById("thirdPartyTestMgr").value || "",
    itMgr: document.getElementById("itMgr").value || "",
    busiMgr: document.getElementById("busiMgr").value || "",
  };

  renderDevProgress(getDevProgressProps);
}

async function initializeFilterForm() {
  const { majorCategoryCodes } = await getMajorCategoryCodes();
  const { programTypes } = await getProgramTypes();
  const { programStatusList } = await getProgramStatusList();
  const { devStatusList } = await getDevStatusList();

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

// 개발자 목록 렌더링
export async function renderDevelopers() {
  const { developers } = await getDevelopers();
  const developerList = document.getElementById("developerList");

  // 기존 목록을 지웁니다.
  developerList.innerHTML = "";

  // 개발자 목록을 렌더링합니다.
  developers.forEach((developer) => {
    const listItem = document.createElement("li");

    listItem.textContent = developer.name;
    listItem.setAttribute("tabindex", "0");

    developerList.appendChild(listItem);
  });
}

function onDeveloperItemClick(event) {
  const target = event.target;

  if (target.tagName === "LI") {
    // 선택된 개발자 이름을 입력 필드에 설정합니다.
    const developerInput = document.getElementById("developerForFilter");

    developerInput.textContent = target.textContent;
    developerInput.value = target.id; // 수정1!
  }
}

// 메타데이터 조회 apis
// 프로그램 구분 목록 조회
export async function getProgramTypes() {
  try {
    const { programType: programTypes } = await tmsFetch(`/programType`);

    return { programTypes };
  } catch (error) {
    console.error(error.message, "프로그램 구분 목록을 불러오지 못 했습니다.");
  }
}

// 프로그램 상태 목록 조회
export async function getProgramStatusList() {
  try {
    const { programStatus: programStatusList } = await tmsFetch(`/programStatus`);

    return { programStatusList };
  } catch (error) {
    console.error(error.message, "프로그램 상태 목록을 불러오지 못 했습니다.");
  }
}

// 개발진행 상태 목록 조회
export async function getDevStatusList() {
  try {
    const { devStatus: devStatusList } = await tmsFetch(`/devStatus`);

    return { devStatusList };
  } catch (error) {
    console.error(error.message, "개발진행 상태 목록을 불러오지 못 했습니다.");
  }
}

// 개발자 목록 조회
export async function getDevelopers() {
  try {
    const { developer: developers } = await tmsFetch(`/developer`);

    return { developers };
  } catch (error) {
    console.error(error.message, "개발자 목록을 불러오지 못 했습니다.");
  }
}
