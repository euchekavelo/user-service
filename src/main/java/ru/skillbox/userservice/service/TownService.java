package ru.skillbox.userservice.service;

import ru.skillbox.userservice.dto.ResponseDto;
import ru.skillbox.userservice.dto.TownDto;

public interface TownService {

    ResponseDto createTown(TownDto townDto);
}
