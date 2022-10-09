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
public class LeaderboardTest {

	@LocalServerPort
	private int port;

	private final String baseUrl = "http://localhost:";

	@Autowired
	private LeaderboardRepository leaderboard;

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private JwtDecoder jwtDecoder;

	@AfterEach
	void tearDown() {
		if (leaderboard.count() > 0)
			leaderboard.deleteAll();
	}

	@Test
	public void getLeaderboardWithToken() throws Exception {
		URI uri = new URI(baseUrl + port + "/api/leaderboards");

		Leaderboard leaderboardtest = new Leaderboard(1);
		leaderboard.save(leaderboardtest);

		RequestBuilder request = MockMvcRequestBuilders.get(uri).with(SecurityMockMvcRequestPostProcessors.jwt());
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();
		String responseAsString = response.getContentAsString();
		LeaderboardDTO leaderboard[] = objectMapper.readValue(responseAsString, LeaderboardDTO[].class);

		assertEquals(200, response.getStatus());
		assertNotNull(leaderboard);
		assertEquals(leaderboardtest.getId(), leaderboard[leaderboard.length - 1].getId());
	}

	@Test
	public void getLeaderboardWithoutToken() throws Exception {
		URI uri = new URI(baseUrl + port + "/api/leaderboards");

		Leaderboard leaderboardtest = new Leaderboard(1);
		leaderboard.save(leaderboardtest);

		RequestBuilder request = MockMvcRequestBuilders.get(uri).with(SecurityMockMvcRequestPostProcessors.csrf());
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();
		String responseAsString = response.getContentAsString();
		LeaderboardDTO leaderboard[] = objectMapper.readValue(responseAsString, LeaderboardDTO[].class);

		assertEquals(200, response.getStatus());
		assertNotNull(leaderboard);
		assertEquals(leaderboardtest.getId(), leaderboard[leaderboard.length - 1].getId());
	}

	@Test
	public void getInvalidLeaderboardWithToken() throws Exception {
		URI uri = new URI(baseUrl + port + "/api/leaderboards/d0c9e19b-00b7-43b7-9880-7e28ccfd7bb9");

		RequestBuilder request = MockMvcRequestBuilders
				.get(uri)
				.with(SecurityMockMvcRequestPostProcessors.jwt())
				.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();

		assertEquals(404, response.getStatus());
	}

	@Test
	public void getInvalidLeaderboardWithoutToken() throws Exception {
		URI uri = new URI(baseUrl + port + "/api/leaderboards/d0c9e19b-00b7-43b7-9880-7e28ccfd7bb9");

		RequestBuilder request = MockMvcRequestBuilders
				.get(uri)
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();

		assertEquals(401, response.getStatus());
	}

	@Test
	public void addLeaderboardWithToken() throws Exception {
		URI uri = new URI(baseUrl + port + "/api/leaderboards");

		JSONObject jsonContent = new JSONObject();
		jsonContent.put("month", "1");

		RequestBuilder request = MockMvcRequestBuilders
				.post(uri)
				.with(SecurityMockMvcRequestPostProcessors.jwt())
				.content(jsonContent.toString())
				.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();

		assertEquals(201, response.getStatus());
	}

	@Test
	public void addLeaderboardWithoutToken() throws Exception {
		URI uri = new URI(baseUrl + port + "/api/leaderboards");

		JSONObject jsonContent = new JSONObject();
		jsonContent.put("month", "1");

		RequestBuilder request = MockMvcRequestBuilders
				.post(uri)
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.content(jsonContent.toString())
				.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();

		assertEquals(401, response.getStatus()); // idk why its giving 201
	}

	@Test
	public void updateLeaderboardWithToken() throws Exception {
		Leaderboard leaderboardtest = new Leaderboard(1);
		leaderboard.save(leaderboardtest);
		leaderboardtest.setMonth(12);

		JSONObject jsonContent = new JSONObject();
		jsonContent.put("month", "12");

		URI uri = new URI(baseUrl + port + "/api/leaderboards/" + leaderboardtest.getId());

		RequestBuilder request = MockMvcRequestBuilders
				.put(uri)
				.with(SecurityMockMvcRequestPostProcessors.jwt())
				.content(jsonContent.toString())
				.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();

		assertEquals(200, response.getStatus());

		RequestBuilder getRequest = MockMvcRequestBuilders.get(uri).with(SecurityMockMvcRequestPostProcessors.jwt());
		MockHttpServletResponse updatedResponse = mockMvc.perform(getRequest).andReturn().getResponse();
		String responseAsString = updatedResponse.getContentAsString();
		LeaderboardDTO leaderboard = objectMapper.readValue(responseAsString, LeaderboardDTO.class);

		assertNotNull(leaderboard);
		assertEquals(leaderboardtest.getId(), leaderboard.getId());
		assertEquals(leaderboardtest.getMonth(), leaderboard.getMonth());
	}

	@Test
	public void updateLeaderboardWithoutToken() throws Exception {
		Leaderboard leaderboardtest = new Leaderboard(1);
		leaderboard.save(leaderboardtest);

		JSONObject jsonContent = new JSONObject();
		jsonContent.put("month", "12");

		URI uri = new URI(baseUrl + port + "/api/leaderboards" + leaderboardtest.getId());

		RequestBuilder request = MockMvcRequestBuilders
				.put(uri)
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.content(jsonContent.toString())
				.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();

		assertEquals(401, response.getStatus());
	}

	@Test
	public void deleteLeaderboardWithToken() throws Exception {
		Leaderboard leaderboardtest = new Leaderboard(1);
		leaderboard.save(leaderboardtest);

		URI uri = new URI(baseUrl + port + "/api/leaderboards/" + leaderboardtest.getId());

		RequestBuilder request = MockMvcRequestBuilders
				.delete(uri)
				.with(SecurityMockMvcRequestPostProcessors.jwt());
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();

		assertEquals(204, response.getStatus());
	}

	@Test
	public void deleteLeaderboardWithoutToken() throws Exception {
		Leaderboard leaderboardtest = new Leaderboard(1);
		leaderboard.save(leaderboardtest);

		URI uri = new URI(baseUrl + port + "/api/leaderboards/" + leaderboardtest.getId());

		RequestBuilder request = MockMvcRequestBuilders
				.delete(uri)
				.with(SecurityMockMvcRequestPostProcessors.csrf());
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();

		assertEquals(401, response.getStatus());
	}
}
