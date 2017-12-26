package uk.ac.ucl.cs.radar.sbse;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import uk.ac.ucl.cs.radar.jmetal.variation.operators.CrossOver;
import  uk.ac.ucl.cs.radar.jmetal.variation.operators.Mutation;
import  uk.ac.ucl.cs.radar.jmetal.problem.GenericRadarProblem;
import  uk.ac.ucl.cs.radar.jmetal.solution.encoding.DefaultSolutionEncoding;
import  uk.ac.ucl.cs.radar.jmetal.solution.encoding.SolutionEncoding;
import  uk.ac.ucl.cs.radar.model.Decision;
import  uk.ac.ucl.cs.radar.model.Model;
import  uk.ac.ucl.cs.radar.model.Solution;
import  uk.ac.ucl.cs.radar.utilities.JmetalUtil;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.ibea.IBEA;
import org.uma.jmetal.algorithm.multiobjective.mocell.MOCell;
import org.uma.jmetal.algorithm.multiobjective.mocell.MOCellBuilder;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.randomsearch.RandomSearch;
import org.uma.jmetal.algorithm.multiobjective.randomsearch.RandomSearchBuilder;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Builder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;

public class SbseAlgorithm {

	String algorithmName;
	Model model;
	Problem<SolutionEncoding> problem;
    Algorithm<List<SolutionEncoding>> algorithm;
    CrossoverOperator<SolutionEncoding> crossover;
    MutationOperator<SolutionEncoding> mutation;
    SelectionOperator<List<SolutionEncoding>, SolutionEncoding> selection;
    String referenceParetoFront = "/Users/INTEGRALSABIOLA/Documents/JavaProject/jMetal/jmetal-problem/src/test/resources/pareto_fronts/ZDT1.pf" ;
    AlgorithmRunner algorithmRunner;
    List<SolutionEncoding> optimalSolutions; 
    List<SolutionEncoding> allEvaluatedSolutions; 
    long computingTime;
    public SbseAlgorithm(Model semanticModel,boolean addConstraintAsObjective, String runTimePath){
		model = semanticModel;
    	this.problem = new GenericRadarProblem (semanticModel, addConstraintAsObjective,runTimePath);
    	setSelection();
    	optimalSolutions = new ArrayList<>();
    	allEvaluatedSolutions = new ArrayList<>();
	}
	void setReferenceFront (String referenceFront){
		this.referenceParetoFront = referenceFront;
	}
	void setCrossOver (double crossoverProb){
	    this.crossover = new CrossOver(crossoverProb);
	}
	void setMutation (double mutationProb){
	    this.mutation = new Mutation(mutationProb);
	}
	void setSelection (){
	    this.selection = new BinaryTournamentSelection<SolutionEncoding>(
	            new RankingAndCrowdingDistanceComparator<SolutionEncoding>());
	}
	public void buildAlgorithm (String name, int maxEvaluation, int populationSize){
		algorithmName = name;
		if (name.equals("NSGAII")){
			 this.algorithm = buildNSGAII(maxEvaluation,populationSize) ;
		}else if (name.equals("SPEA2")){
			this.algorithm = buildSPEA2(maxEvaluation,populationSize) ;
		}else if (name.equals("MOCell")){
			this.algorithm = buildMoCell(maxEvaluation,populationSize) ;
		}else if (name.equals("RandomSearch")){
			this.algorithm = buildRandom() ;
		}else if (name.equals("IBEA")){
			this.algorithm = buildIBEA(maxEvaluation,populationSize) ;
		}
		
	}
	
	private Algorithm<List<SolutionEncoding>> buildNSGAII (int maxEvaluation, int populationSize){
		Algorithm<List<SolutionEncoding>> algorithm = new NSGAIIBuilder<SolutionEncoding>(problem, crossover, mutation)
		        .setSelectionOperator(selection)
		        .setMaxEvaluations(maxEvaluation)
		        .setPopulationSize(populationSize)
		        .build() ; 
		return algorithm;
	}
	private Algorithm<List<SolutionEncoding>> buildSPEA2 (int maxEvaluation, int populationSize){
		Algorithm<List<SolutionEncoding>> algorithm = new SPEA2Builder<SolutionEncoding>(problem, crossover, mutation)
		        .setSelectionOperator(selection)
		        .setMaxIterations(maxEvaluation/populationSize)
		        .setPopulationSize(populationSize)
		        .build() ; 
		return algorithm;
	}
	private Algorithm<List<SolutionEncoding>> buildMoCell (int maxEvaluation, int populationSize){
		Algorithm<List<SolutionEncoding>> algorithm = new MOCellBuilder<SolutionEncoding>(problem, crossover, mutation)
		        .setSelectionOperator(selection)
		        .setMaxEvaluations(maxEvaluation)
		        .setPopulationSize(populationSize)
		        .build() ; 
		return algorithm;
	}
	private Algorithm<List<SolutionEncoding>> buildIBEA (int maxEvaluation, int populationSize){
		IBEA<SolutionEncoding> algorithm = new IBEABuilder(problem) 
		.setArchiveSize(populationSize)
	    .setPopulationSize(populationSize)
	    .setMaxEvaluations(maxEvaluation)
	    .setSelection(selection)
	    .build() ;
		return algorithm;
	}
	private Algorithm<List<SolutionEncoding>> buildRandom (){
		Algorithm<List<SolutionEncoding>> algorithm = new RandomSearchBuilder<SolutionEncoding>(problem)
		        .build() ; 
		return algorithm;
	}
	public Map<Solution, double[]> solve (){
		algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute() ;
		optimalSolutions = algorithm.getResult() ;
		
	    computingTime = algorithmRunner.getComputingTime() ;
	    return getOptimalSolutions();
	}
	public Map<Solution, double[]> getOptimalSolutions (){
		return convertJmetalSolutionsToRadarEquivalent(optimalSolutions);
	}
	public Map<Solution, double[]> convertJmetalSolutionsToRadarEquivalent (List<SolutionEncoding> optimalSolutions){
		Map<Solution, double[]> result = new LinkedHashMap<Solution, double[]>();
		int j =0;
		for (SolutionEncoding solution: optimalSolutions){
			System.out.println ("Solution: " + ((DefaultSolutionEncoding)solution).getVariableValueString(j));
			Map <Decision, List<String>> selection = JmetalUtil.getSelectionFromDecsionVariables(solution, model);
			Solution s = new Solution(selection);
			s.setSemanticModel(model); // needed to be set so as to be used when computing info  value later.
			double [] objValue =  new double [solution.getNumberOfObjectives()]; 
			for (int i =0; i < objValue.length; i++){
				objValue[i] = solution.getObjective(i);
			}
			result.put(s, objValue);
			j++;
		}
		
		return result;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<Solution, double[]> getAllEvaluatedSolutions (){
		if (algorithmName.equals("NSGAII")){
			allEvaluatedSolutions = ((NSGAII)algorithm).getAllEvaluatedSolutions();
		}else if (algorithmName.equals("SPEA2")){
			allEvaluatedSolutions= ((SPEA2)algorithm).getAllEvaluatedSolutions();
		}else if (algorithmName.equals("MOCell")){
			allEvaluatedSolutions = ((MOCell)algorithm).getAllEvaluatedSolutions();
		}else if (algorithmName.equals("Random")){
			allEvaluatedSolutions = ((RandomSearch)algorithm).getAllEvaluatedSolutions();
		}
		return convertJmetalSolutionsToRadarEquivalent(allEvaluatedSolutions);
	}
	
	public long getComputationTime (){
		return computingTime;
	}
	
}
