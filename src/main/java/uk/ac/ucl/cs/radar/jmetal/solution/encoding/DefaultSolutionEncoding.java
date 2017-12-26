package uk.ac.ucl.cs.radar.jmetal.solution.encoding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.ucl.cs.radar.jmetal.problem.IGenericRadarProblem;
import uk.ac.ucl.cs.radar.jmetal.problem.GenericRadarProblem;
import uk.ac.ucl.cs.radar.jmetal.solution.encoding.SolutionEncoding;
import uk.ac.ucl.cs.radar.model.Decision;
import uk.ac.ucl.cs.radar.model.DecisionType;
import uk.ac.ucl.cs.radar.model.ProblemType;
import uk.ac.ucl.cs.radar.utilities.JmetalUtil;
import uk.ac.ucl.cs.radar.utilities.PseudoRandom;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

@SuppressWarnings("serial")
public class DefaultSolutionEncoding implements SolutionEncoding,Cloneable {
	  private double[] objectives;
	  private List<Integer[]> variables;
	  protected IGenericRadarProblem problem ;
	  protected Map<Object, Object> attributes ;
	  protected final JMetalRandom randomGenerator ;
	  private ProblemType problemType;
	  /**
	   * Constructor
	   */
	  public DefaultSolutionEncoding(IGenericRadarProblem problem ) {
	    this.problem = problem;
	    attributes = new HashMap<>() ;
	    randomGenerator = JMetalRandom.getInstance() ;
	    objectives = new double[problem.getNumberOfObjectives()] ;
	    problemType = problem.getProblemType();
	   
	    if (problemType == ProblemType.MutuallyExclsisve){
	    	setDecisionVariablesForMutuallyExclusiveProblems(problem);
	    }else if (problemType == ProblemType.NonMutuallyExclusive){
	    	setDecisionVariablesForNonMutuallyExclusiveProblems(problem);
	    }else{
	    	setDecisionVariablesForBothMutuallyAndNonMutuallyExclusiveProblems(problem);
	    }
	    
	  }
	  @Override
	  public Object clone()  {
		  DefaultSolutionEncoding cloned = null;
			try {
				cloned = (DefaultSolutionEncoding)super.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		  return cloned;
	  }
	  /**
	   * Copy constructor
	   * @param solution to copy
	   */
	  public DefaultSolutionEncoding(DefaultSolutionEncoding solution) {
	    this(solution.problem) ;
	    for(int i =0; i < variables.size(); i++){ 
	    	this.variables.set(i, solution.variables.get(i).clone());
	    }
	    
	    /*for(int i =0; i < variables.size(); i++){
	    	for(int j=0; j < variables.get(i).length; j++){
	    		this.variables.get(i)[j] = new Integer (solution.variables.get(i)[j]);
	    		//this.variables.set(i, solution.variables.get(i).clone()); 
	    	}
	    }*/
	    for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
	      objectives[i] = solution.getObjective(i) ;
	    }

	    attributes = new HashMap<Object, Object>(solution.attributes) ;
	  }
	  void initialiseVariable (List<Integer[]> variables, int l, int u){
		  this.variables = new ArrayList<Integer[]>();
		  for(int i =0; i < variables.size(); i++){
		    	Integer[] decisionPoint = new Integer[variables.get(i).length];
		    	this.variables.add(decisionPoint);
		   }
		  for(int i =0; i < variables.size(); i++){
		    	for(int j=0; j < variables.get(i).length; j++){
		    		this.variables.get(i)[j] = randomGenerator.nextInt(l, u) ;
		    	}
		  }
	  }
	  void makeDecisionVariableMutuallyExclusive(){
		  int i =0;
		    for (Integer[] entry : this.variables){
				int indexToChange = PseudoRandom.randInt(0, entry.length - 1);
				variables.get(i)[indexToChange] = randomGenerator.nextInt(1, 1) ;
				i++;
			}
	  }
	  void makeDecisionVariableBothMutuallyExclusiveAndNonMutuallyExclusive(){
		  List <Decision> decisions = problem.getSemanticModel().getDecisions();	
		  int i =0;
		    for (Integer[] var : this.variables){
		    	int j =0;
				  for(Decision d : decisions){
				  	if (i == j){
				  		if ( d.getDecisionType() == DecisionType.MutuallyExclsisve){
				  			int indexToChange = PseudoRandom.randInt(0, var.length - 1);
							var[indexToChange] = randomGenerator.nextInt(1, 1) ;
				  		}else if (d.getDecisionType() == DecisionType.NonMutuallyExclusive){
					    	for(int k =0; k < var.length; k++){
					    		var[k] = randomGenerator.nextInt(0, 1) ;
					    	}
				  		}else{}
				    	break;
				  	}
			    	j++;
				  }
				i++;
			} 
	  }
	  void setDecisionVariablesForMutuallyExclusiveProblems(IGenericRadarProblem problem){
		  	List<Integer[]> variables = problem.getDecisionVariables();
		  	initialiseVariable(variables, 0, 0);
		    makeDecisionVariableMutuallyExclusive();
		    
	  }
	  void setDecisionVariablesForNonMutuallyExclusiveProblems(IGenericRadarProblem problem){
		  	List<Integer[]> variables = problem.getDecisionVariables();
		  	initialiseVariable(variables, 0, 1);
		    
	  }
	  void setDecisionVariablesForBothMutuallyAndNonMutuallyExclusiveProblems(IGenericRadarProblem problem){
		  	List<Integer[]> variables = problem.getDecisionVariables();
		  	initialiseVariable(variables, 0, 0);
		  	makeDecisionVariableBothMutuallyExclusiveAndNonMutuallyExclusive();
	  }
	

