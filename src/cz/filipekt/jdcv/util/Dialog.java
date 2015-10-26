package cz.filipekt.jdcv.util;

import java.util.ArrayList;
import java.util.List;

import cz.filipekt.jdcv.Visualizer;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Allows for showing simple dialogs to the user. The dialogs show textual information
 * and the user then clicks the OK button. 
 * 
 *  @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class Dialog {
	
	/**
	 * Type of the dialogs that are produced by {@link Visualizer#showDialog(Type, String)} 
	 */
	public static enum Type {
		INFO, SUCCESS, ERROR;
	}
	
	/**
	 * Maximum number of characters in a single line of the messages that are shown by
	 * the dialog produced in {@link Visualizer#showDialog(Type, String...)}
	 */
	private static final int dialogMessageLineLength = 128;
	
	/**
	 * Width and height of the illustrative icon shown inside the dialog that
	 * is shown with {@link Visualizer#showDialog(Type, String)}
	 */
	private static final int dialogIconSize = 64;
	
	/**
	 * Loads the graphics corresponding to the dialog type specified by
	 * the parameter.
	 * @param type Dialog type determining the image to be loaded
	 * @return Graphics corresponding to the dialog type specified by
	 * the parameter
	 */
	private static ImageView loadGraphic(Dialog.Type type){
		String imageResource = null;
		switch(type){
			case ERROR:
				imageResource = "error.png";
				break;
			case INFO:
				imageResource = "info.png";
				break;
			case SUCCESS:
				imageResource = "success.png";
				break;			
		}
		if (imageResource == null){
			return new ImageView();
		} else {
			return Resources.getImageView(imageResource, dialogIconSize);
		}
	}
	
	/**
	 * Shows a simple dialog to the user, containing a text message. 
	 * User only has to click the OK button.
	 * <b>Warning</b>: this method must be called inside the JavaFX thread
	 * @param type Type of the dialog, i.e. error, success etc.
	 * @param onClicked Code to be invoked when the OK button is clicked
	 * @param messages Each message will we shown as a single line in the dialog
	 */
	public static void show(Dialog.Type type, final Runnable onClicked, String... messages){
		if ((type == null) || (messages == null) || (messages.length == 0)){
			return;
		}
		cropMessages(messages);
		ImageView image = loadGraphic(type);
		List<Label> labels = new ArrayList<>();
		labels.add(new Label());
		for (String msg : messages){
			Label label = new Label(msg);
			labels.add(label);
		}
		labels.add(new Label());
		Button okButton = new Button("OK");
		GridPane gridPane = new GridPane();		
		GridPane.setHalignment(okButton, HPos.CENTER);
		GridPane.setValignment(image, VPos.CENTER);			
		int row = 0;
		gridPane.add(image, 0, 0, 1, labels.size());
		for (Label lab : labels){
			GridPane.setMargin(lab, new Insets(0, 20, 0, 20));
			gridPane.add(lab, 1, row);
			row += 1;
		}
		gridPane.add(okButton, 1, row);
		Scene scene = new Scene(gridPane);
		final Stage dialog = new Stage();
		okButton.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event arg0) {
				dialog.close();
				if (onClicked != null){
					onClicked.run();
				}
			}
		});
		dialog.initStyle(StageStyle.UTILITY);
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.setScene(scene);
		dialog.sizeToScene();
		dialog.show();
	}
	
	/**
	 * Wrapper for {@link Dialog#show(Type, Runnable, String...).
	 * It works exactly the same as calling {@code Dialog.show(type, null, messages)} 
	 * @param type Type of the dialog, i.e. error, success etc.
	 * @param messages Each message will we shown as a single line in the dialog
	 */
	public static void show(Dialog.Type type, String... messages){
		Dialog.show(type, null, messages);
	}
	
	/**
	 * Makes sure that the given Strings contain at most {@link Visualizer#dialogMessageLineLength}
	 * characters each. If any of them is longer, it is replaced with a cropped version.
	 * @param messages These Strings are checked for their length.
	 */
	private static void cropMessages(String[] messages){
		if (messages != null){
			for (int i = 0; i < messages.length; i++){
				String msg = messages[i];
				if ((msg != null) && (msg.length() > dialogMessageLineLength)){
					String newMsg = msg.substring(0, dialogMessageLineLength) + "...";
					messages[i] = newMsg;
				}
			}
		}
	}

}