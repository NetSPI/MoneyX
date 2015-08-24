package com.nvisium.androidnv.api.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.nvisium.androidnv.api.model.User;

public interface UserService extends UserDetailsService {

	public UserDetails loadUserByUsername(String username);
	
	public void addRegularUser(String username, String password, String email,
			String firstname, String lastname);

	public boolean validateCredentials(String username, String password);

	public void logout();

	public boolean validateCurrentPassword(String password);

	public void updatePasswordById(String password);

	public boolean doesUserExist(String username);
	
	public List<User> getPublicUsers();
}
