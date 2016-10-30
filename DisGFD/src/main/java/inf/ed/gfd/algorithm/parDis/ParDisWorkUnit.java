package inf.ed.gfd.algorithm.parDis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import inf.ed.gfd.algorithm.sequential.EdgePattern;
import inf.ed.gfd.structure.Condition;
import inf.ed.gfd.structure.DFS;
import inf.ed.gfd.structure.DisWorkUnit;
import inf.ed.gfd.structure.DisconnectedTree;
import inf.ed.gfd.structure.GfdMsg;
import inf.ed.gfd.structure.GfdNode;
import inf.ed.gfd.structure.Partition;
import inf.ed.gfd.structure.SuppResult;
import inf.ed.gfd.structure.WorkUnit;
import inf.ed.gfd.util.Fuc;
import inf.ed.gfd.util.KV;
import inf.ed.gfd.util.Params;
import inf.ed.grape.interfaces.LocalComputeTask;
import inf.ed.grape.interfaces.Message;
import inf.ed.graph.structure.Graph;
import inf.ed.graph.structure.OrthogonalEdge;
import inf.ed.graph.structure.adaptor.Pair;
import inf.ed.graph.structure.adaptor.TypedEdge;
import inf.ed.graph.structure.adaptor.VertexOString;
import inf.ed.graph.structure.adaptor.VertexString;
import inf.ed.isomorphism.VF2IsomorphismInspector;
import it.unimi.dsi.fastutil.ints.Int2BooleanMap;
import it.unimi.dsi.fastutil.ints.Int2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

public class ParDisWorkUnit extends LocalComputeTask {
	
	//private Set<WorkUnit> workload;
	//private Map<String, WorkUnit> assembledWorkload;
	public GfdMsg gfdMsg = new GfdMsg();
	private int partitionId;
	

	
	static Logger log = LogManager.getLogger(ParDisWorkUnit.class);
	public Int2ObjectMap<IntSet> pivotPMatch1  = new Int2ObjectOpenHashMap<IntSet>();
	public Int2ObjectMap<List<Int2IntMap> > patternNodeMatchesN =  new Int2ObjectOpenHashMap<List<Int2IntMap>>();
	public Int2ObjectMap<List<Int2IntMap> > patternNodeMatchesP =  new Int2ObjectOpenHashMap<List<Int2IntMap>>();
	
	Set<WorkUnit> workload = new HashSet<WorkUnit>();
	HashMap<String,Boolean> transFlag = new HashMap<String,Boolean>();
	HashMap<Integer,HashMap<Integer,List<Pair<Integer,Integer>>>> transferMatch =
			new HashMap<Integer,HashMap<Integer,List<Pair<Integer,Integer>>>>();
	
	public Int2ObjectMap<List<Pair<Integer,Integer>>> edgePatternNodeMatch1 = new Int2ObjectOpenHashMap<List<Pair<Integer,Integer>>>();
	//SuppResult suppResult = new SuppResult();
	public Int2ObjectMap<Int2ObjectMap<IntSet>> pivotMatchGfd1 = new Int2ObjectOpenHashMap<Int2ObjectMap<IntSet>>() ;
	public Int2ObjectMap<Int2BooleanMap> satCId1 = new Int2ObjectOpenHashMap<Int2BooleanMap>();
	public Int2ObjectMap<Int2ObjectMap<IntSet>> varDom1 = new Int2ObjectOpenHashMap<Int2ObjectMap<IntSet>>();
	public Int2ObjectMap<Int2ObjectMap<Set<String>>> literDom1 = new Int2ObjectOpenHashMap<Int2ObjectMap<Set<String>>>();
	
	
	
	public Map<DFS,Integer> dfs2Id = new HashMap<DFS,Integer>();
	public Int2ObjectMap<DFS> id2Dfs = new Int2ObjectOpenHashMap<DFS>();
	
	Set<IntSet> isoCheckResult = new HashSet<IntSet>();
	

	public boolean isGfdCheck;
	
