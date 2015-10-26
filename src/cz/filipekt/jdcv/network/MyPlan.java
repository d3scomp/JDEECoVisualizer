package cz.filipekt.jdcv.network;

import java.util.List;

/**
 * Each person holds at least one plan.  A plan holds a sequence of 
 * act-leg-act-leg... tags.  Such a chain starts and ends with an act(ivity).  
 * Between two acts there is exactly one leg. These rules allow for storing
 * the activities and legs separately in respective lists. The correct
 * order of the elements can be then reconstructed using the mentioned
 * act-leg-act-leg... rule.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class MyPlan {
	
	/**
	 * If a person has more than one plan, one is marked as the selected plan, 
	 * i.e., the one that was used by the simulation.
	 */
	private final boolean selected;
	
	/**
	 * All the activities this plan contains, in the correct order.
	 */
	private final List<MyAct> activities;
	
	/**
	 * All the legs this plan contains, in the correct order.
	 */
	private final List<MyLeg> legs;

	/**
	 * @param selected If a person has more than one plan, one is marked as the selected plan
	 * @param activities All the activities this plan contains, in the correct order.
	 * @param legs All the legs this plan contains, in the correct order.
	 */
	public MyPlan(boolean selected, List<MyAct> activities, List<MyLeg> legs) {
		this.selected = selected;
		this.activities = activities;
		this.legs = legs;
	}

	/**
	 * @return If a person has more than one plan, one is marked as the selected plan, 
	 * i.e., the one that was used by the simulation.
	 * @see {@link MyPlan#selected}
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @return All the activities this plan contains, in the correct order.
	 * @see {@link MyPlan#activities}
	 */
	public List<MyAct> getActivities() {
		return activities;
	}

	/**
	 * @return All the legs this plan contains, in the correct order.
	 * @see {@link MyPlan#legs}
	 */
	public List<MyLeg> getLegs() {
		return legs;
	}
}