	  @Override
	  public void setObjective(int index, double value) {
	    objectives[index] = value ;
	  }

	  @Override
	  public double getObjective(int index) {
	    return objectives[index];
	  }

	  @Override
	  public Integer getVariableValue(int index) {
	    return variables.get(index)[index];
	  }
	  /*@Override
	  public int getVariableValue(int decisionIndex, int optionIndex) {
	    return variables.get(decisionIndex)[optionIndex];
	  }*/

	  @Override
	  public void setVariableValue(int index, Integer value) {
	    variables.get(index)[index] = value ;
	  }
	 /* @Override
	  public void setVariableValue(int decisionIndex, int optionIndex, Integer value) {
		  variables.get(decisionIndex)[optionIndex] = value ;
	  }*/
	  @Override
	  public String getVariableValueString(int index) {
		  String result ="";
		  String options = "";
		  /*for(int i =0; i < variables.size(); i++){
			  if (i == index){
				  for(int j=0; j < variables.get(i).length; j++){
			    		options +=variables.get(i)[j] +",";
			    	}
			    	result += "D" +i +  "{" + options + "} ";
			    	break;
			  }
		    	
		  }*/
		  Map <Decision, List<String>> selection = JmetalUtil.getSelectionFromDecsionVariables(this,problem.getSemanticModel());
		  int i =0;
		  for(Map.Entry<Decision, List<String>> entry: selection.entrySet()){
			  	if (i == index){
			  		String decision = entry.getKey().getDecisionLabel();
			    	for (String option : entry.getValue()){
			    		options += option + ",";
			    	}
			    	result += decision +  "{" + options + "} ";
			    	options = "";
			    	break;
			  	}
		    	i++;
		  }
		  
		  return result;
	  }

	  @Override
	  public int getNumberOfVariables() {
	    return problem.getNumberOfVariables();
	  }

	  @Override
	  public int getNumberOfObjectives() {
	    return problem.getNumberOfObjectives();
	  }

	  @Override
	  public int getUpperBound(int index) {
	    return problem.getUpperBound(index);
	  }

	  @Override
	  public int getLowerBound(int index) {
	    return problem.getLowerBound(index) ;
	  }

	  @Override
	  public Solution<Integer> copy() {
	    return new DefaultSolutionEncoding(this);
	  }

	  @Override
	  public void setAttribute(Object id, Object value) {
	    attributes.put(id, value) ;
	  }

	  @Override
	  public Object getAttribute(Object id) {
	    return attributes.get(id) ;
	  }

	  @Override
	  public boolean equals(Object o) {
	    if (this == o) return true;
	    if (o == null || getClass() != o.getClass()) return false;

	    DefaultSolutionEncoding that = (DefaultSolutionEncoding) o;

	    if (!Arrays.equals(objectives, that.objectives)) return false;
	    for (int i =0; i < variables.size(); i++){
	    	if (!Arrays.equals(variables.get(i), that.variables.get(i))) return false;
	    }
	    
	    return problem != null ? problem.equals(that.problem) : that.problem == null;

	  }

	  @Override
	  public int hashCode() {
	    int result = Arrays.hashCode(objectives);
	    int hashSum =0;
	    for (Integer[] var : variables){
	    	hashSum += Arrays.hashCode(var);
	    }
	    result = 31 * hashSum;
	    result = 31 * result + (problem != null ? problem.hashCode() : 0);
	    return result;
	  }

		/*@Override
		public String getVariableValueString(int decisionIndex, int optionIndex) {
			return variables.get(decisionIndex)[optionIndex].toString();
		}*/

		@Override
		public List<Integer[]> getDecisionVariables() {
			return variables;
		}
}
