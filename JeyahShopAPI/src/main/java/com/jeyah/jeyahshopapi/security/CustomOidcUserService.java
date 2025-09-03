package com.jeyah.jeyahshopapi.security;

import com.jeyah.jeyahshopapi.role.Role;
import com.jeyah.jeyahshopapi.role.RoleRepository;
import com.jeyah.jeyahshopapi.user.CustomUserPrincipal;
import com.jeyah.jeyahshopapi.user.User;
import com.jeyah.jeyahshopapi.user.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CustomOidcUserService extends OidcUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    public CustomOidcUserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

//    @Override
//    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
//        System.out.println("CustomOidcUserService.loadUser called for: " +
//                userRequest.getClientRegistration().getClientName());
//
//        OidcUser oidcUser = super.loadUser(userRequest);
//
//        String email = oidcUser.getEmail();
//
//        User user = userRepository.findByEmail(email).orElseGet(() -> {
//            User newUser = new User();
//            newUser.setEmail(email);
//            newUser.setFirstName(oidcUser.getGivenName());
//            newUser.setLastName(oidcUser.getFamilyName());
//
//            Role userRole = roleRepository.findByName("ROLE_USER")
//                    .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));
//
//            newUser.setRoles(Collections.singletonList(userRole));
//            return userRepository.save(newUser);
//        });
//
//        List<GrantedAuthority> authorities = user.getRoles().stream()
//                .map(role -> new SimpleGrantedAuthority(role.getName()))
//                .collect(Collectors.toList());
//
//        return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo(), "email");
//    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        String email = oidcUser.getEmail();

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFirstName(oidcUser.getGivenName());
            newUser.setLastName(oidcUser.getFamilyName());

            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));
            newUser.setRoles(Collections.singletonList(userRole));
            return userRepository.save(newUser);
        });

        CustomUserPrincipal principal = new CustomUserPrincipal(user);

        // Attach CustomUserPrincipal to OidcUser as an attribute
        Map<String, Object> attributes = new HashMap<>(oidcUser.getAttributes());
        attributes.put("principal", principal);

        List<GrantedAuthority> authorities = principal.getAuthorities()
                .stream()
                .map(a -> (GrantedAuthority) a)  // cast each element
                .collect(Collectors.toList());

        return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo(), "email") {
            @Override
            public Map<String, Object> getAttributes() {
                return attributes;
            }
        };
    }



}
