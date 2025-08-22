package com.jeyah.jeyahshopapi;


import com.jeyah.jeyahshopapi.security.CustomOAuth2UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
public class MainController {

    private final CustomOAuth2UserService customOAuth2UserService;
    public MainController(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }


    @GetMapping("/public")
    public String Greeting(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.isAuthenticated()){
            System.out.println(authentication.getName());
        }
        if(authentication.getPrincipal() instanceof OAuth2User){
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            return "hello " + oAuth2User.getName();
        }
        return "not authenticatd bruv";
    }

    @GetMapping("/public/hello")
    public String hello(){
        return "Public Hello World";
    }


    @GetMapping("user/hello")
    @PreAuthorize("hasRole('USER')")
    public String helloUser(){
        return "Hello User";
    }

    @GetMapping("/manager/hello")
//    @PreAuthorize("hasRole('JeyahShop_manager')")
    public String helloManager(){
        return "Hello Manager";
    }

    @GetMapping("/admin")
//    @PreAuthorize("hasRole('JeyahShop_admin')")
    public String helloAdmin(){
        return "Hello Admin";
    }
}
