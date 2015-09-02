package com.nvisium.androidnv.api.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nvisium.androidnv.api.model.EventMembership;
import com.nvisium.androidnv.api.model.Event;
import com.nvisium.androidnv.api.model.Payment;
import com.nvisium.androidnv.api.model.User;
import com.nvisium.androidnv.api.security.SecurityUtils;
import com.nvisium.androidnv.api.service.EventService;
import com.nvisium.androidnv.api.service.PaymentService;
import com.nvisium.androidnv.api.service.UserService;

@RequestMapping(value = "/payment")
@Controller
public class PaymentController {

	@Autowired
	PaymentService paymentService;
	
	@Autowired
	EventService eventService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	SecurityUtils security;

	/*
	 * VULN: IDOR, Get a list of the payment transactions you've received
	 */
	@RequestMapping(value = "/list-received/{id}", method = RequestMethod.GET)
	public String listReceivedPayments(
			@PathVariable Long id,
			Model model) {
		List<Payment> payments = paymentService.getReceivedPayments(userService.loadUserById(id));
		if (payments.size() == 0) {
			model.addAttribute("info", "User has not received any payments!");
		}
		model.addAttribute("payments", payments);
		return "payment/list-received";
	}

	/*
	 * VULN: IDOR, Get a list of the payment transactions you've sent
	 */
	@RequestMapping(value = "/list-sent/{id}", method = RequestMethod.GET)
	public String listSentPayments(
			@PathVariable Long id,
			Model model) {
		List<Payment> payments = paymentService.getSentPayments(userService.loadUserById(id));
		
		if (payments.size() == 0) {
			model.addAttribute("info", "User has not sent any payments!");
		} else {
			model.addAttribute("payments", payments);
		}
		
		
		return "payment/list-sent";
	}
	
	@RequestMapping(value = "/balance", method = {RequestMethod.GET, RequestMethod.POST})
	public String balance(
			@RequestParam(value = "amount", required = false) BigDecimal amount,
			@RequestParam(value = "creditcard", required = false) String creditcard,
			RedirectAttributes redirectAttrs,
			Model model) {
		
		if (creditcard == null || amount == null) {
			model.addAttribute("user", security.getSecurityContext().getUser());
			return "payment/balance";
		}
		
		/*
		 * Credit card validation would be here if this were real :)
		 */
		
		userService.credit(security.getCurrentUserId(), amount);
		
		UserDetails currentUser = userService.loadUserByUsername(security.getSecurityContext().getUsername());
		
		Authentication authentication = new UsernamePasswordAuthenticationToken(currentUser, currentUser.getPassword(), currentUser.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		redirectAttrs.addFlashAttribute("success", "Balance updated successfully!");
		return "redirect:/dashboard";
	}

	/*
	 * VULN: IDOR + CSRF
	 */
	@RequestMapping(value = "/make-payment", method = {RequestMethod.GET, RequestMethod.POST})
	public String makePayment(
			@RequestParam(value = "event", required = false) Long eventId,
			@RequestParam(value = "amount", required = false) BigDecimal amount,
			RedirectAttributes redirectAttrs,
			Model model) {
				
		 if (eventId == null || amount == null) {
			List<EventMembership> memberships = eventService.getEventsByMembership(security.getCurrentUserId());
			Map<EventMembership, Event> events = new HashMap<EventMembership, Event>();
			Map<Event, User> users = new HashMap<Event, User>();
			for (EventMembership m: memberships) {
				events.put(m, eventService.getEventById(m.getEventId()));
				users.put(eventService.getEventById(m.getEventId()), userService.loadUserById(m.getUser()));
			}
			model.addAttribute("users", users);
			model.addAttribute("events", events);
			return "payment/make-payment";
		}

		if (!paymentService.makePayment(eventService.getEventById(eventId), amount)) {
			model.addAttribute("danger", "Insufficient funds in your account!");
			List<EventMembership> memberships = eventService.getEventsByMembership(security.getCurrentUserId());
			Map<EventMembership, Event> events = new HashMap<EventMembership, Event>();
			Map<Event, User> users = new HashMap<Event, User>();
			for (EventMembership m: memberships) {
				events.put(m, eventService.getEventById(m.getEventId()));
				users.put(eventService.getEventById(m.getEventId()), userService.loadUserById(m.getUser()));
			}
			model.addAttribute("events", events);
			model.addAttribute("users", users);
			return "payment/make-payment";
		}

		UserDetails currentUser = userService.loadUserByUsername(security.getSecurityContext().getUsername());
		
		Authentication authentication = new UsernamePasswordAuthenticationToken(currentUser, currentUser.getPassword(), currentUser.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		redirectAttrs.addFlashAttribute("success", "Payment sent successfully!");
		return "redirect:/dashboard";
	}
}
