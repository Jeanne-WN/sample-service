package com.sampleservice.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    private Logger logger = LoggerFactory.getLogger(HelloWorldController.class);

    @GetMapping("/hello-world")
    public String helloWorld() {
        logger.info("This is a demo for setting up codebase!");
        return "hello world, What a good day!!!";
    }

}
