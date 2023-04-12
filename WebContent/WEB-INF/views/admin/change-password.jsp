<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<%@include file="./head.jsp"%>
</head>
<body>
	<div class="alert-flag" aType='${message.type}'
		aMessage="${message.message }"></div>
	<main>
		<div class="container">
			<section
				class="section register min-vh-100 d-flex flex-column align-items-center justify-content-center py-4">
				<div class="container">
					<div class="row justify-content-center">
						<div
							class="col-lg-4 col-md-6 d-flex flex-column align-items-center justify-content-center">
							<div class="d-flex justify-content-center py-4">
								<a href="index.html"
									class="logo d-flex align-items-center w-auto"> <img
									src="assets/img/logo.png" alt="" /> <span
									class="d-none d-lg-block">Quản lý PTIT GYM</span>
								</a>
							</div>
							<!-- End Logo -->

							<div class="card mb-3">
								<div class="card-body">
									<div class="pt-4 pb-2">
										<h5 class="card-title text-center pb-0 fs-4">Cập nhật mật
											khẩu</h5>
										<p class="text-center small">Do đây là lần đăng nhập đầu
											tiên hoặc mật khẩu của bạn đã được đặt lại, vui lòng cập nhật
											mật khẩu mới để đăng nhập</p>
									</div>

									<form action="admin/login.htm" method="post"
										class="row g-3 needs-validation" novalidate>
										<div class="col-12 ">
											<div class="col-12 invisible position-absolute">
												<label for="yourUsername" class="form-label ">Tên
													đăng nhập</label> <input type="text" name="username"
													value="${userName}" class="form-control" id="yourUsername"
													required />
												<div class="invalid-feedback">Không được bỏ trống</div>
											</div>
											<label for="yourPassword" class="form-label">Mật khẩu
												mới</label> <input type="password" name="newPassword"
												value="${password}" class="form-control" required />
											<div class="text-danger">${matKhau}</div>
										</div>

										<div class="col-12">
											<label for="yourPassword" class="form-label">Xác nhận
												mật khẩu</label> <input type="password" name="reNewPassword"
												value="${password}" class="form-control" required />
											<div class="text-danger">${matKhau}</div>
										</div>

										<div class="col-12">
											<button name="btnChangePassword"
												class="btn btn-primary w-100" type="submit">Login</button>
										</div>
									</form>
								</div>
							</div>
						</div>
					</div>
				</div>
			</section>
		</div>
	</main>
	<!-- End #main -->
	<!-- common script -->
	<%@include file="./script.jsp"%>
</body>
</html>