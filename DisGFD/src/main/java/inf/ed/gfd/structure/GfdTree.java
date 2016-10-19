package inf.ed.gfd.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import inf.ed.gfd.util.ColoneUtils;
import inf.ed.gfd.util.Params;
import inf.ed.graph.structure.SimpleGraph;
import inf.ed.graph.structure.adaptor.Pair;
import inf.ed.graph.structure.adaptor.TypedEdge;
import inf.ed.graph.structure.adaptor.VertexString;

public class GfdTree {
	
	static Logger log = LogManager.getLogger(GfdTree.class);
	
    private GfdNode root;
    
    public HashMap<String, GfdNode> pattern_Map;
    
    public GfdTree(){
        this.root = new GfdNode();
        this.pattern_Map = new HashMap<String, GfdNode>();
    }

    
    
    public GfdTree(GfdNode root){
        this.root = root;
    }

	public GfdNode getRoot() {
		return root;
	}

	public void setRoot(GfdNode root) {
		this.root = root;
	}
	
	/*
	 * when this is root;	edgePattern has order		
	 */
	
	  
	
	  private void extendRootChild(GfdNode t, List<DFS> edgePattern, LiterNode s ){
		  // extend root's children
		      //t.wC2Wp.oriPatternId = t.key;
			  DFS tdfs = t.edgePattern;
			 log.debug(tdfs.toString() + ":");
			  int index = edgePattern.indexOf(tdfs);
			  index++;
			  //for AA_1 , add AA_2, A_1A, and A{C},{C}A  and A_1{C} and {C}A_1
			  if(tdfs.isEqualL()){
				  extendSpecial(true,tdfs,t,s);
				  for(; index < (edgePattern.size()); index++){
					  if(edgePattern.get(index).fLabel.x == tdfs.fLabel.x){
						  DFS dfs1 = new DFS(tdfs.tLabel,edgePattern.get(index).tLabel,edgePattern.get(index).eLabel);
						  addNode(t,  dfs1,s);
						  addNode(t,  edgePattern.get(index),s);
					  }
					  if(edgePattern.get(index).tLabel.x == tdfs.fLabel.x){
						  DFS dfs2 = new DFS(edgePattern.get(index).fLabel,tdfs.tLabel,edgePattern.get(index).eLabel);
						  addNode(t,  dfs2,s);
						  addNode(t,  edgePattern.get(index),s);	  
					  }	  
				  }
			  }
			  //for AB: add AB_1 and A_1B and A{C},{C}A, B{D} and {D}B who are larger than AB.
			  else{
				  extendSpecial(false,tdfs,t,s);
				  for(; index < (edgePattern.size()-1); index++){  
					  if(edgePattern.get(index).fLabel.x == tdfs.fLabel.x || 
							  edgePattern.get(index).tLabel.x == tdfs.fLabel.x ||
									  edgePattern.get(index).fLabel.x == tdfs.tLabel.x || 
											  edgePattern.get(index).tLabel.x == tdfs.tLabel.x){
						  	 addNode(t,  edgePattern.get(index),s);
					  }		  
				  }
				  
			  }
			  
		 }
		  
	  
   private void extendNodeGeneral(GfdNode g, List<DFS> edgePattern, LiterNode s){
	   //g.wC2Wp.oriPatternId = g.key;
	   DFS dfs = g.edgePattern;
		int index = findIndex(dfs,edgePattern);
		DFS dfsn = edgePattern.get(index);
		List<GfdNode> gfds = root.getChildren().get(index).getChildren();
		if(gfds!=null){
			if(dfs.isEqualL()){
				extendSpecial(true, dfs, g, s);	
			}
			else{
				extendSpecial(false, dfs, g, s);	
			}
			extendGeneral(gfds,dfsn, dfs, g, s);	
		}
   }

