package com.nvisium.androidnv.api.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nvisium.androidnv.api.model.User;

@Repository
@Qualifier(value = "accountRepository")
public interface UserRepository extends CrudRepository<User, Long> {

	@Query("select u from User u where u.id = ?1")
	public User findById(Long id);
	
	@Query("select username from User u where u.username = ?1")
	public String findByUsername(String username);

	@Query("select username from User u where u.username = ?1 and u.password = ?2")
	public String findByUsernameAndPassword(String username, String password);

	@Query("select username from User u where u.id = ?1 and u.password = ?2")
	public String findUserByIdAndPassword(long id, String password);

	@Modifying
	@Query("update User u set u.password = ?1 where u.username = ?2")
	public void updatePasswordByUsername(String username, String password);

	@Modifying
	@Query("update User u set u.password = ?1 where u.id = ?2")
	public void updatePasswordById(String password, long id);

	// TODO leave as-is or grab the specific settings stuff...prob just
	// send/receive the entire user model from client
	@Query("select u from User u where u.username=?1")
	public void findSettingsByUsername(String username);

	// TODO: add the right settings to be persisted, or just persist the entire
	// user object or something else lazy
	@Modifying
	@Query("update User u set u.firstname = '' where u.username = ?1")
	public void updateSettings(String username);

	@Query("select u from User u where u.username = ?1")
	public User getUserModelByUsername(String username);

	@Query("select accountNonLocked from User u where u.username = ?1 and u.accountNonLocked = false")
	public boolean getAccountNonLockedByUsername(String username);

	@Query("select u from User u where u.privacyEnabled = false")
	public List<User> getUsersByPrivacyDisabled();
	
	@Modifying
	@Transactional
	@Query("update User u set u.balance = u.balance + ?2 where u.id = ?1")
	public void updateBalance(Long id, BigDecimal amount);
}
