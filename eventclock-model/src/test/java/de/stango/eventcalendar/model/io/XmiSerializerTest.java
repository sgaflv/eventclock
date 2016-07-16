package de.stango.eventcalendar.model.io;

import org.eclipse.emf.ecore.EObject;
import org.joda.time.DateTime;
import org.junit.Test;

import de.stango.eventcalendar.model.ConnectionDetails;
import de.stango.eventcalendar.model.Event;
import de.stango.eventcalendar.model.EventProfile;
import de.stango.eventcalendar.model.ModelFactory;
import de.stango.eventcalendar.model.impl.ModelFactoryImpl;

public class XmiSerializerTest {
	
	@Test
	public void testSerializerSaveAndLoad() {
		ModelFactory factory = new ModelFactoryImpl();
		EventProfile events = factory.createEventProfile();
		
		events.setName("Events for 2016");
		events.setVersion("0.1.1");
		
		for (int i = 0; i < 3; i++) {
			Event event = factory.createEvent();
			event.setLocation("Some location" + i);
			event.setSummary("some summary" + i);
			event.setStarts(DateTime.now());
			
			events.getEvents().add(event);
			events.getEvents().add(event);
		}
		
		XmiSerializer xmiSerializer = new XmiSerializer();
		xmiSerializer.save(events, "test.xml");
		
		EObject object = xmiSerializer.load("test.xml");
		
		xmiSerializer.save(object, "test2.xml");
	}
	
	@Test
	public void testSetupSaveAndLoad() {
		ModelFactory factory = new ModelFactoryImpl();
		ConnectionDetails connection = factory.createConnectionDetails();
		connection.setConnectionAddress("address");
		connection.setUserName("username");
		connection.setPassword("passowrd");
		
		XmiSerializer xmiSerializer = new XmiSerializer();
		xmiSerializer.save(connection, "connectiondata.xml");
	}
	
}
