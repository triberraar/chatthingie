package be.tribersoft.chatthingie

import org.springframework.boot.SpringApplication
import org.springframework.core.env.AbstractEnvironment

fun main(args: Array<String>) {
  System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "dev,proxy")
  SpringApplication.run(ChatthingieApplication::class.java, *args)
}
