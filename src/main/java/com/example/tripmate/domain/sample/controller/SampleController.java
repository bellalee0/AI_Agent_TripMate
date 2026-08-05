package com.example.tripmate.domain.sample.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

    @GetMapping("/samples")
    public String hello() {
        return "hello";
    }
}
