package io.bootify.saven;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;

import com.nimbusds.jose.shaded.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

	@Test
	public void getLeaderboardWithToken() throws Exception {
		Leaderboard leaderboardtest = new Leaderboard();
		leaderboard.save(leaderboardtest);
		URI uri = new URI(baseUrl + port + "/api/leaderboards/" + leaderboardtest.getId());
		RequestBuilder request = MockMvcRequestBuilders.get(uri).with(SecurityMockMvcRequestPostProcessors.jwt().authorities(new SimpleGrantedAuthority("read:leaderboard")));
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();

		assertEquals(200, response.getStatus());

		leaderboard.deleteById(leaderboardtest.getId());
	}

	@Test
	public void getLeaderboardWithoutToken() throws Exception {

		Leaderboard leaderboardtest = new Leaderboard();
		leaderboard.save(leaderboardtest);

		URI uri = new URI(baseUrl + port + "/api/leaderboards/" + leaderboardtest.getId());

		RequestBuilder request = MockMvcRequestBuilders.get(uri).with(SecurityMockMvcRequestPostProcessors.csrf());
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();

		assertEquals(401, response.getStatus());

		leaderboard.deleteById(leaderboardtest.getId());
	}

	@Test
	public void getInvalidLeaderboardWithToken() throws Exception {
		URI uri = new URI(baseUrl + port + "/api/leaderboards/d0c9e19b-00b7-43b7-9880-7e28ccfd7bb9");

		RequestBuilder request = MockMvcRequestBuilders
				.get(uri)
				.with(SecurityMockMvcRequestPostProcessors.jwt().authorities(new SimpleGrantedAuthority("read:leaderboard")))
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
		jsonContent.put("utilityType", "0");
		jsonContent.put("timeWindow", "0");
		jsonContent.put("storedDateTime", "2022-10-11T09:00:00");

		RequestBuilder request = MockMvcRequestBuilders
				.post(uri)
				.with(SecurityMockMvcRequestPostProcessors.jwt().authorities(new SimpleGrantedAuthority("upload:leaderboard")))
				.content(jsonContent.toString())
				.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();

		assertEquals(201, response.getStatus());

		UUID leaderboardid = objectMapper.readValue(response.getContentAsString(), UUID.class);

		leaderboard.deleteById(leaderboardid);
	}

	@Test
	public void addLeaderboardWithoutToken() throws Exception {

		URI uri = new URI(baseUrl + port + "/api/leaderboards");

		JSONObject jsonContent = new JSONObject();
		jsonContent.put("utilityType", "0");
		jsonContent.put("timeWindow", "0");
		jsonContent.put("storedDateTime", "2022-10-11T09:00:00");
		RequestBuilder request = MockMvcRequestBuilders
				.post(uri)
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.content(jsonContent.toJSONString())
				.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();

		assertEquals(401, response.getStatus());
	}

	@Test
	public void updateLeaderboardWithToken() throws Exception {
		Leaderboard leaderboardtest = new Leaderboard();
		leaderboardtest.setUtilityType(0);
		leaderboardtest.setTimeWindow(0);
		leaderboardtest.setStoredDateTime(LocalDateTime.now());
		leaderboard.save(leaderboardtest);

		leaderboardtest.setUtilityType(1);

		JSONObject jsonContent = new JSONObject();
		jsonContent.put("utilityType", "1");
		jsonContent.put("timeWindow", "0");
		jsonContent.put("storedDateTime", LocalDateTime.now().toString());

		URI uri = new URI(baseUrl + port + "/api/leaderboards/" + leaderboardtest.getId());

		RequestBuilder request = MockMvcRequestBuilders
				.put(uri)
				.with(SecurityMockMvcRequestPostProcessors.jwt().authorities(new SimpleGrantedAuthority("update:leaderboard")))
				.content(jsonContent.toString())
				.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();

		assertEquals(200, response.getStatus());

		RequestBuilder getRequest = MockMvcRequestBuilders.get(uri).with(SecurityMockMvcRequestPostProcessors.jwt().authorities(new SimpleGrantedAuthority("read:leaderboard")));
		MockHttpServletResponse updatedResponse = mockMvc.perform(getRequest).andReturn().getResponse();
		String responseAsString = updatedResponse.getContentAsString();
		LeaderboardDTO leaderboardresponse = objectMapper.readValue(responseAsString, LeaderboardDTO.class);

		assertNotNull(leaderboardresponse);
		assertEquals(leaderboardtest.getId(), leaderboardresponse.getId());
		assertEquals(leaderboardtest.getUtilityType(), leaderboardresponse.getUtilityType());

		leaderboard.delete(leaderboardtest);
	}

	@Test
	public void updateLeaderboardWithoutToken() throws Exception {
		Leaderboard leaderboardtest = new Leaderboard();
		leaderboardtest.setUtilityType(0);
		leaderboard.save(leaderboardtest);
		leaderboardtest.setUtilityType(1);

		JSONObject jsonContent = new JSONObject();
		jsonContent.put("utilityType", "1");

		URI uri = new URI(baseUrl + port + "/api/leaderboards/" + leaderboardtest.getId());

		RequestBuilder request = MockMvcRequestBuilders
				.put(uri)
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.content(jsonContent.toString())
				.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();

		assertEquals(401, response.getStatus());

		leaderboard.delete(leaderboardtest);
	}

	@Test
	public void deleteLeaderboardWithToken() throws Exception {
		Leaderboard leaderboardtest = new Leaderboard();
		leaderboard.save(leaderboardtest);

		URI uri = new URI(baseUrl + port + "/api/leaderboards/" + leaderboardtest.getId());

		RequestBuilder request = MockMvcRequestBuilders
				.delete(uri)
				.with(SecurityMockMvcRequestPostProcessors.jwt().authorities(new SimpleGrantedAuthority("delete:leaderboard")));
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();

		assertEquals(204, response.getStatus());
	}

	@Test
	public void deleteLeaderboardWithoutToken() throws Exception {
		Leaderboard leaderboardtest = new Leaderboard();
		leaderboard.save(leaderboardtest);

		URI uri = new URI(baseUrl + port + "/api/leaderboards/" + leaderboardtest.getId());

		RequestBuilder request = MockMvcRequestBuilders
				.delete(uri)
				.with(SecurityMockMvcRequestPostProcessors.csrf());
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();

		assertEquals(401, response.getStatus());

		leaderboard.deleteById(leaderboardtest.getId());
	}
}
