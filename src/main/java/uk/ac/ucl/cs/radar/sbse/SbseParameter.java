package uk.ac.ucl.cs.radar.sbse;

public class SbseParameter {
	boolean useDefault;
	int maxEvaluation;
	int populationSize;
	String algorithmName;
	double crossOverProbability;
	double mutationProbability;
	boolean addConstraintAsObjective = true;
	
	public void setAlgorithmName (String algorithmName){
		this.algorithmName = algorithmName;
	}
	public String getAlgorithmName (){
		return algorithmName;
	}
	public void setMaximumEvaluation (int maxEvaluation){
		this.maxEvaluation = maxEvaluation;
	}
	public int getMaximunEvaluation (){
		return maxEvaluation;
	}
	public void setPopulationSize (int populationSize){
		this.populationSize = populationSize;
	}
	public int getPopulationSize(){
		return populationSize;
	}
	public void setCrossOverProbability (double crossOverProbability){
		this.crossOverProbability = crossOverProbability;
	}
	public double getCrossOverProbability(){
		return crossOverProbability;
	}
	public void setMutationProbability (double mutationProbability){
		this.mutationProbability = mutationProbability;
	}
	public double getMutationProbability(){
		return mutationProbability;
	}
	public void addConstraintAsObjective (boolean addConstraintAsObjective){
		this.addConstraintAsObjective = addConstraintAsObjective;
	}
	public boolean isConstraintAddedToObjective(){
		return addConstraintAsObjective;
	}
	public void useDefaultParameter (boolean useDefaultParam){
		this.useDefault = useDefaultParam;
	}
	public boolean getDefaultParameter(){
		return useDefault;
	}

	
}
