/**
 * 
 */
package inf.ed.gfd.structure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import inf.ed.gfd.util.ColoneUtils;
import inf.ed.gfd.util.Fuc;
import inf.ed.gfd.util.Params;
import inf.ed.graph.structure.Graph;
import inf.ed.graph.structure.adaptor.Pair;
import inf.ed.graph.structure.adaptor.TypedEdge;
import inf.ed.graph.structure.adaptor.VertexString;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

/**
 * @author v1xliu33
 *
 */
public class LiterTree implements Serializable {

	/**
	 * 
	 */
	
	static Logger log = LogManager.getLogger(LiterTree.class);
	
	private LiterNode root;
	public  GfdNode gNode;
	public HashMap<Integer, LiterNode> condition_Map;
	public int layer = 0;
	
    public int addNew = 1;
	public DisConnectedNode dNode;
	List<EqLiteral> literArray = new LinkedList<EqLiteral>();
	List<EqVarLiter> varArray = new LinkedList<EqVarLiter>();
	
	

	public LiterTree() {
		// TODO Auto-generated constructor stub
		this.root = new LiterNode();
		//this.gNode = new GfdNode();
		this.condition_Map = new HashMap<Integer, LiterNode>();
		this.condition_Map.put(0, root);
		
	}
	public LiterTree(GfdNode gNode) {
		// TODO Auto-generated constructor stub
		this.root = new LiterNode();
	    this.gNode = gNode;
		this.condition_Map = new HashMap<Integer, LiterNode>();
	}
	

	public LiterNode getRoot() {
		return root;
	}

	public void setRoot(LiterNode root) {
		this.root = root;
	} 
	
	
	
	// no prun;
	public void extendNode(LiterNode t, Int2ObjectMap<Int2ObjectMap<String>> literCands ){
		Graph<VertexString, TypedEdge> pattern = this.gNode.pattern;
		
		if(t == this.root){
			for(int nodeId = 1; nodeId<= this.gNode.nodeNum; nodeId++){
				addPatternNodeLiter(nodeId,literCands,-1);
			}
			//x1.a = x1.a //add need to revise
			
			for(Entry<Integer,Integer> entry :this.gNode.attrs.entrySet()){
				int attr = entry.getKey();
				if(literCands.containsKey(attr)){
					if(entry.getValue() >1){
						for(int i= 1; i< entry.getValue();i++){
							for(int j = i+1;j< entry.getValue();j++ ){
								Pair<Integer,Integer> p1 = new Pair<Integer,Integer>(entry.getKey(),i-1);
								Pair<Integer,Integer> p2 = new Pair<Integer,Integer>(entry.getKey(),j-1);
								int nodeId1 = this.gNode.nodeSet.get(p1);
								int nodeId2 = this.gNode.nodeSet.get(p2);
								for(int k: literCands.get(attr).keySet()){
									EqVarLiter xv = new EqVarLiter(nodeId1,nodeId2,k,k);
									varArray.add(xv);
									LiterNode g = addNodeYVar(xv,-1);
									g.yVar = varArray.size()-1;
								}
							}
						}
					}
				}
			}
		}
							
			if(t != this.root){
				addNodeNotRoot(t,-1);
			}	    
		}
	
	
	
	
	
	public void extendNodeForPrune(LiterNode t, Int2ObjectMap<Int2ObjectMap<String>> literCands){
		LiterNode pt = new LiterNode();
		if(t == this.root){
		     pt = this.gNode.parent.ltree.getRoot();
		}else{
			 pt = this.gNode.parent.ltree.condition_Map.get(t.cId);
		}
			int nodeId = -1;
		    int num1 = this.gNode.parent.nodeNum;
			int num = this.gNode.nodeNum;
			if(pt.extend && !pt.negCheck){
				for(LiterNode ptc : pt.children){
					addNodeforPrune(ptc);
					if(num1 < num ){
						nodeId = num;
						updateNodeforPrune(nodeId, t, literCands);		
					}
				}
			}
		}
		
		
			
		
		
		///for prune;
		private void addPatternNodeLiter(int nodeId, Int2ObjectMap<Int2ObjectMap<String>> literCands,int n){
			Graph<VertexString, TypedEdge> pattern = this.gNode.pattern;
			int attr = pattern.getVertex(nodeId).getAttr();
			if(literCands.containsKey(attr)){
				for(Entry<Integer, String> entry : literCands.get(attr).entrySet()){
					EqLiteral x = new EqLiteral(nodeId,entry.getKey(),entry.getValue());
					literArray.add(x);
					LiterNode t = addNodeYLiteral(x,-1);
					t.yLiterl = literArray.size()-1;
				}
			}
		}
		
