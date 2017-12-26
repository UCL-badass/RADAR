package uk.ac.ucl.cs.radar.utilities;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import uk.ac.ucl.cs.radar.jmetal.solution.encoding.SolutionEncoding;
import uk.ac.ucl.cs.radar.model.Decision;
import uk.ac.ucl.cs.radar.model.Model;

public class JmetalUtil {

	  public static Map <Decision, List<String>> getSelectionFromDecsionVariables (SolutionEncoding solution, Model semanticModel){
		  Map <Decision, List<String>> result  = new LinkedHashMap<Decision, List<String>>();
		  List<Integer[]>  decisionVariables = new ArrayList<>(solution.getDecisionVariables());
		  List<Decision> decisions = new ArrayList<>(semanticModel.getDecisions());
		  int i =0;
		  for (Integer[] decisionVariable: decisionVariables){
			  List<String> options = new ArrayList<>();
			  for (int j =0; j < decisionVariable.length; j ++){
				  if (decisionVariable[j] == 1){
					  options.add(decisions.get(i).getOptions().get(j));
				  }
			  }
			  result.put(decisions.get(i), options);
			  i++;
		  }
		  return result;
	  }
	/*  
	  public static String solutionSelectionToString( Map <Decision, List<String>> selection){
		  String result = "";
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
	  }*/
}
