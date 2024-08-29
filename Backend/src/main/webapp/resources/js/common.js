// DOM 요소
const spinner = document.getElementById("spinner");

// 문서 로드 시 초기화
document.addEventListener("DOMContentLoaded", init);

// 초기화 함수
function init() {
  hideSpinner();
}

// 메뉴 항목과 하위 항목들을 정의
const menuData = [
  {
    name: "개발진행관리",
    subMenu: [{ name: "프로그램 개발 진행현황", link: "/tms/devProgress" }],
  },
  {
    name: "테스트 진행관리",
    subMenu: [{ name: "테스트 진행현황", link: "/tms/testProgress" }],
  },
  {
    name: "결함진행관리",
    subMenu: [{ name: "결함현황", link: "/tms/defectStatus" }],
  },
  {
    name: "관리자",
    subMenu: [
      { name: "사용자 관리", link: "/tms/adminUser" },
      { name: "공통코드 관리", link: "/tms/commonCode" },
      { name: "분류코드 관리", link: "/tms/categoryCode" },
      { name: "공지사항", link: "/tms/notice" },
    ],
  },
];

// 헤더를 렌더링하는 함수
export async function renderTMSHeader() {
  const userInfoHTML = await getUserInfoHTML();
  const header = document.querySelector("header"); // 첫 번째 <header> 태그 선택
  const menuList = menuData.map(generateMenuItem).join(""); // 전체 메뉴

  if (header) {
    header.innerHTML = `
      <div class="navigation-box">
        <h1 class="logo">
           <a href="/tms/dashboard">TMS</a>
        </h1>
        <nav class="navigation">
            <ul>
                ${menuList}
            </ul>
        </nav>
      </div>
      ${userInfoHTML} <!-- 사용자 정보를 헤더에 추가 -->
    `;

    if (userInfoHTML) {
      const logoutButton = document.getElementById("logoutButton");

      logoutButton.addEventListener("click", logout); // 로그아웃 버튼 클릭 이벤트 핸들러
    }
  }
}

// 사용자 정보 HTML을 반환하는 함수
async function getUserInfoHTML() {
  const { isLogin, userID, authrityCode } = await checkSession();

  if (isLogin) {
    return `
        <div id="userInfoBox">
          <span>${userID}님</span>
          <button type="button" id="logoutButton" onclick="logout">로그아웃</button>
        </div>
      `;
  } else {
    alert("로그인이 필요합니다.");
    window.location.href = "/tms/login"; // 로그인 페이지로 이동
    return "";
  }
}

// 메뉴 항목과 하위 메뉴를 렌더링하는 함수
function generateMenuItem(item) {
  // 서브 메뉴의 첫 번째 링크를 주요 링크로 설정
  const mainLink = item.subMenu.length > 0 ? item.subMenu[0].link : "#";

  // 하위 메뉴를 렌더링하는 함수
  const subMenu =
    item.subMenu.length > 0
      ? `
      <ul id="sub-menu">
        ${item.subMenu
          .map(
            (subItem) => `
          <li><a href="${subItem.link}">${subItem.name}</a></li>
        `
          )
          .join("")}
      </ul>
    `
      : "";

  return `
      <li class="menu-item">
        <a href="${mainLink}" class="main-link">${item.name}</a>
        ${subMenu}
      </li>
    `;
}

// Error 객체를 확장하여 상태 코드를 추가
class TMSError extends Error {
  constructor(message, statusCode) {
    super(message); // 부모 클래스의 생성자를 호출하여 메시지를 설정
    this.statusCode = statusCode; // 상태 코드를 추가 속성으로 설정
    this.name = this.constructor.name; // 오류 이름을 설정
  }
}

/**
 * TMS 서비스의 기본 URL을 추가하여 매번 기본 URL을 작성하지 않도록 하기 위한 fetch 함수입니다.
 *
 * @template T
 * @param {RequestInfo} url - 요청할 URL입니다.
 * @param {RequestInit} [options] - 선택적인 fetch 옵션입니다. 예를 들어, method, headers, body 등을 설정할 수 있습니다.
 * @returns {Promise<T>} - JSON 형식의 데이터를 반환하는 Promise입니다. 응답이 JSON 형식이 아니면 응답 객체를 반환합니다.
 * @throws {Error} - 응답 상태 코드가 오류(예: 4xx 또는 5xx)를 나타내거나 fetch 작업이 실패한 경우 `Error`를 던집니다.
 */