		private void addPatternNodeVar(int nodeId, Int2ObjectMap<Int2ObjectMap<String>> literCands,int n){
			Graph<VertexString, TypedEdge> pattern = this.gNode.pattern;
			int attr = pattern.getVertex(nodeId).getAttr();
			if(literCands.containsKey(attr)){
				int num = this.gNode.attrs.get(attr);
			
				if( num >1){
					for(int i= 1; i< num -1;i++){
						   
							Pair<Integer,Integer> p1 = new Pair<Integer,Integer>(attr,i-1);
							
							int nodeId1 = this.gNode.nodeSet.get(p1);
							for(int k: literCands.get(attr).keySet()){
								EqVarLiter xv = new EqVarLiter(nodeId1,nodeId,k,k);
								varArray.add(xv);
								LiterNode t = addNodeYVar(xv,n);
								t.yVar = varArray.size()-1;
							}
						}
					}
				}
					
		}
		
//prune;
		
		
	

		private void addNodeNotRoot(LiterNode t,int n){
			int i = t.addLiteral+1;
		    for(; i<literArray.size() && i!= t.yLiterl;i++ ){
		    	EqLiteral eql = literArray.get(i);
		    	addNodeXLiteral(t,eql,n);			
		    } 
		    int j = t.addVar +1;
		    for(; j<varArray.size() && j!= t.yVar;j++ ){
		    	EqVarLiter eql = varArray.get(i);
		        addNodeXVar(t,eql,n);

		    }
		}	
				
	private LiterNode addNodeYVar(EqVarLiter xv, int n) {
			// TODO Auto-generated method stub
		LiterNode t = new LiterNode();
		t.setParent(this.root);
		this.root.children.add(t);
		t.dependency = new Condition();
		t.getDependency().setYEqualsVariable(xv);
		t.getDependency().isLiteral = false;
		t.addLiteral = -1;
		t.addVar = -1;
		t.yVar = -1;
		if(n== -1){
			t.cId = condition_Map.size()+1;
			condition_Map.put(t.cId, t);
		}else{
			t.cId = n + addNew ;
			condition_Map.put(t.cId, t);
			addNew ++;
		}
		return t;
	}
	private LiterNode addNodeYLiteral( EqLiteral x, int n) {
			// TODO Auto-generated method stub
		LiterNode t = new LiterNode();
		t.setParent(this.root);
		this.root.children.add(t);
		t.dependency = new Condition();
		t.getDependency().setYEqualsLiteral(x);
		t.getDependency().isLiteral = true;
		if(n== -1){
			t.cId = condition_Map.size()+1;
			condition_Map.put(t.cId, t);
		}else{
			t.cId = n + addNew ;
			condition_Map.put(t.cId, t);
			addNew ++;
		}
		t.addLiteral = -1;
		t.addVar = -1;
		t.yLiterl = -1;
		return t;
	}
	private LiterNode addNodeXVar(LiterNode g, EqVarLiter xv,int n) {
		// TODO Auto-generated method stub
		LiterNode t = new LiterNode();
		t.setParent(g);
		g.children.add(t);
	    t.dependency = ColoneUtils.clone(g.getDependency());
		//t.addXLiteral = false;
		t.addLiteral = g.addLiteral;
		t.addVar = g.addVar+1;
		t.getDependency().XEqualsVariable.add(xv);
		t.literNum ++;
		t.yLiterl = g.yLiterl;
		t.yVar = g.yVar;
		if(n== -1){
			t.cId = condition_Map.size()+1;
			condition_Map.put(t.cId, t);
		}else{
			t.cId = n + addNew ;
			condition_Map.put(t.cId, t);
			addNew ++;
		}
		return t;
		
	}

	private LiterNode addNodeXLiteral(LiterNode g, EqLiteral x, int n) {
		// TODO Auto-generated method stub
		LiterNode t = new LiterNode();
		t.setParent(g);
		g.children.add(t);
	    t.dependency = ColoneUtils.clone(g.getDependency());
		//t.addXLiteral = true;
		t.addLiteral = g.addLiteral+1;
		t.addVar = g.addVar;
		t.getDependency().XEqualsLiteral.add(x);
		t.literNum ++;
		t.yLiterl = g.yLiterl;
		t.yVar = g.yVar;
		if(n== -1){
			t.cId = condition_Map.size()+1;
			condition_Map.put(t.cId, t);
		}else{
			t.cId = n + addNew ;
			condition_Map.put(t.cId, t);
			addNew ++;
		}
		//log.debug(t.cId);
		//log.debug(condition_Map.get(t.cId));
		return t;
		
	}


// for prune;