	Set<String> dom = new HashSet<String>();
	
	public ParDisWorkUnit() {
		// TODO Auto-generated constructor stub
	}
	
	
	
	public void setWorkUnit(Set<WorkUnit> workload) {
		this.workload = workload;
	}
	

	public int processWorkUnitAndGfdMsg(Partition partition){
		pivotPMatch1.clear();
		int i = 0;
		for( WorkUnit w: workload){
				if(w.isGfdCheck){
					
					checkGfd(w,partition);
					//prepareResult(true);
					isoCheckResult = getIsoResult();
					i = 0;
				}
				
					if(w.isIsoCheck){
						checkIso(w);
						
						i =1;
					}
					if(w.isPatternCheck){
						
						gfdMsgProcess(w,partition);
						i=2;
			
				}		
			}
		
		if(i==2){
		//prepareResult(false);
			sendTransferData(partition.getPartitionID());
			}
		return i;
	
	}
	
	Int2ObjectMap<IntSet> isoResult = new Int2ObjectOpenHashMap<IntSet>();
	private void checkIso(WorkUnit w) {
		// TODO Auto-generated method stub
		
		int size  = w.isoPatterns.size();
		isoResult.clear();
		
		
		for(Entry<Integer,Graph<VertexString, TypedEdge>> entry1 : w.isoPatterns.entrySet()){
			int attr1 = entry1.getValue().allVertices().get(0).getAttr();
			for(Entry<Integer,Graph<VertexString, TypedEdge>> entry2 : w.isoPatterns.entrySet()){
				
				if(entry1.getKey()!= entry2.getKey() && entry1.getKey()<entry2.getKey()){
					if(!isoResult.containsKey(entry1.getKey())){
						isoResult.put(entry1.getKey(), new IntOpenHashSet());
					}
					if(!isoResult.get(entry1.getKey()).contains(entry2.getKey())){
						for(Entry<Integer, VertexString> entryattr: entry2.getValue().allVertices().entrySet()){
							int attr2 = entryattr.getValue().getAttr();
							if(attr1 == attr2){
								VF2IsomorphismInspector isomorphismChecker = new VF2IsomorphismInspector();
								boolean flag =isomorphismChecker.isSubgraphIsomorphic(entry1.getValue(), 0, 
										entry2.getValue(), entry2.getKey());
								if(flag){
									isoResult.get(entry1.getKey()).add(entry2.getKey());
									//update isoResult
									updateIsoResult(entry1.getKey(),entry2.getKey());
									break;
								}
							}
						}
						
						
						
						
					}
				}
			}
		}
		
		
	}
	
	private void updateIsoResult(int i,int j) {
		// TODO Auto-generated method stub
		IntSet a = isoResult.get(i);
		a.add(i);
		for(int v: a){
			if(v<j){
				if(!isoResult.containsKey(v)){
					isoResult.put(v, new IntOpenHashSet());
				}
				isoResult.get(v).add(j);
			}
			if(v>j){
				if(!isoResult.containsKey(j)){
					isoResult.put(j, new IntOpenHashSet());
				}
				isoResult.get(j).add(v);
			}
		}
	}
	
	private Set<IntSet>  getIsoResult(){
		Int2ObjectMap<IntSet> resultx = new Int2ObjectOpenHashMap<IntSet>(isoResult);
		Set<IntSet> result = new HashSet<IntSet>();
		for(Entry<Integer,IntSet> entry: isoResult.entrySet()){
			for(int a :entry.getValue()){
				resultx.remove(a);
			}
		}
		for(IntSet a :resultx.values()){
			result.add(a);
		}
		return result;
	}







