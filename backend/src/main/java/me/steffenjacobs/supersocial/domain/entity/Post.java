package me.steffenjacobs.supersocial.domain.entity;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import me.steffenjacobs.supersocial.domain.Platform;

/** @author Steffen Jacobs */
@Entity
public class Post {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;

	@Column
	private String text;

	@Column
	private int platformId;

	@Column
	private String externalId;

	@Column
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@ManyToOne
	private SupersocialUser creator;

	public Post() {
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Platform getPlatform() {
		return Platform.fromId(platformId);
	}

	public void setPlatform(Platform platform) {
		this.platformId = platform.getId();
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public UUID getId() {
		return id;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreator(SupersocialUser creator) {
		this.creator = creator;
	}

	public SupersocialUser getCreator() {
		return creator;
	}
}
