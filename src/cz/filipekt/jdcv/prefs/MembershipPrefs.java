package cz.filipekt.jdcv.prefs;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import cz.filipekt.jdcv.ensembles.MembershipRelation;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

/**
 * Preferences object associated with a given ensemble membership relation
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class MembershipPrefs implements VisibilityChangeable{
	
	/**
	 * Name of the ensemble
	 */
	private final String ensembleName;
	
	/**
	 * @return Name of the ensemble
	 * @see {@link MembershipPrefs#ensembleName}
	 */
	public String getEnsembleName() {
		return ensembleName;
	}

	/**
	 * ID of the coordinator
	 */
	private final String coordinator;
	
	/**
	 * @return ID of the coordinator
	 * @see {@link MembershipPrefs#coordinator}
	 */
	public String getCoordinator() {
		return coordinator;
	}

	/**
	 * ID of the member 
	 */
	private final String member;
	
	/**
	 * @return ID of the member
	 * @see {@link MembershipPrefs#member}
	 */
	public String getMember() {
		return member;
	}
	
	/**
	 * @return Representation of the membership relation for which 
	 * this object sets preferences
	 */
	public MembershipRelation getMembershipRelation(){
		return new MembershipRelation(ensembleName, coordinator, member);
	}
	
	/**
	 * Used for producing logs
	 */
	private final Writer logWriter;

	/**
	 * The geometric shape representing the membership relation
	 */
	private final Line line;

	/**
	 * @param ensembleName Name of the ensemble
	 * @param coordinator ID of the coordinator
	 * @param member ID of the member 
	 * @param line The geometric shape representing the membership relation
	 * @param logWriter Used for producing logs
	 */
	public MembershipPrefs(String ensembleName, String coordinator,
			String member, Line line, Writer logWriter) {
		this.ensembleName = ensembleName;
		this.coordinator = coordinator;
		this.member = member;
		this.line = line;
		this.logWriter = logWriter;
		initVisibilityListener();
	}
	
	/**
	 * Blocks any attempt to make the {@link MembershipPrefs#line} visible.
	 * Moreover, the class remembers the value of "newValue" in the last call 
	 * of the {@link ChangeListenerWithMemory#changed} 
	 */
	private class ChangeListenerWithMemory implements ChangeListener<Boolean>{
		
		/**
		 * Value of "newValue" in the last call of the 
		 * {@link ChangeListenerWithMemory#changed}
		 */
		private boolean lastValue;

		/**
		 * @return Value of "newValue" in the last call of the 
		 * {@link ChangeListenerWithMemory#changed}
		 * @see {@link ChangeListenerWithMemory#lastValue}
		 */
		public boolean getLastValue() {
			return lastValue;
		}

		/**
		 * Sets the last encountered value of the visibility property of
		 * {@link MembershipPrefs#line}. It is usually set when this listener
		 * is activated, and the value is the property value at that time.
		 * @param lastValue The last encountered value of the visibility property of
		 * {@link MembershipPrefs#line}
		 */
		public void setLastValue(boolean lastValue) {
			this.lastValue = lastValue;
		}

		@Override
		public void changed(ObservableValue<? extends Boolean> arg0,
				Boolean oldValue, Boolean newValue) {
			lastValue = newValue;
			if (newValue == true){
				line.setVisible(false);
			}
		}
		
	}
	
	/**
	 * Initializes the value corresponding to {@link MembershipPrefs#line} in
	 * {@link MembershipPrefs#visibilityListeners}.
	 */
	private void initVisibilityListener(){
		if (!visibilityListeners.containsKey(line)){
			ChangeListenerWithMemory listener = new ChangeListenerWithMemory();
			visibilityListeners.put(line, listener);
		}
	}
	
	/**
	 * @return The current color of the membership relation visualization
	 */
	public Paint getColor(){
		if (line == null){
			return null;
		} else {
			return line.getStroke();
		}
	}
	
	/**
	 * Sets the color of the membership relation visualization
	 * @param color The new color
	 */
	public void setColor(Paint color){
		if ((line != null) && (line instanceof Shape)){
			line.setStroke(color);
			log("Color of the specified ensemble membership was set to " + color);
		}
	}
	
	/**
	 * Logs the specified text, using {@link MembershipPrefs#logWriter}
	 * @param text The text to be logged
	 */
	private void log(String text){
		if (logWriter != null){
			try {
				logWriter.append(text);
				logWriter.append("\n");
				logWriter.flush();
			} catch (IOException ex) {}
		}
	}
	
	/**
	 * For each {@link Line} representing a membership relation, this map holds
	 * a corresponding {@link ChangeListener} that ensures the {@link Line} is not
	 * shown in the visualization.
	 * The {@link Line} objects can't be simply marked as invisible, because the
	 * visible property is manipulated continuously by the visualization timeline. 
	 * Thus, this listener-approach is used.
	 */
	private static final Map<Line,ChangeListenerWithMemory> visibilityListeners = new HashMap<>();
	
	/**
	 * For each line it marks if it's in the "visible" or "invisible" mode.
	 * If the value for the given line has not been initialized yet, it
	 * should be interpreted as if a positive value is present. 
	 */
	private static final Map<Line,Boolean> visibility = new HashMap<>();
	
	/**
	 * Specifies, whether the membership relation should be shown in the visualization
	 * @param visible If false, the membership relation won't be shown in the visualization.
	 */
	@Override
	public void setVisible(boolean visible){
		if (line != null){
			ChangeListenerWithMemory listener = visibilityListeners.get(line);
			line.visibleProperty().removeListener(listener);
			if (!visible){
				boolean wasVisible = line.isVisible();
				line.visibleProperty().addListener(listener);
				line.setVisible(false);
				listener.setLastValue(wasVisible);
				visibility.put(line, false);
			} else {
				if (!line.isVisible()){
					line.setVisible(listener.getLastValue());
				}
				visibility.put(line, true);
			}
			log("Visibility of the specified ensemble membership was set to " + visible);
		}
	}

	/**
	 * A check for visibility of this ensemble visualization
	 */
	@Override
	public boolean isVisible() {
		if (line == null){
			return false;
		} else {
			Boolean val = visibility.get(line);
			if (val == null){
				return true;
			} else {
				return val;
			}
		}
	}

}
