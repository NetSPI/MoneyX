package com.nvisium.androidnv.api.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.nvisium.androidnv.api.model.User;

public interface UserService extends UserDetailsService {

	public UserDetails loadUserByUsername(String username);
	
	public User loadUserById(Long id);
	
	public User loadUser(String username);

	public void addRegularUser(String username, String password, String email,
			String answer, String firstname, String lastname);

	public boolean isAnswerValid(String username, String answer);

	public boolean validateCredentials(String username, String password);

	public void logout();

	public boolean validateCurrentPassword(String password);

	public void updatePasswordById(String password);
	
	public void updateAnswerById(String answer);
	
	public void updatePasswordByUsername(String username, String password);
	
	public void updateUser(String username, String firstname, String lastname, String email);

	public boolean doesUserExist(String username);

	public List<User> getPublicUsers();

	public void credit(Long id, BigDecimal amount);

	public boolean debit(Long id, BigDecimal amount);
}
