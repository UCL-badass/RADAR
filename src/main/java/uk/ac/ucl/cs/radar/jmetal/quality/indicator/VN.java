package uk.ac.ucl.cs.radar.jmetal.quality.indicator;

import org.uma.jmetal.qualityindicator.impl.GenericIndicator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.point.Point;

import java.io.FileNotFoundException;
import java.util.List;

@SuppressWarnings("serial")
public class VN<S extends Solution<?>> extends GenericIndicator<S>  {
	private Front front ;
	int nummberOfRuns =0;

	public VN(){
		
	}
	/**
	* Constructor
	*
	* @param referenceParetoFrontFile
	* @throws FileNotFoundException
	*/
	public VN(String frontFile) throws FileNotFoundException {
		super(frontFile) ;
		if (frontFile == null) {
		  throw new JMetalException("The pareto front object is null");
		}
		
		Front front = new ArrayFront(frontFile);
		this.front = front ;
	}
	
	/**
	* Constructor
	*
	* @param referenceParetoFront
	*/
	public VN(Front front) {
		super(front) ;
		if (front == null) {
		  throw new JMetalException("\"The Pareto front approximation is null");
		}
		
		this.front = front ;
	}
	
	/**
	* Evaluate() method
	* @param solutionList
	* @return
	*/
	@Override public Double evaluate(List<S> solutionList) {
		if (solutionList == null) {
			  throw new JMetalException("The solution list is null") ;
		}
		this.front = new ArrayFront(solutionList);
		return vn(solutionList);
	}
	
	/**
	* 
	*
	* @param front Solution front
	* @param referenceFront True Pareto front
	*
	* @return the value of the error ratio indicator
	* @throws JMetalException
	*/
	private double vn(List<S> solutionList) throws JMetalException {
		//front = removeDuplicateFromFront(front);
		double sum = 0.0;
		if (front.getNumberOfPoints() > 0){
			int numberOfObjectives = front.getPointDimensions() ;
			for (int i = 0; i < front.getNumberOfPoints(); i++) {
				 Point currentPoint = front.getPoint(i);
				  if (currentPoint.getDimensionValue(numberOfObjectives-1) <= 0){
						 sum += 1;
				  }
		      }
		}
		 return sum;
	}
	
	@Override public String getName() {
		return super.getName() ;
	}
	public void setNumberOfRuns (int nummberOfRuns){
		this.nummberOfRuns =nummberOfRuns;
	}

	@Override
	public boolean isTheLowerTheIndicatorValueTheBetter() {
		return false;
	}
	

}

