package com.nvisium.androidnv.api.model;

import javax.persistence.*;

@Entity
@Table(name = "friendRequest")
public class FriendRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Version
	private Long version;

	@ManyToOne(targetEntity=User.class)
	@JoinColumn(name = "receiver")
	private User receiver;

	@ManyToOne(targetEntity=User.class)
	@JoinColumn(name = "sender")
	private User sender;

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
}
