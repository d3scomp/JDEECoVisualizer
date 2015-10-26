package cz.filipekt.jdcv.ensembles;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import cz.filipekt.jdcv.plugins.InfoPanel;

/**
 * Stores the shapes representing the ensemble membership relation.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class EnsembleDatabase {
	
	/**
	 * To a membership relation, i.e. a triplet (ensemble,coordinator,member),
	 * it maps a corresponding shape visualizing the relation.
	 */
	private final Map<MembershipRelation,Node> shapeMappings = new HashMap<>();

	/**
	 * @return To a membership relation, i.e. a triplet (ensemble,coordinator,member),
	 * it maps a corresponding shape visualizing the relation.
	 * @see {@link EnsembleDatabase#shapeMappings} 
	 */
	public Map<MembershipRelation,Node> getEnsembleShapes(){
		return shapeMappings;
	}
	
	/**
	 * For given ensemble name and coordinator, it stores a color used
	 * for shapes visualizing the membership relations including these
	 * two (ensemble,coordinator).
	 */
	private final Map<CoordinatorRelation,Paint> colors;
	
	/** 
	 * @param colors Holds the colors used for ensemble memberships
	 */
	public EnsembleDatabase(Map<CoordinatorRelation, Paint> colors) {
		if (colors == null){
			this.colors = new HashMap<>();
		} else {
			this.colors = colors;
		}
	}

	/**
	 * Given an ensemble name and coordinator ID, this method gives a color
	 * which will be used to visualize the ensemble memberships including
	 * the given ensemble and coordinator.
	 * @param ensembleName Name of an ensemble
	 * @param coordinator ID of an coordinator
	 * @return Color which will be used to visualize the ensemble memberships 
	 * including the given ensemble and coordinator
	 */
	private Paint getColor(String ensembleName, String coordinator){
		CoordinatorRelation pair = new CoordinatorRelation(ensembleName, coordinator);
		if (!colors.containsKey(pair)){
			double red = Math.random();
			double green = Math.random();
			double blue = Math.random();
			Paint color = Color.color(red, green, blue, ensembleLineOpacity);
			colors.put(pair, color);
		}
		return colors.get(pair);
	}
	
	/**
	 * Width of the lines used to visualize the ensemble membership relation
	 */
	private final double ensembleLineWidth = 1.5;
	
	/**
	 * Opacity of the lines used to visualize the ensemble membership relation
	 */
	private final double ensembleLineOpacity = 0.8;
	
	/**
	 * Given the ensemble name and IDs of coordinator and member, this method
	 * gives a geometric shape visualizing this relationship.
	 * @param ensembleName Name of an ensemble
	 * @param coordinator ID of an coordinator
	 * @param member ID of a member
	 * @param coordinatorNode The shape visualizing the coordinator
	 * @param memberNode The shape visualizing the member
	 * @return Geometric shape visualizing this relationship specified
	 * by the ensemble name, coordinator and member IDs 
	 */
	public Node getEnsembleShape(String ensembleName, String coordinator, String member, 
			Node coordinatorNode, Node memberNode){
		MembershipRelation t = new MembershipRelation(ensembleName, coordinator, member);
		if (!shapeMappings.containsKey(t) && (memberNode!=null)){
			final Line line = new Line();
			line.setVisible(false);
			line.startXProperty().bind(coordinatorNode.translateXProperty());
			line.startYProperty().bind(coordinatorNode.translateYProperty());
			line.endXProperty().bind(memberNode.translateXProperty());
			line.endYProperty().bind(memberNode.translateYProperty());
			Paint color = getColor(ensembleName, coordinator);
			line.setStroke(color);
			line.setStrokeWidth(ensembleLineWidth);
			final Map<String,String> data = new LinkedHashMap<>();
			data.put("Ensemble Name", ensembleName);
			data.put("Coordinator ID", coordinator);
			data.put("Member ID", member);
			line.setOnMouseEntered(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent arg0) {
					line.setStrokeWidth(ensembleLineWidth * 3);
				}
			});
			line.setOnMouseExited(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent arg0) {
					line.setStrokeWidth(ensembleLineWidth);
				}
			});
			line.setOnMouseClicked(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent arg0) {
					InfoPanel.getInstance().setInfo("Ensemble Membership Selected", data);
				}
			});
			shapeMappings.put(t, line);
		}
		return shapeMappings.get(t);
	}
}