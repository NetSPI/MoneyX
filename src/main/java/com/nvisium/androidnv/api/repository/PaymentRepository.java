package com.nvisium.androidnv.api.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nvisium.androidnv.api.model.Payment;

@Repository
@Qualifier(value = "paymentRepository")
public interface PaymentRepository extends CrudRepository<Payment, Long> {

	@Query("select p from Payment p where p.sender = ?1")
	public List<Payment> findPaymentsBySender(Long sender);

	@Query("select p from Payment p where p.receiver = ?1")
	public List<Payment> findPaymentsByReceiver(Long receiver);
}
