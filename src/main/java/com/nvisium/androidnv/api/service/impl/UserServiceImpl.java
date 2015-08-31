package com.nvisium.androidnv.api.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nvisium.androidnv.api.model.NvUserDetails;
import com.nvisium.androidnv.api.model.Role;
import com.nvisium.androidnv.api.model.User;
import com.nvisium.androidnv.api.repository.UserRepository;
import com.nvisium.androidnv.api.security.SecurityUtils;
import com.nvisium.androidnv.api.service.UserService;

@Service
@Qualifier("userService")
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository accountRepository;

	@Autowired
	private SecurityUtils security;

	/*
	 * Inserts a new user
	 */
	@Override
	public void addRegularUser(String username, String password, String email,
			String answer, String firstname, String lastname) {
		com.nvisium.androidnv.api.model.User user = new com.nvisium.androidnv.api.model.User();
		user.addAccountInfo(username, password, email, answer, firstname,
				lastname);
		accountRepository.save(user);
	}

	@Override
	public boolean validateCredentials(String username, String password) {
		return (!(accountRepository.findByUsernameAndPassword(username,
				password) == null));
	}

	@Override
	@Transactional
	public void logout() {
		// TODO: purge from redis...for now, who cares, tokens live forever!!
		// VULN
	}

	@Override
	public boolean validateCurrentPassword(String password) {
		return (accountRepository.findUserByIdAndPassword(
				security.getCurrentUserId(), password) != null);
	}

	@Override
	@Transactional
	public void updatePasswordById(String password) {
		accountRepository.updatePasswordById(password,
				security.getCurrentUserId());
	}

	public boolean doesUserExist(String username) {
		return accountRepository.findByUsername(username) != null;
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) {

		com.nvisium.androidnv.api.model.User user = accountRepository
				.getUserModelByUsername(username);
		List<GrantedAuthority> authorities = buildUserAuthority(user.getRoles());
		return new NvUserDetails(user, authorities);
	}

	private List<GrantedAuthority> buildUserAuthority(Set<Role> roles) {

		Set<GrantedAuthority> setAuths = new HashSet<GrantedAuthority>();

		// Build user's authorities
		for (Role role : roles) {
			setAuths.add(new SimpleGrantedAuthority(role.getRoleName()));
		}
		return new ArrayList<GrantedAuthority>(setAuths);
	}

	@Override
	public List<User> getPublicUsers() {
		return accountRepository.getUsersByPrivacyDisabled();
	}

	@Override
	public void credit(Long id, BigDecimal amount) {
		accountRepository.updateBalance(id, amount);

		UserDetails currentUser = loadUserByUsername(security
				.getSecurityContext().getUsername());

		Authentication authentication = new UsernamePasswordAuthenticationToken(
				currentUser, currentUser.getPassword(),
				currentUser.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	@Override
	public boolean debit(Long id, BigDecimal amount) {
		User u = accountRepository.findById(id);
		if (u.getBalance().compareTo(amount) == -1) {
			credit(id, amount.negate());

			return true;
		}
		return false;
	}

	@Override
	public boolean isAnswerValid(String username, String answer) {
		if (accountRepository.getAnswerByUsername(username).equals(answer))
			return true;
		else
			return false;
	}

	@Override
	@Transactional
	public void updatePasswordByUsername(String username, String password) {
		accountRepository.updatePasswordByUsername(username, password);
	}

	@Override
	public void updateAnswerById(String answer) {
		accountRepository.updateAnswerById(answer, security.getCurrentUserId());
		
	}
}
