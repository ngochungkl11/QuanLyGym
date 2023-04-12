package com.GymManager.Entity;

import java.time.LocalDate;
import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "KHACHHANG")
public class CustomerEntity {
	@Id
	@NotEmpty(message = "Ma khach hang khong duoc de trong")
	@Column(name = "MaKH")
	private String customerId;
	@NotEmpty(message = "Tên không đươc để trống")
	@Column(name = "HoTen")
	private String name;
	@Column(name = "Phai")
	private boolean gender;
	@NotEmpty(message = "Địa chỉ không được để trống")
	@Column(name = "DiaChi")
	private String address;
	@NotEmpty(message = "Email không được để trống")
	@Email(message = "Vui lòng nhập đúng định dạng email")
	@Column(name = "Email")
	private String email;
	@NotNull(message = "Ngày sinh không được để trống")
	@Column(name = "NgaySinh")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date birthday;
	@NotEmpty(message = "Số điện thoại không được để trống")
	@Column(name = "SDT")
	private String phone;

	@OneToOne
	@JoinColumn(name = "TaiKhoan")
	private AccountEntity account;

	@OneToMany(mappedBy = "customer")
	@LazyCollection(LazyCollectionOption.FALSE)
	private Collection<RegisterEntity> registerList;

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public boolean isGender() {
		return gender;
	}

	public void setGender(boolean gender) {
		this.gender = gender;
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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public AccountEntity getAccount() {
		return account;
	}

	public void setAccount(AccountEntity account) {
		this.account = account;
	}

	public Collection<RegisterEntity> getRegisterList() {
		return registerList;
	}

	public void setRegisterList(Collection<RegisterEntity> registerList) {
		this.registerList = registerList;
	}

	public int getCustomerStatus() {
		int status = 0; // chưa đăng ký
		List<RegisterEntity> registerEntities = (List<RegisterEntity>) this.getRegisterList();
		for (RegisterEntity registerEntity : registerEntities) {
			if (registerEntity.getStatus() == 0 || registerEntity.getStatus() == 1) {
				status = 2; // đã đăng ký
			}
			if (registerEntity.getStatus() == 1) {
				List<RegisterDetailEntity> registerDetailEntities = (List<RegisterDetailEntity>) registerEntity
						.getRegisterDetailList();
				for (RegisterDetailEntity registerDetailEntity : registerDetailEntities) {
					if (registerDetailEntity.getClassEntity().getClassPeriod() == 1) {
						status = 1; // đang tập
					}
				}
			}

		}

		return status;
	}

}
