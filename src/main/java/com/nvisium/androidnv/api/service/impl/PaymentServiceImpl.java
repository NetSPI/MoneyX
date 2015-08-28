package com.nvisium.androidnv.api.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.nvisium.androidnv.api.model.Event;
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

	@Override
	public boolean makePayment(Long eventMembershipId, BigDecimal amount) {
		Payment payment = new Payment();
		
		EventMembership eventMembership = eventMembershipRepository.findOne(eventMembershipId);
		Long eventId = eventMembership.getEventId();
		
		if (amount.compareTo(userRepository.findById(security.getCurrentUserId()).getBalance()) == 1) {
			return false;
		}

		payment.populatePayment(eventId, amount, security.getCurrentUserId(),
				eventRepository.getEventOwnerIdByEventId(eventId));
		paymentRepository.save(payment);
		
		BigDecimal paidAmount = eventMembership.getAmount().min(amount);
		
		userService.debit(security.getCurrentUserId(), paidAmount);
		
		if (eventMembership.getAmount().compareTo(paidAmount) != 1) {
			eventMembershipRepository.delete(eventMembership);
			/* Is the greater event done too? */
			if (eventMembershipRepository.findEventMembershipsByEventId(eventId).size() == 0) {
				Event event = eventRepository.findOne(eventId);
				event.setCompleted(true);
				eventRepository.save(event);
			}
		} else {
			eventMembershipRepository.makePayment(eventId, security.getCurrentUserId(), paidAmount);
		}
		
		return true;
	}

	@Override
	public List<Payment> getSentPayments(Long user) {
		return paymentRepository.findPaymentsBySender(user);
	}

	@Override
	public List<Payment> getReceivedPayments(Long user) {
		return paymentRepository.findPaymentsByReceiver(user);
	}
}
