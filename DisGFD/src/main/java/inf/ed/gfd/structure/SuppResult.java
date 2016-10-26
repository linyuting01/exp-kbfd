package inf.ed.gfd.structure;


import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

import inf.ed.gfd.algorithm.parDis.ParDisWorker;
import inf.ed.gfd.util.Params;
import inf.ed.grape.interfaces.Result;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

public class SuppResult extends Result implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 111222L;
	/**
		 * 
		 */
	static Logger log = LogManager.getLogger(SuppResult.class);
	//private static final long serialVersionUID = 1L;

	
	//public String patternId; 
	//public String conditionId;
	//public int support;
	//public boolean sat;
	//public int partitionId;
    //	public boolean isConnected;
	//public IntSet patterIds; 
	//public IntSet pivotMatch;
	
	//public HashMap<String,Set<String>> cIds;
	//public List<Int2IntMap> boderMatch;
	public HashMap<String,IntSet> pivotMatchP;
	public HashMap<String, HashMap<String,IntSet>> pivotMatchGfd;
	//public HashMap<String, Set<String>> satCIds; // satisfied 
	public HashMap<String, HashMap<String,Boolean>> satCId;
	public int nodeNum;
	public Set<String> dom;
	
	HashMap<Integer,Set<String>> literDom;
	HashMap<Integer,IntSet> varDom;
	
	public boolean extendPattern;
	
	public SuppResult(){
		this.pivotMatchP = new HashMap<String,IntSet>();
		this. pivotMatchGfd = new HashMap<String, HashMap<String,IntSet>>();
		//public HashMap<String, Set<String>> satCIds; // satisfied 
		this.satCId = new HashMap<String, HashMap<String,Boolean>>() ;
		this.extendPattern = false;
		this.dom = new HashSet<String>();
		this.literDom = new HashMap<Integer,Set<String>>();
		this.varDom = new HashMap<Integer,IntSet>();
		
	}
	/*
	//for worker to SC;
	public SuppResult(String pId, String cId, boolean satisfy, int parId, boolean isConnected, IntSet pivotMatch){
		this.patternId = pId;
		
		//this.conditionId = cId;
		//this.support = supp;
		this.sat = satisfy;
		this.partitionId = parId;
		this.isConnected = isConnected;
		this.pivotMatch = pivotMatch;
	}
	//for pattern;
	public SuppResult(String pId,int parId, IntSet pivotMatch){
		this.patternId = pId;
		//this.support = supp;
		this.partitionId = parId;
		this.pivotMatch = pivotMatch;
	}

	public SuppResult(String pId, IntSet a) {
		// TODO Auto-generated constructor stub
	}*/


	public SuppResult(HashMap<String, IntSet> pivotPMatch) {
		// TODO Auto-generated constructor stub
		this.pivotMatchP = pivotPMatch;
	}
	
	public SuppResult(HashMap<String, IntSet> pivotPMatch, HashSet<String> domx) {
		// TODO Auto-generated constructor stub
		this.pivotMatchP = pivotPMatch;
		this.dom = domx;
	}


	public SuppResult(HashMap<String, HashMap<String, IntSet>> pivotMatchGfd2,
			HashMap<String, HashMap<String, Boolean>> satCId2) {
		this.pivotMatchGfd = pivotMatchGfd2;
		this.satCId = satCId2;
		
		// TODO Auto-generated constructor stub
	}


	@Override
	public void assemblePartialResults(Collection<Result> partialResults) {
		// TODO Auto-generated method stub
		HashMap<String,IntSet> pMatch = new HashMap<String,IntSet>();
		
		for(Result r : partialResults){
			SuppResult pr = (SuppResult) r;
			this.extendPattern = pr.extendPattern;
			this.nodeNum = this.nodeNum + pr.nodeNum;
			//log.debug(this.extendPattern);
			if(this.extendPattern == true){
				for(Entry<String, IntSet> entry: pr.pivotMatchP.entrySet()){
					if(!this.pivotMatchP.containsKey(entry.getKey())){
						this.pivotMatchP.put(entry.getKey(), entry.getValue());
					}
					this.pivotMatchP.get(entry.getKey()).retainAll(entry.getValue());
					//if(this.dom.addAll(pr.dom));
					combineLiterDom(this.literDom,pr.literDom);
					combineVarDom(this.varDom,pr.varDom);
					
				}
			}
			else{
				for(Entry<String, HashMap<String,IntSet>> entry: pr.pivotMatchGfd.entrySet()){
					String pId = entry.getKey();
					if(!this.pivotMatchGfd.containsKey(entry.getKey())){
						this.pivotMatchGfd.put(pId, new HashMap<String,IntSet>());
					}
					if(!this.satCId.containsKey(pId)){
						this.satCId.put(pId, new HashMap<String,Boolean>());	
					}
					for(Entry<String,IntSet> entry2 :pr.pivotMatchGfd.get(pId).entrySet()){
						String cId = entry2.getKey();
					    if(!this.pivotMatchGfd.get(pId).containsKey(cId)){
					    	this.pivotMatchGfd.get(entry.getKey()).put(entry2.getKey(), entry2.getValue());
					    }
					    this.pivotMatchGfd.get(pId).get(cId).retainAll(entry2.getValue());
					    
					    if(!this.satCId.get(pId).containsKey(cId)){
							this.satCId.get(pId).put(cId, true);
						}
					    if(pr.satCId.get(pId).get(cId) == false){
							this.satCId.get(pId).put(cId, false);
					    }
					}
					
					
				}
			
			}
				
		}		
			
	}
	
	public void combineLiterDom(HashMap<Integer,Set<String>> dom1, HashMap<Integer,Set<String>> dom2){
		for(Entry<Integer,Set<String>> entry : dom2.entrySet()){
			dom1.get(entry.getKey()).addAll(entry.getValue());
		}
	}
	
	
	public void combineVarDom(HashMap<Integer,IntSet> dom1, HashMap<Integer,IntSet> dom2){
		for(Entry<Integer,IntSet> entry : dom2.entrySet()){
			dom1.get(entry.getKey()).addAll(entry.getValue());
		}
	}
	
	public static void main(String[] args){
		HashMap<String, HashMap<String,IntSet>> gfdPMatch = new HashMap<String,HashMap<String,IntSet>>();
		HashMap<String, HashMap<String,Boolean>> satCId = new HashMap<String,HashMap<String,Boolean>>();
		HashMap<String,IntSet> pivotMatchP = new HashMap<String,IntSet>();
		boolean flag = false;
		 
		HashMap<String,IntSet> pmap = new HashMap<String,IntSet>();
		for(int i=0;i<5;i++){
			IntSet b = new IntOpenHashSet();
			b.add(0);
			b.add(1);
			b.add(2);
			String s = ""+i;
			pmap.put(s, b);
		}
		
		SuppResult sp1 = new SuppResult(pmap);
		sp1.extendPattern = true;
		//SuppResult sp2 = new SuppResult(pmap);
		//sp2.extendPattern = true;
		Collection<Result> partialResults = new HashSet<Result>();
		partialResults.add(sp1);
		//partialResults.add(sp2);
		SuppResult sp = new SuppResult();
		//sp.assemblePartialResults(partialResults,pivotMatchP,gfdPMatch,satCId, flag);
		log.debug("done"+pivotMatchP.size());
		
		HashMap<String, HashMap<String,IntSet>> gfdmatch = new HashMap<String,HashMap<String,IntSet>>();
		for(int i=0;i<5;i++){
			String s = ""+i;
			gfdmatch.put(s, pmap);
		}
		
		HashMap<String,Boolean> cid = new HashMap<String,Boolean>();
		for(int i=0;i<5;i++){
			String s = ""+i;
			cid.put(s, true);
		}
		
		HashMap<String, HashMap<String,Boolean>> c = new HashMap<String,HashMap<String,Boolean>>();
		for(int i=0;i<5;i++){
			String s = ""+i;
			c.put(s, cid);
		}
		SuppResult sp3 = new SuppResult(gfdmatch,c);
		//SuppResult sp4 = new SuppResult(gfdmatch,c);
		Collection<Result> partialResults2 = new HashSet<Result>();
		partialResults2.add(sp3);
		//partialResults2.add(sp4);
		SuppResult s = new SuppResult();
		//s.assemblePartialResults(partialResults,pivotMatchP,gfdPMatch,satCId, flag);
		log.debug("done");
		
		
		
		
	}
		
	@Override
	public void writeToFile(String filename) {
	
		

	}






	
	/*
	public WorkUnitW2C(String pId, int supp, int parId, List<Int2IntMap> boderMatch){
		this.patternId = pId;
		this.support = supp;
		this.partitionId = parId;
		this.boderMatch =  boderMatch;
	}
	*/
	
	
	
	

	
}
