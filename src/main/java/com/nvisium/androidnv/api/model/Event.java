package com.nvisium.androidnv.api.model;

import java.math.BigDecimal;

import javax.persistence.*;

@Entity
@Table(name = "events")
public class Event {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Version
	private Long version;

	// TODO: Actually associate this with a user, not just an id that we have to reference...see Payment Model.
	//@ManyToOne(targetEntity=User.class)
	//@JoinColumn(name = "owner")
	//private User owner;
	@Column(name = "owner")
	private Long owner;
	
	@Column(name = "name")
	private String name;

	@Column(name = "amount")
	private BigDecimal amount;
	
	@Column(name = "completed")
	private Boolean completed;
	
	@Column(name = "hidden")
	private Boolean hidden;

	@Column(name = "created")
	@Temporal(TemporalType.TIMESTAMP)
	private java.util.Date created;

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

	public Long getOwner() {
		return owner;
	}

	public void setOwner(Long owner) {
		this.owner = owner;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	public Boolean getCompleted() {
		return completed;
	}
	
	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}
	
	public Boolean getHidden() {
		return hidden;
	}

	public void setHidden(Boolean hidden) {
		this.hidden = hidden;
	}

	public java.util.Date getCreated() {
		return created;
	}

	public void setCreated(java.util.Date created) {
		this.created = created;
	}
	
	public void populateEvent(Long owner, String name, BigDecimal amount,
	java.util.Date created, Boolean hidden) {
		this.owner = owner;
		this.name = name;
		this.amount = amount;
		this.created = created;
		this.completed = false;
		this.hidden = hidden;
	}
}
