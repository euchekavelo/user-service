package ru.skillbox.userservice.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.userservice.dto.ShortUserDto;
import ru.skillbox.userservice.dto.UserDto;
import ru.skillbox.userservice.dto.ResponseDto;
import ru.skillbox.userservice.dto.UserSubscriptionDto;
import ru.skillbox.userservice.exception.TownNotFoundException;
import ru.skillbox.userservice.exception.UserNotFoundException;
import ru.skillbox.userservice.exception.UserSubscriptionException;
import ru.skillbox.userservice.model.User;
import ru.skillbox.userservice.service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ResponseDto> createUser(@Valid @RequestBody ShortUserDto shortUserDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(shortUserDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable UUID id) throws UserNotFoundException {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto> updateUserById(@PathVariable UUID id, @RequestBody UserDto userDto)
            throws UserNotFoundException, TownNotFoundException {

        return ResponseEntity.ok(userService.updateUserById(id, userDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto> deleteUserById(@PathVariable UUID id) throws UserNotFoundException {
        return ResponseEntity.ok(userService.deleteUserById(id));
    }

    @PostMapping("/subscription")
    public ResponseEntity<ResponseDto> subscribeToUser(@RequestBody UserSubscriptionDto userSubscriptionDto)
            throws UserSubscriptionException, UserNotFoundException {

        return ResponseEntity.ok(userService.subscribeToUser(userSubscriptionDto));
    }

    @PostMapping("/unsubscription")
    public ResponseEntity<ResponseDto> unsubscribeFromUser(@RequestBody UserSubscriptionDto userSubscriptionDto)
            throws UserSubscriptionException {

        return ResponseEntity.ok(userService.unsubscribeFromUser(userSubscriptionDto));
    }
}
