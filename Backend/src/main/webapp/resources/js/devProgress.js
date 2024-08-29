import { tmsFetch, renderTMSHeader, setupPagination, convertDate, initializeSelect } from "./common.js";
import { getMajorCategoryCodes, getSubCategoryCodes } from "./categoryCode.js";

let currentPage = 1;

// 문서 로드 시 초기화
document.addEventListener("DOMContentLoaded", init);

// 모달 아이디
const MODAL_ID = {
  DEV_PROGRESS_REGISTER: "userRegisterModal",
  DEV_PROGRESS_EDIT: "userEditModal",
  DEV_PROGRESS_FILE_DOWNLOAD: "userFileDownloadModal",
};

// 초기화 함수
function init() {
  renderTMSHeader();
  //   setupEventListeners();
  loadInitialDevProgress();
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
