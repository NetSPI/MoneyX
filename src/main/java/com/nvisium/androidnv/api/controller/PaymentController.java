package com.nvisium.androidnv.api.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
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
	
	@PersistenceContext
    private EntityManager em;

	/*
	 * VULN: IDOR, Get a list of the payment transactions you've received
	 */
	@RequestMapping(value = "/list-received/{id}", method = RequestMethod.GET)
	public String listReceivedPayments(
			@PathVariable String id,
			Model model) {
		
		Query q = em.createNativeQuery("select * from Payments p where p.receiver = " + id, Payment.class);
		@SuppressWarnings("unchecked")
		List<Payment> payments = q.getResultList();
				

		
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
			@PathVariable Integer id,
			Model model) {
		
		// List<Payment> payments = paymentService.getSentPayments(userService.loadUserById(id));
		@SuppressWarnings("unchecked")
		List<Payment> payments = 
				em.createNativeQuery("select * from Payments p where p.sender = " + id, Payment.class)
				.getResultList();
		
		if (payments.size() == 0) {
			model.addAttribute("info", "User has not sent any payments!");
		} else {
			model.addAttribute("payments", payments);
		}
		
		return "payment/list-sent";
	}
	
	@Transactional
	@RequestMapping(value = "/balance", method = {RequestMethod.GET, RequestMethod.POST})
	public String balance(
			@RequestParam(value = "amount", required = false) String amount,
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

		em.joinTransaction();
		em.createNativeQuery("Update Users u set balance = balance + " + amount + "where id = " + security.getCurrentUserId()).executeUpdate();
		
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
		
		List<EventMembership> memberships = eventService.getEventsByMembership(security.getCurrentUserId());
		Map<Long, Event> events = new HashMap<Long, Event>();
		Map<Long, User> users = new HashMap<Long, User>();
		Map<Long, EventMembership> eventMemberships = new HashMap<Long, EventMembership>();
				
		 if (eventId == null && amount == null) {
			for (EventMembership m: memberships) {
				events.put(m.getEventId(), eventService.getEventById(m.getEventId()));
				users.put(m.getEventId(), userService.loadUserById(eventService.getEventById(m.getEventId()).getOwner()));
				eventMemberships.put(m.getEventId(), m);
			}
			model.addAttribute("users", users);
			model.addAttribute("events", events);
			model.addAttribute("memberships", eventMemberships);
			return "payment/make-payment";
		} else if (amount == null) {

			events.put(eventId, eventService.getEventById(eventId));
			users.put(eventId, userService.loadUserById(eventService.getEventById(eventId).getOwner()));
			for (EventMembership em: memberships) {
				if (eventId == em.getEventId()) {
					eventMemberships.put(eventId, em);
				}
			}
			
			model.addAttribute("users", users);
			model.addAttribute("events", events);
			model.addAttribute("memberships", eventMemberships);
			return "payment/make-payment";			
		}

		if (!paymentService.makePayment(eventService.getEventById(eventId), amount)) {
			model.addAttribute("danger", "Insufficient funds in your account!");
			for (EventMembership m: memberships) {
				events.put(m.getEventId(), eventService.getEventById(m.getEventId()));
				users.put(m.getEventId(), userService.loadUserById(m.getUser()));
				eventMemberships.put(m.getEventId(), m);
			}
			model.addAttribute("users", users);
			model.addAttribute("events", events);
			model.addAttribute("memberships", eventMemberships);
			return "payment/make-payment";
		}

		UserDetails currentUser = userService.loadUserByUsername(security.getSecurityContext().getUsername());
		
		Authentication authentication = new UsernamePasswordAuthenticationToken(currentUser, currentUser.getPassword(), currentUser.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		redirectAttrs.addFlashAttribute("success", "Payment sent successfully!");
		return "redirect:/payment/list-sent/"+security.getCurrentUserId();
	}
}
