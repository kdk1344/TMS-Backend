import { tmsFetch } from "./common.js";

let currentPage = 1;

// DOM 요소들
const userRegisterForm = document.getElementById("userRegisterForm");
const userFilterForm = document.getElementById("userFilterForm");
const userTableBody = document.getElementById("userTableBody");
const userPagination = document.getElementById("userPagination");
const selectAllUserCheckbox = document.getElementById("selectAllUserCheckbox");
const userRegisterModal = document.getElementById("userRegisterModal");
const openModalButton = document.getElementById("openModalButton");
const closeModalButton = document.getElementById("closeModalButton");
const deleteUserButton = document.getElementById("deleteUserButton");

// 초기화 함수
function init() {
  setupEventListeners();
  loadInitialUsers();
}

// 이벤트 핸들러 설정
function setupEventListeners() {
  if (userRegisterForm) {
    userRegisterForm.addEventListener("submit", handleUserRegisterFormSubmit);
  }

  if (userFilterForm) {
    userFilterForm.addEventListener("submit", submitUserFilter);
    userFilterForm.addEventListener("reset", resetUserFilter);
  }

  if (selectAllUserCheckbox) {
    selectAllUserCheckbox.addEventListener("click", toggleAllCheckboxes);
  }

  if (openModalButton && closeModalButton) {
    openModalButton.addEventListener("click", openModal);
    closeModalButton.addEventListener("click", closeModal);
  }

  window.addEventListener("click", closeModalOnClickOutside);

  if (deleteUserButton) {
    deleteUserButton.addEventListener("click", deleteUser);
  }
}

/**
 * 사용자 데이터를 API에서 비동기적으로 가져옵니다.
 * 이 함수는 선택적 필터와 페이지 번호를 사용하여 사용자 목록을 가져오고 표시합니다.
 *
 * @param {Object} params - 사용자 데이터를 가져오기 위한 필터 및 페이지 정보.
 * @param {number} [params.page=1] - 가져올 페이지 번호 (기본값: 1).
 * @param {string} [params.userName=""] - 사용자 이름으로 필터링할 문자열 (기본값: 빈 문자열).
 * @param {string} [params.authorityName=""] - 권한 이름으로 필터링할 문자열 (기본값: 빈 문자열).
 *
 * @throws {Error} API 요청 실패 시 오류를 발생시킵니다.
 *
 * @returns {Promise<void>} 이 함수는 결과를 반환하지 않습니다. 성공적으로 데이터를 가져온 후,
 *                          `displayUsers` 함수를 호출하여 사용자 목록을 페이지에 표시합니다.
 */
async function getUsers({ page = 1, userName = "", authorityName = "" } = {}) {
  try {
    const query = new URLSearchParams({ page, userName, authorityName }).toString();
    const { totalPages, userList: users } = await tmsFetch(`/users?${query}`);

    displayUsers(users, totalPages);
  } catch (error) {
    console.error("Error fetching users:", error);
  }
}

// 사용자 표시
function displayUsers(users, totalPages) {
  if (userTableBody) {
    userTableBody.innerHTML = "";

    users.forEach((user) => {
      const row = document.createElement("tr");
      row.innerHTML = `
        <td><input type="checkbox" name="user" value="${user.userID}"></td>
        <td>${user.userID}</td>
        <td>${user.userName}</td>
        <td>${user.authorityName}</td>
      `;
      userTableBody.appendChild(row);
    });

    setupPagination(totalPages);
  }
}

// 페이지네이션 설정
function setupPagination(totalPages) {
  if (userPagination) {
    userPagination.innerHTML = "";

    createPaginationButton("<", currentPage <= 1, () => changePage(currentPage - 1), "prev");

    for (let i = 1; i <= totalPages; i++) {
      createPaginationButton(i, i === currentPage, () => changePage(i));
    }

    createPaginationButton(">", currentPage >= totalPages, () => changePage(currentPage + 1), "next");
  }
}

// 페이지 버튼 생성
function createPaginationButton(text, disabled, onClick, buttonType = "page") {
  const button = document.createElement("button");
  button.textContent = text;
  button.disabled = disabled;

  // buttonType에 따라 클래스 추가
  if (buttonType === "page") {
    button.classList.toggle("active", disabled); // 현재 페이지 표시할 클래스 추가
  }

  button.addEventListener("click", onClick);
  userPagination.appendChild(button);
}

// 페이지 변경
function changePage(page) {
  currentPage = page;
  getUsers({ page: currentPage });
}

// 사용자 필터링
function submitUserFilter(event) {
  event.preventDefault(); // 폼 제출 기본 동작 방지

  const userName = document.getElementById("userNameForFilter").value.trim();
  const authorityName = document.getElementById("authorityNameForFilter").value;

  // 페이지를 1로 초기화하고 getUsers 호출
  currentPage = 1;
  getUsers({ page: currentPage, userName, authorityName });
}

// 사용자 필터 리셋
function resetUserFilter() {
  this.reset(); // 폼 초기화
}

// 사용자 등록
async function handleUserRegisterFormSubmit(event) {
  event.preventDefault(); // 폼 제출 기본 동작 방지

  const confirmed = confirm("사용자를 등록하시겠습니까?");

  if (!confirmed) return;

  const formData = new FormData(event.target);

  // FormData 객체를 JSON 객체로 변환
  const formDataObj = Object.fromEntries(formData.entries());

  try {
    const { user, status } = await tmsFetch("/join", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(formDataObj),
    });

    const success = status === "success";

    if (success) {
      alert(`사용자(ID: ${user.userID}) 등록이 완료되었습니다.`);
      event.target.reset(); // 폼 초기화
      closeModal(); // 모달 닫기
    } else {
      alert("사용자 등록에 실패했습니다. 다시 시도해주세요.");
    }
  } catch (error) {
    console.error("Error submitting user register form:", error);
    alert("서버 오류가 발생했습니다.");
  }
}

// 체크박스 전체 선택/해제
function toggleAllCheckboxes() {
  const checkboxes = document.querySelectorAll('input[name="user"]');
  checkboxes.forEach((checkbox) => (checkbox.checked = selectAllUserCheckbox.checked));
}

// 사용자 삭제
async function deleteUser() {
  try {
    const confirmed = confirm("사용자를 삭제하시겠습니까?");

    if (!confirmed) return;

    const selectedUserIDs = Array.from(document.querySelectorAll('input[name="user"]:checked')).map(
      (checkbox) => checkbox.value
    );

    if (selectedUserIDs.length === 0) {
      alert("삭제할 사용자를 선택해주세요.");
      return;
    }

    const { status } = await tmsFetch("/deleteuser", {
      method: "DELETE",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(selectedUserIDs),
    });

    const success = status === "success";

    if (success) {
      alert(`사용자 삭제가 완료되었습니다.`);
      location.reload(); // 페이지 새로고침
    } else {
      alert("사용자 삭제에 실패했습니다. 다시 시도해주세요.");
    }
  } catch (error) {
    console.error("Error deleting users:", error);
  }
}

// 모달 열기
function openModal() {
  if (userRegisterModal) {
    userRegisterModal.style.display = "block";
  }
}

// 모달 닫기
function closeModal() {
  if (userRegisterModal) {
    userRegisterModal.style.display = "none";
  }
}

// 모달 외부 클릭 시 닫기
function closeModalOnClickOutside(event) {
  if (userRegisterModal && event.target === userRegisterModal) {
    closeModal();
  }
}

// 초기 사용자 목록 로드
function loadInitialUsers() {
  getUsers();
}

// 문서 로드 시 초기화
document.addEventListener("DOMContentLoaded", init);
