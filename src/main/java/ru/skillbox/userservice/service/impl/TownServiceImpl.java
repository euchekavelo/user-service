package ru.skillbox.userservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.skillbox.userservice.dto.ResponseDto;
import ru.skillbox.userservice.dto.TownDto;
import ru.skillbox.userservice.model.Town;
import ru.skillbox.userservice.repository.TownRepository;
import ru.skillbox.userservice.service.TownService;

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
        townRepository.save(town);

        return getResponseDto("The town has been successfully created.");
    }

    private ResponseDto getResponseDto(String message) {
        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage(message);
        responseDto.setResult(true);

        return responseDto;
    }
}
