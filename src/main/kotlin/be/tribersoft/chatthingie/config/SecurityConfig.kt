package be.tribersoft.chatthingie.config

import be.tribersoft.chatthingie.domain.UserRepository
import javafx.application.Application
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.event.AuthenticationSuccessEvent
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.session.SessionCreationEvent
import org.springframework.security.core.session.SessionDestroyedEvent
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler
import org.springframework.security.web.session.HttpSessionCreatedEvent
import org.springframework.security.web.session.HttpSessionEventPublisher
import org.springframework.stereotype.Component
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
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
                .antMatchers("/","/index.html","/static/js/*.js", "/static/css/*.css", "/static/*.png").permitAll()
                .anyRequest().authenticated()
                .and().logout().deleteCookies("JSESSIONID").invalidateHttpSession(true).clearAuthentication(true).logoutSuccessHandler(HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK)).permitAll()
          .and().exceptionHandling().authenticationEntryPoint(Http401AuthenticationEntryPoint())
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", CorsConfiguration().applyPermitDefaultValues())
        return source
    }

  @Bean
  fun httpSessionEventPublisher() = HttpSessionEventPublisher()

}

@Component
class UserDetailsService(private val userRepository: UserRepository) {
    fun login(username: String, password: String) = userRepository.getByUsernameAndPassword(username, password)

}

class Http401AuthenticationEntryPoint: AuthenticationEntryPoint {
  override fun commence(req: HttpServletRequest?, resp: HttpServletResponse, auth: AuthenticationException) {
    resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, auth.message)
  }
}


