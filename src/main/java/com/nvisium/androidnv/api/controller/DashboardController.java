package com.nvisium.androidnv.api.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.nvisium.androidnv.api.model.Event;
import com.nvisium.androidnv.api.model.User;
import com.nvisium.androidnv.api.service.EventService;

@RequestMapping()
@Controller
public class DashboardController {

	@Autowired
	EventService eventService;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String listReceivedPayments() {
		return "index";
	}
	
	@RequestMapping(value = "/dashboard", method = RequestMethod.GET)
	public String dashboard(Model model) {
		List<Event> events = eventService.getPublicEvents();
		Map<Event, List<User>> users = new HashMap<Event, List<User>>();
		for (Event e: events) {
			users.put(e, eventService.getUsersbyEventMembership(e.getId()));
		}
		model.addAttribute("events", eventService.getPublicEvents());
		model.addAttribute("users", users);
		return "dashboard";
	}
}
