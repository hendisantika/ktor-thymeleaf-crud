package com.example.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Products : IntIdTable() {
    val name = varchar("name", 255)
    val description = text("description")
    val price = decimal("price", 10, 2)
    val quantity = integer("quantity")
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    val updatedAt = datetime("updated_at").default(LocalDateTime.now())
}