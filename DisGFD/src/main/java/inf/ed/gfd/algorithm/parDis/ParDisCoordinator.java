package inf.ed.gfd.algorithm.parDis;

import inf.ed.gfd.structure.Condition;
import inf.ed.gfd.structure.CrossingEdge;
import inf.ed.gfd.structure.DFS;
import inf.ed.gfd.structure.DisconnectedTree;
import inf.ed.gfd.structure.GFD2;
import inf.ed.gfd.structure.GfdNode;
import inf.ed.gfd.structure.GfdTree;
import inf.ed.gfd.structure.LiterNode;
import inf.ed.gfd.structure.SimP;
import inf.ed.gfd.structure.SuppResult;
import inf.ed.gfd.structure.WorkUnit;
import inf.ed.gfd.util.Fuc;
import inf.ed.gfd.util.KV;
import inf.ed.gfd.util.Params;
import inf.ed.gfd.util.Stat;
import inf.ed.grape.communicate.Client2Coordinator;
import inf.ed.grape.communicate.Worker;
import inf.ed.grape.communicate.Worker2Coordinator;
import inf.ed.grape.interfaces.Result;
import inf.ed.graph.structure.Graph;
import inf.ed.graph.structure.adaptor.Pair;
import inf.ed.graph.structure.adaptor.TypedEdge;
import inf.ed.graph.structure.adaptor.VertexString;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.awt.geom.FlatteningPathIterator;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.Patterns;

import com.carrotsearch.sizeof.RamUsageEstimator;

/**
 * The Class Coordinator.
 * 
 * @author yecol
 */
