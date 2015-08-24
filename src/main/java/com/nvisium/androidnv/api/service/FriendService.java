package com.nvisium.androidnv.api.service;

import java.util.List;

import com.nvisium.androidnv.api.exception.AlreadyFriendsException;
import com.nvisium.androidnv.api.exception.FriendRequestAlreadySentException;
import com.nvisium.androidnv.api.exception.InvalidFriendException;
import com.nvisium.androidnv.api.exception.InvalidFriendRequestException;
import com.nvisium.androidnv.api.model.Friend;
import com.nvisium.androidnv.api.model.FriendRequest;

public interface FriendService {

	public void addFriend(Long sender) throws AlreadyFriendsException, InvalidFriendException;

	public void deleteFriend(Long id);

	public List<Friend> getFriends();

	public List<FriendRequest> getSentFriendRequests();

	public List<FriendRequest> getReceivedFriendRequests();

	public void sendFriendRequest(Long to)
			throws FriendRequestAlreadySentException, InvalidFriendRequestException;

	public void deleteFriendRequest(Long id);

	public Long getFriendRequestSenderId(Long id);
}
