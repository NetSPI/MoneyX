package com.nvisium.androidnv.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nvisium.androidnv.api.exception.AlreadyFriendsException;
import com.nvisium.androidnv.api.exception.FriendRequestAlreadySentException;
import com.nvisium.androidnv.api.exception.InvalidFriendException;
import com.nvisium.androidnv.api.exception.InvalidFriendRequestException;
import com.nvisium.androidnv.api.model.Friend;
import com.nvisium.androidnv.api.model.FriendRequest;
import com.nvisium.androidnv.api.model.User;
import com.nvisium.androidnv.api.repository.FriendRepository;
import com.nvisium.androidnv.api.repository.FriendRequestRepository;
import com.nvisium.androidnv.api.repository.UserRepository;
import com.nvisium.androidnv.api.security.SecurityUtils;
import com.nvisium.androidnv.api.service.FriendService;

@Service
@Qualifier("friendService")
public class FriendServiceImpl implements FriendService {

	@Autowired
	SecurityUtils security;

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	FriendRepository friendRepository;

	@Autowired
	FriendRequestRepository friendRequestRepository;

	public void addFriend(User sender) throws AlreadyFriendsException, InvalidFriendException {
		/*
		 * Sanity check - are we adding ourselves as friends
		 */
		User currentUser = security.getSecurityContext().getUser();
		if (currentUser == sender) {
			throw new InvalidFriendException();
		}
		/*
		 * Has invitation already been sent?
		 */
		if (friendRepository.findExistingFriend(sender, currentUser) == null) {
			Friend friend = new Friend();
			friend.setUser1(currentUser);
			friend.setUser2(sender);
			friendRepository.save(friend);
		} else
			throw new AlreadyFriendsException();
	}

	@Transactional
	public void deleteFriend(User friend) {
		friendRepository.deleteFriend(friend);
	}

	public List<Friend> getFriends() {
		User currentUser = security.getSecurityContext().getUser();
		return friendRepository.findFriendsByUser(currentUser);
	}

	public List<FriendRequest> getSentFriendRequests() {
		return friendRequestRepository.findFriendRequestBySender(security.getSecurityContext().getUser());
	}

	public List<FriendRequest> getReceivedFriendRequests() {
		return friendRequestRepository.findFriendRequestByReceiver(security.getSecurityContext().getUser());
	}

	// VULN - IDOR
	@Transactional
	public void deleteFriendRequest(Long id) {
		friendRequestRepository.deleteFriendRequestById(id);
	}

	public void sendFriendRequest(User receiver)
			throws FriendRequestAlreadySentException, InvalidFriendRequestException {
		/*
		 * Sanity check - did we send ourselves a friend request?
		 */
		
		User currentUser = security.getSecurityContext().getUser();

		if (currentUser == receiver) {
			throw new InvalidFriendRequestException();
		}
		
		/*
		 * Has invitation already been sent?
		 */
		if (friendRequestRepository.findFriendRequestBySenderAndReceiver(receiver, currentUser) == null) {
			FriendRequest friendRequest = new FriendRequest();
			friendRequest.setSender(currentUser);
			friendRequest.setReceiver(receiver);
			friendRequestRepository.save(friendRequest);
		} else
			throw new FriendRequestAlreadySentException();
	}

	public User getFriendRequestSenderId(Long id) {
		return friendRequestRepository.findSenderByFriendRequestId(id);
	}
}
