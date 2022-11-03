package io.bootify.saven.rest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.nimbusds.jose.shaded.json.JSONObject;

public class AuthManagement {

    private RestTemplate restTemplate;

    public String getMgtToken(String baseUrl, String audience, String clientId, String clientSecret) {

        String uri = (baseUrl + "/oauth/token");

        restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject jsonContent = new JSONObject();
        jsonContent.put("client_id", clientId);
        jsonContent.put("client_secret", clientSecret);
        jsonContent.put("audience", audience);
        jsonContent.put("grant_type", "client_credentials");
        
        HttpEntity<String> request = new HttpEntity<String>(jsonContent.toString(), headers);
        JSONObject result = restTemplate.postForObject(uri, request, JSONObject.class);

        return result.get("access_token").toString();

    }

    public void addDefaultRole(String baseUrl, String id, String token, String roleId){
        String uri = (baseUrl + "/api/v2/users/"+ id +"/roles");

        restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("authorization", "Bearer "+ token);

        JSONObject jsonContent = new JSONObject();
        String[] role = new String[]{roleId};
        jsonContent.put("roles", role);

        HttpEntity<String> request = new HttpEntity<String>(jsonContent.toString(), headers);
        restTemplate.postForObject(uri, request, JSONObject.class);

    }
}
