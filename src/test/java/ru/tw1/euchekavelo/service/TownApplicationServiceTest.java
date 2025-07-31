package ru.tw1.euchekavelo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.tw1.euchekavelo.config.ConfigTownApplicationService;
import ru.tw1.euchekavelo.dto.request.TownRequestDto;
import ru.tw1.euchekavelo.dto.response.TownResponseDto;
import ru.tw1.euchekavelo.exception.TownNotFoundException;
import ru.tw1.euchekavelo.model.Town;
import ru.tw1.euchekavelo.service.application.TownApplicationService;
import ru.tw1.euchekavelo.service.domain.TownDomainService;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ConfigTownApplicationService.class)
class TownApplicationServiceTest {

    @Autowired
    private TownDomainService townDomainService;

    @Autowired
    private TownApplicationService townApplicationService;

    @Test
    void createTownTestSuccess() {
        Town savedTown = getTown();
        savedTown.setId(UUID.fromString("12afa0c0-2fe3-47d9-916b-761e59b67bba"));

        TownRequestDto townRequestDto = getTownRequestDto();

        Mockito.when(townDomainService.createTown(Mockito.any())).thenReturn(savedTown);
        TownResponseDto townResponseDto = townApplicationService.createTown(townRequestDto);

        assertThat(townResponseDto.getName()).isNotBlank();
    }

    @Test
    void getTownByIdTestSuccess() {
        UUID townId = UUID.fromString("18afa0c0-2fe3-47d9-916b-761e59b67bba");
        Town town = getTown();
        town.setId(townId);
        Mockito.when(townDomainService.findTownById(townId)).thenReturn(town);

        assertThat(townApplicationService.getTownById(townId).getId()).isEqualTo(town.getId());
        assertThat(townApplicationService.getTownById(townId).getName()).isEqualTo(town.getName());
    }

    @Test
    void getTownByIdTestThrowTownNotFoundException() {
        UUID townId = UUID.fromString("13afa0c0-2fe3-47d9-916b-761e59b69bba");
        Mockito.when(townDomainService.findTownById(townId)).thenThrow(TownNotFoundException.class);

        assertThrows(TownNotFoundException.class, () -> townApplicationService.getTownById(townId));
    }

    @Test
    void deleteTownByIdTestSuccess() throws TownNotFoundException {
        UUID townId = UUID.fromString("19afa0c0-2fe3-47d9-916b-761e59b67bba");
        Mockito.when(townDomainService.findTownById(townId)).thenReturn(Mockito.any());

        assertDoesNotThrow(() -> townApplicationService.deleteTownById(townId));
    }

    @Test
    void deleteTownByIdTestThrowTownNotFoundException() {
        UUID townId = UUID.fromString("15afa0c0-2fe3-47d9-916b-761e59b60bba");
        Mockito.doThrow(TownNotFoundException.class).when(townDomainService).deleteTownById(townId);

        assertThrows(TownNotFoundException.class, () -> townApplicationService.deleteTownById(townId));
    }

    @Test
    void updateTownByIdTestSuccess() throws TownNotFoundException {
        UUID townId = UUID.fromString("22afa0c0-2fe3-47d9-916b-761e59b67bba");
        Town town = getTown();
        town.setId(townId);

        TownRequestDto townRequestDto = getTownRequestDto();
        Mockito.when(townDomainService.findTownById(townId)).thenReturn(town);
        Mockito.when(townDomainService.updateTown(Mockito.any())).thenReturn(town);

        assertThat(townApplicationService.updateTownById(townId, townRequestDto).getName()).isNotNull();
    }

    @Test
    void updateTownByIdTestThrowTownNotFoundException() {
        TownRequestDto townRequestDto = getTownRequestDto();
        UUID townId = UUID.fromString("88afa0c0-2fe3-47d9-916b-761e59b67bba");
        Mockito.when(townDomainService.findTownById(townId)).thenThrow(TownNotFoundException.class);

        assertThrows(TownNotFoundException.class, () -> townApplicationService.updateTownById(townId, townRequestDto));
    }

    private TownRequestDto getTownRequestDto() {
        TownRequestDto townRequestDto = new TownRequestDto();
        townRequestDto.setName("Omsk");

        return townRequestDto;
    }

    private Town getTown() {
        Town town = new Town();
        town.setName("Moscow");

        return town;
    }
}
