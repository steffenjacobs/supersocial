package me.steffenjacobs.supersocial.service;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import me.steffenjacobs.supersocial.domain.dto.PostDTO;
import me.steffenjacobs.supersocial.persistence.ScheduledPostPersistenceManager;

/** @author Steffen Jacobs */
@Component
public class ScheduledPublishingService {

	private static final Logger LOG = LoggerFactory.getLogger(ScheduledPublishingService.class);

	@Autowired
	private ScheduledPostPersistenceManager scheduledPostPersistenceManager;

	@Autowired
	private PostPublishingService postPublishingService;

	@PostConstruct
	public void setup() {
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::checkAndPublishPosts, 0, 60, TimeUnit.SECONDS);
	}

	private void checkAndPublishPosts() {
		LOG.info("Running publish job...");
		Date now = new Date();
		AtomicInteger counter = new AtomicInteger();
		scheduledPostPersistenceManager.getAllScheduledAndNotPublishedPosts().forEach(p -> {
			if (now.after(p.getScheduledDate())) {
				postPublishingService.publish(PostDTO.fromPost(p.getPost(), ""));
				counter.getAndIncrement();
			}
		});
		LOG.info("Finished publish job. Attempted to published {} posts.", counter.get());
	}
}
