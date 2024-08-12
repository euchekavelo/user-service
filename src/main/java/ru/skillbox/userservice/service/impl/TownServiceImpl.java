package ru.skillbox.userservice.service.impl;

import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.skillbox.userservice.dto.response.ResponseDto;
import ru.skillbox.userservice.dto.TownDto;
import ru.skillbox.userservice.dto.response.TownResponseDto;
import ru.skillbox.userservice.exception.TownNotFoundException;
import ru.skillbox.userservice.mapper.TownMapper;
import ru.skillbox.userservice.model.Town;
import ru.skillbox.userservice.repository.TownRepository;
import ru.skillbox.userservice.service.TownService;

import java.util.Optional;
import java.util.UUID;

import static ru.skillbox.userservice.exception.enums.ExceptionMessage.TOWN_NOT_FOUND_EXCEPTION_MESSAGE;

@Observed
@Service
public class TownServiceImpl implements TownService {

    private final TownRepository townRepository;
    private final TownMapper townMapper;
    private final Logger logger = LoggerFactory.getLogger(TownServiceImpl.class);

    @Autowired
    public TownServiceImpl(TownRepository townRepository, TownMapper townMapper) {
        this.townRepository = townRepository;
        this.townMapper = townMapper;
    }

    @Override
    public TownResponseDto createTown(TownDto townDto) {
        Town town = new Town();
        town.setName(townDto.getName());
        Town savedTown = townRepository.save(town);

        return townMapper.townToTownResponseDto(savedTown);
    }

    @Override
    public TownResponseDto getTownById(UUID id) throws TownNotFoundException {
        Optional<Town> optionalTown = townRepository.findById(id);
        if (optionalTown.isEmpty()) {
            logger.error(TOWN_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
            throw new TownNotFoundException(TOWN_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
        }

        return townMapper.townToTownResponseDto(optionalTown.get());
    }

    @Override
    public void deleteTownById(UUID id) throws TownNotFoundException {
        Optional<Town> optionalTown = townRepository.findById(id);
        if (optionalTown.isEmpty()) {
            logger.error(TOWN_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
            throw new TownNotFoundException(TOWN_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
        }

        townRepository.delete(optionalTown.get());
    }

    @Override
    public TownResponseDto updateTownById(UUID id, TownDto townDto) throws TownNotFoundException {
        Optional<Town> optionalTown = townRepository.findById(id);
        if (optionalTown.isEmpty()) {
            logger.error(TOWN_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
            throw new TownNotFoundException(TOWN_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
        }

        Town town = optionalTown.get();
        town.setName(townDto.getName());
        Town updatedTown = townRepository.save(town);

        return townMapper.townToTownResponseDto(updatedTown);
    }
}
