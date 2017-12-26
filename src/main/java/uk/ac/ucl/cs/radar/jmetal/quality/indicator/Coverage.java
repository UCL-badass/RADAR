package uk.ac.ucl.cs.radar.jmetal.quality.indicator;

import org.uma.jmetal.qualityindicator.impl.GenericIndicator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.point.Point;

import java.io.FileNotFoundException;
import java.util.List;

//Epsilon<S extends Solution<?>> extends GenericIndicator<S> 

@SuppressWarnings("serial")
public class Coverage<S extends Solution<?>> extends GenericIndicator<S>  {
	
	public Coverage(){}
	/**
	* Constructor
	*
	* @param referenceParetoFrontFile
	* @throws FileNotFoundException
	*/
	public Coverage(String referenceParetoFrontFile) throws FileNotFoundException {
		super(referenceParetoFrontFile) ;
		/*if (referenceParetoFrontFile == null) {
		  throw new JMetalException("The pareto front object is null");
		}
		
		Front front = new ArrayFront(referenceParetoFrontFile);
		referenceParetoFront = front ;*/
	}
	
	/**
	* Constructor
	*
	* @param referenceParetoFront
	*/
	public Coverage(Front referenceParetoFront) {
		super(referenceParetoFront) ;
		/*if (referenceParetoFront == null) {
		  throw new JMetalException("\"The Pareto front approximation is null");
		}
		
		this.referenceParetoFront = referenceParetoFront ;*/
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
		return coverage(new ArrayFront(solutionList), referenceParetoFront);
	}
	
	/**
	* Returns the value of the error ratio indicator.
	*
	* @param front Solution front
	* @param referenceFront True Pareto front
	*
	* @return the value of the error ratio indicator
	* @throws JMetalException
	*/
	private double coverage(Front front, Front referenceFront) throws JMetalException {
		front = removeDuplicateFromFront(front);
		int numberOfObjectives = referenceFront.getPointDimensions() ;
		double sum = 0;
		
		for (int i = 0; i < front.getNumberOfPoints(); i++) {
		  Point currentPoint = front.getPoint(i);
		  boolean thePointIsInTheParetoFront = false;
		  for (int j = 0; j < referenceFront.getNumberOfPoints(); j++) {
		    Point currentParetoFrontPoint = referenceFront.getPoint(j);
		    boolean found = true;
		    for (int k = 0; k < numberOfObjectives; k++) {
		    	
		      if(currentPoint.getDimensionValue(k) != currentParetoFrontPoint.getDimensionValue(k)){
		        found = false;
		        break;
		      }
		    }
		    if(found){
		      thePointIsInTheParetoFront = true;
		      break;
		    }
		  }
		  if(thePointIsInTheParetoFront){
		    sum++;
		  }
		}
	
		return sum / referenceFront.getNumberOfPoints();
	}
	
	@Override public String getName() {
	return super.getName() ;
	}

	@Override
	public boolean isTheLowerTheIndicatorValueTheBetter() {
		return false;
	}
}

