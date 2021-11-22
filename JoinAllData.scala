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