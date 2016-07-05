package de.stango.eventclock.ui;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ClockPresenter implements IClockPresenter {
	private static final double WIDTH = 250;
	private static final double HEIGHT = 100;
	private static final double RADIUS = HEIGHT / 2;
	
	private static Text clockText;
	private StackPane rootPane;
	private static double xOffset = 0;
	private static double yOffset = 0;
	
	public ClockPresenter(Stage stage) {
		
		setupTextStyle();
		
		rootPane = new StackPane();
		
		rootPane.setStyle(" -fx-background-color: linear-gradient(#eaf6fd 0%, #3D99CC 100%); "
				+ "-fx-background-radius: " + RADIUS + "px;");
		rootPane.getChildren().add(clockText);
		
		Scene scene = new Scene(rootPane, WIDTH, HEIGHT, Color.TRANSPARENT);
		
		stage.setAlwaysOnTop(true);
		stage.setResizable(false);
		stage.setScene(scene);
		stage.initStyle(StageStyle.TRANSPARENT);
		
		setupMouseEvents(stage, rootPane);
	}
	
	private void setupTextStyle() {
		Font textFont = new Font("Arial", 56.0);
		Paint green = Paint.valueOf("green");
		
		clockText = new Text("Time");
		clockText.setFill(green);
		clockText.setFont(textFont);
	}
	
	private void setupMouseEvents(final Stage stage, final StackPane pane) {
		pane.setOnMousePressed(new EventHandler<MouseEvent>() {
			
			@Override
			public void handle(MouseEvent event) {
				xOffset = stage.getX() - event.getScreenX();
				yOffset = stage.getY() - event.getScreenY();
			}
		});
		
		pane.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				stage.setX(event.getScreenX() + xOffset);
				stage.setY(event.getScreenY() + yOffset);
			}
		});
	}
	
	@Override
	public void updateClockText(String clockValue) {
		clockText.setText(clockValue);
	}
	
	@Override
	public void updateHintText(String summary, String location) {
		Tooltip tooltip = new Tooltip(String.format("%s\n%s", summary, location));
		Tooltip.install(rootPane, tooltip);
	}
	
}
