package ru.skillbox.userservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.skillbox.userservice.dto.ResponseDto;
import ru.skillbox.userservice.dto.TownDto;
import ru.skillbox.userservice.exception.TownNotFoundException;
import ru.skillbox.userservice.model.Town;
import ru.skillbox.userservice.repository.TownRepository;
import ru.skillbox.userservice.service.TownService;

import java.util.Optional;
import java.util.UUID;

import static ru.skillbox.userservice.exception.enums.ExceptionMessage.TOWN_NOT_FOUND_EXCEPTION_MESSAGE;

@Service
public class TownServiceImpl implements TownService {

    private final TownRepository townRepository;

    @Autowired
    public TownServiceImpl(TownRepository townRepository) {
        this.townRepository = townRepository;
    }

    @Override
    public ResponseDto createTown(TownDto townDto) {
        Town town = new Town();
        town.setName(townDto.getName());
        Town savedTown = townRepository.save(town);

        return getResponseDto("The town has been successfully created.", savedTown.getId());
    }

    @Override
    public Town getTownById(UUID id) throws TownNotFoundException {
        Optional<Town> optionalTown = townRepository.findById(id);
        if (optionalTown.isEmpty()) {
            throw new TownNotFoundException(TOWN_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
        }

        return optionalTown.get();
    }

    @Override
    public ResponseDto deleteTownById(UUID id) throws TownNotFoundException {
        Optional<Town> optionalTown = townRepository.findById(id);
        if (optionalTown.isEmpty()) {
            throw new TownNotFoundException(TOWN_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
        }

        townRepository.delete(optionalTown.get());

        return getResponseDto("The town was successfully removed.", null);
    }

    @Override
    public ResponseDto updateTownById(UUID id, TownDto townDto) throws TownNotFoundException {
        Optional<Town> optionalTown = townRepository.findById(id);
        if (optionalTown.isEmpty()) {
            throw new TownNotFoundException(TOWN_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
        }

        Town town = optionalTown.get();
        town.setName(townDto.getName());
        townRepository.save(town);

        return getResponseDto("The town with the specified ID was successfully updated.", null);
    }

    private ResponseDto getResponseDto(String message, UUID id) {
        return ResponseDto.builder()
                .message(message)
                .id(id)
                .result(true)
                .build();
    }
}
