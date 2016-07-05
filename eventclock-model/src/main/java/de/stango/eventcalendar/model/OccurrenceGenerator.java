package de.stango.eventcalendar.model;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

public class OccurrenceGenerator {
	private final ModelFactory factory = ModelFactory.eINSTANCE;
	
	public void generateOccurrences(Event event, DateTime fromDate, DateTime toDate) {
		if (fromDate.isAfter(toDate)) {
			return;
		}
		
		List<EventOccurrence> occurrences = new ArrayList<EventOccurrence>();
		
		if (event.getRecurrenceRule() == null) {
			
			if (fromDate.isBefore(event.getEnds()) && toDate.isAfter(event.getStarts())) {
				EventOccurrence newOccurrence = factory.createEventOccurrence();
				newOccurrence.setStarts(event.getStarts());
				newOccurrence.setEnds(event.getEnds());
				occurrences.add(newOccurrence);
			}
			
		} else {
			occurrences = event.getRecurrenceRule().getOccurrences(event, fromDate, toDate);
		}
		
		event.getOccurrences().addAll(occurrences);
	}
}
