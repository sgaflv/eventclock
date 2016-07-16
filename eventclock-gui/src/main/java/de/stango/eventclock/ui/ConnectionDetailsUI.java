package de.stango.eventclock.ui;


import com.google.inject.Singleton;

import de.stango.eventcalendar.model.ConnectionDetails;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

@Singleton
public class ConnectionDetailsUI {

	private static final double WIDTH = 460;
	private static final double HEIGHT = 250;
	
	private final TextField connectionAddress = new TextField("address");
	private final TextField userName = new TextField("user name");
	private final PasswordField password = new PasswordField();
	private final Scene mainScene;
	
	private final Button connect = new Button("Connect");
	public final Button refresh = new Button("Refresh") {
		public void fire() {
			refreshStyle();
		};
	};
	
	private Button cancel = new Button("Cancel") {
		public void fire() {
			Platform.exit();
		}
	};
	
	private final FlowPane rootPane;
	private double xOffset = 0;
	private double yOffset = 0;

	public ConnectionDetailsUI(Stage stage, ConnectionDetails connectionDetails) {
		
		rootPane = new FlowPane(Orientation.VERTICAL);
		rootPane.setVgap(6);
		
		rootPane.getChildren().add(new Text("Connection address:"));
		rootPane.getChildren().add(connectionAddress);
		
		rootPane.getChildren().add(new Text("User name:"));
		rootPane.getChildren().add(userName);
		rootPane.getChildren().add(new Text("Password:"));
		rootPane.getChildren().add(password);
		
		FlowPane buttonPane = new FlowPane();
		
		buttonPane.setHgap(5);
		
		buttonPane.getChildren().add(connect);
		buttonPane.getChildren().add(cancel);
		buttonPane.getChildren().add(refresh);
		
		rootPane.getChildren().add(buttonPane);
		
		mainScene = new Scene(rootPane, WIDTH, HEIGHT, Color.TRANSPARENT);
		
		refreshStyle();
		
		stage.setAlwaysOnTop(true);
		stage.setResizable(false);
		stage.setScene(mainScene);
		stage.initStyle(StageStyle.TRANSPARENT);
		
		setupMouseEvents(stage, rootPane);
	}

	public void refreshStyle() {
		mainScene.getStylesheets().clear();
		mainScene.getStylesheets().add("style.css");
	}
	
	private void setupMouseEvents(final Stage stage, final Pane pane) {
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
}
