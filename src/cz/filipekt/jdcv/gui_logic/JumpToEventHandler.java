/*******************************************************************************
 * Copyright 2015 Charles University in Prague
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *******************************************************************************/
package cz.filipekt.jdcv.gui_logic;

import cz.filipekt.jdcv.MapScene;
import cz.filipekt.jdcv.Visualizer;
import javafx.animation.Timeline;
import javafx.animation.Animation.Status;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

/**
 * @author Ilias Gerostathopoulos <iliasg@d3s.mff.cuni.cz>
 */
public class JumpToEventHandler implements EventHandler<Event>{

	/**
	 * Context in which this handler is called
	 */
	private final Visualizer visualizer;

	/**
	 * @param visualizer Context in which this handler is called
	 */
	public JumpToEventHandler(Visualizer visualizer) {
		this.visualizer = visualizer;
	}

	@Override
	public void handle(Event event) {
		MapScene scene = visualizer.getScene();
		if (scene != null){
			String timeStr = visualizer.getTimeField().getText();
			if ((timeStr != null) && (!timeStr.equals(""))) {
				JumpToHelper.jumpToPointInTimeline(scene,timeStr);
			}
		}
	} 

}
