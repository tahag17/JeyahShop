package com.jeyah.jeyahshopapi;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("public/keycloak")
public class MainController {

    @GetMapping
    public String hello(){
        return "Hello World";
    }

    @GetMapping("user/hello")
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
