/**
 * 
 */
package inf.ed.gfd.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
	
	
	//for disconnected
	List<Set<Pair<Integer,String>>> spacexl;
	List<Set<Pair<Integer,Integer>>> spacexv;
	Set<String> dom;
	
	

	public LiterTree() {
		// TODO Auto-generated constructor stub
		this.root = new LiterNode();
		//this.gNode = new GfdNode();
		this.condition_Map = new HashMap<String, LiterNode>();
		List<Set<Pair<Integer,String>>> spacexl = new ArrayList<Set<Pair<Integer,String>>>();
		List<Set<Pair<Integer,Integer>>> spacexv = new ArrayList<Set<Pair<Integer,Integer>>>();
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
		t.pos = g.childPos;//
		g.childPos++;
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
		t.pos = g.childPos;//
		g.childPos++;
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
	

		
    /**
     * when begin to verify a new gfdNode g, we should first complete its literal extention. 
     * @param dom
     * @param t// extend this node
     * @param nodeId // the new added node
     */
    ///revise  do not update the literal but update the literaldom and vardom
	/*	
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
    		if(t.dependency.isLiteral && t.dependency.YEqualsLiteral.x == nodeId){
    			for(int nodeId2 = 1; nodeId2< this.gNode.nodeNum; nodeId2++){
    				for(String s : dom){
    					boolean flag = addLiteral(t, s, nodeId);
    	        		if(flag == true){
    	        		   addNode(t,0,nodeId2,s);	
    	        		}
    				}
    				
    			}
    			 for(int nodeId2 = 1; nodeId< this.gNode.nodeNum; nodeId2++){
    		        	//add variable
    		            for(int nodeId3 = nodeId+1; nodeId3<= this.gNode.nodeNum; nodeId3++){
    		            	//for 
    			            	boolean flag = addVar(t, nodeId2, nodeId3);
    			            	if(flag == true){
    			            		addNode(t,0,nodeId2,nodeId3);
    			            	} 
    		            	}
    		           }
    		    }
    		if(!t.dependency.isLiteral && t.dependency.YEqualsVariable.y == nodeId){
    			for(int nodeId2 = 1; nodeId2< this.gNode.nodeNum; nodeId2++){
    				for(String s : dom){
    					boolean flag = addLiteral(t, s, nodeId);
    	        		if(flag == true){
    	        		   addNode(t,0,nodeId2,s);	
    	        		}
    				}
    				
    			}
    			 for(int nodeId2 = 1; nodeId< this.gNode.nodeNum; nodeId2++){
    		        	//add variable
    		            for(int nodeId3 = nodeId+1; nodeId3<= this.gNode.nodeNum; nodeId3++){
    		            	//for 
    			            	boolean flag = addVar(t, nodeId2, nodeId3);
    			            	if(flag == true){
    			            		addNode(t,0,nodeId2,nodeId3);
    			            	} 
    		            	}
    		           }
    		    }
		}
		
	}*/
	
public void extendNode(LiterNode t){
		if(t == this.root){
			for(int nodeId = 1; nodeId<= this.gNode.nodeNum; nodeId++){
				for(String s : this.gNode.literDom.get(nodeId)){
	        	    	addNode(this.root,1,nodeId,s);
	        	    }
			}
			for(int nodeId = 1; nodeId< this.gNode.nodeNum; nodeId++){
		        	//add variable
		           for(int nodeId2 : this.gNode.varDom.get(nodeId)){
		            		addNode(this.root,1,nodeId,nodeId2);
		           }
			}
		}
		
		if(t.parent == this.root){
			  for(int nodeId = 1; nodeId<= this.gNode.nodeNum; nodeId++){
		        	
		        	// add Literal: for all possible equal constant
		        	for(String s : this.gNode.literDom.get(nodeId)){
		        		boolean flag = addLiteral(t, s, nodeId);
		        		if(flag == true){
		        			addNode(t,0,nodeId,s);	
		        		}
		        	}
			  }
			  for(int nodeId = 1; nodeId< this.gNode.nodeNum; nodeId++){
		        	//add variable
				  for(int nodeId2 : this.gNode.varDom.get(nodeId)){
		        	   boolean flag = addVar(t, nodeId, nodeId2);
		            	if(flag == true){
		            		addNode(t,0,nodeId,nodeId2);
		            	} 
		           }
			 }
			
		}
		else{
			LiterNode parent = t.parent;
			for(int i = t.pos; i<parent.children.size();i++){
				if(parent.children.get(i).extend){
					LiterNode tmp = parent.children.get(i);
					if(tmp.addXLiteral){
						boolean flag = addLiteral(t, tmp.addxl.y, tmp.addxl.x);
		        		if(flag == true){
		        			addNode(t,0,tmp.addxl.x,tmp.addxl.y);	
		        		}
					}else{
						boolean flag = addVar(t, tmp.addxv.x, tmp.addxv.y);
		        		if(flag == true){
		        			addNode(t,0, tmp.addxv.x, tmp.addxv.y);	
		        		}
					}
					
				}
			}
			
			
			
		}
}
		
	 

	 	
			
			
			
			
		
   /**     	   
    * Just for the fgdtree root->children; 
    * @param dom
    * @param t
    */
        		
	/*
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
	/*
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
   
	 */

	 
	
	 
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
		// tree.extendNode(s, tree.getRoot());
		 log.debug("t has children"+ t.getChildren().size());	 
		
	}

	 //////////////////////////////////////
	 /**
	  * add node for negative checking 
	  * 
	  */
	 public void addNegCheck(LiterNode t){
		 if(t.dependency.isLiteral){
			 Pair<Integer,String> p = t.dependency.YEqualsLiteral;
			 LiterNode x = addNode(t,0, p.x,p.y);
			 x.negCheck = true;
		 }
		 else{
			 Pair<Integer,Integer> p = t.dependency.YEqualsVariable;
			 LiterNode x = addNode(t,0, p.x,p.y);
			 x.negCheck = true;
		 }
	 }
	 
	 ///////////////////////////////////////////////////////////////////////////////
	 /*
	  * fir disconnected pattern literal extention;// condition xy x : pattern num;
	  * 
	  */////////////////////////////////////////////////////////////////////////////
	 
	 
	
	 public void disExtendRoot(){
		 //initialize; component size =2;
		 for(Pair<Integer,String> p1 : spacexl.get(0)){
			 for(Pair<Integer,String> p2 : spacexl.get(1)){
				 List<Pair<Integer,String>> xl = new ArrayList<Pair<Integer,String>>();
				 xl.add(p1);
				 xl.add(p2);
				 for(Pair<Integer,Integer> p3 : spacexv.get(0)){
					 extendRootNodeXl(xl,p3);
				 }
			 }
		}
		 for(Pair<Integer,Integer> p3 : spacexv.get(0)){
			 for(Pair<Integer,Integer> p4 : spacexv.get(0)){
				 if(!p3.equals(p4)){
					 extendRootNodeXv(p3, p4);
				 }
			 }
		 }
		 
	 }
	 
	 public void disExtendGeneral(LiterNode t){
		 for(Pair<Integer,String> p1 : spacexl.get(0)){
			boolean flag = addLiteral(t, p1.y, p1.x);
     		if(flag == true){
     			addNode(t,0,p1.x,p1.y);	
     		}
		 }
		 for(Pair<Integer,Integer> p3 : spacexv.get(0)){
			 boolean flag = addVar(t, p3.x, p3.y);
	     		if(flag == true){
	     			addNode(t,0,p3.x,p3.y);	
	     		} 
		 }
	 }
		 
	 		 
	
	 private LiterNode extendRootNodeXl(List<Pair<Integer,String>> xl, Pair<Integer, Integer> vy){
			LiterNode t = new LiterNode();
			t.setParent(this.root);
			this.root.children.add(t);
			
				t.setDependency((Condition) this.root.getDependency().clone());
				t.addXLiteral = true;
				for(Pair<Integer,String> p : xl){
				   t.getDependency().XEqualsLiteral.put(p.x,p.y);
				}
				t.getDependency().setYEqualsVariable(vy.x, vy.y);
			
			t.key = t.getDependency().toString();
			condition_Map.put(t.key, t);
			return t;
		 }
	 private LiterNode extendRootNodeXv(Pair<Integer,Integer> xv, Pair<Integer, Integer> vy){
			LiterNode t = new LiterNode();
			t.setParent(this.root);
			this.root.children.add(t);
			
				t.setDependency((Condition) this.root.getDependency().clone());
				t.addXLiteral = false;
				t.getDependency().XEqualsVariable.put(xv.x,new IntOpenHashSet());
				t.getDependency().XEqualsVariable.get(xv.x).add(xv.y);
				
				t.getDependency().setYEqualsVariable(vy.x, vy.y);
			
			t.key = t.getDependency().toString();
			condition_Map.put(t.key, t);
			return t;
		 }
		
	 public void extendSpace(DisConnectedNode n, DisconnectedTree t){
		 int pIdenttity = 1;
		 int size = n.patterns.size();
		 int num[] = new int[size];
		 for(int p :n.patterns){
			 String pId = t.connectdPatternIndex.get(p);
			 int nodeNum = t.disConnectedPatternIndex.get(pId).pNodeNum;
			 num[pIdenttity-1] = nodeNum;
		 }
		 //suppose only two components
		// List<Set<Pair<Integer,String>>> spacexl = new  ArrayList<Set<Pair<Integer,String>>>();
		// List<Set<Pair<Integer,Integer>>> spacexv = new  ArrayList<Set<Pair<Integer,Integer>>>();
		 //test
		 for(int i = 0; i< size; i++){
			 Set<Pair<Integer,String>> xls =  extendLSpace(dom, num[i],i+1);
			 spacexl.add(xls);
			 for(int j=i+1;j<size;j++){
				 Set<Pair<Integer,Integer>> xvs = extendVSpace(i,j,num[i],num[j]);
				 spacexv.add(xvs);
		     }
		 }
	 }
		
	 private Set<Pair<Integer,String>> extendLSpace(Set<String> dom, int nodenum, int pIdentity){
		 Set<Pair<Integer,String>> space = new HashSet<Pair<Integer,String>>();
		 for(int i = 0; i< nodenum ; i++){
			 String s = ""+pIdentity+i;
			 int nodeId = Integer.parseInt(s);
			 for(String s2 : dom){
				Pair<Integer,String> p =new Pair<Integer,String>(nodeId,s2);
				space.add(p);
			 }
		 }
		 return space;
		 
	 }
	 private Set<Pair<Integer,Integer>> extendVSpace(int m,int n, int num1,int num2){
		 Set<Pair<Integer,Integer>> space = new HashSet<Pair<Integer,Integer>>();
			 if(num1>num2){
				 int tmpt1 = num2;
				 num2 = num1;
				 num1 = tmpt1;
				 int tmpt = n;
				 n = m;
				 m= tmpt;
				 
			 }
			 else{
			 for(int i=1 ;i<= num1;i++){
				 for(int j=i;i<num2;j++){
					 String s1 = ""+m+i;
					 String s2 = ""+n+i;
					 int nodeId1 = Integer.parseInt(s1);
					 int nodeId2 = Integer.parseInt(s2);
					 Pair<Integer,Integer> p= new Pair<Integer,Integer>(nodeId1,nodeId2);
					 space.add(p);
					 
				 }
			 }
		 }
	
		 return space;
		 
	 }
	 
	 /*
	 public void disExtendY(DisConnectedNode n, DisconnectedTree t, Set<String> dom, LiterNode g){
		 int pIdenttity = 1;
		 int num[] = new int[n.patterns.size()];
		 for(int p :n.patterns){
			 String pId = t.connectdPatternIndex.get(p);
			 int nodeNum = t.disConnectedPatternIndex.get(pId).pNodeNum;
			 num[pIdenttity-1] = nodeNum;
			 for(int i = 0; i< nodeNum ; i++){
				 String s = ""+pIdenttity+i;
				 int nodeId = Integer.parseInt(s);
				 for(String s2 : dom){
					 addNode(g, 1, nodeId, s2);
				 }
			 }
			 pIdenttity++; 
		 }
		 for(int i = 1;i<=n.patterns.size();i++){
			 for(int j= i+1;j<=n.patterns.size();j++){
				 yVarTwoPattern(i,j,num[i-1],num[j-1],g);
			 }
		 }
		 
		 
	 }
	 

	 public void disExtendY(DisConnectedNode n, DisconnectedTree t, Set<String> dom, LiterNode g){
		 int pIdenttity = 1;
		 int num[] = new int[n.patterns.size()];
		 for(int p :n.patterns){
			 String pId = t.connectdPatternIndex.get(p);
			 int nodeNum = t.disConnectedPatternIndex.get(pId).pNodeNum;
			 num[pIdenttity-1] = nodeNum;
		 }
		 for(int i = 1;i<=n.patterns.size();i++){
			 for(int j= i+1;j<=n.patterns.size();j++){
				 yVarTwoPattern(i,j,num[i-1],num[j-1],g);
			 }
		 }
		 
		 
	 }
	 public void yVarTwoPattern(int m,int n, int num1,int num2, LiterNode g){
		 if(num1>num2){
			 int tmpt1 = num2;
			 num2 = num1;
			 num1 = tmpt1;
			 int tmpt = n;
			 n = m;
			 m= tmpt;
			 
		 }
		 else{
		 for(int i=1 ;i<= num1;i++){
			 for(int j=i;i<num2;j++){
				 String s1 = ""+m+i;
				 String s2 = ""+n+i;
				 int nodeId1 = Integer.parseInt(s1);
				 int nodeId2 = Integer.parseInt(s2);
				 addNode(g, 1, nodeId1, nodeId2);
				 
			 }
		 }
	 }
	 }
	 */
}
