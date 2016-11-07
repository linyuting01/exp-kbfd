package inf.ed.gfd.structure;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import inf.ed.gfd.util.ColoneUtils;
import inf.ed.graph.structure.SimpleGraph;
import inf.ed.graph.structure.adaptor.Pair;
import inf.ed.graph.structure.adaptor.TypedEdge;
import inf.ed.graph.structure.adaptor.VertexString;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class GfdTree implements Serializable {
	
	static Logger log = LogManager.getLogger(GfdTree.class);
	
    private GfdNode root;
    
    public int beginId = 0;
    
   
    
    //public HashMap<String, GfdNode> pattern_Map;
    public HashMap<Integer, GfdNode> patterns_Map;
    
   public Map<DFS,Integer> dfs2Ids;
    
    public Int2ObjectMap<Int2ObjectMap<String>> literCands ;
    
    public GfdTree(){
        this.root = new GfdNode();
        this.root.key="";
        this.patterns_Map = new HashMap<Integer, GfdNode>();
        //this.patterns_Map.put(0, root);
        this.literCands =  new Int2ObjectOpenHashMap<Int2ObjectMap<String>>();
        this.dfs2Ids =  new HashMap<DFS,Integer>();
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
	
	
	/******************************************************************************************
	  * first add the right neighbours of the current node;
	  * then check if it has new node to add in , 
	  * if so, add the new node from edge pattern. 
	 *****************************************************************************************/
    /*
	public void extendGfdNode(List<DFS> edgePattern, GfdNode t){
		if(t.parent == this.getRoot()){
			extendRootChild( t, edgePattern);
		}
		else{
			extendNodeGeneral(t);
		}
	}*/
	
	
	public void extendRoot(List<DFS> edgePattern){
		 for(DFS dfs: edgePattern){
			  newNode(root,dfs);
			 //g.extend = true;
		 }
			// for(GfdNode t : this.root.children){
				// log.debug("extenf general t" + t.edgePattern.toString());
				// extendRootChild(t,edgePattern);
				 /*
				 for(GfdNode x :t.children){
					 log.debug(x.edgePattern.toString());
				 }*/
		 // }
		 
	 }
	
	
	
	public void extendGeneral(List<DFS> edgePattern, GfdNode t){
		if(t.parent.parent == this.root){
			Collections.sort(edgePattern);
			extendRootChild(t,edgePattern);
		}
		else{
		DFS dfs = t.edgePattern;
		int fx = dfs.fLabel.x;
		int tx = dfs.tLabel.x;
		int fy = dfs.fLabel.y;
		int ty = dfs.tLabel.y;
		//log.debug("extend dfs:"+ dfs);
		for(DFS dfs1 : edgePattern){
			//log.debug("extend's candidate:" + dfs1);
			int fx1 = dfs1.fLabel.x;
			int tx1 = dfs1.tLabel.x;
			if(fx1==fx){  // t: A_mC_n
				if(tx1 == tx){  //edgepattern: AC
					int n1 = t.attrs.get(tx);//A_mC_n+1
					Pair<Integer,Integer> p3 = new Pair<Integer,Integer>(tx,n1);
					DFS dfs13 = new DFS(dfs.fLabel,p3,dfs1.eLabel);
					
				
					newNode(t,dfs13);	
					int m = t.attrs.get(fx);
					Pair<Integer,Integer> p1 = new Pair<Integer,Integer>(fx,m);  //A_m+1
					int n = t.attrs.get(tx);
					for(int k = 0;k<n;k++){//A_m+1C_0,..., A_m+1C_n
						Pair<Integer,Integer> p2 = new Pair<Integer,Integer>(tx,k);
						DFS dfs11 = new DFS(p1,p2,dfs1.eLabel);
						
						//log.debug(dfs11.toString());
						
						 newNode(t,dfs11);		
					}
					if(dfs.isEqualL()){// A_mA_n
						if(fy <ty){ //m<n add A_nA_m
							DFS dfs12 = new DFS(dfs.tLabel,dfs.fLabel,dfs1.eLabel);	
							//log.debug(dfs12.toString());
							
							newNode(t,dfs12);
							
						}
					}
				}
				if(tx1 > tx){//edgePattern: AD
					if(!t.attrs.containsKey(tx1)){ //AD
						DFS dfs12 = new DFS(dfs.fLabel,dfs1.tLabel,dfs1.eLabel);
				
						
						newNode(t,dfs12);
						
						//log.debug(dfs12.toString());
					}
					else{//AD_1.AD_n
						int n = t.attrs.get(tx1);
						for(int k=0;k<=n;k++){
							Pair<Integer,Integer> p2 = new Pair<Integer,Integer>(tx1,k);
							DFS dfs12 = new DFS(dfs.fLabel,p2,dfs1.eLabel);
							
							 newNode(t,dfs12);	
							
							// log.debug(dfs12.toString());
						}
						
					}
				}
				
				if(tx1<tx){
					
					if(!t.attrs.containsKey(tx1)){
						DFS dfs3 = new DFS(dfs.fLabel,dfs1.tLabel,dfs1.eLabel);
					
						
						newNode(t,dfs3);
					
					}
					if(t.attrs.containsKey(tx1)){
						int n = t.attrs.get(tx1);
						Pair<Integer,Integer> p2 = new Pair<Integer,Integer>(tx1,n);
						DFS dfs3 = new DFS(dfs.fLabel,p2,dfs1.eLabel);
						
						
						newNode(t,dfs3);
					}
				}
				
		
			}else{
				if(tx1 == tx){//A_mC_n
					
					if(fx1 > fx){ //BC
						int n = t.attrs.get(tx);
						for(int k=0;k<n;k++){ //BC_0,BC_n
							Pair<Integer,Integer> p3 = new Pair<Integer,Integer>(tx,n);
							DFS dfs13 = new DFS(dfs1.fLabel,p3,dfs1.eLabel);
							
						
							newNode(t,dfs13);
							//log.debug(dfs13.toString());
						}
					}
					if(fx1<fx){
						
						if(!t.attrs.containsKey(tx1)){
							DFS dfs3 = new DFS(dfs1.fLabel,dfs1.tLabel,dfs1.eLabel);
							DFS dfsn = dfs3.findDFS();
							
							newNode(t,dfs3);
						
						}
						if(t.attrs.containsKey(fx1)){
							int n = t.attrs.get(fx1);
							Pair<Integer,Integer> p2 = new Pair<Integer,Integer>(fx1,n);
							
							DFS dfs3 = new DFS(p2,dfs.tLabel,dfs1.eLabel);
							
							newNode(t,dfs3);
						}
					}
						
				}
				else{
				
				if(fx1 == tx){ // A_mC_n
					if(fx1 > fx){ //CB
						if(!t.attrs.containsKey(tx1)){//CB_0,CB_1
							
							 newNode(t,dfs1);
							 //log.debug(dfs1.toString());
						}
						else{
							int n = t.attrs.get(tx1);
							for(int k =0;k<=n;k++){
								Pair<Integer,Integer> p2 = new Pair<Integer,Integer>(tx1,k);
								DFS dfs3 = new DFS(dfs.tLabel,p2,dfs1.eLabel);
								
								newNode(t,dfs3);
								//log.debug(dfs3.toString());
							}
							
						}	
					}
					
					if(fx1 < fx){
					
						if(!t.attrs.containsKey(tx1)){
							DFS dfs3 = new DFS(dfs.tLabel,dfs1.tLabel,dfs1.eLabel);
							
							newNode(t,dfs3);
						
						}
						if(t.attrs.containsKey(tx1)){
							int n = t.attrs.get(tx1);
							Pair<Integer,Integer> p2 = new Pair<Integer,Integer>(tx1,n);
							DFS dfs3 = new DFS(dfs.tLabel,p2,dfs1.eLabel);
							
							newNode(t,dfs3);
						}
					}
				}
				else{
			
					  if(tx1 == fx){ //A_mC_n
						  if(fx1 > fx){ //DA
							  if(!t.attrs.containsKey(fx1)){//D_A_m
								 DFS dfs3 = new DFS(dfs1.fLabel,dfs.fLabel,dfs1.eLabel);
								
								  newNode(t,dfs3);
								 // log.debug(dfs3.toString());
							  }
							  else{////D_0A_m,D_n,A_m
								  int m = t.attrs.get(fx1);
								  for(int k=0;k<=m;k++){
									  Pair<Integer,Integer> p2 = new Pair<Integer,Integer>(fx1,k);
										DFS dfs3 = new DFS(p2,dfs.fLabel,dfs1.eLabel);
										
										newNode(t,dfs3);
										//log.debug(dfs3.toString());
								  }
									  
								  }
							  }
						  if(fx1<fx){
							 
						            
								  if(!t.attrs.containsKey(fx1)){
										DFS dfs3 = new DFS(dfs1.fLabel,dfs.fLabel,dfs1.eLabel);
										
										newNode(t,dfs3);
									
									}
								  if(t.attrs.containsKey(fx1)){
										int n = t.attrs.get(fx1);
										Pair<Integer,Integer> p2 = new Pair<Integer,Integer>(fx1,n);
										DFS dfs3 = new DFS(p2,dfs.fLabel,dfs1.eLabel);
										
										newNode(t,dfs3);
									}
						  }
					  }	
				}
				}
			}
		 if(fx1!=fx && tx1!=tx && fx1 != tx && tx1!=fx){
			   if(t.attrs.containsKey(tx1) ){
				  if(fx1>fx){
					 if(!t.attrs.containsKey(fx1)){
						 newNode(t,dfs1); 
						 //log.debug(dfs1.toString());
					 }
					 else{
						   int m = t.attrs.get(fx1);
						   for(int k=0;k<=m;k++){
								  Pair<Integer,Integer> p2 = new Pair<Integer,Integer>(fx1,k);
									DFS dfs3 = new DFS(p2,dfs1.tLabel,dfs1.eLabel);
									
									newNode(t,dfs3);
									//log.debug(dfs3.toString());
							  }
					 }
						   
					  }
				}
		 }
		}
		}
	
	
		}


	
	 private void extendRootChild(GfdNode t, List<DFS> edgePattern){
		  // extend root's children
		      //t.wC2Wp.oriPatternId = t.key;
			  DFS tdfs = t.edgePattern;
			// log.debug(tdfs.toString() + ":");
			  int index = edgePattern.indexOf(tdfs);
			  index++;
			  //for AA_1 , add AA_2, A_1A, and A{C},{C}A  and A_1{C} and {C}A_1
			  if(tdfs.isEqualL()){
				  extendSpecial(true,tdfs,t);
				  for(; index < (edgePattern.size()); index++){
					  DFS dfs2 = edgePattern.get(index);
					  if(dfs2.fLabel.x.equals(tdfs.fLabel.x)){
						  DFS dfs1 = new DFS(tdfs.tLabel,dfs2.tLabel,dfs2.eLabel);
						 
						  newNode(t,dfs1);
						 
						  newNode(t,dfs2);
						
						  
					  }
					  if(dfs2.tLabel.x.equals(tdfs.fLabel.x)){
						  DFS dfs3 = new DFS(dfs2.fLabel,tdfs.tLabel,dfs2.eLabel);
						  DFS dfsn = dfs3.findDFS();
						
						  newNode(t,dfs3);
						  
						 newNode(t,dfs2);
						
							  
					  }	  
				  }
			  }
			  //for AB: add AB_1 and A_1B and A{C},{C}A, B{D} and {D}B who are larger than AB.
			  else{
				  extendSpecial(false,tdfs,t);
				  
				  for(; index < edgePattern.size(); index++){  
					  DFS dfs2 = edgePattern.get(index);
					  if(dfs2.fLabel.x.equals(tdfs.fLabel.x) || 
							  dfs2.tLabel.x.equals(tdfs.fLabel.x) ||
									  dfs2.fLabel.x.equals(tdfs.tLabel.x) || 
											 dfs2.tLabel.x.equals( tdfs.tLabel.x)){
						 
						
						  newNode(t,dfs2);
						  
					  }
						  	
				  }
				  
			  }
			  
			
			 
		
		 }
	 
	/* 
	 public void extendNodeGeneral(GfdNode g){
		 
		  DFS dfs = g.edgePattern;
		
		
			
				if(dfs.isEqualL()){
					extendSpecial(true, dfs, g);	
				}
				else{
					extendSpecial(false, dfs, g);	
				}
				extendGeneral(g);	
		
			 Collections.sort(g.children);
			  int i = 0 ;
			  //log.debug( dfs.toString() +"children");
			  for(GfdNode gx : g.children){
				  log.debug("general node " +dfs.toString() + "children "+ gx.edgePattern.toString());
				  gx.pos = i;
				  i++;
			  }
	 }
	 /*
	    * extend AA_1 by AA_2, A_1A and AB by A_1B and AB_1
	    */
		
	
	   private void extendSpecial( boolean flag, DFS dfs, GfdNode g){
	        
	 	   Pair<Integer,Integer> b1 = addPair(dfs.tLabel);
	 	   Pair<Integer,Integer> b2 = addPair(dfs.fLabel);
	 	   
	 	   int i= dfs.fLabel.y;
	 	   int j= dfs.tLabel.y;
	 	   
	 	   if(flag){//A_mA_n
	 		 
	 	      
	 		   if(dfs.fLabel.compareTo(dfs.tLabel)<0){//m<n
	 			  // Pair<Integer,Integer> b1 = addPair(dfs.tLabel);
	 			   int k = j+1;
	 		       for(int m = i; m< k ;m++){
	 		    	 Pair<Integer,Integer> b11 = new Pair<Integer,Integer>(dfs.fLabel.x,m);
	 		    	  DFS dfs1 = new DFS(b11,b1,dfs.eLabel);
	 		    	  DFS dfs2 = new DFS(b1,b11,dfs.eLabel);
	 		    	  newNode(g,dfs1); 
					  newNode(g,dfs2); 
	 		       }
	 		       DFS dfs11 = new DFS(dfs.tLabel,dfs.fLabel,dfs.eLabel);
	 		   }
	 		   else{
	 			   //Pair<Integer,Integer> b2 = addPair(dfs.fLabel);
	 			   
	 			   DFS dfs1 = new DFS(dfs.fLabel,b2,dfs.eLabel);
	 			   newNode(g,dfs1);
					
	 			   for(int m = 0; m< i+1; m++){
	 				  Pair<Integer,Integer> b11 = new Pair<Integer,Integer>(dfs.fLabel.x,m);
	 				  DFS dfs2 = new DFS(b2,b11,dfs.eLabel);
	 				  newNode(g,dfs2); 
	 			   }
	 		   }	
	 	   }
	 	   else{//A_mB_n 
	 		   DFS dfs1 = new DFS(dfs.fLabel,b1,dfs.eLabel);
	 		  newNode(g,dfs1);
	 		   //for(int m = i+1; m<= j; m++){
	 			//  Pair<Integer,Integer> b11 = new Pair<Integer,Integer>(dfs.tLabel.x,m);
	 			 DFS dfs2 = new DFS(b2,dfs.tLabel,dfs.eLabel);
	 			  newNode(g,dfs2);
	 		   }
	 			   
	 	    
	    }
	   
	   /*
	   private void extendGeneral(GfdNode t){
		   //first add right neighbor;
		   DFS dfs1 = t.edgePattern;
		   int pos = t.pos +1;
		   for(;pos<t.parent.children.size();pos++){
			   DFS dfs2 = t.parent.children.get(pos).edgePattern;
			  newNode(t,dfs2);
			   
		   }
		   if(!t.newAttr.isEmpty()){
			   String f1 = dfs1.fLabel.x;
			   String t1 = dfs1.tLabel.x;
			   DFS dfse = dfs1.findDFS();
			  // log.debug(dfse.toString());
			   String id = dfse.toString();
			   GfdNode g = this.pattern_Map.get(id);
			  // log.debug(g.edgePattern.toString());
			   for(GfdNode g1: g.children){
				   DFS child = g1.edgePattern;
				   String f2 = child.fLabel.x;
				   String t2 = child.tLabel.x;
				   if(!f2.equals(f1)&& !t2.equals(t1)){
					   if(t2.equals(t.newAttr)){
						   if(t.attrs.containsKey(f2)){
								  int m = t.attrs.get(f2);
								  for(int i = 0;i<=m;i++){
									  Pair<Integer,Integer> fL = new Pair<Integer,Integer>(f2,i);
									  Pair<Integer,Integer> tL = new Pair<Integer,Integer>(t2,0);
									  DFS dfsn = new DFS(fL,tL,child.eLabel);
									  newNode(t,dfsn);
								  }
						   }
						   
					   }
					   if(f2.equals(t.newAttr)){
						   if(!t.attrs.containsKey(t2))
						   newNode(t,child);
					   }
					   }
				   }
				   
			   }
		   }
			   /*
			   String attr = t.newAttr;
			   //add attr begin's edgePattern
			   for(DFS dfs : edgePattern){
				   String fLabel = dfs.fLabel.x;
				   String tLabel = dfs.tLabel.x;
			   
				   if(tLabel.equals(attr) && !fLabel.equals(dfs1.fLabel.x)){
					  if(fLabel.compareTo(dfs1.fLabel.x)>0){
						  if(t.attrs.containsKey(fLabel)){
							  int m = t.attrs.get(fLabel);
							  for(int i = 0;i<=m;i++){
								  Pair<Integer,Integer> fL = new Pair<Integer,Integer>(fLabel,i);
								  DFS dfsn = new DFS(fL,dfs.tLabel,dfs.eLabel);
								  newNode(t,dfs);
							  }
						  }
						  
						 
					  }
						   
					   }
				   if(fLabel.equals(attr)){
						   if(t.attrs.containsKey(tLabel)){
							   //Pair<Integer,Integer> tL = new Pair<Integer,Integer>(attr,0);
							   int m = t.attrs.get(tLabel);
							   for(int i = 0;i<=m;i++){
								   Pair<Integer,Integer> tL = new Pair<Integer,Integer>(tLabel,i);
								   DFS dfsn = new DFS(dfs.fLabel,tL,dfs.eLabel);
								  newNode(t,dfsn);
								   
							   }
								  
							  }
						   }
					   }
				   }*/
			   
			   
			   
		   
	   

	    /*
	 private void extendSpecial( boolean flag, DFS dfs, GfdNode g){
	        
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
	    
		*/  
	
	/*
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
   }*/

   /*
    * extend AA_1 by AA_2, A_1A and AB by A_1B and AB_1
    */
	 /*
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
	 /*
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
	

   

   /*
    * 
    */
  
   /*
    * for AA to get AA_1
    */
	
  private int findIndex(DFS dfs,List<DFS> edgePattern){
	  DFS dfsn = dfs.findDFS();
	 // log.debug(dfsn.toString());
	  return edgePattern.indexOf(dfsn);
  }
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
  
   private void newNode(GfdNode t, DFS dfs){
	   
	  // for(Entry<pair<Integer,Integer>,Integer> entry: g.nodeSet.)
	 // if(!t.extendDfss.contains(dfs)){
	 
		  if(!t.extendDfss.contains(dfs) ){
		    log.debug("extend dfs : " + dfs);
	        t.extendDfss.add(dfs);
		    //log.debug(dfs.toString());
			GfdNode g = new GfdNode();
			g.setParent(t);
			if(t != this.root){
				g.setPattern(ColoneUtils.clone((SimpleGraph<VertexString, TypedEdge>)t.pattern));
			}
			//g.setPatternCode(ColoneUtils.clone(t.patternCode));
			g.edgePattern = dfs;
			t.children.add(g);
			//how to create new node for pattern;
			
			//g.getPatternCode().add(dfs);
			Pair<Integer,Integer> e1 = dfs.fLabel;
			Pair<Integer,Integer> e2 = dfs.tLabel;
			g.nodeSet = new HashMap<Pair<Integer,Integer>,Integer>(t.nodeSet);
			g.nodeNum = t.nodeNum;
			if(!g.nodeSet.containsKey(e1)){
				log.debug("add nodeSet");
				int attr1 = e1.x;
			
					VertexString vertex1 = new VertexString(g.pattern.vertexSize()+1, attr1);
					g.getPattern().addVertex(vertex1);
			
				g.nodeSet.put(e1,g.nodeNum+1);
				g.nodeNum++;
			}
			if(!g.nodeSet.containsKey(e2)){
				int attr2 = e2.x;
				
					VertexString vertex2 = new VertexString(g.pattern.vertexSize()+1, attr2);
					g.getPattern().addVertex(vertex2);
				
				
				g.nodeSet.put(e2,g.nodeNum+1);
				g.nodeNum++;
			}
			
			int fId = g.nodeSet.get(e1);
			int tId = g.nodeSet.get(e2);
			log.debug(dfs.toString()+"\t" +fId +"\t" +tId +"\n");
			if(g.getPattern().contains(fId, tId)){
				TypedEdge e = g.getPattern().getEdge(fId, tId);
				e.setAttr(dfs.eLabel);
			}
			else{
				if(!g.getPattern().contains(tId, fId)){
				TypedEdge e = new TypedEdge(g.getPattern().allVertices().get(fId),
					g.getPattern().allVertices().get(tId));
				e.setAttr(dfs.eLabel);
				g.getPattern().addEdge(e);
				}
			}
			//g.key = t.key+dfs.toString();
			
				g.pId = patterns_Map.size()+1;
				patterns_Map.put(g.pId, g);
				 if(t == this.root){
					   dfs2Ids.put(dfs, g.pId);
				   }
		
			//create work unit
			
			//int id = this.dfs2Id.get(dfs);
			g.addNode =  new Pair<Integer,Integer>(fId, tId);
			g.ltree.gNode = g;
			if(t == this.root){
				g.attrs = new HashMap<Integer,Integer>();
			}
			else{
				g.attrs = new HashMap<Integer,Integer>(t.attrs);
			}
			if(!g.attrs.containsKey(dfs.fLabel.x)){
				g.attrs.put(dfs.fLabel.x, 1);
			}else{
				int num = g.attrs.get(dfs.fLabel.x);
				if(dfs.fLabel.y == num){
					g.attrs.put(dfs.fLabel.x, num+1);
				}
			}
			if(!g.attrs.containsKey(dfs.tLabel.x)){
				g.attrs.put(dfs.tLabel.x, 1);
				//g.newAttr = dfs.tLabel.x;
			}else{
				int num = g.attrs.get(dfs.tLabel.x);
				if(dfs.tLabel.y == num){
					g.attrs.put(dfs.tLabel.x, num+1);
				}
			}
			
			  DFS dn = dfs.findDFS();
			    int dnId = this.dfs2Ids.get(dn);
			    g.edgeIds = new ArrayList<Integer>(t.edgeIds);
			    g.edgeIds.add(dnId);
			    Collections.sort(g.edgeIds);
			    String s="";
			    for(int i:g.edgeIds){
			    	s = s+i+",";
			    }
			    g.orderId = s;
   }
			
		
			
			//g.ltree.extendNode(dom, g.ltree.getRoot());
	 // }
	   
   }
   
   
   /*
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
	
	/**
	 * when g is not the root, according  updateLiteral to invoke
	 * @param g
	 * @param edgePattern
	 * @param s
	 */
   /*
	 private void extendNode(GfdNode g, List<DFS> edgePattern, 
					   LiterNode s ){
		
		    if(g.parent == this.getRoot()){
		    	extendRootChild(g, edgePattern, s);
		    }
		    else{
		    	 extendNodeGeneral(g, edgePattern, s);	
		    }
		   
	   }
	 */
    
	
	 /***************************************************************************
	  * ****************************************************************************
	 * *
	 *  Interface;
	 * 
	 ***************************************************************************
	  * *****************************************************************************/
	
	/**
	 * when g is the root node, extend the one edge pattern and literal \empty ->y;
	 * @param g
	 * @param edgePattern
	 * @param dom
	 */
   /*
	public void extendNode(GfdNode g, List<DFS> edgePattern, Set<String> dom ){	
	   for(DFS dfs: edgePattern){
		    addNode(root, dfs,dom);
	   }		   
	}
	
	 /**
	  * Given a gfd Q x->Y , update Q'(Q+e) X->Y
	  * @param t is the gfdNode Q
	  * @param edgePattern
	  * @param s is the  X->Y in Q's literal tree
	  */
   /*
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

		*/
   
   public void writeToFile(String filename) {
		try {
			FileOutputStream fos = new FileOutputStream(filename, true);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(this);
			oos.close();
		} catch (Exception e) {// Catch exception if any
			e.printStackTrace();
		}
	}

	static GfdTree readFromFile(String filename) {
		GfdTree t = new GfdTree();
		try {
			FileInputStream fis = new FileInputStream(filename);
			BufferedInputStream bis = new BufferedInputStream(fis);
			ObjectInputStream ois = new ObjectInputStream(bis);
			t = (GfdTree) ois.readObject();
			ois.close();
		} catch (Exception e) {// Catch exception if any
			e.printStackTrace();
		}
		return t;
	}
   
	

	public static void main(String args[]) {  
		HashMap<Integer, String> attr_Map = new HashMap<Integer, String>();
		attr_Map.put(1, "a");
		attr_Map.put(2, "b");
		attr_Map.put(3, "c");
		List<DFS> edgePattern = new ArrayList<DFS>();
		Pair<Integer,Integer> t1 = new Pair<Integer,Integer>(1,0);
		Pair<Integer,Integer> t11 = new Pair<Integer,Integer>(2,0);
		Pair<Integer,Integer> t2 = new Pair<Integer,Integer>(3,0);
		Pair<Integer,Integer> t3 = new Pair<Integer,Integer>(4,0);
		
		DFS d7 =  new DFS(t1,t11,2);
		DFS d1 = new DFS(t1,t2,1);
		
		DFS d2 = new DFS(t1,t2,2);
		DFS d3 = new DFS(t1,t3,2);
		DFS d4 = new DFS(t2,t3,1);
		DFS d5 = new DFS(t3,t1,1);
		DFS d6 = new DFS(t3,t1,2);
		edgePattern.add(d7); //ABl
		edgePattern.add(d1); //AC1
		//edgePattern.add(d2); //AC2
		//edgePattern.add(d3); //AD
		edgePattern.add(d4); //CD
		edgePattern.add(d5); //DA1
		//edgePattern.add(d6); //DA2
		Set<String> dom = new HashSet<String>();
		dom.add("a");
		dom.add("b");
		SuppResult r =new SuppResult();
		GfdTree gtree = new GfdTree();
    	gtree.extendRoot(edgePattern);
		
		//gtree.extendNode(gtree.getRoot(),edgePattern, dom );
		//log.debug( gtree.root.getChildren().size());
		//gtree.initialExtend(edgePattern,attr_Map);
		
		for(GfdNode g1: gtree.root.getChildren()){
		for(Pair<Integer,Integer>p: g1.nodeSet.keySet()){
			log.debug(p.toString() + g1.nodeSet.get(p));
			Pair<Integer,Integer> pair = new Pair<Integer,Integer>(1,0);
			if(g1.nodeSet.containsKey(pair)){
				log.debug("true");
			}
			
		}
		}
		
			//gtree.extendGeneral(edgePattern, g1);
		//}
			//for(GfdNode g2: g1.getChildren()){
				// log.debug("extenf general" + g2.edgePattern.toString());
				//gtree.extendGeneral(edgePattern,g2);
				//for(GfdNode x :g2.children){
				 // log.debug(x.edgePattern.toString());
				// }
				
		   // }
		

		System.out.println(gtree.patterns_Map.size());

		String filename = "hello";
		gtree.writeToFile(filename);

		GfdTree t23 = GfdTree.readFromFile(filename);
		System.out.println(t23.patterns_Map.size());
	}
}
	

		
		

	