	private LiterNode addNodeforPrune(LiterNode g) {
		// TODO Auto-generated method stub
		int preCId = g.parent.cId;
		LiterNode t = new LiterNode();
		t.setParent(this.condition_Map.get(preCId));
		this.root.children.add(t);
		t.dependency = g.dependency;
		t.cId = g.cId;
		t.addLiteral = -1;
		t.addVar = -1;
		t.yVar = -1;
		t.yLiterl = -1;
		condition_Map.put(t.cId, t);
		return t;
	}
	
	private void updateNodeforPrune(int nodeId,LiterNode t, Int2ObjectMap<Int2ObjectMap<String>> literCands){
		int n = this.gNode.parent.ltree.getConditionNum();
		if(t == this.root){
			addPatternNodeLiter( nodeId,  literCands,n);
			addPatternNodeVar( nodeId,  literCands,n);	
		}
		if(t!= this.root){
			addNodeNotRoot(t,n);
		}	
		
	}

	public int getConditionNum(){
		return this.condition_Map.size();
	}

  
			
			
/*
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

	 
	/*
	 
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
		 
		// GfdNode g = new GfdNode();
		 //Graph<VertexString, TypedEdge> pattern = new SimpleGraph<VertexString, TypedEdge>(VertexString.class,
					//TypedEdge.class);
		// pattern.loadGraphFromVEFile("data/query/1-2.ptn",true);
		 //log.debug(pattern.toString());
		// g.setPattern(pattern);
		 
		 
		 tree.gNode = new GfdNode();
		 //Question: from 0 or 1; 1
		 Set<String> s = new HashSet<String>();
		 s.add("a");
		 s.add("b");
		 tree.gNode.literDom.put(1,s);
		 tree.gNode.literDom.put(2,s);
		 tree.gNode.literDom.put(3,s);
		 tree.gNode.nodeNum =3;
		 
		 IntSet b = new IntOpenHashSet();
		 b.add(2);
		 b.add(3);
		 tree.gNode.varDom.put(1, b);
		 tree.gNode.varDom.put(2, b);
		 
		 LiterNode t = new LiterNode();
		 t.setDependency(cond2);
		 tree.extendNode(tree.getRoot());
		 for(LiterNode g: tree.getRoot().children){
		    tree.extendNode(g);
		    for(LiterNode g2 : g.children){
		    	tree.extendNode(g2);
		    	 log.debug("g has children"+ g.getChildren().size());
		    }
		   	
		 }
		
		 
		// tree.extendNode(s, tree.getRoot());
		 log.debug("t has children"+ tree.getRoot().getChildren().size());	 
		
	}*/


	 /**********************************************************************
	  * ********************************************************************
	  * add node for negative checking : public interface for negative pattern
	  * ***********************************************************************
	  * *************************************************************************/
	 public void addNegCheck(LiterNode t,int n){
		 if(t.dependency.isLiteral){
			 EqLiteral p = t.dependency.YEqualsLiteral;
			 LiterNode x = addNodeXLiteral(t, p, n );
			 x.negCheck = true;
			 x.extend = false;
		 }
		 else{
			  EqVarLiter p = t.dependency.YEqualsVariable;
			  LiterNode x = addNodeXVar(t,p, n);
			 x.negCheck = true;
		 }
	 }
	 
	 ///////////////////////////////////////////////////////////////////////////////
	 /*
	  * for disconnected pattern literal extention;// condition xy x : pattern num;
	  * 
	  */////////////////////////////////////////////////////////////////////////////

