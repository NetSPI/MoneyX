package com.nvisium.androidnv.api.service;

import java.math.BigDecimal;
import java.util.List;

import com.nvisium.androidnv.api.model.Payment;
import com.nvisium.androidnv.api.model.Event;
import com.nvisium.androidnv.api.model.User;


public interface PaymentService {

	public boolean makePayment(Event event, BigDecimal amount);

	public List<Payment> getSentPayments(User user);

	public List<Payment> getReceivedPayments(User user);
	
	public BigDecimal getTotalPayments(Event event);
}
