package inf.ed.gfd.structure;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import inf.ed.graph.structure.Graph;
import inf.ed.graph.structure.adaptor.Pair;
import inf.ed.graph.structure.adaptor.TypedEdge;
import inf.ed.graph.structure.adaptor.VertexString;
import it.unimi.dsi.fastutil.ints.IntSet;

public class WorkUnit implements Serializable {
	
	private static final long serialVersionUID = 10L;
	
	public String patternId;
	public String conditionId;
	//public int partitionId;
	public boolean isConnected;
	public boolean isGfdCheck;
	public Condition condition;
	public List<String> patterns;
	
	//for isomorphism checking
	public HashMap<Integer,Graph<VertexString, TypedEdge>> isoPatterns;
	//for pattern;
	public String oriPatternId;
	// String and the pattrn node in the new pattern;
	public HashMap<DFS, Pair<Integer,Integer>> edgeIds;
	
	public WorkUnit(){
		this.edgeIds = new HashMap<DFS, Pair<Integer,Integer>>();
	}
	
	
	public WorkUnit(String pId, String cId, boolean isConnected,boolean gfdCheck){
		this.patternId = pId;
		this.conditionId = cId;;
		//this.partitionId = parId;
		this.isConnected = isConnected;
		this.isGfdCheck = gfdCheck;
	}
	//for worker to SC;
	//verify pattern's local support
	public WorkUnit(String opId, HashMap<DFS,Pair<Integer,Integer>> edgeIds){
		this.oriPatternId = opId;
		this.edgeIds = edgeIds;
	}


	public WorkUnit(String pId, Condition dependency, boolean isConnected,boolean gfdCheck) {
		// TODO Auto-generated constructor stub
		this.patternId = pId;
		this.condition = dependency;
		//this.partitionId = parId;
		this.isConnected = isConnected;
		this.isGfdCheck = gfdCheck;
	}
	
	//for disconnected gfd check;
	public WorkUnit(List<String> ps, Condition dependency, boolean isConnected, boolean gfdCheck){
		this.patterns = ps;
		this.condition = dependency;
		//this.partitionId = parId;
		this.isConnected = isConnected;
		this.isGfdCheck = gfdCheck;
	}
}