	public void gfdMsgProcess(WorkUnit w, Partition partition){
		gfdMsg.clear();
		for(Pair<Integer, Pair<Integer,Integer>> edges :w.edgeIds.values()){
			   int id = edges.x;
				if(!transFlag.containsKey(id)){
					for(Pair<Integer,Integer> p : edgePatternNodeMatch1.get(id)){
						 String val1 = partition.getGraph().allVertices().get(p.x).getValue();
						 String val2 = partition.getGraph().allVertices().get(p.y).getValue();
						 VertexOString f1 = new VertexOString(p.x,val1);
						 VertexOString t1 = new VertexOString(p.y,val2);
						 
				         if(!gfdMsg.transferingEdgeMatch.containsKey(id)){
				        	 gfdMsg.transferingEdgeMatch.put(id, new ArrayList<Pair<VertexOString,VertexOString>>());
				         }
				         gfdMsg.transferingEdgeMatch.get(id).add(new Pair<VertexOString,VertexOString>(f1,t1));
					     gfdMsg.partitionId = partitionId; 
					}
				}
			}
			
		}
		
	
	

	private void checkGfd(WorkUnit w,Partition partition) {
		// TODO Auto-generated method stub
		int pId = w.patternId;
		Int2ObjectMap<Condition> conditions = w.conditions;
		
		for(Entry<Integer, Condition> entry : conditions.entrySet()){
			int cId = entry.getKey();
			Condition c = entry.getValue();
	
			if(!satCId1.containsKey(pId)){
				satCId1.put(pId, new Int2BooleanOpenHashMap());	
			}
			if(!satCId1.get(pId).containsKey(cId)){
				satCId1.get(pId).put(cId, true);
			}
			for(Int2IntMap match : patternNodeMatchesP.get(pId)){
				boolean flag = c.verify(match, partition.getGraph());
				if(flag){
					if(!pivotMatchGfd1.containsKey(pId)){
						pivotMatchGfd1.put(pId, new Int2ObjectOpenHashMap());
						
					}
					if(!pivotMatchGfd1.get(pId).containsKey(cId)){
						pivotMatchGfd1.get(pId).put(cId, new IntOpenHashSet());
					}
					pivotMatchGfd1.get(pId).get(cId).add(match.get(1));
				}
				else{
					satCId1.get(pId).put(cId, false);
				}
				
			}
		}
	}

	

	
	
		
			

	
	
	private void sendTransferData(int partitionId) {
			for (int targetPartitionID = 1; targetPartitionID <= Params.N_PROCESSORS &&  
					targetPartitionID!= partitionId; targetPartitionID++) {
		        
				Message<GfdMsg> nMsg = new Message<GfdMsg>(partitionId,
						targetPartitionID, gfdMsg);
				this.generatedMessages.add(nMsg);
			}
	}

	private void receiveTransferedData(Partition partition,  List<Message<?>> incomingMessages) {

		if (incomingMessages != null) {
			for (Message<?> recvMsg : incomingMessages) {

				//log.debug(recvMsg.toString());
				processMsg(recvMsg,partition);
			}
		}

	}
	
	/**
	 * add node to KB and store the pattern match
	 * @param recvMsg
	 * @param partition
	 */
	public void processMsg(Message<?> recvMsg,Partition partition){
		GfdMsg recvContent = (GfdMsg) recvMsg.getContent();
		for(Entry<Integer, List<Pair<VertexOString,VertexOString>>> entry: 
			recvContent.transferingEdgeMatch.entrySet()){
			for(Pair<VertexOString,VertexOString> pair : entry.getValue()){
				if(!partition.getGraph().contains(pair.x.getID())){
					partition.getGraph().addVertex(pair.x);
				}
				if(!partition.getGraph().contains(pair.y.getID())){
					partition.getGraph().addVertex(pair.y);
				}
				if(!transferMatch.containsKey(entry.getKey())){
					transferMatch.put(recvContent.partitionId, 
							new HashMap<Integer,List<Pair<Integer,Integer>>>());
				}
				if(!transferMatch.get(recvContent.partitionId).containsKey(entry.getKey())){
					transferMatch.get(recvContent.partitionId).put(entry.getKey(), 
							new ArrayList<Pair<Integer,Integer>>());
				}
				transferMatch.get(recvContent.partitionId).get(
						entry.getKey()).add(new Pair<Integer,Integer>(pair.x.getID(), pair.y.getID()));
				
			}
		}
	}


