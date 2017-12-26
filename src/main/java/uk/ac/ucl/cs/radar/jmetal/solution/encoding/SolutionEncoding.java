package uk.ac.ucl.cs.radar.jmetal.solution.encoding;

import java.util.List;

import org.uma.jmetal.solution.Solution;

public interface SolutionEncoding extends Solution<Integer> {
	  public int getLowerBound(int index) ;
	  public int getUpperBound(int index) ;
	  public List<Integer[]> getDecisionVariables ();
}