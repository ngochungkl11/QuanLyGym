package com.GymManager.Controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.GymManager.Entity.CustomerEntity;
import com.GymManager.Entity.RegisterDetailEntity;
import com.GymManager.Entity.ScheduleEntity;
import com.GymManager.ExtraClass.CustomerToday;
import com.GymManager.ExtraClass.FormAttribute;

@Controller
@RequestMapping("admin/schedule")
@Transactional
public class ScheduleController extends MethodAdminController {
	@Autowired
	SessionFactory factory;

	@RequestMapping(method = RequestMethod.GET)
	public String index(ModelMap model) {
		List<ScheduleEntity> scheduleEntities = getAllOfDay("");
		List<CustomerToday> customerTodays = new ArrayList<CustomerToday>();
		for (ScheduleEntity scheduleEntity : scheduleEntities) {
			if (scheduleEntity.getClassEntity().getClassPeriod() == 1) {
				List<RegisterDetailEntity> registerDetailEntities = (List<RegisterDetailEntity>) scheduleEntity
						.getClassEntity().getRegisterDetailEntities();
				for (RegisterDetailEntity registerDetailEntity : registerDetailEntities) {
					if (registerDetailEntity.getRegisterEntity().getStatus() == 1) {
						CustomerToday customerToday = new CustomerToday(
								registerDetailEntity.getRegisterEntity().getCustomer(), scheduleEntity,
								registerDetailEntity.getClassEntity());
						customerTodays.add(customerToday);
					}

				}
			}
		}
		model.addAttribute("cList", customerTodays);
		return "admin/schedule";
	}

	// filter

	@RequestMapping(value = "", params = "btnFilter", method = RequestMethod.GET)
	public String saleFilter(HttpServletRequest request, ModelMap model) {
		String[] type = request.getParameterValues("type");
		String[] session = request.getParameterValues("status");
		String condition = "";
		if (session != null) {
			condition = " and " + toHqlSingleColumOr("session", session);
		}
		System.out.println(condition);

		List<ScheduleEntity> scheduleEntities = getAllOfDay(condition);
		List<CustomerToday> customerTodays = new ArrayList<CustomerToday>();
		for (ScheduleEntity scheduleEntity : scheduleEntities) {
			if (scheduleEntity.getScheduleStatus() == 1) {
				List<RegisterDetailEntity> registerDetailEntities = (List<RegisterDetailEntity>) scheduleEntity
						.getClassEntity().getRegisterDetailEntities();
				for (RegisterDetailEntity registerDetailEntity : registerDetailEntities) {
					if (registerDetailEntity.getRegisterEntity().getStatus() == 1) {

						if (type == null) {
							CustomerToday customerToday = new CustomerToday(
									registerDetailEntity.getRegisterEntity().getCustomer(), scheduleEntity,
									registerDetailEntity.getClassEntity());
							customerTodays.add(customerToday);
						} else if (type.length == 2) {
							CustomerToday customerToday = new CustomerToday(
									registerDetailEntity.getRegisterEntity().getCustomer(), scheduleEntity,
									registerDetailEntity.getClassEntity());
							customerTodays.add(customerToday);
						} else {

							if (type[0].equals("0")) {
								if (registerDetailEntity.getClassEntity().getMaxPP() == 1) {
									CustomerToday customerToday = new CustomerToday(
											registerDetailEntity.getRegisterEntity().getCustomer(), scheduleEntity,
											registerDetailEntity.getClassEntity());
									customerTodays.add(customerToday);
								}

							} else if (type[0].equals("1")) {

								if (registerDetailEntity.getClassEntity().getMaxPP() > 1) {
									CustomerToday customerToday = new CustomerToday(
											registerDetailEntity.getRegisterEntity().getCustomer(), scheduleEntity,
											registerDetailEntity.getClassEntity());
									customerTodays.add(customerToday);
								}

							}

						}

					}
				}
			}
		}
		model.addAttribute("cList", customerTodays);
		return "admin/schedule";
	}

	public List<CustomerEntity> getAllCustomer() {
		Session session = factory.getCurrentSession();
		String hql = "FROM CustomerEntity";
		Query query = session.createQuery(hql);
		List<CustomerEntity> list = query.list();
		return list;
	}

	public List<ScheduleEntity> getAllOfDay(String condition) {
		Session session = factory.getCurrentSession();
		String hql = "from ScheduleEntity WHERE day = DATEPART(WEEKDAY, GETDATE()) " + condition;
		System.out.println(hql);
		Query query = session.createQuery(hql);
		List<ScheduleEntity> list = query.list();
		return list;
	}

}
