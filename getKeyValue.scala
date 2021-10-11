import java.io._
import org.apache.commons._
import org.apache.http._
import org.apache.http.client._
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import java.util.ArrayList
import org.apache.http.message.BasicNameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import com.google.gson.Gson
import scala.Console


case class NodeResponse(key : String, value: String)
case class Node(key : String, value: String, nodes: Array[NodeResponse])
case class Message(action: String, node: Node)

case class Response(date: String, country: String, region: String, var confirmed: Int, var deaths: Int, var recovered: Int, var active: Int, var newCases: Int, var newDeaths: Int, var newRecoveries: Int)


object KeyValueGetData extends App {

    println("Hvem dato vil du ha data om?")
    val date = Console.readLine()

    println("Hvem land/region vil du ha data om?")
    val country = Console.readLine()

    val key = date + ":" + country

    println(key)

    val url = "http://127.0.0.1:2379/v2/keys/" + key
    val result = scala.io.Source.fromURL(url).mkString
    
    //println(result);
    
    val messageParsed = new Gson().fromJson( result, classOf[Message] );
    val valueParsed = new Gson().fromJson( messageParsed.node.nodes(0).value, classOf[Response] );
    
    println("Dato: " + valueParsed.date);
    println("Land: " + valueParsed.country);
    println("# smitta: " + valueParsed.confirmed);
    println("# døde: " + valueParsed.deaths);
    println("# friske: " + valueParsed.recovered);
    println("# aktivt smitta: " + valueParsed.active);
    println("# nye smitta: " + valueParsed.newCases);
    println("# nye døde: " + valueParsed.newDeaths);
    println("# nye friske: " + valueParsed.newRecoveries);

}
