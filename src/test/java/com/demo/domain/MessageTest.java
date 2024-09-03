package com.demo.domain;

import static com.demo.domain.MessageTestSamples.*;
import static com.demo.domain.SubjectTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.demo.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MessageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Message.class);
        Message message1 = getMessageSample1();
        Message message2 = new Message();
        assertThat(message1).isNotEqualTo(message2);

        message2.setId(message1.getId());
        assertThat(message1).isEqualTo(message2);

        message2 = getMessageSample2();
        assertThat(message1).isNotEqualTo(message2);
    }

    @Test
    void subjectTest() {
        Message message = getMessageRandomSampleGenerator();
        Subject subjectBack = getSubjectRandomSampleGenerator();

        message.setSubject(subjectBack);
        assertThat(message.getSubject()).isEqualTo(subjectBack);

        message.subject(null);
        assertThat(message.getSubject()).isNull();
    }
}
