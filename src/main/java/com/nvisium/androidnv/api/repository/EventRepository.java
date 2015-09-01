package com.nvisium.androidnv.api.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nvisium.androidnv.api.model.Event;

@Repository
@Qualifier(value = "eventRepository")
public interface EventRepository extends CrudRepository<Event, Long> {

	@Query("select e from Event e where e.owner = ?1")
	public List<Event> findEventsByOwner(long id);
	
	@Query("select e from Event e where e.id = ?1")
	public Event getEventById(long id);

	@Query("delete from Event where id = ?1")
	@Modifying
	public void deleteEventById(Long id);

	@Query("select owner from Event e where e.id = ?1")
	public Long getEventOwnerIdByEventId(Long eventId);
	
	@Modifying
	@Query("update Event e set e.owner = ?1, e.name = ?2, e.amount = ?3, e.hidden = ?4 where e.id = ?5")
	public void updateEventById(Long owner, String name, BigDecimal amount, Boolean hidden, Long id);

	@Query
	public List<Event> findByHiddenFalse();
	
	@Query
	public List<Event> findByHiddenTrue();
}
