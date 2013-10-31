package edu.sjsu.cmpe.procurement.resource;

import java.io.IOException;
import java.text.ParseException;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.eclipse.jetty.util.ajax.JSONObjectConvertor;
import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.jersey.api.client.ClientResponse;

import edu.sjsu.cmpe.procurement.config.ProcurementServiceConfiguration;
import edu.sjsu.cmpe.procurement.domain.Book;

public class PublishService {
public void publishServiceToLibrary(String data,String category) throws JMSException
{
	ProcurementServiceConfiguration configuration=new ProcurementServiceConfiguration();
	
	String topicName = configuration.getStompTopicName()+category;
	System.out.println("topicName=="+topicName);
	System.out.println("getting in");
	String user = env("APOLLO_USER", "admin");
	String password = env("APOLLO_PASSWORD", "password");
	String host = env("APOLLO_HOST", "54.215.210.214");
	int port = Integer.parseInt(env("APOLLO_PORT", "61613"));
	StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
	factory.setBrokerURI("tcp://" + host + ":" + port);
	Connection connection = factory.createConnection(user, password);
	connection.start();
	Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	Destination dest = new StompJmsDestination(topicName);
	MessageProducer producer = session.createProducer(dest);
	producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);	
	if(data=="SHUTDOWN")
	{
		System.out.println("sending shutdown msg");
		producer.send(session.createTextMessage("SHUTDOWN"));
		
	}
	else
	{
		TextMessage msg = session.createTextMessage(data);
		msg.setLongProperty("id", System.currentTimeMillis());
		producer.send(msg);
		System.out.println("successfully published");
	}
	
	
	connection.close();
	
	
}
private static String env(String key, String defaultValue) {
	String rc = System.getenv(key);
	if( rc== null ) {
	    return defaultValue;
	}
	return rc;
   }
public void parseOutputFromServer(String output) throws JsonProcessingException, IOException, ParseException, JMSException
{
	JSONObject jsnobject = new JSONObject(output);
	System.out.println("hello checking");
	 JSONArray jArray = jsnobject.getJSONArray("shipped_books");
	 for(int i=0; i < jArray.length(); i++)
	 {
		 System.out.println("jarray length==="+jArray.length());
		  JSONObject jObject = jArray.getJSONObject(i);
		  String category=jObject.getString("category");
		  System.out.println("category======"+category);
		  String coverimage=jObject.getString("coverimage");
		  String isbn=jObject.getString("isbn");
		  String title=jObject.getString("title");
		  String msgToPublish=isbn+":"+title+":"+category+":"+coverimage;
		  System.out.println("msgToPublish=="+msgToPublish);
		  publishServiceToLibrary(msgToPublish,category.toLowerCase());				
	 }
	 publishServiceToLibrary("SHUTDOWN","shutdownMsg");
}

}