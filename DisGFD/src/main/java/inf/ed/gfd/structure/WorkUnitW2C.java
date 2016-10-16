package inf.ed.gfd.structure;


import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.IntSet;

public class WorkUnitW2C implements Serializable {
	/**
		 * 
		 */
	private static final long serialVersionUID = 1L;

	
	public String patternId; 
	public String conditionId;
	public int support;
	public boolean sat;
	public int partitionId;
	public boolean isConnected;
	public IntSet patterIds; 
	public List<Int2IntMap> boderMatch;
	
	public WorkUnitW2C(){
		
	}
	
	//for worker to SC;
	public WorkUnitW2C(String pId, String cId, int supp, boolean satisfy, int parId, boolean isConnected){
		this.patternId = pId;
		this.conditionId = cId;
		this.support = supp;
		this.sat = satisfy;
		this.partitionId = parId;
		this.isConnected = isConnected;
	}
	//for pattern;
	public WorkUnitW2C(String pId, int supp, int parId){
		this.patternId = pId;
		this.support = supp;
		this.partitionId = parId;
	}
	
	public WorkUnitW2C(String pId, int supp, int parId, List<Int2IntMap> boderMatch){
		this.patternId = pId;
		this.support = supp;
		this.partitionId = parId;
		this.boderMatch =  boderMatch;
	}
	
	
	
	

	
}
