object GraphGetData extends App {
    // Eksempeldata, endres avhengig av hva brukeren skal ha data for
    val country = "Norway"
    val date = "2020-04-01 00:00:00+00:00"

    val query = "MATCH (country{country:" + country + "}) -[:FLIGHT]-> (destination) RETURN destination"

    val countries = spark.read
        .format("org.neo4j.spark.DataSource")
        .option("url", "bolt://localhost:7687")
        .option("authentication.basic.username", "neo4j")
        .option("authentication.basic.password", "student")
        .option("query", query)
        .load()

    countries.show()
}