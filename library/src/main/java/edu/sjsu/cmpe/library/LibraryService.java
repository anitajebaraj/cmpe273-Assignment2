package edu.sjsu.cmpe.library;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.views.ViewBundle;

import de.spinscale.dropwizard.jobs.JobsBundle;
import edu.sjsu.cmpe.library.api.resources.BookResource;
import edu.sjsu.cmpe.library.api.resources.RootResource;
import edu.sjsu.cmpe.library.config.LibraryServiceConfiguration;
import edu.sjsu.cmpe.library.repository.BookRepository;
import edu.sjsu.cmpe.library.repository.BookRepositoryInterface;
import edu.sjsu.cmpe.library.ui.resources.HomeResource;

public class LibraryService extends Service<LibraryServiceConfiguration> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) throws Exception {
	new LibraryService().run(args);
    }

    @Override
    public void initialize(Bootstrap<LibraryServiceConfiguration> bootstrap) {
	bootstrap.setName("library-service");
	bootstrap.addBundle(new ViewBundle());

	bootstrap.addBundle(new AssetsBundle());
	bootstrap.addBundle(new JobsBundle("edu.sjsu.cmpe.library.api.resources"));
    }

    @Override
    public void run(LibraryServiceConfiguration configuration,
	    Environment environment) throws Exception {	
    	
	// This is how you pull the configurations from library_x_config.yml
	String queueName = configuration.getStompQueueName();
	String topicName = configuration.getStompTopicName();
	String libraryName= configuration.getLibraryName();
	String apolloUser= configuration.getApolloUser();
	String passwordName= configuration.getApolloPassword();
	String hostName =  configuration.getApolloHost();
	String portName= configuration.getApolloPort();
	
	
	
	log.debug("Queue name is {}. Topic name is {}", queueName,
		topicName);
	log.debug("libraryName:"+libraryName);
	log.debug("apolloUser:"+apolloUser);
	log.debug("passwordName:"+passwordName);
	log.debug("hostName:"+hostName);
	log.debug("portName:"+portName);
	//log.debug("user=="+user+"  pwd==="+password+"  host=="+host+"  port==="+port);

	/** Root API */
	environment.addResource(RootResource.class);
	/** Books APIs */
	BookRepositoryInterface bookRepository = new BookRepository();
	environment.addResource(new BookResource(bookRepository));

	/** UI Resources */
	environment.addResource(new HomeResource(bookRepository));
    }
}
