package com.GymManager.Controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/admin/index")
@Transactional
public class HomeController {
	@Autowired
	SessionFactory factory;

	@RequestMapping(method = RequestMethod.GET)
	public String index(HttpServletRequest request, ModelMap model) {
		Date today = new Date();

		Long mumOfCustomer = getNumberEntity("CustomerEntity");
		Long mumOfStaff = getNumberEntity("StaffEntity");
		Long mumOfRegister = getNumberEntity("RegisterEntity");
		Long mumOfClass = getNumberEntity("ClassEntity");
		Long mumOfPack = getNumberEntity("TrainingPackEntity");
		Long mumOfPT = getNumberEntity("PTEntity");

		model.addAttribute("numOfCustomer", mumOfCustomer);
		model.addAttribute("mumOfStaff", mumOfStaff);
		model.addAttribute("mumOfRegister", mumOfRegister);
		model.addAttribute("mumOfClass", mumOfClass);
		model.addAttribute("mumOfPack", mumOfPack);
		model.addAttribute("mumOfPT", mumOfPT);

		model.addAttribute("billFilter", "day");
		model.addAttribute("revenueFilter", "day");
		return "admin/index";
	}

	@RequestMapping(method = RequestMethod.GET, params = "btnFilter")
	public String indexFilter(HttpServletRequest request, ModelMap model) {
		String billFilterUnit = request.getParameter("bill");
		String revenueFilterUnit = request.getParameter("revenue");
		Date today = new Date();
		model.addAttribute("numOfBill", getNumOfBillFilter(billFilterUnit, getNumOfUnit(billFilterUnit, today)));
		model.addAttribute("revenue", getRevenue(revenueFilterUnit, getNumOfUnit(revenueFilterUnit, today)));
		model.addAttribute("numOfCustomer", getNumOfCustomer());
		model.addAttribute("billFilter", billFilterUnit);
		model.addAttribute("revenueFilter", revenueFilterUnit);
		return "admin/index";
	}

	public Long getNumOfBillFilter(String typeUnit, int num) {
		Session session = factory.getCurrentSession();
		String hql = "SELECT COUNT(billId) FROM  BillEntity WHERE " + typeUnit + "(date) = " + num;
		Query query = session.createQuery(hql);
		Long mumOfBill = (Long) query.list().get(0);
		return mumOfBill;
	}

	public Double getRevenue(String typeUnit, int num) {

		Session session = factory.getCurrentSession();
		String hql = "SELECT SUM(money) FROM  BillEntity WHERE " + typeUnit + "(date) = " + num;
		Query query = session.createQuery(hql);
		Double money = (Double) query.list().get(0);
		return money;
	}

	public Long getNumOfCustomer() {

		Session session = factory.getCurrentSession();
		String hql = "SELECT COUNT(customerId) FROM  CustomerEntity";
		Query query = session.createQuery(hql);
		Long mumOfCustomer = (Long) query.list().get(0);
		return mumOfCustomer;
	}

	@SuppressWarnings("deprecation")
	public int getNumOfUnit(String unit, Date date) {
		if (unit.equals("day"))
			return date.getDate();
		else if (unit.equals("month"))
			return date.getMonth() + 1;
		else
			return date.getYear() + 1900;
	}

	public Long getNumberEntity(String entity) {
		Session session = factory.getCurrentSession();
		String hql = "SELECT COUNT(*) FROM " + entity;
		if (entity.equals("ClassEntity")) {
			hql += " where maxPP > 1";
		}
		Query query = session.createQuery(hql);
		Long mum = (Long) query.list().get(0);
		return mum;
	}

}
