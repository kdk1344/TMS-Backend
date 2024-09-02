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
  setupModalEventListeners,
  showSpinner,
  hideSpinner,
} from "./common.js";

let currentPage = 1;

// DOM 요소들
const devProgressFilterForm = document.getElementById("devProgressFilterForm");
const majorCategorySelect = document.getElementById("majorCategoryForFilter");

const uploadDevProgressFileButton = document.getElementById("uploadDevProgressFileButton");
const uploadDevProgressFileInput = document.getElementById("uploadDevProgressFileInput");

const openDevProgressFileUploadModalButton = document.getElementById("openDevProgressFileUploadModalButton");
const closeDevProgressFileUploadModalButton = document.getElementById("closeDevProgressFileUploadModalButton");
const openDevProgressFileDownloadModalButton = document.getElementById("openDevProgressFileDownloadModalButton");
const closeDevProgressFileDownloadModalButton = document.getElementById("closeDevProgressFileDownloadModalButton");

// 문서 로드 시 초기화
document.addEventListener("DOMContentLoaded", init);

// 모달 아이디
const MODAL_ID = {
  DEV_PROGRESS_DEVELOPER_SEARCH: "developerSearchModal",
  DEV_PROGRESS_REGISTER: "devProgressRegisterModal",
  DEV_PROGRESS_EDIT: "devProgressEditModal",
  DEV_PROGRESS_FILE_UPLOAD: "devProgressFileUploadModal",
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

  // 엑셀 업로드 이벤트 핸들러
  if (openDevProgressFileUploadModalButton) {
    openDevProgressFileUploadModalButton.addEventListener("click", () => openModal(MODAL_ID.DEV_PROGRESS_FILE_UPLOAD));
  }

  if (closeDevProgressFileUploadModalButton) {
    closeDevProgressFileUploadModalButton.addEventListener("click", () =>
      closeModal(MODAL_ID.DEV_PROGRESS_FILE_UPLOAD)
    );
  }

  if (uploadDevProgressFileButton) {
    uploadDevProgressFileButton.addEventListener("click", uploadDevProgressFile);
  }

  // 엑셀 다운로드 이벤트 핸들러
  if (openDevProgressFileDownloadModalButton) {
    openDevProgressFileDownloadModalButton.addEventListener("click", () => {
      copyFilterValuesToDownloadForm(); // 필터링 값 복사
      openModal(MODAL_ID.DEV_PROGRESS_FILE_DOWNLOAD);
    });
  }

  if (closeDevProgressFileDownloadModalButton) {
    closeDevProgressFileDownloadModalButton.addEventListener("click", () =>
      closeModal(MODAL_ID.DEV_PROGRESS_FILE_DOWNLOAD)
    );
  }

  // 모달 외부 클릭 시 닫기 버튼 이벤트 핸들러
  setupModalEventListeners(Object.values(MODAL_ID));

  /** @deprecated 개발자 검색 시 '이름'으로 검색 & 굳이 사용자 목록과 동기화되지 않아도 되므로 해당 기능 제외 */
  // const developerSearchButton = document.getElementById("developerSearchButton");
  // if (developerSearchButton) {
  //   developerSearchButton.addEventListener("click", () => {
  //     renderDevelopers();
  //     openModal(MODAL_ID.DEV_PROGRESS_DEVELOPER_SEARCH);
  //   });
  // }

  // const developerList = document.getElementById("developerList");
  // if (developerList) {
  //   developerList.addEventListener("click", onDeveloperItemClick);
  // }

  // if (developerSearchModal) {
  //   closeDeveloperSearchModalButton.addEventListener("click", () => closeModal(MODAL_ID.DEV_PROGRESS_DEVELOPER_SEARCH));
  // }
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

  const getDevProgressProps = getCurrentFilterValues();

  renderDevProgress(getDevProgressProps);
}

// 분류코드 필터 리셋
function resetDevProgressFilter() {
  this.reset(); // 폼 초기화

  /** @deprecated 개발자 검색 시 '이름'으로 검색 & 굳이 사용자 목록과 동기화되지 않아도 되므로 해당 기능 제외 */
  // const deverloperNameDisplay = document.getElementById("developerNameDisplay");
  // deverloperNameDisplay.textContent = "";
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

  const getDevProgressProps = getCurrentFilterValues();

  renderDevProgress(getDevProgressProps);
}

function getCurrentFilterValues() {
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

  return getDevProgressProps;
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

/** 프로그램 개발 진행현황 필터링 폼의 값을 다운로드 폼으로 복사하는 함수 */
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

// API 함수
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

// 개발 진행현황 파일 업로드
async function uploadDevProgressFile() {
  if (uploadDevProgressFileInput.files.length <= 0) return;

  const formData = new FormData();

  // key 이름의 경우 서버와 협의
  formData.append("file", uploadDevProgressFileInput.files[0]);

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

/** @deprecated 개발자 검색 시 '이름'으로 검색 & 굳이 사용자 목록과 동기화되지 않아도 되므로 해당 기능 제외 */
// const developerSearchModal = document.getElementById("developerSearchModal");
// const closeDeveloperSearchModalButton = document.getElementById("closeDeveloperSearchModalButton");

// 개발자 목록 렌더링
// export async function renderDevelopers() {
//   const { developers } = await getDevelopers();
//   const developerList = document.getElementById("developerList");

//   // 기존 목록을 지웁니다.
//   developerList.innerHTML = "";

//   // 개발자 목록을 렌더링합니다.
//   developers.forEach((developer) => {
//     const listItem = document.createElement("li");
//     // data-* 속성을 사용하여 userID와 userName을 저장
//     listItem.dataset.userId = developer.userID;
//     listItem.dataset.userName = developer.userName;

//     const userId = document.createElement("p");
//     userId.textContent = developer.userID;

//     // userName과 userID 요소를 생성합니다.
//     const userName = document.createElement("p");
//     userName.textContent = developer.userName;

//     listItem.appendChild(userId);
//     listItem.appendChild(userName);

//     developerList.appendChild(listItem);
//   });
// }

// const developerSearchModal = document.getElementById("developerSearchModal");
// const closeDeveloperSearchModalButton = document.getElementById("closeDeveloperSearchModalButton");

// function onDeveloperItemClick(event) {
//   // 이벤트가 발생한 요소부터 가장 가까운 li 요소를 찾습니다.
//   const listItem = event.target.closest("li");

//   if (listItem.tagName === "LI") {
//     // 선택된 개발자 이름을 입력 필드에 설정합니다.
//     const developerInput = document.getElementById("developerForFilter");

//     developerInput.value = listItem.dataset.userName;

//     closeModal(MODAL_ID.DEV_PROGRESS_DEVELOPER_SEARCH);
//   }
// }
