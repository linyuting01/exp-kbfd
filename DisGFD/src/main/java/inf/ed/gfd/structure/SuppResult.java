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
import inf.ed.gfd.util.Fuc;
import inf.ed.gfd.util.Params;
import inf.ed.grape.interfaces.Result;
import it.unimi.dsi.fastutil.ints.Int2BooleanMap;
import it.unimi.dsi.fastutil.ints.Int2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
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
	
	public Int2ObjectMap<IntSet> pivotMatchP;
	public Int2ObjectMap<Int2ObjectMap<IntSet>> pivotMatchGfd;
	public Int2ObjectMap<Int2ObjectMap<IntSet>> satCId;
	//public Int2ObjectMap<Int2ObjectMap<IntSet>> varDom ;
	//public Int2ObjectMap<Int2ObjectMap<Set<String>>> literDom;
	
	
	public Int2IntMap patternMatchesNum;
	
	public Int2ObjectMap<IntSet> isoResult;
	public Set<DFS> edgeCands;
	
	//public HashMap<String, Set<String>> satCIds; // satisfied 

	public int nodeNum = 0;
	
	public boolean extendPattern = false ; 
	public boolean isFirst = false;
	public boolean isIsoCheck = false;;
	public boolean checkGfd = false; 
	public boolean matchsize  = false;
	//public boolean istest = false;
	
	
	

	//early termination
	
	public IntSet freqPattern;
	public Int2ObjectMap<IntSet>  freqGfd ;
	public Int2ObjectMap<IntSet> unSatGfds;
	//basic idea : two filter process; one in worker, one in SC;
	
	

	public SuppResult(){
		    this.edgeCands = new HashSet<DFS>();
			this.pivotMatchP = new Int2ObjectOpenHashMap<IntSet>();
			this.pivotMatchGfd = new Int2ObjectOpenHashMap<Int2ObjectMap<IntSet>>() ;
			this.satCId = new Int2ObjectOpenHashMap<Int2ObjectMap<IntSet>>();
			this.isoResult = new Int2ObjectOpenHashMap<IntSet>();
			this.patternMatchesNum = new Int2IntOpenHashMap();
			
			this.freqGfd = new Int2ObjectOpenHashMap<IntSet>();;
			this.freqPattern = new  IntOpenHashSet();
			this.unSatGfds =  new Int2ObjectOpenHashMap<IntSet>();
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

    public SuppResult(Int2ObjectMap<IntSet> isoResult, boolean isIsoCheck){
    	this.isoResult = isoResult;
    	this.isIsoCheck = true;
    }
	public SuppResult(Int2ObjectMap<IntSet> pivotPMatch) {
		// TODO Auto-generated constructor stub
		this.pivotMatchP = pivotPMatch;
	}
	
	public String toString(){
		
		String s1 = " boolean extendPattern =" + extendPattern+"\n";
		s1+= " boolean checkGfd =" + checkGfd+"\n";

		 s1+=   " boolean isIsoCheck =" + isIsoCheck+"\n";
		 s1+=   " boolean isFirst =" + isFirst+"\n";
		 s1+=   " boolean isFirst =" + isFirst+"\n";
	
		 s1+= " boolean matchsize =" + matchsize+"\n";
		 if(extendPattern){
			 
				for(Entry<Integer, IntSet> entry:pivotMatchP.entrySet()){
					s1+= "pId= "+ entry.getKey() + "\t" + "matches size" +entry.getValue().size() +"\n";
					
				}
				
		 }
		 if(checkGfd){
			 for(Entry<Integer, Int2ObjectMap<IntSet>> entry: pivotMatchGfd.entrySet()){
					int pId = entry.getKey();
					for(Entry<Integer,IntSet> entry2 : pivotMatchGfd.get(pId).entrySet()){
						int cId = entry2.getKey();
						s1+= "pId= "+ pId + "\t" + "cId = "+  cId +"matches size" +entry2.getValue().size() +
								"sat info" + satCId.get(pId).get(cId)+"\n";
					}
			 }
			 
		 }
		 if(isIsoCheck){
			 for(Entry<Integer,IntSet> entry : isoResult.entrySet()){
				 s1+= "pattern pId = " +entry.getKey() + "isomorphism to " + entry.getValue().toString();
			 }
		 }
		 
		return s1;
		
	}
	



	public SuppResult(Int2ObjectMap<Int2ObjectMap<IntSet>>pivotMatchGfd2,
			Int2ObjectMap<Int2ObjectMap<IntSet>>  satCId2) {
		this.pivotMatchGfd = pivotMatchGfd2;
		this.satCId = satCId2;
		
		// TODO Auto-generated constructor stub
	}
	
	public SuppResult(Set<DFS> edgeCands)
	{
		this.edgeCands = edgeCands;
	}


	//@Override
	public void assemblePartialResults(Collection<Result> partialResults){
		log.debug("Params run_mode = " + Params.RUN_MODE);
		if(Params.RUN_MODE == 1){
			
			assemblePartialResults1(partialResults);
		}
		else{
			assemblePartialResults2(partialResults);
		}
	}
	public void assemblePartialResults1(Collection<Result> partialResults) {
		
		
		HashMap<String,IntSet> pMatch = new HashMap<String,IntSet>();
		log.debug("partial result size " + partialResults.size());
		for(Result r : partialResults){
			SuppResult pr = (SuppResult) r;
			this.extendPattern = pr.extendPattern;
			this.checkGfd = pr.checkGfd;
			this.isIsoCheck = pr.isIsoCheck;
			this.nodeNum = this.nodeNum + pr.nodeNum;
			this.isFirst = pr.isFirst;
			this.matchsize = pr.matchsize;
			//this.istest = pr.istest;
		
			//log.debug("suppresult node num" + this.nodeNum);
			//log.debug(this.extendPattern);
			if(matchsize){
				for(Entry<Integer,Integer> entry : pr.patternMatchesNum.entrySet()){
					if(!patternMatchesNum.containsKey(entry.getKey())){
						patternMatchesNum.put(entry.getKey(), entry.getValue());
					}
					else{
						int key = entry.getKey();
						int i = patternMatchesNum.get(entry.getKey());
						int num = i + entry.getValue();
						patternMatchesNum.put(key, num);
					}
				}
			}
			
		
			if(this.isIsoCheck == true){
				if(!pr.isoResult.isEmpty()){
					this.isoResult.putAll(pr.isoResult);
				}
			}
			
			if(this.extendPattern == true){
				for(Entry<Integer, IntSet> entry: pr.pivotMatchP.entrySet()){
					if(!this.pivotMatchP.containsKey(entry.getKey())){
						this.pivotMatchP.put(entry.getKey(), new IntOpenHashSet());
					}
					this.pivotMatchP.get(entry.getKey()).addAll(entry.getValue());	
				}
			}
				
			if(this.checkGfd == true){
				for(Entry<Integer, Int2ObjectMap<IntSet>> entry: pr.pivotMatchGfd.entrySet()){
					int pId = entry.getKey();
					if(!this.pivotMatchGfd.containsKey(entry.getKey())){
						this.pivotMatchGfd.put(pId, new Int2ObjectOpenHashMap<IntSet>());
					}
					if(!this.satCId.containsKey(pId)){
						this.satCId.put(pId, new Int2ObjectOpenHashMap<IntSet>());
					}
					for(Entry<Integer,IntSet> entry2 :pr.pivotMatchGfd.get(pId).entrySet()){
						int cId = entry2.getKey();
					    if(!this.pivotMatchGfd.get(pId).containsKey(cId)){
					    	this.pivotMatchGfd.get(pId).put(cId, new IntOpenHashSet());
					    }
					    this.pivotMatchGfd.get(pId).get(cId).addAll(entry2.getValue());
					}
					for(Entry<Integer,IntSet> entry2 :pr.satCId.get(pId).entrySet()){
						int cId = entry2.getKey();
						 
					    if(!this.satCId.get(pId).containsKey(cId)){
							this.satCId.get(pId).put(cId,  new IntOpenHashSet());
						}
					    this.satCId.get(pId).get(cId).addAll(entry2.getValue());
					    
					}
					 
					  
					    
					   
					}
					
					
				}
		}
			
	}
	
			
	
	
	public void combineLiterDom(Int2ObjectMap<Set<String>> dom1, 
			Int2ObjectMap<Set<String>> dom2){
		for(Entry<Integer,Set<String>> entry : dom2.entrySet()){
			if(!dom1.containsKey(entry.getKey())){
				dom1.put(entry.getKey(), entry.getValue());
			}
			else{
				dom1.get(entry.getKey()).addAll(entry.getValue());
			}
		}
	}
	
	
	public void combineVarDom(Int2ObjectMap<IntSet> dom1, Int2ObjectMap<IntSet> dom2){
		for(Entry<Integer,IntSet> entry : dom2.entrySet()){
			if(!dom1.containsKey(entry.getKey())){
				dom1.put(entry.getKey(), entry.getValue());
			}
			else{
				dom1.get(entry.getKey()).addAll(entry.getValue());
			}
		}
	}
	


	public void assemblePartialResults2(Collection<Result> partialResults){
		//SuppResult pr = Fuc.getRandomSuppResult(partialResults);
		
	
		
		
		SuppResult pr = Fuc.getRandomSuppResult(partialResults);
		
		this.extendPattern = pr.extendPattern;
		this.checkGfd = pr.checkGfd;
		this.isIsoCheck = pr.isIsoCheck;
		this.nodeNum = this.nodeNum + pr.nodeNum;
		this.isFirst = pr.isFirst;
		this.matchsize = pr.matchsize;
	
		HashMap<String,IntSet> pMatch = new HashMap<String,IntSet>();
		log.debug("partial result size " + partialResults.size());
		if(this.checkGfd || this.extendPattern){
			for(Result r : partialResults){
				
				SuppResult spr = (SuppResult) r;
					if(this.extendPattern){
						if(spr.freqPattern != null){
							if(!spr.freqPattern.isEmpty()){
								this.freqPattern.addAll(spr.freqPattern);
							}
						}
					}
				if(this.checkGfd ){
					this.freqGfd.putAll(spr.freqGfd);
					this.unSatGfds.putAll(spr.unSatGfds);
				}
			}
		}
		for(Result r : partialResults){
			SuppResult spr = (SuppResult) r;

			if(matchsize){
				for(Entry<Integer,Integer> entry : pr.patternMatchesNum.entrySet()){
					if(!patternMatchesNum.containsKey(entry.getKey())){
						patternMatchesNum.put(entry.getKey(), entry.getValue());
					}
					else{
						int key = entry.getKey();
						int i = patternMatchesNum.get(entry.getKey());
						int num = i + entry.getValue();
						patternMatchesNum.put(key, num);
					}
				}
			}
			
		
			if(this.isIsoCheck == true){
				if(!pr.isoResult.isEmpty()){
					this.isoResult.putAll(pr.isoResult);
				}
				
			}
		
			if(this.extendPattern == true){
				//first filter,
				for(Entry<Integer, IntSet> entry: pr.pivotMatchP.entrySet()){
					int pId = entry.getKey();
					if(!freqPattern.contains(pId)){
						if(!this.pivotMatchP.containsKey(pId)){
							this.pivotMatchP.put(pId,entry.getValue());
						}
						else{
							this.pivotMatchP.get(pId).addAll(entry.getValue());
							int size1 = this.pivotMatchP.get(pId).size();
							if(size1 >= Params.VAR_SUPP){
								freqPattern.add(pId);
							}
						}
					}
				}
			}
			if(this.checkGfd){
				
				for(Entry<Integer, Int2ObjectMap<IntSet>> entry: pr.pivotMatchGfd.entrySet()){
					int pId = entry.getKey();
					for(Entry<Integer,IntSet> entry2 :pr.pivotMatchGfd.get(pId).entrySet()){
						int cId = entry2.getKey();
						if(freqGfd.isEmpty() ||!freqGfd.containsKey(pId)){
								if(!this.pivotMatchGfd.containsKey(entry.getKey())){
									this.pivotMatchGfd.put(pId,new Int2ObjectOpenHashMap<IntSet>());
								}
								 if(!this.pivotMatchGfd.get(pId).containsKey(cId)){
								    	this.pivotMatchGfd.get(pId).put(cId,entry2.getValue());    
								 }
								 else{
									 this.pivotMatchGfd.get(pId).get(cId).addAll(entry2.getValue());
									 int size2 = this.pivotMatchGfd.get(pId).get(cId).size();
									 if(size2 >= Params.VAR_SUPP){
										 if(!freqGfd.containsKey(pId)){
											 freqGfd.put(pId, new IntOpenHashSet());
										 }
										 freqGfd.get(pId).add(cId);
										 
									 }
								 }
						}
						if(freqGfd.containsKey(pId)){
							if(!freqGfd.get(pId).contains(cId)){
								if(!this.pivotMatchGfd.containsKey(entry.getKey())){
									this.pivotMatchGfd.put(pId,new Int2ObjectOpenHashMap<IntSet>());
								}
								 if(!this.pivotMatchGfd.get(pId).containsKey(cId)){
								    	this.pivotMatchGfd.get(pId).put(cId,entry2.getValue());    
								 }
								 else{
									 this.pivotMatchGfd.get(pId).get(cId).addAll(entry2.getValue());
									 int size2 = this.pivotMatchGfd.get(pId).get(cId).size();
									 if(size2 >= Params.VAR_SUPP){
										 freqGfd.get(pId).add(cId);	 
									 }
								 }
							}
						}
					}
				}
			}
				for(Entry<Integer, Int2ObjectMap<IntSet>> entry: pr.satCId.entrySet()){
					int pId = entry.getKey();
					for(Entry<Integer,IntSet> entry2 :pr.satCId.get(pId).entrySet()){
						int cId = entry2.getKey();
						if(unSatGfds.isEmpty() ||!unSatGfds.containsKey(pId)){
								if(!this.satCId.containsKey(entry.getKey())){
									this.satCId.put(pId,new Int2ObjectOpenHashMap<IntSet>());
								}
								 if(!this.satCId.get(pId).containsKey(cId)){
								    	this.satCId.get(pId).put(cId,entry2.getValue());    
								 }
								 else{
									 this.satCId.get(pId).get(cId).addAll(entry2.getValue());
									 int size2 = this.satCId.get(pId).get(cId).size();
									 if(size2 >= Params.VAR_UNSAT){
										 if(!unSatGfds.containsKey(pId)){
											 unSatGfds.put(pId, new IntOpenHashSet());
										 }
										 unSatGfds.get(pId).add(cId);
										 
									 }
								 }
						}
						if(unSatGfds.containsKey(pId)){
							if(!unSatGfds.get(pId).contains(cId)){
								if(!this.satCId.containsKey(entry.getKey())){
									this.satCId.put(pId,new Int2ObjectOpenHashMap<IntSet>());
								}
								 if(!this.satCId.get(pId).containsKey(cId)){
								    	this.satCId.get(pId).put(cId,entry2.getValue());    
								 }
								 else{
									 this.satCId.get(pId).get(cId).addAll(entry2.getValue());
									 int size2 = this.satCId.get(pId).get(cId).size();
									 if(size2 >= Params.VAR_UNSAT){
										 unSatGfds.get(pId).add(cId);	 
									 }
								 }
							}
						}
					}
				}
				
			}
		Int2ObjectMap<IntSet> a = new Int2ObjectOpenHashMap<IntSet>(this.isoResult);
		this.isoResult = Fuc.getIsoResult(a);
		}
		
	@Override
	public void writeToFile(String filename) {
	
		

	}







	
	

	
}
