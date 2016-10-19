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
import inf.ed.graph.structure.adaptor.Pair;
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
	public HashMap<String, LiterNode> condition_Map;
	public int layer = 0;
	

	public LiterTree() {
		// TODO Auto-generated constructor stub
		this.root = new LiterNode();
		//this.gNode = new GfdNode();
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
	
	/*
	public void initialExtend(Set<String> dom){
		 
		 for(int nodeId = 1; nodeId< this.gNode.nodeNum; nodeId++){
	        	
	        	// add Literal: for all possible equal constant
	        	for(String s : dom){
        		    addNode(this.root,1,nodeId,s);
	        	}
	        	for(int nodeId2 = nodeId+1; nodeId2<= this.gNode.nodeNum; nodeId2++){
	        		addNode(this.root,1,nodeId,nodeId2);
	        	}
		 }

	}
	*/
	
	
	
	private boolean addLiteral(LiterNode t, String s, int nodeId){
			//HashMap<Integer, String> l, HashMap<Integer, IntSet> v, 
			//String s, int nodeId){
		// if there is no nodeId.A = s; 

		HashMap<Integer, String> l = t.dependency.XEqualsLiteral;
	    HashMap<Integer, IntSet> v = t.dependency.XEqualsVariable;
	  
	   
	    
		if(l.containsKey(nodeId)){
			return false;
		}
	
		// and no nodeId.A = s && nodeId2.A = s -> nodeId.A = nodeId2.A
		if(!t.getDependency().isLiteral){
			 Pair<Integer, Integer> yv = t.dependency.YEqualsVariable;
			  
			if(l.containsKey(yv.x)){
				if(l.get(yv.x).equals(s)){
					return false;
				}
			}
			if(l.containsKey(yv.y)){
				if(l.get(yv.y).equals(s)){
					return false;
				}
			}
		}else{
			Pair<Integer, String> yl = t.dependency.YEqualsLiteral;
		
		// and no nodeId.A = nodeId2.A && nodeId2.A = s -> nodeId.A =s
			if(v.containsKey(nodeId)){
				IntSet a = v.get(nodeId);
				for(int i: a){
					if(l.containsKey(i)){
						if(l.get(i).equals(s)){
							if(yl.y.equals(s)){
								return false;
							}
						}
					}
				}
			}
		}
		return true;	
	}
	
	private boolean addVar(LiterNode t, int nodeId, int nodeId2){

		HashMap<Integer, String> l = t.dependency.XEqualsLiteral;
	    HashMap<Integer, IntSet> v = t.dependency.XEqualsVariable;
	  
	  
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
		if(t.getDependency().isLiteral){
			 Pair<Integer, String> yl = t.dependency.YEqualsLiteral;
			if(l.containsKey(nodeId)){
				//there is no nodeId.A = nodeId2.A, nodeId.A =c -> nodeId2.A =c
				if(l.get(nodeId) == yl.y && nodeId2 == yl.x){
							return false;
				}
			}
			if(l.containsKey(nodeId2)){
				//there is no nodeId.A = nodeId2.A, nodeId2.A =c -> nodeId.A =c
				if(l.get(nodeId2) == yl.y && nodeId == yl.x){
							return false;
				}
			}
		}else{
			 Pair<Integer, Integer> yv = t.dependency.YEqualsVariable;
		//there is no nodeId.A = nodeId2.A -> nodeId.A = nodeId2.A
			if(nodeId == yv.x && nodeId2 == yv.y){
				return false;
			}
		}
		
		
		return true;
	}
	// y is literal condition empty->y
	 public void addNode(LiterNode g, Condition y){
		 if(y.isLiteral){
			 int nodeId = y.YEqualsLiteral.x;
			 String s =  y.YEqualsLiteral.y;
			 addNode(g,1,nodeId, s);
		 }else{
			 int nodeId = y.YEqualsVariable.x;
			 int nodeId2 = y.YEqualsVariable.y;
			 addNode(g,1,nodeId, nodeId2);
		 }
	 }
			
	public LiterNode addNode(LiterNode g, int X, int nodeId, String s){
		LiterNode t = new LiterNode();
		t.setParent(g);
		g.children.add(t);
		if(X == 0){//X
			t.setDependency((Condition) g.getDependency().clone());
			t.addXLiteral = true;
			t.addxl = new Pair<Integer,String>(nodeId,s);
			t.getDependency().XEqualsLiteral.put(nodeId, s);
		}else{
			t.dependency = new Condition();
			t.getDependency().setYEqualsLiteral(nodeId, s);
			t.getDependency().isLiteral = true;
			log.debug(this.root.children.size());
		}
		t.key = t.getDependency().toString();
		condition_Map.put(t.key, t);
		return t;
	 }
		
	 public LiterNode addNode(LiterNode g, int X,  int nodeId, int nodeId2){
		 
		HashMap<Integer,IntSet> v = new HashMap<Integer,IntSet>();
		LiterNode t = new LiterNode();
		t.setDependency((Condition) g.getDependency().clone());
		t.setParent(g);
		g.children.add(t);
		if(X == 0){
			t.addXLiteral = false;
			t.addxv = new Pair<Integer,Integer>(nodeId,nodeId2);
			v = t.getDependency().XEqualsVariable;
			if(!v.containsKey(nodeId)){
				v.put(nodeId, new IntOpenHashSet());
			}
			v.get(nodeId).add(nodeId2);
			if(!v.containsKey(nodeId2)){
				v.put(nodeId2, new IntOpenHashSet());
			}
			v.get(nodeId2).add(nodeId);
			
		}else{
			t.dependency = new Condition();
			t.getDependency().setYEqualsVariable(nodeId, nodeId2);
			t.getDependency().isLiteral = false;
		}
		
		t.key = t.getDependency().toString();
		log.debug(nodeId+ "+"+ nodeId2);
		log.debug(t.key);
		condition_Map.put(t.key, t);
		return t;
	 }
	 
	 
	 
	 
		
	 /***************************************************************************
	  * ****************************************************************************
	 * *
	 *  Interface;
	 * 
	 ***************************************************************************
	  * *****************************************************************************/
	
	 /**
	  * after Sc assembel the dependencies, update the next level's literal tree
	 * @param t current pattern node, need update next level
	 * @param s current literal node (verified), need add the corrsponding literal for next level pattern. 
	 */
	
	private void newNode(Set<String> dom, LiterNode t,int flagR, int nodeId, boolean flagE){//control the loop
		for(String s : dom){
			if(flagR ==0){
				boolean flag = addLiteral(t, s, nodeId);
        		if(flag == true){
        		   addNode(t,0,nodeId,s);	
        		}
			}
			else{
			       addNode(t,1,nodeId,s);
			}
		}
		int nodeId2 = 1;
		if(flagE == true){
			nodeId2 = nodeId+1;
		}
		 for(; nodeId2<= this.gNode.nodeNum; nodeId2++){
			 if(flagR ==0){
					boolean flag = addVar(t, nodeId2, nodeId);
	        		if(flag == true && !flagE){
	        		   addNode(t,0,nodeId2,nodeId);	
	        		}
	        		if(flag == true && flagE){
		        		   addNode(t,0,nodeId,nodeId2);	
		        		}
				}
				else{
					if(nodeId2 <this.gNode.nodeNum){
						if(!flagE){
			        		   addNode(t,1,nodeId2,nodeId);	
			        	}
						else{
				        		addNode(t,1,nodeId,nodeId2);	
				        }
					}
				      
				}
		}
	}
		
		
		
    /**
     * when begin to verify a new gfdNode
     * @param dom
     * @param t// extend this node
     * @param nodeId // the new added node
     */
		
	public void updateNode(Set<String> dom, LiterNode t, int nodeId){
		
		if(t == this.root){
			for(String s : dom){
				 addNode(t,1,nodeId,s);
			}
			for(int nodeId2 = 1; nodeId2< this.gNode.nodeNum; nodeId2++){
				addNode(t,1,nodeId2,nodeId);	
			}
		}
		else{
			for(String s : dom){
				boolean flag = addLiteral(t, s, nodeId);
        		if(flag == true){
        		   addNode(t,0,nodeId,s);	
        		}
			}
    		for(int nodeId2 = 1; nodeId2< this.gNode.nodeNum; nodeId2++){
    			boolean flag = addVar(t, nodeId2, nodeId);
        		if(flag == true ){
        		   addNode(t,0,nodeId2,nodeId);	
        		}	
			}
		}
		
			
			
			
			
		
        	   
        		
		}
	}
	public void extendNode(Set<String> dom, LiterNode t){
		
		//Int2ObjectMap<VertexString> vertexMap = this.gNode.getPattern().allVertices(); 
		
	    /*
	    boolean flag;
		
		//for all nodes in pattern Q
	    for(int nodeId = 1; nodeId<= this.gNode.nodeNum; nodeId++){
		    if(t == this.root){
				newNode(dom, this.root, 1, nodeId,true);
			}
		    else{
		    	newNode(dom, t, 0, nodeId,true);
		    }
	    }
	}
	*/
		boolean flag;
        for(int nodeId = 1; nodeId<= this.gNode.nodeNum; nodeId++){
        	
        	// add Literal: for all possible equal constant
        	for(String s : dom){
        	    if(t == this.root){
        	    	addNode(this.root,1,nodeId,s);
        	    }
        	    else{
	        		flag = addLiteral(t, s, nodeId);
	        		if(flag == true){
	        			addNode(t,0,nodeId,s);	
	        		}
        	    }
        		
        	}
        }
        for(int nodeId = 1; nodeId< this.gNode.nodeNum; nodeId++){
        	//add variable
            for(int nodeId2 = nodeId+1; nodeId2<= this.gNode.nodeNum; nodeId2++){
            	//for X
            	if(t == this.root){
            		addNode(this.root,1,nodeId,nodeId2);
            	}else{
	            	flag = addVar(t, nodeId, nodeId2);
	            	if(flag == true){
	            		addNode(t,0,nodeId,nodeId2);
	            	} 
            	}
            }
        }
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
		 //Graph<VertexString, TypedEdge> pattern = new SimpleGraph<VertexString, TypedEdge>(VertexString.class,
					//TypedEdge.class);
		// pattern.loadGraphFromVEFile("data/query/1-2.ptn",true);
		 //log.debug(pattern.toString());
		// g.setPattern(pattern);
		 
		 
		 
		LiterNode t = new LiterNode();
		 t.setDependency(cond2);
		 Set<String> s = new HashSet<String>();
		 s.add("a");
		 s.add("b");
		 tree.extendNode(s, tree.getRoot());
		 log.debug("t has children"+ t.getChildren().size());	 
		
	}


}
