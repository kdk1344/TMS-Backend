import { tmsFetch } from "./common.js";

/**
 * Fetches user data from the API with optional filters.
 * @param {Object} params - The parameters for fetching users.
 * @param {number} [params.page=1] - The page number to fetch.
 * @param {string} [params.userName] - Optional user name filter.
 * @param {string} [params.authorityName] - Optional authority name filter.
 */
export const getUsers = async ({ page = 1, userName = "", authorityName = "" }) => {
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
};

export const displayUsers = (users) => {
  const userList = document.getElementById("userList");
  userList.innerHTML = "";
  users.forEach((user) => {
    const li = document.createElement("li");
    li.textContent = `${user.userName} (${user.authorityName})`;
    userList.appendChild(li);
  });
};

window.onload = getUsers;

// 사용자 등록 모달
const userRegisterModal = document.getElementById("userRegisterModal");
const openModalButton = document.getElementById("openModalButton");
const closeModalButton = document.getElementById("closeModalButton");

openModalButton.onclick = () => {
  userRegisterModal.style.display = "block";
};

closeModalButton.onclick = () => {
  userRegisterModal.style.display = "none";
};

// 모달 외부 영역 클릭 시 모달 닫기
window.onclick = (event) => {
  if (event.target === userRegisterModal) {
    userRegisterModal.style.display = "none";
  }
};
