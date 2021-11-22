import org.apache.spark.sql.SparkSession


import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._

import scala.io.Source

object ColumnFamilyInsertData {
    def main(args: Array[String]) {
        val spark = SparkSession.builder
            .config("spark.master", "local")
            .appName("ColumnFamilyInsertData")
            .getOrCreate()

        val fileName = "datasett/country_vaccinations.csv"
        // Schema for DF
        val kolonnefamilieSchema = 
        StructType(Seq(
            StructField("country", StringType, true),
            StructField("iso_code", StringType, true),
            StructField("date", DateType, true),
            StructField("total_vaccinated", IntegerType, true),
            StructField("people_fully_vaccinated", IntegerType, true),
            StructField("daily_vaccinations_raw", IntegerType, true),
            StructField("daily_vaccinations", IntegerType, true),
            StructField("total_vaccinations_per_hundred", FloatType, true),
            StructField("people_vaccinated_per_hundred", FloatType, true),
            StructField("people_fully_vaccinated_per_hundred", FloatType, true),
            StructField("daily_vaccinations_per_million", FloatType, true),
            StructField("vaccines", StringType, true),
            StructField("source_name", StringType, true),
            StructField("source_website", StringType, true)
        ))

        // Laster inn DF
        // val kolonnefamilieDF = spark.read.format("csv").schema(kolonnefamilieSchema).option("header", true).load(fileName)
        val kolonnefamilieDF = spark.read
            .format("csv")
            .schema(kolonnefamilieSchema)
            .option("header", true)
            .load(fileName)

        // Skriver DF
        // kolonnefamilieDF.write.format("org.apache.spark.sql.cassandra").option("table","vaccine").option("keyspace","kode").option("confirm.truncate", true).mode("Overwrite").save()
        kolonnefamilieDF.write
            .format("org.apache.spark.sql.cassandra")
            .option("table","vaccine")
            .option("keyspace","kode")
            .option("confirm.truncate", true)
            .mode("Overwrite")
            .save()
    }
}

/*
    // Oppretter keyspace og tabell

    CREATE KEYSPACE kode WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

    CREATE TABLE kode.vaccine (
        country TEXT,
        iso_code TEXT,
        date DATE,
        total_vaccinated INT,
        people_fully_vaccinated INT,
        daily_vaccinations_raw INT,
        daily_vaccinations INT,
        total_vaccinations_per_hundred DOUBLE,
        people_vaccinated_per_hundred DOUBLE,
        people_fully_vaccinated_per_hundred DOUBLE,
        daily_vaccinations_per_million DOUBLE,
        vaccines TEXT,
        source_name TEXT,
        source_website TEXT,
        PRIMARY KEY (country, date)
    );
    CREATE TABLE kode.vaccine (country TEXT,iso_code TEXT,date DATE,total_vaccinated INT,people_fully_vaccinated INT,daily_vaccinations_raw INT,daily_vaccinations INT,total_vaccinations_per_hundred DOUBLE,people_vaccinated_per_hundred DOUBLE,people_fully_vaccinated_per_hundred DOUBLE,daily_vaccinations_per_million DOUBLE,vaccines TEXT,source_name TEXT,source_website TEXT,PRIMARY KEY (country, date));
*/
/*
Starter spark shell
spark-shell --conf spark.cassandra.connection.host=127.0.0.1 \
--packages com.datastax.spark:spark-cassandra-connector_2.12:3.0.0 \
--conf spark.sql.extensions=com.datastax.spark.connector.CassandraSparkExtensions
*/