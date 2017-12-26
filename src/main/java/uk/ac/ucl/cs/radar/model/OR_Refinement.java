package uk.ac.ucl.cs.radar.model;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import uk.ac.ucl.cs.radar.exception.CyclicDependencyException;
import uk.ac.ucl.cs.radar.exception.ParameterDistributionException;

/**
 * @author Saheed A. Busari and Emmanuel Letier
 */
public class OR_Refinement extends Expression {
	/**
	 * The decision that correspond to the OR refinement
	 */
	private Decision decision;
	/**
	 * The AND_Refinements that correspond to the options of the OR_Refinement
	 */
	private Map<String, AND_Refinement> definition;
	/**
	 * A variable that this OR_Refinement defines.
	 */
	QualityVariable parent;
	/**
	 * Stores a boolean value to determine whether this OR_Refinement has mutually exclusive options or non-mutually exclusive options
	 */
	private boolean xor;
	private String orOperationMode;
	
	public OR_Refinement(){
		definition = new LinkedHashMap<String, AND_Refinement>();
	}
	
	public void isXOR(boolean xor){
		this.xor = xor;
	}
	public void orOperationMode(String orOperationMode){
		this.orOperationMode = orOperationMode;
	}
	/**
	 * Simulates a solution s.
	 * @param s a solution to be simulated through monte-carlo simulation.
	 * @return an array of simulated values.
	 */
	/*@Override
	public double[] simulate2(Solution s){
		String option = s.selection(decision);
		AND_Refinement and_ref = definition.get(option);
		return and_ref.simulate(s);
	}*/
	/**
	 * To be used for non-mutually exculusive selection
	 * @param s
	 * @return
	 */
	@Override
	public double[] simulate(Solution s) {
		double [] result = new double[s.getSemanticModel().getNbr_Simulation()];
		Arrays.fill(result, 0);
		List<String> selections = s.selection(decision);
		if ( selections != null && selections.size() > 0){
			for (String option : s.selection(decision)){				
				AND_Refinement and_ref = definition.get(option);
				if (and_ref == null){
					throw new RuntimeException("Ensure the option "+ option + " is spelt correctly through out its use in the model." );
				}
				double [] ans = and_ref.simulate(s);
				if (xor){
					result = ans;
				}else{
					for (int i=0; i < ans.length; i ++){
						if(orOperationMode.equals("+")){
							result[i] += ans[i];
						}else{
							result[i] *= ans[i];
						}
						
					}
				}
			}
		}
		return result;
	}
	/**
	 * Sets the decision for the OR_Refinement.
	 * @param decision OR_Refinement decision.
	 */
	public void setDecision (Decision decision){
		this.decision = decision;
	}
	/**
	 * @return OR_Refinement decision.
	 */
	public Decision getDecision (){
		return decision;
	}
	/**
	 * Adds all arithemetic definitions of all alternate AND_Refinement to the OR_Refinement.
	 * @param definition a map of arithmetic expression, where the map key is the option name and the map value is the AND_Refinement corresponding to the option.
	 */
	public void setDefinition (Map<String, AND_Refinement>  definition){
		this.definition= definition;
	}
	/**
	 * @return OR_Refinement definition, which is a map of arithmetic expression, where the map key is the option name and the map value is the AND_Refinement corresponding to the option.
	 */
	public Map<String, AND_Refinement>  getDefinition (){
		return definition;
	}
	/**
	 * Adds individual arithemetic definition of all alternate options to OR_Refinement.
	 * @param option_name  option name that correspond to the AND_Refinement def.
	 * @param def  AND_Refinement to be added.
	 */
	public void addDefinition (String option_name, AND_Refinement def){
		definition.put(option_name, def);
	}
	/**
	 * @return a quality variable that is a parent of an OR_Refinement.
	 */
	@Override
	public QualityVariable getParent() {
		return parent;
	}
	/**
	 * Adds the parent of an OR_Refinement.
	 * @param parent the quality variable that is a parent of the OR_Refinement.
	 */
	public void setParent(QualityVariable parent) {
		this.parent = parent;
	}
	/**
	 * Traverses the model recursively from a OR_Refinement to its children (alternate AND_Refinement) up to the leaf quality variables of the model.
	 * @param m semantic model obtained from parsing.
	 * @return solutions from the leaf quality variables of the decision model constructed up to the point of this OR_Refinement, where solutions are combined.
	 */
	/*@Override
	public SolutionSet getAllSolutions2(Model m){
		SolutionSet result = new SolutionSet();
		for (String option: this.decision.getOptions()){
			AND_Refinement ref = definition.get(option);
			SolutionSet solutions = ref.getAllSolutions(m);
			 if (solutions.isEmpty()){
                Solution s = new Solution();
                s = s.addDecision(this.decision, option);
                result.add(s);
	         }else {
                for(Solution s: solutions.set()){
                    s = s.addDecision(this.decision, option);
                    result.add(s);
                }
	         }
			
		}
		return result;
	}*/
	/**
	 * Local solution set for this OR refinement
	 * @param m
	 * @return
	 */
	public Map<String, SolutionSet> getLocalSolutions(Model m){
		Map<String, SolutionSet> result =  new LinkedHashMap<String, SolutionSet>();
		for (String option: decision.getOptions()){
			AND_Refinement ref = definition.get(option);
			SolutionSet solutions = ref.getAllSolutions(m);
			SolutionSet solutionsSetPerOption = new SolutionSet();
			 if (solutions.isEmpty()){
	                Solution s = new Solution();
	                //s = s.addDecision(decision, option);
	                s = s.addDecision(decision, Arrays.asList(option));
	                solutionsSetPerOption.add(s);
		     }else {
	                for(Solution s: solutions.set()){
	                    //s = s.addDecision(decision, option);
	                    s = s.addDecision(decision, Arrays.asList(option));
	                    solutionsSetPerOption.add(s);
	                }
		     }
			 result.put(option, solutionsSetPerOption);
		}
		return result;
		
	}
	/**
	 * Add the solutions set obtained for each ANDRefinemnet corresponding to each option
	 * @param localSolutionSet
	 * @return
	 */
	SolutionSet allLocalSolutionSet (Map<String, SolutionSet> localSolutionSet){
		SolutionSet allLocalSolutions = new SolutionSet();
		for(Map.Entry<String, SolutionSet> entry: localSolutionSet.entrySet()){
			allLocalSolutions.addAll(entry.getValue());
		}
		return allLocalSolutions;
	}
	/**
	 * To be used for non-mutually exculusive and mutually exclusive selection
	 * @param m
	 * @return
	 */
	public SolutionSet getAllSolutions(Model m){
		SolutionSet result = new SolutionSet();
		Map<String, SolutionSet> localSolutionSet =  getLocalSolutions(m);
		if (xor){
			result = allLocalSolutionSet(localSolutionSet);
		}else{
			List<String> decisionOptionsInBitFors = getBitVector(decision.getOptions().size());
			for (String subSetOfOptionsInBitForm : decisionOptionsInBitFors){
				List<String> subSetOfOptions = getOptionFromBit(subSetOfOptionsInBitForm);
				SolutionSet combinedSubsetOfOptions  = new SolutionSet();
				for(String option: subSetOfOptions){
					for (Map.Entry<String, SolutionSet> entry: localSolutionSet.entrySet()){
						if(option.equals(entry.getKey())){
							combinedSubsetOfOptions.addAll(entry.getValue());
						}
					}
				}
				if (combinedSubsetOfOptions.isEmpty()){
					Solution s = new Solution();
	                s = s.addDecision(decision, subSetOfOptions);
	                result.add(s);
				}
				for(Solution s: combinedSubsetOfOptions.set()){
                    s = s.addDecision(decision, subSetOfOptions);
                    // NB: had to override the equals method in Solution class to allow addition of Solutions with same decision name
                    result.add(s);
                }
			}
		}
		return result;
	}
	List<String> getOptionFromBit (String bit){
		List<String> result =  new ArrayList<String>();
		char [] bitArray =  bit.toCharArray();
		for (int i =0; i < bitArray.length; i ++){
			if (bitArray[i] == '1'){
				result.add(decision.getOptions().get(i));
			}
		}
		return result;
	}
	List<String> getBitVector(int nbrOptions){
		List<String> result = new ArrayList<String>();
		
		for (int i=0; i < Math.pow(2, nbrOptions); i ++){
			BigInteger bit =  new BigInteger (""+i);
			String record =  bit.toString(2);
			String paddedRecord = StringUtils.leftPad(record, nbrOptions, '0');
			result.add(paddedRecord);
		}
		return result;
	}
	/**
	 * Visits the children of OR_Refinement to generate the variable dependency graph.
	 * @param visitor model visitor
	 * @param m semantic model obtained from parsing.
	 */
	@Override
	public void accept(ModelVisitor visitor, Model m) {
		for (AND_Refinement andRef : getAndrefinements()){
			andRef.accept(visitor,m);
		}
		visitor.visit(this);
		
	}
	/**
	 * @return all AND_Refinemnt of a OR_Refinement
	 */
	List<AND_Refinement> getAndrefinements(){
		return new ArrayList<AND_Refinement>(definition.values());
	}
	/**
	 * Traverses the model recursively from a OR_refinement to its children (AND_Refinement) and to the leaf quality variables of the model to check for cyclic dependencies between quality variables.
	 * @param m semantic model obtained from parsing.
	 * @throws CyclicDependencyException if there exist a cyclic dependency between quality variables.
	 */
	@Override
	public void getCyclicDependentVariables(Model m) throws CyclicDependencyException {
		for (AND_Refinement andRef : getAndrefinements()){
			andRef.getCyclicDependentVariables(m);
		}
	}
	@Override
	public double getParamExpressionValue(Model m) throws ParameterDistributionException {
		throw new RuntimeException ("And-Refinement variable cannot be used as an argument for another distribution.");
	}
	
}
