package de.stango.eventclock;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.stango.eventclock.ui.ClockPresenter;
import de.stango.eventclock.ui.IClockPresenter;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainWindow extends Application {
	
	private static final Duration REFRESH_RATE = Duration.seconds(1);
	
	private IClockPresenter clockPresenter;
	
	@Override
	public void start(Stage stage) throws Exception {
		clockPresenter = new ClockPresenter(stage);
		
		bindToTime();
		stage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
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
