package be.tribersoft.chatthingie.domain

import org.springframework.stereotype.Component
import java.util.*

data class Room(val name: String, val withHistory: Boolean = false, val id: UUID = UUID.randomUUID())

@Component
class RoomRepository(private val rooms: MutableList<Room> = mutableListOf(room1, room2, room3)) {
  fun all() = rooms
  fun add(room: Room) = rooms.add(room)
  fun delete(roomId: UUID) = rooms.removeIf { it.id == roomId}
  fun getById(roomId: UUID) = rooms.find { it.id == roomId }!!
}

val room1 = Room("room1")
val room2 = Room("room2")
val room3 = Room("room3")
