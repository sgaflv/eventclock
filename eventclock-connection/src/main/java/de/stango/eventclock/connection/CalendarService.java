package de.stango.eventclock.connection;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.SimpleLog;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.client.methods.PropFindMethod;
import org.apache.jackrabbit.webdav.client.methods.ReportMethod;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.stango.eventcalendar.model.EventProfile;
import de.stango.eventcalendar.model.ModelFactory;
import de.stango.eventcalendar.model.impl.ModelFactoryImpl;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;

public class CalendarService {
	private Log logger = new SimpleLog("Logger");
	
	private static final Namespace D_NAMESPACE = Namespace.getNamespace("D", "DAV:");
	private static final Namespace C_NAMESPACE = Namespace.getNamespace("C", "urn:ietf:params:xml:ns:caldav");
	private static final Namespace CS_NAMESPACE = Namespace.getNamespace("CS", "http://calendarserver.org/ns/");
	private static final SimpleDateFormat WEBCAL_DATE_TIME_FORMAT = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
	
	private PropFindMethod makePropFindMethod(String uri) throws ParserConfigurationException, IOException {
		DavPropertyNameSet set = new DavPropertyNameSet();
		set.add(PropFindMethod.PROPERTY_RESOURCETYPE, D_NAMESPACE);
		set.add("displayname", D_NAMESPACE);
		set.add("getctag", CS_NAMESPACE);
		
		PropFindMethod method = new PropFindMethod(uri, PropFindMethod.PROPFIND_BY_PROPERTY, set, 0);
		
		return method;
	}
	
	public MultiStatus doSyncQuery(HttpClient client, String uri) throws ParserConfigurationException, IOException {
		PropFindMethod method = makePropFindMethod(uri);
		
		return executeMultiStatus(client, method);
	}
	