export async function tmsFetch(url, options) {
  const TMS_BASE_URL = "/tms/api";

  return fetchAPI(TMS_BASE_URL + url, options);
}

/**
 * API에서 데이터를 가져오고 다양한 HTTP 상태 오류를 처리합니다.
 *
 * @template T
 * @param {RequestInfo} url - 가져올 URL입니다. 문자열 또는 `Request` 객체일 수 있습니다.
 * @param {RequestInit} [options] - 선택적으로 fetch 요청에 대한 옵션을 설정할 수 있습니다. 예: method, headers, body 등.
 * @returns {Promise<T>} - JSON 형식의 데이터를 반환하는 Promise입니다. 응답이 JSON 형식이 아니면 응답 객체를 반환합니다.
 * @throws {Error} - 응답 상태 코드가 오류(예: 4xx 또는 5xx)를 나타내거나 fetch 작업이 실패한 경우 `Error`를 던집니다.
 *
 * @example
 * fetchAPI('/api/data', { method: 'GET' })
 *   .then(data => console.log(data))
 *   .catch(error => console.error(error));
 */
export async function fetchAPI(url, options) {
  try {
    const response = await fetch(url, options);

    if (!response.ok) {
      const statusCode = response.status;

      if (statusCode >= 500) throw new TMSError("[Error 500] 서버에 문제가 생겼습니다.", statusCode);
      if (statusCode === 401) throw new TMSError("[Error 401] 아이디와 비밀번호를 확인해주세요.", statusCode);
      if (statusCode === 403) throw new TMSError("[Error 403] 접근 권한이 없습니다.", statusCode);
      if (statusCode === 404) throw new TMSError("[Error 404] 요청하신 페이지를 찾을 수 없습니다.", statusCode);
      if (statusCode === 409)
        throw new TMSError("[Error 409] 기존 리소스와 충돌이 발생했습니다.(ex. 중복 아이디)", statusCode);
      if (statusCode >= 400) throw new TMSError("[Error] 유효하지 않은 요청입니다.", statusCode);
    }

    const contentType = response.headers.get("content-type");
    const isJsonType = contentType && contentType.includes("application/json");

    if (isJsonType) return response.json();

    return response;
  } catch (error) {
    console.error(error);
    throw error;
  }
}

// 모달 열기
export function openModal(modalId) {
  const modal = document.getElementById(modalId);

  if (modal) {
    modal.style.display = "block";
  } else {
    console.error(`Modal with ID "${modalId}" not found.`);
  }
}

// 모달 닫기
export function closeModal(modalId) {
  const modal = document.getElementById(modalId);

  if (modal) {
    modal.style.display = "none";

    const form = modal.querySelector("form");
    const filePreview = modal.querySelector(".file-preview");

    if (form) form.reset(); // 모달 내부 폼이 있는 경우 폼 리셋

    /** @note file-preview라는 클래스명을 첨부파일 프리뷰 요소에 넣어야 함  */
    if (filePreview) filePreview.innerHTML = ""; // file-preview 요소 리셋
  } else {
    console.error(`Modal with ID "${modalId}" not found.`);
  }
}

// 모달 외부 클릭 시 닫기
export function closeModalOnClickOutside(event, modalId) {
  const modal = document.getElementById(modalId);

  if (modal && event.target === modal) {
    closeModal(modalId); // 모달 닫기
  }
}

// 타임스탬프를 yyyy-mm-dd 형식으로 변환하는 함수
export function convertDate(timestamp) {
  // 타임스탬프를 Date 객체로 변환
  const date = new Date(timestamp);

  // 년, 월, 일 추출
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0"); // 월은 0부터 시작하므로 +1
  const day = String(date.getDate()).padStart(2, "0"); // 일

  // yyyy-mm-dd 형식으로 반환
  return `${year}-${month}-${day}`;
}

