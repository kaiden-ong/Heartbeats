package edu.uw.ischool.kong314.heartbeats

import com.mongodb.client.model.Filters.eq
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

// Create data class to represent a MongoDB document
fun main() {
    val database = getDatabase()
    runBlocking {
        database.listCollectionNames().collect() {
            println(it)
        }
    }
}

fun getDatabase(): MongoDatabase {
    val client = MongoClient.create(connectionString = System.getenv("MONGO_URI")!!)
    return client.getDatabase(databaseName = "sample_training")
}