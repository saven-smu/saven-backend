package io.bootify.saven;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;

import org.json.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.http.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.bootify.saven.domain.*;
import io.bootify.saven.model.*;
import io.bootify.saven.repos.*;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserLeaderboardTest {

	@LocalServerPort
	private int port;

	private final String baseUrl = "http://localhost:";

	@Autowired
	private LeaderboardRepository leaderboard;

	@Autowired
	private UserRepository users;

	@Autowired
	private UserLeaderboardRepository userleaderboard;

	@Autowired
    protected MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

    @MockBean
    private JwtDecoder jwtDecoder;

	@AfterEach
	void tearDown() {
		userleaderboard.deleteAll();
		leaderboard.deleteAll();
		users.deleteAll();
	}

	@Test
	public void getUserLeaderboardWithToken() throws Exception {
		URI uri = new URI(baseUrl + port + "/api/userLeaderboards");

		Leaderboard leaderboardtest = new Leaderboard(1);
		leaderboard.save(leaderboardtest);

		User usertest = new User("John Doe", "Singapore", "test@gmail.com", "HDB-5room", 4);
		users.save(usertest);

		UserLeaderboard userleaderboardtest = new UserLeaderboard(1.1, leaderboardtest, usertest);
		userleaderboard.save(userleaderboardtest);

		RequestBuilder request = MockMvcRequestBuilders.get(uri).with(SecurityMockMvcRequestPostProcessors.jwt());
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();
		String responseAsString = response.getContentAsString();
		UserLeaderboardDTO userleaderboard[] = objectMapper.readValue(responseAsString, UserLeaderboardDTO[].class);
		
		assertEquals(200, response.getStatus());
		assertNotNull(userleaderboard);
		assertEquals(userleaderboardtest.getId(), userleaderboard[userleaderboard.length - 1].getId());
	}

    @Test
	public void getUserLeaderboardWithoutToken() throws Exception {
		URI uri = new URI(baseUrl + port + "/api/userLeaderboards");

		Leaderboard leaderboardtest = new Leaderboard(1);
		leaderboard.save(leaderboardtest);

		User usertest = new User("John Doe", "Singapore", "test@gmail.com", "HDB-5room", 4);
		users.save(usertest);

		UserLeaderboard userleaderboardtest = new UserLeaderboard(1.1, leaderboardtest, usertest);
		userleaderboard.save(userleaderboardtest);

		RequestBuilder request = MockMvcRequestBuilders.get(uri).with(SecurityMockMvcRequestPostProcessors.csrf());
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();
		
		assertEquals(401, response.getStatus());
	}

	@Test
	public void getInvalidUserLeaderboardWithToken() throws Exception {
		URI uri = new URI(baseUrl + port + "/api/userLeaderboards/d0c9e19b-00b7-43b7-9880-7e28ccfd7bb9");

		RequestBuilder request = MockMvcRequestBuilders
				.get(uri)
				.with(SecurityMockMvcRequestPostProcessors.jwt())
				.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();

		assertEquals(404, response.getStatus());
	}

    @Test
	public void getInvalidUserLeaderboardWithoutToken() throws Exception {
		
		URI uri = new URI(baseUrl + port + "/api/userLeaderboards/d0c9e19b-00b7-43b7-9880-7e28ccfd7bb9");

		RequestBuilder request = MockMvcRequestBuilders
								.get(uri);
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();

		assertEquals(404, response.getStatus());
	}

    @Test
	public void addUserLeaderboardWithToken() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/userLeaderboards");

		Leaderboard leaderboardtest = new Leaderboard(1);
		leaderboard.save(leaderboardtest);

		User usertest = new User("John Doe", "Singapore", "test@gmail.com", "HDB-5room", 4);
		users.save(usertest);

        JSONObject jsonContent = new JSONObject();
		jsonContent.put("position", "1");
		jsonContent.put("computedScore", "3.14");
		jsonContent.put("leaderboard", leaderboardtest.getId());
		jsonContent.put("user", usertest.getId());

		RequestBuilder request = MockMvcRequestBuilders
								.post(uri)
								.with(SecurityMockMvcRequestPostProcessors.jwt())
								.content(jsonContent.toString())
								.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();

		assertEquals(201, response.getStatus());
	}

    @Test
	public void addUserLeaderboardWithoutToken() throws Exception {
        URI uri = new URI(baseUrl + port + "/api/userLeaderboards");

		Leaderboard leaderboardtest = new Leaderboard(1);
		leaderboard.save(leaderboardtest);

		User usertest = new User("John Doe", "Singapore", "test@gmail.com", "HDB-5room", 4);
		users.save(usertest);

        JSONObject jsonContent = new JSONObject();
		jsonContent.put("position", "1");
		jsonContent.put("computedScore", "3.14");
		jsonContent.put("leaderboard", leaderboardtest.getId());
		jsonContent.put("user", usertest.getId());

		RequestBuilder request = MockMvcRequestBuilders
								.post(uri)
								.with(SecurityMockMvcRequestPostProcessors.csrf())
								.content(jsonContent.toString())
								.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();

		assertEquals(401, response.getStatus());
	}

	@Test
	public void updateUserLeaderboardWithToken() throws Exception {
		Leaderboard leaderboardtest = new Leaderboard(1);
		leaderboard.save(leaderboardtest);

		User usertest = new User("John Doe", "Singapore", "test@gmail.com", "HDB-5room", 4);
		users.save(usertest);

		UserLeaderboard userleaderboardtest = new UserLeaderboard(1.1, leaderboardtest, usertest);
		userleaderboard.save(userleaderboardtest);

		User usertest2 = new User("John Doe 2", "Singapore", "test@gmail.com", "HDB-5room", 4);

        JSONObject jsonContent = new JSONObject();
		jsonContent.put("position", "1");
		jsonContent.put("computedScore", "3.14");
		jsonContent.put("leaderboard", leaderboardtest.getId());
		jsonContent.put("user", usertest2.getId());
		
        URI uri = new URI(baseUrl + port + "/api/userLeaderboards/" + userleaderboardtest.getId());

		RequestBuilder putRequest = MockMvcRequestBuilders
								.put(uri)
								.with(SecurityMockMvcRequestPostProcessors.jwt())
								.content(jsonContent.toString())
								.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(putRequest).andReturn().getResponse();

        assertEquals(200, response.getStatus());

        URI uri2 = new URI(baseUrl + port + "/api/userLeaderboards");

		RequestBuilder getRequest = MockMvcRequestBuilders.get(uri2).with(SecurityMockMvcRequestPostProcessors.jwt());
		MockHttpServletResponse updatedResponse = mockMvc.perform(getRequest).andReturn().getResponse();
		String responseAsString = updatedResponse.getContentAsString();
		UserLeaderboardDTO[] userleaderboard = objectMapper.readValue(responseAsString, UserLeaderboardDTO[].class);

		assertNotNull(userleaderboard);
		assertEquals(1, userleaderboard.length);
		assertEquals(userleaderboardtest.getId(), userleaderboard[userleaderboard.length - 1].getId());
		assertEquals(usertest2.getId(), userleaderboard[userleaderboard.length - 1].getUser());
	}

    @Test
	public void updateUserLeaderboardWithoutToken() throws Exception {

		Leaderboard leaderboardtest = new Leaderboard(1);
		leaderboard.save(leaderboardtest);

		User usertest = new User("John Doe", "Singapore", "test@gmail.com", "HDB-5room", 4);
		users.save(usertest);

		UserLeaderboard userleaderboardtest = new UserLeaderboard(1.1, leaderboardtest, usertest);
		userleaderboard.save(userleaderboardtest);

		User usertest2 = new User("John Doe 2", "Singapore", "test@gmail.com", "HDB-5room", 4);

        JSONObject jsonContent = new JSONObject();
		jsonContent.put("position", "1");
		jsonContent.put("computedScore", "3.14");
		jsonContent.put("leaderboard", leaderboardtest.getId());
		jsonContent.put("user", usertest2.getId());
		
        URI uri = new URI(baseUrl + port + "/api/userLeaderboards/" + userleaderboardtest.getId());

		RequestBuilder putRequest = MockMvcRequestBuilders
								.put(uri)
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
								.content(jsonContent.toString())
								.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(putRequest).andReturn().getResponse();

        assertEquals(401, response.getStatus());
    }

    @Test
	public void deleteUserLeaderboardWithToken() throws Exception {
		Leaderboard leaderboardtest = new Leaderboard(1);
		leaderboard.save(leaderboardtest);

		User usertest = new User("John Doe", "Singapore", "test@gmail.com", "HDB-5room", 4);
		users.save(usertest);

		UserLeaderboard userleaderboardtest = new UserLeaderboard(1.1, leaderboardtest, usertest);
		userleaderboard.save(userleaderboardtest);

		URI uri = new URI(baseUrl + port + "/api/userLeaderboards/" + userleaderboardtest.getId());

		RequestBuilder request = MockMvcRequestBuilders
				.delete(uri)
				.with(SecurityMockMvcRequestPostProcessors.jwt());
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();

		assertEquals(204, response.getStatus());
	}

    @Test
	public void deleteUserLeaderboardWithoutToken() throws Exception {
		Leaderboard leaderboardtest = new Leaderboard(1);
		leaderboard.save(leaderboardtest);

		User usertest = new User("John Doe", "Singapore", "test@gmail.com", "HDB-5room", 4);
		users.save(usertest);

		UserLeaderboard userleaderboardtest = new UserLeaderboard(1.1, leaderboardtest, usertest);
		userleaderboard.save(userleaderboardtest);

		URI uri = new URI(baseUrl + port + "/api/leaderboards/" + leaderboardtest.getId());

		RequestBuilder request = MockMvcRequestBuilders
				.delete(uri)
				.with(SecurityMockMvcRequestPostProcessors.csrf());
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();

		assertEquals(401, response.getStatus());
	}
}
