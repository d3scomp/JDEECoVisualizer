package cz.filipekt.jdcv.network;

import java.util.List;

/**
 * Represents a "person" XML element from the source XML file containing plans
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class MyPerson {
	
	/**
	 * A unique id of the person
	 */
	private final String id;
	
	/**
	 * Sex of the person.
	 * 'true' means male, 'false' means female, null means unspecified
	 */
	private final Boolean sex;
	
	/**
	 * Age of the person (in years)
	 * Null value means the age is not specified.
	 */
	private final Integer age;
	
	/**
	 * Defines if this person holds a drivers-license.
	 * Null value = it is not specified if the person holds a license
	 */
	private final Boolean license;
	
	/**
	 * Defines if this person has access to a car (always, sometimes or never).
	 * Possible values: "always", "never", "sometimes" 
	 */
	private final String carAvail;
	
	/**
	 * Defines if this person has a job.
	 * Null value = it is not specified if the person has a job
	 */
	private final Boolean employed;
	
	/**
	 * All the plans this person has.
	 */
	private final List<MyPlan> plans;

	/**
	 * @return A unique id of the person
	 * @see {@link MyPerson#id}
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return Sex of the person. 'true' means male, 'false' means female, null means unspecified
	 * @see {@link MyPerson#sex}
	 */
	public Boolean getSex() {
		return sex;
	}

	/**
	 * @return Age of the person (in years). Null value means the age is not specified.
	 * @see {@link MyPerson#age}
	 */
	public Integer getAge() {
		return age;
	}

	/**
	 * @return True if this person holds a drivers-license. Null value = it is not specified if the person holds a license.
	 * @see {@link MyPerson#license}
	 */
	public Boolean getLicense() {
		return license;
	}

	/**
	 * @return Defines when this person has access to a car. Possible values: "always", "never", "sometimes".
	 * @see {@link MyPerson#carAvail} 
	 */
	public String getCarAvail() {
		return carAvail;
	}

	/**
	 * @return True if this person has a job. Null value = it is not specified if the person has a job.
	 * @see {@link MyPerson#employed}
	 */
	public Boolean getEmployed() {
		return employed;
	}
	
	/**
	 * @return All the plans this person has.
	 * @see {@link MyPerson#plans}
	 */
	public List<MyPlan> getPlans() {
		return plans;
	}

	/**
	 * @param id A unique id of the person
	 * @param sex Sex of the person. 'true' means male, 'false' means female, null means unspecified
	 * @param age Age of the person (in years). Null value means the age is not specified.
	 * @param license True if this person holds a drivers-license. Null value = it is not specified if the person holds a license.
	 * @param carAvail Defines when this person has access to a car. Possible values: "always", "never", "sometimes".
	 * @param employed True if this person has a job. Null value = it is not specified if the person has a job.
	 * @param plans All the plans this person has.
	 */
	public MyPerson(String id, Boolean sex, Integer age, Boolean license,
			String carAvail, Boolean employed, List<MyPlan> plans) {
		this.id = id;
		this.sex = sex;
		this.age = age;
		this.license = license;
		this.carAvail = carAvail;
		this.employed = employed;
		this.plans = plans;
	}
}
