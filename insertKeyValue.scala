

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


case class CountryEntery(date: String, country: String, confirmed: Int, deaths: Int, recovered: Int, active: Int, newCases: Int, newDeaths: Int, newRecoveries: Int, region: String)
case class RegionEntery(date: String, region: String, var confirmed: Int, var deaths: Int, var recovered: Int, var active: Int, var newCases: Int, var newDeaths: Int, var newRecoveries: Int)

object insertKeyValue extends App {
	val source = Source.fromFile("../datasett/KeyValue.csv")
	
//	def parseInt(value : String) : Option[Int] = if (value =="") None else Some(value.toInt)

	// Samleobjekter for de aggregerte verdiene
	var date = "0"

	var EM = new RegionEntery("0","0",0,0,0,0,0,0,0);
	var eu = new RegionEntery("0","0",0,0,0,0,0,0,0);
	var africa = new RegionEntery("0","0",0,0,0,0,0,0,0);
	var america = new RegionEntery("0","0",0,0,0,0,0,0,0);
	var SEA = new RegionEntery("0","0",0,0,0,0,0,0,0);
	var wp = new RegionEntery("0","0",0,0,0,0,0,0,0);

	
	source.getLines.drop(1).foreach { row =>
	
		val line = row.split(",").map(_.trim);
				
		val tempDate = line{0}
		val tempKey = tempDate + ":" + line{1}.replaceAll("\\s", "")
		val tempCountry = new CountryEntery(tempDate, line{1}.replaceAll("\\s", ""), line{2}.toInt, line{3}.toInt, line{4}.toInt, line{5}.toInt, line{6}.toInt, line{7}.toInt, line{8}.toInt, line{9});
		
		// Ferdig med å lage objektet for landet
		println(tempCountry);
		
		
		// Lager de aggregerte objektene
		if (date == line{0}){
			line{9} match {
				case "Eastern Mediterranean" => {
					EM.confirmed += line{2}.toInt;
					EM.deaths += line{3}.toInt;
					EM.recovered += line{4}.toInt;
					EM.active += line{5}.toInt;
					EM.newCases += line{6}.toInt;
					EM.newDeaths += line{7}.toInt;
					EM.newRecoveries += line{8}.toInt;
				}
				case "Europe" =>  {
					eu.confirmed += line{2}.toInt;
					eu.deaths += line{3}.toInt;
					eu.recovered += line{4}.toInt;
					eu.active += line{5}.toInt;
					eu.newCases += line{6}.toInt;
					eu.newDeaths += line{7}.toInt;
					eu.newRecoveries += line{8}.toInt;
				}
				case "Africa" =>  {
					africa.confirmed += line{2}.toInt;
					africa.deaths += line{3}.toInt;
					africa.recovered += line{4}.toInt;
					africa.active += line{5}.toInt;
					africa.newCases += line{6}.toInt;
					africa.newDeaths += line{7}.toInt;
					africa.newRecoveries += line{8}.toInt;
				}
				case "Americas" =>  {
					america.confirmed += line{2}.toInt;
					america.deaths += line{3}.toInt;
					america.recovered += line{4}.toInt;
					america.active += line{5}.toInt;
					america.newCases += line{6}.toInt;
					america.newDeaths += line{7}.toInt;
					america.newRecoveries += line{8}.toInt;
				}
				case "Western Pacific" =>  {
					wp.confirmed += line{2}.toInt;
					wp.deaths += line{3}.toInt;
					wp.recovered += line{4}.toInt;
					wp.active += line{5}.toInt;
					wp.newCases += line{6}.toInt;
					wp.newDeaths += line{7}.toInt;
					wp.newRecoveries += line{8}.toInt;
				}
				case "South-East Asia" =>  {
					SEA.confirmed += line{2}.toInt;
					SEA.deaths += line{3}.toInt;
					SEA.recovered += line{4}.toInt;
					SEA.active += line{5}.toInt;
					SEA.newCases += line{6}.toInt;
					SEA.newDeaths += line{7}.toInt;
					SEA.newRecoveries += line{8}.toInt;
				}
			}
		} else {
		
			val emAsJson = new Gson().toJson(EM)
			val euAsJson = new Gson().toJson(eu)
			val africaAsJson = new Gson().toJson(africa)
			val americaAsJson = new Gson().toJson(america)
			val SEAAsJson = new Gson().toJson(SEA)
			val wpAsJson = new Gson().toJson(wp)
			
			
			
			val EMpost = new HttpPost("http://127.0.0.1:2379/v2/keys/" + date + ":" + "Eastern_Mediterranean")
			val EMnameValuePairs = new ArrayList[NameValuePair]()
			EMnameValuePairs.add(new BasicNameValuePair("value", emAsJson))
			EMpost.setEntity(new UrlEncodedFormEntity(EMnameValuePairs))

			val EMclient = new DefaultHttpClient
			val EMresponse = EMclient.execute(EMpost)
			println("--- HEADERS ---")
			EMresponse.getAllHeaders.foreach(arg => println(arg))
			
			
			val EUpost = new HttpPost("http://127.0.0.1:2379/v2/keys/" + date + ":" + "Europe")
			val EUnameValuePairs = new ArrayList[NameValuePair]()
			EUnameValuePairs.add(new BasicNameValuePair("value", euAsJson))
			EUpost.setEntity(new UrlEncodedFormEntity(EUnameValuePairs))

			val EUclient = new DefaultHttpClient
			val EUresponse = EUclient.execute(EUpost)
			println("--- HEADERS ---")
			EUresponse.getAllHeaders.foreach(arg => println(arg))
			
			
			val AFpost = new HttpPost("http://127.0.0.1:2379/v2/keys/" + date + ":" + "Africa")
			val AFnameValuePairs = new ArrayList[NameValuePair]()
			AFnameValuePairs.add(new BasicNameValuePair("value", africaAsJson))
			AFpost.setEntity(new UrlEncodedFormEntity(AFnameValuePairs))

			val AFclient = new DefaultHttpClient
			val AFresponse = AFclient.execute(AFpost)
			println("--- HEADERS ---")
			AFresponse.getAllHeaders.foreach(arg => println(arg))
			
			
			
			val AMpost = new HttpPost("http://127.0.0.1:2379/v2/keys/" + date + ":" + "Americas")
			val AMnameValuePairs = new ArrayList[NameValuePair]()
			AMnameValuePairs.add(new BasicNameValuePair("value", americaAsJson))
			AMpost.setEntity(new UrlEncodedFormEntity(AMnameValuePairs))

			val AMclient = new DefaultHttpClient
			val AMresponse = AMclient.execute(AMpost)
			println("--- HEADERS ---")
			AMresponse.getAllHeaders.foreach(arg => println(arg))
			
			
			
			val WPpost = new HttpPost("http://127.0.0.1:2379/v2/keys/" + date + ":" + "Western_Pacific")
			val WPnameValuePairs = new ArrayList[NameValuePair]()
			WPnameValuePairs.add(new BasicNameValuePair("value", wpAsJson))
			WPpost.setEntity(new UrlEncodedFormEntity(WPnameValuePairs))

			val WPclient = new DefaultHttpClient
			val WPresponse = WPclient.execute(WPpost)
			println("--- HEADERS ---")
			WPresponse.getAllHeaders.foreach(arg => println(arg))
			
			
			
			val SEApost = new HttpPost("http://127.0.0.1:2379/v2/keys/" + date + ":" + "South-East_Asia")
			val SEAnameValuePairs = new ArrayList[NameValuePair]()
			SEAnameValuePairs.add(new BasicNameValuePair("value", SEAAsJson))
			SEApost.setEntity(new UrlEncodedFormEntity(SEAnameValuePairs))

			val SEAclient = new DefaultHttpClient
			val SEAresponse = SEAclient.execute(SEApost)
			println("--- HEADERS ---")
			SEAresponse.getAllHeaders.foreach(arg => println(arg))
			
			
			
			
			date = line{0}
			EM = new RegionEntery(line{0},"Eastern Mediterranean",0,0,0,0,0,0,0);
			eu = new RegionEntery(line{0},"Europe",0,0,0,0,0,0,0);
			africa = new RegionEntery(line{0},"Africa",0,0,0,0,0,0,0);			
			america = new RegionEntery(line{0},"Americas",0,0,0,0,0,0,0);
			wp = new RegionEntery(line{0},"Western Pacific",0,0,0,0,0,0,0);
			SEA = new RegionEntery(line{0},"South-East Asia",0,0,0,0,0,0,0);
			
			
			line{9} match {
				case "Eastern Mediterranean" => {
					EM.confirmed += line{2}.toInt;
					EM.deaths += line{3}.toInt;
					EM.recovered += line{4}.toInt;
					EM.active += line{5}.toInt;
					EM.newCases += line{6}.toInt;
					EM.newDeaths += line{7}.toInt;
					EM.newRecoveries += line{8}.toInt;
				}
				case "Europe" =>  {
					eu.confirmed += line{2}.toInt;
					eu.deaths += line{3}.toInt;
					eu.recovered += line{4}.toInt;
					eu.active += line{5}.toInt;
					eu.newCases += line{6}.toInt;
					eu.newDeaths += line{7}.toInt;
					eu.newRecoveries += line{8}.toInt;
				}
				case "Africa" =>  {
					africa.confirmed += line{2}.toInt;
					africa.deaths += line{3}.toInt;
					africa.recovered += line{4}.toInt;
					africa.active += line{5}.toInt;
					africa.newCases += line{6}.toInt;
					africa.newDeaths += line{7}.toInt;
					africa.newRecoveries += line{8}.toInt;
				}
				case "Americas" =>  {
					america.confirmed += line{2}.toInt;
					america.deaths += line{3}.toInt;
					america.recovered += line{4}.toInt;
					america.active += line{5}.toInt;
					america.newCases += line{6}.toInt;
					america.newDeaths += line{7}.toInt;
					america.newRecoveries += line{8}.toInt;
				}
				case "Western Pacific" =>  {
					wp.confirmed += line{2}.toInt;
					wp.deaths += line{3}.toInt;
					wp.recovered += line{4}.toInt;
					wp.active += line{5}.toInt;
					wp.newCases += line{6}.toInt;
					wp.newDeaths += line{7}.toInt;
					wp.newRecoveries += line{8}.toInt;
				}
				case "South-East Asia" =>  {
					SEA.confirmed += line{2}.toInt;
					SEA.deaths += line{3}.toInt;
					SEA.recovered += line{4}.toInt;
					SEA.active += line{5}.toInt;
					SEA.newCases += line{6}.toInt;
					SEA.newDeaths += line{7}.toInt;
					SEA.newRecoveries += line{8}.toInt;
				}
				
			}
		}
		
		val countryAsJson = new Gson().toJson(tempCountry)
		
		
		val post = new HttpPost("http://127.0.0.1:2379/v2/keys/" + tempKey)
		val nameValuePairs = new ArrayList[NameValuePair]()
		nameValuePairs.add(new BasicNameValuePair("value", countryAsJson))
		post.setEntity(new UrlEncodedFormEntity(nameValuePairs))


		val client = new DefaultHttpClient
		val response = client.execute(post)
		println("--- HEADERS ---")
		response.getAllHeaders.foreach(arg => println(arg))
			
	}
	println(EM);
}