	 /**********************************************************************
	  * ********************************************************************
	  * for disconnected pattern literal extention;// condition xy x : pattern num;
	  * ***********************************************************************
	  * *************************************************************************/
	/*
	 private void processSupSatLiter(IntSet p1match1,IntSet p2match2 , IntSet p3match1,IntSet p3match2,
			 DisconnectedTree dtree, List<Pair<Integer,String>> xl,Pair<Integer,Integer> p3){
		 IntSet p1join = Fuc.Intersection(p1match1, p3match2);
		 double supp1 = (double)p1join.size()/dtree.nodeNum;
		 if(supp1 >= Params.VAR_SUPP){
			 IntSet p2join = Fuc.Intersection(p2match2, p3match2);
			 double supp2 = (double)p2join.size()/dtree.nodeNum;
			 if(supp2 >= Params.VAR_SUPP){
				 double supp = supp1 * supp2;
				 if(supp >= Params.VAR_SUPP){
					 LiterNode t = extendRootNodeXl(xl,p3);
					 if(p1join.equals(p1match1) && p2join.equals(p2match2)){
						 //satisfy
						 t.isSat = true;
						 t.extend = false;
						 this.dNode.conditions.add(t.dependency);
					 }
					 if(t.extend){
						 t.pivot1 = p1join;
						 t.pivot1 = p2join;
					 }
				 }
			 }
		 }
		 
	 }
	 private void processSupSatVar(IntSet p1match1,IntSet p2match2 , IntSet p3match1,IntSet p3match2,
			 DisconnectedTree dtree, Pair<Integer,Integer> p3, Pair<Integer,Integer> p4){
		 IntSet p1join = Fuc.Intersection(p1match1, p3match2);
		 double supp1 = (double)p1join.size()/dtree.nodeNum;
		 if(supp1 >= Params.VAR_SUPP){
			 IntSet p2join = Fuc.Intersection(p2match2, p3match2);
			 double supp2 = (double)p2join.size()/dtree.nodeNum;
			 if(supp2 >= Params.VAR_SUPP){
				 double supp = supp1 * supp2;
				 if(supp >= Params.VAR_SUPP){
					 LiterNode t = extendRootNodeXv(p3, p4);
					 if(p1join.equals(p1match1) && p2join.equals(p2match2)){
						 //satisfy
						 t.isSat = true;
						 t.extend = false;
						 this.dNode.conditions.add(t.dependency);
					 }
					 if(t.extend){
						 t.pivot1 = p1join;
						 t.pivot1 = p2join;
					 }
				 }
			 }
		 }
		 
	 }
	 
	 private void processSupSatLiter1(IntSet p1match1, LiterNode ln, DisconnectedTree dtree, int nodeId, String s){
		 IntSet p1join = new IntOpenHashSet();
			 p1join = Fuc.Intersection(p1match1, ln.pivot1);
			 double supp1 = (double)p1join.size()/dtree.nodeNum;
			 if(supp1 >= Params.VAR_SUPP){
				 double supp2 = (double)ln.pivot2.size()/dtree.nodeNum;
				 double supp = supp1 * supp2;
				 if(supp >= Params.VAR_SUPP){
					 LiterNode t = addNode(ln,0,nodeId,s);
					 if(p1join.equals(p1match1)){
						 //satisfy
						 t.isSat = true;
						 t.extend = false;
						 this.dNode.conditions.add(t.dependency);
					 }
					 if(t.extend){
						 t.pivot1 = p1join;
						 t.pivot2 = ln.pivot2;
						 t.addXLiteral = true;
						 t.addXLiteral2 = true;
					 }
				 }
			 }
		 }
	 private void processSupSatLiter2(IntSet p1match1, LiterNode ln, DisconnectedTree dtree, int nodeId, String s){
		 IntSet p1join = new IntOpenHashSet();
			 p1join = Fuc.Intersection(p1match1, ln.pivot2);
			 double supp1 = (double)p1join.size()/dtree.nodeNum;
			 if(supp1 >= Params.VAR_SUPP){
				 double supp2 = (double)ln.pivot1.size()/dtree.nodeNum;
				 double supp = supp1 * supp2;
				 if(supp >= Params.VAR_SUPP){
					 LiterNode t = addNode(ln,0,nodeId,s);
					 if(p1join.equals(p1match1)){
						 //satisfy
						 t.isSat = true;
						 t.extend = false;
						 this.dNode.conditions.add(t.dependency);
					 }
					 if(t.extend){
						 t.pivot2 = p1join;
						 t.pivot1 = ln.pivot1;
						 t.addXLiteral = true;
						 t.addXLiteral2 = true;
					 }
				 }
			 }
		 }
	 private void processSupSatVar2(IntSet p1match1, IntSet p2match2,LiterNode ln, DisconnectedTree dtree, int nodeId, int nodeId2){
		 IntSet p1join = new IntOpenHashSet();
		 IntSet p2join = new IntOpenHashSet();
			 p1join = Fuc.Intersection(p1match1, ln.pivot1);
			 double supp1 = (double)p1join.size()/dtree.nodeNum;
			 if(supp1 >= Params.VAR_SUPP){
				 p1join = Fuc.Intersection(p2match2, ln.pivot2);
				 double supp2 = (double)p2join.size()/dtree.nodeNum;
				 double supp = supp1 * supp2;
				 if(supp >= Params.VAR_SUPP){
					 LiterNode t = addNode(ln,0,nodeId,nodeId2);
					 if(p1join.equals(p1match1)){
						 //satisfy
						 t.isSat = true;
						 t.extend = false;
						 this.dNode.conditions.add(t.dependency);
					 }
					 if(t.extend){
						 t.pivot2 = p1join;
						 t.pivot1 = ln.pivot1;
					 }
				 }
			 }
		 }
	 
		 
	 
	
	 private void disExtendInitial(DisconnectedTree dtree){
		 //initialize; component size =2;
		 DisConnectedNode dn = this.dNode;
		 String s1 = ""+dn.patterns.get(0);
		 String s2 = ""+dn.patterns.get(1);
		 DisConnectedNode d1 = dtree.disConnectedPatternIndex.get(s1);
		 DisConnectedNode d2 = dtree.disConnectedPatternIndex.get(s2);
		 for(int i: d1.allLiterCands.keySet()){
			 for(String ss1 :d1.allLiterCands.get(i).keySet()){
				 Pair<Integer,String> p1 = new Pair<Integer,String>(i,ss1);
				 for(int j: d2.allLiterCands.keySet()){
					 for(String ss2 :d2.allLiterCands.get(j).keySet()){
						 Pair<Integer,String> p2 = new Pair<Integer,String>(j,ss2);
						 List<Pair<Integer,String>> xl = new ArrayList<Pair<Integer,String>>();
						 xl.add(p1);
						 xl.add(p2);
						 for(int i1: dn.allVarCands.keySet()){
							 for(int j1: dn.allVarCands.get(i1).keySet()){
								 Pair<Integer,Integer> p3 = new Pair<Integer,Integer>(i1,j1);
								 extendRootNodeXl(xl,p3);
								 /////////////////////////////////////////////
								 //test supp and sat
								 IntSet p1match1 = d1.allLiterCands.get(i).get(ss1);
								 IntSet p2match2 = d2.allLiterCands.get(j).get(ss2);
								 IntSet p3match1 = dn.allVarCands.get(i1).get(j1).get(0);
								 IntSet p3match2 = dn.allVarCands.get(i1).get(j1).get(1);
								 processSupSatLiter(p1match1,p2match2 , p3match1,p3match2,
										dtree,  xl,p3);	 
							 }
						 }
					 }		 
				 }
			 }
		 }
		 for(int i1: dn.allVarCands.keySet()){
			 for(int j1: dn.allVarCands.get(i1).keySet()){
				 Pair<Integer,Integer> p3 = new Pair<Integer,Integer>(i1,j1);
				 for(int i2: dn.allVarCands.keySet()){
					 for(int j2: dn.allVarCands.get(i1).keySet()){
						 Pair<Integer,Integer> p4 = new Pair<Integer,Integer>(i2,j2);
						 if(!p3.equals(p4)){
							 IntSet p1match1 = dn.allVarCands.get(i1).get(j1).get(0);
							 IntSet p2match2 = dn.allVarCands.get(i1).get(j1).get(1);
							 IntSet p3match1 = dn.allVarCands.get(i2).get(j2).get(0);
							 IntSet p3match2 = dn.allVarCands.get(i2).get(j2).get(1);
							 
							processSupSatVar(p1match1,p2match2 , p3match1,p3match2,
										dtree, p3,p4);	 
						 }
					 }
				 }
			 }
		 }
	 }
				 
		
	 
	 private void disExtendRootchildren(LiterNode t, DisconnectedTree dtree ){
		 DisConnectedNode dn = this.dNode;
		 String s1 = ""+dn.patterns.get(0);
		 String s2 = ""+dn.patterns.get(1);
		 DisConnectedNode d1 = dtree.disConnectedPatternIndex.get(s1);
		 DisConnectedNode d2 = dtree.disConnectedPatternIndex.get(s2);
		 if(t.extend == true){
			 for(int i: d1.allLiterCands.keySet()){
				 for(String ss1 :d1.allLiterCands.get(i).keySet()){
					 Pair<Integer,String> p1 = new Pair<Integer,String>(i,ss1);
					 boolean flag = addLiteral(t, p1.y, p1.x);
					 if(flag == true){
						 IntSet p1match1 = d1.allLiterCands.get(i).get(ss1);
						 processSupSatLiter1(p1match1, t, dtree, i, ss1);
					 }
				 }
			 }
			 for(int i: d2.allLiterCands.keySet()){
				 for(String ss1 :d2.allLiterCands.get(i).keySet()){
					 Pair<Integer,String> p1 = new Pair<Integer,String>(i,ss1);
					 boolean flag = addLiteral(t, p1.y, p1.x);
					 if(flag == true){
						 IntSet p1match1 = d1.allLiterCands.get(i).get(ss1);
						 processSupSatLiter2(p1match1, t, dtree, i, ss1);
					 }
				 }
			 }
			 for(int i1: dn.allVarCands.keySet()){
				 for(int j1: dn.allVarCands.get(i1).keySet()){
					 boolean flag = addVar(t, i1,j1 );
		        		if(flag == true){
							 IntSet p1match1 = dn.allVarCands.get(i1).get(j1).get(0);
							 IntSet p2match2 = dn.allVarCands.get(i1).get(j1).get(1);
							 processSupSatVar2(p1match1, p2match2,t, dtree, i1,j1);
						}
				 }
			 }
		 }
	 }
		
				 
	
	 
	 private void extendGeneral(LiterNode t, DisconnectedTree dtree){
		 DisConnectedNode dn = this.dNode;
		 String s1 = ""+dn.patterns.get(0);
		 String s2 = ""+dn.patterns.get(1);
		 DisConnectedNode d1 = dtree.disConnectedPatternIndex.get(s1);
		 DisConnectedNode d2 = dtree.disConnectedPatternIndex.get(s2);
		 
		 LiterNode parent = t.parent;
			for(int i = t.pos; i<parent.children.size();i++){
				if(parent.children.get(i).extend){
					LiterNode tmp = parent.children.get(i);
					if(tmp.extend){
						if(tmp.addXLiteral){
							
							boolean flag = addLiteral(t, tmp.addxl.y, tmp.addxl.x);
			        		if(flag == true){
			        			if(tmp.addXLiteral1){
			        				IntSet p1match1 = d1.allLiterCands.get(tmp.addxl.x).get(tmp.addxl.y);
			        				 processSupSatLiter1(p1match1, t, dtree, tmp.addxl.x,tmp.addxl.y);
								}
			        			if(tmp.addXLiteral2){
			        				IntSet p1match1 = d2.allLiterCands.get(tmp.addxl.x).get(tmp.addxl.y);
			        				 processSupSatLiter1(p1match1, t, dtree, tmp.addxl.x,tmp.addxl.y);
								}
			        			
			        		}
						}else{
							boolean flag = addVar(t, tmp.addxv.x, tmp.addxv.y);
			        		if(flag == true){
			        			 IntSet p1match1 = dn.allVarCands.get(tmp.addxv.x).get(tmp.addxv.y).get(0);
								 IntSet p2match2 = dn.allVarCands.get(tmp.addxv.x).get(tmp.addxv.y).get(1);
								 processSupSatVar2(p1match1, p2match2,t, dtree,  tmp.addxv.x, tmp.addxv.y);
								 
			        		}
						}
						
					}
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
			
			//t.key = t.getDependency().toString();
			t.cId = condition_Map.size()+1;
			condition_Map.put(t.cId, t);
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
			
		//	t.key = t.getDependency().toString();
			t.cId = condition_Map.size()+1;
			condition_Map.put(t.cId, t);
			return t;
		 }
	 
	 
	 /**
	  * the final interface; compute disconnected gfds with the same pattern (this.dNode);
	  * @param dtree
	  */
	 /*
	 public void extendDisConnected(DisconnectedTree dtree){
		 Queue<LiterNode> sln = new LinkedList<LiterNode>();
	 
		 disExtendInitial(dtree);
		 for(LiterNode t: this.getRoot().children){
			 disExtendRootchildren(t, dtree);
			 for(LiterNode g : t.children){
				 sln.add(g);
			 }
		 }
		 while(!sln.isEmpty()){
			 LiterNode t = sln.poll();
			 extendGeneral(t,dtree);
			 if(t.children != null ||!t.children.isEmpty())
			 for(LiterNode g : t.children){
				 sln.add(g);
			 }
		 }
	 }*/

}
