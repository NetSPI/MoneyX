package com.nvisium.androidnv.api.model;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class NvUserDetails implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private User user;
	List<GrantedAuthority> authorities;

	public NvUserDetails(User user, List<GrantedAuthority> authorities) {
		super();
		this.user = user;
		this.authorities = authorities;
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public String getPassword() {
		return user.getPassword();
	}

	public String getUsername() {
		return user.getUsername();
	}

	public boolean isAccountNonExpired() {
		return user.isAccountNonExpired();
	}

	public boolean isAccountNonLocked() {
		return user.isAccountNonLocked();
	}

	public boolean isCredentialsNonExpired() {
		return user.isCredentialsNonExpired();
	}

	public boolean isEnabled() {
		return user.isEnabled();
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setAuthorities(List<GrantedAuthority> authorities) {
		this.authorities = authorities;
	}

}
