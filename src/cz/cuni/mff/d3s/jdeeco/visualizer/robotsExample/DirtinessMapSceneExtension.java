package cz.cuni.mff.d3s.jdeeco.visualizer.robotsExample;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cz.cuni.mff.d3s.jdeeco.visualizer.extensions.MapSceneExtensionPoint;
import cz.filipekt.jdcv.MapScene;
import cz.filipekt.jdcv.SceneImportHandler.ImageProvider;
import cz.filipekt.jdcv.SceneImportHandler.ShapeProvider;
import cz.filipekt.jdcv.events.Event;
import cz.filipekt.jdcv.events.EventType;
import cz.filipekt.jdcv.network.MyNode;
import cz.filipekt.jdcv.plugins.InfoPanel;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

/**
 * Provides the capability to add key frames corresponding to dirtiness events
 * to the simulation.
 * 
 * @author Ilias Gerostathopoulos <iliasg@d3s.mff.cuni.cz>
 */
public class DirtinessMapSceneExtension implements MapSceneExtensionPoint {
	
	@Override
	public Collection<KeyFrame> buildFrames(Map<EventType, List<Event>> otherEvents, MapScene mapScene) throws IOException {
		Collection<KeyFrame> res = new ArrayList<>();
		Collection<Node> localDirtinessShapes = new ArrayList<>();
		List<Event> dirtinessEvents = otherEvents.get(EventType.DIRTINESS);
		for (Event e : dirtinessEvents) {
			DirtinessEvent de = (DirtinessEvent) e;
			double timeVal = mapScene.convertToVisualizationTime(de.getTime());
			Duration time = new Duration(timeVal);
			MyNode node = mapScene.getNodes().get(de.getNode()); 
			if (mapScene.getAdditionalResourcesPath() == null) {
				// additionalResourcesPath is not yet set, this is done by the acmescripts 
				return res; 
			}
			double intensity = de.getIntensity();
			ShapeProvider provider = new ImageProvider(false, mapScene.getAdditionalResourcesPath() + "dirt.png",
					mapScene.NODE_IMAGE_WIDTH, mapScene.NODE_IMAGE_HEIGHT, intensity);
			Node dirtinessShape = generateNodeWithBackgroundImage(mapScene, provider, node);
			KeyValue kv = new KeyValue(dirtinessShape.visibleProperty(), Boolean.TRUE);
			KeyFrame kf = new KeyFrame(time, kv);
			localDirtinessShapes.add(dirtinessShape);
			res.add(kf);
		}
		Collection<Node> dirtinessShapes = mapScene.getOtherShapes();
		dirtinessShapes.clear();
		dirtinessShapes.addAll(localDirtinessShapes);
		for (Node dirtinessShape : dirtinessShapes){
			KeyValue kv = new KeyValue(dirtinessShape.visibleProperty(), Boolean.FALSE);
			KeyFrame kf = new KeyFrame(Duration.ZERO, kv);
			res.add(kf);
		}
		return res;
	} 
	
	private Node generateNodeWithBackgroundImage(MapScene mapScene, ShapeProvider provider, MyNode node) throws IOException {

		double x = mapScene.getMatsimToVisual().transformX(node.getX());
		double y = mapScene.getMatsimToVisual().transformY(node.getY());
		Node shape = provider.getNewShape();

		if (shape != null) {
			shape.setTranslateX(x);
			shape.setTranslateY(y);
		}

		final Map<String, String> data = new LinkedHashMap<>();
		data.put("Node ID", node.getId());
		data.put("x-coordinate", node.getX() + "");
		data.put("y-coordinate", node.getY() + "");

		shape.setOnMouseEntered(null);
		shape.setOnMouseExited(null);
		shape.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				InfoPanel.getInstance().setInfo("Node selected:", data);
			}
		});
		return shape;
	}
	
}
