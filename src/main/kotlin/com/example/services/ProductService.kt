package com.example.services

import com.example.database.DatabaseFactory.dbQuery
import com.example.database.Products
import com.example.models.Product
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime

class ProductService {

    suspend fun getAllProducts(): List<Product> = dbQuery {
        Products.selectAll()
            .map { it.toProduct() }
    }

    suspend fun getProduct(id: Int): Product? = dbQuery {
        Products.select { Products.id eq id }
            .map { it.toProduct() }
            .singleOrNull()
    }

    suspend fun addProduct(product: Product): Product {
        var key = 0
        dbQuery {
            key = (Products.insert {
                it[name] = product.name
                it[description] = product.description
                it[price] = product.price
                it[quantity] = product.quantity
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            } get Products.id).value
        }
        return getProduct(key) ?: throw IllegalStateException("Product not found after insert")
    }

    suspend fun updateProduct(id: Int, product: Product): Boolean = dbQuery {
        Products.update({ Products.id eq id }) {
            it[name] = product.name
            it[description] = product.description
            it[price] = product.price
            it[quantity] = product.quantity
            it[updatedAt] = LocalDateTime.now()
        } > 0
    }

    suspend fun deleteProduct(id: Int): Boolean = dbQuery {
        Products.deleteWhere { Products.id eq id } > 0
    }

    private fun ResultRow.toProduct(): Product = Product(
        id = this[Products.id].value,
        name = this[Products.name],
        description = this[Products.description],
        price = this[Products.price],
        quantity = this[Products.quantity],
        createdAt = this[Products.createdAt],
        updatedAt = this[Products.updatedAt]
    )
}