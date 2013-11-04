package edu.sjsu.cmpe.library.api.resources;

import java.net.MalformedURLException;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;

import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;

import de.spinscale.dropwizard.jobs.Job;
import de.spinscale.dropwizard.jobs.annotations.Every;
import edu.sjsu.cmpe.library.repository.BookRepository;
import edu.sjsu.cmpe.library.repository.BookRepositoryInterface;

@Every("5mn")
public class subscribeResource extends Job {
	
	public subscribeResource()
	{
		
	}

	@Override
	public void doJob() {
		ConnectionResource connection= new ConnectionResource();
		try {
			connection.subscribeToBookOrders();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
}
