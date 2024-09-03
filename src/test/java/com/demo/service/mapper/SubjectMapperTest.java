package com.demo.service.mapper;

import static com.demo.domain.SubjectAsserts.*;
import static com.demo.domain.SubjectTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SubjectMapperTest {

    private SubjectMapper subjectMapper;

    @BeforeEach
    void setUp() {
        subjectMapper = new SubjectMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSubjectSample1();
        var actual = subjectMapper.toEntity(subjectMapper.toDto(expected));
        assertSubjectAllPropertiesEquals(expected, actual);
    }
}
