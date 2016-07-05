package de.stango.eventclock.connection;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;

import de.stango.eventcalendar.model.EventOccurrence;
import de.stango.eventcalendar.model.EventProfile;
import de.stango.eventcalendar.model.OccurrenceList;
import de.stango.eventcalendar.model.io.XmiSerializer;
import net.fortuna.ical4j.model.DateTime;

public class WebDavClient {
	private HttpClient client;
	private Credentials credentials;
	private CalendarService service = new CalendarService();
	private final long DAY = 24 * 60 * 60 * 1000;
	
	public WebDavClient(String username, String password) throws Exception {
		client = new HttpClient();
		credentials = new UsernamePasswordCredentials(username, password);
		client.getState().setCredentials(AuthScope.ANY, credentials);
	}
	
	public MultiStatus report(String url) throws Exception {
		DateTime from = new DateTime();
		DateTime to = new DateTime(from.getTime() + 1 * DAY);
		
		MultiStatus multi = service.doCalendarQuery(client, url, from, to);
		for (MultiStatusResponse response : multi.getResponses()) {
			System.out.println(response.getHref());
		}
		return multi;
	}
	
	public void connect(String url) throws Exception {
		DateTime from = new DateTime();
		DateTime to = new DateTime(from.getTime() + 1 * DAY);
		
		// service.doSyncQuery(client, url);
		EventProfile profile = service.getEvents(client, url, from, to);
		
		OccurrenceList occurrences = new OccurrenceList();
		occurrences.generateOccurrences(profile);
		
		for (EventOccurrence occurrence : occurrences.getOccurrences()) {
			
			System.out.println(
					occurrence.getStarts() + " " + occurrence.getEnds() + ":" + occurrence.getEvent().getSummary());
		}
		
		XmiSerializer serializer = new XmiSerializer();
		serializer.save(profile, "connectionResult.xml");
		
	}
	
}
