package de.stango.eventclock.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.stango.eventcalendar.model.ConnectionDetails;
import de.stango.eventcalendar.model.ModelFactory;
import de.stango.eventcalendar.model.impl.ModelFactoryImpl;
import de.stango.eventcalendar.model.io.XmiSerializer;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

public class MainWindow {

	private static final Duration REFRESH_RATE = Duration.seconds(1);

	private StyleWatcher styleWatcher;
	private IClockPresenter clockPresenter;
	
	private ConnectionDetails connectionDetails;
	
	private ConnectionDetailsUI connectionDetailsUi;


	private ConnectionDetails getConnectionDetails() {
		
		ModelFactory factory = new ModelFactoryImpl();
		ConnectionDetails connection = factory.createConnectionDetails();
		connection.setConnectionAddress("address");
		connection.setUserName("username");
		connection.setPassword("passowrd");
		
		XmiSerializer xmiSerializer = new XmiSerializer();
		xmiSerializer.save(connection, "connection.xml");
		
		return connection;
	}


	private void bindToTime() {
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				Calendar time = Calendar.getInstance();
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
				setText(simpleDateFormat.format(time.getTime()));
			}

			private void setText(String format) {
				clockPresenter.updateClockText(format);
			}
		}), new KeyFrame(REFRESH_RATE));

		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();

	}
}
