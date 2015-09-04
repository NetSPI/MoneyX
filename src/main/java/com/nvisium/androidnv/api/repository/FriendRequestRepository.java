package com.nvisium.androidnv.api.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nvisium.androidnv.api.model.FriendRequest;
import com.nvisium.androidnv.api.model.User;

@Repository
@Qualifier(value = "friendRequestRepository")
public interface FriendRequestRepository extends
		CrudRepository<FriendRequest, Long> {

	@Query("select f from FriendRequest f where f.sender = ?1")
	public List<FriendRequest> findFriendRequestBySender(User sender);

	@Query("select f from FriendRequest f where f.receiver = ?1")
	public List<FriendRequest> findFriendRequestByReceiver(User receiver);

	@Query("select f.sender from FriendRequest f where f.id = ?1")
	public User findSenderByFriendRequestId(Long id);

	@Query("delete from FriendRequest where id = ?1")
	@Modifying
	public void deleteFriendRequestById(Long id);

	@Query("select f from FriendRequest f where f.receiver = ?1 and f.sender = ?2")
	public FriendRequest findFriendRequestBySenderAndReceiver(User receiver,
			User sender);
}
