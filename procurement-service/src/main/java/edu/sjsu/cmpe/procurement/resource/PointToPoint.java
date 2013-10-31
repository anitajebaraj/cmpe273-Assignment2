package edu.sjsu.cmpe.procurement.resource;

import java.io.IOException;
import java.util.ArrayList;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;
import org.fusesource.stomp.jms.message.StompJmsMessage;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import edu.sjsu.cmpe.procurement.config.ProcurementServiceConfiguration;
import edu.sjsu.cmpe.procurement.domain.Book;

public class PointToPoint {
	Book book=new Book();
	public void getMessagesFromQueue() throws JMSException
	{
	
		ProcurementServiceConfiguration configuration=new ProcurementServiceConfiguration();
		String queueName = configuration.getStompQueueName();
		
		ArrayList orderIsbnList=new ArrayList();
		book.setOrderIsbnList(orderIsbnList);
		String user = env("APOLLO_USER", "admin");
		String password = env("APOLLO_PASSWORD", "password");
		String host = env("APOLLO_HOST", "54.215.210.214");
		int port = Integer.parseInt(env("APOLLO_PORT", "61613"));
		

		StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
		factory.setBrokerURI("tcp://" + host + ":" + port);
		Connection connection=factory.createConnection(user, password);
		
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination dest = new StompJmsDestination(queueName);

		MessageConsumer consumer = session.createConsumer(dest);
		System.out.println("Waiting for messages from " + queueName + "...");
		
		Message msg;
		long waitUntil = 5000; // wait for 5 sec
		while((msg = consumer.receive(waitUntil))!=null) {
		   // Message msg = consumer.receive(waitUntil);
		    System.out.println("msg=="+msg);		 
		    if( msg instanceof  TextMessage ) 
		    {
		    	String body = ((TextMessage) msg).getText();	
		    	System.out.println("Received message in text message = " + body);
		    	String[] queueValues=body.split(":");
		    	System.out.println("queueValues[1]=="+queueValues[1]);
		    	orderIsbnList.add(queueValues[1]);
		    	book.setOrderIsbnList(orderIsbnList);
		    	System.out.println("orderIsbnList=="+orderIsbnList);
		    } 
		    else {
		         System.out.println("Unexpected message type: " + msg.getClass());
		    }
		   
		    
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
	  
	   //Jersey Client
	   public void orderBooks() 
		{
			 Client client = Client.create();  
	         WebResource webResource = client.resource("http://54.215.210.214:9000/orders");  
	         String input = "{\"id\":\"05322\",\"order_book_isbns\":"+book.getOrderIsbnList()+"}";
	         System.out.println("input=="+input);
	         ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, input);
	           
	         System.out.println("Response " + response.getEntity(String.class));
	
		}
}
	  

