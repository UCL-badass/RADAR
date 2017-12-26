//  IBEARunner.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package uk.ac.ucl.cs.radar.sbse;

import org.uma.jmetal.algorithm.multiobjective.ibea.IBEA;
import uk.ac.ucl.cs.radar.sbse.IBEABuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.runner.AbstractAlgorithmRunner;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;

import uk.ac.ucl.cs.radar.jmetal.problem.GenericRadarProblem;
import uk.ac.ucl.cs.radar.jmetal.solution.encoding.SolutionEncoding;
import uk.ac.ucl.cs.radar.jmetal.variation.operators.CrossOver;
import uk.ac.ucl.cs.radar.jmetal.variation.operators.Mutation;
import uk.ac.ucl.cs.radar.model.Model;
import uk.ac.ucl.cs.radar.utilities.Helper;

import java.util.List;

/**
 * Class for configuring and running the IBEA algorithm
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class IBEARunner extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments.
   * @throws java.io.IOException
   * @throws SecurityException
   * @throws ClassNotFoundException
   * Invoking command:
  java org.uma.jmetal.runner.multiobjective.IBEARunner problemName [referenceFront]
   */
  public static void main(String[] args) throws Exception {
    //Problem<DoubleSolution> problem;
    IBEA<SolutionEncoding> algorithm;
    CrossoverOperator<SolutionEncoding> crossover;
    MutationOperator<SolutionEncoding> mutation;
    SelectionOperator<List<SolutionEncoding>, SolutionEncoding> selection;

    String problemName ;
    String referenceParetoFront = "" ;
    if (args.length == 1) {
      problemName = args[0];
    } else if (args.length == 2) {
      problemName = args[0] ;
      referenceParetoFront = args[1] ;
    } else {
    	///Users/INTEGRALSABIOLA/Documents/JavaProject/
        problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT1";
        referenceParetoFront = "/Users/INTEGRALSABIOLA/Documents/JavaProject/jMetal/jmetal-problem/src/test/resources/pareto_fronts/ZDT1.pf" ;

    }


	String  model = "./src/main/resources/uk.ac.ucl.cs.examples/BSS/BSS.rdr";
	Model m;
	try {
		m = Helper.loadModel(model, 10000, "ENB", "ENB");
	} catch (Exception e) {
		throw new JMetalException("Unable to load model.");
	}
	GenericRadarProblem problem = new GenericRadarProblem (m, false, "");
	
	//problemList.add(new ExperimentProblem<>(CbaProblem));
    //problem = ProblemUtils.loadProblem(problemName);

    double crossoverProbability = 0.9 ;
    double crossoverDistributionIndex = 20.0 ;
    crossover = new CrossOver(crossoverProbability) ;

    double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
    double mutationDistributionIndex = 20.0 ;
    mutation = new Mutation(mutationProbability) ;

    selection = new BinaryTournamentSelection<SolutionEncoding>() ;

    algorithm = new IBEABuilder(problem)
      .setArchiveSize(100)
      .setPopulationSize(100)
      .setMaxEvaluations(25000)
      .setCrossover(crossover)
      .setMutation(mutation)
      .setSelection(selection)
      .build() ;

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
      .execute() ;

    List<SolutionEncoding> population = algorithm.getResult() ;
    long computingTime = algorithmRunner.getComputingTime() ;

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);
    if (!referenceParetoFront.equals("")) {
      printQualityIndicators(population, referenceParetoFront) ;
    }
  }
}
