package com.nvisium.androidnv.api.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.nvisium.androidnv.api.model.Payment;
import com.nvisium.androidnv.api.repository.EventMembershipRepository;
import com.nvisium.androidnv.api.repository.EventRepository;
import com.nvisium.androidnv.api.repository.PaymentRepository;
import com.nvisium.androidnv.api.repository.UserRepository;
import com.nvisium.androidnv.api.security.SecurityUtils;
import com.nvisium.androidnv.api.service.PaymentService;

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
	SecurityUtils security;

	@Override
	public boolean makePayment(Long eventId, BigDecimal amount) {
		Payment payment = new Payment();
		
		if (amount.compareTo(userRepository.findById(security.getCurrentUserId()).getBalance()) == 1) {
			return false;
		}

		payment.populatePayment(eventId, amount, security.getCurrentUserId(),
				eventRepository.getEventOwnerIdByEventId(eventId));
		paymentRepository.save(payment);
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
