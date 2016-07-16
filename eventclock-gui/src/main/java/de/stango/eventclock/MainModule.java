package de.stango.eventclock;

import java.nio.file.FileSystems;
import java.nio.file.Path;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.name.Names;

public class MainModule implements Module {

	private static final String RESOURCES = "Resources";

	@Override
	public void configure(Binder binder) {
		binder.bind(Path.class).annotatedWith(Names.named(RESOURCES))
				.toInstance(FileSystems.getDefault().getPath("src/main/resources"));
		
		
	}
}
