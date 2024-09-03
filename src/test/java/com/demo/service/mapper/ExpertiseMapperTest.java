package com.demo.service.mapper;

import static com.demo.domain.ExpertiseAsserts.*;
import static com.demo.domain.ExpertiseTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExpertiseMapperTest {

    private ExpertiseMapper expertiseMapper;

    @BeforeEach
    void setUp() {
        expertiseMapper = new ExpertiseMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getExpertiseSample1();
        var actual = expertiseMapper.toEntity(expertiseMapper.toDto(expected));
        assertExpertiseAllPropertiesEquals(expected, actual);
    }
}
