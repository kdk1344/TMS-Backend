import { tmsFetch, showSpinner, hideSpinner } from "./common.js";

// DOM 요소
const loginForm = document.getElementById("loginForm");

// 문서 로드 시 초기화
document.addEventListener("DOMContentLoaded", init);

// 초기화 함수
function init() {
  setupEventListeners();
}

// 이벤트 핸들러 설정
function setupEventListeners() {
  if (loginForm) {
    loginForm.addEventListener("submit", login);
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

    if (success) {
        window.location.href = "/tms/dashboard"; // 대시보드로 이동
    }
  } catch (error) {
    alert(error.message + "\n다시 시도해주세요.");
  } finally {
    hideSpinner();
  }
}
