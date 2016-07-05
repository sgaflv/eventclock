package de.stango.eventclock.connection;

import org.junit.Test;

import de.stango.eventcalendar.model.ConnectionSetup;
import de.stango.eventcalendar.model.io.XmiSerializer;
import de.stango.eventclock.connection.WebDavClient;

public class WebDavClientTest {
	
	@Test
	public void testConnect() throws Exception {
		XmiSerializer xmiSerializer = new XmiSerializer();
		
		ConnectionSetup connection = (ConnectionSetup) xmiSerializer.load("connection.xml");
		
		WebDavClient client = new WebDavClient(connection.getUserName(), connection.getPassword());
		client.connect(connection.getConnectionAddress());
	}
	
}
