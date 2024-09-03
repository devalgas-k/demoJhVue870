package com.demo.domain;

import static com.demo.domain.ExperienceTestSamples.*;
import static com.demo.domain.ExpertiseTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.demo.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ExpertiseTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Expertise.class);
        Expertise expertise1 = getExpertiseSample1();
        Expertise expertise2 = new Expertise();
        assertThat(expertise1).isNotEqualTo(expertise2);

        expertise2.setId(expertise1.getId());
        assertThat(expertise1).isEqualTo(expertise2);

        expertise2 = getExpertiseSample2();
        assertThat(expertise1).isNotEqualTo(expertise2);
    }

    @Test
    void experienceTest() {
        Expertise expertise = getExpertiseRandomSampleGenerator();
        Experience experienceBack = getExperienceRandomSampleGenerator();

        expertise.addExperience(experienceBack);
        assertThat(expertise.getExperiences()).containsOnly(experienceBack);
        assertThat(experienceBack.getExpertise()).containsOnly(expertise);

        expertise.removeExperience(experienceBack);
        assertThat(expertise.getExperiences()).doesNotContain(experienceBack);
        assertThat(experienceBack.getExpertise()).doesNotContain(expertise);

        expertise.experiences(new HashSet<>(Set.of(experienceBack)));
        assertThat(expertise.getExperiences()).containsOnly(experienceBack);
        assertThat(experienceBack.getExpertise()).containsOnly(expertise);

        expertise.setExperiences(new HashSet<>());
        assertThat(expertise.getExperiences()).doesNotContain(experienceBack);
        assertThat(experienceBack.getExpertise()).doesNotContain(expertise);
    }
}
