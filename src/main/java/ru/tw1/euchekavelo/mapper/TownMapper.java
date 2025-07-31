package ru.tw1.euchekavelo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import ru.tw1.euchekavelo.dto.request.TownRequestDto;
import ru.tw1.euchekavelo.dto.response.TownResponseDto;
import ru.tw1.euchekavelo.model.Town;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TownMapper {

    TownResponseDto townToTownResponseDto(Town town);

    Town townDtoToTown(TownRequestDto townRequestDto);

    Town towndDtoToTown(@MappingTarget Town town, TownRequestDto townRequestDto);
}
