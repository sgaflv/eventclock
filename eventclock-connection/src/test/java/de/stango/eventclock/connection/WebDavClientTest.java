package de.stango.eventclock.connection;

import org.junit.Test;

import de.stango.eventcalendar.model.ConnectionDetails;
import de.stango.eventcalendar.model.io.XmiSerializer;
import de.stango.eventclock.connection.WebDavClient;

public class WebDavClientTest {
	
	@Test
	public void testConnect() throws Exception {
		XmiSerializer xmiSerializer = new XmiSerializer();
		
		ConnectionDetails connection = (ConnectionDetails) xmiSerializer.load("connection.xml");
		
		WebDavClient client = new WebDavClient(connection.getUserName(), connection.getPassword());
		client.connect(connection.getConnectionAddress());
	}
	
}
