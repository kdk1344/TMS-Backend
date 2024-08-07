// 사용자 등록 모달
const userRegisterModal = document.getElementById("userRegisterModal");
const openModalButton = document.getElementById("openModalButton");
const closeModalButton = document.getElementsById("closeModalButton");

// 버튼 클릭 시 모달 열기
openModalButton.onclick = function () {
    userRegisterModal.style.display = "block";
}

// 닫기 버튼 클릭 시 모달 닫기
closeModalButton.onclick = function () {
    userRegisterModal.style.display = "none";
}

// 모달 바깥 클릭 시 모달 닫기
window.onclick = function (event) {
    if (event.target == userRegisterModal) {
        userRegisterModal.style.display = "none";
    }
}