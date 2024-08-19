// DOM 요소
const spinner = document.getElementById("spinner");

// 페이지 로딩 시
document.addEventListener("DOMContentLoaded", () => {
  hideSpinner();
});

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
export function renderTMSHeader() {
  const header = document.querySelector("header"); // 첫 번째 <header> 태그 선택
  const menuList = menuData.map(generateMenuItem).join(""); // 전체 메뉴

  if (header) {
    header.innerHTML = `
        <h1 class="logo">
           TMS
        </h1>
        <nav class="navigation">
            <ul>
                ${menuList}
            </ul>
        </nav>
    `;
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
      if (response.status >= 500) throw new Error("[Error 500] 서버에 문제가 생겼습니다.");
      if (response.status === 401) throw new Error("[Error 401] 아이디와 비밀번호를 확인해주세요.");
      if (response.status === 403) throw new Error("[Error 403] 접근 권한이 없습니다.");
      if (response.status === 404) throw new Error("[Error 404] 요청하신 페이지를 찾을 수 없습니다.");
      if (response.status === 409) throw new Error("[Error 409] 기존 리소스와 충돌이 발생했습니다.(ex. 중복 아이디)");
      if (response.status >= 400) throw new Error("[Error] 유효하지 않은 요청입니다.");
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
  } else {
    console.error(`Modal with ID "${modalId}" not found.`);
  }
}

// 모달 외부 클릭 시 닫기
export function closeModalOnClickOutside(event, modalId) {
  const modal = document.getElementById(modalId);

  if (modal && event.target === modal) {
    closeModal(modalId);
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

// 파일 목록 프리뷰 업데이트 함수
export function updateFilePreview(fileInputId, fileListOutputId) {
  const fileInput = document.getElementById(fileInputId);
  const fileListOutput = document.getElementById(fileListOutputId);

  if (!fileInput || !fileListOutput) {
    console.error("파일 입력 필드 또는 파일 목록 출력 요소를 찾을 수 없습니다.");
    return;
  }

  const files = fileInput.files;
  const fileNames = Array.from(files)
    .map((file) => `<li>${file.name}</li>`)
    .join("");

  fileListOutput.innerHTML = `<ul>${fileNames}</ul>`;
}

// 로딩 스피너 표시
export function showSpinner() {
  spinner.style.display = "flex";
}

// 로딩 스피너 숨기기
export function hideSpinner() {
  spinner.style.display = "none";
}
