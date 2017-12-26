package uk.ac.ucl.cs.radar.model;

public class ConstraintViolation extends Statistic{

	@Override
	double evaluate(Solution s, QualityVariable var) {
		return s.getNbrViolations();
	}

}
