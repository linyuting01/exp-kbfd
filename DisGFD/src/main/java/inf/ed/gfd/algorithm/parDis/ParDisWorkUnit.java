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
import inf.ed.gfd.structure.GfdMsg;
import inf.ed.gfd.structure.GfdNode;
import inf.ed.gfd.structure.Partition;
import inf.ed.gfd.structure.SuppResult;
import inf.ed.gfd.structure.WorkUnit;
import inf.ed.gfd.util.KV;
import inf.ed.gfd.util.Params;
import inf.ed.grape.interfaces.LocalComputeTask;
import inf.ed.grape.interfaces.Message;
import inf.ed.graph.structure.Graph;
import inf.ed.graph.structure.OrthogonalEdge;
import inf.ed.graph.structure.adaptor.Pair;
import inf.ed.graph.structure.adaptor.VertexOString;
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
	public HashMap<String,IntSet> pivotPMatch  = new HashMap<String,IntSet>();
	public HashMap<String, List<Pair<Integer,Integer>>> edgePatternNodeMatch = new HashMap<String, List<Pair<Integer,Integer>>>();
	public HashMap<String, List<Int2IntMap> > patternNodeMatchesN =  new HashMap<String, List<Int2IntMap>>();
	public HashMap<String, List<Int2IntMap> > patternNodeMatchesP =  new HashMap<String, List<Int2IntMap>>();
	
	HashMap<String,List<WorkUnit>> workload = new HashMap<String,List<WorkUnit>>();
	HashMap<String,Boolean> transFlag = new HashMap<String,Boolean>();
	HashMap<Integer,HashMap<String,List<Pair<Integer,Integer>>>> transferMatch =
			new HashMap<Integer,HashMap<String,List<Pair<Integer,Integer>>>>();
	
	
	//SuppResult suppResult = new SuppResult();
	public HashMap<String, HashMap<String,IntSet>> pivotMatchGfd = new HashMap<String, HashMap<String,IntSet>>() ;
	public HashMap<String, HashMap<String,Boolean>> satCId = new HashMap<String, HashMap<String,Boolean>>();
	
	public boolean isGfdCheck;
	
	public ParDisWorkUnit() {
		// TODO Auto-generated constructor stub
	}
	
	
	
	public void setWorkUnit(HashMap<String,List<WorkUnit>> workload) {
		this.workload = workload;
	}
	

	public boolean processWorkUnitAndGfdMsg(Partition partition){
		pivotPMatch.clear();
		for( Entry<String, List<WorkUnit>> entry : workload.entrySet()){
			for(WorkUnit w: entry.getValue()){
				if(w.isGfdCheck){
					checkGfd(w,partition);
					//prepareResult(true);
					
					return true;
				}
				else{
					gfdMsgProcess(w);
				}		
			}	
			
		}
		//prepareResult(false);
		sendTransferData(partition.getPartitionID());
		return false;
	}
	
	public void gfdMsgProcess(WorkUnit w){
		gfdMsg.clear();
		for(DFS dfs : w.edgeIds.keySet()){
			DFS dfsnew = dfs.findDFS();
			String id = dfsnew.toString();
			if(!transFlag.containsKey(id)){
				for(Pair<Integer,Integer> p : edgePatternNodeMatch.get(id)){
					 VertexOString f1 = new VertexOString(p.x,dfs.fLabel.x);
					 VertexOString t1 = new VertexOString(p.y,dfs.tLabel.x);
					 
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
		String pId = w.patternId;
		Condition c = w.condition;
		String cId = c.toString();
		String gfdId = pId+cId;
		if(!satCId.containsKey(pId)){
			satCId.put(pId, new HashMap<String,Boolean>());	
		}
		if(!satCId.get(pId).containsKey(cId)){
			satCId.get(pId).put(cId, true);
		}
		for(Int2IntMap match : patternNodeMatchesN.get(pId)){
			boolean flag = c.verify(match, partition.getGraph());
			if(flag){
				if(!pivotMatchGfd.containsKey(pId)){
					pivotMatchGfd.put(pId, new HashMap<String,IntSet>());
					
				}
				if(!pivotMatchGfd.get(pId).containsKey(cId)){
					pivotMatchGfd.get(pId).put(cId, new IntOpenHashSet());
				}
				pivotMatchGfd.get(pId).get(cId).add(match.get(1));
			}
			else{
				satCId.get(pId).put(cId, false);
			}
			
		}
	}

	

	@Override
	public void compute(Partition partition) {
		
		// TODO Auto-generated method stub
		
		log.debug("begin local compute current super step = " + this.getSuperstep());
		//log.debug(partition.getPartitionID());
		
			//generate edge patterns.
			EdgePattern eP = new EdgePattern();
			List<DFS> edgePattern = eP.edgePattern( partition.getGraph(), pivotPMatch,
					edgePatternNodeMatch,patternNodeMatchesN);
			log.debug(edgePattern.size());
		    SuppResult w = (SuppResult)this.generatedResult;
		    w.pivotMatchP =	pivotPMatch;
		    log.debug(w.pivotMatchP.size());
		    w.extendPattern = true;
		    w.nodeNum = Params.GRAPHNODENUM;
		    log.debug("set suppreslut done!");
		    //superstep++;
		
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
		for(Entry<String, List<Pair<VertexOString,VertexOString>>> entry: 
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
							new HashMap<String,List<Pair<Integer,Integer>>>());
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
	public void prepareResult(boolean flag) {
		// TODO Auto-generated method stub
		if(flag){
			 SuppResult w = (SuppResult)this.generatedResult;
			    w.pivotMatchP =	pivotPMatch;
			    w.extendPattern = true;
			    w.nodeNum = Params.GRAPHNODENUM;
		}
		else{
			SuppResult w = (SuppResult)this.generatedResult;
			w.extendPattern = false;
			w.pivotMatchGfd = pivotMatchGfd;
			w.satCId = satCId;
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



	public void setWorkUnits(HashMap<String, List<WorkUnit>> workload2) {
		// TODO Auto-generated method stub
		this.workload = workload2;
	}

	@Override
	public boolean incrementalCompute(Partition partition){
		log.info("now incremental compute ");
		pivotMatchGfd.clear();
		satCId.clear();
		pivotPMatch.clear();
		boolean flag = processWorkUnitAndGfdMsg(partition);
		if(flag){
			prepareResult(true);
		}
		return flag;	
	}
	public void incrementalCompute(Partition partition, List<Message<?>> incomingMessages) {
		// TODO Auto-generated method stub

	   // log.info("now incremental compute ");
	   
	     receiveTransferedData(partition,  incomingMessages);
		 IncrePattern(partition,edgePatternNodeMatch);
		 for(HashMap<String,List<Pair<Integer,Integer>>> edgeMatch: transferMatch.values()){
			IncrePattern(partition,edgeMatch);
		 }
		 prepareResult(false);
	    }

		
	

	
private  void IncrePattern(Partition partition, HashMap<String, List<Pair<Integer,Integer>>> edgeMatch){
		//pivotPMatch.clear();
		patternNodeMatchesP.clear();;
	    patternNodeMatchesP = (HashMap<String, List<Int2IntMap>>)patternNodeMatchesN.clone();
	    patternNodeMatchesN. clear();
	    for(Entry<String,List<WorkUnit>> entry1: workload.entrySet()){
		    for(WorkUnit w : entry1.getValue()){
		    	
		    	IncPattern( w, partition,edgeMatch);
		    }
			   
	    }
}
private void IncPattern(WorkUnit w, Partition partition, HashMap<String, List<Pair<Integer,Integer>>> edgeMatch){
	
	String ppId = w.oriPatternId;
	List<Int2IntMap> pmatches = patternNodeMatchesP.get(ppId);
	
	//for each match of previous pattern ppId
	for(Int2IntMap match : pmatches){
		//for each edge wait to added into ppId
		for(Entry<DFS, Pair<Integer,Integer>> entry : w.edgeIds.entrySet()){
			DFS dfs = entry.getKey();
			String opId = w.oriPatternId;
			Pair<Integer,Integer> pair = entry.getValue();
			
			IncPatterMatchEdge(match,opId,dfs, pair,partition,edgeMatch);
		}	
		
		
	}
}

	private void IncPatterMatchEdge(Int2IntMap match, String opId,DFS dfs, 
			Pair<Integer,Integer> pair, Partition partition,HashMap<String, List<Pair<Integer,Integer>>> edgeMatch){
//for each edge wait to added into ppId
				
					
					String edgeId1 = dfs.toString();
					String pId = opId + edgeId1;
					
					String edgeId = dfs.findDFS().toString();	
					List<Pair<Integer,Integer>> pairL = edgeMatch.get(edgeId);
					
				
					//edge (fId,tId,eLabel)
					int fId = pair.x;
					int tId = pair.y;
					int eLabel = dfs.eLabel;
					
					if(match.containsKey(fId)){//add the node AB A is in ppId
						if(match.containsKey(tId)){// add AB AB is in ppId
							Pair<Integer,Integer> p = new Pair<Integer,Integer>(match.get(fId),match.get(tId));
							if(pairL.contains(p)){
								if(matchKB(p.x,p.y,eLabel,partition)){
									addMatch(match, pId, fId, tId, 0,0 );
								}
							}
						}
						else{
							for(Pair<Integer,Integer> p: pairL){
								if(p.x == match.get(fId)){
									if(matchKB(p.x,p.y,eLabel,partition)){
										addMatch(match, pId, fId, tId, 1,(int)p.y);
									}
									
									
								}
							}
						}
					}
					if(match.containsKey(tId)){
						for(Pair<Integer,Integer> p: pairL){
							if(p.y == match.get(fId)){
								if(matchKB(p.x,p.y,eLabel,partition)){
									addMatch(match, pId, fId, tId, 2,(int)p.x);
								}
							}
						}
					}
	
	}




	
private void addMatch(Int2IntMap match, String pId, int fId, int tId, int flag, int x){
		
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
		if(!pivotPMatch.containsKey(pId)){
			pivotPMatch.put(pId, new IntOpenHashSet());
		}
		pivotPMatch.get(pId).add(tmpt.get(1));
	}
	
	private boolean matchKB(int fId, int tId, int elabel, Partition partition){
		OrthogonalEdge e = partition.getGraph().getEdge(fId, tId);
		int[] attrs= e.getAttr();
		for(int m: attrs){
			if(m == elabel) {
			 return true;
			}			
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
	

}
