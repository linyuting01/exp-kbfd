package inf.ed.gfd.structure;


import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import inf.ed.gfd.util.Params;
import inf.ed.grape.interfaces.Result;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.IntSet;

public class SuppResult extends Result implements Serializable {
	/**
		 * 
		 */
	private static final long serialVersionUID = 1L;

	
	public String patternId; 
	public String conditionId;
	//public int support;
	public boolean sat;
	public int partitionId;
	public boolean isConnected;
	public IntSet patterIds; 
	public IntSet pivotMatch;
	public boolean extendPattern;
	//public List<Int2IntMap> boderMatch;
	
	public SuppResult(){
		
	}
	
	//for worker to SC;
	public SuppResult(String pId, String cId, boolean satisfy, int parId, boolean isConnected, IntSet pivotMatch){
		this.patternId = pId;
		this.conditionId = cId;
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

	@Override
	public void assemblePartialResults(Collection<Result> partialResults) {
		// TODO Auto-generated method stub
		HashMap<String,IntSet> pMatch = new HashMap<String,IntSet>();
		HashMap<String,Boolean> pSat = new HashMap<String,Boolean>();
		HashMap<String,Double> pSupp = new HashMap<String,Double>();
		
		for(Result r : partialResults){
			SuppResult pr = (SuppResult) r;
			String pId = pr.patternId;
			if(!pMatch.containsKey(pId)){
				pMatch.put(pId, pr.pivotMatch);
			 }
			 else{
				 pMatch.get(pId).addAll(pr.pivotMatch);
			 }
			if(!pSat.containsKey(pId)){
				pSat.put(pId, pr.sat);
			 }
			 else{
				 boolean flag = pSat.get(pId) && pr.sat;
				 pSat.put(pId, flag);	 
			 }	 	
		}
		for(Entry<String, IntSet> entry : pMatch.entrySet() ){
			pSupp.put(entry.getKey(), (double) entry.getValue().size()/Params.GRAPHNODENUM);
		}
		
		
	}
	public void generateWorkUnits(String cId, GfdTree stree, HashMap<String,Double> pSupp, HashMap<String,Boolean> pSat, boolean flag){
		
		for(String s :pSupp.keySet()){
			if(pSupp.get(s) > Params.VAR_SUPP &&  pSat.get(s)){
				//extenf node;
				if(flag == true){ //extend pattern
					GfdNode g = stree.pattern_Map.get(s);
					//extendNode(t, edgePattern,attr_Map);
				}
				else{
					GfdNode g = stree.pattern_Map.get(s);
					LiterNode t = g.ltree.condition_Map.get(cId);
					//extendNode(t)
				}
			}
		}
		
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
