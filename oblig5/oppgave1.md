# Oppgave 1
## Deloppgave 1

### Dokument 
import org.apache.spark.sql.types._

val dokumentSchema = 
StructType(Seq(
    StructField("country_region_code", StringType, true),
    StructField("country_region", StringType, true),
    StructField("sub_region_1", StringType, true),
    StructField("sub_region_2", StringType, true),
    StructField("metro_area", StringType, true),
    StructField("iso_3166_2_code", StringType, true),
    StructField("census_fips_code", IntegerType, true),
    StructField("place_id", StringType, true),
    StructField("date", DateType, true),
    StructField("retail_and_recreation_percent_change_from_baseline", IntegerType, true),
    StructField("grocery_and_pharmacy_percent_change_from_baseline", IntegerType, true),
    StructField("parks_percent_change_from_baseline", IntegerType, true),
    StructField("transit_stations_percent_change_from_baseline", IntegerType, true),
    StructField("workplaces_percent_change_from_baseline", IntegerType, true),
    StructField("residential_percent_change_from_baseline", IntegerType, true)
))

val dokumentDF = spark.read.format("csv").schema(dokumentSchema).option("header", true).load("datasett/datasett_dokument.csv")

dokumentDF.write.format("parquet").mode("errorIfExists").save("dokument.parquet")


### Kolonnefamilie
import org.apache.spark.sql.types._

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

val kolonnefamilieDF = spark.read.format("csv").schema(kolonnefamilieSchema).option("header", true).load("datasett/country_vaccinations.csv")

kolonnefamilieDF.write.format("parquet").mode("errorIfExists").save("kolonnefamilie.parquet")

### Key Value 
import org.apache.spark.sql.types._

val KeyValueSchema =
StructType(Seq(
    StructField("Date", DateType, true),
    StructField("Country/Region", StringType, true),
    StructField("Confirmed", IntegerType, true),
    StructField("Deaths", IntegerType, true),
    StructField("Recovered", IntegerType, true),
    StructField("Active", IntegerType, true),
    StructField("New cases", IntegerType, true),
    StructField("New deaths", IntegerType, true),
    StructField("New recovered", IntegerType, true),
    StructField("WHO Region", StringType, true)    
))

val KeyValueDF = spark.read.format("csv").option("header",true).schema(KeyValueSchema).load("datasett/keyValue.csv")


KeyValueDF.withColumnRenamed("New cases","NewCases").withColumnRenamed("New deaths","NewDeaths").withColumnRenamed("New recovered","NewRecovered").withColumnRenamed("WHO Region", "WHORegion").write.format("parquet").mode("errorIfExists").save("keyValue.parquet")

### Graph
Dette datasettet er delt inn i 2 deler, en del for nodene og en for relasjonene mellom nodene. For å gjøre filene om til parquet filer vil det si at det må gjøres 2 ganger, en gang per fil.

#### Noder
val graphNodesDF = spark.read.format("csv").option("header", false).load("datasett/graph_node.csv")

graphNodesDF.write.format("parquet").mode("errorIfExists").save("graphNodes.parquet")

#### Kanter
import org.apache.spark.sql.types._

val GraphSchema =
StructType(Seq(
    StructField("Callsign", StringType, true),
    StructField("Number", StringType, true),
    StructField("ICAO24", StringType, true),
    StructField("Registration", StringType, true),
    StructField("Typecode", StringType, true),
    StructField("Origin", StringType, true),
    StructField("Destination", StringType, true),
    StructField("FirstSeen", DateType, true),
    StructField("LastSeen", DateType, true),
    StructField("Day", DateType, true),StructField("Lat1", FloatType, true),
    StructField("Long1", FloatType, true),
    StructField("Att1", FloatType, true),
    StructField("Lat2", FloatType, true),
    StructField("Long2", FloatType, true),
    StructField("Att2", FloatType, true)
))

val graphRelationsDF = spark.read.format("csv").option("header", false).schema(GraphSchema).load("datasett/graph_relation.csv")

graphRelationsDF.write.format("parquet").mode("errorIfExists").save("graphRelations.parquet")

## Deloppgave 2
### Key Value
I dette datasettet skal det først bare brukes en CSV fil for å lese inn alle oppføringer. I tillegg skal det aggergeres på 


#### Opprette vanlig CSV fil
val keyValueDF = spark.read.format("parquet").option("header", true).load("keyValue.parquet")

keyValueDF.write.format("csv").mode("errorIfExists").save("out/keyValue.csv")

