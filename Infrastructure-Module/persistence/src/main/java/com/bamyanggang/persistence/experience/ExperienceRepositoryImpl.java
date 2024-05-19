package com.bamyanggang.persistence.experience;

import com.bamyanggang.domainmodule.domain.experience.aggregate.Experience;
import com.bamyanggang.domainmodule.domain.experience.repository.ExperienceRepository;
import com.bamyanggang.persistence.common.exception.PersistenceException.NotFound;
import com.bamyanggang.persistence.experience.jpa.entity.ExperienceJpaEntity;
import com.bamyanggang.persistence.experience.jpa.repository.ExperienceJpaRepository;
import com.bamyanggang.persistence.experience.mapper.ExperienceMapper;
import com.bamyanggang.persistence.user.UserRepositoryImpl;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ExperienceRepositoryImpl implements ExperienceRepository {
    private final ExperienceJpaRepository experienceJpaRepository;
    private final ExperienceMapper experienceMapper;
    private final UserRepositoryImpl userRepositoryImpl;

    @Override
    public void save(Experience experience) {
        ExperienceJpaEntity experienceJpaEntity = experienceMapper.toExperienceJpaEntity(experience);
        experienceJpaRepository.save(experienceJpaEntity);
    }

    @Override
    public void deleteByExperienceId(UUID experienceId) {
        experienceJpaRepository.deleteById(experienceId);
    }

    @Override
    public Experience findByExperienceId(UUID experienceId) {
        ExperienceJpaEntity experienceJpaEntity = experienceJpaRepository.findById(experienceId)
                .orElseThrow(NotFound::new);

        return experienceMapper.toExperienceDomainEntity(experienceJpaEntity);
    }

    @Override
    public List<Experience> findAllByUserId(UUID userId) {
        List<ExperienceJpaEntity> userExperienceJpaEntities = experienceJpaRepository.findAllByUserId(userId);
        return userExperienceJpaEntities.stream().map(experienceMapper::toExperienceDomainEntity).toList();
    }

    @Override
    public List<Experience> findByUserIdAndYearDesc(int year, UUID userId) {
        LocalDateTime startYear = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime endYear = LocalDateTime.of(year, 12, 31, 23, 59);
        List<ExperienceJpaEntity> experienceJpaEntities = experienceJpaRepository
                .findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(userId, startYear, endYear);

        return experienceJpaEntities.stream().map(experienceMapper::toExperienceDomainEntity).toList();
    }
}
