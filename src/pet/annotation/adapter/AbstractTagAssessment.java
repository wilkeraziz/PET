package pet.annotation.adapter;

import pet.annotation.Assessment;

public abstract class AbstractTagAssessment implements Assessment {

	private final String facade;
	private final String details;
	
	protected AbstractTagAssessment(final String facade, final String details){
		this.facade = facade;
		this.details = details;
	}
	
	@Override
	public String toString(){
		return facade;
	}
	
	public String getDetails(){
		return details;
	}
	
}
