package inf.ed.gfd.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import inf.ed.gfd.util.Params;

public class DisConnectedNode {
	static Logger log = LogManager.getLogger(DisConnectedNode.class);
	
	public DisConnectedNode parent;
	public List<DisConnectedNode> children;
	//public String pId;
	public List<Integer> patterns;
	public DisConnectedNode rNeighbour;
	public int layer;
	double supp;
	public int flag; // for prune
	public int lastPId;
	public int pNodeNum = 0;
	public LiterTree ltree;

	public DisConnectedNode() {
		// TODO Auto-generated constructor stub
		this.parent = new DisConnectedNode();
		this.children = new ArrayList<DisConnectedNode>();
		this.patterns = new ArrayList<Integer>();	
		this.flag = Integer.MAX_VALUE;
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
