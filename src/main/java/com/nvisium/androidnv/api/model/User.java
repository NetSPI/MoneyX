package com.nvisium.androidnv.api.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "users")
public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Version
	private Long version;

	@Column(name = "username")
	private String username;

	@Column(name = "password")
	private String password;

	@Column(name = "email")
	private String email;

	@Column(name = "answer")
	private String answer;

	@Column(name = "firstname")
	private String firstname;

	@Column(name = "lastname")
	private String lastname;

	@Column(name = "privacyEnabled")
	private Boolean privacyEnabled;

	@Column(name = "enabled")
	private Boolean enabled;

	@Column(name = "accountNonExpired")
	private Boolean accountNonExpired;

	@Column(name = "credentialsNonExpired")
	private Boolean credentialsNonExpired;

	@Column(name = "accountNonLocked")
	private Boolean accountNonLocked;

	@Column(name = "credit_card")
	private Long creditCard;

	@Column(name = "balance")
	private BigDecimal balance;

	@OneToMany(mappedBy = "user")
	private Set<Role> roles;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public void setCreditCard(long creditCard) {
		this.creditCard = creditCard;
	}

	public Long getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(Long creditCard) {
		this.creditCard = creditCard;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public boolean isAccountNonLocked() {
		return true;
	}

	public void setAccountNonLocked(Boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public void addAccountInfo(String username, String password, String answer,
			String email, String firstname, String lastname) {
		this.setUsername(username);
		this.setPassword(password);
		this.setAnswer(answer);
		this.setEmail(email);
		this.setFirstname(firstname);
		this.setLastname(lastname);
		this.setAccountNonLocked(true);
		this.roles = new HashSet<Role>();
		// create a new user with basic user privileges
		roles.add(new Role("USER", this));
		this.setRoles(roles);
		this.setPrivacyEnabled(false);
		this.balance = new BigDecimal(0.0);
	}

	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	public Boolean getPrivacyEnabled() {
		return privacyEnabled;
	}

	public void setPrivacyEnabled(Boolean privacyEnabled) {
		this.privacyEnabled = privacyEnabled;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Boolean getAccountNonExpired() {
		return accountNonExpired;
	}

	public void setAccountNonExpired(Boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	public Boolean getCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	public void setCredentialsNonExpired(Boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Boolean getAccountNonLocked() {
		return accountNonLocked;
	}
}
