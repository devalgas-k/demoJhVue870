package com.demo.service.mapper;

import static com.demo.domain.ExperienceAsserts.*;
import static com.demo.domain.ExperienceTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExperienceMapperTest {

    private ExperienceMapper experienceMapper;

    @BeforeEach
    void setUp() {
        experienceMapper = new ExperienceMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getExperienceSample1();
        var actual = experienceMapper.toEntity(experienceMapper.toDto(expected));
        assertExperienceAllPropertiesEquals(expected, actual);
    }
}
