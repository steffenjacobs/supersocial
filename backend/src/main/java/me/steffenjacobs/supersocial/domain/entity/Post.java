package me.steffenjacobs.supersocial.domain.entity;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

/** @author Steffen Jacobs */
@Entity
public class Post implements Secured {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;

	@Column
	private String text;

	@Column
	private String externalId;

	@Column
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date published;

	@ManyToOne
	private SupersocialUser creator;

	@Column(length = 512)
	private String errorMessage;

	@OneToOne
	private AccessControlList accessControlList;

	@ManyToOne(fetch = FetchType.EAGER)
	private SocialMediaAccount socialMediaAccountToPostWith;

	public Post() {
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
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

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public Date getPublished() {
		return published;
	}

	public void setPublished(Date published) {
		this.published = published;
	}

	@Override
	public SecuredType getSecuredType() {
		return SecuredType.Post;
	}

	@Override
	public AccessControlList getAccessControlList() {
		return accessControlList;
	}

	@Override
	public void setAccessControlList(AccessControlList accessControlList) {
		this.accessControlList = accessControlList;
	}

	public SocialMediaAccount getSocialMediaAccountToPostWith() {
		return socialMediaAccountToPostWith;
	}

	public void setSocialMediaAccountToPostWith(SocialMediaAccount socialMediaAccountToPostWith) {
		this.socialMediaAccountToPostWith = socialMediaAccountToPostWith;
	}
}
