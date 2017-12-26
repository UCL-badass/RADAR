package uk.ac.ucl.cs.radar.jmetal.problem;

import java.util.List;

import org.uma.jmetal.problem.Problem;

import uk.ac.ucl.cs.radar.jmetal.solution.encoding.SolutionEncoding;
import uk.ac.ucl.cs.radar.model.Model;
import uk.ac.ucl.cs.radar.model.ProblemType;

public interface IGenericRadarProblem extends Problem<SolutionEncoding> {
	  public Integer getLowerBound(int index) ;
	  public Integer getUpperBound(int index) ;
	  public List<Integer[]> getDecisionVariables();
	  public void setDecisionVariables(List<Integer[]> decisionVariables);
	  public ProblemType getProblemType();
	  public void setProblemType(ProblemType problemType);
	  public Model getSemanticModel ();
	  
}

