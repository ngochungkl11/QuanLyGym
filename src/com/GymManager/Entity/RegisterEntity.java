package com.GymManager.Entity;

import java.math.BigDecimal;
import java.util.*;
import java.util.Collection;

import javax.persistence.*;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "THONGTINDK")
public class RegisterEntity {
	@Id
	@Column(name = "MaTTDK")
	private String registerId;
	@ManyToOne
	@JoinColumn(name = "MaKH")
	private CustomerEntity customer;

	@Column(name = "NgayDK")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date registerDate;

	@Column(name = "TrangThai")
	private int status;
	@Column(name = "GiaTien")
	private BigDecimal money;
	@ManyToOne
	@JoinColumn(name = "NguoiTao")
	private AccountEntity account;
	@ManyToOne
	@JoinColumn(name = "NhanVienThanhToan")
	private StaffEntity staffEntity;

	@OneToMany(mappedBy = "registerEntity")
	@LazyCollection(LazyCollectionOption.FALSE)
	private Collection<RegisterDetailEntity> registerDetailList;

	public BigDecimal getMoney() {
		return money;
	}

	public void setMoney(BigDecimal money) {
		this.money = money;
	}

	public String getRegisterId() {
		return registerId;
	}

	public void setRegisterId(String registerId) {
		this.registerId = registerId;
	}

	public CustomerEntity getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerEntity customer) {
		this.customer = customer;
	}

	public Date getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public AccountEntity getAccount() {
		return account;
	}

	public void setAccount(AccountEntity account) {
		this.account = account;
	}

	public StaffEntity getStaffEntity() {
		return staffEntity;
	}

	public void setStaffEntity(StaffEntity staffEntity) {
		this.staffEntity = staffEntity;
	}

	public Collection<RegisterDetailEntity> getRegisterDetailList() {
		return registerDetailList;
	}

	public void setRegisterDetailList(Collection<RegisterDetailEntity> registerDetailList) {
		this.registerDetailList = registerDetailList;
	}

}
