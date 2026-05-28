package com.example.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/")
    public String hello(){
        return "hello user module";
    }

    @GetMapping("hi")
    public String hi(){
        return "hi user module";
    }
}
