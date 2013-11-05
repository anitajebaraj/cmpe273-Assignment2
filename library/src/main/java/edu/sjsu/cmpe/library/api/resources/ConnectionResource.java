package edu.sjsu.cmpe.library.api.resources;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;

import edu.sjsu.cmpe.library.config.LibraryServiceConfiguration;
import edu.sjsu.cmpe.library.domain.Book;
import edu.sjsu.cmpe.library.domain.Book.Status;


public class ConnectionResource {
public void subscribeToBookOrders() throws JMSException, MalformedURLException
{
	LibraryServiceConfiguration configuration=new LibraryServiceConfiguration();
	String topicName = configuration.getStompTopicName();
	System.out.println("destination=="+topicName);
	/*String user = env("APOLLO_USER", "admin");
	String password = env("APOLLO_PASSWORD", "password");
	String host = env("APOLLO_HOST", "54.215.210.214");
	int port = Integer.parseInt(env("APOLLO_PORT", "61613"));*/
	String user =configuration.getApolloUser();
		String password = configuration.getApolloPassword();
		String host = configuration.getApolloHost();
		int port =  Integer.parseInt(configuration.getApolloPort());
		System.out.println("user=="+user+"  pwd==="+password+"  host=="+host+"  port==="+port);

	String body="";
	StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
	factory.setBrokerURI("tcp://" + host + ":" + port);
	Connection connection = factory.createConnection(user, password);
	
	connection.start();
	
	Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	Destination dest = new StompJmsDestination(topicName);
	MessageConsumer consumer = session.createConsumer(dest);
	System.currentTimeMillis();
	System.out.println("Waiting for messages...");
	while(true) {
	    Message msg = consumer.receive();
	   System.out.println("msg is received==="+msg);
	    if( msg instanceof  TextMessage ) {
		 body = ((TextMessage) msg).getText();
		if( "SHUTDOWN".equals(body)) {
		    break;
		}
		System.out.println("Received message = " + body);
		String[] values=body.split(":");
		Book book=new Book();
		long orderIsbn=Long.parseLong(values[0]);
		book=checkIfBookAvailable(orderIsbn);
		//System.out.println("book=="+book);
		
		if(book==null)
		{
			
			Book newBook=new Book();
			System.out.println("values[3]=="+values[3]);
			String coverImage=values[3]+":"+values[4];
			
			newBook.setIsbn(orderIsbn);
			newBook.setTitle(values[1]);
			newBook.setCoverimage(new URL(coverImage));
			newBook.setCategory(values[2]);
			Status status=Status.available;
			newBook.setStatus(status);
		saveNewBookFromOrder(newBook,orderIsbn);
		}
		else
		{
			Status status=Status.available;
			book.setStatus(status);
		}
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
public Book saveNewBookFromOrder(Book newBook,Long isbn) {	
	ConcurrentHashMap<Long, Book> newBookHashMap=Book.getNewBookHashMap();
	System.out.println("newBookHashMap===="+newBookHashMap);
	checkNotNull(newBook, "newBook instance must not be null");
	System.out.println("isbn==="+isbn);
// Finally, save the new book into the map
//bookInMemoryMap.putIfAbsent(isbn, newBook);
	newBookHashMap.putIfAbsent(isbn, newBook);
	Book.setNewBookHashMap(newBookHashMap);
	System.out.println("newBookHashMap===="+newBookHashMap);
return newBook;
}
public Book checkIfBookAvailable(Long isbn) {
	ConcurrentHashMap<Long, Book> bookInMemoryMap=Book.getNewBookHashMap();
checkArgument(isbn > 0,
	"ISBN was %s but expected greater than zero value", isbn);
return bookInMemoryMap.get(isbn);
}
}
