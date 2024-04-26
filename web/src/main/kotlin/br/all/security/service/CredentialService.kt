package br.all.security.service

import br.all.application.shared.exceptions.UnauthorizedUserException
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class CredentialService {
    fun getAuthenticatedUserId(): UUID {
        val authentication = SecurityContextHolder.getContext().authentication
        if (!authentication.isAuthenticated) throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized user.")
        val applicationUser = authentication.principal as ApplicationUser
        return applicationUser.id
    }
}