package io.bootify.saven;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.json.JSONObject;

import io.bootify.saven.domain.*;
import io.bootify.saven.model.*;
import io.bootify.saven.repos.*;
import lombok.With;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserTest {

	@LocalServerPort
	private int port;

	private final String baseUrl = "http://localhost:";

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private UserRepository users;

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private JwtDecoder jwtDecoder;

	@AfterEach
	void tearDown() {
		if (users.count() > 0){
			users.deleteAll();
		}
	}

	@Test
	public void addUserWithoutToken() throws Exception {
		URI uri = new URI(baseUrl + port + "/api/users");

		JSONObject jsonContent = new JSONObject();
		jsonContent.put("name", "John Doe");
		jsonContent.put("address", "Singapore");
		jsonContent.put("email", "test@gmail.com");
		jsonContent.put("housingType", "HDB-5room");
		jsonContent.put("householdMembers", 4);

		RequestBuilder request = MockMvcRequestBuilders
								.post(uri)
								.content(jsonContent.toJSONString())
								.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();

		assertEquals(403, response.getStatus());
		//ResponseEntity<UserDTO> result = restTemplate.postForEntity(uri, jsonContent, UserDTO.class);

		//assertEquals(401, result.getStatusCode().value());

	}

	@Test
	public void addUserWithToken() throws Exception {

		URI uri = new URI(baseUrl + port + "/api/users");

		JSONObject jsonContent = new JSONObject();
		jsonContent.put("name", "John Doe");
		jsonContent.put("address", "Singapore");
		jsonContent.put("email", "test@gmail.com");
		jsonContent.put("housingType", "HDB-5room");
		jsonContent.put("householdMembers", 4);

		RequestBuilder request = MockMvcRequestBuilders
								.post(uri)
								.with(SecurityMockMvcRequestPostProcessors.jwt())
								.content(jsonContent.toJSONString())
								.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();

		assertEquals(201, response.getStatus());

	}

	@Test
	public void getUserWithoutToken() throws Exception {
		
		URI uri = new URI(baseUrl + port + "/api/users");
	
		User userTest = new User("John Doe", "Singapore", "test@gmail.com", "HDB-5room", 4);
		users.save(userTest);
		
		RequestBuilder request = MockMvcRequestBuilders.get(uri);
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();
		
		assertEquals(401, response.getStatus());

	}

	@Test
	public void getUserWithToken() throws Exception {
		
		URI uri = new URI(baseUrl + port + "/api/users");
	
		User userTest = new User("John Doe", "Singapore", "test@gmail.com", "HDB-5room", 4);
		users.save(userTest);
		
		RequestBuilder request = MockMvcRequestBuilders.get(uri).with(SecurityMockMvcRequestPostProcessors.jwt());
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();
		String responseAsString = response.getContentAsString();
		UserDTO users[] = objectMapper.readValue(responseAsString, UserDTO[].class);
		
		assertEquals(200, response.getStatus());
		assertNotNull(users);
		assertEquals(userTest.getId(), users[users.length - 1].getId());
	}

	@Test
	public void updateUserWithoutToken() throws Exception {
		JSONObject jsonContent = new JSONObject();
		jsonContent.put("name", "John Doe 2");
		jsonContent.put("address", "Singapore");
		jsonContent.put("email", "test2@gmail.com");
		jsonContent.put("housingType", "HDB-5room");
		jsonContent.put("householdMembers", 4);
	
		User userTest = new User("John Doe", "Singapore", "test@gmail.com", "HDB-5room", 4);
		users.save(userTest);
		
		URI uri = new URI(baseUrl + port + "/api/users/" + userTest.getId());

		RequestBuilder request = MockMvcRequestBuilders
								.put(uri)
								.content(jsonContent.toJSONString())
								.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();

		assertEquals(403, response.getStatus());
		
	}

	@Test
	public void updateUserWithToken() throws Exception {
		JSONObject jsonContent = new JSONObject();
		jsonContent.put("name", "John Doe 2");
		jsonContent.put("address", "Singapore");
		jsonContent.put("email", "test2@gmail.com");
		jsonContent.put("housingType", "HDB-5room");
		jsonContent.put("householdMembers", 4);
	
		User userTest = new User("John Doe", "Singapore", "test@gmail.com", "HDB-5room", 4);
		users.save(userTest);
		
		URI uri = new URI(baseUrl + port + "/api/users/" + userTest.getId());

		RequestBuilder putRequest = MockMvcRequestBuilders
								.put(uri)
								.with(SecurityMockMvcRequestPostProcessors.jwt())
								.content(jsonContent.toJSONString())
								.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(putRequest).andReturn().getResponse();

		RequestBuilder getRequest = MockMvcRequestBuilders.get(uri).with(SecurityMockMvcRequestPostProcessors.jwt());
		MockHttpServletResponse updatedResponse = mockMvc.perform(getRequest).andReturn().getResponse();
		String responseAsString = updatedResponse.getContentAsString();
		UserDTO user = objectMapper.readValue(responseAsString, UserDTO.class);

		assertEquals(200, response.getStatus());
		assertEquals("John Doe 2", user.getName());
		assertEquals("test2@gmail.com", user.getEmail());
	}

	@Test
	public void deleteUserWithoutToken() throws Exception {

		User userTest = new User("John Doe", "Singapore", "test@gmail.com", "HDB-5room", 4);
		users.save(userTest);
		
		URI uri = new URI(baseUrl + port + "/api/users/" + userTest.getId());

		RequestBuilder request = MockMvcRequestBuilders
								.delete(uri);
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();

		assertEquals(403, response.getStatus());
		
	}

	@Test
	public void deleteUserWithToken() throws Exception {
		
		User userTest = new User("John Doe", "Singapore", "test@gmail.com", "HDB-5room", 4);
		users.save(userTest);
		
		URI uri = new URI(baseUrl + port + "/api/users/" + userTest.getId());

		RequestBuilder request = MockMvcRequestBuilders
								.delete(uri)
								.with(SecurityMockMvcRequestPostProcessors.jwt());
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();

		assertEquals(204, response.getStatus());
		
	}

}