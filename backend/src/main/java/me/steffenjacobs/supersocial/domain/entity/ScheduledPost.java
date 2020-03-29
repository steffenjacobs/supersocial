package me.steffenjacobs.supersocial.domain.entity;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
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
public class ScheduledPost implements Secured {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;

	@OneToOne
	private AccessControlList accessControlList;
	@OneToOne
	private Post post;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date scheduledDate;

	@Column
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@ManyToOne
	private SupersocialUser creator;

	public ScheduledPost() {
	}

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}

	public Date getScheduledDate() {
		return scheduledDate;
	}

	public void setScheduledDate(Date scheduledDate) {
		this.scheduledDate = scheduledDate;
	}

	public SupersocialUser getCreator() {
		return creator;
	}

	public void setCreator(SupersocialUser creator) {
		this.creator = creator;
	}

	public UUID getId() {
		return id;
	}

	public Date getCreated() {
		return created;
	}

	@Override
	public SecuredType getSecuredType() {
		return SecuredType.ScheduledPost;
	}

	@Override
	public AccessControlList getAccessControlList() {
		return accessControlList;
	}

	@Override
	public void setAccessControlList(AccessControlList accessControlList) {
		this.accessControlList = accessControlList;
	}

}
