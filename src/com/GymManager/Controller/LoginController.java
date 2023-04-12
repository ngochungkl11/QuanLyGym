package com.GymManager.Controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.GymManager.Entity.AccountEntity;
import com.GymManager.Entity.ClassEntity;
import com.GymManager.Entity.PTEntity;
import com.GymManager.Entity.RegisterEntity;
import com.GymManager.ExtraClass.GymLimit;
import com.GymManager.ExtraClass.Message;
import com.GymManager.ExtraClass.RandomPassword;
import com.GymManager.Serializer.LoginRequest;
import com.GymManager.Service.LoginService;

@Controller
@Transactional
public class LoginController {
	@Autowired
	SessionFactory factory;
	@Autowired
	public JavaMailSender mailer;

	@RequestMapping(value = "admin/login", method = RequestMethod.GET)
	public String adminLogin(ModelMap model) {
		model.addAttribute("loginRequest", new LoginRequest());
		return "admin/login";
	}

	@RequestMapping(value = "admin/login", method = RequestMethod.POST, params = "btnLogin")
	public String handleLogin(ModelMap model, HttpSession ss, HttpServletRequest request) {
		Session session = factory.getCurrentSession();
		String password = DigestUtils.md5Hex(request.getParameter("password")).toUpperCase();
		String hql = "FROM AccountEntity WHERE (policyId = '0' or policyId = '2') AND username = '"
				+ request.getParameter("username") + "' AND password = '" + password + "'";

		Query query = session.createQuery(hql);
		if (query.list().size() > 0) {
			AccountEntity account = (AccountEntity) query.list().get(0);
			if (account.getStatus() == 0) {
				model.addAttribute("message", new Message("error", "Tài khoản này đã bị khoá"));
				return "admin/login";
			}
			if (account.getStatus() == 2) {
				model.addAttribute("message", new Message("error", "Đổi mật khẩu để tiếp tục"));
				model.addAttribute("userName", request.getParameter("username"));
				return "admin/change-password";
			}
			ss.setAttribute("admin", account);
		} else {
			model.addAttribute("matKhau", "Tài khoản hoặc mật khẩu không đúng");
			model.addAttribute("userName", request.getParameter("username"));
			model.addAttribute("password", request.getParameter("password"));

			ss.removeAttribute("admin");
			return "admin/login";
		}
		GymLimit gymLimit = new GymLimit();
		List<RegisterEntity> registerEntities1 = getExpireRegister(gymLimit.getExpiredRegister());
		for (RegisterEntity registerEntity : registerEntities1) {
			String mailMessage = "Hợp hợp đồng đăng ký PTITGYM với mã " + registerEntity.getRegisterId()
					+ " của bạn chỉ còn 1 ngày là hết hạn, vui lòng đến trung tâm để thanh toán trong hôm nay";
			try {
				MimeMessage mail = mailer.createMimeMessage();
				MimeMessageHelper helper = new MimeMessageHelper(mail, true);
				helper.setFrom("nguyenminhnhat301101@gmail.com", "PTITGYM");
				helper.setTo(registerEntity.getCustomer().getEmail());
				helper.setReplyTo("nguyenminhnhat301101@gmail.com");
				helper.setSubject("Thanh toán hợp đồng đăng ký PTITGYM");
				helper.setText(mailMessage);
				mailer.send(mail);
			} catch (Exception e) {
				// TODO: handle exception
			}

		}

		List<RegisterEntity> registerEntities2 = getExpireRegister2(gymLimit.getExpiredRegister());

		if (ss.getAttribute("cancelRegisters") != null) {
			List<RegisterEntity> registerEntitiesSS = (List<RegisterEntity>) ss.getAttribute("cancelRegisters");
			for (RegisterEntity registerEntity : registerEntities2) {
				registerEntitiesSS.add(registerEntity);

			}
			ss.setAttribute("cancelRegisters", registerEntitiesSS);
		} else
			ss.setAttribute("cancelRegisters", registerEntities2);
		for (RegisterEntity registerEntity : registerEntities2) {
			updateStatusRegister(registerEntity, 2);
			String mailMessage = "Hợp hợp đồng đăng ký PTITGYM với mã " + registerEntity.getRegisterId()
					+ " của bạn đã bị huỷ do hết hạn thanh toán";
			try {
				MimeMessage mail = mailer.createMimeMessage();
				MimeMessageHelper helper = new MimeMessageHelper(mail, true);
				helper.setFrom("nguyenminhnhat301101@gmail.com", "PTITGYM");
				helper.setTo(registerEntity.getCustomer().getEmail());
				helper.setReplyTo("nguyenminhnhat301101@gmail.com");
				helper.setSubject("Huỷ hợp đồng đăng ký PTITGYM");
				helper.setText(mailMessage);
				mailer.send(mail);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		updatePTStatus();

		return "redirect:/admin/customer.htm";
	}

	@RequestMapping(value = "admin/login", method = RequestMethod.POST, params = "btnForgetPass")
	public String handleFogetPass(ModelMap model, HttpSession ss, HttpServletRequest request) {
		String userName = request.getParameter("username");
		AccountEntity account = getAccount(userName);
		if (account == null) {
			model.addAttribute("message", new Message("error", "Tài khoản không tồn tại"));
			return "admin/login";
		} else {
			if (account.getStatus() == 0) {
				model.addAttribute("message", new Message("error", "Tài khoản này đã bị khoá"));
				return "admin/login";
			} else {
				Session session = factory.openSession();
				Transaction t = session.beginTransaction();

				try {
					RandomPassword radomPassword = new RandomPassword(8);
					String newPassword = DigestUtils.md5Hex(radomPassword.getPassword()).toUpperCase();
					account.setPassword(newPassword);
					account.setStatus(2);
					session.merge(account);
					t.commit();
					String mailMessage = "Mật khẩu mới cho tài khoản PTITGYM của bạn là: "
							+ radomPassword.getPassword();
					MimeMessage mail = mailer.createMimeMessage();
					MimeMessageHelper helper = new MimeMessageHelper(mail, true);
					helper.setFrom("nguyenminhnhat301101@gmail.com", "PTITGYM");
					helper.setTo(account.getStaff().getEmail());
					helper.setReplyTo("nguyenminhnhat301101@gmail.com");
					helper.setSubject("Tai khoản PTITGYM");
					helper.setText(mailMessage);
					mailer.send(mail);
					model.addAttribute("message", new Message("success",
							"Đặt lại mật khẩu thành công, kiểm trả email của bạn để nhận mật khẩu !!!"));
					return "admin/login";
				} catch (Exception e) {
					t.rollback();
					System.out.println(e.getCause());

				} finally {
					session.close();
				}

			}
		}

		return "admin/login";
	}

	@RequestMapping(value = "admin/login", method = RequestMethod.POST, params = "btnChangePassword")
	public String handleChangePassword(ModelMap model, HttpSession ss, HttpServletRequest request) {

		String newPassword = request.getParameter("newPassword");
		String reNewPassword = request.getParameter("reNewPassword");

		if (newPassword.trim().isEmpty()) {
			model.addAttribute("message1", "Nội dung không được để trống!");
			return "admin/change-password";
		} else {
			if (newPassword.equals(reNewPassword) == false) {
				model.addAttribute("message2", "Mật khẩu không trùng khớp!");
				return "admin/change-password";
			} else {
				String userName = request.getParameter("username");
				AccountEntity account = getAccount(userName);
				Session session = factory.openSession();
				Transaction t = session.beginTransaction();
				try {
					String password = DigestUtils.md5Hex(newPassword).toUpperCase();
					account.setPassword(password);
					account.setStatus(1);
					session.merge(account);
					t.commit();
					model.addAttribute("message", new Message("success",
							"Cập nhật mật khẩu thành công, hãy sử dụng mật khẩu mới để đăng nhập !!!"));
					return "admin/login";
				} catch (Exception e) {

					t.rollback();
					System.out.println(e.getCause());

				} finally {
					session.close();
				}
			}
		}

		return "admin/login";
	}

	@RequestMapping(value = "admin/logout")
	public String handleLogout(HttpSession ss) {
		ss.removeAttribute("admin");
		return "admin/login";
	}

	public AccountEntity getAccount(String userName) {
		Session session = factory.getCurrentSession();
		return (AccountEntity) session.get(AccountEntity.class, userName);
	}

	public List<RegisterEntity> getExpireRegister(int num) {
		Session session = factory.getCurrentSession();
		String hql = "FROM RegisterEntity where DATEDIFF(day, registerDate, getdate()) =" + num + " and status = 0";
		Query query = session.createQuery(hql);
		List<RegisterEntity> list = query.list();
		return list;
	}

	public List<RegisterEntity> getExpireRegister2(int num) {
		Session session = factory.getCurrentSession();
		String hql = "FROM RegisterEntity where DATEDIFF(day, registerDate, getdate()) >" + num + " and status = 0";
		Query query = session.createQuery(hql);
		List<RegisterEntity> list = query.list();
		return list;
	}

	public boolean updateStatusRegister(RegisterEntity registerEntity, int status) {
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		try {
			registerEntity.setStatus(status);
			session.merge(registerEntity);
			t.commit();
		} catch (Exception e) {
			t.rollback();
			System.out.println(e.getCause());
			return false;
		} finally {
			session.close();
		}
		return true;
	}

	public List<ClassEntity> getAllClass() {
		Session session = factory.getCurrentSession();
		String hql = "FROM ClassEntity where maxPP > 1";
		Query query = session.createQuery(hql);
		List<ClassEntity> list = query.list();
		return list;
	}

	public boolean updatePTStatus() {
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		try {
			for (ClassEntity classEntity : getAllClass()) {
				if (classEntity.getClassPeriod() == 2 && classEntity.getMaxPP() > 1
						&& classEntity.getPtEntity().getStatus() == 1) {
					PTEntity ptEntity = classEntity.getPtEntity();
					ptEntity.setStatus(0);
					System.out.println(ptEntity.getPtName());
					session.update(ptEntity);
				}
			}
			t.commit();

		} catch (Exception e) {
			// TODO: handle exception
			t.rollback();
		} finally {
			session.close();
		}

		return true;
	}

}
