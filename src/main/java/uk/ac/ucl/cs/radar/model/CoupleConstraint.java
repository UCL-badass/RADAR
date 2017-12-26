package uk.ac.ucl.cs.radar.model;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class CoupleConstraint extends Constraint implements IConstraint{

	@Override
	public void isSolutionValid(Solution s, Model m) {
		s.setIsValidSolution(true);
		List<CoupleConstraint> andConstraints = m.getCoupledConstraints();
		for (CoupleConstraint andConstraint : andConstraints){
			ConstraintArgument leftArg = andConstraint.getLeftConstraintArgumnet();
			ConstraintArgument rightArg = andConstraint.getRightConstraintArgumnet();
			
			
			// if left constraint is a decision only and right is decision only?
			if (StringUtils.isEmpty(leftArg.getOption()) && StringUtils.isEmpty(rightArg.getOption())){
				if ((s.containsDecision(leftArg.getDecision()) && !s.containsDecision(rightArg.getDecision())) 
						|| (!s.containsDecision(leftArg.getDecision()) && s.containsDecision(rightArg.getDecision()))){
						s.setIsValidSolution(false);
						s.incrementViolationCounter();
				}
				if ((s.getDecisionSelection(leftArg.getDecision()).size() <= 0 && s.getDecisionSelection(rightArg.getDecision()).size() >0) 
						|| ((s.getDecisionSelection(leftArg.getDecision()).size() > 0 && s.getDecisionSelection(rightArg.getDecision()).size() <= 0)) ){
					s.setIsValidSolution(false);
					s.incrementViolationCounter();
				}
				if ((s.getDecisionSelection(leftArg.getDecision()).size() <= 0 && s.getDecisionSelection(rightArg.getDecision()).size() <= 0) ){
					s.setIsValidSolution(false);
					s.incrementViolationCounter();
				}
			}
			
			// if left constraint is a decision only and right has decision option?
			if (StringUtils.isEmpty(leftArg.getOption()) && !StringUtils.isEmpty(rightArg.getOption())){
				if (s.containsDecision(leftArg.getDecision())){
					List<String> rightArgOptionSelection = s.getDecisionSelection(rightArg.getDecision());
					// if solution s contain the option in the right argument, and the options of the decision in question is not empty
					if((rightArgOptionSelection.contains(rightArg.getOption()) && s.getDecisionSelection(leftArg.getDecision()).size() < 0)
							|| (!rightArgOptionSelection.contains(rightArg.getOption()) && s.getDecisionSelection(leftArg.getDecision()).size() > 0) ){
						s.setIsValidSolution(false);
						s.incrementViolationCounter();
					}
				}
			}
			
			// if left constraint has decision option and right is decision only?
			if (!StringUtils.isEmpty(leftArg.getOption()) && StringUtils.isEmpty(rightArg.getOption())){
				if (s.containsDecision(leftArg.getDecision())){
					List<String> leftArgOptionSelection = s.getDecisionSelection(leftArg.getDecision());
					if ((leftArgOptionSelection != null && leftArgOptionSelection.size()>= 0 && leftArgOptionSelection.contains(leftArg.getOption()) && s.getDecisionSelection(rightArg.getDecision()).size() <= 0)
							|| (leftArgOptionSelection != null && leftArgOptionSelection.size()>= 0 && !leftArgOptionSelection.contains(leftArg.getOption()) && s.getDecisionSelection(rightArg.getDecision()).size() > 0)){
							s.setIsValidSolution(false);
							s.incrementViolationCounter();
					}
				}
			}
			
			// if left constraint has decision option and right has decision option?
			if (!StringUtils.isEmpty(leftArg.getOption()) && !StringUtils.isEmpty(rightArg.getOption())){
				List<String> leftArgOptionSelection = s.getDecisionSelection(leftArg.getDecision());
				if ((leftArgOptionSelection != null && leftArgOptionSelection.size()>= 0 && leftArgOptionSelection.contains(leftArg.getOption())
						&& s.getDecisionSelection(rightArg.getDecision()) != null && s.getDecisionSelection(rightArg.getDecision()).size() >= 0 && !s.getDecisionSelection(rightArg.getDecision()).contains(rightArg.getOption()))
						|| (leftArgOptionSelection != null && leftArgOptionSelection.size()>= 0 && !leftArgOptionSelection.contains(leftArg.getOption())
								&& s.getDecisionSelection(rightArg.getDecision()) != null && s.getDecisionSelection(rightArg.getDecision()).size() >= 0 && s.getDecisionSelection(rightArg.getDecision()).contains(rightArg.getOption()))){
						s.setIsValidSolution(false);
						s.incrementViolationCounter();
					
				}
			}
			
		}
		
	}
}
