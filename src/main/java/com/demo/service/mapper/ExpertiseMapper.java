package com.demo.service.mapper;

import com.demo.domain.Experience;
import com.demo.domain.Expertise;
import com.demo.service.dto.ExperienceDTO;
import com.demo.service.dto.ExpertiseDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Expertise} and its DTO {@link ExpertiseDTO}.
 */
@Mapper(componentModel = "spring")
public interface ExpertiseMapper extends EntityMapper<ExpertiseDTO, Expertise> {
    @Mapping(target = "experiences", source = "experiences", qualifiedByName = "experienceCompanySet")
    ExpertiseDTO toDto(Expertise s);

    @Mapping(target = "experiences", ignore = true)
    @Mapping(target = "removeExperience", ignore = true)
    Expertise toEntity(ExpertiseDTO expertiseDTO);

    @Named("experienceCompany")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "company", source = "company")
    ExperienceDTO toDtoExperienceCompany(Experience experience);

    @Named("experienceCompanySet")
    default Set<ExperienceDTO> toDtoExperienceCompanySet(Set<Experience> experience) {
        return experience.stream().map(this::toDtoExperienceCompany).collect(Collectors.toSet());
    }
}
