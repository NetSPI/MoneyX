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
import com.nvisium.androidnv.api.repository.FriendRepository;
import com.nvisium.androidnv.api.repository.FriendRequestRepository;
import com.nvisium.androidnv.api.security.SecurityUtils;
import com.nvisium.androidnv.api.service.FriendService;

@Service
@Qualifier("friendService")
public class FriendServiceImpl implements FriendService {

	@Autowired
	SecurityUtils security;

	@Autowired
	FriendRepository friendRepository;

	@Autowired
	FriendRequestRepository friendRequestRepository;

	@Override
	public void addFriend(Long sender) throws AlreadyFriendsException, InvalidFriendException {
		/*
		 * Sanity check - are we adding ourselves as friends
		 */
		if (security.getCurrentUserId() == sender) {
			throw new InvalidFriendException();
		}
		/*
		 * Has invitation already been sent?
		 */
		if (friendRepository.findExistingFriend(sender,
				security.getCurrentUserId()) == null) {
			Friend friend = new Friend();
			friend.setUser1(security.getCurrentUserId());
			friend.setUser2(sender);
			friendRepository.save(friend);
		} else
			throw new AlreadyFriendsException();
	}

	@Override
	@Transactional
	public void deleteFriend(Long friendId) {
		friendRepository.deleteFriend(friendId);
	}

	@Override
	public List<Friend> getFriends() {
		return friendRepository.findFriendsByUser(security.getCurrentUserId());
	}

	@Override
	public List<FriendRequest> getSentFriendRequests() {
		return friendRequestRepository.findFriendRequestBySender(security
				.getCurrentUserId());
	}

	@Override
	public List<FriendRequest> getReceivedFriendRequests() {
		return friendRequestRepository.findFriendRequestByReceiver(security
				.getCurrentUserId());
	}

	// VULN - IDOR
	@Override
	@Transactional
	public void deleteFriendRequest(Long id) {
		friendRequestRepository.deleteFriendRequestById(id);
	}

	@Override
	public void sendFriendRequest(Long receiver)
			throws FriendRequestAlreadySentException, InvalidFriendRequestException {
		/*
		 * Sanity check - did we send ourselves a friend request?
		 */

		if (security.getCurrentUserId() == receiver) {
			throw new InvalidFriendRequestException();
		}
		
		/*
		 * Has invitation already been sent?
		 */
		if (friendRequestRepository.findFriendRequestBySenderAndReceiver(
				receiver, security.getCurrentUserId()) == null) {
			FriendRequest friendRequest = new FriendRequest();
			friendRequest.setSender(security.getCurrentUserId());
			friendRequest.setReceiver(receiver);
			friendRequestRepository.save(friendRequest);
		} else
			throw new FriendRequestAlreadySentException();
	}

	@Override
	public Long getFriendRequestSenderId(Long id) {
		return friendRequestRepository.findSenderByFriendRequestId(id);
	}
}
