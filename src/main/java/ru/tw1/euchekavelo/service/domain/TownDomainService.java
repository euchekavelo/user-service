package ru.tw1.euchekavelo.service.domain;

import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.tw1.euchekavelo.exception.TownNotFoundException;
import ru.tw1.euchekavelo.model.Town;
import ru.tw1.euchekavelo.repository.TownRepository;

import java.util.UUID;

import static ru.tw1.euchekavelo.exception.enums.ExceptionMessage.TOWN_NOT_FOUND_EXCEPTION_MESSAGE;

@Observed
@Service
@RequiredArgsConstructor
public class TownDomainService {

    private final TownRepository townRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(TownDomainService.class);

    public Town createTown(Town town) {
        return townRepository.save(town);
    }

    public Town findTownById(UUID id) {
        return townRepository.findById(id).orElseThrow(() -> {
            LOGGER.error(TOWN_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
            return new TownNotFoundException(TOWN_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
        });
    }

    public void deleteTownById(UUID id) {
        Town town = findTownById(id);
        townRepository.delete(town);
    }

    public Town updateTown(Town town) {
        return createTown(town);
    }
}