	@Override
	public void prepareResult(int flag) {
		// TODO Auto-generated method stub
		if(flag == 0){
			 SuppResult w = (SuppResult)this.generatedResult;
			    w.pivotMatchP =	pivotPMatch1;
			    w.extendPattern = true;
			    w.nodeNum = Params.GRAPHNODENUM;
		}
		if(flag == 1){
			SuppResult w = (SuppResult)this.generatedResult;
			w.isoResult = isoCheckResult;
			w.isIsoCheck = true;
		}
		if(flag ==2){
			SuppResult w = (SuppResult)this.generatedResult;
			w.checkGfd = true;
			w.pivotMatchGfd = pivotMatchGfd1;
			w.satCId = satCId1;
			 w.nodeNum = Params.GRAPHNODENUM;
			
		}
		
		//send to SC
		
	
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ParDisWorkUnit localCompute = new ParDisWorkUnit();
		Partition partition = new Partition(0);
		String filepath = "data/dbpedia_sample"+ ".p" + partition.getPartitionID();
		partition.loadPartitionDataFromEVFile(filepath);
		localCompute.compute(partition);
		
	
		
			
	}

	@Override
	public void compute(Partition partition) {
		
		// TODO Auto-generated method stub
		
		log.debug("begin local compute current super step = " + this.getSuperstep());
		//log.debug(partition.getPartitionID());
		
			//generate edge patterns.
			EdgePattern eP = new EdgePattern();
			
			Set<DFS> edgePattern = eP.edges(partition.getGraph());
					//eP.edgePattern(partition.getGraph(), pivotPMatch,
					//edgePatternNodeMatch,literDom,varDom );
			log.debug(edgePattern.size());
		    SuppResult w = (SuppResult)this.generatedResult;
		    w.edgeCands = edgePattern;
		    w.isFirst = true;
	}
	@Override
	public void compute2(Partition partition){
		EdgePattern eP = new EdgePattern();
		for(WorkUnit w: workload){
			log.debug(w.id2Dfs.size());
			id2Dfs = w.id2Dfs;
			for(Entry<Integer,DFS> entry : id2Dfs.entrySet()){
				dfs2Id.put(entry.getValue(), entry.getKey());
			}
		}
		eP.edgePattern(partition.getGraph(), pivotPMatch1,edgePatternNodeMatch1,patternNodeMatchesN,literDom1,varDom1,dfs2Id);
		 SuppResult w = (SuppResult)this.generatedResult;
		 w.extendPattern = true;
		 w.pivotMatchP = pivotPMatch1;
		 w.literDom = literDom1;
		 w.varDom = varDom1;
	}




	@Override
	public int incrementalCompute(Partition partition){
		log.info("now incremental compute to verify  ");
		pivotMatchGfd1.clear();
		satCId1.clear();
		pivotPMatch1.clear();
		literDom1.clear();
		varDom1.clear();
		int flag = processWorkUnitAndGfdMsg(partition);
		if(flag == 0){
			prepareResult(0);
		}
		if(flag == 1){
			prepareResult(1);
		}
		return flag;	
	}
	public void incrementalCompute(Partition partition, List<Message<?>> incomingMessages) {
		// TODO Auto-generated method stub

	   // log.info("now incremental compute ");
	   
	     receiveTransferedData(partition,  incomingMessages);
		 IncrePattern(partition,edgePatternNodeMatch1);
		 for(HashMap<Integer,List<Pair<Integer,Integer>>> edgeMatch: transferMatch.values()){
			IncrePattern(partition,edgePatternNodeMatch1);
		 }
		 prepareResult(2);
	    }

		
	

	
private  void IncrePattern(Partition partition, Int2ObjectMap<List<Pair<Integer, Integer>>> edgePatternNodeMatch12){
		//pivotPMatch.clear();
		//patternNodeMatchesP.clear();;
	    //patternNodeMatchesP = (HashMap<String, List<Int2IntMap>>)patternNodeMatchesN.clone();
	   // patternNodeMatchesN. clear();
	    for(WorkUnit w: workload){
		    	IncPattern( w, partition,edgePatternNodeMatch12);
		    }
			   

}
private void IncPattern(WorkUnit w, Partition partition, Int2ObjectMap<List<Pair<Integer, Integer>>> edgePatternNodeMatch12){
	
	int ppId = w.oriPatternId;
	List<Int2IntMap> pmatches = patternNodeMatchesP.get(ppId);
	
	//for each match of previous pattern ppId
	for(Int2IntMap match : pmatches){
		//for each edge wait to added into ppId
	    for(Entry<Integer,Pair<Integer,Pair<Integer,Integer>>> entry : w.edgeIds.entrySet()){
	    	int pId = entry.getKey();
	    	
	    		int edgeId = entry.getValue().x;
	    		Pair<Integer,Integer> pair = entry.getValue().y;
	    		IncPatterMatchEdge(match,ppId,pId,edgeId, pair,partition,edgePatternNodeMatch12);
	    	}
	    }
		
	}


