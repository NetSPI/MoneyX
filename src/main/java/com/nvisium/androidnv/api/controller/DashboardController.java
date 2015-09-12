package com.nvisium.androidnv.api.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.nvisium.androidnv.api.model.Event;
import com.nvisium.androidnv.api.model.EventMembership;
import com.nvisium.androidnv.api.service.EventService;
import com.nvisium.androidnv.api.model.User;
import com.nvisium.androidnv.api.service.UserService;
import com.nvisium.androidnv.api.model.Payment;
import com.nvisium.androidnv.api.service.PaymentService;
import com.nvisium.androidnv.api.security.SecurityUtils;

@RequestMapping()
@Controller
public class DashboardController {

	@Autowired
	EventService eventService;
	
	@Autowired
	SecurityUtils security;
	
	@Autowired
	UserService userService;
	
	@Autowired
	PaymentService paymentService;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String listReceivedPayments() {
		return "index";
	}

	@RequestMapping(value = "/dashboard", method = RequestMethod.GET)
	public String dashboard(Model model) {
		List<Event> owned = eventService.getEventsByOwner(security.getCurrentUserId());
		Map<Event, List<User>> users = new HashMap<Event, List<User>>();
		
		for (Event e: owned) {
			users.put(e, eventService.getUsersbyEventMembership(e.getId()));
		}
		List<EventMembership> memberships = eventService.getEventsByMembership(security.getCurrentUserId());
		Map<EventMembership,Event> events = new HashMap<EventMembership,Event>();
		for (EventMembership em: memberships) {
			events.put(em,eventService.getEventById(em.getEventId()));
			List<User> u = new java.util.ArrayList<User>();
			u.add(userService.loadUserById(eventService.getEventById(em.getEventId()).getOwner()));
			users.put(eventService.getEventById(em.getEventId()),u);
		}
		List<Payment> sent = paymentService.getSentPayments(userService.loadUserById(security.getCurrentUserId()));
		List<Payment> received = paymentService.getReceivedPayments(userService.loadUserById(security.getCurrentUserId()));
		
		model.addAttribute("events", events);
		model.addAttribute("owned", owned);
		model.addAttribute("users", users);
		model.addAttribute("sent", sent);
		model.addAttribute("received", received);
		return "dashboard";
	}

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public String test(
			@RequestParam(required = false, value = "test") String test,
			Model model) {
             	return "test";
	}
}
