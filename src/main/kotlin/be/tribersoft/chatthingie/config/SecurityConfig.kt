package be.tribersoft.chatthingie.config

import be.tribersoft.chatthingie.domain.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig(): WebSecurityConfigurerAdapter() {
    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/login").permitAll()
                .antMatchers("/index.html","/static/js/*.js", "/static/css/*.css", "/static/*.png").permitAll()
                .anyRequest().authenticated()
                .and().logout().deleteCookies("JSESSIONID").invalidateHttpSession(true).clearAuthentication(true).logoutSuccessHandler(WebSocketCleanUpHandler())
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", CorsConfiguration().applyPermitDefaultValues())
        return source
    }

}

@Component
class UserDetailsService(private val userRepository: UserRepository) {
    fun login(username: String, password: String) = userRepository.getByUsernameAndPassword(username, password)

}

class WebSocketCleanUpHandler(): LogoutSuccessHandler {
    override fun onLogoutSuccess(req: HttpServletRequest?, resp: HttpServletResponse, auth: Authentication) {
        println("close all sockets") // do something smart with the JSESSIONID cookie
        resp.status = HttpStatus.OK.value()
        resp.writer.flush()
    }

}
