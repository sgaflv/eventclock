package de.stango.eventclock;

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.stango.eventclock.ui.ConnectionDetailsUI;
import de.stango.eventclock.ui.StyleWatcher;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
	
	private StyleWatcher styleWatcher;
	
	public static void main(String[] args) {
		Main.launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		Injector injector = Guice.createInjector(new MainModule());
		
		Thread dirWatcherThread = new Thread(styleWatcher);
		dirWatcherThread.start();
		
		MainModule main = injector.getInstance(MainModule.class);
		

		stage.show();
	}
}
