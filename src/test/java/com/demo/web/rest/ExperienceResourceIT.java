package com.demo.web.rest;

import static com.demo.domain.ExperienceAsserts.*;
import static com.demo.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.demo.IntegrationTest;
import com.demo.domain.Experience;
import com.demo.domain.enumeration.Contract;
import com.demo.repository.ExperienceRepository;
import com.demo.service.ExperienceService;
import com.demo.service.dto.ExperienceDTO;
import com.demo.service.mapper.ExperienceMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ExperienceResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ExperienceResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_COMPANY = "AAAAAAAAAA";
    private static final String UPDATED_COMPANY = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final byte[] DEFAULT_LOGO_COMPANY = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_LOGO_COMPANY = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_LOGO_COMPANY_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_LOGO_COMPANY_CONTENT_TYPE = "image/png";

    private static final Boolean DEFAULT_IN_PROGRESS = false;
    private static final Boolean UPDATED_IN_PROGRESS = true;

    private static final Contract DEFAULT_CONTRACT = Contract.CDI;
    private static final Contract UPDATED_CONTRACT = Contract.CDD;

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/experiences";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ExperienceRepository experienceRepository;

    @Mock
    private ExperienceRepository experienceRepositoryMock;

    @Autowired
    private ExperienceMapper experienceMapper;

    @Mock
    private ExperienceService experienceServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restExperienceMockMvc;

    private Experience experience;

    private Experience insertedExperience;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Experience createEntity() {
        return new Experience()
            .title(DEFAULT_TITLE)
            .company(DEFAULT_COMPANY)
            .description(DEFAULT_DESCRIPTION)
            .logoCompany(DEFAULT_LOGO_COMPANY)
            .logoCompanyContentType(DEFAULT_LOGO_COMPANY_CONTENT_TYPE)
            .inProgress(DEFAULT_IN_PROGRESS)
            .contract(DEFAULT_CONTRACT)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Experience createUpdatedEntity() {
        return new Experience()
            .title(UPDATED_TITLE)
            .company(UPDATED_COMPANY)
            .description(UPDATED_DESCRIPTION)
            .logoCompany(UPDATED_LOGO_COMPANY)
            .logoCompanyContentType(UPDATED_LOGO_COMPANY_CONTENT_TYPE)
            .inProgress(UPDATED_IN_PROGRESS)
            .contract(UPDATED_CONTRACT)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE);
    }

    @BeforeEach
    public void initTest() {
        experience = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedExperience != null) {
            experienceRepository.delete(insertedExperience);
            insertedExperience = null;
        }
    }

    @Test
    @Transactional
    void createExperience() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Experience
        ExperienceDTO experienceDTO = experienceMapper.toDto(experience);
        var returnedExperienceDTO = om.readValue(
            restExperienceMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(experienceDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ExperienceDTO.class
        );

        // Validate the Experience in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedExperience = experienceMapper.toEntity(returnedExperienceDTO);
        assertExperienceUpdatableFieldsEquals(returnedExperience, getPersistedExperience(returnedExperience));

        insertedExperience = returnedExperience;
    }

    @Test
    @Transactional
    void createExperienceWithExistingId() throws Exception {
        // Create the Experience with an existing ID
        experience.setId(1L);
        ExperienceDTO experienceDTO = experienceMapper.toDto(experience);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restExperienceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(experienceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Experience in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        experience.setTitle(null);

        // Create the Experience, which fails.
        ExperienceDTO experienceDTO = experienceMapper.toDto(experience);

        restExperienceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(experienceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCompanyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        experience.setCompany(null);

        // Create the Experience, which fails.
        ExperienceDTO experienceDTO = experienceMapper.toDto(experience);

        restExperienceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(experienceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkInProgressIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        experience.setInProgress(null);

        // Create the Experience, which fails.
        ExperienceDTO experienceDTO = experienceMapper.toDto(experience);

        restExperienceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(experienceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkContractIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        experience.setContract(null);

        // Create the Experience, which fails.
        ExperienceDTO experienceDTO = experienceMapper.toDto(experience);

        restExperienceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(experienceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllExperiences() throws Exception {
        // Initialize the database
        insertedExperience = experienceRepository.saveAndFlush(experience);

        // Get all the experienceList
        restExperienceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(experience.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].company").value(hasItem(DEFAULT_COMPANY)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].logoCompanyContentType").value(hasItem(DEFAULT_LOGO_COMPANY_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].logoCompany").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_LOGO_COMPANY))))
            .andExpect(jsonPath("$.[*].inProgress").value(hasItem(DEFAULT_IN_PROGRESS.booleanValue())))
            .andExpect(jsonPath("$.[*].contract").value(hasItem(DEFAULT_CONTRACT.toString())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllExperiencesWithEagerRelationshipsIsEnabled() throws Exception {
        when(experienceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restExperienceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(experienceServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllExperiencesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(experienceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restExperienceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(experienceRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getExperience() throws Exception {
        // Initialize the database
        insertedExperience = experienceRepository.saveAndFlush(experience);

        // Get the experience
        restExperienceMockMvc
            .perform(get(ENTITY_API_URL_ID, experience.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(experience.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.company").value(DEFAULT_COMPANY))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.logoCompanyContentType").value(DEFAULT_LOGO_COMPANY_CONTENT_TYPE))
            .andExpect(jsonPath("$.logoCompany").value(Base64.getEncoder().encodeToString(DEFAULT_LOGO_COMPANY)))
            .andExpect(jsonPath("$.inProgress").value(DEFAULT_IN_PROGRESS.booleanValue()))
            .andExpect(jsonPath("$.contract").value(DEFAULT_CONTRACT.toString()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingExperience() throws Exception {
        // Get the experience
        restExperienceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingExperience() throws Exception {
        // Initialize the database
        insertedExperience = experienceRepository.saveAndFlush(experience);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the experience
        Experience updatedExperience = experienceRepository.findById(experience.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedExperience are not directly saved in db
        em.detach(updatedExperience);
        updatedExperience
            .title(UPDATED_TITLE)
            .company(UPDATED_COMPANY)
            .description(UPDATED_DESCRIPTION)
            .logoCompany(UPDATED_LOGO_COMPANY)
            .logoCompanyContentType(UPDATED_LOGO_COMPANY_CONTENT_TYPE)
            .inProgress(UPDATED_IN_PROGRESS)
            .contract(UPDATED_CONTRACT)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE);
        ExperienceDTO experienceDTO = experienceMapper.toDto(updatedExperience);

        restExperienceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, experienceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(experienceDTO))
            )
            .andExpect(status().isOk());

        // Validate the Experience in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedExperienceToMatchAllProperties(updatedExperience);
    }

    @Test
    @Transactional
    void putNonExistingExperience() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        experience.setId(longCount.incrementAndGet());

        // Create the Experience
        ExperienceDTO experienceDTO = experienceMapper.toDto(experience);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExperienceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, experienceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(experienceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Experience in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchExperience() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        experience.setId(longCount.incrementAndGet());

        // Create the Experience
        ExperienceDTO experienceDTO = experienceMapper.toDto(experience);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExperienceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(experienceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Experience in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamExperience() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        experience.setId(longCount.incrementAndGet());

        // Create the Experience
        ExperienceDTO experienceDTO = experienceMapper.toDto(experience);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExperienceMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(experienceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Experience in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateExperienceWithPatch() throws Exception {
        // Initialize the database
        insertedExperience = experienceRepository.saveAndFlush(experience);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the experience using partial update
        Experience partialUpdatedExperience = new Experience();
        partialUpdatedExperience.setId(experience.getId());

        partialUpdatedExperience
            .logoCompany(UPDATED_LOGO_COMPANY)
            .logoCompanyContentType(UPDATED_LOGO_COMPANY_CONTENT_TYPE)
            .contract(UPDATED_CONTRACT);

        restExperienceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExperience.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedExperience))
            )
            .andExpect(status().isOk());

        // Validate the Experience in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertExperienceUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedExperience, experience),
            getPersistedExperience(experience)
        );
    }

    @Test
    @Transactional
    void fullUpdateExperienceWithPatch() throws Exception {
        // Initialize the database
        insertedExperience = experienceRepository.saveAndFlush(experience);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the experience using partial update
        Experience partialUpdatedExperience = new Experience();
        partialUpdatedExperience.setId(experience.getId());

        partialUpdatedExperience
            .title(UPDATED_TITLE)
            .company(UPDATED_COMPANY)
            .description(UPDATED_DESCRIPTION)
            .logoCompany(UPDATED_LOGO_COMPANY)
            .logoCompanyContentType(UPDATED_LOGO_COMPANY_CONTENT_TYPE)
            .inProgress(UPDATED_IN_PROGRESS)
            .contract(UPDATED_CONTRACT)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE);

        restExperienceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExperience.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedExperience))
            )
            .andExpect(status().isOk());

        // Validate the Experience in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertExperienceUpdatableFieldsEquals(partialUpdatedExperience, getPersistedExperience(partialUpdatedExperience));
    }

    @Test
    @Transactional
    void patchNonExistingExperience() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        experience.setId(longCount.incrementAndGet());

        // Create the Experience
        ExperienceDTO experienceDTO = experienceMapper.toDto(experience);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExperienceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, experienceDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(experienceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Experience in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchExperience() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        experience.setId(longCount.incrementAndGet());

        // Create the Experience
        ExperienceDTO experienceDTO = experienceMapper.toDto(experience);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExperienceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(experienceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Experience in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamExperience() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        experience.setId(longCount.incrementAndGet());

        // Create the Experience
        ExperienceDTO experienceDTO = experienceMapper.toDto(experience);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExperienceMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(experienceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Experience in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteExperience() throws Exception {
        // Initialize the database
        insertedExperience = experienceRepository.saveAndFlush(experience);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the experience
        restExperienceMockMvc
            .perform(delete(ENTITY_API_URL_ID, experience.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return experienceRepository.count();
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

    protected Experience getPersistedExperience(Experience experience) {
        return experienceRepository.findById(experience.getId()).orElseThrow();
    }

    protected void assertPersistedExperienceToMatchAllProperties(Experience expectedExperience) {
        assertExperienceAllPropertiesEquals(expectedExperience, getPersistedExperience(expectedExperience));
    }

    protected void assertPersistedExperienceToMatchUpdatableProperties(Experience expectedExperience) {
        assertExperienceAllUpdatablePropertiesEquals(expectedExperience, getPersistedExperience(expectedExperience));
    }
}