	private void IncPatterMatchEdge(Int2IntMap match, int opId, int pId, int edgeId, 
			Pair<Integer,Integer> pair, Partition partition,Int2ObjectMap<List<Pair<Integer, Integer>>> edgePatternNodeMatch12){
//for each edge wait to added into ppId
					
					List<Pair<Integer,Integer>> pairL = edgePatternNodeMatch12.get(edgeId);
					
				
					//edge (fId,tId,eLabel)
					int fId = pair.x;
					int tId = pair.y;
					int eLabel = id2Dfs.get(edgeId).eLabel;
					
					if(match.containsKey(fId)){//add the node AB A is in ppId
						if(match.containsKey(tId)){// add AB AB is in ppId
							Pair<Integer,Integer> p = new Pair<Integer,Integer>(match.get(fId),match.get(tId));
							if(pairL.contains(p)){
								if(matchKB(match,pId,p.x,p.y,eLabel,partition)){
									addMatch(match, pId, fId, tId, 0,0 );
								}
							}
						}
						else{
							for(Pair<Integer,Integer> p: pairL){
								if(p.x == match.get(fId)){
									if(matchKB(match,pId,p.x,p.y,eLabel,partition)){
										addMatch(match, pId, fId, tId, 1,(int)p.y);
									}
									
									
								}
							}
						}
					}
					if(match.containsKey(tId)){
						for(Pair<Integer,Integer> p: pairL){
							if(p.y == match.get(fId)){
								if(matchKB(match,pId, p.x,p.y,eLabel,partition)){
									addMatch(match, pId, fId, tId, 2,(int)p.x);
								}
							}
						}
					}
	
	}




	
private void addMatch(Int2IntMap match, int pId, int fId, int tId, int flag, int x){
		
		Int2IntMap tmpt = new Int2IntOpenHashMap(match);
		if(flag == 1){
			tmpt.put(tId,x);
		}
		if(flag==2)
		{
		    tmpt.put(fId, x);
		}
		if(!patternNodeMatchesN.containsKey(pId)){
			patternNodeMatchesN.put(pId, new ArrayList<Int2IntMap>());
		}
		patternNodeMatchesN.get(pId).add(tmpt);
		if(!pivotPMatch1.containsKey(pId)){
			pivotPMatch1.put(pId, new IntOpenHashSet());
		}
		pivotPMatch1.get(pId).add(tmpt.get(1));
		
	}
	
