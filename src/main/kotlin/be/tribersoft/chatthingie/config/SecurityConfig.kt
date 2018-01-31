package be.tribersoft.chatthingie.config

import be.tribersoft.chatthingie.domain.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig(): WebSecurityConfigurerAdapter() {
    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/login").permitAll()
                .anyRequest().authenticated()
                .and().logout().deleteCookies("JSESSIONID")
                .and().logout().logoutSuccessHandler(( HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK)));

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
//
//class JWTAuthenticationFilter(private val userDetailsService: UserDetailsService, private val objectMapper: ObjectMapper, private val jwtService: JwtService) : UsernamePasswordAuthenticationFilter() {
//
//    @Throws(AuthenticationException::class)
//    override fun attemptAuthentication(req: HttpServletRequest, res: HttpServletResponse): Authentication? {
//        val credentials = objectMapper.readValue(req.inputStream, UserJson::class.java)
//
//        try {
//            val user = userDetailsService.login(credentials.username, credentials.password)
//            return UsernamePasswordAuthenticationToken(user,"", user.authorities)
//        } catch (e: BadCredentialsException) {
//            logger.error(e)
//            handleError(res)
//            return null
//        }
//
//    }
//
//    @Throws(IOException::class, ServletException::class)
//    override fun successfulAuthentication(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain, auth: Authentication) {
//
//        val user = auth.principal as User
//        res.addHeader(JwtConstants.HEADER_NAME, jwtService.create(user))
//    }
//
//    override fun unsuccessfulAuthentication(request: HttpServletRequest?, response: HttpServletResponse?, failed: AuthenticationException?) {
//        super.unsuccessfulAuthentication(request, response, failed)
//        response?.setHeader(JwtConstants.HEADER_NAME, jwtService.emptyJwt())
//    }
//}
//
//class JWTAuthorizationFilter(authManager: AuthenticationManager, private val jwtService: JwtService) : BasicAuthenticationFilter(authManager) {
//
//    @Throws(IOException::class, ServletException::class)
//    override fun doFilterInternal(req: HttpServletRequest,
//                                  res: HttpServletResponse,
//                                  chain: FilterChain) {
//        val header = req.getHeader(JwtConstants.HEADER_NAME)
//        if (header == null || !header.startsWith(JwtConstants.PREFIX)) {
//            chain.doFilter(req, res)
//            return
//        }
//        try {
//            val authentication = jwtService.getToken(header)
//            SecurityContextHolder.getContext().authentication = authentication
//            chain.doFilter(req, res)
//        } catch (e: BadCredentialsException) {
//            logger.error(e)
//            return handleError(res)
//        } catch (e: JWTDecodeException) {
//            logger.error(e)
//            return handleError(res)
//        }
//    }
//
//}
//
//fun handleError(res: HttpServletResponse) {
//    res.sendError(HttpStatus.UNAUTHORIZED.value())
//}
//
//@ConfigurationProperties("jwt")
//data class JwtConfiguration(var secret: String, var duration: Int)
//
//@Component
//class LogoutHandler( private val jwtService: JwtService): LogoutSuccessHandler {
//    override fun onLogoutSuccess(req: HttpServletRequest, res: HttpServletResponse, auth: Authentication?) {
//    }
//}
//
//class JwtConstants {
//    companion object {
//        const val HEADER_NAME = "Authorization"
//        const val PREFIX = "Bearer "
//    }
//}
//
//@Component
//class JwtService(private val jwtConfiguration: JwtConfiguration, private val userRepository: UserRepository) {
//
//    private fun getId(jwt: String): String {
//        val algorithm = Algorithm.HMAC256(jwtConfiguration.secret)
//        val verifier = JWT.require(algorithm)
//                .withIssuer("chatthingie")
//                .build() //Reusable verifier instance
//        val decoded = verifier.verify(jwt.replace(JwtConstants.PREFIX, ""))
//        return decoded.claims["id"]!!.asString()
//    }
//
//    fun getToken(jwt: String): UsernamePasswordAuthenticationToken {
//        val userId = getId(jwt)
//        val user = userRepository.getById(UUID.fromString(userId))
//        return UsernamePasswordAuthenticationToken(user,"", user.authorities)
//    }
//
//    fun create(user: User): String {
//        val algorithm = Algorithm.HMAC256(jwtConfiguration.secret)
//        return JwtConstants.PREFIX + JWT.create()
//                .withIssuer("chatthingie")
//                .withSubject(user.username)
//                .withClaim("id", user.id.toString())
//                .withArrayClaim("roles", user.roles.toTypedArray())
//                .withExpiresAt(Date(System.currentTimeMillis() + jwtConfiguration.duration.toInt()))
//                .sign(algorithm)
//    }
//
//    fun refresh(jwt: String): String {
//        val algorithm = Algorithm.HMAC256(jwtConfiguration.secret)
//        val userId = getId(jwt)
//        val user = userRepository.getById(UUID.fromString(userId))
//        return JwtConstants.PREFIX + JWT.create()
//                .withIssuer("chatthingie")
//                .withSubject(user.username)
//                .withClaim("id", user.id.toString())
//                .withArrayClaim("roles", user.roles.toTypedArray())
//                .withExpiresAt(Date(System.currentTimeMillis() + jwtConfiguration.duration.toInt()))
//                .sign(algorithm)
//    }
//
//    fun emptyJwt() = JwtConstants.PREFIX
//
//}