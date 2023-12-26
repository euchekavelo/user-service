package ru.skillbox.userservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.userservice.service.TestService;

import java.util.UUID;

@RestController
public class HelloController {

    private final TestService testService;

    public HelloController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping
    public String index() {
        testService.test();
        return "Hello world from users service!";
    }

    @GetMapping("/{id}")
    public String test(@PathVariable UUID id) {
        testService.test1(id);
        return "Hello world from users service!";
    }
}
