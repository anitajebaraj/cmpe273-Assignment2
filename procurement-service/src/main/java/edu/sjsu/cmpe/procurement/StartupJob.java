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


@Every("1mn")
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
	Client client = Client.create(); 
	//Http POST
	
    WebResource webResourceForPost = client.resource("http://54.215.210.214:9000/orders");  
    String input = "{\"id\":\"05322\",\"order_book_isbns\":"+book.getOrderIsbnList()+"}";
    System.out.println("input=="+input);
    ClientResponse responseFromPost = webResourceForPost.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, input);
      
    System.out.println("Response " + responseFromPost.getEntity(String.class));

    String responseMsg=responseFromPost.getEntity(String.class);
  
  
   // HTTP GET
    //if(book.getOrderIsbnList().get(0)==null)
	 
	WebResource webResourceForGet = client
	   .resource("http://54.215.210.214:9000/orders/05322");

	ClientResponse responseFromGet = webResourceForGet.accept("application/json")
               .get(ClientResponse.class);

	if (responseFromGet.getStatus() != 200) {
	   throw new RuntimeException("Failed : HTTP error code : "
		+ responseFromGet.getStatus());
	}

	String output = responseFromGet.getEntity(String.class);
  
	System.out.println("Output from Server .... \n");
	System.out.println(output);
	
	book.setResponseFromGet(output);
	//logic for publishing the output from server to library
	
	PublishService pubSub=new PublishService();
	//parsing output
	//replace publish with parsing the output and use pubishservice in parser
	try {
		pubSub.parseOutputFromServer(output);
	} catch (JsonProcessingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (JMSException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
}
		else
			System.out.println("no messages in queue....wait");
   
}
}