   /*
    * extend AA_1 by AA_2, A_1A and AB by A_1B and AB_1
    */
   private void extendSpecial( boolean flag, DFS dfs, GfdNode g, LiterNode s){
        
 	   Pair<Integer,Integer> b1 = addPair(dfs.tLabel);
 	   Pair<Integer,Integer> b2 = addPair(dfs.fLabel);
 	   int i= dfs.fLabel.y;
 	   int j= dfs.tLabel.y;
 	   
 	   if(flag){//A_mA_n
 		   if(dfs.fLabel.compareTo(dfs.tLabel)<0){//m<n
 			   DFS dfs1 = new DFS(dfs.fLabel,b1,dfs.eLabel);
 			   DFS dfs2 = new DFS(dfs.tLabel,dfs.fLabel,dfs.eLabel);
 			   addNode(g,  dfs1, s);
 			   addNode(g,  dfs2,  s);
 			   
 			   //A_1A_3 ; add A_1A_4,A_1A_2, A_2A_3, and A_3,A_1
 			   if(i<j-1){
 				   for(i=i+1;i<j;i++){
 					   Pair<Integer,Integer> tp = new Pair<Integer,Integer>(dfs.fLabel);
 					   tp.y = i;
 					   DFS dfs3 = new DFS(dfs.fLabel,tp,dfs.eLabel);
 					   DFS dfs4 = new DFS(tp,dfs.tLabel,dfs.eLabel);
 					  //log.debug(dfs3.toString()+"\t"+dfs4.toString());
 					   addNode(g,  dfs3,  s);
 					   addNode(g,  dfs4, s);
 					} 
 			   }
 		   }
 		   else{//A_3A_1, A_3A_2, A_2A_1, A_3,A_4  ---m>n
 			   DFS dfs1 = new DFS(dfs.fLabel,b2,dfs.eLabel);
 			   addNode(g,  dfs1, s);
 			   if(j<i-1){
 				   for(i=i-1;i>j;i--){
 					   Pair<Integer,Integer> tp = new Pair<Integer,Integer>(dfs.fLabel);
 					   tp.y = i;
 					   DFS dfs3 = new DFS(tp,dfs.tLabel,dfs.eLabel);
 					   DFS dfs4 = new DFS(dfs.fLabel,tp,dfs.eLabel);
 					   addNode(g,  dfs3, s);
 					   addNode(g,  dfs4, s);
 					} 
 			   }
 			   
 		   }
 	   }
 	   else{//A_mB_n 
 		   DFS dfs1 = new DFS(dfs.fLabel,b1,dfs.eLabel);
 		   DFS dfs2 = new DFS(b2,dfs.tLabel,dfs.eLabel);
 		   addNode(g, dfs1, s);
 		   addNode(g, dfs2, s);
 	   }  
    }
    
	
   /*
	 * @flag : AB or BA 
	 * @gfds : root->children->children;
	 * @dfsn : dsf in edgePattern; 
	 * @dfs  : t.dfs
	 * @attr_Map : attr ID to String
	 */
    private void extendGeneral(List<GfdNode> gfds, DFS dfsn, DFS dfs, 
    		GfdNode t, LiterNode s){
			for(GfdNode g:gfds){
				DFS dfs1 = g.edgePattern;

				
				if(!dfs1.isEqualL() && !dfs1.isEqualL(dfsn)){//AB : AX	
					if(dfs1.fLabel.equals(dfsn.fLabel)){
						DFS dfs2 = new DFS(dfs.fLabel,dfs1.tLabel,dfs.eLabel);
						addNode(t, dfs2, s);
					}
					if(dfs1.tLabel.equals(dfsn.fLabel)){//XA
						DFS dfs2 = new DFS(dfs1.fLabel,dfs.fLabel,dfs.eLabel);
						addNode(t,  dfs2, s);
					}
					if(dfs1.tLabel.equals(dfsn.tLabel)){//XB
						DFS dfs2 = new DFS(dfs1.fLabel,dfs.tLabel,dfs.eLabel);
						addNode(t,  dfs2, s);
					}
					if(dfs.fLabel.compareTo(dfs.tLabel)<0 ){
						if(dfs1.fLabel.equals(dfsn.tLabel) && !dfs1.tLabel.equals(dfsn.fLabel)){//BX
							DFS dfs2 = new DFS(dfs.tLabel,dfs1.tLabel,dfs.eLabel);
							addNode(t,  dfs2, s);
						}
					}
			   }
		   }	
			
	}
	
