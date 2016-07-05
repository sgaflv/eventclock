package de.stango.eventcalendar.model.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import de.stango.eventcalendar.model.ModelPackage;

public class XmiSerializer {
	
	public void save(EObject container, String fileName) {
		
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		resourceSet.getPackageRegistry().put(ModelPackage.eNS_URI, ModelPackage.eINSTANCE);
		Resource resource = resourceSet.createResource(URI.createURI(fileName));
		resource.getContents().add(container);
		
		FileOutputStream fo;
		try {
			fo = new FileOutputStream(new File(fileName));
			resource.save(fo, null);
			fo.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	public EObject load(String fileName) {
		
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		resourceSet.getPackageRegistry().put(ModelPackage.eNS_URI, ModelPackage.eINSTANCE);
		
		Resource resource = resourceSet.createResource(URI.createURI("test"));
		
		FileInputStream fi;
		
		try {
			fi = new FileInputStream(new File(fileName));
			resource.load(fi, null);
			fi.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return resource.getContents().get(0);
	}
}
