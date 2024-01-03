package ru.skillbox.userservice.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.userservice.dto.ResponseDto;
import ru.skillbox.userservice.dto.TownDto;
import ru.skillbox.userservice.exception.TownNotFoundException;
import ru.skillbox.userservice.model.Town;
import ru.skillbox.userservice.service.TownService;

import java.util.UUID;

@RestController
@RequestMapping("/towns")
public class TownController {

    private final TownService townService;

    @Autowired
    public TownController(TownService townService) {
        this.townService = townService;
    }

    @PostMapping
    public ResponseEntity<ResponseDto> createTown(@Valid @RequestBody TownDto townDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(townService.createTown(townDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Town> getTownById(@PathVariable UUID id) throws TownNotFoundException {
        return ResponseEntity.ok(townService.getTownById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto> deleteTownById(@PathVariable UUID id) throws TownNotFoundException {
        return ResponseEntity.ok(townService.deleteTownById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto> updateTownById(@PathVariable UUID id, @Valid @RequestBody TownDto townDto)
            throws TownNotFoundException {

        return ResponseEntity.ok(townService.updateTownById(id, townDto));
    }
}