	private int findIndex(DFS dfs,List<DFS> edgePattern){
	  DFS dfsn = dfs.findDFS();
	  return edgePattern.indexOf(dfsn);
  }
   

   /*
    * 
    */
  
   /*
    * for AA to get AA_1
    */
  private Pair<Integer,Integer> addPair(Pair<Integer,Integer> a){
	   
	   if(a.y == 0){
		   Pair<Integer,Integer> b = new Pair<Integer,Integer>(a.x,1);
		   return b;
	   }
	   else{
		   Pair<Integer,Integer> b = new Pair<Integer,Integer>(a.x,a.y+1);
		   return b;
	   }
   }
   
			
   /**
    * 
    * @param attr_Map attr id: attr 
    * @param dfs
    * @return
    */
  
   private GfdNode newNode(GfdNode t, DFS dfs){
	  
			log.debug(dfs.toString());
			GfdNode g = new GfdNode();
			g.setParent(t);
			//g.setPattern(ColoneUtils.clone((SimpleGraph<VertexString, TypedEdge>)t.pattern));
			//g.setPatternCode(ColoneUtils.clone(t.patternCode));
			g.edgePattern = dfs;
			t.children.add(g);
			//how to create new node for pattern;
			Pair<Integer,Integer> e1 = dfs.fLabel;
			Pair<Integer,Integer> e2 = dfs.tLabel;
			//g.getPatternCode().add(dfs);
			if(!g.nodeSet.containsKey(e1)){
				//String attr1 = getAttr( e1);
				//VertexString vertex1 = new VertexString(g.pattern.vertexSize()+1, attr1);
				//g.getPattern().addVertex(vertex1);
				g.nodeSet.put(e1,g.nodeNum+1);
				g.nodeNum++;
			}
			if(!g.nodeSet.containsKey(e2)){
				//String attr2 = getAttr( e2);
				//VertexString vertex2 = new VertexString(g.pattern.vertexSize()+1, attr2);
				//g.getPattern().addVertex(vertex2);
				g.nodeSet.put(e2,g.nodeNum+1);
				g.nodeNum++;
			}
			/*
			int fId = g.nodeSet.get(e1);
			int tId = g.nodeSet.get(e2);
			if(g.getPattern().contains(fId, tId)){
				TypedEdge e = g.getPattern().getEdge(fId, tId);
				e.setAttr(dfs.eLabel);
			}
			else{
				TypedEdge e = new TypedEdge(g.getPattern().allVertices().get(fId),
					g.getPattern().allVertices().get(tId));
				e.setAttr(dfs.eLabel);
			}*/
			pattern_Map.put(g.key, g);
			//create work unit
			int fId = g.nodeSet.get(e1);
			int tId = g.nodeSet.get(e2);
			t.wC2Wp.edgeIds.put(dfs, new Pair<Integer,Integer>(fId, tId));	
			g.ltree.gNode = g;
			//g.ltree.extendNode(dom, g.ltree.getRoot());
			return g;
	   
   }
   
   
   
   private void addNode(GfdNode t, DFS dfs, Set<String> dom){
	   
		   GfdNode g = newNode(t,dfs);
		
				g.key = dfs.toString();
				g.ltree.extendNode(dom, g.ltree.getRoot());	
				log.debug(g.ltree.getRoot().children.size());
	
    }

	private void addNode(GfdNode t, DFS dfs,  LiterNode s){
		if(t.nodeNum >= Params.var_K){
			log.debug("pattern has already k size");
		}
		else{
			GfdNode g = newNode(t,dfs);
			g.key = g.parent.key+dfs.toString();
		    g.ltree.addNode(g.ltree.getRoot(), s.dependency);
		}
	}
	