	private boolean matchKB(Int2IntMap match,int pId, int fId, int tId, int elabel, Partition partition){
		OrthogonalEdge e = partition.getGraph().getEdge(fId, tId);
		
		 String val1 =partition.getGraph().allVertices().get(fId).getValue();
		 String val2 =partition.getGraph().allVertices().get(tId).getValue();
		int[] attrs= e.getAttr();
		String[] val = new String[match.size()];
		for(int m: attrs){
			if(m == elabel) {
				int ksize = match.size();
				for(int i: match.keySet()){
					 if(!literDom1.containsKey(pId)){
			        	  literDom1.put(pId, new Int2ObjectOpenHashMap<Set<String>>());
			           }
			           if(!literDom1.get(pId).containsKey(i)){
			        	   literDom1.get(pId).put(i, new HashSet<String>());
			           }
			           String val3 =partition.getGraph().allVertices().get(i).getValue();
			           literDom1.get(pId).get(i).add(val3);  
			           val[i] = val3;
				}
				 if(!literDom1.get(pId).containsKey(fId)){
		        	   literDom1.get(pId).put(fId, new HashSet<String>());
		           }
		          // String val1 =partition.getGraph().allVertices().get(fId).getValue();
		           literDom1.get(pId).get(fId).add(val1); 
		           if(!literDom1.get(pId).containsKey(tId)){
		        	   literDom1.get(pId).put(tId, new HashSet<String>());
		           }
		           //String val2 =partition.getGraph().allVertices().get(tId).getValue();
		           literDom1.get(pId).get(tId).add(val2);
			}
			for(int i=0;i<val.length;i++){
				for(int j = i+1;j<val.length;j++){
					if(val[i].equals(val[j])){
						 if(!varDom1.containsKey(pId)){
			 	        	  varDom1.put(pId, new Int2ObjectOpenHashMap<IntSet>());
			 	           }
			 	           if(!varDom1.get(pId).containsKey(i)){
			 	        	   varDom1.get(pId).put(i, new IntOpenHashSet());
			 	           }
			 	           varDom1.get(pId).get(i).add(j);
					}
				}
				if(val[i].equals(val1)){
					if(i < fId){
						if(!varDom1.containsKey(pId)){
			 	        	  varDom1.put(pId, new Int2ObjectOpenHashMap<IntSet>());
			 	           }
			 	           if(!varDom1.get(pId).containsKey(i)){
			 	        	   varDom1.get(pId).put(i, new IntOpenHashSet());
			 	           }
			 	           varDom1.get(pId).get(i).add(fId);
					}
					if(i > fId){
						if(!varDom1.containsKey(pId)){
			 	        	  varDom1.put(pId, new Int2ObjectOpenHashMap<IntSet>());
			 	           }
			 	           if(!varDom1.get(pId).containsKey(fId)){
			 	        	   varDom1.get(pId).put(i, new IntOpenHashSet());
			 	           }
			 	           varDom1.get(pId).get(fId).add(i);
					}
				}
				if(val[i].equals(val2)){
					if(i < tId){
						if(!varDom1.containsKey(pId)){
			 	        	  varDom1.put(pId, new Int2ObjectOpenHashMap<IntSet>());
			 	           }
			 	           if(!varDom1.get(pId).containsKey(i)){
			 	        	   varDom1.get(pId).put(i, new IntOpenHashSet());
			 	           }
			 	           varDom1.get(pId).get(i).add(tId);
					}
					if(i > tId){
						if(!varDom1.containsKey(pId)){
			 	        	  varDom1.put(pId, new Int2ObjectOpenHashMap<IntSet>());
			 	           }
			 	           if(!varDom1.get(pId).containsKey(tId)){
			 	        	   varDom1.get(pId).put(i, new IntOpenHashSet());
			 	           }
			 	           varDom1.get(pId).get(tId).add(i);
					}
				}
			}
			 return true;
			}			
		return false;		
	
	}

	@Override
	public void prepareResult(Partition partition) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void prepareResult() {
		// TODO Auto-generated method stub
		
	}



	public void setWorkUnits(Set<WorkUnit> workload2) {
		// TODO Auto-generated method stub
		log.debug("begin set workunit in superstep" + this.getSuperstep());
		this.workload.clear();
		// TODO Auto-generated method stub
		this.workload = workload2;
	}
	
	
	

	
}
