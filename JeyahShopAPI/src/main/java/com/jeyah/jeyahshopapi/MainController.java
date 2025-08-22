package com.jeyah.jeyahshopapi;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {


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
    @PreAuthorize("hasRole('MANAGER')")
    public String helloManager(){
        return "Hello Manager";
    }

    @GetMapping("/admin/hello")
    @PreAuthorize("hasRole('ADMIN')")
    public String helloAdmin(){
        return "Hello Admin";
    }
}
