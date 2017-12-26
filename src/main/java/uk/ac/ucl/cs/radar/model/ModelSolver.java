package uk.ac.ucl.cs.radar.model;
import java.util.List;
import java.util.Map;

import uk.ac.ucl.cs.radar.sbse.SbseParameter;
import  uk.ac.ucl.cs.radar.sbse.SbseSolver;
import uk.ac.ucl.cs.radar.model.ConstraintContext;
import uk.ac.ucl.cs.radar.model.ExcludeConstraint;
import uk.ac.ucl.cs.radar.model.RequireConstraint;
import uk.ac.ucl.cs.radar.model.Solution;

/**
 * @author Saheed Busari and Emmanuel Letier
 */
public class ModelSolver {
	/**
	 * Solves the model and saves the analysis results. 
	 * @param m semantic model obtained during parsing.
	 * @return analysis result.
	 */
	public static AnalysisResult solve(Model m){

		// get all objectives
		List<Objective> objectives = m.getObjectives();
			
		// check for cylic dependencies in quality variables.
		try{
			m.getCyclicDependentVariables();
		}catch (Exception e){
			throw new RuntimeException (e.getMessage());
		}
		System.out.println("Finished checking cyclic dependecies");
		// get all decisions
		List<Decision> decisions = m.getDecisions();
		
		// instantiate the result object
		AnalysisResult result = new AnalysisResult(objectives,decisions);
		
		//long StartBeforeUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		long start = System.currentTimeMillis();
		
		// get all solutions
		System.out.println("Generating design space.");
		List<Solution> allSolutions = m.getAllSolutions().list(); 
		
	
		result.addAllSolutions(allSolutions);
		// solution space
		result.addSolutionSpace(m.getSolutionSpace());

		// add subgraph obejective
		result.addSubGraphObejctive(m.getSubGraphObjective());
		
		// Evaluate objectives for all solutions
		System.out.println("Evaluation and computation of optimal solutions to begin");
		

		int i =0;
		for (Solution s: allSolutions){
			//result.addEvaluation(s, m.evaluate(objectives, s));	
			result.addEvaluation(s, new Simulator().evaluate(objectives, s,m));	
			i++;
		}
		
		// add -ve sign for maximisaiton
		Map<Solution, double[]> evaluatedSolutions = m.addMaximisationSign(result.getEvaluatedSolutions());
		
		// Shortlists Pareto-optimal solutions
		result.addShortlist(new Optimiser().getParetoSet(evaluatedSolutions, objectives));
		
		System.out.println("Optimal solutions computed");
		
		result.addNumberOfVariables(m.getQualityVariables().size());
		result.addNumberOfDecisions(m.getDecisions().size());
		
		// Computes Value of Information
		Objective infoValueObjective = m.getInfoValueObjective();
		List<String> paramNames = m.getParameters();
		List<Parameter> parameters = Model.getParameterList(paramNames, m);
		int nbrParam = parameters.size();
		System.out.println("Information value to be computed");
		if (infoValueObjective != null ){
			InformationValueAnalyser.computeInformationValue(result,infoValueObjective, result.getShortListSolutions(), parameters);;
		}
		
		long end = System.currentTimeMillis();
		long runTime = (end - start) / 1000;
		result.addRunTime(runTime);
		
		result.addNumberOfParameters(nbrParam);
		result.addSubGraphObejctive(m.getSubGraphObjective());
		result.addEviObjective(infoValueObjective);
		System.out.println("Information value computed");
		return result;
	}
	
