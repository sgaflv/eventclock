package de.stango.eventcalendar.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OccurrenceList implements Comparator<EventOccurrence> {
	private final List<EventOccurrence> occurrences = new ArrayList<EventOccurrence>();
	private final List<EventOccurrence> readonlyOccurrences = Collections.unmodifiableList(occurrences);
	
	public void generateOccurrences(EventProfile profile) {
		occurrences.clear();
		for (Event event : profile.getEvents()) {
			occurrences.addAll(event.getOccurrences());
		}
		
		occurrences.sort(this);
	}
	
	public List<EventOccurrence> getOccurrences() {
		return readonlyOccurrences;
	}
	
	@Override
	public int compare(EventOccurrence o1, EventOccurrence o2) {
		long compare = o1.getStarts().getMillis() - o2.getStarts().getMillis();
		
		if (compare > 0) {
			return 1;
		}
		
		if (compare < 0) {
			return -1;
		}
		
		return 0;
	}
}
