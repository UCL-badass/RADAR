package uk.ac.ucl.cs.radar.sbse;

import java.util.ArrayList;
import java.util.List;

import uk.ac.ucl.cs.radar.model.AnalysisResult;
import uk.ac.ucl.cs.radar.model.Model;
import uk.ac.ucl.cs.radar.model.ModelSolver;
import uk.ac.ucl.cs.radar.model.Parser;
import uk.ac.ucl.cs.radar.model.ScatterPlotJFrame3D;
import uk.ac.ucl.cs.radar.model.TwoDJFramePlotter;
import uk.ac.ucl.cs.radar.userinterface.RADAR_GUI;
import uk.ac.ucl.cs.radar.userinterface.UserInterfaceUtility;
import uk.ac.ucl.cs.radar.utilities.Helper;
import uk.ac.ucl.cs.radar.utilities.InputValidator;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class radar_console_app {
	
	// how to exit the command.
	//@Parameter(names = "--debug", description = "Debug mode")
	public boolean debug = false;
	
	@Parameter(names = "--help", description = "Help")
	private boolean help = false;

	@Parameter(names = "--output", description = "Output folder where the results are saved. Input <file path>.  ")
	public String output = null;
	
	@Parameter(names = "--model", description = "The file that contains the decision model. Input <file path>. ")
	public String model = null;
	
	//@Parameter(names = "--parse", description = "Parses the decision model to check for syntax error.")
	private boolean parse = true;
	
	@Parameter(names = "--param",arity = 5, description = "Specify the approximate algorithm parameters. Inputs <population size> (integer), <crossover rate> (double), <mutation rate> (double), <maximum evaluation> (integer) and <runs> (integer) in this order. ")
	public List<String> algorithmParameter = new ArrayList<String>();
	
	@Parameter(names = "--param-default", description = "Approximate algorithm uses default parameters for <population size> (100), <crossover rate> (0.8), <mutation rate> (1/#options), <maximum evaluation> (1000) and <runs> (10) . ")
	public boolean defaultAlgorithmParameter = false;
	/*
	@Parameter(names = "--solve", description = "Solves the decision model using exhaustive search. ")
	public boolean solve = true;
	*/
	@Parameter(names = "--solve-using", arity =1, description = "Solves the decision model. Input <algorithm name> e.g. ExhaustiveSearch, NSGAII, SPEA2, MOCell, IBEA, RandomSearch, SbseAlgs. ")
	public ArrayList<String> solve = new ArrayList<String>();;
	
	
	//@Parameter(names = "--decision", description = "Generates the model decision dependency graph.")
	public boolean decision = true;
	
	//@Parameter(names = "--variable", description = "Generates the model AND/OR variable depenedency graph.")
	public boolean variable = true;
	
	@Parameter(names = "--opt", description = "Optimisation type for handling RADAR constraints. Example usage is --opt 'n+1'.")
	public String optimisationType = "n+1";
	
	
	@Parameter(names = "--nbr_sim", description = "Number of simulation run. Input <sample size> .")
	public Integer nbr_Simulation = 10000;
	
	//@Parameter(names = "--problem-name", description = "Name of the experiment. Input <experiment name>  ")
	public String expName = "Testing";
		
	// a list needs an arity, but because we do not know the arity we enforce every thing in a quoted string separated by comma.
	@Parameter(names ="--EVPI", description = "Computes evtpi and evppi. Input is an objective name. An example usage of this command is: --infoValueObjective 'ENB' ")
	private String infoValueObjective = "";
	
	@Parameter(names ="--subGraphObj", description = "Generates AND/OR subgraph for the specified objective only. Input is an objective name. An example usage of this command is: --subGraphObjective 'InvestigationCost' ")
	private String subGraphObjective = "";
	
	
	//@Parameter(names ="--pareto", description = "Displays the pareto plots.")
	public boolean pareto = true;
	int noOfRuns =1;
	boolean printResult = true;
   
	public static void main(String[]args) throws Exception {
    	radar_console_app cmd = new radar_console_app();
    	JCommander jcommander = new JCommander(cmd, args);
    	jcommander.setProgramName("Radar");
        if (cmd.help) {
        	jcommander.usage();
            return;
        }
        cmd.run(cmd);
    }
	Model loadModel () throws Exception{
		//4. when parse is specified quickly parse and write a message that the model is parsed.
		Model semanticModel =null;
		if (parse == true){
			InputValidator.validateModelPath(model);
    		InputValidator.validateOutputPath(output);
    		try {
    			semanticModel = new Parser().parseCommandLineModel(model.trim(), nbr_Simulation, infoValueObjective,subGraphObjective);
    		}catch (RuntimeException re){
    			throw new RuntimeException( "Error: "+ re.getMessage());
    		}
    		System.out.println("Model was parsed succesfully.");
    		
		}
		// if solve is specified, we  parse the model and solve.
		if (semanticModel == null){
			semanticModel = new Parser().parseCommandLineModel(model.trim(), nbr_Simulation, infoValueObjective,subGraphObjective);
		}
		if (model != null && output != null && semanticModel == null){
			throw new RuntimeException ("Specify a command to solve the model. Use the --help command for more information.");
		}
		return semanticModel;
		
	}
	SbseParameter getSbseParameter (String optimisationType){
		SbseParameter param = new SbseParameter ();
		param.useDefaultParameter(true);
		param.setAlgorithmName(solve.get(0));
		if (defaultAlgorithmParameter == false){
			param.setCrossOverProbability(Double.parseDouble(algorithmParameter.get(1)));
			param.setMutationProbability(Double.parseDouble(algorithmParameter.get(2)));
			param.setMaximumEvaluation(Integer.parseInt(algorithmParameter.get(3)));
			param.setPopulationSize(Integer.parseInt(algorithmParameter.get(0)));
		}else{
			param.setCrossOverProbability(0.8);
			param.setMaximumEvaluation(25000);
			param.setPopulationSize(100);
		}
		if (optimisationType == "n+1"){
			param.addConstraintAsObjective(true);
		}else{
			param.addConstraintAsObjective(false);
		}
		return param;

	}
	AnalysisResult solve (Model semanticModel, AnalysisResult result,  String problem){
		try {
			result = ModelSolver.checkCyclicDependency(semanticModel, result);
			if (solve.contains("ExhaustiveSearch")){
				System.out.println("Solving the model " + problem + " with exhaistive search!");
				result = ModelSolver.solve(semanticModel);
			}else if(solve.contains("SbseAlgs")) {
				printResult = false;
				decision = false;
				variable  =false;
				pareto= false;
				System.out.println("Solving the model " + problem + " with NSGAII, SPEAII, MoCell, IBEA and Random Search!");
				int nbrRuns = algorithmParameter != null && algorithmParameter.size() >0? Integer.parseInt(algorithmParameter.get(4)) : noOfRuns;
				SbseExperiment.runExperiment(problem, optimisationType, output, model, infoValueObjective, nbrRuns, getSbseParameter(optimisationType));
				
			}else{
				System.out.println("Solving the model " + problem + " with " +solve.get(0));
				result =  ModelSolver.aproximateOptimisation(semanticModel,  result, getSbseParameter(optimisationType),output);
			}
		} catch (Exception e) {
			System.out.print("Error: ");
    		System.out.println(e.getMessage());
		}
			return result;
	}
	void analyseRadarModel (int nbr_simulation){
		try {
    		
			
    		Model semanticModel = loadModel ();
    		semanticModel.setNbr_Simulation(nbr_simulation);
    		// validate input parameters
    		InputValidator.objectiveExist(semanticModel, infoValueObjective);
    		InputValidator.objectiveExist(semanticModel, subGraphObjective); 
    		InputValidator.validateSbseParameters(defaultAlgorithmParameter, solve, algorithmParameter);    	
    		InputValidator.validateSbseParameterValues(algorithmParameter);
    		// analyse model
    		String[] problemPath = model.split("/");
			String problem = problemPath[problemPath.length -2];
			if (output == null){
				output = radar_console_app.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				output = Helper.processJarLocationPath(output);
			}
    		String modelResultPath = output + "/" + semanticModel.getModelName() + "/" + optimisationType+ "/" + solve.get(0) + "/" + "AnalysisResult/";

    		AnalysisResult result = null;
    		
    		result = solve(semanticModel,result,problem);
    		result.modelHasConstraint((semanticModel.getConstraints().size() > 0? true:false));
    		if (infoValueObjective != "" && !solve.contains("SbseAlgs")){
    			result = ModelSolver.computeInformationValue(semanticModel, result);
    		}
			if (printResult == true){
				String analysisResult = result.analysisToString();
				String analysisResultToCSV = result.analysisResultToCSV();
				Helper.printResults (modelResultPath , analysisResult, semanticModel.getModelName() +".out", false);
				Helper.printResults (modelResultPath , analysisResultToCSV, semanticModel.getModelName() +".csv", false);	
			}
			// generate graphs
			if (decision == true){
				String decisionGraph = semanticModel.generateDecisionDiagram(result.getAllSolutions());
				Helper.printResults (modelResultPath + "graph/", decisionGraph, semanticModel.getModelName() + "dgraph.dot", false);
			}
			if (variable == true){
				String variableGraph = semanticModel.generateDOTRefinementGraph(semanticModel, result.getSubGraphObjective());
				Helper.printResults (modelResultPath + "graph/", variableGraph,  semanticModel.getModelName() + "vgraph.dot", false);
			}
    		if (pareto == false){
    			if (semanticModel.getObjectives().size() == 2){
					TwoDJFramePlotter twoDPlot = new TwoDJFramePlotter();
					twoDPlot.plot(semanticModel,modelResultPath, result);
				}else if (semanticModel.getObjectives().size() == 3){
					ScatterPlotJFrame3D sc3D= new ScatterPlotJFrame3D( );
					sc3D.plot(semanticModel, modelResultPath, result);;
				}
    		}
    		System.out.println("Analysis is Finished!");
    		System.out.println("Results are in : " + output);
    		
    	}catch (Exception e){
    		System.out.print("Error: ");
    		System.out.println(e.getMessage());
    	}
	}
		
	public void run(radar_console_app cmdParam) throws Exception {
		if (parse == true){
			analyseRadarModel (nbr_Simulation);
			System.exit(0);
		}
		
		
		
    }

	
}
