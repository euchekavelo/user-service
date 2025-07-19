package ru.tw1.euchekavelo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.tw1.euchekavelo.config.ConfigTownService;
import ru.tw1.euchekavelo.dto.request.TownRequestDto;
import ru.tw1.euchekavelo.dto.response.TownResponseDto;
import ru.tw1.euchekavelo.exception.TownNotFoundException;
import ru.tw1.euchekavelo.model.Town;
import ru.tw1.euchekavelo.repository.TownRepository;
import ru.tw1.euchekavelo.service.application.TownApplicationService;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ConfigTownService.class)
class TownApplicationServiceTest {

    @Autowired
    private TownApplicationService townApplicationService;

    @Autowired
    private TownRepository townRepository;

    @Test
    void createTownTestSuccess() {
        Town savedTown = new Town();
        savedTown.setId(UUID.fromString("11afa0c0-2fe3-47d9-916b-761e59b67bba"));
        savedTown.setName("Moscow");

        Mockito.when(townRepository.save(Mockito.any(Town.class))).thenReturn(savedTown);
        TownRequestDto townRequestDto = new TownRequestDto();
        townRequestDto.setName("Moscow");
        TownResponseDto townResponseDto = townApplicationService.createTown(townRequestDto);

        assertThat(townResponseDto.getName()).isNotBlank();
    }

    @Test
    void getTownByIdTestSuccess() throws TownNotFoundException {
        UUID townId = UUID.fromString("11afa0c0-2fe3-47d9-916b-761e59b67bba");
        Town town = new Town();
        town.setId(townId);
        town.setName("Moscow");
        Mockito.when(townRepository.findById(townId)).thenReturn(Optional.of(town));

        assertThat(townApplicationService.getTownById(townId).getId()).isEqualTo(town.getId());
        assertThat(townApplicationService.getTownById(townId).getName()).isEqualTo(town.getName());
    }

    @Test
    void getTownByIdTestThrowTownNotFoundException() {
        UUID townId = UUID.fromString("11afa0c0-2fe3-47d9-916b-761e59b67bba");
        Mockito.when(townRepository.findById(townId)).thenReturn(Optional.empty());

        assertThrows(TownNotFoundException.class, () -> townApplicationService.getTownById(townId));
    }

    @Test
    void deleteTownByIdTestSuccess() throws TownNotFoundException {
        UUID townId = UUID.fromString("11afa0c0-2fe3-47d9-916b-761e59b67bba");
        Mockito.when(townRepository.findById(townId)).thenReturn(Optional.of(Mockito.mock(Town.class)));

        assertDoesNotThrow(() -> townApplicationService.deleteTownById(townId));
    }

    @Test
    void deleteTownByIdTestThrowTownNotFoundException() {
        UUID townId = UUID.fromString("11afa0c0-2fe3-47d9-916b-761e59b67bba");
        Mockito.when(townRepository.findById(townId)).thenReturn(Optional.empty());

        assertThrows(TownNotFoundException.class, () -> townApplicationService.deleteTownById(townId));
    }

    @Test
    void updateTownByIdTestSuccess() throws TownNotFoundException {
        UUID townId = UUID.fromString("11afa0c0-2fe3-47d9-916b-761e59b67bba");
        Town town = new Town();
        town.setName("Test town");
        town.setId(townId);

        TownRequestDto townRequestDto = new TownRequestDto();
        townRequestDto.setName("Omsk");
        Mockito.when(townRepository.findById(townId)).thenReturn(Optional.of(Mockito.mock(Town.class)));
        Mockito.when(townRepository.save(Mockito.any())).thenReturn(town);

        assertThat(townApplicationService.updateTownById(townId, townRequestDto).getName()).isNotNull();
    }

    @Test
    void updateTownByIdTestThrowTownNotFoundException() {
        TownRequestDto townRequestDto = new TownRequestDto();
        townRequestDto.setName("Omsk");
        UUID townId = UUID.fromString("11afa0c0-2fe3-47d9-916b-761e59b67bba");
        Mockito.when(townRepository.findById(townId)).thenReturn(Optional.empty());

        assertThrows(TownNotFoundException.class, () -> townApplicationService.updateTownById(townId, townRequestDto));
    }
}