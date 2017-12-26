package uk.ac.ucl.cs.radar.sbse;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import uk.ac.ucl.cs.radar.model.Model;
import uk.ac.ucl.cs.radar.model.Solution;

public class SbseSolver {

	Map<Solution, double[]> optimalSolutions;
	Map<Solution, double[]> allEvaluatedSolutions;
	List<Solution> allSolutions;
	public SbseSolver(){
		optimalSolutions = new LinkedHashMap<Solution, double[]>();
		allSolutions = new ArrayList<Solution>();
	}
	public Map<Solution, double[]> solve(Model m, SbseParameter param, boolean addConstraintAsObjective, String runtimePath){
		
		SbseAlgorithm alg = new SbseAlgorithm(m,  addConstraintAsObjective,runtimePath);
		alg.setCrossOver(param.getCrossOverProbability());
		alg.setMutation(param.getMutationProbability());
		alg.buildAlgorithm(param.getAlgorithmName(), param.getMaximunEvaluation(), param.getPopulationSize());
		Map<Solution, double[]> optimalSolutions = alg.solve();
		allEvaluatedSolutions = alg.getAllEvaluatedSolutions();
		allSolutions = new ArrayList<Solution>(alg.getAllEvaluatedSolutions().keySet());
		return optimalSolutions;
	}
	public List<Solution> getAllEvaluatedSolutions (){
		return allSolutions;
	}
	public Map<Solution, double[]> getAllEvaluatedSolutionsWithObjectives (){
		return allEvaluatedSolutions;
	}
}
