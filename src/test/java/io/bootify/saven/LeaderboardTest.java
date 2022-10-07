package io.bootify.saven;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import io.bootify.saven.domain.*;
import io.bootify.saven.model.*;
import io.bootify.saven.repos.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)

public class LeaderboardTest {

	@LocalServerPort
	private int port;

	private final String baseUrl = "http://localhost:";

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private LeaderboardRepository leaderboard;

	@Autowired
	private UserRepository users;

	@Autowired
	private UserLeaderboardRepository userleaderboard;

	@AfterEach
	void tearDown() {
		userleaderboard.deleteAll();
		leaderboard.deleteAll();
		users.deleteAll();
	}

	@Test
	public void getLeaderboard() throws Exception {
		URI uri = new URI(baseUrl + port + "/api/leaderboards");
		Leaderboard leaderboardtest = new Leaderboard(1);
		leaderboard.save(leaderboardtest);

		ResponseEntity<LeaderboardDTO[]> result = restTemplate.getForEntity(uri, LeaderboardDTO[].class);
		LeaderboardDTO[] leaderboard = result.getBody();

		assertEquals(200, result.getStatusCode().value());
		assertNotNull(leaderboard);
		assertEquals(leaderboardtest.getId(), leaderboard[leaderboard.length - 1].getId());
	}

	@Test
	public void getUserLeaderboard() throws Exception {
		URI uri = new URI(baseUrl + port + "/api/userLeaderboards");

		Leaderboard leaderboardtest = new Leaderboard(1);
		leaderboard.save(leaderboardtest);
		User usertest = new User("fuck u", "fucking cb", "legit fuck u", "motherfuck", 100);
		users.save(usertest);
		UserLeaderboard userleaderboardtest = new UserLeaderboard(1.1, leaderboardtest, usertest);
		leaderboard.save(leaderboardtest);

		userleaderboard.save(userleaderboardtest);

		ResponseEntity<UserLeaderboardDTO[]> result = restTemplate.getForEntity(uri, UserLeaderboardDTO[].class);
		UserLeaderboardDTO userleaderboard[] = result.getBody();
		assertEquals(200, result.getStatusCode().value());
		assertNotNull(userleaderboard);
		assertEquals(userleaderboardtest.getId(), userleaderboard[userleaderboard.length - 1].getId());
	}

	@Test
	public void getLeaderboard_InvalidLeaderboardId_Failure() throws Exception {
		URI uri = new URI(baseUrl + port + "/api/leaderboards/d0c9e19b-00b7-43b7-9880-7e28ccfd7bb9");

		ResponseEntity<Leaderboard> result = restTemplate.getForEntity(uri, Leaderboard.class);

		assertEquals(404, result.getStatusCode().value());
	}

	@Test
	public void getUserLeaderboard_InvalidUserLeaderboardId_Failure() throws Exception {
		URI uri = new URI(baseUrl + port + "/api/leaderboards/d0c9e19b-00b7-43b7-9880-7e28ccfd7bb9");

		ResponseEntity<UserLeaderboardDTO> result = restTemplate.getForEntity(uri, UserLeaderboardDTO.class);

		assertEquals(404, result.getStatusCode().value());
	}

	@Test
	public void overwriteLeaderboard() throws Exception {
		URI uri = new URI(baseUrl + port + "/api/leaderboards");
		Leaderboard leaderboardtest = new Leaderboard(1);
		leaderboard.save(leaderboardtest);
		leaderboardtest.setMonth(12);
		leaderboard.save(leaderboardtest);

		ResponseEntity<LeaderboardDTO[]> result = restTemplate.getForEntity(uri, LeaderboardDTO[].class);
		LeaderboardDTO[] leaderboard = result.getBody();

		assertEquals(200, result.getStatusCode().value());
		assertNotNull(leaderboard);
		assertEquals(1, leaderboard.length);
		assertEquals(leaderboardtest.getId(), leaderboard[leaderboard.length - 1].getId());
		assertEquals(leaderboardtest.getMonth(), leaderboard[leaderboard.length - 1].getMonth());
	}

	@Test
	public void overwriteUserLeaderboard() throws Exception {
		URI uri = new URI(baseUrl + port + "/api/userLeaderboards");
		Leaderboard leaderboardtest = new Leaderboard(1);
		leaderboard.save(leaderboardtest);
		User usertest = new User("kill", "me", "pls", "end my misery", 100);
		users.save(usertest);
		UserLeaderboard userleaderboardtest = new UserLeaderboard(1.1, leaderboardtest, usertest);
		userleaderboard.save(userleaderboardtest);

		leaderboardtest.setMonth(12);
		userleaderboardtest.setLeaderboard(leaderboardtest);
		usertest.setHousingType("legit kms");
		userleaderboardtest.setUser(usertest);
		userleaderboard.save(userleaderboardtest);

		ResponseEntity<UserLeaderboardDTO[]> result = restTemplate.getForEntity(uri, UserLeaderboardDTO[].class);
		UserLeaderboardDTO userleaderboard[] = result.getBody();

		assertEquals(200, result.getStatusCode().value());
		assertNotNull(userleaderboard);
		assertEquals(1, userleaderboard.length);
		assertEquals(userleaderboardtest.getId(), userleaderboard[userleaderboard.length - 1].getId());
		assertEquals(usertest.getId(), userleaderboard[userleaderboard.length - 1].getUser());
	}
}
