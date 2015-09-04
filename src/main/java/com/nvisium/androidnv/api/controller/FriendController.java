package com.nvisium.androidnv.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.nvisium.androidnv.api.exception.AlreadyFriendsException;
import com.nvisium.androidnv.api.exception.FriendRequestAlreadySentException;
import com.nvisium.androidnv.api.exception.InvalidFriendException;
import com.nvisium.androidnv.api.exception.InvalidFriendRequestException;
import com.nvisium.androidnv.api.security.SecurityUtils;
import com.nvisium.androidnv.api.service.FriendService;
import com.nvisium.androidnv.api.model.Friend;
import com.nvisium.androidnv.api.model.User;
import com.nvisium.androidnv.api.service.UserService;
import com.nvisium.androidnv.api.security.SecurityUtils;

@RequestMapping(value = "/friend")
@Controller
public class FriendController {
	
	@Autowired
	SecurityUtils security;

	@Autowired
	FriendService friendService;
	
	@Autowired
	UserService userService;
	
	/*
	 * VULN - CSRF, shouldn't be able to call this with GET
	 */
	@RequestMapping(value = "/delete-friend", method = {RequestMethod.GET, RequestMethod.POST})
	public String deleteFriend(
			@RequestParam(value = "friend", required = false) long friend,
			Model model) {
		
		if (friend == 0) {
			return listFriends(model);
		}
		
		friendService.deleteFriend(userService.loadUserById(friend));
		model.addAttribute("success", "Friend was successfully removed");
		return listFriends(model);
	}
	
	@RequestMapping(value = "/get-friends", method = RequestMethod.GET)
	public String listFriends(
			Model model) {
		
		User currentUser = security.getSecurityContext().getUser();
		java.util.List<User> friends = new java.util.ArrayList<User>();
		
		for(Friend f: friendService.getFriends()) {
			if (currentUser.getId().equals(f.getUser1().getId())) {
				friends.add(f.getUser2());
			} else {
				friends.add(f.getUser1());
			}
		}
		
		model.addAttribute("friends",friends);
		return "friend/get-friends";
	}

	@RequestMapping(value = "/list-sent-friend-requests", method = RequestMethod.GET)
	public String listSentFriendRequests(Model model) {
		model.addAttribute("friendrequests", friendService.getSentFriendRequests());
		return "friend/sent-requests";
	}

	@RequestMapping(value = "/list-received-friend-requests", method = RequestMethod.GET)
	public String listReceivedFriendRequests(Model model) {
		model.addAttribute("friendrequests", friendService.getReceivedFriendRequests());
		return "friend/received-requests";
	}

	@RequestMapping(value = "/send-friend-request/{receiver}", method = RequestMethod.GET)
	public String send(@PathVariable Long receiver, Model model) {
		try {
			friendService.sendFriendRequest(userService.loadUserById(receiver));
			model.addAttribute("success", "User has been sent a friend request!");
		} catch (FriendRequestAlreadySentException e) {
			model.addAttribute("error", "User has already been sent a friend request!");
		} catch (InvalidFriendRequestException e) {
			model.addAttribute("error", "Cannot send friend request!");
		}
		return listSentFriendRequests(model);
	}

	/*
	 * VULN - IDOR and CSRF
	 */
	@RequestMapping(value = "/accept-friend-request/{id}", method = RequestMethod.GET)
	public String accept(@PathVariable Long id, Model model) {
		User sender = friendService.getFriendRequestSenderId(id);
		try {
			friendService.addFriend(sender);
			friendService.deleteFriendRequest(id);
			model.addAttribute("success", "Successfully accepted friend request!");
		} catch (AlreadyFriendsException e) {
			model.addAttribute("error", "User is already your friend!");
		} catch (InvalidFriendException e) {
			model.addAttribute("error", "Cannot accept friend request!");
		}
		return listFriends(model);
	}

	/*
	 * VULN - IDOR and CSRF
	 */
	@RequestMapping(value = "/delete-friend-request/{id}", method = RequestMethod.GET)
	public String delete(@PathVariable Long id, Model model) {
		friendService.deleteFriendRequest(id);
		model.addAttribute("success","Deleted the friend request.");
		
		return listFriends(model);
	}
}
