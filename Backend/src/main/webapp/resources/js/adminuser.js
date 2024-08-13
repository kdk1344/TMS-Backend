import { tmsFetch } from "./common.js";

let currentPage = 1;

// DOM 요소들
const userRegisterForm = document.getElementById("userRegisterForm");
const userEditForm = document.getElementById("userEditForm");
const userFilterForm = document.getElementById("userFilterForm");
const userTableBody = document.getElementById("userTableBody");
const userPagination = document.getElementById("userPagination");
const selectAllUserCheckbox = document.getElementById("selectAllUserCheckbox");
const openUserRegisterModalButton = document.getElementById("openUserRegisterModalButton");
const closeUserRegisterModalButton = document.getElementById("closeUserRegisterModalButton");
const closeUserEditModalButton = document.getElementById("closeUserEditModalButton");
const deleteUserButton = document.getElementById("deleteUserButton");

// 모달 아이디
const MODAL_ID = {
  USER_REGISTER: "userRegisterModal",
  USER_EDIT: "userEditModal",
};

// 초기화 함수
function init() {
  setupEventListeners();
  loadInitialUsers();
}

// 이벤트 핸들러 설정
function setupEventListeners() {
  // 사용자 테이블에서 클릭된 행의 데이터 로드 (이벤트 위임)
  if (userTableBody) {
    userTableBody.addEventListener("click", (event) => {
      const clickedElement = event.target;

      if (clickedElement.type === "checkbox") {
        return;
      }

      const row = event.target.closest("tr");

      if (row) {
        loadUserDataFromRow(row);
      }
    });
  }

  // 사용자 수정 폼 제출 이벤트 핸들러
  if (userEditForm) {
    userEditForm.addEventListener("submit", editUser);
  }

  // 사용자 등록 폼 제출 이벤트 핸들러
  if (userRegisterForm) {
    userRegisterForm.addEventListener("submit", registerUser);
  }

  // 사용자 필터 폼 제출 및 리셋 이벤트 핸들러
  if (userFilterForm) {
    userFilterForm.addEventListener("submit", submitUserFilter);
    userFilterForm.addEventListener("reset", resetUserFilter);
  }

  // 사용자 삭제 버튼 클릭 이벤트 핸들러
  if (deleteUserButton) {
    deleteUserButton.addEventListener("click", deleteUser);
  }

  // 전체 선택 체크박스 클릭 이벤트 핸들러
  if (selectAllUserCheckbox) {
    selectAllUserCheckbox.addEventListener("click", toggleAllCheckboxes);
  }

  // 모달 열기 및 닫기 버튼 이벤트 핸들러
  if (openUserRegisterModalButton && closeUserRegisterModalButton && closeUserEditModalButton) {
    openUserRegisterModalButton.addEventListener("click", () => openModal(MODAL_ID.USER_REGISTER));
    closeUserRegisterModalButton.addEventListener("click", () => closeModal(MODAL_ID.USER_REGISTER));
    closeUserEditModalButton.addEventListener("click", () => closeModal(MODAL_ID.USER_EDIT));
  }

  // 모달 외부 클릭 시 닫기 버튼 이벤트 핸들러
  setupModalEventListeners();
}

function setupModalEventListeners() {
  const modals = [MODAL_ID.USER_REGISTER, MODAL_ID.USER_EDIT]; // 모든 모달 ID 배열

  modals.forEach((modalId) => {
    // 모달 외부 클릭 시 닫기 설정
    window.addEventListener("click", (event) => closeModalOnClickOutside(event, modalId));
  });
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
        <td class="user-id">${user.userID}</td>
        <td class="user-name">${user.userName}</td>
        <td class="user-authority-name">${user.authorityName}</td>
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

// HTML 요소에서 사용자 정보를 가져와서 사용자 수정 폼에 미리 채우기
function loadUserDataFromRow(row) {
  const userID = row.querySelector(".user-id").textContent.trim();
  const userName = row.querySelector(".user-name").textContent.trim();
  const authorityName = row.querySelector(".user-authority-name").textContent.trim();

  // 폼에 사용자 정보 입력
  document.getElementById("userIDForEdit").value = userID;
  document.getElementById("userNameForEdit").value = userName;

  const userAuthoritySelect = document.getElementById("authorityNameForEdit");

  for (let option of userAuthoritySelect.options) {
    if (option.value.trim() === authorityName) {
      option.selected = true;
      break;
    }
  }

  // 비밀번호는 보안상 빈칸으로 둠
  document.getElementById("passwordForEdit").value = "";

  // 모달 열기
  openModal(MODAL_ID.USER_EDIT);
}

// 사용자 등록
async function registerUser(event) {
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
      closeModal(MODAL_ID.USER_REGISTER); // 모달 닫기
    } else {
      alert("사용자 등록에 실패했습니다. 다시 시도해주세요.");
    }
  } catch (error) {
    console.error("Error submitting user register form:", error);
    alert("서버 오류가 발생했습니다.");
  }
}

// 사용자 수정
async function editUser(event) {
  event.preventDefault(); // 폼 제출 기본 동작 방지

  // 사용자 정보 가져와서 폼에 넣기

  const formData = new FormData(event.target);

  // FormData 객체를 JSON 객체로 변환
  const formDataObj = Object.fromEntries(formData.entries());

  try {
    const { user, status } = await tmsFetch("/idmodify", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(formDataObj),
    });

    const success = status === "success";

    if (success) {
      alert(`사용자(ID: ${user.userID}) 수정이 완료되었습니다.`);
      event.target.reset(); // 폼 초기화
      closeModal(MODAL_ID.USER_EDIT); // 모달 닫기
      location.reload(); // 페이지 새로고침
    } else {
      alert("사용자 수정에 실패했습니다. 다시 시도해주세요.");
    }
  } catch (error) {
    console.error("Error submitting user edit form:", error);
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
function openModal(modalId) {
  const modal = document.getElementById(modalId);

  if (modal) {
    modal.style.display = "block";
  } else {
    console.error(`Modal with ID "${modalId}" not found.`);
  }
}

// 모달 닫기
function closeModal(modalId) {
  const modal = document.getElementById(modalId);

  if (modal) {
    modal.style.display = "none";
  } else {
    console.error(`Modal with ID "${modalId}" not found.`);
  }
}

// 모달 외부 클릭 시 닫기
function closeModalOnClickOutside(event, modalId) {
  const modal = document.getElementById(modalId);

  if (modal && event.target === modal) {
    closeModal(modalId);
  }
}

// 초기 사용자 목록 로드
function loadInitialUsers() {
  getUsers();
}

// 문서 로드 시 초기화
document.addEventListener("DOMContentLoaded", init);
