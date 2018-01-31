package be.tribersoft.chatthingie.rest

import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(path = arrayOf("/test"))
@PreAuthorize("isAuthenticated()")
class CountryResource() {
    @PostMapping(consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun all(@RequestBody something: Something) = "something else"

    @GetMapping
    fun someting() = "something"

}

data class Something(val test:String)