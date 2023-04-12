package com.GymManager.Entity;

import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "NHANVIEN")
public class StaffEntity {

	@Id
	@NotEmpty(message = "MaNV khong duoc bo trong")
	@Column(name = "MaNV")
	private String staffId;

	@NotEmpty(message = "Tên không được để trống")
	@Column(name = "HoTen")
	private String name;

	@Column(name = "Phai")
	private boolean gender;
	@NotEmpty(message = "cmnd khong duoc bo trong")
	@Column(name = "CMND")
	private String identityCard;
	@NotEmpty(message = "dia chi khong duoc bo trong")
	@Column(name = "DiaChi")
	private String address;
	@NotEmpty(message = "email khong duoc bo trong")
	@Column(name = "Email")
	private String email;
	@NotNull(message = "Ngay sinh khong duoc bo trong")
	@Column(name = "NgaySinh")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date birthday;
	@NotEmpty(message = "SDT khong duoc bo trong")
	@Column(name = "SDT")
	private String phone;
	@Column(name = "TrangThai")
	private int status;
	@OneToOne
	@JoinColumn(name = "TaiKhoan")
	private AccountEntity account;
	@OneToMany(mappedBy = "staffEntity")
	@LazyCollection(LazyCollectionOption.FALSE)
	private Collection<RegisterEntity> registerEntityList;

	public String getStaffId() {
		return staffId;
	}

	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean getGender() {
		return gender;
	}

	public void setGender(Boolean gender) {
		this.gender = gender;
	}

	public String getIdentityCard() {
		return identityCard;
	}

	public void setIdentityCard(String identityCard) {
		this.identityCard = identityCard;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getphone() {
		return phone;
	}

	public void setphone(String phone) {
		this.phone = phone;
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

}
