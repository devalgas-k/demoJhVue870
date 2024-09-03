package com.demo.service.mapper;

import static com.demo.domain.JobAsserts.*;
import static com.demo.domain.JobTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JobMapperTest {

    private JobMapper jobMapper;

    @BeforeEach
    void setUp() {
        jobMapper = new JobMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getJobSample1();
        var actual = jobMapper.toEntity(jobMapper.toDto(expected));
        assertJobAllPropertiesEquals(expected, actual);
    }
}
