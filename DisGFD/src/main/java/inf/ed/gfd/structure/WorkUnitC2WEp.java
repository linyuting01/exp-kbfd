package inf.ed.gfd.structure;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

import inf.ed.graph.structure.adaptor.Pair;
import it.unimi.dsi.fastutil.ints.IntSet;

public class WorkUnitC2WEp implements Serializable {
	/**
	 * 
		 */
	private static final long serialVersionUID = 1L;
	
	public String oriPatternId;
	// String and the pattrn node in the new pattern;
	public HashMap<DFS, Pair<Integer>> edgeIds;
	
	public WorkUnitC2WEp(){
		this.edgeIds = new  HashMap<DFS, Pair<Integer>>();
	}
	
	//for worker to SC;
	//verify pattern's local support
	public WorkUnitC2WEp(String opId, HashMap<DFS,Pair<Integer>> edgeIds){
		this.oriPatternId = opId;
		this.edgeIds = edgeIds;
	}
	
	//for gfd;checking  literal revise
	

}
