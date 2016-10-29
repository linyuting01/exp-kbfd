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

public class WorkUnit implements Comparable<WorkUnit>, Serializable {
	
	private static final long serialVersionUID = 10L;
	
	public int patternId;
	//public int partitionId;
	//public boolean isConnected;
	public boolean isGfdCheck;
	public Int2ObjectMap<Condition> conditions;
	public boolean isIsoCheck;
	public boolean isPatternCheck;
	
	public List<String> patterns;
	
	//for isomorphism checking
	public HashMap<Integer,Graph<VertexString, TypedEdge>> isoPatterns;
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

	}


	public WorkUnit(int pId, Int2ObjectMap<Condition> dependency, boolean isGfdCheck) {
		// TODO Auto-generated constructor stub
		this.patternId = pId;
		this.conditions = dependency;
		//this.partitionId = parId;
		//this.isConnected = isConnected;
		this.isGfdCheck = true;
	}
	
	
	public WorkUnit(HashMap<Integer, Graph<VertexString, TypedEdge>> works) {
		// TODO Auto-generated constructor stub
		this.isoPatterns = works;
		this.isIsoCheck = true;
	}


	public WorkUnit(Int2ObjectMap<DFS> id2Dfs2) {
		// TODO Auto-generated constructor stub
		this.id2Dfs = id2Dfs2;
	}


	public int EtIsoWork(){
		return isoPatterns.size();
	}

	@Override
	public int compareTo(WorkUnit o) {
		// TODO Auto-generated method stub
		return this.EtIsoWork() - o.EtIsoWork();
	}
}
