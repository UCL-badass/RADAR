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
public class Precision<S extends Solution<?>> extends GenericIndicator<S>  {

	public Precision(){
		
	}
	/**
	* Constructor
	*
	* @param referenceParetoFrontFile
	* @throws FileNotFoundException
	*/
	public Precision(String referenceParetoFrontFile) throws FileNotFoundException {
		super(referenceParetoFrontFile) ;
		if (referenceParetoFrontFile == null) {
		  throw new JMetalException("The pareto front object is null");
		}
		
		Front front = new ArrayFront(referenceParetoFrontFile);
		referenceParetoFront = front ;
	}
	
	/**
	* Constructor
	*
	* @param referenceParetoFront
	*/
	public Precision(Front referenceParetoFront) {
		super(referenceParetoFront) ;
		if (referenceParetoFront == null) {
		  throw new JMetalException("\"The Pareto front approximation is null");
		}
		
		this.referenceParetoFront = referenceParetoFront ;
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
		return precision(new ArrayFront(solutionList), referenceParetoFront);
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
	private double precision(Front front, Front referenceFront) throws JMetalException {
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
	
		return sum / front.getNumberOfPoints();
	}
	
	@Override public String getName() {
	return super.getName() ;
	}

	@Override
	public boolean isTheLowerTheIndicatorValueTheBetter() {
		return false;
	}
}


