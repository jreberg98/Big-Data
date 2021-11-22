import org.apache.spark.sql.{SaveMode, SparkSession}
import java.util.Date
//import org.neo4j.spark.DataSource

object JoinPlanes extends App {
    
    val spark = SparkSession.builder
        .config("spark.master", "local")
        .appName("ColumnFamilyInsertData")
        .getOrCreate()

    val country = "Norway"
    val date = new Date("2020-04-03")

    // Laster fra graf DB
    val query = "MATCH (c:Country)-[plane:Flight]->(destination:Country) WHERE (c.name = \"" + country + "\") AND plane.date = " + date + " RETURN destination.name"

    val graphDF = spark
        .read
        .format("org.neo4j.spark.DataSource")
        .option("url", "bolt://localhost:7687")
        .option("authentication.basic.username", "neo4j")
        .option("authentication.basic.password", "neo4j")
        .option("labels", "Country")
        .option("query", query)
        .load().show()

    val kolonnefamilieDF = spark.read.format("org.apache.spark.sql.cassandra").option("table", "vaccine").option("keyspace", "kode").load

    val joinDF = kolonnefamilieDF
        .join(graphDF, kolonnefamilieDF("country") === graphDF("country"))
    
}