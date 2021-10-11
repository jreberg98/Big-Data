
import scala.Console
import java.io._
import org.apache.commons._
import org.apache.http._
import org.apache.http.client._
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import java.util.ArrayList
import org.apache.http.message.BasicNameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import com.google.gson.Gson
import scala.io.Source

case class CountryEnteryAdd(date: String, country: String, confirmed: Int, deaths: Int, recovered: Int, active: Int, 
    newCases: Int, newDeaths: Int, newRecoveries: Int, region: String)

object KeyValueAddData extends App {

    println("HVilken dato gjelder dataen for?")
    val date = Console.readLine()
    
    println("Hvem land lager du inn data for?")
    val country = Console.readLine()

    println("Hvilken region ligger " + country + " i?")
    val region = Console.readLine()

    println("Antall smitta totalt?")
    val confirmed = Console.readInt()

    println("Antall døde totalt?")
    val deaths  = Console.readInt()

    println("Antall friske totalt?")
    val recovered = Console.readInt()

    println("Antall nåværende smittede?")
    val active = Console.readInt()

    println("Antall nye smitta idag?")
    val newCases = Console.readInt()

    println("Antall døde idag?")
    val newDeaths = Console.readInt()

    println("Antall friske idag?")
    val newRecoveries = Console.readInt()


    val countryEntery = new CountryEntery(date, country, confirmed, deaths, recovered, active, newCases, newDeaths, newRecoveries, region)
    val key = date + ":" + country

    val countryAsJson = new Gson().toJson(countryEntery)
		
		val post = new HttpPost("http://127.0.0.1:2379/v2/keys/" + key)
		val nameValuePairs = new ArrayList[NameValuePair]()
		nameValuePairs.add(new BasicNameValuePair("value", countryAsJson))
		post.setEntity(new UrlEncodedFormEntity(nameValuePairs))


		val client = new DefaultHttpClient
		val response = client.execute(post)
		println("--- HEADERS ---")
		response.getAllHeaders.foreach(arg => println(arg))



    
    val url = "http://127.0.0.1:2379/v2/keys/" + key
    val result = scala.io.Source.fromURL(url).mkString
    
    //println(result);
}