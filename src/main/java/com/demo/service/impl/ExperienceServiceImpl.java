package com.demo.service.impl;

import com.demo.domain.Experience;
import com.demo.repository.ExperienceRepository;
import com.demo.service.ExperienceService;
import com.demo.service.dto.ExperienceDTO;
import com.demo.service.mapper.ExperienceMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.demo.domain.Experience}.
 */
@Service
@Transactional
public class ExperienceServiceImpl implements ExperienceService {

    private static final Logger LOG = LoggerFactory.getLogger(ExperienceServiceImpl.class);

    private final ExperienceRepository experienceRepository;

    private final ExperienceMapper experienceMapper;

    public ExperienceServiceImpl(ExperienceRepository experienceRepository, ExperienceMapper experienceMapper) {
        this.experienceRepository = experienceRepository;
        this.experienceMapper = experienceMapper;
    }

    @Override
    public ExperienceDTO save(ExperienceDTO experienceDTO) {
        LOG.debug("Request to save Experience : {}", experienceDTO);
        Experience experience = experienceMapper.toEntity(experienceDTO);
        experience = experienceRepository.save(experience);
        return experienceMapper.toDto(experience);
    }

    @Override
    public ExperienceDTO update(ExperienceDTO experienceDTO) {
        LOG.debug("Request to update Experience : {}", experienceDTO);
        Experience experience = experienceMapper.toEntity(experienceDTO);
        experience = experienceRepository.save(experience);
        return experienceMapper.toDto(experience);
    }

    @Override
    public Optional<ExperienceDTO> partialUpdate(ExperienceDTO experienceDTO) {
        LOG.debug("Request to partially update Experience : {}", experienceDTO);

        return experienceRepository
            .findById(experienceDTO.getId())
            .map(existingExperience -> {
                experienceMapper.partialUpdate(existingExperience, experienceDTO);

                return existingExperience;
            })
            .map(experienceRepository::save)
            .map(experienceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExperienceDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Experiences");
        return experienceRepository.findAll(pageable).map(experienceMapper::toDto);
    }

    public Page<ExperienceDTO> findAllWithEagerRelationships(Pageable pageable) {
        return experienceRepository.findAllWithEagerRelationships(pageable).map(experienceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ExperienceDTO> findOne(Long id) {
        LOG.debug("Request to get Experience : {}", id);
        return experienceRepository.findOneWithEagerRelationships(id).map(experienceMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Experience : {}", id);
        experienceRepository.deleteById(id);
    }
}
