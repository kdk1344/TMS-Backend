<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Test Management System</title>
    <style>
        body {
            background-color: white;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
            font-family: Arial, sans-serif;
            color: white;
        }
        .login-container {
            text-align: center;
        }
        .content {
            font-size: 24px;
            font-weight: normal;
            color : #E2E2E2;
        }
        .login-container input {
            display: block;
            margin: 10px auto;
            padding: 10px;
            width: 200px;
            border: 1px solid #1c1c1c;
            border-radius: 5px;
            margin-right: 20px;
        }
        .login-container button {
            background-color: #337ab7;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }
        .login-container button:hover {
            background-color: #286090;
        }
        .sidebar {
            display: flex;
            justify-content: center;
            align-items: center;
            margin-bottom: 20px;
        }
        .logo {
            font-size: 48px;
            font-weight: bold;
            color: black;
            margin-bottom: 20px;
            margin-right: 20px;
        }
        .user {
        	font-size: 24px;
        	color: black;
        }
    </style>
</head>
<body>
    <div class="login-container">
        <div class="sidebar">
	        <div class="logo">TMS</div>
		    <div class="content">Test Management System</div>
	    </div>
        <form action="login" method="post">
        	<div class="sidebar">
        		<div class="user">User</div>
           		<input type="text" name="userID" placeholder="ID" required>
           	</div>
           	<div class="sidebar">
           		<div class="user">Password</div>
           		<input type="password" name="password" placeholder="Password" required>
         	</div>
            <button type="submit">Login</button>
        </form>
    </div>
</body>
</html>