package edu.sjsu.cmpe.procurement;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashSet;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.ws.rs.core.MediaType;

import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;
import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import de.spinscale.dropwizard.jobs.Job;
import de.spinscale.dropwizard.jobs.annotations.Every;
import edu.sjsu.cmpe.procurement.config.ProcurementServiceConfiguration;
import edu.sjsu.cmpe.procurement.domain.Book;
import edu.sjsu.cmpe.procurement.resource.PointToPoint;
import edu.sjsu.cmpe.procurement.resource.PublishService;


@Every("5mn")
public class StartupJob extends Job
{
	
	
public StartupJob() {
		
	}

@Override
public void doJob(){
	Book book=new Book();
	// General logic for getting Jobs from queue
	PointToPoint msgQueue=new PointToPoint();
	
			try {
				msgQueue.getMessagesFromQueue();
			} catch (JMSException e) {
				
				e.printStackTrace();
			}
			
			
			
			
		if(!book.getOrderIsbnList().isEmpty())
		{
			System.out.println("before POST");
			try{
			//Http POST
			
			final Client clientPost = Client.create(); 
		    WebResource webResourceForPost = clientPost.resource("http://54.215.210.214:9000/orders");  
		    String input = "{\"id\":\"05322\",\"order_book_isbns\":"+book.getOrderIsbnList()+"}";
		    System.out.println("input=="+input);
		    ClientResponse responseFromPost = webResourceForPost.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, input);
		    String responseMsg=responseFromPost.getEntity(String.class);	
		    System.out.println("responseMsg===="+responseMsg);
		   
			}
			catch(Exception e)
			{
				System.out.println("in exception e");
				e.printStackTrace();
			}
			
			
   // HTTP GET
			try
			{
			final Client clientGet = Client.create(); 
			WebResource webResourceForGet = clientGet.resource("http://54.215.210.214:9000/orders/05322");
			ClientResponse responseFromGet = webResourceForGet.accept("application/json").get(ClientResponse.class);
			if (responseFromGet.getStatus() != 200) {
			   throw new RuntimeException("Failed : HTTP error code : "+ responseFromGet.getStatus());
			}
		 
			String output = responseFromGet.getEntity(String.class);
			System.out.println("Output from Server .... \n");
			System.out.println(output);
			
			book.setResponseFromGet(output);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
	
	
	//logic for publishing the output from server to library
    System.out.println("before parse");
	PublishService pubSub=new PublishService();
	System.out.println("before parse");
	//parsing output
	try {
		System.out.println("on parse");
		pubSub.parseOutputFromServer(book.getResponseFromGet());
	} catch (JsonProcessingException e) {
		System.out.println("JsonProcessingException in parser");
		e.printStackTrace();
	} catch (IOException e) {
		System.out.println("IO Exception in parser");
		e.printStackTrace();
	} catch (ParseException e) {
		
		e.printStackTrace();
	} catch (JMSException e) {
		
		e.printStackTrace();
	}
		
}
		else
			System.out.println("no messages in queue....wait");
   
}
}