// 현재 시간을 yyyy-mm-dd 형식으로 반환하는 함수
export function getCurrentDate() {
  const now = new Date(); // 현재 날짜와 시간
  const year = now.getFullYear(); // 년도
  const month = String(now.getMonth() + 1).padStart(2, "0"); // 월 (0부터 시작하므로 +1)
  const day = String(now.getDate()).padStart(2, "0"); // 일

  return `${year}-${month}-${day}`; // yyyy-mm-dd 형식
}

// 파일 목록을 메모리에서 관리할 배열
const fileListStore = new Map();

// 파일 목록 프리뷰 업데이트 함수
export function updateFilePreview(fileInputId, fileListOutputId) {
  const fileInput = document.getElementById(fileInputId);
  const fileListOutput = document.getElementById(fileListOutputId);

  if (!fileInput || !fileListOutput) {
    console.error("파일 입력 필드 또는 파일 목록 출력 요소를 찾을 수 없습니다.");
    return;
  }

  // 파일 목록을 포함할 <ul> 요소 생성
  const fileList = document.createElement("ul");

  const files = Array.from(fileInput.files);

  // 저장된 파일 목록을 순회하며 <li> 요소 생성
  files.forEach((file, _) => {
    const fileItem = document.createElement("li");

    // 파일 이름 표시
    const fileName = document.createElement("span");
    fileName.textContent = file.name;
    fileName.classList.add("file-name");

    // 삭제 버튼 생성
    const removeButton = document.createElement("button");
    removeButton.textContent = "삭제";
    removeButton.classList.add("file-remove-button");

    // 삭제 버튼 클릭 이벤트
    removeButton.addEventListener("click", () => {
      removeFile(file.name, fileInputId, fileListOutputId);
    });

    // 파일 항목에 파일 이름과 삭제 버튼 추가
    fileItem.appendChild(fileName);
    fileItem.appendChild(removeButton);

    // <li> 요소를 <ul> 요소에 추가
    fileList.appendChild(fileItem);
  });

  // <ul> 요소를 파일 목록 출력 요소에 추가
  fileListOutput.replaceChildren(fileList);
}

// 파일 추가 함수
export function addFiles(fileInputId) {
  const fileInput = document.getElementById(fileInputId);

  // 새로 선택된 파일들을 가져옴
  const newFiles = Array.from(fileInput.files);

  // 새로 선택된 파일들을 fileListStore에 추가
  newFiles.forEach((file) => {
    fileListStore.set(file.name, file);
  });

  // 새로운 파일들을 저장하기 위한 DataTransfer 객체 생성
  const dt = new DataTransfer();

  // fileListStore에 있는 파일들을 DataTransfer 객체에 추가
  fileListStore.forEach((file) => dt.items.add(file));

  // 파일 입력 필드의 파일 목록을 DataTransfer 객체의 파일로 업데이트
  fileInput.files = dt.files;
}

// 파일 제거 함수
export function removeFile(fileName, fileInputId, fileListOutputId) {
  const fileInput = document.getElementById(fileInputId);

  // 기존 파일 목록에서 삭제
  fileListStore.delete(fileName);

  // DataTransfer 객체를 사용하여 새로운 파일 목록 생성
  const dt = new DataTransfer();

  Array.from(fileInput.files)
    .filter((file) => file.name !== fileName) // 삭제할 파일을 제외
    .forEach((file) => dt.items.add(file));

  fileInput.files = dt.files; // fileInput의 파일 목록을 업데이트

  updateFilePreview(fileInputId, fileListOutputId); // 미리보기 업데이트
}

// 파일을 다운로드하는 함수
export async function downloadFile(fileID) {
  try {
    showSpinner();

    // getFile 함수를 호출하여 파일 데이터를 가져옴
    const { blob, fileName } = await getFile(fileID);

    // Blob을 URL 객체로 변환
    const url = window.URL.createObjectURL(blob);

    // 다운로드를 트리거할 <a> 태그를 동적으로 생성
    const a = document.createElement("a");

    // 생성한 <a> 태그의 href 속성을 Blob URL로 설정
    a.href = url;

    // 파일 이름을 설정
    a.download = fileName;

    // <a> 태그를 문서의 body에 추가
    document.body.appendChild(a);

    // <a> 태그를 클릭하여 파일 다운로드를 시작
    a.click();

    // <a> 태그를 문서에서 제거
    a.remove();

    // 생성한 URL 객체를 해제하여 메모리 누수 방지
    window.URL.revokeObjectURL(url);
  } catch (error) {
    alert(error.message + "\n다시 시도해주세요.");
  } finally {
    hideSpinner();
  }
}