	/**
	 * Solves the model and saves the analysis results. Used bu the Progress Bar to determine the status of the RADAR analysis. 
	 * @param m semantic model obtained during parsing.
	 * @return analysis result.
	 */
	public static AnalysisResult solve(Model m, AnalysisResult intermediateResult, int analysisIndex, boolean useSbse, SbseParameter sbseParameter, String simulationRunTimePath){
		AnalysisResult result = null;
		if (intermediateResult == null){
			result = checkCyclicDependency(m, result);
		}else{
			result = intermediateResult;
		}
		
		//design space generation
		if (analysisIndex == 0 && useSbse == false){
			result =generateDesignSpace(m,result);
			checkConstraints(m,result);
		}
		// simulation
		if (analysisIndex == 1 && useSbse == false){
			result =simulateModel(m,result);
		}
		// optimisation
		if (analysisIndex == 2){
			result =optimise(m,result, useSbse,sbseParameter,simulationRunTimePath);
		}
		// optimisation
	/*	if (analysisIndex == 2){
			String message = "";
			//message+= "Shortlisting the Pareto Optimal solutions.\n";
			long optimisationStartTime = System.currentTimeMillis();
			// Shortlists Pareto-optimal solutions
			SbseSolver solver = new SbseSolver();
			result.addShortlist(solver.solve(m,true));
			result.addAllSolutions(solver.getAllEvaluatedSolutions());
			result.addEvaluations(solver.getAllEvaluatedSolutionsWithObjectives());
			System.out.println("Approximate Optimal solutions computed");
			result.addNumberOfVariables(m.getQualityVariables().size());
			result.addNumberOfDecisions(m.getDecisions().size());
			long optimisationEndTime = System.currentTimeMillis()-optimisationStartTime ;
			result.addOptimisationRuntime(optimisationEndTime/1000);
			message += "Shortlisted Approximate Pareto Optimal solutions in "+ optimisationEndTime/1000 +" seconds.\n";
			message += "----------------------------------------------------------------------------.\n";
			result.setConsoleMessage(message);
		}*/
		
		//information value Analysis
		if (analysisIndex == 3){
			result =computeInformationValue(m,result);
		}
		return result;
	}
	/**
	 * Checks for cylic dependency in a model. Used by the radar gui
	 * @param m semantic model
	 * @param result intermediate analysis result
	 */
	public static AnalysisResult checkCyclicDependency(Model m, AnalysisResult result){
		String message = "";
		List<Objective> objectives = m.getObjectives();
		List<Decision> decisions = m.getDecisions();
		result = new AnalysisResult(objectives,decisions);
		long cyclicCheckStartTime = System.currentTimeMillis();
		try{
			m.getCyclicDependentVariables();
		}catch (Exception e){
			throw new RuntimeException (e.getMessage());
		}
		long cyclicCheckEndTime = System.currentTimeMillis() -cyclicCheckStartTime;
		result.setConsoleMessage(message + "   runtime["+cyclicCheckEndTime + " ms].\n\n" );
		return result;
	}
	/**
	 * Generates the design space a model. Used by the radar gui
	 * @param m semantic model
	 * @param result intermediate analysis result
	 */
	static AnalysisResult generateDesignSpace (Model m, AnalysisResult result){
		String message = "";
		long designSpaceStartTime = System.currentTimeMillis();
		List<Solution> allSolutions = m.getAllSolutions().list();
		result.addAllSolutions(allSolutions);
		result.addSolutionSpace(allSolutions.size());
		result.addSubGraphObejctive(m.getSubGraphObjective());
		result.modelHasConstraint((m.getConstraints().size() > 0? true:false));
		// solution space
		long designSpaceEndTime = System.currentTimeMillis()-designSpaceStartTime ;
		result.addDesignSpaceRunTime(designSpaceEndTime/1000);
		message += "   runtime["+ designSpaceEndTime + " ms].\n\n";
		result.setConsoleMessage(message);
		return result;
	}
	static void checkConstraints (Model m, AnalysisResult result ){
		for (Solution s: result.getAllSolutions()){
			ConstraintContext excludeContext = new ConstraintContext(new ExcludeConstraint());
			excludeContext.executeStrategy(s, m);
			ConstraintContext requireContext = new ConstraintContext(new RequireConstraint());
			requireContext.executeStrategy(s, m);
		}
		// get only valid solutions
	}
	/**
	 * Simulates the design space of a model. Used by the radar gui
	 * @param m semantic model
	 * @param result intermediate analysis result
	 */
	static AnalysisResult simulateModel (Model m, AnalysisResult result){
		String message = "";
		long simulationStartTime = System.currentTimeMillis();
		int i =0;
		for (Solution s: result.getAllSolutions()){	
			System.out.println("Solution:" + i + " " + s.selectionToString());
			result.addEvaluation(s, new Simulator().evaluate(result.getObjectives(), s,m));
			i++;
		}
		long simulationEndTime = System.currentTimeMillis()-simulationStartTime ;
		result.addSimulationRuntime(simulationEndTime/1000);
		message += "   runtime[" + simulationEndTime +" ms].\n\n";
		result.setConsoleMessage(message);
		return result;
	}
	/**
	 * Perform optimiation on a model. Used by the radar gui
	 * @param m semantic model
	 * @param result intermediate analysis result
	 */
	static AnalysisResult optimise (Model m, AnalysisResult result, boolean useSbse, SbseParameter sbseParameter, String simulationRunTimePath){
		if (useSbse){
			result = aproximateOptimisation(m,result,sbseParameter,simulationRunTimePath);
		}else{
			result = exactOptimisation(m,result);
		}
		/*String message = "";
		long optimisationStartTime = System.currentTimeMillis();
		Map<Solution, double[]> evaluatedSolutions = m.addMaximisationSign(result.getEvaluatedSolutions());
		result.addShortlist(new Optimiser().getParetoSet(evaluatedSolutions, result.getObjectives()));
		result.addNumberOfVariables(m.getQualityVariables().size());
		result.addNumberOfDecisions(m.getDecisions().size());
		long optimisationEndTime = System.currentTimeMillis()-optimisationStartTime ;
		result.addOptimisationRuntime(optimisationEndTime/1000);
		message += "   runtime["+ optimisationEndTime +" ms].\n\n";
		result.setConsoleMessage(message);*/
		return result;
	}
	
