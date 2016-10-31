package inf.ed.gfd.structure;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import inf.ed.graph.structure.Graph;
import inf.ed.graph.structure.adaptor.Pair;
import inf.ed.graph.structure.adaptor.TypedEdge;
import inf.ed.graph.structure.adaptor.VertexString;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;

public class WorkUnit implements  Serializable {
	
	private static final long serialVersionUID = 10L;
	
	public int patternId;
	//public int partitionId;
	//public boolean isConnected;
	public boolean isGfdCheck = false;
	public Int2ObjectMap<Condition> conditions;
	public boolean isIsoCheck = false;
	public boolean isPatternCheck = false;
	
	public List<String> patterns;
	
	//for isomorphism checking
	public Pair<Graph<VertexString, TypedEdge>,Graph<VertexString, TypedEdge>> isoPatterns;
	public Pair<Integer,Integer> isoIds;
	//for pattern;
	public int oriPatternId;
	// String and the pattrn node in the new pattern;
	//add pId
	
	public Int2ObjectMap<Pair<Integer,Pair<Integer,Integer>>> edgeIds;
	//public HashMap<DFS, Pair<Integer,Integer>> edgeIds;
	
	public Int2ObjectMap<DFS> id2Dfs;
	
	public WorkUnit(){
		this.edgeIds = new Int2ObjectOpenHashMap<Pair<Integer,Pair<Integer,Integer>>>();
	}
	

	//for worker to SC;
	//verify pattern's local support
	public WorkUnit(int opId, Int2ObjectMap<Pair<Integer,Pair<Integer,Integer>>>  edgeIds){
		this.oriPatternId = opId;
		this.edgeIds = edgeIds;
		this.isPatternCheck = true;
		this.isIsoCheck = false;
		this.isGfdCheck = false;

	}


	public WorkUnit(int pId, Int2ObjectMap<Condition> dependency, boolean isGfdCheck) {
		// TODO Auto-generated constructor stub
		this.patternId = pId;
		this.conditions = dependency;
		//this.partitionId = parId;
		//this.isConnected = isConnected;
		this.isGfdCheck = true;
		this.isPatternCheck = false;
		this.isIsoCheck = false;
	}
	
	
	public WorkUnit(Pair<Graph<VertexString, TypedEdge>,Graph<VertexString, TypedEdge>> works) {
		// TODO Auto-generated constructor stub
		this.isoPatterns = works;
		this.isIsoCheck = true;
		this.isGfdCheck = false;
		this.isPatternCheck = false;
	}


	public WorkUnit(Int2ObjectMap<DFS> id2Dfs2) {
		// TODO Auto-generated constructor stub
		this.id2Dfs = id2Dfs2;
	}


	public WorkUnit(Pair<Graph<VertexString, TypedEdge>, Graph<VertexString, TypedEdge>> pair,
			Pair<Integer, Integer> pairId) {
		// TODO Auto-generated constructor stub
		this.isoPatterns = pair;
		this.isoIds = pairId;
		this.isIsoCheck = true;
		this.isGfdCheck = false;
		this.isPatternCheck = false;
	}

}
