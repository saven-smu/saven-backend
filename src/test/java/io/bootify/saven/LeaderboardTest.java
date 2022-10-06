package io.bootify.saven;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.time.Month;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import io.bootify.saven.domain.*;
import io.bootify.saven.repos.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)

public class LeaderboardTest {
    
	@LocalServerPort
	private int port;

	private final String baseUrl = "http://localhost:";

	@Autowired
	/**
	 * Use TestRestTemplate for testing a real instance of your application as an external actor.
	 * TestRestTemplate is just a convenient subclass of RestTemplate that is suitable for integration tests.
 	 * It is fault tolerant, and optionally can carry Basic authentication headers.
	 */
	private TestRestTemplate restTemplate;

	@Autowired
	private LeaderboardRepository leaderboard;

	@Autowired
	private UserRepository users;

    @Autowired
    private UserLeaderboardRepository userleaderboard;

	@AfterEach
	void tearDown(){
		// clear the database after each test
		leaderboard.deleteAll();
		users.deleteAll();
        userleaderboard.deleteAll();
	}

	@Test
	public void getLeaderboard() throws Exception {
		URI uri = new URI(baseUrl + port + "/api/leaderboards");
        Leaderboard leaderboardtest = new Leaderboard();
		leaderboard.save(leaderboardtest);
		
		ResponseEntity<Leaderboard[]> result = restTemplate.getForEntity(uri, Leaderboard[].class);
		Leaderboard[] leaderboard = result.getBody();
		
		assertEquals(200, result.getStatusCode().value());
        assertNotNull(leaderboard);
        assertEquals(leaderboardtest.getId(), leaderboard[0].getId());

        // should i check got 1 or check that not null cause assertEquals got squiggly lines
        // assertEquals(1, leaderboard.length);
        // assertNotNull(leaderboard); 
	}

	@Test
	public void getUserLeaderboard() throws Exception {
        UserLeaderboard userleaderboardtest = new UserLeaderboard();
        // UUID id = ;
		userleaderboard.save(userleaderboardtest);

		URI uri = new URI(baseUrl + port + "/api/userLeaderboards");

		ResponseEntity<UserLeaderboard[]> result = restTemplate.getForEntity(uri, UserLeaderboard[].class);
		UserLeaderboard[] userleaderboard = result.getBody();
		assertEquals(200, result.getStatusCode().value());
        // assertNotNull(userleaderboard);
        assertNotNull(userleaderboard);
		assertEquals(userleaderboardtest.getId(), userleaderboard[0].getId());
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
		
		ResponseEntity<UserLeaderboard> result = restTemplate.getForEntity(uri, UserLeaderboard.class);
			
		assertEquals(404, result.getStatusCode().value());
	}

	// @Test
	// public void addBook_Success() throws Exception {
	// 	URI uri = new URI(baseUrl + port + "/books");
	// 	Book book = new Book("A New Hope");
	// 	users.save(new User("admin", encoder.encode("goodpassword"), "ROLE_ADMIN"));

	// 	ResponseEntity<Book> result = restTemplate.withBasicAuth("admin", "goodpassword")
	// 									.postForEntity(uri, book, Book.class);
			
	// 	assertEquals(201, result.getStatusCode().value());
	// 	assertEquals(book.getTitle(), result.getBody().getTitle());
	// }

	// /**
	//  * TODO: Activity 2 (Week 6)
	//  * Add integration tests for delete/update a book.
	//  * For delete operation: there should be two tests for success and failure (book not found) scenarios.
	//  * Similarly, there should be two tests for update operation.
	//  * You should assert both the HTTP response code, and the value returned if any
	//  * 
	//  * For delete and update, you should use restTemplate.exchange method to send the request
	//  * E.g.: ResponseEntity<Void> result = restTemplate.withBasicAuth("admin", "goodpassword")
	// 									.exchange(uri, HttpMethod.DELETE, null, Void.class);
	//  */
	// // your code here
	// public void deleteBook_ValidBookID_Success() throws Exception {
	// 	Book book = books.save(new Book("A New Hope"));
	// 	URI uri = new URI(baseUrl + port + "/books/" + book.getId().longValue());
	// 	users.save(new User("admin", encoder.encode("goodpassword"), "ROLE_ADMIN"));
	// 	ResponseEntity<Void> result = restTemplate.withBasicAuth("admin", "goodpassword")
	// 	.exchange(uri, HttpMethod.DELETE, null, Void.class);
	// 	assertEquals(200, result.getStatusCode().value());
	// 	Optional<Book> emptyValue = Optional.empty();
	// 	assertEquals(emptyValue, books.findById(book.getId()));
	// }
}
