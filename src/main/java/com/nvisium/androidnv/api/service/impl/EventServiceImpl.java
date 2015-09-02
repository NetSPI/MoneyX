package com.nvisium.androidnv.api.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nvisium.androidnv.api.model.Event;
import com.nvisium.androidnv.api.model.EventMembership;
import com.nvisium.androidnv.api.model.User;
import com.nvisium.androidnv.api.repository.EventMembershipRepository;
import com.nvisium.androidnv.api.repository.EventRepository;
import com.nvisium.androidnv.api.repository.UserRepository;
import com.nvisium.androidnv.api.security.SecurityUtils;
import com.nvisium.androidnv.api.service.EventService;

@Service
@Qualifier(value = "eventService")
public class EventServiceImpl implements EventService {

	@Autowired
	private EventRepository eventRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private EventMembershipRepository eventMembershipRepository;
	
	@Autowired
	SecurityUtils security;

	/*
	 * Inserts a new event
	 */
	public Long addEvent(String name, BigDecimal amount) {
		Event event = new Event();
		event.populateEvent(security.getCurrentUserId(), name, amount,
				new Date(System.currentTimeMillis()), false);
		eventRepository.save(event);
		return event.getId();
	}

	/*
	 * Updates an event
	 */
	@Transactional
	public void updateEvent(Long owner, String name, BigDecimal amount, Boolean hidden, Long id) {
		eventRepository.updateEventById(owner, name, amount, hidden, id);
	}

	/*
	 * Inserts a new event membership
	 */
	public void addEventMembership(Long event, Long user, BigDecimal amount) {
		EventMembership eventMembership = new EventMembership();
		eventMembership.populateEventMembership(event, user, amount, new Date(
				System.currentTimeMillis()));
		eventMembershipRepository.save(eventMembership);
	}

	@Transactional
	public void deleteEventMembership(Long eventId, Long userId) {
		eventMembershipRepository.deleteEventByIdAndUser(eventId, userId);
	}
	
	public boolean isUserMember(Long eventId, Long userId) {
		return eventMembershipRepository.isUserMember(eventId, userId);
	}

	public List<Event> getEventsByOwner(Long user) {
		return eventRepository.findEventsByOwner(user);
	}
	
	public List<Event> getPublicEvents() {
		return eventRepository.findByHiddenFalse();
	}

	@Transactional
	public void deleteEvent(Long id) {
		eventRepository.deleteEventById(id);
	}
	
	public Event getEventById(Long id) {
		return eventRepository.getEventById(id);
	}

	public List<EventMembership> getEventsByMembership(Long userId) {
		return eventMembershipRepository.findEventMembershipByUserId(userId);
	}
	
	public List<User> getUsersbyEventMembership(Long eventId) {
		List<EventMembership> eventMemberships = eventMembershipRepository.findEventMembershipsByEventId(eventId);
		List<User> users = new LinkedList<User>();
		for (EventMembership tempMembership: eventMemberships) {
			users.add(userRepository.findById(tempMembership.getUser()));
		}
		return users;
		
	}

	public List<EventMembership> getMembershipsByEvent(Long eventId) {
		return eventMembershipRepository.findEventMembershipsByEventId(eventId);
	}
}
