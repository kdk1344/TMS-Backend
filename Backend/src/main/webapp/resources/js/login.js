import { tmsFetch, showSpinner, hideSpinner, redirectHomeOnLogin, getNotices, convertDate } from "./common.js";

// DOM 요소
const loginForm = document.getElementById("loginForm");

// 문서 로드 시 초기화
document.addEventListener("DOMContentLoaded", init);

// 초기화 함수
function init() {
  redirectHomeOnLogin();
  renderNotices();
  setupEventListeners();
}

// 이벤트 핸들러 설정
function setupEventListeners() {
  if (loginForm) {
    loginForm.addEventListener("submit", login);
  }
}

// 공지사항 목록 표시
async function renderNotices() {
  const { notices } = await getNotices();
  const noticeList = document.getElementById("noticeList");

  if (noticeList) {
    noticeList.innerHTML = "";

    notices.forEach((notice) => {
      const row = document.createElement("li");

      row.innerHTML = `
        <span class="notice-post-date">${convertDate(notice.postDate)}</span>
        <span class="notice-title ellipsis">${notice.title}</span>
      `;

      noticeList.appendChild(row);
    });
  }
}

async function login(event) {
  event.preventDefault(); // 폼 제출 기본 동작 방지

  const formData = new FormData(event.target);
  const formDataObj = Object.fromEntries(formData.entries());

  try {
    showSpinner();

    const response = await tmsFetch("/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(formDataObj),
    });

    const success = response.status === "success";
    if (success) window.location.href = "/tms/dashboard"; // 대시보드로 이동
  } catch (error) {
    alert(error.message);
  } finally {
    hideSpinner();
  }
}
