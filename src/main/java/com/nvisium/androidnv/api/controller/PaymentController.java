package com.nvisium.androidnv.api.controller;

import java.math.BigDecimal;
import java.util.List;

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

import com.nvisium.androidnv.api.model.Payment;
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
		List<Payment> payments = paymentService.getReceivedPayments(id);
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
		List<Payment> payments = paymentService.getSentPayments(id);
		if (payments.size() == 0) {
			model.addAttribute("info", "User has not sent any payments!");
		}
		model.addAttribute("payments", payments);
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
		return "redirect:/get-settings";
	}

	/*
	 * VULN: IDOR
	 */
	@RequestMapping(value = "/make-payment", method = {RequestMethod.GET, RequestMethod.POST})
	public String makePayment(
			@RequestParam(value = "event", required = false) Long eventId,
			@RequestParam(value = "user", required = false) Long userId,
			@RequestParam(value = "amount", required = false) BigDecimal amount,
			RedirectAttributes redirectAttrs,
			Model model) {
		
		/* if (eventId == null || userId == null || amount == null) { */
		if (userId == null || amount == null) {
			model.addAttribute("users", userService.getPublicUsers());
			return "payment/make-payment";
		}
		
		// Holy crap, this logic is broken. why events here? not users?
		
		if (!paymentService.makePayment(eventId, amount)) {
			model.addAttribute("danger", "Insufficient funds in your account!");
		}
		eventService.deleteEventMembership(eventId, userId);
		redirectAttrs.addFlashAttribute("success", "Payment sent successfully!");
		return "redirect:/dashboard";
	}
}
