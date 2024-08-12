package ru.skillbox.userservice.service;

import ru.skillbox.userservice.dto.TownDto;
import ru.skillbox.userservice.dto.response.TownResponseDto;
import ru.skillbox.userservice.exception.TownNotFoundException;

import java.util.UUID;

public interface TownService {

    TownResponseDto createTown(TownDto townDto);

    TownResponseDto getTownById(UUID id) throws TownNotFoundException;

    void deleteTownById(UUID id) throws TownNotFoundException;

    TownResponseDto updateTownById(UUID id, TownDto townDto) throws TownNotFoundException;
}