	public void updateLiteral(GfdNode t, List<DFS> edgePattern, LiterNode s){
		log.debug(s.key);
		if(t.children.isEmpty() || t.children == null){
			this.extendNode(t, edgePattern, s);
		}
		else{
			for(GfdNode g : t.children){
			//if(s == this.root){
				if(s.parent == t.ltree.getRoot()){
					 g.ltree.addNode(g.ltree.getRoot(), s.dependency);
				}
				else{
					 String key = s.getParent().key;
					 log.debug(key);
					 LiterNode update = g.ltree.condition_Map.get(key);
					 if(s.addXLiteral){
						 g.ltree.addNode(update, 0, s.addxl.x,  s.addxl.y);
					 }else{
						 g.ltree.addNode(update, 0, s.addxv.x, s.addxv.y);
					 }
				 }
			}
		}
			// }
		 
	 }
	
	/*
	 * get attr from attr Id
	 */
	/*
	private String getAttr( Pair<Integer,Integer> node){
		int id;
		if(node.y == 0){
			id = node.x;
		}
		else{
			id = node.y;
		}
		return attr_Map.get(id);
	}*/
	
	
    
	
	 /***************************************************************************
	  * ****************************************************************************
	 * *
	 *  Interface;
	 * 
	 ***************************************************************************
	  * *****************************************************************************/
	public void extendNode(GfdNode g, List<DFS> edgePattern, Set<String> dom ){	
	   for(DFS dfs: edgePattern){
		    addNode(root, dfs,dom);
	   }		   
	}
	 public void extendNode(GfdNode g, List<DFS> edgePattern, 
					   LiterNode s ){
		
		    if(g.parent == this.getRoot()){
		    	extendRootChild(g, edgePattern, s);
		    }
		    else{
		    	 extendNodeGeneral(g, edgePattern, s);	
		    }
		   
	   }

		
	

	public static void main(String args[]) {  
		HashMap<Integer, String> attr_Map = new HashMap<Integer, String>();
		attr_Map.put(1, "a");
		attr_Map.put(2, "b");
		attr_Map.put(3, "c");
		List<DFS> edgePattern = new ArrayList<DFS>();
		Pair<Integer,Integer> t1 = new Pair<Integer,Integer>(1,0);
		Pair<Integer,Integer> t11 = new Pair<Integer,Integer>(1,1);
		Pair<Integer,Integer> t2 = new Pair<Integer,Integer>(2,0);
		Pair<Integer,Integer> t3 = new Pair<Integer,Integer>(3,0);
		
		DFS d1 = new DFS(t1,t2,1);
		DFS d7 =  new DFS(t1,t11,2);
		DFS d2 = new DFS(t1,t2,2);
		DFS d3 = new DFS(t1,t3,2);
		DFS d4 = new DFS(t2,t3,1);
		DFS d5 = new DFS(t3,t1,1);
		DFS d6 = new DFS(t3,t1,2);
		edgePattern.add(d7);
		edgePattern.add(d1);
		edgePattern.add(d2);
		edgePattern.add(d3);
		edgePattern.add(d4);
		edgePattern.add(d5);
		edgePattern.add(d6);
		Set<String> dom = new HashSet<String>();
		dom.add("a");
		dom.add("b");
		
		GfdTree gtree = new GfdTree();
		gtree.extendNode(gtree.getRoot(),edgePattern, dom );
		log.debug( gtree.root.getChildren().size());
		//gtree.initialExtend(edgePattern,attr_Map);
		for(GfdNode g1: gtree.root.getChildren()){
			
			log.debug(g1.key);
			log.debug(g1.ltree.getRoot().children.size());
			
			for(LiterNode s :g1.ltree.getRoot().children){
				log.debug(s.key);
				gtree.updateLiteral(g1,edgePattern, s);
				g1.ltree.extendNode(dom, s);
			}
		
			for(GfdNode g2: g1.getChildren()){
				if(g2.nodeNum > g1.nodeNum){
					g2.ltree.updateNode(dom, g2.ltree.getRoot(), g2.nodeNum);
					for(LiterNode l : g2.ltree.getRoot().children){
						g2.ltree.updateNode(dom, g2.ltree.getRoot(), g2.nodeNum);
					}
				}
				
		    }
		
		
			//gtree.extendNode(g1,edgePattern, dom );
		}
	
		log.debug("sucess");
		
	    
	}

}
