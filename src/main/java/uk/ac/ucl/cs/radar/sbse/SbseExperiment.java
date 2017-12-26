package uk.ac.ucl.cs.radar.sbse;

//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU Lesser General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU Lesser General Public License for more details.
//
//You should have received a copy of the GNU Lesser General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>.


import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.ibea.IBEA;
import org.uma.jmetal.algorithm.multiobjective.mocell.MOCellBuilder;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.randomsearch.RandomSearchBuilder;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Builder;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.qualityindicator.impl.*;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentBuilder;
import org.uma.jmetal.util.experiment.component.*;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;

import uk.ac.ucl.cs.radar.jmetal.quality.indicator.Coverage;
import uk.ac.ucl.cs.radar.jmetal.quality.indicator.Precision;
import uk.ac.ucl.cs.radar.jmetal.quality.indicator.VN;
import uk.ac.ucl.cs.radar.jmetal.quality.indicator.VR;
import uk.ac.ucl.cs.radar.jmetal.solution.encoding.SolutionEncoding;
import uk.ac.ucl.cs.radar.jmetal.variation.operators.CrossOver;
import uk.ac.ucl.cs.radar.jmetal.variation.operators.Mutation;
import uk.ac.ucl.cs.radar.jmetal.problem.GenericRadarProblem;
import uk.ac.ucl.cs.radar.model.Model;
import uk.ac.ucl.cs.radar.utilities.Helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* Example of experimental study based on solving the ZDT problems with four versions of NSGA-II,
* each of them applying a different crossover probability (from 0.7 to 1.0).
*
* This experiment assumes that the reference Pareto front are known, so the names of files
* containing them and the directory where they are located must be specified.
*
* Six quality indicators are used for performance assessment.
*
* The steps to carry out the experiment are: 1. Configure the experiment 2. Execute the algorithms
* 3. Compute the quality indicators 4. Generate Latex tables reporting means and medians 5.
* Generate Latex tables with the result of applying the Wilcoxon Rank Sum Test 6. Generate Latex
* tables with the ranking obtained by applying the Friedman test 7. Generate R scripts to obtain
* boxplots
*
* @author Antonio J. Nebro <antonio@lcc.uma.es>
*/
public class SbseExperiment {
	
private static int INDEPENDENT_RUNS = 30;
/*
static Model loadModel (String model, int nbr_Simulation, String infoValueObjective, String subGraphObjective) throws Exception{
	Model semanticModel = null;
	try {
		semanticModel = new Parser().parseCommandLineModel(model.trim(), nbr_Simulation, infoValueObjective,subGraphObjective);
	}catch (RuntimeException re){
		throw new RuntimeException( "Error: "+ re.getMessage());
	}
	return semanticModel;
	
}*/

static boolean runExperiment (String study, String optimisationType, String path, String problemPath, String problemEVPI, int runs , SbseParameter param) throws Exception{
	/*    if (args.length != 1) {
	  throw new JMetalException("Missing argument: experimentBaseDirectory");
	}*/
	INDEPENDENT_RUNS = runs;
	boolean addConstraintAsObjective =true;
	if(optimisationType.equals("1+n")){
		addConstraintAsObjective = false;
	}
	  
	String experimentBaseDirectory = path + optimisationType + "/"+ study; //"/Users/INTEGRALSABIOLA/Documents/RADAR_Project/SbseResults/" + optimisationType;	
	List<ExperimentProblem<SolutionEncoding>> problemList = new ArrayList<>();
	
	Model semantic_model = Helper.loadModel(problemPath, 10000, problemEVPI, problemEVPI);
	GenericRadarProblem problem = new GenericRadarProblem (semantic_model, addConstraintAsObjective,experimentBaseDirectory);
	problemList.add(new ExperimentProblem<>(problem));
	
	/*String  model = "./uk.ac.ucl.cs.examples/CBA/CBA.rdr";
	Model m;
	try {
		m = loadModel(model, 10000, "ENB", "ENB");
	} catch (Exception e) {
		throw new JMetalException("Unable to load model.");
	}
	GenericRadarProblem CbaProblem = new GenericRadarProblem (m, false);
	problemList.add(new ExperimentProblem<>(CbaProblem));*/
	
	
	
	List<ExperimentAlgorithm<SolutionEncoding, List<SolutionEncoding>>> algorithmList =
	        configureAlgorithmList(problemList,param);
	
	List<String> referenceFrontFileNames = Arrays.asList("ZDT1.pf");
	
	Experiment<SolutionEncoding, List<SolutionEncoding>> experiment =
	        new ExperimentBuilder<SolutionEncoding, List<SolutionEncoding>>("RadarStudy" + semantic_model.getModelName())
	                .setAlgorithmList(algorithmList)
	                .setProblemList(problemList)
	                .setExperimentBaseDirectory(experimentBaseDirectory)
	                .setOutputParetoFrontFileName("FUN")
	                .setOutputParetoSetFileName("VAR")
	                .setReferenceFrontDirectory(experimentBaseDirectory + "/pareto_fronts")
	                //.setReferenceFrontDirectory("/Users/INTEGRALSABIOLA/Documents/JavaProject/jMetal/jmetal-problem/src/test/resources/pareto_fronts")
	                //.setReferenceFrontFileNames(referenceFrontFileNames)
	                .setIndicatorList(Arrays.asList(
	                		new Coverage<SolutionEncoding>(),
	                		new Precision<SolutionEncoding>(),
	                		new VN<SolutionEncoding>(),
	                		new VR<SolutionEncoding>(),
	                        new Epsilon<SolutionEncoding>(),
	                        new Spread<SolutionEncoding>(),
	                        new GenerationalDistance<SolutionEncoding>(),
	                        new PISAHypervolume<SolutionEncoding>(),
	                        new InvertedGenerationalDistance<SolutionEncoding>(),
	                        new InvertedGenerationalDistancePlus<SolutionEncoding>()
	                        ))
	                .setIndependentRuns(INDEPENDENT_RUNS)
	                .setNumberOfCores(8)
	                .build();
	
	new ExecuteAlgorithms<>(experiment).run();
	new GenerateReferenceParetoFront(experiment).run();
	new ComputeQualityIndicators<>(experiment).run();
	new GenerateLatexTablesWithStatistics(experiment).run();
	new GenerateWilcoxonTestTablesWithR<>(experiment).run();
	new GenerateFriedmanTestTables<>(experiment).run();
	new GenerateBoxplotsWithR<>(experiment).setRows(3).setColumns(3).run();
	return true;
}
public static void main(String[] args) throws Exception {
	
	String optType1 = "1+n";
	String optType2 ="n+1";
	String path = args[0]; // "/Users/INTEGRALSABIOLA/Documents/RADAR_Project/SbseResults/";
	
	int runs = 30;
	boolean cluster = false;
	
	String cbaPath = cluster == true? path + "/models/CBA_Subset.rdr" : "/Users/INTEGRALSABIOLA/Documents/RADAR_Project/RadarTool/src/main/resources/uk.ac.ucl.cs.examples/CBA_Subset/CBA_Subset.rdr";
	String webPortalPath = cluster == true? path + "/models/WebPortal.rdr" : "/Users/INTEGRALSABIOLA/Documents/RADAR_Project/RadarTool/src/main/resources/uk.ac.ucl.cs.examples/WebPortal/WebPortal.rdr";
	String bssPath =cluster == true? path +  "/models/BSS.rdr" : "/Users/INTEGRALSABIOLA/Documents/RADAR_Project/RadarTool/src/main/resources/uk.ac.ucl.cs.examples/BSS/BSS.rdr";
	String drupalPath =cluster == true? path +  "/models/Drupal.rdr" : "/Users/INTEGRALSABIOLA/Documents/RADAR_Project/RadarTool/src/main/resources/uk.ac.ucl.cs.examples/Drupal/Drupal.rdr";
	String amazonPath =cluster == true? path +  "/models/Amazon.rdr" : "/Users/INTEGRALSABIOLA/Documents/RADAR_Project/RadarTool/src/main/resources/uk.ac.ucl.cs.examples/Amazon/Amazon.rdr";
	String berkeleyPath =cluster == true? path +  "/models/BerkeleyDB.rdr" : "/Users/INTEGRALSABIOLA/Documents/RADAR_Project/RadarTool/src/main/resources/uk.ac.ucl.cs.examples/BerkeleyDB/BerkeleyDB.rdr";

	//String [] problems = {"CBA_Subset", "WebPortal", "BSS", "Drupal", "Amazon", "BerkeleyDB"};
	Map<String, String> modelFiles = new HashMap<String,String>();
	modelFiles.put("ENB", cbaPath); 
	modelFiles.put("TotalFeaturesUsedBefore",webPortalPath); 
	modelFiles.put("ExpectedCustomerSatisfaction",bssPath); 
	//modelFiles.put("ExpectedNumberOfInstallations",drupalPath); 
	modelFiles.put("ExpectedFeatureCostMonth",amazonPath); 
	modelFiles.put("ExpectedNetBenefit",berkeleyPath); 

	SbseParameter param = new SbseParameter ();
	param.useDefaultParameter(true);
	param.setCrossOverProbability(0.8);
	param.setMutationProbability(0.1);
	param.setMaximumEvaluation(25000);
	param.setPopulationSize(100);
	
	
//	if (SbseExperiment.runExperiment ("WebPortal", optType1,path, webPortalPath, "TotalFeaturesUsedBefore",runs)){
//		if (SbseExperiment.runExperiment ("BSS", optType1,path, bssPath, "ExpectedCustomerSatisfaction",runs)){
//			if (SbseExperiment.runExperiment ("BerkeleyDB", optType1,path, berkeleyPath, "ExpectedNetBenefit",runs)){
//				if (SbseExperiment.runExperiment ("Amazon", optType1,path, amazonPath, "ExpectedFeatureCostMonth",runs)){
//					System.out.println("Wait for 60 seconds.......................");
//					Thread.sleep(60000);
//				}
//				
//			}
//		}
//	}
	
	/*if (SbseExperiment.runExperiment ("WebPortal", optType2,path, webPortalPath, "TotalFeaturesUsedBefore",runs)){
		Thread.sleep(60000);
		if (SbseExperiment.runExperiment ("BSS", optType2,path, bssPath, "ExpectedCustomerSatisfaction",runs)){
			Thread.sleep(60000);
			if (SbseExperiment.runExperiment ("BerkeleyDB", optType2,path, berkeleyPath, "ExpectedNetBenefit",runs)){
				Thread.sleep(60000);
				if (SbseExperiment.runExperiment ("Amazon", optType2,path, amazonPath, "ExpectedFeatureCostMonth",runs)){
					System.out.println("Wait for 60 seconds.......................");
					Thread.sleep(60000);
				}
				
			}
		}
	}*/
	
	
	/*for (Map.Entry<String, String> entry: modelFiles.entrySet()){ 
		String[] problemPath = entry.getValue().split("/");
		String onePlusNproblem = problemPath[problemPath.length -2];
		SbseExperiment.runExperiment (onePlusNproblem, optType1,path, entry.getValue(), entry.getKey() );
		Thread.sleep(60000);
	}
	
	System.out.println("Wait for 60 seconds.......................");
	Thread.sleep(60000);
		
	for (Map.Entry<String, String> entry: modelFiles.entrySet()){ 
		String[] problemPath = entry.getValue().split("/");
		String nPlusOneProblem = problemPath[problemPath.length -2];
		SbseExperiment.runExperiment (nPlusOneProblem, optType2, path, entry.getValue(), entry.getKey() );
		Thread.sleep(60000);
	}*/
	
	runs = 1;
	// Drupal problem
	System.out.println("Start Drupal Problem for 1+n and  here.......................");
	String[] drupalProblemPath = drupalPath.split("/");
	String drupalOnePlusNproblem = drupalProblemPath[drupalProblemPath.length -2];
	//SbseExperiment.runExperiment (drupalOnePlusNproblem, optType1, path, drupalPath, "ExpectedNumberOfInstallations",runs );
	System.out.println("Start Drupal Problem for n+1 here.......................");
	Thread.sleep(60000);
	
	String drupalNPlusOneproblem = drupalProblemPath[drupalProblemPath.length -2];
	SbseExperiment.runExperiment (drupalNPlusOneproblem, optType2,path, drupalPath, "ExpectedNumberOfInstallations",runs, null );

	
}

/**
* The algorithm list is composed of pairs {@link Algorithm} + {@link Problem} which form part of
* a {@link ExperimentAlgorithm}, which is a decorator for class {@link Algorithm}. The {@link
* ExperimentAlgorithm} has an optional tag component, that can be set as it is shown in this example,
* where four variants of a same algorithm are defined.
*/
static List<ExperimentAlgorithm<SolutionEncoding, List<SolutionEncoding>>> configureAlgorithmList(
      List<ExperimentProblem<SolutionEncoding>> problemList, SbseParameter param) {
	List<ExperimentAlgorithm<SolutionEncoding, List<SolutionEncoding>>> algorithms = new ArrayList<>();
	
	for (int i = 0; i < problemList.size(); i++) {
		    Algorithm<List<SolutionEncoding>> algorithm = new SPEA2Builder<>(
		            problemList.get(i).getProblem(),
		            new CrossOver(param.getCrossOverProbability()),
		            new Mutation(param.getDefaultParameter() == false?1.0 / problemList.get(i).getProblem().getNumberOfVariables(): param.getMutationProbability()))
		            //new Mutation(1.0))
		            .setMaxIterations(param.getMaximunEvaluation()/param.getPopulationSize())
		            .setPopulationSize(param.getPopulationSize())
		            .build();
		    algorithms.add(new ExperimentAlgorithm<SolutionEncoding, List<SolutionEncoding>>(algorithm, "SPEA2", problemList.get(i).getTag()));
		  }
	
	  for (int i = 0; i < problemList.size(); i++) {
	    Algorithm<List<SolutionEncoding>> algorithm = new NSGAIIBuilder<>(
	            problemList.get(i).getProblem(),
	            new CrossOver(param.getCrossOverProbability()),
	            new Mutation(param.getDefaultParameter() == false? 1.0 / problemList.get(i).getProblem().getNumberOfVariables(): param.getMutationProbability()))
	            //new Mutation(1.0))
	            .setMaxEvaluations(param.getMaximunEvaluation())
	            .setPopulationSize(param.getPopulationSize())
	            .build();
	    algorithms.add(new ExperimentAlgorithm<SolutionEncoding, List<SolutionEncoding>>(algorithm, "NSGAII", problemList.get(i).getTag()));
	  }
	 
	
	  for (int i = 0; i < problemList.size(); i++) {
	    Algorithm<List<SolutionEncoding>> algorithm = new MOCellBuilder<>(problemList.get(i).getProblem(), 
	    		new CrossOver(param.getCrossOverProbability()),
	            new Mutation(param.getDefaultParameter() == false?1.0 / problemList.get(i).getProblem().getNumberOfVariables(): param.getMutationProbability()))
	            //new Mutation(1.0))
	            .setMaxEvaluations(param.getMaximunEvaluation())
	            .setPopulationSize(param.getPopulationSize())
	            .build();
	    algorithms.add(new ExperimentAlgorithm<SolutionEncoding, List<SolutionEncoding>>(algorithm, "MOCell", problemList.get(i).getTag()));
	  }

	  for (int i = 0; i < problemList.size(); i++) {
		  IBEA<SolutionEncoding> algorithm = new IBEABuilder(problemList.get(i).getProblem()) 
				  .setArchiveSize(param.getPopulationSize())
			      .setPopulationSize(param.getPopulationSize())
			      .setMaxEvaluations(param.getMaximunEvaluation())
			      .setCrossover(new CrossOver(param.getCrossOverProbability()))
			      .setMutation(new Mutation(param.getDefaultParameter() == false?1.0 / problemList.get(i).getProblem().getNumberOfVariables():param.getMutationProbability()))
			      //.setMutation(new Mutation(1.0))
			      .setSelection(new BinaryTournamentSelection<SolutionEncoding>())
			      .build() ;
	    algorithms.add(new ExperimentAlgorithm<SolutionEncoding, List<SolutionEncoding>>(algorithm, "IBEA", problemList.get(i).getTag()));
	  }
	
	  for (int i = 0; i < problemList.size(); i++) {
	    Algorithm<List<SolutionEncoding>> algorithm = new RandomSearchBuilder<>(problemList.get(i).getProblem())
	            .setMaxEvaluations(param.getPopulationSize())
	            .build();
	    algorithms.add(new ExperimentAlgorithm<SolutionEncoding, List<SolutionEncoding>>(algorithm, "Random", problemList.get(i).getTag()));
	  }
	
	return algorithms;
	}
}
