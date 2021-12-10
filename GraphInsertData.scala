import org.apache.spark.sql.types._
import org.apache.spark.sql.SparkSession

object GraphInsertData extends App {
        
    val spark = SparkSession.builder
            .config("spark.master", "local")
            .appName("ColumnFamilyInsertData")
            .getOrCreate()

    // Schema for datasettet
    val schema = StructType(
        StructField("callsign", StringType, true),
        StructField("number", StringType, true),
        StructField("icao24", StringType, true),
        StructField("registration", StringType, true),
        StructField("typecode", StringType, true),
        StructField("origin", StringType, true),
        StructField("destination", StringType, true),
        StructField("firstseen",StringType, true),
        StructField("lastseen", StringType, true),
        StructField("day", StringType, true),
        StructField("lat1", StringType, true),
        StructField("long1", StringType, true),
        StructField("att1", StringType, true),
        StructField("lat2", StringType, true),
        StructField("long2", StringType, true),
        StructField("att2", StringType, true)
    )
    

    // Laster inn datasettet
    val df = spark.read
        .format("csv")
        .option("header", true)
        .schema(schema)
        .load("/datasett/graph.csv")

    // Alle landa som er med i datasettet, tar forbehold da om at det alltid går fly til og fra land
    val countries = df.select("departure").distinct()
    // Skriver nodene
    countries.write
        .format("org.neo4j.spark.DataSource")
        .mode("Overwrite")
        .option("url", "bolt://localhost:7687")
        .option("authentication.basic.username", "neo4j")
        .option("authentication.basic.password", "student")
        .option("label", ":country")
        .option("node.keys", "country")
        .save()

    // Data per flyreise
    val flights = df.select(
            $"origin".as("origin"),
            $"destination".as("destination"),
            $"day".as("date")
        )
    // Skriver relasjonene
    flight.write
        .format("org.neo4j.spark.DataSource")
        .mode("Append")
        .option("url", "bolt://localhost:7687")
        .option("authentication.basic.username", "neo4j")
        .option("authentication.basic.password", "student")
        .option("relationship", "FLIGHT")
        .option("relationship.source.labels", "country")
        .option("relationship.source.node.keys", "source.country:country")
        .option("relationship.target.labels", "countyr")
        .option("relationship.target.node.keys", "country")
        .save()

}