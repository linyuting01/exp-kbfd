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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
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
import java.util.Queue;
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
	
	
	
	
	//public Set<Pair<Integer,Integer>> gfdResults = new HashSet<Pair<Integer,Integer>>();
	//for negative gfds;
	//public IntSet negCands = new IntOpenHashSet();
	
	
	//public IntSet negGfdP = new IntOpenHashSet();
	
	//public Set<Pair<Integer,Condition>> negGfdXF = new HashSet<Pair<Integer,Condition>>();
	public Map<DFS,Integer> dfs2Id = new HashMap<DFS,Integer>();
	public Int2ObjectMap<DFS> id2Dfs = new Int2ObjectOpenHashMap<DFS>();
	
	Set<GFD2> connectedGfds = new HashSet<GFD2>();
	Set<GFD2> negGfds = new HashSet<GFD2>();
	public Set<GFD2> disConnectedGfds = new HashSet<GFD2>();
	
	
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
		log.debug("Processing graph = " + KV.DATASET);
	
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
	public synchronized void localComputeCompleted(String workerID, boolean activeWorkerIDs)
			throws RemoteException {

		log.info("receive acknowledgement from worker " + workerID + "\n saying activeWorkers: "
				+ activeWorkerIDs);

		this.activeWorkerSet.add(workerID);
		
		

	}

	public void finishLocalCompute() throws RemoteException {
		//process disConnected patterns
		Stat.getInstance().findGfdsTime =  (System.currentTimeMillis() - wholeStartTime) * 1.0 / 1000;
		long disStartTime = System.currentTimeMillis();
		distree.disConnectedGFD(extendPatterns,gfdTree);
		disConnectedGfds = distree.disConnectedGfds;
		Stat.getInstance().findDisConnectedGfdsTime = (System.currentTimeMillis() - disStartTime) * 1.0 / 1000;
		//output final results;
		Stat.getInstance().totalTime = (System.currentTimeMillis() - wholeStartTime) * 1.0 / 1000;
		getResult();
		
		
		this.shutdown();
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
				//log.debug(resultMap.values().size());
				finalResult.assemblePartialResults(resultMap.values());
				//log.debug(finalResult.pivotMatchP.size());
				//log.debug("graph node num : " +finalResult.nodeNum);
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
		log.debug("id2Dfs size" +id2Dfs.size() + " " + i);
		WorkUnit w = new WorkUnit(id2Dfs);
		workunits.add(w);	
	}
	
	
	
	
	public void extendAndDistributeWorkUnits(SuppResult finalResult) throws RemoteException{
		workunits.clear();
		if(this.superstep == 1){
			createIdforDfs(finalResult);
			assignWorkunitsToWorkers();
		}
		else{
			if(this.superstep == 2){
				intialExtendAndGenerateWorkUnits(finalResult);
				assignWorkunitsToWorkers();
			}
			else{
				if(finalResult.checkGfd ){
					verifyGfdAndGenerateWorkUnits(finalResult);
				}
				//patternCheck
				if(finalResult.extendPattern){
					verifyPatternAndGenerateWorkUnits(finalResult);
					assignWorkunitsToWorkers();
				}
				if(finalResult.isIsoCheck){
					isoCheckProcess(finalResult);
					generateWorkUnitsForPatternCheck(finalResult);
					assignWorkunitsToWorkers();
						
				}
				if(!finalResult.checkGfd && !finalResult.extendPattern && !finalResult.isIsoCheck ){
					finishLocalCompute();
				}
			}
		}

		
	}
	

	public void intialExtendAndGenerateWorkUnits(SuppResult finalResult){
		log.debug("begin compute support of edge pattern and extend condition y");
		for(int s :finalResult.pivotMatchP.keySet()){	
			//log.debug("node num" +finalResult.nodeNum);
			int supp = finalResult.pivotMatchP.get(s).size();
			log.debug("supp od rdgePattern"+ supp);
			if(supp >= Params.VAR_SUPP){
				//log.debug("supp value " + supp);
				/////////////////////////////////
				//revise;
				DFS dfs = id2Dfs.get(s);
				edgePattern.add(dfs);
			//	log.debug("edgePattern size" +edgePattern.size());
			}		
		}
		
		
		gfdTree.extendRoot(edgePattern, dfs2Id,finalResult);
		
		log.debug("edgePattern size" + edgePattern.size());
		
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
	

	List<SimP> extendPatterns = new ArrayList<SimP>();
	IntSet tmpPatternCheck = new IntOpenHashSet();
	
	public void verifyGfdAndGenerateWorkUnits(SuppResult finalResult) throws RemoteException{
          
			log.debug("begin to verify gfd and extend condition X and produce workunit for next step.");
			
			//begin to create workunit
			verifyGfds(finalResult);
			
			boolean flagExtend = generateWorkUnitforGfdCheck(finalResult);
			log.debug(flagExtend);
			if(!flagExtend){
				log.debug("no condition more, begin to extend pattern");
				boolean flagExtendP = generateAndAssignWorkUnitforIsoCheck(finalResult);
				
				if(!flagExtendP){
					log.debug("all process done!");
					finishLocalCompute();
				}	
			}
	}

	private void addConenectedGfd(GfdNode g, LiterNode t){
		GFD2 gfd = new GFD2(g.pattern,t.dependency);	
		connectedGfds.add(gfd);
		t.isSat = true;	
		g.ltree.addNegCheck(t);
	}
	private void verifyGfds(SuppResult finalResult){
		for(Entry<Integer, Int2ObjectMap<IntSet>> entry: finalResult.pivotMatchGfd.entrySet()){
			int pId = entry.getKey();
			for(Entry<Integer,IntSet> entry2 :finalResult.pivotMatchGfd.get(pId).entrySet()){
				int cId = entry2.getKey();
				GfdNode g = gfdTree.patterns_Map.get(pId);
				LiterNode t = g.ltree.condition_Map.get(cId);
				
				int supp =  entry2.getValue().size();
				t.supp = supp;
				t.pivotMatch = entry2.getValue();
				//log.debug("supp value " + supp);//
				
				//log.debug("check negative gfd ");//
				if(t.negCheck){
					t.extend = false;
					if(supp == 0){
						GFD2 gfd = new GFD2(g.pattern,t.dependency);	
						negGfds.add(gfd);
					}
				}
				
				if(!t.negCheck){
					//is a minimum gfd; check negative, update liter and var dom;
					if(supp >= Params.VAR_SUPP){
						t.extend = true;
						log.debug("supp: " + supp);//
						if(finalResult.satCId.get(pId).get(cId)){
							addConenectedGfd(g,t);
						}
						else{
							//log.debug(t.children.size());
							if(t.literNum < Params.VAR_LITERNUM){
								g.ltree.extendNode(t);
						}
							
						}
					}
					
				}
			}
		}
			
	}
	
		
			
	private boolean generateWorkUnitforGfdCheck(SuppResult finalResult) throws RemoteException{
		boolean flagExtend = false;
		//log.debug(flagExtend);
		for(int pId: finalResult.pivotMatchGfd.keySet()){
			GfdNode g = gfdTree.patterns_Map.get(pId);
			Int2ObjectMap<Condition> conditions = new Int2ObjectOpenHashMap<Condition>();
			for(int cId: finalResult.pivotMatchGfd.get(pId).keySet()){
				
				LiterNode t = g.ltree.condition_Map.get(cId);
				//log.debug(t.children.size());
				if(t.children!=null && t.extend ){
					if(t.children.size()!= 0){
						//log.debug("t chidrensize" + t.children.size());
						flagExtend = true;
						for(LiterNode tc : t.children){
							conditions.put(tc.cId, tc.dependency);
						}
					
					}			
				}
			}
			WorkUnit w = new WorkUnit(g.pId,conditions,true);
			workunits.add(w);
    	}
		if(flagExtend){
			assignWorkunitsToWorkers();
		}
		log.debug(flagExtend);
		return flagExtend;
	}
	
	
	
	
	private boolean generateAndAssignWorkUnitforIsoCheck(SuppResult finalResult) throws RemoteException{
			
		boolean flagExtendP = false;
		Set<Integer> layerGfds = new HashSet<Integer>();
		
		tmpPatternCheck.clear();
		
		for(int pId:finalResult.pivotMatchGfd.keySet()){
			GfdNode g = gfdTree.patterns_Map.get(pId);
			if(g.nodeNum < Params.var_K && g.extend){
				gfdTree.extendGeneral(edgePattern, g);
			}
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
			return flagExtendP;
		}
		
		HashMap<String, IntSet> cluster1 = new HashMap<String, IntSet>();
		HashMap<String, IntSet> cluster = new HashMap<String, IntSet>();
		for(int i: layerGfds){
			String s = gfdTree.patterns_Map.get(i).orderId;
			if(!cluster1.containsKey(s)){
				cluster1.put(s, new IntOpenHashSet());
			}
			cluster1.get(s).add(i);
		}
		
		for(Entry<String,IntSet> entry : cluster1.entrySet()){
			if(entry.getValue().size()>1){
				cluster.put(entry.getKey(), entry.getValue());
			}
		}
		
		if(cluster.isEmpty()){
			
			generateWorkUnitsForPatternCheck(finalResult);
			assignWorkunitsToWorkers();
		}
		
		else{
			//HashMap<Integer,Graph<VertexString, TypedEdge>> works = new 
					//HashMap<Integer,Graph<VertexString, TypedEdge>>();
			
			for(String s :cluster.keySet()){
				for(int i: cluster.get(s)){
					for(int j : cluster.get(s)){
						if(i<j){
							Pair<Graph<VertexString, TypedEdge>,Graph<VertexString, TypedEdge>> pair = 
									new Pair<Graph<VertexString, TypedEdge>,
									Graph<VertexString, TypedEdge>> (gfdTree.patterns_Map.get(i).pattern,
											gfdTree.patterns_Map.get(j).pattern);
							Pair<Integer,Integer> pairId = new Pair<Integer,Integer>(i,j);
							WorkUnit w = new WorkUnit(pair,pairId);
							workunits.add(w);
							
						}                   
					}
					
				}
			}
			log.debug("isoWorkUnit" +workunits.size());
			assignIsoWOrkUnits();
		}
		return flagExtendP;
					
	}
	
	
				
	public void generateWorkUnitsForPatternCheck(SuppResult r)	{
		//isoCheckProcess(r);
		log.debug("begin to generate work units for pattern check" );
		WorkUnit w = new WorkUnit();
		for(int pId :tmpPatternCheck){
			GfdNode g = gfdTree.patterns_Map.get(pId);
			for(GfdNode t:g.children){
				if(t.extend){
					//gfdTree.extendGeneralNode(edgePattern, t);
			        DFS dfs = t.edgePattern.findDFS();
					int id = dfs2Id.get(dfs);
					Pair<Integer,Pair<Integer,Integer>> pair = new 
							Pair<Integer,Pair<Integer,Integer>>(id,t.addNode);
				    w.edgeIds.put(t.pId, pair);
				    w.oriPatternId = t.pId;
				    w.isPatternCheck = true;
				
				}
				
			}
			workunits.add(w);
			
		}
	
	}
	

    public void isoCheckProcess(SuppResult r){
    	int j = 0;
    	for(Entry<Integer,IntSet> entry: r.isoResult.entrySet()){
    		GfdNode g = gfdTree.patterns_Map.get(entry.getKey());
    		g.isopatterns = new IntOpenHashSet(entry.getValue());
    		for(int i: entry.getValue()){
    			GfdNode g1 = gfdTree.patterns_Map.get(i);
    			g1.extend = false;
    		}
    	}
    	
    }
							
		
	public void verifyPatternAndGenerateWorkUnits(SuppResult finalResult) throws RemoteException{
		           // extendPatterns.clear();
		
					boolean flagExtend = false;

					
					log.debug("begin to verify pattern support");
					for(int pId :finalResult.pivotMatchP.keySet()){
						GfdNode g = gfdTree.patterns_Map.get(pId);
					    int supp = finalResult.pivotMatchP.get(pId).size();
					    g.supp = supp;
					    log.debug("patterns support" + g.supp);
					    if(supp == 0){
                            GFD2 gfd = new GFD2(g.pattern,null);
							negGfds.add(gfd);
						}
						if(supp >= Params.VAR_SUPP){
							g.extend = true;
							flagExtend = true;
							g.literDom = finalResult.literDom.get(pId);
							g.varDom = finalResult.varDom.get(pId);
								
							//for disconnected
							SimP simp = new SimP(pId,supp,g.nodeNum);
							extendPatterns.add(simp);
							/////////////////
							g.ltree.extendNode(g.ltree.getRoot());
							
								Int2ObjectMap<Condition> conditions = new Int2ObjectOpenHashMap<Condition>();
								for(LiterNode tc : g.ltree.getRoot().children){
									conditions.put(tc.cId, tc.dependency);
								}
								WorkUnit w = new WorkUnit(g.pId,conditions,true);
								workunits.add(w);
							}
							
						}
					
					//log.debug("flagExtend" + flagExtend);
				
					if(!flagExtend){
						log.debug("all process done!");
						finishLocalCompute();		
					}
				}

	
	private void assignWorkunitsToWorkers() throws RemoteException {

		Stat.getInstance().sc2wcommunicationData += RamUsageEstimator.sizeOf(workunits);
        Stat.getInstance().totalWorkUnit += workunits.size();
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
	
	

	private void assignIsoWOrkUnits() throws RemoteException{
		
		
		Queue<WorkUnit> workload = new LinkedList<WorkUnit>();
		int machineNum = workerProxyMap.size();
		for(WorkUnit w : workunits){
			workload.add(w);
		}

		//Stat.getInstance().totalWorkUnit = workloads.size();

		Int2ObjectMap<Set<WorkUnit>> assignment = new Int2ObjectOpenHashMap<Set<WorkUnit>>();
		log.debug("finished assigment.");
		
			for(int assignedMachine = 0; assignedMachine  < machineNum ;assignedMachine ++){
				if(!workload.isEmpty()){
					WorkUnit w = workload.poll();
					if(!assignment.containsKey(assignedMachine)){
						assignment.put(assignedMachine, new HashSet<WorkUnit>());
					}
					assignment.get(assignedMachine).add(w);		
			}
		}
		  // for there is not enough work for all workers
			WorkUnit w = new WorkUnit();
			w.isIsoCheck = true;
			for(int assignedMachine = 0; assignedMachine  < machineNum ;assignedMachine ++){
				if(!assignment.containsKey(assignedMachine)){
					assignment.put(assignedMachine, new HashSet<WorkUnit>());
				}
				assignment.get(assignedMachine).add(w);
			}
			

		for (int machineID : assignment.keySet()) {
			// here machineID = partitionID
			String workerID = partitionWorkerMap.get(machineID);
			ParDisWorkerProxy workerProxy = workerProxyMap.get(workerID);
			workerProxy.setWorkUnits(assignment.get(machineID));
			log.info("now sent BPAR assigment for machine " + machineID + " on " + workerID);

		}
	}
	//partition to check isomorphism;
	

	private void getResult(){
		
	    String filename = KV.RESULT_FILE_PATH ;
	
		//public IntSet negGfdP = new IntOpenHashSet();
		//public Set<GFD2> disConnectedGfds = new HashSet<GFD2>();
		//public Set<Pair<Integer,Condition>> negGfdXF = new HashSet<Pair<Integer,Condition>>();
		log.debug("Write final result file ");

		PrintWriter writer;
		try {
			File file1 = new File(filename+".neg");
			writer = new PrintWriter(file1);
			for(GFD2 gfd : negGfds){
				log.debug(negGfds.size());
				writer.println(gfd.tofileC());
			}
			writer.flush();
			writer.close();
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}
			
		try{
			File file1 = new File(filename+".gfds");
			writer = new PrintWriter(file1);
			log.debug(connectedGfds.size());
			for(GFD2 gfd : connectedGfds){
				writer.println(gfd.tofileC());
			}
			writer.flush();
			writer.close();
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try{
			File file1 = new File(filename+".disconnected");
			writer = new PrintWriter(file1);
			for(GFD2 gfd : disConnectedGfds){
				writer.println(gfd.tofileDC());
			}
			writer.flush();
			writer.close();
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try{
			File file1 = new File(filename+".info");
			writer = new PrintWriter(file1);
			
		    writer.println(Stat.getInstance().getInfo());
			
			writer.flush();
			writer.close();
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
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
	
	
	

}
