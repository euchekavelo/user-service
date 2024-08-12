package ru.skillbox.userservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.skillbox.userservice.config.ConfigTownService;
import ru.skillbox.userservice.dto.TownDto;
import ru.skillbox.userservice.dto.response.TownResponseDto;
import ru.skillbox.userservice.exception.TownNotFoundException;
import ru.skillbox.userservice.model.Town;
import ru.skillbox.userservice.repository.TownRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ConfigTownService.class)
class TownServiceTest {

    @Autowired
    private TownService townService;

    @Autowired
    private TownRepository townRepository;

    @Test
    void createTownTestSuccess() {
        Town savedTown = new Town();
        savedTown.setId(UUID.fromString("11afa0c0-2fe3-47d9-916b-761e59b67bba"));
        savedTown.setName("Moscow");

        Mockito.when(townRepository.save(Mockito.any(Town.class))).thenReturn(savedTown);
        TownDto townDto = new TownDto();
        townDto.setName("Moscow");
        TownResponseDto townResponseDto = townService.createTown(townDto);

        assertThat(townResponseDto.getName()).isNotBlank();
    }

    @Test
    void getTownByIdTestSuccess() throws TownNotFoundException {
        UUID townId = UUID.fromString("11afa0c0-2fe3-47d9-916b-761e59b67bba");
        Town town = new Town();
        town.setId(townId);
        town.setName("Moscow");
        Mockito.when(townRepository.findById(townId)).thenReturn(Optional.of(town));

        assertThat(townService.getTownById(townId).getId()).isEqualTo(town.getId());
        assertThat(townService.getTownById(townId).getName()).isEqualTo(town.getName());
    }

    @Test
    void getTownByIdTestThrowTownNotFoundException() {
        UUID townId = UUID.fromString("11afa0c0-2fe3-47d9-916b-761e59b67bba");
        Mockito.when(townRepository.findById(townId)).thenReturn(Optional.empty());

        assertThrows(TownNotFoundException.class, () -> townService.getTownById(townId));
    }

    @Test
    void deleteTownByIdTestSuccess() throws TownNotFoundException {
        UUID townId = UUID.fromString("11afa0c0-2fe3-47d9-916b-761e59b67bba");
        Mockito.when(townRepository.findById(townId)).thenReturn(Optional.of(Mockito.mock(Town.class)));

        assertDoesNotThrow(() -> townService.deleteTownById(townId));
    }

    @Test
    void deleteTownByIdTestThrowTownNotFoundException() {
        UUID townId = UUID.fromString("11afa0c0-2fe3-47d9-916b-761e59b67bba");
        Mockito.when(townRepository.findById(townId)).thenReturn(Optional.empty());

        assertThrows(TownNotFoundException.class, () -> townService.deleteTownById(townId));
    }

    @Test
    void updateTownByIdTestSuccess() throws TownNotFoundException {
        UUID townId = UUID.fromString("11afa0c0-2fe3-47d9-916b-761e59b67bba");
        Town town = new Town();
        town.setName("Test town");
        town.setId(townId);

        TownDto townDto = new TownDto();
        townDto.setName("Omsk");
        Mockito.when(townRepository.findById(townId)).thenReturn(Optional.of(Mockito.mock(Town.class)));
        Mockito.when(townRepository.save(Mockito.any())).thenReturn(town);

        assertThat(townService.updateTownById(townId, townDto).getName()).isNotNull();
    }

    @Test
    void updateTownByIdTestThrowTownNotFoundException() {
        TownDto townDto = new TownDto();
        townDto.setName("Omsk");
        UUID townId = UUID.fromString("11afa0c0-2fe3-47d9-916b-761e59b67bba");
        Mockito.when(townRepository.findById(townId)).thenReturn(Optional.empty());

        assertThrows(TownNotFoundException.class, () -> townService.updateTownById(townId, townDto));
    }
}