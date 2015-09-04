package com.nvisium.androidnv.api.model;

import javax.persistence.*;

@Entity
@Table(name = "friends")
public class Friend {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Version
	private Long version;

	@ManyToOne(targetEntity=User.class)
	@JoinColumn(name = "user1")
	private User user1;

	@ManyToOne(targetEntity=User.class)
	@JoinColumn(name = "user2")
	private User user2;

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

	public User getUser1() {
		return user1;
	}

	public void setUser1(User user1) {
		this.user1 = user1;
	}

	public User getUser2() {
		return user2;
	}

	public void setUser2(User user2) {
		this.user2 = user2;
	}
}