#### Aggregert CSV fil
Gruperer datasettet på region og dato. Ettersom det allerede er lest inn og lagt inn i spark trengs ikke det å gjøres igjen.

keyValueDF.groupBy("WHORegion", "Date").agg(
    sum("Confirmed"),
    sum("Deaths"),
    sum("Recovered"),
    sum("Active"),
    sum("NewCases"),
    sum("NewDeaths"),
    sum("NewRecovered")    
).write.format("csv").mode("errorIfExists").save("out/keyValueAggregated.csv")


### Dokument
I dette datasettet er det oppføringer på land med region, og datoer. Databasen skal ha aggregeringer for hver region.

val documentDF = spark.read.format("parquet").option("header", true).load("dokument.parquet")

documentDF.select(
    documentDF("country_region_code"),
    documentDF("country_region"),
    documentDF("sub_region_1"),
    documentDF("date"),
    documentDF("retail_and_recreation_percent_change_from_baseline"),
    documentDF("grocery_and_pharmacy_percent_change_from_baseline"),
    documentDF("parks_percent_change_from_baseline"),
    documentDF("transit_stations_percent_change_from_baseline"),
    documentDF("workplaces_percent_change_from_baseline"),
    documentDF("residential_percent_change_from_baseline")
).write.format("csv").save("out/document.csv")


### Kolonnefamilie
val columnDF = spark.read.format("parquet").option("header", true).load("kolonnefamilie.parquet")

columnDF.write.format("csv").save("out/kolonnefamilie.csv")


### Graph
Her er det som tidligere nevnt to datasett. Det kunne strengt tatt bare vært et datasett, og så bare valgt raden med land. Derimot tenker jeg at når man først har begge filene så er det like greit å gjøre det som er lettest for maskinen.

#### Nodene
val graphNodesDF = spark.read.format("parquet").option("header", false).load("graphNodes.parquet")

graphNodesDF.write.format("csv").save("out/graphNodes.csv")

#### Relasjonene
val graphRelationsDF = spark.read.format("parquet").option("header", false).load("graphRelations.parquet")

graphRelationsDF.write.format("csv").save("out/graphRelations.csv")


## Deloppgave 3
Legger sammen alle datasetta, med samme navn og dato. Dermed kunne mam ha lagt sammen alle datasetta til et datasett, men det vil da bli en ganske mye større fil.

import org.apache.spark.sql.types._

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

val KeyValueSchema =
StructType(Seq(
    StructField("Date", DateType, true),
    StructField("Country/Region", StringType, true),
    StructField("Confirmed", IntegerType, true),
    StructField("Deaths", IntegerType, true),
    StructField("Recovered", IntegerType, true),
    StructField("Active", IntegerType, true),
    StructField("New cases", IntegerType, true),
    StructField("New deaths", IntegerType, true),
    StructField("New recovered", IntegerType, true),
    StructField("WHO Region", StringType, true)    
))


val dokumentSchema = 
StructType(Seq(
    StructField("country_region_code", StringType, true),
    StructField("country_region", StringType, true),
    StructField("sub_region_1", StringType, true),
    StructField("sub_region_2", StringType, true),
    StructField("metro_area", StringType, true),
    StructField("iso_3166_2_code", StringType, true),
    StructField("census_fips_code", IntegerType, true),
    StructField("place_id", StringType, true),
    StructField("date", DateType, true),
    StructField("retail_and_recreation_percent_change_from_baseline", IntegerType, true),
    StructField("grocery_and_pharmacy_percent_change_from_baseline", IntegerType, true),
    StructField("parks_percent_change_from_baseline", IntegerType, true),
    StructField("transit_stations_percent_change_from_baseline", IntegerType, true),
    StructField("workplaces_percent_change_from_baseline", IntegerType, true),
    StructField("residential_percent_change_from_baseline", IntegerType, true)
))


val dokumentDF = spark.read.format("csv").schema(dokumentSchema).option("header", true).load("datasett/datasett_dokument.csv").dropDuplicates("date","country_region")

val KeyValueDF = spark.read.format("csv").option("header",true).schema(KeyValueSchema).load("datasett/keyValue.csv")

val kolonnefamilieDF = spark.read.format("csv").schema(kolonnefamilieSchema).option("header", true).load("datasett/country_vaccinations.csv")



dokumentDF.join(KeyValueDF, dokumentDF("country_region") === KeyValueDF("Country/Region") && dokumentDF("date") === KeyValueDF("Date")).join(kolonnefamilieDF, dokumentDF("country_region") === kolonnefamilieDF("country") && dokumentDF("date") === kolonnefamilieDF("date")).show