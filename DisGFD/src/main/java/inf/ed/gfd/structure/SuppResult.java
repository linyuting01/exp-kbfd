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
import java.util.Set;

import inf.ed.gfd.util.Params;
import inf.ed.grape.interfaces.Result;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

public class SuppResult extends Result implements Serializable {
	/**
		 * 
		 */
	private static final long serialVersionUID = 1L;

	
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
	public HashMap<String, Set<String>> satCIds; // satisfied 
	
	public boolean extendPattern;
	
	public SuppResult(){
		
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


	@Override
	public void assemblePartialResults(Collection<Result> partialResults, HashMap<String,IntSet> pivotMatch,
			HashMap<String, HashMap<String,IntSet>> gfdPMatch, HashMap<String, Set<String>> cIds, boolean flagP) {
		// TODO Auto-generated method stub
		HashMap<String,IntSet> pMatch = new HashMap<String,IntSet>();
		
		for(Result r : partialResults){
			SuppResult pr = (SuppResult) r;
			flagP = pr.extendPattern;
			if(flagP == true){
				for(Entry<String, IntSet> entry: pr.pivotMatchP.entrySet()){
					if(!pivotMatch.containsKey(entry.getKey())){
						pivotMatch.put(entry.getKey(), entry.getValue());
					}
					IntSet a = new IntOpenHashSet(pivotMatch.get(entry.getKey()));
					a.retainAll(entry.getValue());
				}
			}
			else{
				for(Entry<String, HashMap<String,IntSet>> entry: pr.pivotMatchGfd.entrySet()){
					String pId = entry.getKey();
					if(!gfdPMatch.containsKey(entry.getKey())){
						gfdPMatch.put(pId, new HashMap<String,IntSet>());
					}
					for(Entry<String,IntSet> entry2 :pr.pivotMatchGfd.get(pId).entrySet()){
						String cId = entry2.getKey();
					    if(!gfdPMatch.get(pId).containsKey(cId)){
						 gfdPMatch.get(entry.getKey()).put(entry2.getKey(), entry2.getValue());
					    }
					    gfdPMatch.get(pId).get(cId).retainAll(entry2.getValue());
					}
				}
				for(Entry<String, Set<String>> entry: pr.satCIds.entrySet()){
					if(!cIds.containsKey(entry.getKey())){
						cIds.put(entry.getKey(), entry.getValue());
					}
					Set<String> a = new HashSet<String>(cIds.get(entry.getKey()));
					a.retainAll(entry.getValue());
				}
			}
				
		}		
			
	}
		
		
	@Override
	public void writeToFile(String filename) {
	
		

	}


	@Override
	public void assemblePartialResults(Collection<Result> partialResults) {
		// TODO Auto-generated method stub
		
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
