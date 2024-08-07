<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
	<!DOCTYPE html>
	<html lang="ko">

	<head>
		<%@ include file="./common.jsp" %>
			<meta charset="UTF-8">
			<link rel="stylesheet" type="text/css" href="../../resources/css/login.css">

			<title>Test Management System</title>
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
				<button type="submit">Login12345</button>
			</form>
		</div>
	</body>

	</html>