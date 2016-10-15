package inf.ed.gfd.structure;

import java.io.Serializable;
import java.util.Set;

import inf.ed.graph.structure.adaptor.Pair;
import it.unimi.dsi.fastutil.ints.IntSet;

public class WorkUnitC2W implements Serializable {
	/**
	 * 
		 */
	private static final long serialVersionUID = 1L;
	public String patternId; 
	public String oriPatternId;
	public Set<String> edgeIds = null;
	public Set<Pair<Integer>> nodeIds = null;
	public String conditionId;
	public int partitionId;
	public boolean isConnected;
	public IntSet patternIds = null;
	public boolean extend; // extend pattern or check satisfiable.
	
	public WorkUnitC2W(){
		
	}
	
	//for worker to SC;
	//verify pattern's local support
	public WorkUnitC2W(String opId, Set<String> edgeIds, Set<Pair<Integer>> nodeIds){
		this.oriPatternId = opId;
		this.edgeIds = edgeIds;
		this.nodeIds = nodeIds;
		this.extend = true;
	}
	
	//for gfd;checking  literal revise
	public WorkUnitC2W(String pId, String cId, int supp, boolean satisfy, int parId, boolean isConnected){
		this.patternId = pId;
		this.conditionId = cId;;
		this.partitionId = parId;
		this.isConnected = isConnected;
	}

}
