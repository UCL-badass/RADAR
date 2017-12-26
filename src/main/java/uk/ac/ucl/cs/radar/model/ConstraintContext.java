package uk.ac.ucl.cs.radar.model;

public class ConstraintContext {

	private IConstraint  constraint;
	public ConstraintContext(IConstraint constraint){
		this.constraint =constraint;
	}
	public void executeStrategy (Solution s, Model m){
		constraint.isSolutionValid(s, m);
	}
}
