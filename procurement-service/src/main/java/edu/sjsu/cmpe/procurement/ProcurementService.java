package edu.sjsu.cmpe.procurement;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;
import org.fusesource.stomp.jms.message.StompJmsMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

import de.spinscale.dropwizard.jobs.Job;
import de.spinscale.dropwizard.jobs.JobsBundle;
import de.spinscale.dropwizard.jobs.annotations.OnApplicationStart;
import edu.sjsu.cmpe.procurement.config.ProcurementServiceConfiguration;
import edu.sjsu.cmpe.procurement.resource.PointToPoint;


public class ProcurementService extends Service<ProcurementServiceConfiguration> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) throws Exception {
	new ProcurementService().run(args);
    }

    @Override
    public void initialize(Bootstrap<ProcurementServiceConfiguration> bootstrap) {
	bootstrap.setName("procurement-service");
	bootstrap.addBundle(new JobsBundle("edu.sjsu.cmpe.procurement"));
    }

    @Override
    public void run(ProcurementServiceConfiguration configuration,
	    Environment environment) throws Exception {
	//String queueName = configuration.getStompQueueName();
	//String topicName = configuration.getStompTopicName();
	//log.debug("Queue name is {}. Topic is {}", queueName, topicName);
	
	// TODO: Apollo STOMP Broker URL and login
	
	//PointToPoint msgQueue=new PointToPoint();
	//msgQueue.getMessagesFromQueue();

    }
   
}
