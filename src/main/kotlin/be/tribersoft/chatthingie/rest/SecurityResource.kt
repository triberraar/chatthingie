package be.tribersoft.chatthingie.rest

import be.tribersoft.chatthingie.config.UserDetailsService
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping()
class SecurityResource(private val userDetailsService: UserDetailsService) {

    @PostMapping(path = arrayOf("/login"), consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun login(@RequestBody json: LoginJson) {
        val user = userDetailsService.login(json.username, json.password)
        val authentication = UsernamePasswordAuthenticationToken(user, "", user.authorities)
        SecurityContextHolder.getContext().authentication = authentication
    }
}

data class LoginJson(val username: String, val password: String)
