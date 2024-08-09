import { tmsFetch } from "./common.js";

let currentPage = 1;

/**
 * Fetches user data from the API with optional filters.
 * @param {Object} params - The parameters for fetching users.
 * @param {number} [params.page=1] - The page number to fetch.
 * @param {string} [params.userName] - Optional user name filter.
 * @param {string} [params.authorityName] - Optional authority name filter.
 */
async function getUsers({ page = 1, userName = "", authorityName = "" } = {}) {
  try {
    const query = new URLSearchParams({
      page: page,
      userName: userName,
      authorityName: authorityName,
    }).toString();

    const users = await tmsFetch(`/users?${query}`);

    displayUsers(users);
  } catch (error) {
    console.error("Error fetching users:", error);
  }
}

function displayUsers(users) {
  const userTableBody = document.getElementById("userTableBody");

  userTableBody.innerHTML = ""; // 기존 사용자 목록 제거

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

  /** @todo 서버에서 총 페이지 개수 받아와야함 */
  // setupPagination(users.totalPages);
  setupPagination(10);
}

// 초기화 버튼 클릭 시 폼 리셋
const userFilterForm = document.getElementById("userFilterForm");

function submitUserFilter(event) {
  event.preventDefault(); // 폼 제출 기본 동작 방지

  const userName = document.getElementById("userNameForFilter").value.trim();
  const authorityName = document.getElementById("authorityNameForFilter").value;

  // 페이지를 1로 초기화하고 getUsers 호출
  currentPage = 1;
  getUsers({ page: currentPage, userName, authorityName });
}

function resetUserFilter() {
  // 폼 초기화
  this.reset();
}

userFilterForm.addEventListener("submit", submitUserFilter);
userFilterForm.addEventListener("reset", resetUserFilter);

function setupPagination(totalPages) {
  const userPagination = document.getElementById("userPagination");

  userPagination.innerHTML = ""; // 기존 페이지네이션 버튼 제거

  // 이전 페이지 버튼
  const prevButton = document.createElement("button");

  prevButton.textContent = "<";
  prevButton.classList.add("prev");
  prevButton.disabled = currentPage <= 1;
  userPagination.appendChild(prevButton);

  prevButton.onclick = () => {
    changePage(currentPage - 1);
  };

  for (let i = 1; i <= totalPages; i++) {
    const pageButton = document.createElement("button");

    pageButton.textContent = i;

    if (i === currentPage) pageButton.classList.add("active"); // 현재 페이지를 나타내는 버튼 클래스 추가

    pageButton.onclick = () => {
      changePage(i);
    };

    userPagination.appendChild(pageButton);
  }

  // 다음 페이지 버튼
  const nextButton = document.createElement("button");

  nextButton.textContent = ">";
  nextButton.classList.add("next");
  nextButton.disabled = currentPage >= totalPages;

  nextButton.onclick = () => {
    changePage(currentPage + 1);
  };

  userPagination.appendChild(nextButton);
}

function changePage(page) {
  currentPage = page;

  getUsers({ page: currentPage });
}

const selectAllUserCheckbox = document.getElementById("selectAllUserCheckbox");
selectAllUserCheckbox.onclick = toggleAllCheckboxes;

function toggleAllCheckboxes() {
  const checkboxes = document.querySelectorAll('input[name="user"]');

  checkboxes.forEach((checkbox) => (checkbox.checked = selectAllUserCheckbox.checked));
}

// 사용자 목록 초기 데이터 로드
window.onload = function () {
  getUsers();
};

// 사용자 등록 모달
const userRegisterModal = document.getElementById("userRegisterModal");
const openModalButton = document.getElementById("openModalButton");
const closeModalButton = document.getElementById("closeModalButton");

openModalButton.onclick = function () {
  userRegisterModal.style.display = "block";
};

closeModalButton.onclick = function () {
  userRegisterModal.style.display = "none";
};

// 모달 외부 영역 클릭 시 모달 닫기
window.onclick = function (event) {
  if (event.target === userRegisterModal) {
    userRegisterModal.style.display = "none";
  }
};
