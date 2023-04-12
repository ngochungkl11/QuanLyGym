package com.GymManager.Entity;

import java.time.LocalDate;
import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "TKB_LOP")
public class ScheduleEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Ma")
	private Integer id;

	@Column(name = "MaLop")
	private String classId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "MaLop", insertable = false, updatable = false)
	private ClassEntity classEntity;

	@Column(name = "Thu")
	private Integer day;
	@Column(name = "Buoi")
	private int session;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ClassEntity getClassEntity() {
		return classEntity;
	}

	public void setClassEntity(ClassEntity classEntity) {
		this.classEntity = classEntity;
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public int getSession() {
		return session;
	}

	public void setSession(int session) {
		this.session = session;
	}

	public ScheduleEntity(String classId, ClassEntity classEntity, Integer day, int session) {
		super();
		this.classId = classId;
		this.classEntity = classEntity;
		this.day = day;
		this.session = session;
	}

	public ScheduleEntity() {
		super();
	}

	public int getScheduleStatus() {
		ClassEntity classEntity = this.classEntity;
		LocalDate date2 = LocalDate.parse(classEntity.getDateStart().toString());
		LocalDate date = date2.plusMonths(classEntity.getTrainingPackEntity().getPackDuration());
		Date toDay = new Date();
		if (toDay.after(classEntity.getDateStart()) && toDay.before(java.sql.Date.valueOf(date))) {
			return 1;
		}
		return 0;
	}

}
