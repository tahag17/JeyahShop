package com.jeyah.jeyahshopapi.security;

import com.jeyah.jeyahshopapi.role.Role;
import com.jeyah.jeyahshopapi.role.RoleRepository;
import com.jeyah.jeyahshopapi.user.User;
import com.jeyah.jeyahshopapi.user.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public CustomOAuth2UserService(final UserRepository userRepository, final RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    public void init() {
        System.out.println("CustomOAuth2UserService bean initialized!");
    }


//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//        System.out.println("CustomOAuth2UserService.loadUser called for: " + userRequest.getClientRegistration().getClientName());
//
//        OAuth2User oAuth2User = super.loadUser(userRequest);
//        System.out.println("Loaded OAuth2User: " + oAuth2User.getAttributes());
//
//        String email = oAuth2User.getAttribute("email");
//
//        // Check if user already exists
//        User user = userRepository.findByEmail(email).orElseGet(() -> {
//            User newUser = new User();
//            newUser.setEmail(email);
//            newUser.setFirstName(oAuth2User.getAttribute("given_name"));
//            newUser.setLastName(oAuth2User.getAttribute("family_name"));
//
//            // Fetch ROLE_USER from DB
//            Role userRole = roleRepository.findByName("ROLE_USER")
//                    .orElseThrow(() -> new RuntimeException("ROLE_USER not found in DB"));
//
//            newUser.setRoles(Collections.singletonList(userRole));
//            System.out.println("Saving new user: " + newUser.getEmail());
//
//            return userRepository.save(newUser);
//        });
//
//        // Optionally, you can return a custom OAuth2User with your User info
//        return oAuth2User;
//    }

        @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
            System.out.println("CustomOAuth2UserService.loadUser called for: " + userRequest.getClientRegistration().getClientName());

            OAuth2User oAuth2User = super.loadUser(userRequest);

            String email = oAuth2User.getAttribute("email");

            User user = userRepository.findByEmail(email).orElseGet(() -> {
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setFirstName(oAuth2User.getAttribute("given_name"));
                newUser.setLastName(oAuth2User.getAttribute("family_name"));

                // Fetch ROLE_USER from DB
                Role userRole = roleRepository.findByName("ROLE_USER")
                        .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));

                newUser.setRoles(Collections.singletonList(userRole));

                return userRepository.save(newUser);
            });

            // Map DB roles to Spring authorities
            List<GrantedAuthority> authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                    .collect(Collectors.toList());

// Return a custom OAuth2User with authorities
            return new DefaultOAuth2User(authorities, oAuth2User.getAttributes(), "email");
        }


}
