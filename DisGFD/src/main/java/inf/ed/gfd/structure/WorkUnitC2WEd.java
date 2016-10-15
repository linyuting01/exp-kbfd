package inf.ed.gfd.structure;

import java.io.Serializable;

import it.unimi.dsi.fastutil.ints.IntSet;

public class WorkUnitC2WEd implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public String patternId;
	public String conditionId;
	public int partitionId;
	public boolean isConnected;
	public IntSet patternIds = null;

	public WorkUnitC2WEd(){
		
	}
	
	
	public WorkUnitC2WEd(String pId, String cId, int supp, boolean satisfy, int parId, boolean isConnected){
		this.patternId = pId;
		this.conditionId = cId;;
		this.partitionId = parId;
		this.isConnected = isConnected;
	}
}
