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