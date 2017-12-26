package uk.ac.ucl.cs.radar.model;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class RequireConstraint extends Constraint implements IConstraint {

	@Override
	public void isSolutionValid(Solution s, Model m) {
		
		//get all the constraints that are 'requires'
		// for each one:
		//    get the left and right arguments
		//    get the left and right decion-option pair
		//    from Solution s, get the decison d that corresponds to left and right argument decisions
		//    And check that the options that is required is present
		//    if yes, return true, else return false and increment violation count
		s.setIsValidSolution(true);
		List<RequireConstraint> requireConstraints = m.getRequireConstraints();
		for (RequireConstraint reqConstraint : requireConstraints){
			ConstraintArgument leftArg = reqConstraint.getLeftConstraintArgumnet();
			ConstraintArgument rightArg = reqConstraint.getRightConstraintArgumnet();
			
			// if left constraint is a decision only and right is decision only?
			if (StringUtils.isEmpty(leftArg.getOption()) && StringUtils.isEmpty(rightArg.getOption())){
				List<String> leftArgOptionSelection = s.getDecisionSelection(leftArg.getDecision());
				// solution has the decision in question and there is a selection for that decision i.e we cannot have a null selection
				if (s.containsDecision(leftArg.getDecision()) && leftArgOptionSelection.size() > 0){
					List<String> rightArgOptionSelection = s.getDecisionSelection(rightArg.getDecision());
					// an option must be selected for the right hand decision
					if(!s.containsDecision(rightArg.getDecision())){
						s.setIsValidSolution(false);
						s.incrementViolationCounter();
					}
					else if(s.containsDecision(rightArg.getDecision()) && rightArgOptionSelection.size() <= 0){
						s.setIsValidSolution(false);
						s.incrementViolationCounter();
					}
				}
			}
			
			// if left constraint is a decision only and right has decision option?
			if (StringUtils.isEmpty(leftArg.getOption()) && !StringUtils.isEmpty(rightArg.getOption())){
				if (s.containsDecision(leftArg.getDecision())){
					List<String> rightArgOptionSelection = s.getDecisionSelection(rightArg.getDecision());
					// if solution s does not contain the option in the right argument, and the options of the left decision in question is not empty
					//&& s.getDecisionSelection(rightArg.getDecision()).size() >0
					if(!rightArgOptionSelection.contains(rightArg.getOption()) && s.getDecisionSelection(leftArg.getDecision()).size() >0 ){
						s.setIsValidSolution(false);
						s.incrementViolationCounter();
					}
					
				}
			}
			
			// if left constraint has decision option and right is decision only?
			if (!StringUtils.isEmpty(leftArg.getOption()) && StringUtils.isEmpty(rightArg.getOption())){
				if (s.containsDecision(leftArg.getDecision())){
					List<String> leftArgOptionSelection = s.getDecisionSelection(leftArg.getDecision());
					if (leftArgOptionSelection != null && leftArgOptionSelection.size()>= 0 
							&& leftArgOptionSelection.contains(leftArg.getOption())){
						
						List<String> rightArgOptionSelection = s.getDecisionSelection(rightArg.getDecision());
						if(!rightArgOptionSelection.contains(rightArg.getOption())){
							s.setIsValidSolution(false);
							s.incrementViolationCounter();
						}
						
					}
					
				}
			}			
			// if left constraint has decision option and right has decision option?		
			if (!StringUtils.isEmpty(leftArg.getOption()) && !StringUtils.isEmpty(rightArg.getOption())){
				if (s.containsDecision(leftArg.getDecision())){
					List<String> leftArgOptionSelection = s.getDecisionSelection(leftArg.getDecision());
					if (leftArgOptionSelection != null && leftArgOptionSelection.size()>= 0 
							&& leftArgOptionSelection.contains(leftArg.getOption())){
						List<String> rightArgOptionSelection = s.getDecisionSelection(rightArg.getDecision());
						if(rightArgOptionSelection.size()>= 0 
								&& !rightArgOptionSelection.contains(rightArg.getOption())){
							s.setIsValidSolution(false);
							s.incrementViolationCounter();
						}
					}
				}
				
			}		
		}
	}

}
