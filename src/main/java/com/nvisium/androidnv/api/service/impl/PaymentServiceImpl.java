package com.nvisium.androidnv.api.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.nvisium.androidnv.api.model.Event;
import com.nvisium.androidnv.api.model.User;
import com.nvisium.androidnv.api.model.EventMembership;
import com.nvisium.androidnv.api.model.Payment;
import com.nvisium.androidnv.api.repository.EventMembershipRepository;
import com.nvisium.androidnv.api.repository.EventRepository;
import com.nvisium.androidnv.api.repository.PaymentRepository;
import com.nvisium.androidnv.api.repository.UserRepository;
import com.nvisium.androidnv.api.security.SecurityUtils;
import com.nvisium.androidnv.api.service.EventService;
import com.nvisium.androidnv.api.service.PaymentService;
import com.nvisium.androidnv.api.service.UserService;

@Service
@Qualifier(value = "paymentService")
public class PaymentServiceImpl implements PaymentService {

	@Autowired
	private PaymentRepository paymentRepository;
	
	@Autowired
	private EventMembershipRepository eventMembershipRepository;
	
	@Autowired
	private EventRepository eventRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private EventService eventService;
	
	@Autowired
	SecurityUtils security;

	public boolean makePayment(Event event, BigDecimal amount) {
		Payment payment = new Payment();
		
		EventMembership eventMembership = eventMembershipRepository.findOne(event.getId());
		Long eventId = eventMembership.getEventId();
		
		if (amount.compareTo(userRepository.findById(security.getCurrentUserId()).getBalance()) == 1) {
			return false;
		}
		User sender = userRepository.findById(security.getCurrentUserId());
		User receiver = userRepository.findById(eventRepository.getEventOwnerIdByEventId(event.getId()));

		payment.populatePayment(event, amount, sender, receiver);
		paymentRepository.save(payment);
		
		BigDecimal paidAmount = eventMembership.getAmount().min(amount);
		
		userService.debit(sender.getId(), paidAmount);
		//userRepository.save(sender);
		userService.credit(receiver.getId(),paidAmount);
		
		if (eventMembership.getAmount().compareTo(paidAmount) != 1) {
			eventMembershipRepository.delete(eventMembership);
			/* Is the greater event done too? */
			if (eventMembershipRepository.findEventMembershipsByEventId(event.getId()).size() == 0) {
				Event e = eventRepository.findOne(event.getId());
				e.setCompleted(true);
				eventRepository.save(e);
			}
		} else {
			eventMembershipRepository.makePayment(eventId, security.getCurrentUserId(), paidAmount);
		}
		
		return true;
	}

	public List<Payment> getSentPayments(User user) {
		return paymentRepository.findPaymentsBySender(user);
	}

	public List<Payment> getReceivedPayments(User user) {
		return paymentRepository.findPaymentsByReceiver(user);
	}
	
	public BigDecimal getTotalPayments(Event event) {
		BigDecimal total = new BigDecimal(0.00);
		
		List<Payment> payments = paymentRepository.findPaymentsByEvent(event);
		
		for (Payment p: payments) {
			total = total.add(p.getAmount());
		}
		
		return total;
	}
}
