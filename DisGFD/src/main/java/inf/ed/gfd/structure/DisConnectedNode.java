package inf.ed.gfd.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import inf.ed.gfd.util.Params;
import it.unimi.dsi.fastutil.ints.IntSet;

public class DisConnectedNode {
	static Logger log = LogManager.getLogger(DisConnectedNode.class);
	
	public DisConnectedNode parent;
	public List<DisConnectedNode> children;
	//public String pId;
	public List<Integer> patterns;
	public DisConnectedNode rNeighbour;
	public int layer = 0;
	double supp = 0;
	public int flag; // for prune
	public int lastPId;
	public int pNodeNum = 0;
	public LiterTree ltree;
	public List<IntSet> pivotMatches;
	
	public HashMap<String, HashMap<Integer,IntSet>> stringCands; //just for root children
	public HashMap<Integer,HashMap<Integer,List<IntSet>>> allVarCands;
	public HashMap<Integer,HashMap<String,IntSet>> allLiterCands;

	public DisConnectedNode() {
		// TODO Auto-generated constructor stub
		this.children = new ArrayList<DisConnectedNode>();
		this.patterns = new ArrayList<Integer>();	
		this.flag = Integer.MAX_VALUE;
		//this.stringCands = new HashMap<Integer, HashMap<String, HashMap<Integer,IntSet>>>();

	}

	
	public String getpId(){
		String s = "";
		Collections.sort(patterns);
		for(int i: patterns){
			s = s +i +",";
		}
		return s;
	}
	
	public int getCompontNum(){
		return this.patterns.size();
	}

}
