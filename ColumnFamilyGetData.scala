import org.apache.spark.sql.SparkSession

object ColumnFamilyGetData {
    def main(args: Array[String]) {
        val spark = SparkSession.builder
            .config("spark.master", "local")
            .appName("ColumnFamilyInsertData")
            .getOrCreate()


        val df = spark.read.format("org.apache.spark.sql.cassandra").option("table", "vaccine").option("keyspace", "kode").load

        // TODO: Kan ikke kompileres
        // Bytter ut land og dato med ønsket verdi
        df.select("*").where($"country" === "Norway" && $"date" === "2020-12-05").show

    }
}