package cz.filipekt.jdcv.plugins;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import cz.filipekt.jdcv.util.Resources;

/**
 * The side panel used for viewing detailed information about selected visualized components
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class InfoPanel implements Plugin {

	/**
	 * Short name of the panel
	 */
	private final String pluginName = "Info";
	
	/**
	 * @return Short name of the panel
	 */
	@Override
	public String getName() {
		return pluginName;
	}

	/**
	 * @return The thumbnail shown right next to the panel title in the panel-switching button
	 * @throws IOException If the image couldn't be found. This should never happen unless the
	 * application resources directory has been messed with
	 */
	@Override
	public InputStream getThumbnail() throws IOException {
		return Resources.getResourceInputStream("info-2.png");
	}

	/**
	 * @return This panel will view the information contained in {@link InfoPanel#title} and 
	 * {@link InfoPanel#pairs}
	 */
	@Override
	public Node getPanel() {
		return pane;
	}
	
	/**
	 * Specifies the textual data to be viewed by the info panel.
	 * @param title Title of the data to be viewed
	 * @param pairs Set of key-value pairs to be viewed
	 */
	public void setInfo(String title, Map<String,String> pairs){
		this.title = title;
		this.pairs = pairs;
		putInfoInPanel();
	}
	
	/**
	 * Title of the data to be viewed
	 */
	private String title;
	
	/**
	 * Set of key-value pairs to be viewed inside {@link InfoPanel#panel}
	 */
	private Map<String,String> pairs;
	
	/**
	 * The area to hold the textual information contained in {@link InfoPanel#pairs}
	 */
	private final TextArea area = new TextArea();
	
	/**
	 * Scrollable wrapper for {@link InfoPanel#area}
	 */
	private final ScrollPane pane = new ScrollPane();

	/**
	 * This is a singleton class, so uncontrolled instantiation is forbidden
	 */
	private InfoPanel() {
		area.setEditable(false);
		pane.setContent(area);
		pane.setFitToHeight(true);
		pane.setFitToWidth(true);
	}
	
	/**
	 * The singleton instance of this class
	 */
	private static final InfoPanel INSTANCE = new InfoPanel();
	
	/**
	 * @return The singleton instance of this class
	 */
	public static InfoPanel getInstance(){
		return INSTANCE;
	}

	/**
	 * Takes the data from {@link InfoPanel#title} and {@link InfoPanel#pairs} and
	 * views them inside the {@link InfoPanel#panel}
	 */
	private void putInfoInPanel(){
		StringBuilder sb = new StringBuilder();
		if (title != null){
			sb.append(title);
			sb.append("\n");
		}
		if (pairs != null){
			for (String key : pairs.keySet()){
				sb.append("[");
				sb.append(key);
				sb.append("] ");
				sb.append(pairs.get(key));
				sb.append("\n");
			}
		}
		area.clear();
		area.setText(sb.toString());
	}
	
}
