package com.nvisium.androidnv.api.service;

import java.math.BigDecimal;
import java.util.List;

import com.nvisium.androidnv.api.model.Event;
import com.nvisium.androidnv.api.model.EventMembership;
import com.nvisium.androidnv.api.model.User;

public interface EventService {

	public Long addEvent(String name, BigDecimal amount);

	public void updateEvent(Long owner, String name, BigDecimal amount, Boolean ispublic, Long id);

	public void addEventMembership(Long event, Long user, BigDecimal amount);

	public void deleteEvent(Long id);

	public void deleteEventMembership(Long eventId, Long userId);
	
	public boolean isUserMember(Long eventId, Long userId);

	public List<Event> getEventsByOwner(Long owner);
	
	public List<Event> getPublicEvents();

	public List<EventMembership> getEventsByMembership(Long userId);
	
	public List<EventMembership> getMembershipsByEvent(Long eventId);
	
	public List<User> getUsersbyEventMembership(Long eventId);
	
	public Event getEventById(Long id);
	
}
