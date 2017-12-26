package uk.ac.ucl.cs.radar.model;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.ucl.cs.radar.model.Decision;

public final class Solution {

	private boolean isValid;
	private  int violations;
	private final Map<Decision, List<String>> selection;
	private Model sematicModel;
	
	public Solution(){
		selection = new LinkedHashMap<Decision, List<String>>();
	}
	/**
	 * Constructs a solution with a mapping of decisions and selected options.
	 * @param mapping a mapping between decision and selected option.
	 */
	public Solution(Map<Decision, List<String>> mapping){
		selection = mapping;
	}
	/**
	 * Constructs a solution with a mapping of decisions and selected options.
	 * @param mapping a mapping between decision and selected option.
	 */
	/*Solution(Map<Decision, List<String>> mapping){
		selection = mapping;
		nonmutual_selection = mapping;
	}*/
	/**
	 * Adds a decision and corresponding selected option to a solution instance.
	 * @param d decision.
	 * @param option selected option.
	 * @return a new solution instance with the updated decision and selected option.
	 */
	Solution addDecision (Decision d, List<String> option){
		Map<Decision, List<String>> mapping = new LinkedHashMap<Decision, List<String>>(this.selection);
		mapping.put(d, option);
		return new Solution(mapping);
	}

	/**
	* 
	* If a decision is made in both `this` and s, the union keeps the option selected in `this`. 
	* @param s solution s to be combined.
	* @return a new solution that takes the union of all decisions in `this` and `s`.
	*/
	Solution union(Solution s){
		Map<Decision, List<String>> mapping = new LinkedHashMap<Decision, List<String>>(s.selection);
		mapping.putAll(this.selection);
		return new Solution(mapping);		
	}
	/**
	 * @param d decision.
	 * @return the selected option for the given decision d.
	 */
	public List<String> selection (Decision d){
		return selection.get(d);
	}
	
	public boolean containsDecision (String decisionName){
		boolean decisionInSolution =false;
		for(Decision d : selection.keySet()){
			if (d.getDecisionLabel().equals(decisionName)){
				decisionInSolution = true;
			}
		}
		return decisionInSolution;
	}
	/**
	 * @return the set of all decision for a solution instance.
	 */
	Set<Decision> decisions(){
		return selection.keySet();
	}
	/**
	 * @return  the string form of a solution selections, which is a mapping between decisions and options.
	 */
	public String selectionToString (){
		return this.selection.toString();
	}
	/**
	* @return true if both solutions have the same option selection on all their decisions.
	*/
	@Override
    public boolean equals(Object o){
        if (o.getClass() != getClass()) return false;
        Solution s = (Solution) o;
        return this.selection.equals(s.selection);
    }
	@Override
	public int hashCode (){
		return selection.hashCode();
	}
	
	public Model getSemanticModel (){
		return sematicModel;
	}
	public void setSemanticModel(Model model){
		sematicModel =  model;
	}
	/**
	* @return a solution selection, a mapping between decisions and options
	*/
	public Map<Decision, List<String>> getSelection(){
		return selection;
	}
	public List<String> getDecisionSelection(String decisonName){
		List<String> result = new ArrayList<String>();
		for(Map.Entry<Decision, List<String>> entry: selection.entrySet()){
			if(entry.getKey().getDecisionLabel().equals(decisonName)){
				return entry.getValue();
			}
		}
		return result;
	}
	public void setIsValidSolution (boolean isValid){
		this.isValid = isValid;
	}
	public void incrementViolationCounter (){
		this.violations++;
	}
	public double getNbrViolations (){
		return (double)violations;
	}
	
	
}
