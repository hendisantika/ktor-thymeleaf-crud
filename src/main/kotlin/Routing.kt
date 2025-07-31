package com.example

import com.example.models.Product
import com.example.services.ProductService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.*
import java.math.BigDecimal

fun Application.configureRouting() {
    val productService = ProductService()

    routing {
        // Home page - list all products
        get("/") {
            val products = productService.getAllProducts()
            call.respond(ThymeleafContent("index", mapOf("products" to products)))
        }

        // Create new product form
        get("/products/new") {
            call.respond(ThymeleafContent("create", mapOf()))
        }

        // Save new product
        post("/products") {
            val parameters = call.receiveParameters()
            val name = parameters["name"] ?: ""
            val description = parameters["description"] ?: ""
            val price = parameters["price"]?.toBigDecimalOrNull() ?: BigDecimal.ZERO
            val quantity = parameters["quantity"]?.toIntOrNull() ?: 0

            val product = Product(
                name = name,
                description = description,
                price = price,
                quantity = quantity
            )

            productService.addProduct(product)
            call.respondRedirect("/", permanent = false)
        }

        // View product details
        get("/products/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@get
            }

            val product = productService.getProduct(id)
            if (product == null) {
                call.respond(HttpStatusCode.NotFound, "Product not found")
                return@get
            }

            call.respond(ThymeleafContent("view", mapOf("product" to product)))
        }

        // Edit product form
        get("/products/{id}/edit") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@get
            }

            val product = productService.getProduct(id)
            if (product == null) {
                call.respond(HttpStatusCode.NotFound, "Product not found")
                return@get
            }

            call.respond(ThymeleafContent("edit", mapOf("product" to product)))
        }

        // Update product
        post("/products/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@post
            }

            val parameters = call.receiveParameters()
            val method = parameters["_method"]

            // Handle PUT request (update)
            if (method == "put") {
                val name = parameters["name"] ?: ""
                val description = parameters["description"] ?: ""
                val price = parameters["price"]?.toBigDecimalOrNull() ?: BigDecimal.ZERO
                val quantity = parameters["quantity"]?.toIntOrNull() ?: 0

                val product = Product(
                    id = id,
                    name = name,
                    description = description,
                    price = price,
                    quantity = quantity
                )

                val updated = productService.updateProduct(id, product)
                if (!updated) {
                    call.respond(HttpStatusCode.NotFound, "Product not found")
                    return@post
                }

                call.respondRedirect("/products/$id", permanent = false)
            } else {
                call.respond(HttpStatusCode.MethodNotAllowed)
            }
        }

        // Delete product
        post("/products/{id}/delete") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@post
            }

            val deleted = productService.deleteProduct(id)
            if (!deleted) {
                call.respond(HttpStatusCode.NotFound, "Product not found")
                return@post
            }

            call.respondRedirect("/", permanent = false)
        }
    }
}
