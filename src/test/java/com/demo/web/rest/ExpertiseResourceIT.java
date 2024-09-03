package com.demo.web.rest;

import static com.demo.domain.ExpertiseAsserts.*;
import static com.demo.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.demo.IntegrationTest;
import com.demo.domain.Expertise;
import com.demo.repository.ExpertiseRepository;
import com.demo.service.dto.ExpertiseDTO;
import com.demo.service.mapper.ExpertiseMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ExpertiseResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ExpertiseResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_LEVEL = 20;
    private static final Integer UPDATED_LEVEL = 21;

    private static final String ENTITY_API_URL = "/api/expertise";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ExpertiseRepository expertiseRepository;

    @Autowired
    private ExpertiseMapper expertiseMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restExpertiseMockMvc;

    private Expertise expertise;

    private Expertise insertedExpertise;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Expertise createEntity() {
        return new Expertise().title(DEFAULT_TITLE).description(DEFAULT_DESCRIPTION).level(DEFAULT_LEVEL);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Expertise createUpdatedEntity() {
        return new Expertise().title(UPDATED_TITLE).description(UPDATED_DESCRIPTION).level(UPDATED_LEVEL);
    }

    @BeforeEach
    public void initTest() {
        expertise = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedExpertise != null) {
            expertiseRepository.delete(insertedExpertise);
            insertedExpertise = null;
        }
    }

    @Test
    @Transactional
    void createExpertise() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Expertise
        ExpertiseDTO expertiseDTO = expertiseMapper.toDto(expertise);
        var returnedExpertiseDTO = om.readValue(
            restExpertiseMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(expertiseDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ExpertiseDTO.class
        );

        // Validate the Expertise in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedExpertise = expertiseMapper.toEntity(returnedExpertiseDTO);
        assertExpertiseUpdatableFieldsEquals(returnedExpertise, getPersistedExpertise(returnedExpertise));

        insertedExpertise = returnedExpertise;
    }

    @Test
    @Transactional
    void createExpertiseWithExistingId() throws Exception {
        // Create the Expertise with an existing ID
        expertise.setId(1L);
        ExpertiseDTO expertiseDTO = expertiseMapper.toDto(expertise);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restExpertiseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(expertiseDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Expertise in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        expertise.setTitle(null);

        // Create the Expertise, which fails.
        ExpertiseDTO expertiseDTO = expertiseMapper.toDto(expertise);

        restExpertiseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(expertiseDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllExpertise() throws Exception {
        // Initialize the database
        insertedExpertise = expertiseRepository.saveAndFlush(expertise);

        // Get all the expertiseList
        restExpertiseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(expertise.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].level").value(hasItem(DEFAULT_LEVEL)));
    }

    @Test
    @Transactional
    void getExpertise() throws Exception {
        // Initialize the database
        insertedExpertise = expertiseRepository.saveAndFlush(expertise);

        // Get the expertise
        restExpertiseMockMvc
            .perform(get(ENTITY_API_URL_ID, expertise.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(expertise.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.level").value(DEFAULT_LEVEL));
    }

    @Test
    @Transactional
    void getNonExistingExpertise() throws Exception {
        // Get the expertise
        restExpertiseMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingExpertise() throws Exception {
        // Initialize the database
        insertedExpertise = expertiseRepository.saveAndFlush(expertise);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the expertise
        Expertise updatedExpertise = expertiseRepository.findById(expertise.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedExpertise are not directly saved in db
        em.detach(updatedExpertise);
        updatedExpertise.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION).level(UPDATED_LEVEL);
        ExpertiseDTO expertiseDTO = expertiseMapper.toDto(updatedExpertise);

        restExpertiseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, expertiseDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(expertiseDTO))
            )
            .andExpect(status().isOk());

        // Validate the Expertise in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedExpertiseToMatchAllProperties(updatedExpertise);
    }

    @Test
    @Transactional
    void putNonExistingExpertise() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        expertise.setId(longCount.incrementAndGet());

        // Create the Expertise
        ExpertiseDTO expertiseDTO = expertiseMapper.toDto(expertise);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExpertiseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, expertiseDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(expertiseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Expertise in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchExpertise() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        expertise.setId(longCount.incrementAndGet());

        // Create the Expertise
        ExpertiseDTO expertiseDTO = expertiseMapper.toDto(expertise);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExpertiseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(expertiseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Expertise in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamExpertise() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        expertise.setId(longCount.incrementAndGet());

        // Create the Expertise
        ExpertiseDTO expertiseDTO = expertiseMapper.toDto(expertise);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExpertiseMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(expertiseDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Expertise in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateExpertiseWithPatch() throws Exception {
        // Initialize the database
        insertedExpertise = expertiseRepository.saveAndFlush(expertise);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the expertise using partial update
        Expertise partialUpdatedExpertise = new Expertise();
        partialUpdatedExpertise.setId(expertise.getId());

        partialUpdatedExpertise.level(UPDATED_LEVEL);

        restExpertiseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExpertise.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedExpertise))
            )
            .andExpect(status().isOk());

        // Validate the Expertise in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertExpertiseUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedExpertise, expertise),
            getPersistedExpertise(expertise)
        );
    }

    @Test
    @Transactional
    void fullUpdateExpertiseWithPatch() throws Exception {
        // Initialize the database
        insertedExpertise = expertiseRepository.saveAndFlush(expertise);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the expertise using partial update
        Expertise partialUpdatedExpertise = new Expertise();
        partialUpdatedExpertise.setId(expertise.getId());

        partialUpdatedExpertise.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION).level(UPDATED_LEVEL);

        restExpertiseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExpertise.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedExpertise))
            )
            .andExpect(status().isOk());

        // Validate the Expertise in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertExpertiseUpdatableFieldsEquals(partialUpdatedExpertise, getPersistedExpertise(partialUpdatedExpertise));
    }

    @Test
    @Transactional
    void patchNonExistingExpertise() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        expertise.setId(longCount.incrementAndGet());

        // Create the Expertise
        ExpertiseDTO expertiseDTO = expertiseMapper.toDto(expertise);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExpertiseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, expertiseDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(expertiseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Expertise in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchExpertise() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        expertise.setId(longCount.incrementAndGet());

        // Create the Expertise
        ExpertiseDTO expertiseDTO = expertiseMapper.toDto(expertise);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExpertiseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(expertiseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Expertise in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamExpertise() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        expertise.setId(longCount.incrementAndGet());

        // Create the Expertise
        ExpertiseDTO expertiseDTO = expertiseMapper.toDto(expertise);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExpertiseMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(expertiseDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Expertise in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteExpertise() throws Exception {
        // Initialize the database
        insertedExpertise = expertiseRepository.saveAndFlush(expertise);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the expertise
        restExpertiseMockMvc
            .perform(delete(ENTITY_API_URL_ID, expertise.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return expertiseRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Expertise getPersistedExpertise(Expertise expertise) {
        return expertiseRepository.findById(expertise.getId()).orElseThrow();
    }

    protected void assertPersistedExpertiseToMatchAllProperties(Expertise expectedExpertise) {
        assertExpertiseAllPropertiesEquals(expectedExpertise, getPersistedExpertise(expectedExpertise));
    }

    protected void assertPersistedExpertiseToMatchUpdatableProperties(Expertise expectedExpertise) {
        assertExpertiseAllUpdatablePropertiesEquals(expectedExpertise, getPersistedExpertise(expectedExpertise));
    }
}
