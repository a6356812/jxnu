package com.example.demo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class hello {

    @RequestMapping("/hello")
    public String say(){
        return "你是猪q:";
    }
}
