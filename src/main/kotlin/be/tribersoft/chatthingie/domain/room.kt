package be.tribersoft.chatthingie.domain

import org.springframework.stereotype.Component
import java.util.*

data class Room(val name: String, val id: UUID = UUID.randomUUID())

@Component
class RoomRepository() {
  fun all() = setOf("room1", "room2")
}

val room1 = Room("room1")
val room2 = Room("room2")
val room3 = Room("room3")
