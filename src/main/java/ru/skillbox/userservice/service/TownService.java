package ru.skillbox.userservice.service;

import ru.skillbox.userservice.dto.ResponseDto;
import ru.skillbox.userservice.dto.TownDto;
import ru.skillbox.userservice.exception.TownNotFoundException;
import ru.skillbox.userservice.model.Town;

import java.util.UUID;

public interface TownService {

    ResponseDto createTown(TownDto townDto);

    Town getTownById(UUID id) throws TownNotFoundException;

    ResponseDto deleteTownById(UUID id) throws TownNotFoundException;

    ResponseDto updateTownById(UUID id, TownDto townDto) throws TownNotFoundException;
}
