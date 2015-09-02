package com.nvisium.androidnv.api.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "payments")
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Version
	private Long version;

	@ManyToOne(targetEntity=Event.class)
	@JoinColumn(name = "event")
	private Event event;

	@Column(name = "amount")
	private BigDecimal amount;

	@Column(name = "timestamp")
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;

	@ManyToOne(targetEntity=User.class)
	@JoinColumn(name = "sender")
	private User sender;

	@ManyToOne(targetEntity=User.class)
	@JoinColumn(name = "receiver")
	private User receiver;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public java.util.Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(java.util.Date timestamp) {
		this.timestamp = timestamp;
	}

	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	public User getReceiver() {
		return receiver;
	}

	public void setReceiver(User receiver) {
		this.receiver = receiver;
	}

	public void populatePayment(Event event, BigDecimal amount, User sender, User receiver) {
		this.event = event;
		this.amount = amount;
		this.sender = sender;
		this.receiver = receiver;
	}
}
