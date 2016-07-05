package de.stango.eventcalendar.model;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTimeZone;

import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.RRule;

public class RecurrenceRule {
	private final ModelFactory factory = ModelFactory.eINSTANCE;
	
	private final RRule recurrenceRule;
	
	public RecurrenceRule(String rule) throws ParseException {
		recurrenceRule = new RRule(rule);
	}
	
	@Override
	public String toString() {
		return recurrenceRule.getRecur().toString();
	}
	
	public List<EventOccurrence> getOccurrences(Event event, org.joda.time.DateTime fromDay,
			org.joda.time.DateTime toDay) {
		final long millisPerDay = 24 * 60 * 60 * 1000;
		
		DateTimeZone zone = DateTimeZone.getDefault();
		
		DateTime periodStart = new DateTime(fromDay.getMillis());
		DateTime periodEnd = new DateTime(toDay.getMillis());
		
		DateList list = recurrenceRule.getRecur().getDates(periodStart, periodEnd, Value.DATE_TIME);
		
		long startTime = event.getStarts().getMillisOfDay();
		long endTime = event.getEnds().getMillisOfDay();
		if (endTime < startTime) {
			endTime += millisPerDay;
		}
		
		List<EventOccurrence> occurrences = new ArrayList<EventOccurrence>();
		
		for (Object dateObject : list) {
			DateTime date = (DateTime) dateObject;
			
			org.joda.time.DateTime jodaDate = new org.joda.time.DateTime(date.getTime());
			
			long startOfDay = jodaDate.withTimeAtStartOfDay().getMillis();
			
			EventOccurrence newOccurrence = factory.createEventOccurrence();
			newOccurrence.setStarts(new org.joda.time.DateTime(startOfDay + startTime, zone));
			newOccurrence.setEnds(new org.joda.time.DateTime(startOfDay + endTime, zone));
			
			occurrences.add(newOccurrence);
		}
		return occurrences;
	}
}
