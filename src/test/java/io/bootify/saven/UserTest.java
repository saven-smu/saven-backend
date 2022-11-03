package io.bootify.saven;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.json.JSONObject;

import io.bootify.saven.domain.*;
import io.bootify.saven.model.*;
import io.bootify.saven.repos.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserTest {

	@LocalServerPort
	private int port;

	private final String baseUrl = "http://localhost:";

	@Autowired
	private UserRepository users;

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private JwtDecoder jwtDecoder;

	@Test
	public void addUserWithoutToken() throws Exception {
		URI uri = new URI(baseUrl + port + "/api/users");

		JSONObject jsonContent = new JSONObject();
		jsonContent.put("name", "John Doe");
		jsonContent.put("address", "Singapore");
		jsonContent.put("email", "test@gmail.com");
		jsonContent.put("housingType", "HDB5");
		jsonContent.put("householdMembers", 4);

		RequestBuilder request = MockMvcRequestBuilders
								.post(uri)
								.with(SecurityMockMvcRequestPostProcessors.csrf())
								.content(jsonContent.toString())
								.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();

		assertEquals(401, response.getStatus());

	}

	@Test
	public void addUserWithToken() throws Exception {

		URI uri = new URI(baseUrl + port + "/api/users");

		JSONObject jsonContent = new JSONObject();
		jsonContent.put("name", "John Doe");
		jsonContent.put("address", "Singapore");
		jsonContent.put("email", "test@gmail.com");
		jsonContent.put("housingType", "HDB5");
		jsonContent.put("householdMembers", 4);
		jsonContent.put("credits", 4);

		RequestBuilder request = MockMvcRequestBuilders
								.post(uri)
								.with(SecurityMockMvcRequestPostProcessors.jwt().authorities(new SimpleGrantedAuthority("upload:user")))
								.content(jsonContent.toString())
								.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();

		assertEquals(201, response.getStatus());

		UUID userId = objectMapper.readValue(response.getContentAsString(), UUID.class);
		users.deleteById(userId);
		
	}

	@Test
	public void getUserWithoutToken() throws Exception {
		
		URI uri = new URI(baseUrl + port + "/api/users");
	
		User userTest = new User("John Doe", "Singapore", "test@gmail.com", "HDB5", 4);
		users.save(userTest);
		
		RequestBuilder request = MockMvcRequestBuilders.get(uri).with(SecurityMockMvcRequestPostProcessors.csrf());;
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();
		
		assertEquals(401, response.getStatus());

		users.deleteById(userTest.getId());

	}

	@Test
	public void getUserWithToken() throws Exception {
			
		User userTest = new User("John Doe", "Singapore", "test@gmail.com", "HDB5", 4);
		users.save(userTest);
		
		URI uri = new URI(baseUrl + port + "/api/users/" + userTest.getId());
		RequestBuilder request = MockMvcRequestBuilders.get(uri).with(SecurityMockMvcRequestPostProcessors.jwt().authorities(new SimpleGrantedAuthority("read:user")));
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();

		assertEquals(200, response.getStatus());

		users.deleteById(userTest.getId());	
	}

	@Test
	public void updateUserWithoutToken() throws Exception {
		JSONObject jsonContent = new JSONObject();
		jsonContent.put("name", "John Doe 2");
		jsonContent.put("address", "Singapore");
		jsonContent.put("email", "test2@gmail.com");
		jsonContent.put("housingType", "HDB5");
		jsonContent.put("householdMembers", 4);
	
		User userTest = new User("John Doe", "Singapore", "test@gmail.com", "HDB5", 4);
		users.save(userTest);
		
		URI uri = new URI(baseUrl + port + "/api/users/" + userTest.getId());

		RequestBuilder request = MockMvcRequestBuilders
								.put(uri)
								.with(SecurityMockMvcRequestPostProcessors.csrf())
								.content(jsonContent.toString())
								.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();

		assertEquals(401, response.getStatus());
		users.deleteById(userTest.getId());
	}

	@Test
	public void updateUserWithToken() throws Exception {
		JSONObject jsonContent = new JSONObject();
		jsonContent.put("name", "John Doe 2");
		jsonContent.put("address", "Singapore");
		jsonContent.put("email", "test2@gmail.com");
		jsonContent.put("housingType", "HDB5");
		jsonContent.put("householdMembers", 4);
		jsonContent.put("credits", 4);
	
		User userTest = new User("John Doe", "Singapore", "test@gmail.com", "HDB5", 4);
		users.save(userTest);
		
		URI uri = new URI(baseUrl + port + "/api/users/" + userTest.getId());

		RequestBuilder putRequest = MockMvcRequestBuilders
								.put(uri)
								.with(SecurityMockMvcRequestPostProcessors.jwt().authorities(new SimpleGrantedAuthority("update:user")))
								.content(jsonContent.toString())
								.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(putRequest).andReturn().getResponse();

		RequestBuilder getRequest = MockMvcRequestBuilders.get(uri).with(SecurityMockMvcRequestPostProcessors.jwt().authorities(new SimpleGrantedAuthority("read:user")));
		MockHttpServletResponse updatedResponse = mockMvc.perform(getRequest).andReturn().getResponse();
		String responseAsString = updatedResponse.getContentAsString();
		UserDTO user = objectMapper.readValue(responseAsString, UserDTO.class);

		assertEquals(200, response.getStatus());
		assertEquals("John Doe 2", user.getName());
		assertEquals("test2@gmail.com", user.getEmail());

		users.deleteById(userTest.getId());
	}

	@Test
	public void deleteUserWithoutToken() throws Exception {

		User userTest = new User("John Doe", "Singapore", "test@gmail.com", "HDB5", 4);
		users.save(userTest);
		
		URI uri = new URI(baseUrl + port + "/api/users/" + userTest.getId());

		RequestBuilder request = MockMvcRequestBuilders
								.delete(uri)
								.with(SecurityMockMvcRequestPostProcessors.csrf());
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();

		assertEquals(401, response.getStatus());
		
		users.deleteById(userTest.getId());
	}

	@Test
	public void deleteUserWithToken() throws Exception {
		
		User userTest = new User("John Doe", "Singapore", "test@gmail.com", "HDB5", 4);
		users.save(userTest);
		
		URI uri = new URI(baseUrl + port + "/api/users/" + userTest.getId());

		RequestBuilder request = MockMvcRequestBuilders
								.delete(uri)
								.with(SecurityMockMvcRequestPostProcessors.jwt().authorities(new SimpleGrantedAuthority("delete:user")));
		MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();

		assertEquals(204, response.getStatus());

		//users.deleteById(userTest.getId());
		
	}

}
