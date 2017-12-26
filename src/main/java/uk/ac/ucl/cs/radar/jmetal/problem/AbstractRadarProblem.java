package uk.ac.ucl.cs.radar.jmetal.problem;

import java.util.List;


import uk.ac.ucl.cs.radar.jmetal.problem.IGenericRadarProblem;

import org.uma.jmetal.problem.impl.AbstractGenericProblem;

import uk.ac.ucl.cs.radar.jmetal.solution.encoding.DefaultSolutionEncoding;
import uk.ac.ucl.cs.radar.jmetal.solution.encoding.SolutionEncoding;

@SuppressWarnings("serial")
public abstract class AbstractRadarProblem extends AbstractGenericProblem<SolutionEncoding>
implements IGenericRadarProblem {

	private List<Integer> lowerLimit ;
	private List<Integer> upperLimit ;
	private List<Integer[]> decisionVariables;
	
	/* Getters */
	@Override
	public Integer getUpperBound(int index) {
		return upperLimit.get(index);
	}

	@Override
	public Integer getLowerBound(int index) {
		return lowerLimit.get(index);
	}
	@Override
	public List<Integer[]> getDecisionVariables(){
		return decisionVariables;
	}
	@Override
	public void setDecisionVariables(List<Integer[]> decisionVariables){
		this.decisionVariables = decisionVariables;
	}
	
	/* Setters */
	protected void setLowerLimit(List<Integer> lowerLimit) {
	  this.lowerLimit = lowerLimit;
	}
	
	protected void setUpperLimit(List<Integer> upperLimit) {
	  this.upperLimit = upperLimit;
	}
	
	
	
	@Override
	public SolutionEncoding createSolution() {
	  return new DefaultSolutionEncoding(this);
	}
}