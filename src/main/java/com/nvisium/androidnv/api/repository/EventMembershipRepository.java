package com.nvisium.androidnv.api.repository;

import java.math.BigDecimal;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nvisium.androidnv.api.model.EventMembership;

@Repository
@Qualifier(value = "eventMembershipRepository")
public interface EventMembershipRepository extends
		CrudRepository<EventMembership, Long> {

	@Query("delete EventMembership where eventId = ?1 and user = ?2")
	@Modifying
	@Transactional
	public void deleteEventByIdAndUser(Long event, Long user);
	
	@Query("select e from EventMembership e where e.user = ?1")
	public List<EventMembership> findEventMembershipByUserId(Long userId);

	@Query("select e from EventMembership e where e.eventId = ?1")
	public List<EventMembership> findEventMembershipsByEventId(Long eventId);
	
	@Query("select e from EventMembership e where e.eventId = ?1 and e.user = ?2")
	public EventMembership findEventMembershipByEventIdAndUserId(Long eventId, Long userId);

	@Query("select count(e) > 0 from EventMembership e where e.eventId = ?1 and e.user = ?2")
	public boolean isUserMember(Long eventId, Long userId);
	
	@Modifying
	@Transactional
	@Query("Update EventMembership e set e.amount = e.amount - ?3 where e.eventId = ?1 and e.user = ?2")
	public void makePayment(Long eventId, Long userId, BigDecimal amount);
}
