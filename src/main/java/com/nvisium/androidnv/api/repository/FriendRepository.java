package com.nvisium.androidnv.api.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nvisium.androidnv.api.model.Friend;
import com.nvisium.androidnv.api.model.User;

@Repository
@Qualifier(value = "friendRepository")
public interface FriendRepository extends CrudRepository<Friend, User> {

	@Query("select f from Friend f where (f.user1 = ?1 and f.user2 = ?2) or (f.user2 = ?2 and f.user2 = ?1)")
	public Friend findExistingFriend(User user1, User user2);

	@Query("delete from Friend where user1 = ?1 or user2 = ?1")
	@Modifying
	public void deleteFriend(User id);

	@Query("select f from Friend f where f.user1 = ?1 or f.user2 = ?1")
	public List<Friend> findFriendsByUser(User id);
}
