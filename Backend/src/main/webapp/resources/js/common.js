// common.js
export const getUsers = async () => {
  try {
    const response = await fetch("/tms/api/users");
    console.log(response);
    // const users = await response.json();
    
    // displayUsers(users);
  } catch (error) {
    console.error("Error fetching users:", error);
  }
};

export const displayUsers = (users) => {
  const userList = document.getElementById("userList");
  userList.innerHTML = "";
  users.forEach((user) => {
    const li = document.createElement("li");
    li.textContent = `${user.name} (${user.email})`;
    userList.appendChild(li);
  });
};

window.onload = getUsers;