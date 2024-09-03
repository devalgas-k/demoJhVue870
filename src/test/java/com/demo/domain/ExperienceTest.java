package com.demo.domain;

import static com.demo.domain.ExperienceTestSamples.*;
import static com.demo.domain.ExpertiseTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.demo.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ExperienceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Experience.class);
        Experience experience1 = getExperienceSample1();
        Experience experience2 = new Experience();
        assertThat(experience1).isNotEqualTo(experience2);

        experience2.setId(experience1.getId());
        assertThat(experience1).isEqualTo(experience2);

        experience2 = getExperienceSample2();
        assertThat(experience1).isNotEqualTo(experience2);
    }

    @Test
    void expertiseTest() {
        Experience experience = getExperienceRandomSampleGenerator();
        Expertise expertiseBack = getExpertiseRandomSampleGenerator();

        experience.addExpertise(expertiseBack);
        assertThat(experience.getExpertise()).containsOnly(expertiseBack);

        experience.removeExpertise(expertiseBack);
        assertThat(experience.getExpertise()).doesNotContain(expertiseBack);

        experience.expertise(new HashSet<>(Set.of(expertiseBack)));
        assertThat(experience.getExpertise()).containsOnly(expertiseBack);

        experience.setExpertise(new HashSet<>());
        assertThat(experience.getExpertise()).doesNotContain(expertiseBack);
    }
}
