package inf.ed.gfd.structure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import inf.ed.graph.structure.Graph;
import inf.ed.graph.structure.SimpleGraph;
import inf.ed.graph.structure.adaptor.TypedEdge;
import inf.ed.graph.structure.adaptor.VertexString;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

public class LiterNode implements Serializable {
	
	private static final long serialVersionUID = 1L;

	static Logger log = LogManager.getLogger(LiterNode.class);
	private Condition dependency;
	private LiterNode parent; 
	private List<LiterNode>  children;
	private GfdNode gnode;
	
	public LiterNode() {
		this.children = new ArrayList<LiterNode>();
		// TODO Auto-generated constructor stub
	}

	public Condition getDependency() {
		return dependency;
	}

	public void setDependency(Condition dependency) {
		this.dependency = dependency;
	}

	public LiterNode getParent() {
		return parent;
	}

	public void setParent(LiterNode parent) {
		this.parent = parent;
	}

	public List<LiterNode> getChildren() {
		return children;
	}

	public void setChildren(List<LiterNode> children) {
		this.children = children;
	}
	/*
	 * here just consider one attribute for all node : val 
	 */
	public void extendNode(Set<String> dom){
		Int2ObjectMap<VertexString> vertexMap = new Int2ObjectOpenHashMap<VertexString>();
		vertexMap = this.gnode.getPattern().allVertices(); 
		HashMap<Integer, String> xl = this.dependency.XEqualsLiteral;
		HashMap<Integer, String> yl = this.dependency.YEqualsLiteral;
	    HashMap<Integer, IntSet> xv = this.dependency.XEqualsVariable;
	    HashMap<Integer, IntSet> yv = this.dependency.YEqualsVariable;
	    
	    boolean flag1,flag2, flag3, flag4;
		
		//for all nodes in pattern Q
        for(int nodeId: vertexMap.keySet()){
        	
        	// add Literal: for all possible equal constant
        	for(String s : dom){
        	
        		flag1 = addLiteral(xl, xv, s, nodeId);
        		flag2 = addLiteral(xl, yv, s, nodeId);
        		flag3 = addLiteral(yl, xv, s, nodeId);
        		flag4 = addLiteral(yl, yv, s, nodeId);
        		if(flag1 == true && flag2 == true && flag3 == true && flag4 == true){
        			LiterNode t1 = this.addNode();
        			t1.getDependency().XEqualsLiteral.put(nodeId, s);
        			LiterNode t2 = this.addNode();
        			t2.getDependency().YEqualsLiteral.put(nodeId, s);
        		}
        		
        	}
        	
        	//add variable
            for(int nodeId2: vertexMap.keySet()){
            	//for X
            	flag1 = addVar(xl, xv, nodeId, nodeId2);
            	flag2 = addVar(xl, yv, nodeId, nodeId2);
            	flag3 = addVar(yl, xv, nodeId, nodeId2);
            	flag4 = addVar(yl, yv, nodeId, nodeId2);
            	
            	if(flag1 == true && flag2 == true && flag3 == true && flag4 == true){
            		LiterNode t1 = this.addNode();
            		updateNodeV(t1.getDependency().XEqualsVariable, nodeId, nodeId2);
            		LiterNode t2 = this.addNode();
            		updateNodeV(t2.getDependency().YEqualsVariable, nodeId, nodeId2);		
            	}  	
            }
        }
       }
	
	public boolean addLiteral(HashMap<Integer, String> l, HashMap<Integer, IntSet> v, String s, int nodeId){
		// if there is no nodeId.A = s; 
		if(l.containsKey(nodeId)){
			return false;
		}
	
		// and no nodeId.A = nodeId2.A && nodeId2.A = s
		if(v.containsKey(nodeId)){
			IntSet a = v.get(nodeId);
			for(int i: a){
				if(l.containsKey(i)){
					if(l.get(i).equals(s)){
						return false;
					}
				}
			}
		}
		return true;	
	}
	
	public boolean addVar(HashMap<Integer, String> l, HashMap<Integer, IntSet> v, int nodeId, int nodeId2){
	    if(nodeId == nodeId2){
	    	return false;
	    }
		//if there is no nodeId.A = c, nodeId2.A =c
		if(l.containsKey(nodeId) && l.containsKey(nodeId2)){
			if(l.get(nodeId).equals(l.get(nodeId2))){
				return false;
			}
		}
		// if there is no nodeId.A = nodeId2.A
		if(v.containsKey(nodeId)){
			if(v.get(nodeId).contains(nodeId2)){
				return false;
			}
		}
		return true;
	}
	
			
	 public LiterNode addNode(){
		LiterNode t = new LiterNode();
		t.setDependency((Condition) this.getDependency().clone());
		t.setParent(this);
		this.children.add(t);
		return t;
	 }
	 
	 public void updateNodeV(HashMap<Integer,IntSet> v, int nodeId, int nodeId2){
		if(!v.containsKey(nodeId)){
			v.put(nodeId, new IntOpenHashSet());
		}
		v.get(nodeId).add(nodeId2);
		if(!v.containsKey(nodeId2)){
			v.put(nodeId2, new IntOpenHashSet());
		}
		v.get(nodeId2).add(nodeId);
	 }
	 
	 public static void main(String args[]) {  
		 Condition cond = new Condition();
		 cond.XEqualsLiteral.put(1,"asd");
		 cond.XEqualsVariable.put(1, new IntOpenHashSet());
		 cond.XEqualsVariable.get(1).add(2);
		 cond.XEqualsVariable.get(1).add(3);
		 String x = cond.toString();
		 log.debug(x);
		 
		 Condition cond2 = (Condition) cond.clone();
		 
		 cond2.XEqualsLiteral.put(2, "234"); 
		 cond2.XEqualsVariable.get(1).add(4);
		 String conds1 = cond.toString();
		 String conds2 = cond2.toString();
		 log.debug(conds1 + "\t"+conds2);
		 
		 GfdNode g = new GfdNode();
		 Graph<VertexString, TypedEdge> pattern = new SimpleGraph<VertexString, TypedEdge>(VertexString.class,
					TypedEdge.class);
		 pattern.loadGraphFromVEFile("data/query/1-2.ptn",true);
		 log.debug(pattern.toString());
		 g.setPattern(pattern);
		 
		 LiterNode t = new LiterNode();
		 t.gnode = g; 
		 t.setDependency(cond2);
		 Set<String> s = new HashSet<String>();
		 s.add("a");
		 s.add("b");
		 t.extendNode(s);
		 log.debug("t has children"+ t.getChildren());	 
		
	}
}