	public EventProfile getEvents(HttpClient client, String uri, DateTime from, DateTime to)
			throws ParserConfigurationException, IOException, DavException, ParserException {
		ModelFactory factory = new ModelFactoryImpl();
		EventProfile profile = factory.createEventProfile();
		
		ReportMethod method = makeEventReportMethod(uri, from, to);
		CalendarVisitor visitor = new CalendarVisitor(from, to);
		
		try {
			int response = client.executeMethod(method);
			
			MultiStatus multiStatus = method.getResponseBodyAsMultiStatus();
			
			for (MultiStatusResponse res : multiStatus.getResponses()) {
				DavPropertySet propSet = res.getProperties(DavServletResponse.SC_OK);
				
				String calString = propSet.get("calendar-data", C_NAMESPACE).getValue().toString();
				
				calString = calString.replaceAll("\n ", "").replaceAll("\r", "");
				
				StringReader sin = new StringReader(calString);
				
				CalendarBuilder builder = new CalendarBuilder();
				Calendar calendar = builder.build(sin);
				
				EventProfile curProfile = visitor.visitComponents(calendar);
				profile.getEvents().addAll(curProfile.getEvents());
			}
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
		
		return profile;
		
	}
	
	private MultiStatus executeMultiStatus(HttpClient client, PropFindMethod method) {
		if (method == null) {
			return null;
		}
		try {
			client.executeMethod(method);
			
			MultiStatus multiStatus = method.getResponseBodyAsMultiStatus();
			
			for (MultiStatusResponse res : multiStatus.getResponses()) {
				System.out.println(res.getHref());
				
				DavPropertySet propSet = res.getProperties(DavServletResponse.SC_OK);
				
				for (DavPropertyName name : propSet.getPropertyNames()) {
					
					DavProperty<?> prop = propSet.get(name);
					System.out.println(prop.getName() + " = " + prop.getValue().toString() + "("
							+ propSet.getClass().getCanonicalName() + ")");
				}
			}
			
			return null;
			
		} catch (Exception e) {
			if (logger.isDebugEnabled())
				logger.debug("Exception occurs when querying calendar events from CalDav server", e);
			return null;
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
		}
	}
	
	private ReportMethod makeEventReportMethod(String uri, DateTime from, DateTime to)
			throws ParserConfigurationException, DavException, IOException {
			
		Document doc = DomUtil.createDocument();
		Element calendarQuery = createCalendarQueryElement(doc);
		
		ReportInfo reportInfo = new ReportInfo(calendarQuery, DavConstants.DEPTH_0);
		DavPropertyNameSet propNameSet = reportInfo.getPropertyNameSet();
		propNameSet.add(DavPropertyName.GETETAG);
		propNameSet.add(DavPropertyName.create("calendar-data", C_NAMESPACE));
		
		Element filter = createFilterElement(doc, from, to);
		
		reportInfo.setContentElement(filter);
		
		ReportMethod report = null;
		report = new ReportMethod(uri, reportInfo);
		return report;
	}
	
	private Element createCalendarQueryElement(Document doc) {
		Element calendarQuery = DomUtil.createElement(doc, "calendar-query", C_NAMESPACE);
		calendarQuery.setAttributeNS(Namespace.XMLNS_NAMESPACE.getURI(),
				Namespace.XMLNS_NAMESPACE.getPrefix() + ":" + D_NAMESPACE.getPrefix(), D_NAMESPACE.getURI());
		calendarQuery.setAttributeNS(Namespace.XMLNS_NAMESPACE.getURI(),
				Namespace.XMLNS_NAMESPACE.getPrefix() + ":" + C_NAMESPACE.getPrefix(), C_NAMESPACE.getURI());
				
		return calendarQuery;
	}
	
	private Element createFilterElement(Document doc, DateTime from, DateTime to) {
		
		Element eventComp = DomUtil.createElement(doc, "comp-filter", C_NAMESPACE);
		eventComp.setAttribute("name", "VEVENT");
		eventComp.appendChild(createTimeRange(doc, from, to));
		eventComp.appendChild(createPropStatusFilter(doc));
		
		Element todoComp = DomUtil.createElement(doc, "comp-filter", C_NAMESPACE);
		todoComp.setAttribute("name", "VTODO");
		todoComp.appendChild(createTimeRange(doc, from, to));
		todoComp.appendChild(createStatusCompleteElement(doc));
		
		Element calendarComp = DomUtil.createElement(doc, "comp-filter", C_NAMESPACE);
		calendarComp.setAttribute("name", "VCALENDAR");
		calendarComp.appendChild(eventComp);
		calendarComp.appendChild(todoComp);
		
		Element filter = DomUtil.createElement(doc, "filter", C_NAMESPACE);
		filter.appendChild(calendarComp);
		return filter;
	}
	
	private Element createStatusCompleteElement(Document doc) {
		Element compStatus = DomUtil.createElement(doc, "prop-filter", C_NAMESPACE);
		compStatus.setAttribute("name", "COMPLETED");
		Element isNotDefined = DomUtil.createElement(doc, "is-not-defined", C_NAMESPACE);
		compStatus.appendChild(isNotDefined);
		return compStatus;
	}
	
	private Element createPropStatusFilter(Document doc) {
		Element propFilter = DomUtil.createElement(doc, "prop-filter", C_NAMESPACE);
		propFilter.setAttribute("name", "STATUS");
		Element textMatch = DomUtil.createElement(doc, "text-match", C_NAMESPACE);
		textMatch.setAttribute("negate-condition", "yes");
		textMatch.setTextContent("CANCELLED");
		propFilter.appendChild(textMatch);
		return propFilter;
	}
	
	static public void printNode(Node doc) {
		String docString = getStringFromDocument(doc);
		System.out.println(docString);
	}
	
	static public String getStringFromDocument(Node doc) {
		try {
			DOMSource domSource = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(domSource, result);
			return writer.toString();
		} catch (TransformerException ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	private Element createTimeRange(Document doc, DateTime from, DateTime to) {
		
		Element timeRange = DomUtil.createElement(doc, "time-range", C_NAMESPACE);
		
		timeRange.setAttribute("start", WEBCAL_DATE_TIME_FORMAT.format(from));
		timeRange.setAttribute("end", WEBCAL_DATE_TIME_FORMAT.format(to));
		return timeRange;
	}
	
	public MultiStatus doCalendarQuery(HttpClient client, String uri, DateTime from, DateTime to) throws Exception {
		ReportMethod report = makeEventReportMethod(uri, from, to);
		if (report == null)
			return null;
		try {
			client.executeMethod(report);
			MultiStatus multiStatus = report.getResponseBodyAsMultiStatus();
			return multiStatus;
		} catch (Exception e) {
			if (logger.isDebugEnabled())
				logger.debug("Exception occurs when querying calendar events from CalDav server", e);
			return null;
		} finally {
			if (report != null) {
				report.releaseConnection();
			}
		}
	}
	
}
