package com.bamyanggang.domainmodule.domain.user.service

import com.bamyanggang.domainmodule.domain.user.aggregate.Token
import com.bamyanggang.domainmodule.domain.user.repository.TokenRepository

class TokenReader(
    private val tokenRepository: TokenRepository
) {
    fun readToken(
        refreshToken: String
    ): Token {
        return tokenRepository.findByValue(refreshToken)
    }

}
