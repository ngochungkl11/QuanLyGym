package com.GymManager.Controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.GymManager.Entity.StaffEntity;
import com.GymManager.Entity.StaffEntity;
import com.GymManager.Entity.AccountEntity;
import com.GymManager.Entity.CustomerEntity;
import com.GymManager.Entity.RegisterEntity;
import com.GymManager.Entity.StaffEntity;
import com.GymManager.Entity.TrainingPackEntity;
import com.GymManager.ExtraClass.FormAttribute;
import com.GymManager.ExtraClass.Message;
import com.GymManager.ExtraClass.RandomPassword;

@Controller
@RequestMapping("admin/employee")
@Transactional
public class EmployeeController extends MethodAdminController {
	@Autowired
	SessionFactory factory;
	@Autowired
	public JavaMailSender mailer;

	@RequestMapping(method = RequestMethod.GET)
	public String index(ModelMap model) {
		StaffEntity staff = newStaff();
		staff.setStatus(1);
		model.addAttribute("staff", staff);
		model.addAttribute("cFormAttribute",
				new FormAttribute("Them moi nhan vien", "admin/employee.htm", "btnCreate"));
		model.addAttribute("cList", getAllStaff());
		return "admin/employee";
	}

	// detail
	@RequestMapping(value = "detail/{id}.htm", method = RequestMethod.GET)
	public String getDetail(ModelMap model, @PathVariable("id") String id) {
		model.addAttribute("staff", newStaff());
		model.addAttribute("staffDetail", getStaff(id));
		model.addAttribute("idModal", "modal-detail");
		model.addAttribute("cList", getAllStaff());
		return "admin/employee";

	}

	// get view create staff

	@RequestMapping(value = "add.htm", method = RequestMethod.GET)
	public String getCreate(ModelMap model, RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("idModal", "modal-create");
		return "redirect:/admin/employee.htm";

	}

	// create staff
	@RequestMapping(method = RequestMethod.POST, params = "btnCreate")
	public String createStaff(ModelMap model, @Validated @ModelAttribute("staff") StaffEntity staff,
			BindingResult result, RedirectAttributes redirectAttributes, HttpServletRequest request) {
//			System.out.println(staff.getStaffId());
		if (!result.hasErrors()) {
			Session session = factory.openSession();

			Transaction t = session.beginTransaction();
			try {

				session.save(staff);

				t.commit();
				redirectAttributes.addFlashAttribute("message", new Message("success", "Thêm mới thành công !!!"));

				return "redirect:/admin/employee.htm";

			} catch (Exception e) {

				t.rollback();
				System.out.println(e);

				if (e.getCause().toString().contains("UNIQUE_NHANVIEN_SDT")) {
					result.rejectValue("phone", "staff", "số điện thoại này đã được sử dụng");
				}

				if (e.getCause().toString().contains("UCHECK_NHANVIEN_SDT")) {
					result.rejectValue("phone", "staff", "số điện thoại không đúng định dạng");
				}
				if (e.getCause().toString().contains("CK_NHANVIEN_NGAYSINH")) {
					result.rejectValue("birthday", "staff", "Tuổi nhân viên phải trên 18 ");
				}
				if (e.getCause().toString().contains("UNIQUE_NHANVIEN_EMAIL")) {
					result.rejectValue("email", "staff", "Email đã được sử dụng");
				}
				if (e.getCause().toString().contains("UNIQUE_NHANVIEN_CMND")) {
					result.rejectValue("identityCard", "pt", "CMND đã tồn tại");
				}
				if (e.getCause().toString().contains("String or binary data would be truncated")) {
					result.rejectValue("staffId", "staff", "Ma khong qua 8 ky tu");
				}
			}

			finally {
				session.close();
			}
		}
		model.addAttribute("idModal", "modal-create");
		model.addAttribute("cFormAttribute",
				new FormAttribute("Thêm mới nhân viên", "admin/employee.htm", "btnCreate"));
		model.addAttribute("staffUpdate", staff);
		model.addAttribute("cList", getAllStaff());
		return "admin/employee";
	}

	// return views update
	@RequestMapping(value = "update/{id}.htm", method = RequestMethod.GET)
	public String getUpdate(ModelMap model, @PathVariable("id") String id) {

		model.addAttribute("staff", getStaff(id));
		model.addAttribute("idModal", "modal-create");
		model.addAttribute("cList", getAllStaff());
		model.addAttribute("cFormAttribute", new FormAttribute("Chỉnh sửa thông tin nhân viên",
				"admin/employee/update/" + id + ".htm", "btnUpdate"));
		return "admin/employee";
	}

