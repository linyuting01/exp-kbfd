package inf.ed.gfd.structure;

import java.io.Serializable;
import java.util.HashMap;

import inf.ed.graph.structure.adaptor.Pair;
import it.unimi.dsi.fastutil.ints.IntSet;

public class WorkUnit implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public String patternId;
	public String conditionId;
	public int partitionId;
	public boolean isConnected;
	public IntSet patternIds;
	
	//for pattern;
	public String oriPatternId;
	// String and the pattrn node in the new pattern;
	public HashMap<DFS, Pair<Integer,Integer>> edgeIds;
	
	public WorkUnit(){
		this.edgeIds = new HashMap<DFS, Pair<Integer,Integer>>();
	}
	
	
	public WorkUnit(String pId, String cId, int supp, boolean satisfy, int parId, boolean isConnected){
		this.patternId = pId;
		this.conditionId = cId;;
		this.partitionId = parId;
		this.isConnected = isConnected;
	}
	//for worker to SC;
	//verify pattern's local support
	public WorkUnit(String opId, HashMap<DFS,Pair<Integer,Integer>> edgeIds){
		this.oriPatternId = opId;
		this.edgeIds = edgeIds;
	}
}
