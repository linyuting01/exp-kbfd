package inf.ed.gfd.structure;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import inf.ed.graph.structure.adaptor.Pair;
import it.unimi.dsi.fastutil.ints.IntSet;

public class DisWorkUnit  implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public Set<Pair<Integer,Integer>> disPatterns;
	public HashMap<Integer,HashMap<Integer,HashMap<String,IntSet>>> pivot;
	public int nodeNum;
	//public HashMap<Integer,HashMap<String,IntSet>> pivot2;
	

	public DisWorkUnit() {
		// TODO Auto-generated constructor stub
		this.pivot = new HashMap<Integer,HashMap<Integer,HashMap<String,IntSet>>>();
		this.disPatterns = new HashSet<Pair<Integer,Integer>>();
	}
}
