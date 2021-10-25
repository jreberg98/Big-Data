import java.io._
import java.util.concurrent.TimeUnit

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.Console

import org.mongodb.scala._
import org.mongodb.scala.model._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Projections._
import org.mongodb.scala.model.Sorts._
import org.mongodb.scala.model.Updates._
import org.mongodb.scala.model.UpdateOptions
import org.mongodb.scala.bson.BsonObjectId

object DocumentAddData extends App {
    // Hjelpeting
    implicit class DocumentObservable[C](val observable: Observable[Document]) extends ImplicitObservable[Document] {
        override val converter: (Document) => String = (doc) => doc.toJson
    }

    implicit class GenericObservable[C](val observable: Observable[C]) extends ImplicitObservable[C] {
        override val converter: (C) => String = (doc) => doc.toString
    }

    trait ImplicitObservable[C] {
        val observable: Observable[C]
        val converter: (C) => String

        def results(): Seq[C] = Await.result(observable.toFuture(), Duration(10, TimeUnit.SECONDS))
        def headResult() = Await.result(observable.head(), Duration(10, TimeUnit.SECONDS))
        def printResults(initial: String = ""): Unit = {
        if (initial.length > 0) print(initial)
        results().foreach(res => println(converter(res)))
        }
        def printHeadResult(initial: String = ""): Unit = println(s"${initial}${converter(headResult())}")
    }

    val mongoClient: MongoClient = MongoClient();
    val database: MongoDatabase = mongoClient.getDatabase("test");
    val collection: MongoCollection[Document] = database.getCollection("profiles");



    println("Hva er landets regionkode?")
    val regionCode = Console.readLine()

    println("Hvem land vil du legge inn data for?")
    val country = Console.readLine()

    println("Hvilken dato gjelder oppføringen for?")
    val date = Console.readLine()

    println("Hva er endringen i fritidsaktiviteter?")
    val recreation = Console.readInt()

    println("Hva er endringen i butikktid?")
    val stores = Console.readInt()

    println("Hva er endringen i tid brukt i parker o.l?")
    val parks  = Console.readInt()

    println("Hva er endringen i bruka av offentlig transport?")
    val publicTransport = Console.readInt()

    println("Hva er endringen i tid brukt på jobb?")
    val work = Console.readInt()

    println("Hva er endringen i tid brukt hjemme?")
    val home = Console.readInt()

    val document = Document(
        "Region_code" -> regionCode,
        "Coutry" -> country,
        "Sub_region" -> "",
        "Date" -> date,
        "Recreation" -> recreation,
        "Stores" -> stores,
        "Parks" -> parks,
        "Public_transport" -> publicTransport,
        "Work" -> work,
        "At_home" -> home
    );

    println(document)


    collection.insertOne(document)

    println("Har lagt inn i DB \n")
    
    // Skrivefeil på å legge inn i DB, skulle vært "Country"
    collection.find(document
        ).printResults()

}