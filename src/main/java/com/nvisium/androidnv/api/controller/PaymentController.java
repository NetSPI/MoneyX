package com.nvisium.androidnv.api.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nvisium.androidnv.api.model.Payment;
import com.nvisium.androidnv.api.service.EventService;
import com.nvisium.androidnv.api.service.PaymentService;

@RequestMapping(value = "/payment")
@Controller
public class PaymentController {

	@Autowired
	PaymentService paymentService;
	@Autowired
	EventService eventService;

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

	/*
	 * VULN: IDOR
	 */
	@RequestMapping(value = "/make-payment", method = RequestMethod.POST)
	public String makePayment(
			@RequestParam(value = "event", required = false) Long eventId,
			@RequestParam(value = "user", required = false) Long userId,
			@RequestParam(value = "amount", required = false) BigDecimal amount,
			RedirectAttributes redirectAttrs) {
		
		if (eventId == null || userId == null || amount == null) {
			return "payment/make-payment";
		}
		
		paymentService.makePayment(eventId, amount);
		eventService.deleteEventMembership(eventId, userId);
		redirectAttrs.addFlashAttribute("success", "Payment sent successfully!");
		return "redirect:/get-settings";
	}
}
