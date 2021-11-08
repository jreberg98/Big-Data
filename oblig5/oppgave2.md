# Oppgave 2
## Forberedelser
start-dfs.sh for å starte hadoop
import org.apache.spark.sql.types._ i scala for å importere datatyper til schema
## Deloppgave 1
hadoop fs -copyFromLocal keyValue.parquet /keyValue.parquet

hadoop fs -copyFromLocal dokument.parquet /dokument.parquet

hadoop fs -copyFromLocal graphNodes.parquet /graphNodes.parquet

hadoop fs -copyFromLocal graphRelations.parquet /graphRelations.parquet

hadoop fs -copyFromLocal kolonnefamilie.parquet /kolonnefamilie.parquet



## Deloppgave 2
### Key Value
val keyValueDF = spark.read.format("parquet").option("header", true).load("hdfs://localhost:9000/keyValue.parquet")

keyValueDF.write.format("csv").mode("errorIfExists").save("out/keyValue.csv")

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
val documentDF = spark.read.format("parquet").option("header", true).load("hdfs://localhost:9000/dokument.parquet")

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
val columnDF = spark.read.format("parquet").option("header", true).load("hdfs://localhost:9000/kolonnefamilie.parquet")

columnDF.write.format("csv").save("out/kolonnefamilie.csv")

### Graph
#### Nodene
val graphNodesDF = spark.read.format("parquet").option("header", false).load("hdfs://localhost:9000/graphNodes.parquet")

graphNodesDF.write.format("csv").save("out/graphNodes.csv")

#### Relasjonene
val graphRelationsDF = spark.read.format("parquet").option("header", false).load("hdfs://localhost:9000/graphRelations.parquet")

graphRelationsDF.write.format("csv").save("out/graphRelations.csv")

## Deloppgave 3
