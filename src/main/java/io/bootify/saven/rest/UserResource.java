package io.bootify.saven.rest;

import io.bootify.saven.model.UserDTO;
import io.bootify.saven.service.UserService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserResource {

    @Value("${auth0.audience_mgt}")
     private String audience;
     
     @Value("${auth0.client_id}")
     private String clientId;
     
     @Value("${auth0.client_secret}")
     private String clientSecret;

     @Value("${auth0.management_domain}")
     private String url;

     @Value("${auth0.role_id}")
     private String role;

    private final UserService userService;

    public UserResource(final UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping
    @PreAuthorize("hasAuthority('read:user')")    
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable final UUID id) {
        System.out.println("HEllo"+SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString());
        return ResponseEntity.ok(userService.get(id));
    }

    @GetMapping("verifyUser/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable final String email) {
        AuthManagement mgtToken = new AuthManagement();
        String token = mgtToken.getMgtToken(url, audience, clientId, clientSecret);
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        mgtToken.addDefaultRole(url, id, token, role);
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<UUID> createUser(@RequestBody @Valid final UserDTO userDTO) {
        return new ResponseEntity<>(userService.create(userDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('update:user')")
    public ResponseEntity<Void> updateUser(@PathVariable final UUID id,
            @RequestBody @Valid final UserDTO userDTO) {
        userService.update(id, userDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteUser(@PathVariable final UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
