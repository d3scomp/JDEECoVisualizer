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
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * @author Ilias Gerostathopoulos <iliasg@d3s.mff.cuni.cz>
 */
public class JumpToHelper {

	public static void jumpToPointInTimeline(MapScene scene, String timeStr) {
		double time = Double.parseDouble(timeStr) * 1000;
		double timeVal = scene.convertToVisualizationTime(time);
		Duration duration = new Duration(timeVal);
		Timeline timeline = scene.getTimeLine();

		if ((duration.greaterThan(timeline.getTotalDuration())) || (duration.lessThan(new Duration(0)))) {
			System.out.println("Please specify a number between 0 and "
					+ (Math.round(scene.convertToSimulationTime(timeline.getTotalDuration().toMillis()) / 1000) - 1));
		} else {
			timeline.jumpTo(duration);
		}
	}
}