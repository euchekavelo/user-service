package ru.tw1.euchekavelo.service.application;

import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tw1.euchekavelo.dto.request.TownRequestDto;
import ru.tw1.euchekavelo.dto.response.TownResponseDto;
import ru.tw1.euchekavelo.mapper.TownMapper;
import ru.tw1.euchekavelo.model.Town;
import ru.tw1.euchekavelo.service.domain.TownDomainService;

import java.util.UUID;

@Observed
@Service
@RequiredArgsConstructor
public class TownApplicationService {

    private final TownDomainService townDomainService;
    private final TownMapper townMapper;

    public TownResponseDto createTown(TownRequestDto townRequestDto) {
        Town town = townMapper.townDtoToTown(townRequestDto);
        Town savedTown = townDomainService.createTown(town);

        return townMapper.townToTownResponseDto(savedTown);
    }

    public TownResponseDto getTownById(UUID id) {
        Town town = townDomainService.findTownById(id);

        return townMapper.townToTownResponseDto(town);
    }

    public void deleteTownById(UUID id) {
        townDomainService.deleteTownById(id);
    }

    public TownResponseDto updateTownById(UUID id, TownRequestDto townRequestDto) {
        Town town = townDomainService.findTownById(id);
        Town updatedTown = townDomainService.updateTown(townMapper.towndDtoToTown(town, townRequestDto));

        return townMapper.townToTownResponseDto(updatedTown);
    }
}
