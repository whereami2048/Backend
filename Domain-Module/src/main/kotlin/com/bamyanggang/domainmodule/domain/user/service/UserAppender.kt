package com.bamyanggang.domainmodule.domain.user.service

import com.bamyanggang.domainmodule.domain.user.aggregate.User
import com.bamyanggang.domainmodule.domain.user.enums.SocialLoginProvider
import com.bamyanggang.domainmodule.domain.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserAppender(
    private val userRepository: UserRepository
) {
    fun appendUser (
        socialId: String,
        profileImgUrl: String,
        provider: SocialLoginProvider,
        nickName: String,
        jobSearchStatus: String?,
        desiredJob: String?,
        goal: String?,
        dream: String?
    ): User {
        return User.create(
            socialId = socialId,
            profileImgUrl = profileImgUrl,
            provider = provider,
            nickName = nickName,
            jobSearchStatus = jobSearchStatus,
            desiredJob = desiredJob,
            goal = goal,
            dream = dream
        ).also { userRepository.save(it) }
    }
}