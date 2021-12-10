# Oppgave 2
Ettersom 3 av datasetta brukes til mer eller mindre det samme valgte jeg å slå de sammen. På den måten slipper nettsiden å hente data fra 3 datakilder, men får samme data fra 1 kilde. Det gjør det lettere å lage nettsiden ettersom det blir mindre kompleksitet.

I tillegg har jeg lagt til en join mellom cassandra og neo4j, ettersom det er sammenhengen mellom reise og utviklingen av smitte og vaksinering som har vært interessant for nettsiden.

## Alle datasett
### Teori
Denne koden skal bare kjøres en gang, for å legge data inn i en database. På grunn av det vurderer jeg det til at man kan gjøre spørringen en del "tyngre". Det som gjør spørringen tung er at det er forholdsvis store datasett, og ingen av dem er små nok til å kunne flyttes mellom noder. Dermed vil det bli mye data som må sammenlignes mellom ulike noder, som krever en del trafikk mellom noder samt prosessering i nodene.

Datasetta er omtrent like store, så det har i utgangspunktet ikke noe særlig å si hvilket datasett som brukes til å joine de andre. Datasettet om bevegelsesmønster fra Google(dokumentdatabasen) er litt større enn de andre, siden det har flere nivåer enn bare land. De dypere nivåene blir derimot fjernet, så når datasetta er lest inn vil de være noen lunde tilsvarende.

Dette er som nevnt en engangs handling, ettersom at tanken er at dataen skal legges inn i en database. Grunnen til at man kan dette er at datasetta består av "historiske" data, altså data som i utgangspunktet ikke endres.


### Kode
import org.apache.spark.sql.types._
import org.apache.spark.sql.SparkSession


object JoinAllData extends App {
    
    val spark = SparkSession.builder
            .config("spark.master", "local")
            .appName("ColumnFamilyInsertData")
            .getOrCreate()


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

    // Kan gjøres samtidig som databasene ellers blir fylt med data. På den måten slipper man å lese filene flere ganger. Blir derimot en tyngree prosess, som gjør at det tar lengre tid.
    // Fordelen med dette er at alt av data blir lag
    val joinDF = 
        dokumentDF
            .join(KeyValueDF, dokumentDF("country_region") === KeyValueDF("Country/Region")
                && dokumentDF("date") === KeyValueDF("Date"))
            .join(kolonnefamilieDF, dokumentDF("country_region") === kolonnefamilieDF("country") 
              && dokumentDF("date") === kolonnefamilieDF("date"))
            .show
}

## Cassandra og neo4j

### Teori
Cassandra databasen har data om vaksinering på landsbasis, mens neo4j har data om flyreiser mellom ulike land. I neo4j er hvert land definert som en node, mens hver flytur er definert som en relasjon mellom 2 noder.

Ettersom flyreise datasettet er veldig stort er det ikke hensiktsmessig å ha det lagret flere steder. Derfor er dette kode som skal kjøres hver gang et resultat trengs, imotsettning til over. Derfor må dette være kode som er lettere å kjøre.

For å få til dette må det begrenses hva som hentes ut fra databasene. Fra neo4j gjøres det med å bare liste ut land et fly har flyd til fra et valgt land på en valgt dato. Det vil da bli en forholdsvis liten liste i denne sammenhengen.

Fra cassandra blir det hentet ut status for vaksinering i et valgt land på en valgt dato. Dette blir da slått sammen med neo4j resultatet, og kunne eventuelt blitt brukt videre til å hente vaksinasjon i de landene det blei reist til.

### Kode
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