package cz.filipekt.jdcv.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import cz.filipekt.jdcv.Visualizer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Utility class simplifying the access to the resources of the application.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class Resources {

		/**
		 * Opens and returns a stream on the resource specified by its name in the parameter.
		 * @param resourceName Name of the resource to be loaded
		 * @return A stream reading from the specified resource.
		 * @throws IOException If the resource couldn't be found or the stream couldn't be opened.
		 */
		public static InputStream getResourceInputStream(String resourceName) throws IOException {
			try {
				URI resource = getResourceAsURI(resourceName);
				URL urlResource = resource.toURL();
				return urlResource.openStream();
			} catch (IllegalArgumentException ex){
				throw new IOException(ex);
			}
		}
		
		/**
		 * Constructs an URI pointing to the resource specified by the name in the parameter.
		 * @param resourceName The resource name
		 * @return URI pointing to the resource given by its name in the method parameter.
		 * @throws IllegalArgumentException If the given resource name is null or in an invalid format 
		 */
		public static URI getResourceAsURI(String resourceName) throws IllegalArgumentException {
			if (resourceName == null){
				throw new IllegalArgumentException();
			}
			try {
				if (Debug.debugModeOn){
					return Paths.get(Debug.projectDir, "resources", resourceName).toUri();
				} else {
					return Visualizer.class.getResource("/resources/" + resourceName).toURI();
				}
			} catch (URISyntaxException ex){
				throw new IllegalArgumentException(ex);
			}
		}

		/**
		 * Loads an image specified by its resource name and returns it wrapped in an instance 
		 * of {@link ImageView}. When the image could not be loaded for any reason, an empty 
		 * {@link ImageView} is returned.  
		 * @param resourceName Name of the resource containing the desired image
		 * @param size Preferred width and height of the returned {@link ImageView} 
		 * @return The desired image wrapped in a {@link ImageView}. If the image could
		 * not be loaded, an empty {@link ImageView} is returned.
		 */
		public static ImageView getImageView(String resourceName, double size){
			try (InputStream stream = getResourceInputStream(resourceName)){							
				return new ImageView(new Image(stream, size, size, true, true));
			} catch (IOException ex) { // if the specified image is not found
				return new ImageView();
			}
		}

}
