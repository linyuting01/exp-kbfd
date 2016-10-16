/**
 * 
 */
package inf.ed.gfd.structure;

import java.util.HashMap;
import java.util.HashSet;
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

/**
 * @author v1xliu33
 *
 */
public class LiterTree {

	/**
	 * 
	 */
	
	static Logger log = LogManager.getLogger(LiterTree.class);
	
	private LiterNode root;
	public  GfdNode gNode;
	HashMap<String, LiterNode> condition_Map;
	

	public LiterTree() {
		// TODO Auto-generated constructor stub
		this.root = new LiterNode();
		this.gNode = new GfdNode();
		this.condition_Map = new HashMap<String, LiterNode>();
	}
	public LiterTree(GfdNode gNode) {
		// TODO Auto-generated constructor stub
		this.root = new LiterNode();
	    this.gNode = gNode;
		this.condition_Map = new HashMap<String, LiterNode>();
	}
	

	public LiterNode getRoot() {
		return root;
	}

	public void setRoot(LiterNode root) {
		this.root = root;
	} 
	
	/*
	public Condition getCondition(LiterNode t){
	 LiterNode node = t.getParent();
	 Condition cond = new Condition();
	 while(node!= root){
		 cond.combineCondition(t.getDependecy());	 
		 node = node.getParent();
	 }
	 return cond;	 
	}
	*/
	
	
	
	public void extendNode(Set<String> dom, LiterNode t){
		Int2ObjectMap<VertexString> vertexMap = this.gNode.getPattern().allVertices(); 
		HashMap<Integer, String> xl = t.dependency.XEqualsLiteral;
		HashMap<Integer, String> yl = t.dependency.YEqualsLiteral;
	    HashMap<Integer, IntSet> xv = t.dependency.XEqualsVariable;
	    HashMap<Integer, IntSet> yv = t.dependency.YEqualsVariable;
	    
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
        			addNode(t,0,nodeId,s);	
        		    addNode(t,1,nodeId,s);
        			
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
            		addNode(t,0,nodeId,nodeId2);
            		addNode(t,1,nodeId,nodeId2);
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
	
			
	 public LiterNode addNode(LiterNode g, int X, int nodeId, String s){
		LiterNode t = new LiterNode();
		t.setDependency((Condition) g.getDependency().clone());
		t.setParent(g);
		g.children.add(t);
		if(X == 0){//X
			t.getDependency().XEqualsLiteral.put(nodeId, s);
		}else{
			t.getDependency().YEqualsLiteral.put(nodeId, s);
		}
		String key = t.getDependency().toString();
		condition_Map.put(key, t);
		return t;
	 }
		
	 public LiterNode addNode(LiterNode g, int X,  int nodeId, int nodeId2){
		 
		 HashMap<Integer,IntSet> v = new HashMap<Integer,IntSet>();
		LiterNode t = new LiterNode();
		t.setDependency((Condition) g.getDependency().clone());
		t.setParent(g);
		g.children.add(t);
		if(X == 0){
			v = t.getDependency().XEqualsVariable;
		}else{
			v = t.getDependency().YEqualsVariable;
		}
		if(!v.containsKey(nodeId)){
			v.put(nodeId, new IntOpenHashSet());
		}
		v.get(nodeId).add(nodeId2);
		if(!v.containsKey(nodeId2)){
			v.put(nodeId2, new IntOpenHashSet());
		}
		v.get(nodeId2).add(nodeId);
		t.key = t.getDependency().toString();
		condition_Map.put(t.key, t);
		return t;
	 }
	 
	
	 
	 public static void main(String args[]) {  
		 LiterTree tree  = new LiterTree();
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
		 t.setDependency(cond2);
		 Set<String> s = new HashSet<String>();
		 s.add("a");
		 s.add("b");
		 tree.extendNode(s, t);
		 log.debug("t has children"+ t.getChildren().size());	 
		
	}


}
