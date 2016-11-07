package inf.ed.gfd.structure;


import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import inf.ed.graph.structure.adaptor.Pair;
import inf.ed.graph.structure.adaptor.VertexOString;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class GfdMsg implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public  Int2ObjectMap<List<Pair<Integer,Integer>>> transferingEdgeMatch= new  
			Int2ObjectOpenHashMap<List<Pair<Integer,Integer>>>();
	
	public Int2ObjectMap<List<Int2IntMap>> patternmatches = new Int2ObjectOpenHashMap<List<Int2IntMap>>();
	
	public Int2ObjectMap<List<TransAttr>> transAttr_Map = new Int2ObjectOpenHashMap<List<TransAttr>>();
	
	public int partitionId;
	
	public GfdMsg(){
	}
	public GfdMsg( Int2ObjectMap<List<Pair<Integer,Integer>>>  transferingEdgeMatch, int pId){
		this.transferingEdgeMatch = transferingEdgeMatch;
		this.partitionId = pId;
	}


	public String toString() {
		String ret = "";
		int size = 0;
		for(Entry<Integer, List<Pair<Integer,Integer>>> entry : transferingEdgeMatch.entrySet()){
			size =entry.getValue().size();
			ret += " transfering edge pattern match" + entry.getKey() + " node size = " + size;
			
		}
	return ret;
		
	}
	public void clear(){
		this.transferingEdgeMatch.clear();
		this.patternmatches.clear();
		
	}
	
	
}
