package inf.ed.gfd.structure;


import java.io.Serializable;

import it.unimi.dsi.fastutil.ints.IntSet;

public class WorkUnitW2C implements Serializable {
	/**
		 * 
		 */
	private static final long serialVersionUID = 1L;

	
	public String patternId; 
	public String oriPatternId;
	public String edgeId;
	public int fId; 
	public int tId;
	public String conditionId;
	public int support;
	public boolean satisfy;
	public int partitionId;
	public boolean isConnected;
	public IntSet patterIds;
	
	public WorkUnitW2C(){
		
	}
	
	//for worker to SC;
	public WorkUnitW2C(String pId, String cId, int supp, boolean satisfy, int parId, boolean isConnected){
		this.patternId = pId;
		this.conditionId = cId;
		this.support = supp;
		this.satisfy = satisfy;
		this.partitionId = parId;
		this.isConnected = isConnected;
	}
	
	
	
	

	
}
