package uk.ac.ucl.cs.radar.model;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class ExcludeConstraint extends Constraint implements IConstraint {

	@Override
	public void isSolutionValid(Solution s, Model m) {
		s.setIsValidSolution(true);
		List<ExcludeConstraint> excludeConstraints = m.getExcludeConstraints();
		for (ExcludeConstraint excludeConstraint : excludeConstraints){
			ConstraintArgument leftArg = excludeConstraint.getLeftConstraintArgumnet();
			ConstraintArgument rightArg = excludeConstraint.getRightConstraintArgumnet();
			
			
			// if left constraint is a decision only and right is decision only?
			if (StringUtils.isEmpty(leftArg.getOption()) && StringUtils.isEmpty(rightArg.getOption())){
				if (s.containsDecision(leftArg.getDecision()) && s.containsDecision(rightArg.getDecision())){
						s.setIsValidSolution(false);
						s.incrementViolationCounter();
				}
			}
			
			// if left constraint is a decision only and right has decision option?
			if (StringUtils.isEmpty(leftArg.getOption()) && !StringUtils.isEmpty(rightArg.getOption())){
				if (s.containsDecision(leftArg.getDecision())){
					List<String> rightArgOptionSelection = s.getDecisionSelection(rightArg.getDecision());
					// if solution s contain the option in the right argument, and the options of the decision in question is not empty
					if(rightArgOptionSelection.contains(rightArg.getOption()) && s.getDecisionSelection(leftArg.getDecision()).size() >0){
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
						if(rightArgOptionSelection.size() >0){
							s.setIsValidSolution(false);
							s.incrementViolationCounter();
						}
					}
					/*List<String> rightArgOptionSelection = s.getDecisionSelection(rightArg.getDecision());
					if(rightArgOptionSelection.size() <= 0){
						s.setIsValidSolution(false);
						s.incrementViolationCounter();
					}*/
				}
			}
			
			// if left constraint has decision option and right has decision option?
			if (!StringUtils.isEmpty(leftArg.getOption()) && !StringUtils.isEmpty(rightArg.getOption())){
				List<String> leftArgOptionSelection = s.getDecisionSelection(leftArg.getDecision());
				if (leftArgOptionSelection != null && leftArgOptionSelection.size()>= 0 
						&& leftArgOptionSelection.contains(leftArg.getOption())){
					List<String> rightArgOptionSelection = s.getDecisionSelection(rightArg.getDecision());
					if(rightArgOptionSelection != null && rightArgOptionSelection.size()>= 0 &&
							rightArgOptionSelection.contains(rightArg.getOption())){
						s.setIsValidSolution(false);
						s.incrementViolationCounter();
					}
				}
			}
			
		}
		
	}

}
