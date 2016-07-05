package de.stango.eventclock.connection;

import java.text.ParseException;
import java.util.List;

import de.stango.eventcalendar.model.Event;
import de.stango.eventcalendar.model.EventProfile;
import de.stango.eventcalendar.model.ModelFactory;
import de.stango.eventcalendar.model.OccurrenceGenerator;
import de.stango.eventcalendar.model.RecurrenceRule;
import de.stango.eventcalendar.model.impl.ModelFactoryImpl;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Status;

public class CalendarVisitor {
	private final DateTime from;
	private final DateTime to;
	private VTimeZone lastTimeZone;
	private final ModelFactory factory = new ModelFactoryImpl();
	
	OccurrenceGenerator generator = new OccurrenceGenerator();
	
	public CalendarVisitor(DateTime from, DateTime to) {
		this.from = from;
		this.to = to;
	}
	
	public EventProfile visitComponents(Calendar calendar) {
		EventProfile events = factory.createEventProfile();
		
		@SuppressWarnings("unchecked")
		List<? extends CalendarComponent> components = (List<? extends CalendarComponent>) calendar.getComponents();
		
		for (CalendarComponent component : components) {
			
			PeriodList periods = component.calculateRecurrenceSet(new Period(from, to));
			
			// if (periods.isEmpty()) {
			// continue;
			// }
			
			if (component instanceof VTimeZone) {
				visitTimeZone((VTimeZone) component);
			} else if (component instanceof VEvent) {
				if (((VEvent) component).getStatus().equals(Status.VEVENT_CANCELLED)) {
					continue;
				}
				Event newEvent = visitEvent((VEvent) component, periods);
				events.getEvents().add(newEvent);
			} else {
				System.err.println("Do not know what to do with " + component.getName());
			}
			
		}
		
		return events;
	}
	
	private Event visitEvent(VEvent event, PeriodList periods) {
		org.joda.time.DateTime tillDate = org.joda.time.DateTime.now().plusWeeks(2);
		org.joda.time.DateTime fromDate = org.joda.time.DateTime.now().withTimeAtStartOfDay();
		
		Event newEvent = factory.createEvent();
		
		// for (Object o : periods) {
		// Period period = (Period) o;
		// System.out.println("From " + period.getStart() + ", to " +
		// period.getEnd());
		// newEvent.setStarts(new
		// org.joda.time.DateTime(period.getStart().getTime()));
		// newEvent.setEnds(new
		// org.joda.time.DateTime(period.getEnd().getTime()));
		// }
		
		newEvent.setUid(event.getUid().toString());
		if (event.getLocation() != null) {
			newEvent.setLocation(event.getLocation().getValue().toString());
		}
		
		if (event.getSummary() != null) {
			newEvent.setSummary(event.getSummary().getValue().toString());
		}
		
		newEvent.setStarts(new org.joda.time.DateTime(event.getStartDate().getDate().getTime()));
		newEvent.setEnds(new org.joda.time.DateTime(event.getEndDate().getDate().getTime()));
		
		RRule recurrenceRule = (RRule) event.getProperty("RRULE");
		if (recurrenceRule != null) {
			try {
				newEvent.setRecurrenceRule(new RecurrenceRule(recurrenceRule.getRecur().toString()));
			} catch (ParseException e) {
				newEvent.setRecurrenceRule(null);
			}
		}
		generator.generateOccurrences(newEvent, fromDate, tillDate);
		
		return newEvent;
	}
	
	private void visitTimeZone(VTimeZone timezone) {
	
	}
}