// 서버에서 파일을 가져오는 함수
export async function getFile(fileID) {
  // 서버에서 파일을 다운로드하기 위해 요청을 보냄
  const response = await tmsFetch(`/downloadAttachment?seq=${fileID}`);

  // 응답을 Blob 형식으로 변환
  const blob = await response.blob();

  // 응답의 Content-Disposition 헤더에서 파일 이름을 추출
  const fileName = decodeURIComponent(
    response.headers.get("Content-Disposition").split("filename=")[1].replace(/"/g, "")
  );

  return { blob, fileName };
}

// 파일 ID를 기반으로 서버에서 파일을 가져와 파일 입력 필드에 추가
export async function loadFilesToInput(fileInputID, fileIDs) {
  const fileInput = document.getElementById(fileInputID);

  if (!fileInput) {
    console.error(`파일 입력 필드 (${fileInputID})를 찾을 수 없습니다.`);
    return;
  }

  // DataTransfer 객체를 사용하여 파일 목록을 업데이트
  const dataTransfer = new DataTransfer();

  try {
    // 모든 파일을 비동기적으로 가져오고, 결과를 배열로 저장
    const files = await Promise.all(
      fileIDs.map(async (fileID) => {
        try {
          if (fileID === "") return null;

          const { blob, fileName } = await getFile(fileID); // getFile 함수로 파일을 가져옴

          return new File([blob], fileName); // Blob과 파일 이름으로 File 객체 생성
        } catch (error) {
          console.error(`파일 ID ${fileID}를 가져오는 데 실패했습니다:`, error);
          return null; // 실패한 파일은 null로 반환
        }
      })
    );

    // null이 아닌 파일만 DataTransfer 객체에 추가
    files.forEach((file) => {
      if (file) {
        dataTransfer.items.add(file);
      }
    });

    // 파일 입력 필드에 파일 목록 설정
    fileInput.files = dataTransfer.files;
  } catch (error) {
    console.error(error.message);
  }
}

// 날짜 필드를 초기화하는 함수
export function initializeDateFields(startDateInputId, endDateInputId) {
  const startDateInput = document.getElementById(startDateInputId);
  const endDateInput = document.getElementById(endDateInputId);

  // 오늘 날짜
  const today = new Date();
  const todayString = today.toISOString().split("T")[0];

  // 오늘로부터 30일 전
  const thirtyDaysAgo = new Date();
  thirtyDaysAgo.setDate(today.getDate() - 30);
  const thirtyDaysAgoString = thirtyDaysAgo.toISOString().split("T")[0];

  // 기본값 설정
  startDateInput.value = thirtyDaysAgoString;
  endDateInput.value = todayString;
}

// 로그인 확인 후 로그인 된 상태면 HOME으로 보내는 함수
export async function redirectHomeOnLogin() {
  const { isLogin } = await checkSession();

  if (isLogin) window.location.href = "/tms/dashboard";
}

// 로딩 스피너 표시
export function showSpinner() {
  spinner.style.display = "flex";
}

// 로딩 스피너 숨기기
export function hideSpinner() {
  spinner.style.display = "none";
}

// 페이지네이션 설정
export function setupPagination({ paginationElementId, totalPages, currentPage, changePage }) {
  const paginationContainer = document.getElementById(paginationElementId);

  if (paginationContainer) {
    paginationContainer.innerHTML = ""; // 기존 내용 지우기

    // '처음' 버튼
    createPaginationButton({
      container: paginationContainer,
      text: "<<",
      disabled: currentPage <= 1,
      onClick: () => changePage(1),
      buttonType: "first",
    });

    // 이전 버튼
    createPaginationButton({
      container: paginationContainer,
      text: "<",
      disabled: currentPage <= 1,
      onClick: () => changePage(currentPage - 1),
      buttonType: "prev",
    });

    // 페이지 버튼들
    const MAX_BUTTON_NUM = 10; // 최대 페이지 버튼 수

    let startPage = 1;
    let endPage = totalPages;

    // 총 페이지가 MAX_BUTTOM_NUM 보다 많은 경우
    if (totalPages > MAX_BUTTON_NUM) {
      // 현재 페이지가 시작 부분 근처에 있는 경우
      if (currentPage <= MAX_BUTTON_NUM) {
        startPage = 1;
        endPage = MAX_BUTTON_NUM;
      } else {
        // startPage를 1, 11, 21... 이런 식으로 설정
        startPage = Math.floor((currentPage - 1) / MAX_BUTTON_NUM) * MAX_BUTTON_NUM + 1;

        // endPage를 설정
        endPage = Math.min(startPage + MAX_BUTTON_NUM - 1, totalPages);
      }
    }

    for (let i = startPage; i <= endPage; i++) {
      createPaginationButton({
        container: paginationContainer,
        text: i,
        disabled: i === currentPage,
        onClick: () => changePage(i),
      });
    }

    // 다음 버튼
    createPaginationButton({
      container: paginationContainer,
      text: ">",
      disabled: currentPage >= totalPages,
      onClick: () => changePage(currentPage + 1),
      buttonType: "next",
    });

    // '끝' 버튼
    createPaginationButton({
      container: paginationContainer,
      text: ">>",
      disabled: currentPage >= totalPages,
      onClick: () => changePage(totalPages),
      buttonType: "last",
    });
  }
}

// 페이지 버튼 생성
export function createPaginationButton({ container, text, disabled, onClick, buttonType = "page" }) {
  const button = document.createElement("button");
  button.textContent = text;
  button.disabled = disabled;

  // buttonType에 따라 클래스 추가
  if (buttonType === "page") {
    button.classList.toggle("active", disabled); // 현재 페이지 표시할 클래스 추가
  }

  button.addEventListener("click", onClick);
  container.appendChild(button);
}

// option 목록을 가져와 select 요소에 옵션을 설정하는 함수
export function initializeSelect(selectElementId, options = [], valueKey = "code", textContentKey = "codeName") {
  const select = document.getElementById(selectElementId);

  // 기존 옵션을 모두 삭제하고, 기본값이 되는 옵션은 따로 저장
  const defaultOption = select.querySelector("option[selected]");
  select.innerHTML = ""; // 기존 옵션들 삭제

  // 기본값 옵션을 다시 추가
  if (defaultOption) select.appendChild(defaultOption);

  // 새 옵션 생성 및 추가
  options.forEach((option) => {
    const optionElement = document.createElement("option");

    optionElement.value = option[valueKey];
    optionElement.textContent = option[textContentKey];

    select.appendChild(optionElement);
  });
}

/* API 함수 */
// 사용자 로그인 상태 확인하는 함수
export async function checkSession() {
  try {
    const response = await tmsFetch("/checkSession");
    const isLogin = response.status === "success";

    if (isLogin) return { isLogin, userID: response.userID, authrityCode: response.authrityCode };
  } catch (error) {
    if (error.statusCode === 401) return { isLogin: false, userID: null, authrityCode: null };
    else alert(error.message);
  }
}

// 로그아웃 함수
export async function logout() {
  try {
    const response = await tmsFetch("/logout");
    const isSuccess = response.status === "success";

    if (isSuccess) window.location.href = "/tms/login"; // 로그아웃 후 로그인 페이지로 이동
  } catch (error) {
    alert(error.message);
  }
}

// 대분류 코드 목록 조회
export async function getMajorCategoryCodes() {
  try {
    const { parentCodes: majorCategoryCodes } = await tmsFetch("/catparentCode");

    return { majorCategoryCodes };
  } catch (error) {
    console.error("Error fetching CC Parent Codes", error.message);
  }
}

// 중분류 코드 목록 조회
export async function getSubCategoryCodes(parentCode = "") {
  try {
    if (parentCode === "") return [];

    const query = new URLSearchParams({ parentCode }).toString();
    const { CTCodes: subCategoryCodes = [] } = await tmsFetch(`/catcode?${query}`);

    return { subCategoryCodes };
  } catch (error) {
    console.error("Error fetching CC Codes", error.message);
  }
}
