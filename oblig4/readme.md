# Oblig 4

## Info om DB
For graf og kolonnefamilie databasene har de hver sin fil med info om hva som er tenkt med hver av databasene. I tillegg inneholder de koden som brukes for hver database.

## GitHub
[Github](https://github.com/jreberg98/Big-Data)

Alt ligger i tillegg på github. I mappen oblig 4 ligger info om kolonnefamilie og graf databasene. Kode ligger i rot mappen. I tillegg er også filene for dokument databasen i de ulike filene der.

## Dokument database kode

### Legge til data
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

### Henting av data
import java.io._
import java.util.concurrent.TimeUnit

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import org.mongodb.scala._
import org.mongodb.scala.model._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Projections._
import org.mongodb.scala.model.Sorts._
import org.mongodb.scala.model.Updates._
import org.mongodb.scala.model.UpdateOptions
import org.mongodb.scala.bson.BsonObjectId
import scala.Console


object DocumentGetData extends App {
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

    print("Hvem land vil du ha data for? ")
    val country = Console.readLine()


    print("Hvem dato vil du ha data for? ")
    val date = Console.readLine()

    //collection.find().printResults();
    //collection.find(Document()).printResults();

    collection.find(Document("Coutry" -> country,
                             "Date" -> date)
        ).printResults()
}

### Laste inn datasettet
import java.io._
import java.util.concurrent.TimeUnit

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import org.mongodb.scala._
import org.mongodb.scala.model._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Projections._
import org.mongodb.scala.model.Sorts._
import org.mongodb.scala.model.Updates._
import org.mongodb.scala.model.UpdateOptions
import org.mongodb.scala.bson.BsonObjectId


object DocumentInsertData extends App {
    
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

    var errorInDataset = 0;

    val mongoClient: MongoClient = MongoClient();
    val database: MongoDatabase = mongoClient.getDatabase("test");
    val collection: MongoCollection[Document] = database.getCollection("profiles");

    val source = io.Source.fromFile("./datasett/datasett_dokument.csv")


    source.getLines.drop(1).foreach { line =>
        val row = line.split(",").map(_.trim)

        def parseInt(value : String) : Option[Int] = if (value == "") None else Some(value.toInt)

        // Skal bare bruke rader for landene, ikke for subregioner i landene
        if(row(2) != "") {
            println("skip")
        } else {
            // Hopper over alle rader som mangler
            if(row.length == 15){
                val document = Document(
                "Region_code" -> row(0),
                "Coutry" -> row(1),
                "Sub_region" -> row(2),
                "Date" -> row(8),
                "Recreation" -> parseInt(row(9)),
                "Stores" -> parseInt(row(10)),
                "Parks" -> parseInt(row(11)),
                "Public_transport" -> parseInt(row(12)),
                "Work" -> parseInt(row(13)),
                "At_home" -> parseInt(row(14))
                );

                collection.insertOne(document).results();
            println("\n\n" + document + "\n\n")
            } else {
                errorInDataset += 1;
            }
        }
    }

    println("All documents done")
    println(errorInDataset + " rader i datasettet var feil")
}