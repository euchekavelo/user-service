package ru.skillbox.userservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.skillbox.userservice.dto.response.TownResponseDto;
import ru.skillbox.userservice.model.Town;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TownMapper {

    TownResponseDto townToTownResponseDto(Town town);
}
