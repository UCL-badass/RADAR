package uk.ac.ucl.cs.radar.jmetal.variation.operators;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.operator.CrossoverOperator;
import uk.ac.ucl.cs.radar.jmetal.solution.encoding.SolutionEncoding;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;


@SuppressWarnings("serial")
public class CrossOver implements CrossoverOperator<SolutionEncoding> {
	private double crossoverProbability ;
	  private JMetalRandom randomGenerator ;

	  /** Constructor */
	  public CrossOver(double crossoverProbability) {
	    if (crossoverProbability < 0) {
	      throw new JMetalException("Crossover probability is negative: " + crossoverProbability) ;
	    }
	    this.crossoverProbability = crossoverProbability;
	    randomGenerator = JMetalRandom.getInstance() ;
	  }

	  /* Getter */
	  public double getCrossoverProbability() {
	    return crossoverProbability;
	  }

	  @Override
	  public List<SolutionEncoding> execute(List<SolutionEncoding> solutions) {
	    if (solutions == null) {
	      throw new JMetalException("Null parameter") ;
	    } else if (solutions.size() != 2) {
	      throw new JMetalException("There must be two parents instead of " + solutions.size()) ;
	    }

	    return doCrossover(crossoverProbability, solutions.get(0), solutions.get(1)) ;
	  }

	  /**
	   * Perform the crossover operation.
	   *
	   * @param probability Crossover setProbability
	   * @param parent1     The first parent
	   * @param parent2     The second parent
	   * @return An array containing the two offspring
	   */
	  public List<SolutionEncoding> doCrossover(double probability, SolutionEncoding parent1, SolutionEncoding parent2)  {
	    List<SolutionEncoding> offspring = new ArrayList<>(2);
		//offspring.add((SolutionEncoding) parent1.clone()) ;
		//offspring.add((SolutionEncoding) parent2.clone()) ;
	    offspring.add((SolutionEncoding) parent1.copy()) ;
	    offspring.add((SolutionEncoding) parent2.copy()) ;
	    int crossoverPoint = randomGenerator.nextInt(0, parent1.getNumberOfVariables() - 1);
        for (int i = crossoverPoint; i < parent1.getNumberOfVariables(); i++) {
        	Integer[] valueX1 = (Integer[])parent1.getDecisionVariables().get(i).clone();
        	Integer[] valueX2 = (Integer[])parent2.getDecisionVariables().get(i).clone();
        	offspring.get(0).getDecisionVariables().set(i, valueX2);
        	offspring.get(1).getDecisionVariables().set(i, valueX1);
        } // for
	    return offspring;
	  }

	  /**
	   * Two parents are required to apply this operator.
	   * @return
	   */
	  public int getNumberOfParents() {
	    return 2 ;
	  }
	}
