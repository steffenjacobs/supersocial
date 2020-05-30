package me.steffenjacobs.supersocial.service;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import me.steffenjacobs.supersocial.domain.SocialMediaAccountRepository;
import me.steffenjacobs.supersocial.persistence.PostPersistenceManager;
import me.steffenjacobs.supersocial.service.exception.CredentialMissingException;
import me.steffenjacobs.supersocial.service.exception.FacebookException;
import me.steffenjacobs.supersocial.service.exception.FacebookPostNotFoundException;
import me.steffenjacobs.supersocial.service.exception.TwitterPostNotFoundException;

/**
 * Automatically fetches analytics data for all published posts and social media
 * accounts hourly.
 * 
 * @author Steffen Jacobs
 */
@Component
public class ScheduledAnalyticsService {
	private static final Logger LOG = LoggerFactory.getLogger(ScheduledAnalyticsService.class);

	@Autowired
	private PostPersistenceManager postPersistenceManager;

	@Autowired
	private StatisticService statisticService;

	@Autowired
	private SocialMediaAccountRepository socialMediaAccountRepository;

	@PostConstruct
	public void setup() {
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::refresh, 1, 60, TimeUnit.MINUTES);
	}

	private void refresh() {
		// TODO: restrict statistics to track
		// TODO: make it possible to change the refresh interval, probably
		// dependent on creation date or change rate.
		long now = System.currentTimeMillis();
		LOG.info("Running analytics job...");
		AtomicInteger postCounter = new AtomicInteger();
		AtomicInteger accountCounter = new AtomicInteger();

		fetchPostAnalytics(postCounter);
		fetchSocialMediaAccountAnalytics(accountCounter);

		LOG.info("Finished analytics job. Fetched analytics for {} posts and {} social media accounts. Took {}ms.", postCounter.get(), accountCounter.get(),
				System.currentTimeMillis() - now);
	}

	/**
	 * Fetch analytics data for all
	 * {@link me.steffenjacobs.supersocial.domain.entity.SocialMediaAccount
	 * social media accounts} and store them into an elasticsearch index
	 */
	private void fetchSocialMediaAccountAnalytics(AtomicInteger accountCounter) {
		StreamSupport.stream(socialMediaAccountRepository.findAll().spliterator(), false).forEach(a -> {
			try {
				statisticService.fetchAll(a);
				accountCounter.incrementAndGet();
				LOG.info("Fetched statistics for account '{}'", a.getId());
			} catch (FacebookException | CredentialMissingException e) {
				LOG.error("Could not fetch statistics for account {}: {}", a.getId(), e.getMessage());
			} catch (Exception e) {
				LOG.error("Could not fetch statistics for account {}: {}", a.getId(), e.getMessage(), e);
			}
		});
	}

	/**
	 * Fetch analytics data for all published
	 * {@link me.steffenjacobs.supersocial.domain.entity.Post posts} and store
	 * them into an elasticsearch index.
	 */
	private void fetchPostAnalytics(AtomicInteger postCounter) {
		postPersistenceManager.getAllPosts().forEach(p -> {
			try {
				if (p.getPublished() == null) {
					return;
				}
				statisticService.fetchAll(p);
				postCounter.incrementAndGet();
				LOG.info("Fetched statistics for post '{}'", p.getId());
			} catch (FacebookPostNotFoundException | TwitterPostNotFoundException e) {
				LOG.error("Could not fetch statistics for post {}: Post does not exist anymore.", p.getId());
			} catch (FacebookException | CredentialMissingException e) {
				LOG.error("Could not fetch statistics for post {}: {}", p.getId(), e.getMessage());
			} catch (Exception e) {
				LOG.error("Could not fetch statistics for post {}: {} ", p.getId(), e.getMessage(), e);
			}
		});
	}
}
