package com.nvisium.androidnv.api.service;

import java.math.BigDecimal;
import java.util.List;

import com.nvisium.androidnv.api.model.Payment;

public interface PaymentService {

	public boolean makePayment(Long eventId, BigDecimal amount);

	public List<Payment> getSentPayments(Long user);

	public List<Payment> getReceivedPayments(Long user);
}
