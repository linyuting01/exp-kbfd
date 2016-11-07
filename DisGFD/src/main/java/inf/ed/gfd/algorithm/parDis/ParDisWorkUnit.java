package inf.ed.gfd.algorithm.parDis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

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
import inf.ed.gfd.structure.TransAttr;
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
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntList;
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
	public Int2ObjectMap<List<Int2IntMap> > transPatternNodeMatches =  new Int2ObjectOpenHashMap<List<Int2IntMap>>();
	Set<WorkUnit> workload = new HashSet<WorkUnit>();
	HashMap<String,Boolean> transFlag = new HashMap<String,Boolean>();
	IntSet transAttrFlag  = new IntOpenHashSet();
	Int2ObjectMap<List<Pair<Integer,Integer>>> transferMatch =
			new Int2ObjectOpenHashMap<List<Pair<Integer,Integer>>>();
	
	public Int2ObjectMap<List<Pair<Integer,Integer>>> edgePatternNodeMatch1 = new Int2ObjectOpenHashMap<List<Pair<Integer,Integer>>>();
	//SuppResult suppResult = new SuppResult();
	public Int2ObjectMap<Int2ObjectMap<IntSet>> pivotMatchGfd1 = new Int2ObjectOpenHashMap<Int2ObjectMap<IntSet>>() ;
	public Int2ObjectMap<Int2ObjectMap<IntSet>> satCId1 = new Int2ObjectOpenHashMap<Int2ObjectMap<IntSet>>();
	
	public Int2ObjectMap<List<TransAttr>> kbAttr_Map = new Int2ObjectOpenHashMap<List<TransAttr>>();
	
	
	public Map<DFS,Integer> dfs2Id = new HashMap<DFS,Integer>();
	public Int2ObjectMap<DFS> id2Dfs = new Int2ObjectOpenHashMap<DFS>();
	
	public Int2ObjectMap<IntList> nodeAttr_Map = new Int2ObjectOpenHashMap<IntList>();
	
    public Int2ObjectMap<List<TransAttr>> transAttr_Map = new Int2ObjectOpenHashMap<List<TransAttr>>();
	
	Int2DoubleMap avgMatch = new Int2DoubleOpenHashMap();
	
	public ParDisWorkUnit() {
		// TODO Auto-generated constructor stub
	}
	
	
	
	public void setWorkUnit(Set<WorkUnit> workload) {
		this.workload = workload;
	}
	

	public int processWorkUnitAndGfdMsg(Partition partition){
		//pivotPMatch1.clear();
		isoResult.clear();
		int i = -1;
		int size = workload.size();
		int round = 1;
		WorkUnit ws = Fuc.getRandomWorkUnit(workload);
		if(ws.isAvg){
			for( WorkUnit w: workload){
				if(patternNodeMatchesP.containsKey(w.patternId)){
					avgMatch.put(w.patternId, w.avg);
				}
			}
			loadBalance();
			log.debug("begin to balance pattern match");
			sendBalanceMsg();
			return 3;
			
			
		}
		if(ws.isGfdCheck){
			i = 0;
		}
		if(ws.isIsoCheck){
			i =1;
		}
		if(ws.isPatternCheck){
			i=2;
		}
		
		for( WorkUnit w: workload){
			if(i == 0){
				checkGfd(w,partition);
			}
			if(i == 1){
				checkIso(w);
			}
			if(i == 2){
			  gfdMsgProcess(w,partition);
			}
				
		}
		
		
		if(i==2){
		//prepareResult(false);
			//if(!gfdMsg.transferingEdgeMatch.isEmpty()){
				if(Params.N_PROCESSORS > 1){
			
					sendTransferData(partition.getPartitionID());
					//prepareResult(3);
				}
			}
		//}
		return i;
	
	}

	

	Int2ObjectMap<IntSet> isoResult = new Int2ObjectOpenHashMap<IntSet>();
	private void checkIso(WorkUnit w) {
		// TODO Auto-generated method stub
		if(w.isoPatterns != null){
	       
				int attr1 = w.isoPatterns.x.allVertices().get(1).getAttr();
				for(Entry<Integer, VertexString> entryattr: w.isoPatterns.y.allVertices().entrySet()){
					int attr2 = entryattr.getValue().getAttr();
					if(attr1 == attr2){
						log.debug("begin to check pattern isomorphism!");
						
						VF2IsomorphismInspector isomorphismChecker = new VF2IsomorphismInspector();
						boolean flag =isomorphismChecker.isSubgraphIsomorphic( w.isoPatterns.x, 1, 
								w.isoPatterns.y, entryattr.getKey());
						log.debug("begin to verify pattens isomorphism and the result == " + flag);
						if(flag){
							 if(!isoResult.containsKey(w.isoIds.x)){
							    	isoResult.put(w.isoIds.x, new IntOpenHashSet());
							    	break;
							    }
							isoResult.get(w.isoIds.x).add(w.isoIds.y);
							//update isoResult
							//updateIsoResult(w.isoIds.x,w.isoIds.y);
							
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
	
	public  Int2ObjectMap<IntSet>  getIsoResult(Int2ObjectMap<IntSet> isoResult){
		Int2ObjectMap<IntSet> resultx = new Int2ObjectOpenHashMap<IntSet>(isoResult);
		Stack<Integer> stack = new Stack<Integer>();
		Set<IntSet> result = new HashSet<IntSet>();
		for(Entry<Integer,IntSet> entry: isoResult.entrySet()){
			if(resultx.containsKey(entry.getKey())){
				for(int a :entry.getValue()){
					stack.add(a);
				}	
				while(!stack.isEmpty()){
					int i = stack.pop();
						resultx.get(entry.getKey()).addAll(isoResult.get(i));
						stack.addAll(isoResult.get(i));
						resultx.remove(i);
					}
				}
		}
		for(IntSet a :resultx.values()){
			result.add(a);
		}
		return resultx;
	}







	public void gfdMsgProcess(WorkUnit w, Partition partition){
		gfdMsg.clear();
		if(w.edgeIds != null &&!w.edgeIds.isEmpty()){
		for(Pair<Integer, Pair<Integer,Integer>> edges :w.edgeIds.values()){
			   int id = edges.x;
				if(!transFlag.containsKey(id)){
					if(edgePatternNodeMatch1.containsKey(id)){
						if(!gfdMsg.transferingEdgeMatch.containsKey(id)){
				        	 gfdMsg.transferingEdgeMatch.put(id, edgePatternNodeMatch1.get(id));
				         }
						gfdMsg.partitionId = partition.getPartitionID();
						for(Pair<Integer,Integer> p : edgePatternNodeMatch1.get(id)){    
						 	if(!transAttrFlag.contains(p.x)){
						 		transAttrFlag.add(p.x);
						 		gfdMsg.transAttr_Map.put(p.x, kbAttr_Map.get(p.x));
						 	}
						 	if(!transAttrFlag.contains(p.y)){
						 		transAttrFlag.add(p.y);
						 		gfdMsg.transAttr_Map.put(p.y, kbAttr_Map.get(p.y));
						 	}
						}
					}
				}
			}
		}
			
		}
		
	
	

	private void checkGfd(WorkUnit w,Partition partition) {
		// TODO Auto-generated method stub
		int pId = w.patternId;
		int round =1;
		if(patternNodeMatchesP.containsKey(pId)){
			if(!pivotMatchGfd1.containsKey(pId)){
				pivotMatchGfd1.put(pId, new Int2ObjectOpenHashMap<IntSet>());
				
			}
			if(!satCId1.containsKey(pId)){
				satCId1.put(pId, new Int2ObjectOpenHashMap<IntSet>());	
			}
			for(Int2IntMap match : patternNodeMatchesP.get(pId)){
				//log.debug("pattern matchsize" + patternNodeMatchesP.get(pId).size() + "now the " +round);
				round++;
				Int2ObjectMap<Condition> conditions = w.conditions;
				int round2 =1;
				//log.debug("pattern match size" + patternNodeMatchesP.size());
				for(Entry<Integer, Condition> entry : conditions.entrySet()){
					//log.debug("condition size "+ conditions.size() + "now the"+ round2);
					round2++;
					int cId = entry.getKey();
					Condition c = entry.getValue();
			
					
					if(!satCId1.get(pId).containsKey(cId)){
						satCId1.get(pId).put(cId, new IntOpenHashSet());
					}
					
				
					boolean flag = c.verifyX( match, kbAttr_Map);
					if(flag){
					
						boolean flag2 = c.verifyY(match, kbAttr_Map);
						if(flag2){
							if(!pivotMatchGfd1.get(pId).containsKey(cId)){
								pivotMatchGfd1.get(pId).put(cId, new IntOpenHashSet());
							}
							pivotMatchGfd1.get(pId).get(cId).add(match.get(1));
						}
						else{
							//not satisfy condition but satisfy x
							satCId1.get(pId).get(cId).add(match.get(1));
						}
						
					}
				
			}
			}
		}
			
			
			
		
	}

	private void sendTransferData(int partitionId) {
		log.debug(Params.N_PROCESSORS);
			for (int targetPartitionID = 0; targetPartitionID < Params.N_PROCESSORS  
					; targetPartitionID++) {
			  if(targetPartitionID!= partitionId ){
				Message<GfdMsg> nMsg = new Message<GfdMsg>(partitionId,
						targetPartitionID, gfdMsg);
				this.generatedMessages.add(nMsg);
				//log.debug(this.generatedMessages.size());
			  }
			}
	}
	

	
	
	
	IntSet balancePId = new IntOpenHashSet();
	private void loadBalance() {
		balancePId.clear();
		// TODO Auto-generated method stub
		for(Entry<Integer, Double> entry : avgMatch.entrySet()){
			int key = entry.getKey();
			
			if(matchNum.containsKey(key)){
				double avg = entry.getValue();
				int m = matchNum.get(key);
				double skew = m/avg;
				if(skew > Params.VAR_SKEW){
					balancePId.add(key);
				}
			}
		}
	}	
			

	
	
	
	
	public Int2ObjectMap<List<Int2IntMap>> getMatch(GfdMsg gfdMsg){
		Int2ObjectMap<List<Int2IntMap>> tmpt = new Int2ObjectOpenHashMap<List<Int2IntMap>>();
		if(!balancePId.isEmpty()){
			for(int pId:balancePId){
				int i = 1;
				List<Int2IntMap> tmptm = new ArrayList<Int2IntMap>();
				Iterator<Int2IntMap> it = patternNodeMatchesP.get(pId).iterator();
				int size = matchNum.get(pId);
				int avg = size/Params.N_PROCESSORS;
				  while(it.hasNext() && i<avg) {
					  Int2IntMap a =  it.next();
    					tmptm.add(a);
    					i++;
    					it.remove();
    					for(int node :a.values()){
    						if(!transAttrFlag.contains(node)){
						 		transAttrFlag.add(node);
						 		  gfdMsg.transAttr_Map.put(node, kbAttr_Map.get(node));
						 	}
    					}
    				}
    			tmpt.put(pId, tmptm);
			}
		}
		return tmpt;
	}
	
	private void sendBalanceMsg(){
		//gfdMsg.clear();
		//transAttr_Map.clear();
		//loadBalance();
		for (int targetPartitionID = 0; targetPartitionID < Params.N_PROCESSORS  
				; targetPartitionID++) {
		  if(targetPartitionID!= partitionId ){
			  GfdMsg gm = new GfdMsg();
			  gm.patternmatches =  getMatch(gm);
			  Message<GfdMsg> nMsg = new Message<GfdMsg>(partitionId,
					targetPartitionID, gm);
			 this.generatedMessages.add(nMsg);
			//log.debug(this.generatedMessages.size());
		  }
	}
	}
	
	private void receiveBalancedMsg(Partition partition,  List<Message<?>> incomingMessages){
		if (incomingMessages != null) {
			for (Message<?> recvMsg : incomingMessages) {
				GfdMsg msg = (GfdMsg)recvMsg.getContent();
				for(Entry<Integer,List<Int2IntMap> >entry :msg.patternmatches.entrySet()){
					int pId = entry.getKey();
					if(!patternNodeMatchesP.containsKey(pId)){
						patternNodeMatchesP.put(pId, new ArrayList<Int2IntMap>());
					}
					patternNodeMatchesP.get(pId).addAll(entry.getValue());
				
			}
			}
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
		for(Entry<Integer, List<Pair<Integer,Integer>>> entry: 
			recvContent.transferingEdgeMatch.entrySet()){
			int edgeId = entry.getKey();
			if(!transferMatch.containsKey(edgeId)){
				transferMatch.put(edgeId,new ArrayList<Pair<Integer,Integer>>());
			}
			transferMatch.get(edgeId).addAll(entry.getValue());
		}
		for(Entry<Integer, List<TransAttr>> entry: 
			recvContent.transAttr_Map.entrySet()){
			int nodeId = entry.getKey();
			if(!kbAttr_Map.containsKey(nodeId)){
				kbAttr_Map.put(nodeId,entry.getValue());
			}
			
		}
	}
	


	@Override
	public void prepareResult(int flag) {
		// TODO Auto-generated method stub
		if(flag ==0){
			SuppResult w = (SuppResult)this.generatedResult;
			w.checkGfd = true;
			w.pivotMatchGfd = pivotMatchGfd1;
			w.satCId = satCId1;
			 w.nodeNum = Params.GRAPHNODENUM;
			
		}
		if(flag == 1){
			SuppResult w = (SuppResult)this.generatedResult;
			if(!isoResult.isEmpty()){
				w.isoResult = Fuc.getIsoResult(isoResult);
			}
			w.isIsoCheck = true;
			w.nodeNum = Params.GRAPHNODENUM;
		}
		
		
		if(flag == 2){
			 SuppResult w = (SuppResult)this.generatedResult;
			    w.pivotMatchP =	pivotPMatch1;
			    w.extendPattern = true;
			    w.nodeNum = Params.GRAPHNODENUM;
			    w.matchsize = true;
				w.patternMatchesNum = matchNum;
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
		
		//log.debug("begin local compute current super step = " + this.getSuperstep());
		//log.debug(partition.getPartitionID());
		
			//generate edge patterns.
		/*
			EdgePattern eP = new EdgePattern();
			
			Set<DFS> edgePattern = eP.edges(partition.getGraph());
			log.debug("local edgePattern size" + edgePattern.size());
					//eP.edgePattern(partition.getGraph(), pivotPMatch,
					//edgePatternNodeMatch,literDom,varDom );
			//log.debug(edgePattern.size());
		    SuppResult w = (SuppResult)this.generatedResult;
		    w.edgeCands = edgePattern;
		    w.extendPattern = true;*/
		for(WorkUnit w : workload){
			checkGfd(w,partition);
		}
		prepareResult(0);
		
	}
	
	Int2IntMap matchNum = new Int2IntOpenHashMap();
	@Override
	public void compute2(Partition partition){
		EdgePattern eP = new EdgePattern();
		for(WorkUnit w: workload){
			//log.debug(w.id2Dfs.size());
			dfs2Id= w.dfs2Ids;
			nodeAttr_Map = w.nodeAttr_Map;
			for(Entry<DFS,Integer> entry : dfs2Id.entrySet()){
				id2Dfs.put(entry.getValue(), entry.getKey());
			}
		}
		 eP.edgePattern(partition.getGraph(), pivotPMatch1,edgePatternNodeMatch1,
				 patternNodeMatchesN,dfs2Id,matchNum,kbAttr_Map,nodeAttr_Map);
		log.debug("pattern matches " +patternNodeMatchesN.size());
		 SuppResult w = (SuppResult)this.generatedResult;
		 w.extendPattern = true;
		 w.pivotMatchP = pivotPMatch1;
		 w.nodeNum = Params.GRAPHNODENUM;
		 w.matchsize = true;
		 w.patternMatchesNum = matchNum;
	}



   int flag = -1;
	@Override
	public int incrementalCompute(Partition partition){
		log.info("now incremental compute to verify  ");
	
		flag = processWorkUnitAndGfdMsg(partition);
		//log.debug("flag" + flag);
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
		if(flag == 2){
	         log.debug("receive the edgematch mas, begin to check pattern");
			 IncrePattern(partition,edgePatternNodeMatch1);
			 
			
			if(Params.N_PROCESSORS > 1){
				receiveTransferedData(partition,  incomingMessages);
				IncrePattern(partition,transferMatch);
				
			} 
			 prepareResult(2);
		}
		if(flag == 3){
			log.debug("receive the balance msg begin to check gfd");
			receiveBalancedMsg(partition,  incomingMessages);
			for(WorkUnit w : workload){
				checkGfd(w,partition);
			}
			 prepareResult(0);
		}
	

			
		}
	    

		
	

	
private  void IncrePattern(Partition partition, Int2ObjectMap<List<Pair<Integer, Integer>>> edgePatternNodeMatch12){
		//pivotPMatch.clear();
		//patternNodeMatchesP.clear();;
	    //patternNodeMatchesP = (HashMap<String, List<Int2IntMap>>)patternNodeMatchesN.clone();
	   // patternNodeMatchesN. clear();
	    for(WorkUnit w: workload){
	    	//log.debug(w.toString());
		    	IncPattern( w, partition,edgePatternNodeMatch12);
		    }
			   

}
private void IncPattern(WorkUnit w, Partition partition, Int2ObjectMap<List<Pair<Integer, Integer>>> edgePatternNodeMatch12){
	
	int ppId = w.oriPatternId;
	log.debug("prevous patter ID = " +ppId);
	if(patternNodeMatchesP.containsKey(ppId)){
		List<Int2IntMap> pmatches = patternNodeMatchesP.get(ppId);
		log.debug("pattern match size = "+ pmatches.size());
		//for each match of previous pattern ppId
		for(Int2IntMap match : pmatches){
			//for each edge wait to added into ppId
		    for(Entry<Integer,Pair<Integer,Pair<Integer,Integer>>> entry : w.edgeIds.entrySet()){
		    	int pId = entry.getKey();
		    	if(!pivotPMatch1.containsKey(pId)){
					pivotPMatch1.put(pId, new IntOpenHashSet());
				}
		    		int edgeId = entry.getValue().x;
		    		Pair<Integer,Integer> pair = entry.getValue().y;
		    		IncPatterMatchEdge(match,ppId,pId,edgeId, pair,partition,edgePatternNodeMatch12);
		    	}
		    }
		}
		
	}


	private void IncPatterMatchEdge(Int2IntMap match, int opId, int pId, int edgeId, 
			Pair<Integer,Integer> pair, Partition partition,Int2ObjectMap<List<Pair<Integer, Integer>>> edgePatternNodeMatch12){
//for each edge wait to added into ppId
					
		  if(edgePatternNodeMatch12.containsKey(edgeId)){
					List<Pair<Integer,Integer>> pairL = edgePatternNodeMatch12.get(edgeId);
					
				
					//edge (fId,tId,eLabel)
					int fId = pair.x;
					int tId = pair.y;
					int eLabel = id2Dfs.get(edgeId).eLabel;
					
					if(match.containsKey(fId)){//add the node AB A is in ppId
						if(match.containsKey(tId)){// add AB AB is in ppId
							Pair<Integer,Integer> p = new Pair<Integer,Integer>(match.get(fId),match.get(tId));
							if(pairL.contains(p)){
								//log.debug("find a match by two nodes ");
									addMatch(match, pId, fId, tId, 0,0 );
								
							}
						}
						else{
							for(Pair<Integer,Integer> p: pairL){
								if(p.x == match.get(fId) && !match.values().contains(p.y)){
									//log.debug("find a match by fnodes");
										addMatch(match, pId, fId, tId, 1,(int)p.y);
									}
									
									
								}
							}
						}
					else{
					
					if(match.containsKey(tId)){
						for(Pair<Integer,Integer> p: pairL){
							if(p.y == match.get(tId) && !match.values().contains(p.x)){
								//log.debug("find a match by tnode");
									addMatch(match, pId, fId, tId, 2,(int)p.x);
								}
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
		
		pivotPMatch1.get(pId).add(tmpt.get(1));
		  if(!matchNum.containsKey(pId)){
	        	 matchNum.put(pId, 1);  
	         }else{
	        	 int i1 = matchNum.get(pId);
	        	 matchNum.put(pId, i1+1);
	         }
		
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
		//log.debug("begin set workunit in superstep" + workload2.size());
		this.workload.clear();
		// TODO Auto-generated method stub
		this.workload = workload2;
		
	}
	
	
	

	
}
