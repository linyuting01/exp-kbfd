package inf.ed.gfd.structure;


import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import inf.ed.graph.structure.adaptor.Pair;
import inf.ed.graph.structure.adaptor.VertexOString;

public class GfdMsg implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public  HashMap<String, List<Pair<VertexOString,VertexOString>>> transferingEdgeMatch= new  HashMap<String, 
			List<Pair<VertexOString,VertexOString>>>();
	public int partitionId;
	
	public GfdMsg(){
		
	}
	public GfdMsg( HashMap<String, List<Pair<VertexOString,VertexOString>>> transferingEdgeMatch, int pId){
		this.transferingEdgeMatch = transferingEdgeMatch;
		this.partitionId = pId;
	}


	public String toString() {
		int size = 0;
		for(Entry<String, List<Pair<VertexOString,VertexOString>>> entry : transferingEdgeMatch.entrySet()){
			size =size + entry.getValue().size();
		}
		String ret = "";
		ret += " transfering match node size = " + 2*size;
		return ret;
	}
	public void clear(){
		this.transferingEdgeMatch.clear();
		
	}
}
