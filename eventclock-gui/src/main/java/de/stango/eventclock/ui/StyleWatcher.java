package de.stango.eventclock.ui;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class StyleWatcher implements Runnable {

	private Path path;
	private ConnectionDetailsUI refreshSubject;

	@Inject
	public StyleWatcher(@Named("Resources") Path path, ConnectionDetailsUI refreshSubject) {
		this.path = path;
		this.refreshSubject = refreshSubject;
	}

	// print the events and the affected file
	private void printEvent(WatchEvent<?> event) {
		Kind<?> kind = event.kind();
		refreshSubject.refreshStyle();
	}

	@Override
	public void run() {
		try {
			WatchService watchService = path.getFileSystem().newWatchService();
			path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

			while (true) {
				WatchKey watchKey;
				watchKey = watchService.take();

				Thread.sleep(300);
				
				// poll for file system events on the WatchKey
				for (final WatchEvent<?> event : watchKey.pollEvents()) {
					printEvent(event);
				}

				// if the watched directory gets deleted, get out of run method
				if (!watchKey.reset()) {
					watchKey.cancel();
					watchService.close();
					break;
				}
			}

		} catch (InterruptedException ex) {

			return;
		} catch (IOException ex) {
			ex.printStackTrace();
			return;
		}
	}
}
