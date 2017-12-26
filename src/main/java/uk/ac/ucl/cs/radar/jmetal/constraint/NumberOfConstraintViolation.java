package uk.ac.ucl.cs.radar.jmetal.constraint;

//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU Lesser General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU Lesser General Public License for more details.
//
//You should have received a copy of the GNU Lesser General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>.
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ConstraintViolationComparator;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;

/**
* This class implements a <code>Comparator</code> (a method for comparing <code>Solution</code> objects)
* based on the overall constraint violation of the solutions, as done in NSGA-II.
*
* @author Antonio J. Nebro <antonio@lcc.uma.es>
*/
@SuppressWarnings("serial")
public class NumberOfConstraintViolation<S extends Solution<?>>
implements ConstraintViolationComparator<S> {
	private NumberOfViolatedConstraints<S> numberOfViolatedConstraints ;
	
	/**
	* Constructor
	*/
	public NumberOfConstraintViolation() {
		numberOfViolatedConstraints = new NumberOfViolatedConstraints<S>() ;
	}
	
	/**
	* Compares two solutions. If the solutions has no constraints the method return 0
	*
	* @param solution1 Object representing the first <code>Solution</code>.
	* @param solution2 Object representing the second <code>Solution</code>.
	* @return -1, or 0, or 1 if o1 is less than, equal, or greater than o2,
	* respectively.
	*/
	public int compare(S solution1, S solution2) {
		double violatedConstraintSolution1 ;
		double violatedConstraintSolution2;
		if (numberOfViolatedConstraints.getAttribute(solution1) == null) {
		  return 0 ;
		}
		violatedConstraintSolution1 =  numberOfViolatedConstraints.getAttribute(solution1);
		violatedConstraintSolution2 = numberOfViolatedConstraints.getAttribute(solution2);
		
		if ((violatedConstraintSolution1 < 0) && (violatedConstraintSolution2 < 0)) {
		  if (violatedConstraintSolution1 > violatedConstraintSolution2) {
		    return -1;
		  } else if (violatedConstraintSolution2 > violatedConstraintSolution1) {
		    return 1;
		  } else {
		    return 0;
		  }
		} else if ((violatedConstraintSolution1 == 0) && (violatedConstraintSolution2 < 0)) {
		  return -1;
		} else if ((violatedConstraintSolution1 < 0) && (violatedConstraintSolution2 == 0)) {
		  return 1;
		} else {
		  return 0;
		}
	}
}