	static AnalysisResult exactOptimisation(Model m, AnalysisResult result){
		String message = "";
		long optimisationStartTime = System.currentTimeMillis();
		Map<Solution, double[]> evaluatedSolutions = m.addMaximisationSign(result.getEvaluatedSolutions());
		result.addShortlist(new Optimiser().getParetoSet(evaluatedSolutions, result.getObjectives()));
		result.addNumberOfVariables(m.getQualityVariables().size());
		result.addNumberOfDecisions(m.getDecisions().size());
		long optimisationEndTime = System.currentTimeMillis()-optimisationStartTime ;
		result.addOptimisationRuntime(optimisationEndTime/1000);
		message += "   runtime["+ optimisationEndTime +" ms].\n\n";
		result.setConsoleMessage(message);
		return result;
	}
	public static AnalysisResult aproximateOptimisation(Model m, AnalysisResult result, SbseParameter sbseParameter, String runTimePath){
		String message = "";
		//message+= "Shortlisting the Pareto Optimal solutions.\n";
		long optimisationStartTime = System.currentTimeMillis();
		// Shortlists Pareto-optimal solutions
		SbseSolver solver = new SbseSolver();
		result.addShortlist(solver.solve(m,sbseParameter, sbseParameter.isConstraintAddedToObjective(),runTimePath));
		result.setSbseParameter(sbseParameter);
		result.modelHasConstraint((m.getConstraints().size() > 0? true:false));
		result.addAllSolutions(solver.getAllEvaluatedSolutions());
		result.addEvaluations(solver.getAllEvaluatedSolutionsWithObjectives());
		System.out.println("Approximate Optimal solutions computed");
		result.addNumberOfVariables(m.getQualityVariables().size());
		result.addNumberOfDecisions(m.getDecisions().size());
		result.useSbse(true);
		long optimisationEndTime = System.currentTimeMillis()-optimisationStartTime ;
		result.addOptimisationRuntime(optimisationEndTime/1000);
		message += "   runtime["+ optimisationEndTime +" ms].\n\n";
		result.setConsoleMessage(message);
		return result;
	}
	
	/**
	 * Computes the evtpi and evppi in a decision model. Used by the radar gui
	 * @param m sematic model
	 * @param result intermediate analysis result
	 */
	public static AnalysisResult computeInformationValue(Model m, AnalysisResult result){
		String message = "";
		long InformationValueAnalysisStartTime = System.currentTimeMillis();
		Objective infoValueObjective = m.getInfoValueObjective();
		List<String> paramNames = m.getParameters();
		List<Parameter> parameters = Model.getParameterList(paramNames, m);
		int nbrParam = parameters.size();
		if (infoValueObjective != null && parameters.size() > 0 ){
			InformationValueAnalyser.computeInformationValue(result,infoValueObjective, result.getShortListSolutions(), parameters);;
		}
		long InformationValueAnalysisEndTime = System.currentTimeMillis()-InformationValueAnalysisStartTime;
		result.addInformationValueRuntime(InformationValueAnalysisEndTime/1000);
		result.addNumberOfParameters(nbrParam);
		result.addSubGraphObejctive(m.getSubGraphObjective());
		result.addEviObjective(infoValueObjective);
		message+= "   runtime["+ InformationValueAnalysisEndTime + " ms].\n\n";
		result.setConsoleMessage(message);
		return result;
	}
}
