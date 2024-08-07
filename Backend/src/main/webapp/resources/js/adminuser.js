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
