//  ZDT1.java
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

package uk.ac.ucl.cs.radar.jmetal.problem;

import uk.ac.ucl.cs.radar.jmetal.problem.AbstractRadarProblem;
import uk.ac.ucl.cs.radar.jmetal.solution.encoding.SolutionEncoding;
import uk.ac.ucl.cs.radar.model.CoupleConstraint;
import uk.ac.ucl.cs.radar.model.ConstraintContext;
import uk.ac.ucl.cs.radar.model.Decision;
import uk.ac.ucl.cs.radar.model.ExcludeConstraint;
import uk.ac.ucl.cs.radar.model.Model;


import uk.ac.ucl.cs.radar.model.Objective;
import uk.ac.ucl.cs.radar.model.ProblemType;
import uk.ac.ucl.cs.radar.model.RequireConstraint;
import uk.ac.ucl.cs.radar.model.Simulator;
import uk.ac.ucl.cs.radar.model.Solution;
import uk.ac.ucl.cs.radar.utilities.Helper;
import uk.ac.ucl.cs.radar.utilities.JmetalUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.uma.jmetal.problem.ConstrainedProblem;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;
import org.uma.jmetal.util.solutionattribute.impl.OverallConstraintViolation;

//

/** Class representing problem ZDT1 */
@SuppressWarnings("serial")
public class GenericRadarProblem extends AbstractRadarProblem implements ConstrainedProblem<SolutionEncoding>{
	
	Model semanticModel;
	List<Objective> objectives;
	boolean addConstraintAsObjective;
	ProblemType problemType;
	public OverallConstraintViolation<SolutionEncoding> overallConstraintViolationDegree ;
	public NumberOfViolatedConstraints<SolutionEncoding> numberOfViolatedConstraints ;
	String runTimePath= "";
  /** Constructor. Creates default instance of problem ZDT1 (30 decision variables) */

  /**
   * Creates a new instance of problem ZDT1.
   *
   * @param numberOfVariables Number of variables.
   */
  List<Integer[]> getDecisionVariable (Model m){
	  List<Integer[]> decisionVariable = new ArrayList<Integer[]>();
	  List<Decision> decisions = m.getDecisions();
	  for (Decision d : decisions){
		  decisionVariable.add(new Integer[d.getOptions().size()]);
	  }
	  return decisionVariable;
  }
  public GenericRadarProblem( Model semanticModel, boolean addConstraintAsObjective, String runTimePath) {
	    setNumberOfVariables(semanticModel.getDecisions().size());
	    //setNumberOfObjectives(addConstraintAsObjective==true? semanticModel.getObjectives().size(): semanticModel.getObjectives().size()-1 );
	    setNumberOfObjectives(semanticModel.getObjectives().size()+1); //cater for nbr of constraints
	    setName(semanticModel.getModelName());

	    List<Integer> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
	    List<Integer> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

	    for (int i = 0; i < getNumberOfVariables(); i++) {
	      lowerLimit.add(0);
	      upperLimit.add(1);
	    }
	    
	    setLowerLimit(lowerLimit);
	    setUpperLimit(upperLimit);
	    setDecisionVariables(getDecisionVariable(semanticModel));
	    this.runTimePath = runTimePath;
	    this.semanticModel = semanticModel;
	    setProblemType(semanticModel.getProblemType());
	    objectives = semanticModel.getObjectives();
	    this.addConstraintAsObjective = addConstraintAsObjective;
	    numberOfViolatedConstraints = new NumberOfViolatedConstraints<SolutionEncoding>() ;
  }


  /** Evaluate() method */
  public void evaluate(SolutionEncoding solution) {
	  Map <Decision, List<String>> selection = JmetalUtil.getSelectionFromDecsionVariables(solution,semanticModel);
	  Solution s = new Solution(selection);
	  // if there is a constraint add another objective.
	  ConstraintContext excludeContext = new ConstraintContext(new ExcludeConstraint());
	  excludeContext.executeStrategy(s, semanticModel);
	  ConstraintContext requireContext = new ConstraintContext(new RequireConstraint());
	  requireContext.executeStrategy(s, semanticModel);
	  ConstraintContext andContext = new ConstraintContext(new CoupleConstraint());
	  andContext.executeStrategy(s, semanticModel);
	  
	  long t0 = System.currentTimeMillis();
	  double[] objValues = new Simulator().evaluate(objectives, s, semanticModel);
	  long t1 = System.currentTimeMillis() -t0;
	  String simulationTime = Long.toString(t1);
	 /* if (StringUtils.isNotEmpty(runTimePath) && StringUtils.isNotBlank(runTimePath)){
		  try {
				Helper.printResults(runTimePath,simulationTime , semanticModel.getModelName(), true);
		  }catch (IOException e) {
				throw new RuntimeException (e.getMessage());
		  }
	  }*/
	 
	  //int numberOfObejctives = (semanticModel.getConstraints().size() > 0)? getNumberOfObjectives() : getNumberOfObjectives()-1;
	  int numberOfObejctives =  getNumberOfObjectives() ;
	  for (int i =0 ; i < objValues.length; i++){
		  double objValue =  objValues[i];
		  if (semanticModel.getObjectives().get(i).getIsMinimisation() == false){
			  objValue *=-1;
		  }
		  solution.setObjective(i, objValue);
	  }
	  if (semanticModel.getConstraints().size() > 0){
		  solution.setObjective(numberOfObejctives-1, (int)s.getNbrViolations()); //c onstraint is treated as an objective
		  if (!addConstraintAsObjective){
			  numberOfViolatedConstraints.setAttribute(solution, (int)s.getNbrViolations());
		  }
	  }
  }

	@Override
	public void evaluateConstraints(SolutionEncoding solution) {
		
	}

	@Override
	public ProblemType getProblemType() {
		return problemType;
	}

	@Override
	public void setProblemType(ProblemType problemType) {
		this.problemType = problemType;
	}

	@Override
	public Model getSemanticModel() {
		return semanticModel;
	}
}
