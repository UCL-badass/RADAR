package uk.ac.ucl.cs.radar.model;

public abstract class Constraint {

	ConstraintArgument leftArg;
	ConstraintArgument rightArg;
	void setLeftConstraintArgumnet (ConstraintArgument leftArg){
		this.leftArg = leftArg;
	}
	ConstraintArgument getLeftConstraintArgumnet (){
		return leftArg;
	}
	void setRightConstraintArgumnet (ConstraintArgument rightArg){
		this.rightArg = rightArg;
	}
	ConstraintArgument getRightConstraintArgumnet (){
		return rightArg;
	}
}