	@RequestMapping(value = "update/{id}.htm", method = RequestMethod.POST, params = "btnUpdate")
	public String updateStaff(ModelMap model, @Validated @ModelAttribute("staff") StaffEntity staff,
			BindingResult result, RedirectAttributes redirectAttributes, @PathVariable("id") String id) {
		if (!result.hasErrors()) {
			Session session = factory.openSession();

			Transaction t = session.beginTransaction();
			try {
				AccountEntity accountEntity = getStaff(id).getAccount();

				staff.setAccount(accountEntity);
				session.merge(staff);
				if (staff.getStatus() == 0) {
					accountEntity.setStatus(0);
					session.merge(accountEntity);
				}
				t.commit();
				redirectAttributes.addFlashAttribute("message", new Message("success", "Cập nhật thành công !!!"));

				return "redirect:/admin/employee.htm";

			} catch (Exception e) {

				t.rollback();
				System.out.println(e.getCause());
				if (e.getCause().toString().contains("UNIQUE_NHANVIEN_SDT")) {
					result.rejectValue("phone", "staff", "số điện thoại này đã được sử dụng");
				}

				if (e.getCause().toString().contains("UCHECK_NHANVIEN_SDT")) {
					result.rejectValue("phone", "staff", "số điện thoại không đúng định dạng");
				}
				if (e.getCause().toString().contains("CK_NHANVIEN_NGAYSINH")) {
					result.rejectValue("birthday", "staff", "Tuổi nhân viên phải lớn hơn 18");
				}
				if (e.getCause().toString().contains("UNIQUE_NHANVIEN_EMAIL")) {
					result.rejectValue("email", "staff", "email này đã được sử dụng");
				}
				if (e.getCause().toString().contains("UNIQUE_NHANVIEN_CMND")) {
					result.rejectValue("identityCard", "pt", "CMND đã tồn tại");
				}
				if (e.getCause().toString().contains("String or binary data would be truncated")) {
					result.rejectValue("staffId", "staff", "Ma khong qua 8 ky tu");
				}
			}

			finally {
				session.close();
			}
		}
		model.addAttribute("idModal", "modal-create");
		model.addAttribute("cList", getAllStaff());
		model.addAttribute("cFormAttribute", new FormAttribute("Chỉnh sửa thông tin nhân viên",
				"admin/employee/update/" + id + ".htm", "btnUpdate"));
		return "admin/employee";
	}

	// create account staff

	@RequestMapping(value = "{id}/create-account.htm", method = RequestMethod.POST)
	public String getCreateAccount(ModelMap model, RedirectAttributes redirectAttributes, @PathVariable("id") String id,
			HttpServletRequest request) {

		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		String userName = request.getParameter("userName");
		System.out.println(userName);

		StaffEntity staff = getStaff(id);
		redirectAttributes.addFlashAttribute("error", "Tài khoản không được bỏ trống");
		if (!userName.equals("")) {
			try {

				RandomPassword radomPassword = new RandomPassword(8);

				AccountEntity accountEntity = new AccountEntity(userName,
						DigestUtils.md5Hex(radomPassword.getPassword()).toUpperCase(), 1, 2, new Date(), staff);
				accountEntity.setStatus(2);
				session.save(accountEntity);

				String mailMessage = "Mật khẩu cho tài khoản PTITGYM của bạn là: " + radomPassword.getPassword();

				MimeMessage mail = mailer.createMimeMessage();
				MimeMessageHelper helper = new MimeMessageHelper(mail, true);
				helper.setFrom("nguyenminhnhat301101@gmail.com", "PTITGYM");
				helper.setTo(staff.getEmail());
				helper.setReplyTo("nguyenminhnhat301101@gmail.com");
				helper.setSubject("Tai khoản PTITGYM");
				helper.setText(mailMessage);
				mailer.send(mail);

				staff.setAccount(accountEntity);
				session.merge(staff);
				t.commit();
				redirectAttributes.addFlashAttribute("message", new Message("success", "Tạo tài khoản thành công !!!"));
				return "redirect:/admin/employee.htm";
			} catch (Exception e) {

				t.rollback();
				System.out.println(e);
				if (e.getCause().toString().contains("duplicate key")) {
					redirectAttributes.addFlashAttribute("error", "Tên tài khoản đã tồn tại");
				}
			} finally {
				session.close();
			}

		}

		redirectAttributes.addFlashAttribute("userName", userName);
		redirectAttributes.addFlashAttribute("idModal", "modal-create-account");
		redirectAttributes.addFlashAttribute("staffId", id);

		return "redirect:/admin/employee.htm";
	}

	@RequestMapping(value = "", params = "btnFilter", method = RequestMethod.GET)
	public String saleFilter(@RequestParam Map<String, String> allParams, ModelMap model, HttpServletRequest request) {

		Session session = factory.getCurrentSession();

		String whereClause = "";
		String hqlGender = "";
		String dateCreate = toHqlRangeCondition(allParams.get("dateLeft"), allParams.get("dateRight"), "birthday");
		if (request.getParameterValues("gender") != null) {
			hqlGender = toHqlSingleColumOr("gender", request.getParameterValues("gender"));
		}

		String hqlStatus = "";
		if (request.getParameterValues("status") != null) {
			hqlStatus = toHqlSingleColumOr("status", request.getParameterValues("status"));
		}

		List<String> conditionCluaseList = new ArrayList<>();
		conditionCluaseList.addAll(Arrays.asList(dateCreate, hqlGender, hqlStatus));
		whereClause = toHqlWhereClause(conditionCluaseList);
		String hql = "from StaffEntity " + whereClause;
		System.out.println(hql);
		Query query = session.createQuery(hql);
		List<StaffEntity> list = query.list();
		model.addAttribute("cList", list);
		StaffEntity staff = newStaff();
		staff.setStatus(1);
		model.addAttribute("staff", staff);
		model.addAttribute("cFormAttribute",
				new FormAttribute("Them moi nhan vien", "admin/employee.htm", "btnCreate"));

		return "admin/employee";
	}

	// method

	public List<StaffEntity> getAllStaff() {
		Session session = factory.getCurrentSession();
		String hql = "FROM StaffEntity";
		Query query = session.createQuery(hql);
		List<StaffEntity> list = query.list();
		return list;
	}

	public StaffEntity getStaff(String id) {
		Session session = factory.getCurrentSession();
		return (StaffEntity) session.get(StaffEntity.class, id);
	}

	public StaffEntity newStaff() {
		StaffEntity staff = new StaffEntity();
		staff.setStaffId(this.toPK("NV", "StaffEntity", "staffId"));
		return staff;
	}

}
