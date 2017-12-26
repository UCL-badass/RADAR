package uk.ac.ucl.cs.radar.jmetal.variation.operators;

import java.util.Arrays;
import java.util.Collections;

import org.uma.jmetal.operator.MutationOperator;
import uk.ac.ucl.cs.radar.jmetal.solution.encoding.SolutionEncoding;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

@SuppressWarnings("serial")
public class Mutation implements MutationOperator<SolutionEncoding> {
	  private double mutationProbability ;
	  private JMetalRandom randomGenerator ;

	  /** Constructor */
	  public Mutation(double mutationProbability) {
	    if (mutationProbability < 0) {
	      throw new JMetalException("Mutation probability is negative: " + mutationProbability) ;
	    }
	    this.mutationProbability = mutationProbability;
	    randomGenerator = JMetalRandom.getInstance() ;
	  }

	  /* Getter */
	  public double getMutationProbability() {
	    return mutationProbability;
	  }


	  /** Execute() method */
	  @Override
	  public SolutionEncoding execute(SolutionEncoding solution) {
	    if (null == solution) {
	      throw new JMetalException("Null parameter") ;
	    }

	    doMutation(mutationProbability, solution);
	    return solution;
	  }

	  /**
	   * Perform the mutation operation
	   *
	   * @param probability Mutation setProbability
	   * @param solution    The solution to mutate
	   */
	  public void doMutation(double probability, SolutionEncoding solution)  { 
		  for (int i = 0; i < solution.getDecisionVariables().size(); i++) {				
				if (randomGenerator.nextDouble() < probability) {
					Collections.shuffle(Arrays.asList(solution.getDecisionVariables().get(i)));
				}
			}
	  }
	}

