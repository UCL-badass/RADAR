//  IBEA.java
//
//  Author:
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Juan J. Durillo
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

// This implementation is based on the PISA code:
// http://www.tik.ee.ethz.ch/sop/pisa/selectors/ibea/?page=ibea.php

package uk.ac.ucl.cs.radar.sbse;

import org.uma.jmetal.algorithm.multiobjective.ibea.IBEA;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmBuilder;

import uk.ac.ucl.cs.radar.jmetal.solution.encoding.SolutionEncoding;
import uk.ac.ucl.cs.radar.jmetal.variation.operators.CrossOver;
import uk.ac.ucl.cs.radar.jmetal.variation.operators.Mutation;

import java.util.List;

/**
 * This class implements the IBEA algorithm
 */
public class IBEABuilder implements AlgorithmBuilder<IBEA<SolutionEncoding>> {
  private Problem<SolutionEncoding> problem;
  private int populationSize;
  private int archiveSize;
  private int maxEvaluations;

  private CrossoverOperator<SolutionEncoding> crossover;
  private MutationOperator<SolutionEncoding> mutation;
  private SelectionOperator<List<SolutionEncoding>, SolutionEncoding> selection;

  /**
   * Constructor
   * @param problem
   */
  public IBEABuilder(Problem<SolutionEncoding> problem) {
    this.problem = problem;
    this.populationSize = 100;
    this.archiveSize = 100;
    this.maxEvaluations = 25000;

    double crossoverProbability = 0.9;
    crossover = new CrossOver(crossoverProbability);

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    mutation = new Mutation(mutationProbability);

    selection = new BinaryTournamentSelection<SolutionEncoding>();
  }
  /**
   * Constructor
   * @param problem
   */
  public IBEABuilder(Problem<SolutionEncoding> problem, int populationSize, int archiveSize, int maxEvaluatotion, 
		  double crossoverProb) {
    this.problem = problem;
    this.populationSize = populationSize;
    this.archiveSize = archiveSize;
    this.maxEvaluations = maxEvaluatotion;

    double crossoverProbability = crossoverProb;
    crossover = new CrossOver(crossoverProbability);

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    mutation = new Mutation(mutationProbability);

    selection = new BinaryTournamentSelection<SolutionEncoding>();
  }

  /* Getters */
  public int getPopulationSize() {
    return populationSize;
  }

  public int getArchiveSize() {
    return archiveSize;
  }

  public int getMaxEvaluations() {
    return maxEvaluations;
  }

  public CrossoverOperator<SolutionEncoding> getCrossover() {
    return crossover;
  }

  public MutationOperator<SolutionEncoding> getMutation() {
    return mutation;
  }

  public SelectionOperator<List<SolutionEncoding>, SolutionEncoding> getSelection() {
    return selection;
  }

  /* Setters */
  public IBEABuilder setPopulationSize(int populationSize) {
    this.populationSize = populationSize;

    return this;
  }

  public IBEABuilder setArchiveSize(int archiveSize) {
    this.archiveSize = archiveSize;

    return this;
  }

  public IBEABuilder setMaxEvaluations(int maxEvaluations) {
    this.maxEvaluations = maxEvaluations;

    return this;
  }

  public IBEABuilder setCrossover(CrossoverOperator<SolutionEncoding> crossover) {
    this.crossover = crossover;

    return this;
  }

  public IBEABuilder setMutation(MutationOperator<SolutionEncoding> mutation) {
    this.mutation = mutation;

    return this;
  }

  public IBEABuilder setSelection(SelectionOperator<List<SolutionEncoding>, SolutionEncoding> selection) {
    this.selection = selection;

    return this;
  }

  public IBEA<SolutionEncoding> build() {
    return new IBEA<SolutionEncoding>(problem, populationSize, archiveSize, maxEvaluations, selection, crossover,
        mutation);
  }
}
