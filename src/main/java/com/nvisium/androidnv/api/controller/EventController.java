package com.nvisium.androidnv.api.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nvisium.androidnv.api.model.Event;
import com.nvisium.androidnv.api.model.EventMembership;
import com.nvisium.androidnv.api.model.Friend;
import com.nvisium.androidnv.api.model.User;
import com.nvisium.androidnv.api.security.SecurityUtils;
import com.nvisium.androidnv.api.service.EventService;
import com.nvisium.androidnv.api.service.UserService;
import com.nvisium.androidnv.api.service.FriendService;


@RequestMapping(value = "/event")
@Controller
public class EventController {

	@Autowired
	EventService eventService;
	
	@Autowired
	UserService userService;

	@Autowired
	FriendService friendService;	
	
	@Autowired
	SecurityUtils security;

	/*
	 * VULN: IDOR, List all active events you are the owner of
	 */
	@RequestMapping(value = "/list-owner/{user}", method = RequestMethod.GET)
	public String listEventsOwned(
			@PathVariable("user") Long user,
			Model model) {
		
		java.util.List<Event> events = eventService.getEventsByOwner(user);
		if (events.size() == 0) {
			model.addAttribute("info", "User does not own any events!");
		}
		
		model.addAttribute("events", events);
		return "event/list-owned";
	}

	/*
	 * VULN: IDOR, List all active events you are a member of
	 */
	@RequestMapping(value = "/list-member/{user}", method = RequestMethod.GET)
	public String listEventMembership(
			@PathVariable("user") Long user,
			Model model) {
		java.util.List<EventMembership> events_m = eventService.getEventsByMembership(user);
		java.util.List<Event> events = new java.util.ArrayList<Event>();
		Map<Long, User> users = new HashMap<Long, User>();
		Map<Long, EventMembership> memberships = new HashMap<Long, EventMembership>();
		
		if (events_m.size() == 0) {
			model.addAttribute("info", "User is not part of any events!");
		} else {
			for ( EventMembership m : events_m) {
				Event e = eventService.getEventById(m.getEventId());
				events.add(e);
				users.put(m.getEventId(), userService.loadUserById(e.getOwner()));
				memberships.put(m.getEventId(), m);
			}
			model.addAttribute("events", events);
			model.addAttribute("users", users);
			model.addAttribute("memberships", memberships);
		}
	
		return "event/list-member";
	}

	/*
	 * Add a new event
	 * 
	 * 
	 * yungjassy.pyc
	 */
	@RequestMapping(value = "/add", method = {RequestMethod.GET, RequestMethod.POST})
	public String add(@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "amount", required = false) BigDecimal amount,
			@RequestParam(value = "users", required = false) LinkedList<Long> users,
			RedirectAttributes redirectAttrs,
			Model model) {
		
		/* If we need anything, let's just take the user to the add page */
		if (name == null || amount == null) {
			
			User currentUser = security.getSecurityContext().getUser();
			java.util.List<User> friends = new java.util.ArrayList<User>();
			
			for(Friend f: friendService.getFriends()) {
				if (currentUser.getId().equals(f.getUser1().getId())) {
					friends.add(f.getUser2());
				} else {
					friends.add(f.getUser1());
				}
			}
			
			model.addAttribute("users", friends);
			return "event/add-event";
		}
		
		Long eventId = eventService.addEvent(name, amount);
		
		if (users != null && users.size() > 0) {
			for (Long tempUserId: users) {
				eventService.addEventMembership(eventId, tempUserId, amount.divide(new BigDecimal(users.size()),BigDecimal.ROUND_UP));
			}
		}
				
		redirectAttrs.addFlashAttribute("success", "Successfully created event");
		return "redirect:/event/list-owner/" + security.getCurrentUserId();
	}

	/*
	 * VULN: IDOR + CSRF, Delete an event
	 */
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
	public String delete(@PathVariable Long id) {
		eventService.deleteEvent(id);
		return "redirect:/event/list-owner/" + security.getCurrentUserId();
	}

	/*
	 * VULN: IDOR, This will add a member to an existing event
	 */
	@RequestMapping(value = "/add-member", method = {RequestMethod.GET, RequestMethod.POST})
	public String addMember(
			@RequestParam(value = "eventId", required = false) Long eventId,
			@RequestParam(value = "userId", required = false) Long userId,
			@RequestParam(value = "amount", required = false) BigDecimal amount,
			RedirectAttributes redirectAttrs,
			Model model) {
		
		/* If we need anything, let's just take the user to the add page */
		if (eventId == null || userId == null || amount == null) {
			model.addAttribute("users", userService.getPublicUsers());
			model.addAttribute("events", eventService.getPublicEvents());
			return "event/add-member";
		}
		
		if (eventService.isUserMember(eventId, userId)) {
			model.addAttribute("info", "User is already a member of that event");
			return "event/add-member";
		}
		
		eventService.addEventMembership(eventId, userId, amount);
		redirectAttrs.addFlashAttribute("success", "Successfully added user to event!");
		return "redirect:/event/list-member/" + userId;
	}

	/*
	 * VULN - CSRF
	 * This will delete a member from an existing event, but doesn't delete the
	 * event itself
	 */
	@RequestMapping(value = "/delete-member/{eventId}/{userId}", method = RequestMethod.GET)
	public String removeMember(@PathVariable Long eventId,
			@PathVariable Long userId) {
		eventService.deleteEventMembership(eventId, userId);
		return "event/event-member-deleted";
	}

	/*
	 * VULN: IDOR + CSRF, we can tamper with the owner, This is used to update an
	 * existing event
	 */
	@RequestMapping(value = "/update/{id}/{owner}/{name}/{amount}/{hidden}", method = RequestMethod.GET)
	public String updateEvent(@PathVariable Long id,
			@PathVariable Long owner, @PathVariable String name,
			@PathVariable BigDecimal amount,
			@PathVariable Boolean hidden) {
		eventService.updateEvent(owner, name, amount, hidden, id);
		return "event/event-updated";
	}
}
