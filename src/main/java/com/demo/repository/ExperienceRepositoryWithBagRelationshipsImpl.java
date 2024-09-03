package com.demo.repository;

import com.demo.domain.Experience;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class ExperienceRepositoryWithBagRelationshipsImpl implements ExperienceRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String EXPERIENCES_PARAMETER = "experiences";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Experience> fetchBagRelationships(Optional<Experience> experience) {
        return experience.map(this::fetchExpertise);
    }

    @Override
    public Page<Experience> fetchBagRelationships(Page<Experience> experiences) {
        return new PageImpl<>(fetchBagRelationships(experiences.getContent()), experiences.getPageable(), experiences.getTotalElements());
    }

    @Override
    public List<Experience> fetchBagRelationships(List<Experience> experiences) {
        return Optional.of(experiences).map(this::fetchExpertise).orElse(Collections.emptyList());
    }

    Experience fetchExpertise(Experience result) {
        return entityManager
            .createQuery(
                "select experience from Experience experience left join fetch experience.expertise where experience.id = :id",
                Experience.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<Experience> fetchExpertise(List<Experience> experiences) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, experiences.size()).forEach(index -> order.put(experiences.get(index).getId(), index));
        List<Experience> result = entityManager
            .createQuery(
                "select experience from Experience experience left join fetch experience.expertise where experience in :experiences",
                Experience.class
            )
            .setParameter(EXPERIENCES_PARAMETER, experiences)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