public class ParDisCoordinator extends UnicastRemoteObject implements Worker2Coordinator,
		Client2Coordinator {

	private static final long serialVersionUID = 1L;

	/** The total number of worker threads. */
	private static AtomicInteger totalWorkerThreads = new AtomicInteger(0);

	/** The workerID to WorkerProxy map. */
	protected Map<String, ParDisWorkerProxy> workerProxyMap = new ConcurrentHashMap<String, ParDisWorkerProxy>();

	/** The workerID to Worker map. **/
	private Map<String, Worker> workerMap = new HashMap<String, Worker>();

	/** The partitionID to workerID map. **/
	private Map<Integer, String> partitionWorkerMap;

	/** The virtual vertexID to partitionID map. */
	private Map<Integer, Integer> virtualVertexPartitionMap;

	/** Set of Workers maintained for acknowledgement. */
	private Set<String> workerAcknowledgementSet = new HashSet<String>();

	/** Set of workers who will be active in the next super step. */
	private Set<String> activeWorkerSet = new HashSet<String>();

	/** Set of partial results. partitionID to Results **/
	private Map<Integer, Result> resultMap = new HashMap<Integer, Result>();

	/** The start time. */
	long wholeStartTime;
	long localStartTime;
	long firstPartialResultArrivalTime;
	boolean isFirstPartialResult = true;

	long superstep = 0;

	private String finalResultSuffix = "";
	private boolean beginNextCompute = false;

	/** for project Disgfd **/

	private Set<WorkUnit> workunits = new HashSet<WorkUnit>();

	
	public Set<Pair<String,String>> negGfdXCands = new HashSet<Pair<String,String>>();
	GfdTree gfdTree = new GfdTree();
	

	
	List<DFS> edgePattern = new ArrayList<DFS>();

	
	
	public HashMap<String, Integer> labelId = new HashMap<String, Integer>();
	
	DisconnectedTree distree = new DisconnectedTree();
	public Set<Integer> layerGfds = new HashSet<Integer>();
	
	
	
	Set<Pair<Integer,Integer>> gfdResults = new HashSet<Pair<Integer,Integer>>();
	//for negative gfds;
	public IntSet negCands = new IntOpenHashSet();
	public IntSet negGfdP = new IntOpenHashSet();
	public Set<Pair<Integer,Condition>> negGfdXF = new HashSet<Pair<Integer,Condition>>();
	public Map<DFS,Integer> dfs2Id = new HashMap<DFS,Integer>();
	public Int2ObjectMap<DFS> id2Dfs = new Int2ObjectOpenHashMap<DFS>();
	
	
	
	HashMap<Integer, String> attr_Map = new HashMap<Integer,String>();
	
	Set<String> dom  = new HashSet<String>();

	static Logger log = LogManager.getLogger(ParDisCoordinator.class);

	/**
	 * Instantiates a new coordinator.
	 * 
	 * @throws RemoteException
	 *             the remote exception
	 * @throws PropertyNotFoundException
	 *             the property not found exception
	 */
	public ParDisCoordinator() throws RemoteException {
		super();

	}

	/**
	 * Gets the active worker set.
	 * 
	 * @return the active worker set
	 */
	public Set<String> getActiveWorkerSet() {
		return activeWorkerSet;
	}

	/**
	 * Sets the active worker set.
	 * currentLocalComputeTaskQueue
	 * @param activeWorkerSet
	 *            the new active worker set
	 */
	public void setActiveWorkerSet(Set<String> activeWorkerSet) {
		this.activeWorkerSet = activeWorkerSet;
	}

	/**
	 * Registers the worker computation nodes with the master.
	 * 
	 * @param worker
	 *            Represents the {@link Setting2Worker.WorkerImpl Worker}
	 * @param workerID
	 *            the worker id
	 * @param numWorkerThreads
	 *            Represents the number of worker threads available in the
	 *            worker computation node
	 * @return worker2 master
	 * @throws RemoteException
	 *             the remote exception
	 */
	@Override
	public Worker2Coordinator register(Worker worker, String workerID, int numWorkerThreads)
			throws RemoteException {

		log.debug("Worker " + workerID + " registered and ready to work!");
		totalWorkerThreads.getAndAdd(numWorkerThreads);
		ParDisWorkerProxy workerProxy = new ParDisWorkerProxy(worker, workerID,
				numWorkerThreads, this);
		workerProxyMap.put(workerID, workerProxy);
		workerMap.put(workerID, worker);
		return (Worker2Coordinator) UnicastRemoteObject.exportObject(workerProxy, 0);
	}

	/**
	 * Gets the worker proxy map info.
	 * 
	 * @return Returns the worker proxy map info
	 */
	public Map<String, ParDisWorkerProxy> getWorkerProxyMap() {
		return workerProxyMap;
	}

	/**
	 * Send worker partition info.
	 * 
	 * @throws RemoteException
	 *             the remote exception
	 */
	public void sendWorkerPartitionInfo() throws RemoteException {
		log.debug("sendWorkerPartitionInfo");
		for (Map.Entry<String, ParDisWorkerProxy> entry : workerProxyMap.entrySet()) {
			ParDisWorkerProxy workerProxy = entry.getValue();
			workerProxy.setWorkerPartitionInfo(virtualVertexPartitionMap, partitionWorkerMap,
					workerMap);
		}
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 * @throws Exception
	 *             the exception
	 */

	public static void main(String[] args) throws Exception {
		if (args.length < 3) {
			 System.out.println("paras: config-file, n, opt");
			System.exit(0);
		}
		Params.CONFIG_FILENAME = args[0].trim();
		Params.N_PROCESSORS = Integer.parseInt(args[1].trim());
		Params.RUN_MODE =  Integer.parseInt(args[2].trim());
				
				
				//Integer.parseInt(args[2].trim());
		log.debug("PARAM_CONFIG_FILE = " + Params.CONFIG_FILENAME);
		log.debug("PARAM_N = " + Params.N_PROCESSORS);
		log.debug("PARAM_RUN_MODE = " + Params.RUN_MODE);
		log.debug("Processing FRAGMENTED graph = " + KV.DATASET);
		System.setSecurityManager(new SecurityManager());
		ParDisCoordinator coordinator;
		Stat.getInstance().setting = KV.SETTING_PARDISGFD;
		try {
			coordinator = new ParDisCoordinator();
			Registry registry = LocateRegistry.createRegistry(KV.RMI_PORT);
			registry.rebind(KV.COORDINATOR_SERVICE_NAME, coordinator);
			log.info("Coordinator listen to " + KV.RMI_PORT + " and ready to work!");
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMddHHmm");
			coordinator.finalResultSuffix = simpleDateFormat.format(new Date());
		} catch (RemoteException e) {
			ParDisCoordinator.log.error(e);
			e.printStackTrace();
		}
	}

	/**
	 * Halts all the workers and prints the final solution.
	 * 
	 * @throws RemoteException
	 *             the remote exception
	 */
	public void halt() throws RemoteException {
		log.debug("Worker Proxy Map " + workerProxyMap);

		for (Map.Entry<String, ParDisWorkerProxy> entry : workerProxyMap.entrySet()) {
			ParDisWorkerProxy workerProxy = entry.getValue();
			workerProxy.halt();
		}
		restoreInitialState();
	}

	/**
	 * Restore initial state of the system.
	 */
	private void restoreInitialState() {
		this.activeWorkerSet.clear();
		this.workerAcknowledgementSet.clear();
		this.partitionWorkerMap.clear();
		this.superstep = 0;
	}

	/**
	 * Removes the worker.
	 * 
	 * @param workerID
	 *            the worker id
	 */
	public void removeWorker(String workerID) {
		workerProxyMap.remove(workerID);
		workerMap.remove(workerID);
	}

	/**
	 * Gets the partition worker map.
	 * 
	 * @return the partition worker map
	 */
	public Map<Integer, String> getPartitionWorkerMap() {
		return partitionWorkerMap;
	}

	/**
	 * Sets the partition worker map.
	 * 
	 * @param partitionWorkerMap
	 *            the partition worker map
	 */
	public void setPartitionWorkerMap(Map<Integer, String> partitionWorkerMap) {
		this.partitionWorkerMap = partitionWorkerMap;
	}

	/**
	 * Defines a deployment convenience to stop each registered.
	 * 
	 * @throws RemoteException
	 *             the remote exception {@link system.Worker Worker} and then
	 *             stops itself.
	 */

	@Override
	public void shutdown() throws RemoteException {
		for (Map.Entry<String, ParDisWorkerProxy> entry : workerProxyMap.entrySet()) {
			ParDisWorkerProxy workerProxy = entry.getValue();
			try {
				workerProxy.shutdown();
			} catch (Exception e) {
				continue;
			}
		}
		System.exit(0);
	}

	/**
	 * Assign distributed partitions to workers, assuming graph has been
	 * partitioned and distributed.
	 */
	public void distributedLoadWholeGraph() {
		throw new IllegalArgumentException("No distribute whole graph in this setting.");
	}

	public void assignDistributedPartitions() {

		log.info("begin assign distributed partitions.");
		partitionWorkerMap = new HashMap<Integer, String>();
		// Assign partitions to workers in the ratio of the number of worker
		// threads that each worker has.
		assert workerProxyMap.size() == Params.N_PROCESSORS;

		int partitionID = 0;
		for (Map.Entry<String, ParDisWorkerProxy> entry : workerProxyMap.entrySet()) {

			ParDisWorkerProxy workerProxy = entry.getValue();
			activeWorkerSet.add(entry.getKey());
			log.debug("assign partition " + partitionID + " to worker " + workerProxy.getWorkerID());
			partitionWorkerMap.put(partitionID, workerProxy.getWorkerID());
			workerProxy.addPartitionID(partitionID);
			partitionID++;

		}
	}

	/**
	 * Start super step.
	 * 
	 * @throws RemoteException
	 *             the remote exception
	 */
	public void nextLocalCompute() throws RemoteException {
		
		log.info("---------------next begin super step " + superstep + "---------------");
		log.debug("activeWorkerSet size " + this.activeWorkerSet.size());
		this.workerAcknowledgementSet.clear();
		this.workerAcknowledgementSet.addAll(this.activeWorkerSet);

		isFirstPartialResult = true;

		for (String workerID : this.activeWorkerSet) {
			this.workerProxyMap.get(workerID).nextLocalCompute(superstep);
		}
		superstep++;
		resultMap.clear();
		
		//this.activeWorkerSet.clear();
	}

	@Override
	public synchronized void localComputeCompleted(String workerID, Set<String> activeWorkerIDs)
			throws RemoteException {

		log.info("receive acknowledgement from worker " + workerID + "\n saying activeWorkers: "
				+ activeWorkerIDs.toString());

		if (isFirstPartialResult) {
			isFirstPartialResult = false;
			firstPartialResultArrivalTime = System.currentTimeMillis();
		}

		//this.activeWorkerSet.addAll(activeWorkerIDs);
		//this.workerAcknowledgementSet.remove(workerID);

		if (this.workerAcknowledgementSet.size() == 0) {

			Stat.getInstance().finishGapTime = (System.currentTimeMillis() - firstPartialResultArrivalTime) * 1.0 / 1000;

			//superstep++;
			if (superstep < Params.var_K * Params.var_K) {
				log.info("superstep =" + superstep + ", manually active all worker.");
				//for (Map.Entry<String, ParDisWorkerProxy> entry : workerProxyMap.entrySet()) {
					//activeWorkerSet.add(entry.getKey());
				//}
			}
			if (activeWorkerSet.size() != 0){
				//beginNextCompute = true;
			}
			else {
				finishLocalCompute();
				log.debug("all process done!");
			}
		}
	}

	public void finishLocalCompute() throws RemoteException {

		this.resultMap.clear();

		this.workerAcknowledgementSet.clear();
		this.workerAcknowledgementSet.addAll(this.workerProxyMap.keySet());

		for (Map.Entry<String, ParDisWorkerProxy> entry : workerProxyMap.entrySet()) {
			ParDisWorkerProxy workerProxy = entry.getValue();
			workerProxy.processPartialResult();
		}
	}

	

	private void sendPartitionInfo() throws RemoteException {
		for (Map.Entry<String, ParDisWorkerProxy> entry : workerProxyMap.entrySet()) {
			ParDisWorkerProxy workerProxy = entry.getValue();
			workerProxy.setWorkerPartitionInfo(null, partitionWorkerMap, workerMap);
		}
	}

	public void preProcess() throws RemoteException {

		wholeStartTime = System.currentTimeMillis();
		// read border nodes;

		// distributed partition;
		assignDistributedPartitions();
		sendPartitionInfo();

		Stat.getInstance().getInputFilesLocalAndDistributedTime = (System.currentTimeMillis() - wholeStartTime) * 1.0 / 1000;

		//List<GFD2> queries = readGFDFromDir();
		//log.info("load " + queries.size() + " gfds from: " + KV.QUERY_DIR_PATH);

		this.workerAcknowledgementSet.clear();
		this.workerAcknowledgementSet.addAll(this.activeWorkerSet);
		nextLocalCompute();
		//process();

		//sendGFDs2Workers(queries);
	}



	@Override
	public void process() throws RemoteException {
		nextLocalCompute();
	}



	@Override
	public void postProcess() throws RemoteException {
	}

	@Override
	public void vote2halt(String workerID) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendPartialResult(String workerID, Map<Integer, Result> mapPartitionID2Result) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendPartialResult(String workerID, Map<Integer, Result> partialResults, double communicationData)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	
	public synchronized void receivePartialResults(String workerID,
			Map<Integer, Result> mapPartitionID2Result) throws RemoteException {

		log.debug("current ack set = " + this.workerAcknowledgementSet.toString());
		log.debug("receive partitial results = " + workerID);

		for (Entry<Integer, Result> entry : mapPartitionID2Result.entrySet()) {
			resultMap.put(entry.getKey(), entry.getValue());
		}

		this.workerAcknowledgementSet.remove(workerID);

		if (this.workerAcknowledgementSet.size() == 0) {
			

			/** receive all the partial results, assemble them. */
			log.info("assemble the result");

			

				SuppResult finalResult = new SuppResult();
				//log.debug(finalResult.pivotMatchP.size());
				log.debug(resultMap.values().size());
				finalResult.assemblePartialResults(resultMap.values());
				log.debug(finalResult.pivotMatchP.size());
				log.debug("graph node num : " +finalResult.nodeNum);
				log.debug("assembel done!");
				//ws.clear();
				extendAndDistributeWorkUnits(finalResult);
				//log.debug("has created the new workunit");
				
				
				try {
					log.debug("begin to process.");
					process();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
		}
	}
	
	//put them into paraResult;
	HashMap<Integer,Set<String>> literDom;
	HashMap<Integer,IntSet> varDom;
	
	//List<DFS> edgePattern = new ArrayList<DFS>();
	//HashMap<String,List<WorkUnit>> ws = new HashMap<String,List<WorkUnit>>();
	//superstep 1;
	public void createIdforDfs(SuppResult finalResult){
		int i = 1;
		log.debug("begin to create id for edgePatterns");
		for(DFS dfs :finalResult.edgeCands){
			dfs2Id.put(dfs, i);
			id2Dfs.put(i, dfs);
			i++;
			
		}
		gfdTree.dfs2Ids = dfs2Id;
		log.debug("id2Dfs size" +id2Dfs.size());
		WorkUnit w = new WorkUnit(id2Dfs);
		workunits.add(w);	
	}
	
	
	public void intialExtendAndGenerateWorkUnits(SuppResult finalResult){
		log.debug("begin compute support of edge pattern and extend condition y");
		for(int s :finalResult.pivotMatchP.keySet()){	
			double supp = ((double) finalResult.pivotMatchP.get(s).size())/finalResult.nodeNum;
			if(supp >= Params.VAR_SUPP){
				log.debug("supp value " + supp);
				/////////////////////////////////
				//revise;
				DFS dfs = id2Dfs.get(s);
				edgePattern.add(dfs);
				log.debug("edgePattern size" +edgePattern.size());
			}		
		}
		
		gfdTree.extendRoot(edgePattern, dfs2Id,finalResult);
		
		log.debug("begin to extend dependencied empty->y and create "
				+ "workunit for edge pattern with empty -> y ");
		
		for(GfdNode g:gfdTree.getRoot().children){
			g.ltree.extendNode(g.ltree.getRoot());
			Int2ObjectMap<Condition> conditions = new Int2ObjectOpenHashMap<Condition>();
			for(LiterNode t:g.ltree.getRoot().children){
				conditions.put(t.cId, t.dependency);
			}
			WorkUnit w = new WorkUnit(g.pId,conditions,true);
			workunits.add(w);
				
		}
	}
	
	
	public void extendAndDistributeWorkUnits(SuppResult finalResult) throws RemoteException{
		workunits.clear();
		if(this.superstep == 1){
			createIdforDfs(finalResult);
			assignWorkunitsToWorkers();
		}
		if(this.superstep == 2){
			intialExtendAndGenerateWorkUnits(finalResult);
			assignWorkunitsToWorkers();
		}
		else{
			if(finalResult.checkGfd){
				verifyGfdAndGenerateWorkUnits(finalResult);
				assignWorkunitsToWorkers();
			}
			if(finalResult.extendPattern){
				verifyPatternAndGenerateWorkUnits(finalResult);
				assignWorkunitsToWorkers();
			}
			if(finalResult.isIsoCheck){
				checkPatternsAndGenerateWorkUnits();
				assignIsoWOrkUnits();
				
			}
		}
		
	}
	IntSet tmpPatternCheck = new IntOpenHashSet();
	List<SimP> extendPatterns = new ArrayList<SimP>();
	public void verifyGfdAndGenerateWorkUnits(SuppResult finalResult) throws RemoteException{
            boolean  disConnectedRevoke = false;
			log.debug("begin to verify gfd and extend condition X and produce workunit for next step.");
			for(Entry<Integer, Int2ObjectMap<IntSet>> entry: finalResult.pivotMatchGfd.entrySet()){
				int pId = entry.getKey();
				for(Entry<Integer,IntSet> entry2 :finalResult.pivotMatchGfd.get(pId).entrySet()){
					int cId = entry2.getKey();
					GfdNode g = gfdTree.patterns_Map.get(pId);
					LiterNode t = g.ltree.condition_Map.get(cId);
					
					double supp = ((double) entry2.getValue().size())/finalResult.nodeNum;
					t.supp = supp;
					t.pivotMatch = entry2.getValue();
					log.debug("supp value " + supp);
					
					log.debug("check negative gfd ");
					if(supp == 0 && t.negCheck){
						negGfdXF.add(new Pair<Integer,Condition>(pId,t.dependency));
						t.extend = false;
					}
					
					if(!t.negCheck){
						//is a minimum gfd; check negative, update liter and var dom;
						if(supp >= Params.VAR_SUPP){
							if(finalResult.satCId.get(pId).get(cId)){
								Pair<Integer,Integer> gfd = new Pair<Integer,Integer>(pId,cId);
								gfdResults.add(gfd);
								//for negative gfd checking 
								t.isSat = true;	
								g.ltree.addNegCheck(t);
							}
							else{
								g.ltree.extendNode(t);
							}
						}else{
							t.extend = false;
							//////////////////////////////////////////////////
							//update literal dom and var dom
							if(t.dependency.XEqualsLiteral.isEmpty() && t.dependency.XEqualsVariable.isEmpty()){
								//update literdom and vardom;
								disConnectedRevoke = true;
								if(t.dependency.isLiteral){
									Pair<Integer,String> p = t.dependency.YEqualsLiteral;
									if(g.literDom.containsKey(p.x)){
									for(String s: g.literDom.get(p.x)){
									  if(s.equals(p.y)){
										  g.literDom.get(p.x).remove(s);
									  }
									}
									}
								}
								else{
									Pair<Integer,Integer> p = t.dependency.YEqualsVariable;
									if(g.varDom.containsKey(p.x)){
									for(int s: g.varDom.get(p.x)){
									  if(s == p.y){
										  g.varDom.get(p.x).remove(s);
									  }
									}
									}
								}
							}
							
							//////////////////////////////////////////////////////////
						}
						}
				}
				
			}
			//begin to create workunit
			
			boolean flagExtend = false;
			for(int pId: finalResult.pivotMatchGfd.keySet()){
				for(int cId: finalResult.pivotMatchGfd.get(pId).keySet()){
					GfdNode g = gfdTree.patterns_Map.get(pId);
					LiterNode t = g.ltree.condition_Map.get(cId);
					if(t.children!=null ){
						if(!t.children.isEmpty()){
							flagExtend = true;
							Int2ObjectMap<Condition> conditions = new Int2ObjectOpenHashMap<Condition>();
							for(LiterNode tc : t.children){
								conditions.put(t.cId, t.dependency);
							}
							WorkUnit w = new WorkUnit(g.pId,conditions,true);
							workunits.add(w);
						}
							
				}
			}
	    	}
			tmpPatternCheck.clear();
			if(flagExtend == false){
				log.debug("no condition more, begin to extend pattern");
				boolean flagExtendP = false;
				//no more extend of literNode
				layerGfds.clear();
				for(int pId:finalResult.pivotMatchGfd.keySet()){
					GfdNode g = gfdTree.patterns_Map.get(pId);
					gfdTree.extendGeneral(edgePattern, g);
					if(g.children != null){
						if(!g.children.isEmpty()){
							flagExtendP = true;
							tmpPatternCheck.add(g.pId);
							for(GfdNode g1: g.children){
								layerGfds.add(g1.pId);
							}
						}
					}
				}
				if(flagExtendP == false){
					distree.disConnectedGFD(extendPatterns);
					finishLocalCompute();
					log.debug("all process done!");
					this.shutdown();
				}
				classifyLayerGfds();
			}
	}

    public void isoCheckProcess(SuppResult r){
    	int j = 0;
    	for(IntSet a :r.isoResult){
    		for(int i :a){
    			j=i;
    			GfdNode g = gfdTree.patterns_Map.get(i);
    			g.extend = true;
    			g.parents = new IntOpenHashSet(a);
    			break;
    		}
    		for(int i :a ){
    			if(i!=j){
	    			GfdNode g = gfdTree.patterns_Map.get(i);
	    			g.extend = false;
    			}
    		}
    	}
    }
				
	public void checkPatternsAndGenerateWorkUnits()	{
		WorkUnit w = new WorkUnit();
		for(int pId :tmpPatternCheck){
			GfdNode g = gfdTree.patterns_Map.get(pId);
			for(GfdNode t:g.children){
				if(t.extend){
			
					int id = dfs2Id.get(t.edgePattern);
					Pair<Integer,Pair<Integer,Integer>> pair = new 
							Pair<Integer,Pair<Integer,Integer>>(id,t.addNode);
				    w.edgeIds.put(t.pId, pair);
				    w.oriPatternId = t.pId;
				
				}
				
			}
			workunits.add(w);
			
		}
	}
							
							
				
							/*
							//add isomorphic checking
							flagExtendP = true;
							for(GfdNode t: g.children && t.extend){
							
								t.w.oriPatternId = g.parent.key;
								t.w.isPatternCheck = true;;
								workunits.add(t.w);
								*/
		
	
	//partition to check isomorphism;
	HashMap<String, IntSet> cluster = new HashMap<String, IntSet>();
	public void classifyLayerGfds(){
		cluster.clear();
		for(int i: layerGfds){
			String s = gfdTree.patterns_Map.get(i).orderId;
			if(!cluster.containsKey(s)){
				cluster.put(s, new IntOpenHashSet());
			}
			cluster.get(s).add(i);
		}
			//suppose edgepattern has number, 
	}
	
	public void assignIsoWOrkUnits() throws RemoteException{
		PriorityQueue<WorkUnit> workloads = new PriorityQueue<WorkUnit>();
		
		HashMap<Integer,Graph<VertexString, TypedEdge>> works = new HashMap<Integer,Graph<VertexString, TypedEdge>>();
		
		
		Set<WorkUnit> workloadIso = new HashSet<WorkUnit>();
		for(String s :cluster.keySet()){
			works.clear();
			for(int i: cluster.get(s)){
				works.put(i, gfdTree.patterns_Map.get(i).pattern);
			}
			WorkUnit w = new WorkUnit(works);
			workloads.add(w);
		}
		
		Bpar bParInstance = new Bpar();
		int machineNum = workerProxyMap.size();

		Stat.getInstance().totalWorkUnit = workloads.size();

		Int2ObjectMap<Set<WorkUnit>> assignment = bParInstance.makespan(machineNum,
				workloads);
		log.debug("finished assigment.");

		for (int machineID : assignment.keySet()) {
			// here machineID = partitionID
			String workerID = partitionWorkerMap.get(machineID);
			ParDisWorkerProxy workerProxy = workerProxyMap.get(workerID);
			workerProxy.setWorkUnits(assignment.get(machineID));
			log.info("now sent BPAR assigment for machine " + machineID + " on " + workerID);

		}
	}

		    

	
	
		
	public void verifyPatternAndGenerateWorkUnits(SuppResult finalResult) throws RemoteException{
		           // extendPatterns.clear();
					boolean flagExtend = false;
					
					log.debug("begin to verify pattern support");
					for(int pId :finalResult.pivotMatchP.keySet()){
						GfdNode g = gfdTree.patterns_Map.get(pId);
					    double supp = ((double) finalResult.pivotMatchP.get(pId).size())/finalResult.nodeNum;
					    g.supp = supp;
					    if(supp == 0){
			
					    	negGfdP.add(pId);
							//negCands.add(pId);
						}
						if(supp >= Params.VAR_SUPP){
							g.literDom = finalResult.literDom.get(pId);
							g.varDom = finalResult.varDom.get(pId);
					        // end one gfdNode
							flagExtend = true;
							
							
							//for disconnected
							SimP simp = new SimP(pId,supp,g.nodeNum);
							extendPatterns.add(simp);
							/////////////////
							
							Int2ObjectMap<Condition> conditions = new Int2ObjectOpenHashMap<Condition>();
							for(LiterNode tc : g.ltree.getRoot().children){
								conditions.put(tc.cId, tc.dependency);
							}
							WorkUnit w = new WorkUnit(g.pId,conditions,true);
							workunits.add(w);
							
						}
					}
				
					//processNegativePCands();
					//for disconnected
					/*
					if(!extendPatterns.isEmpty()){
						Collections.sort(extendPatterns);
						distree.extendTree(extendPatterns);
						
					}*/
					if(flagExtend == false){
						distree.disConnectedGFD(extendPatterns);
						finishLocalCompute();
						log.debug("all process done!");
						this.shutdown();
					}
				}
	
	
	
	private void assignWorkunitsToWorkers() throws RemoteException {

		log.debug("begin assign work units to workers.");
		log.debug("workload size" + workunits.size());

		long assignStartTime = System.currentTimeMillis();

		int machineNum = workerProxyMap.size();
		//Stat.getInstance().totalWorkUnit = workunits.size();

		Int2ObjectMap<Set<WorkUnit>> assignment = new 
				Int2ObjectOpenHashMap<Set<WorkUnit>>();
		//Int2ObjectMap<Int2ObjectMap<IntSet>> prefetchRequest = new Int2ObjectOpenHashMap<Int2ObjectMap<IntSet>>();
		//Int2ObjectMap<Set<CrossingEdge>> crossingEdges = new Int2ObjectOpenHashMap<Set<CrossingEdge>>();
		Random r = new Random();

		//log.debug("should be very quick");
		//for (WorkUnit wu : workunits) {
			for(int assignedMachine = 0; assignedMachine  < machineNum ;assignedMachine ++){
				assignment.put(assignedMachine, workunits);
			}
		log.debug("job assignment finished. begin to dispatch.");

		for (int machineID : assignment.keySet()) {
			String workerID = partitionWorkerMap.get(machineID);
			ParDisWorkerProxy workerProxy = workerProxyMap.get(workerID);
			workerProxy.setWorkUnits(assignment.get(machineID));
		}
		localStartTime = System.currentTimeMillis();
		Stat.getInstance().jobAssignmentTime = (localStartTime - assignStartTime) * 1.0 / 1000;
	}

}
