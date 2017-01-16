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
import inf.ed.graph.structure.adaptor.VertexOString;
import inf.ed.graph.structure.adaptor.VertexString;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
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
public class ParDisCoordinator extends UnicastRemoteObject implements Worker2Coordinator, Client2Coordinator {

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

	public Set<Pair<String, String>> negGfdXCands = new HashSet<Pair<String, String>>();
	GfdTree gfdTree = new GfdTree();

	List<DFS> edgePattern = new ArrayList<DFS>();
	public Int2ObjectMap<IntList> attr_Map = new Int2ObjectOpenHashMap<IntList>();

	public HashMap<String, Integer> labelId = new HashMap<String, Integer>();

	DisconnectedTree distree = new DisconnectedTree();

	// public Set<Pair<Integer,Integer>> gfdResults = new
	// HashSet<Pair<Integer,Integer>>();
	// for negative gfds;
	// public IntSet negCands = new IntOpenHashSet();

	// public IntSet negGfdP = new IntOpenHashSet();

	// public Set<Pair<Integer,Condition>> negGfdXF = new
	// HashSet<Pair<Integer,Condition>>();
	public Map<DFS, Integer> dfs2Id = new HashMap<DFS, Integer>();
	public Int2ObjectMap<DFS> id2Dfs = new Int2ObjectOpenHashMap<DFS>();

	Set<GFD2> connectedGfds = new HashSet<GFD2>();
	Set<GFD2> negGfds = new HashSet<GFD2>();
	public Set<GFD2> disConnectedGfds = new HashSet<GFD2>();

	// HashMap<Integer, String> attr_Map = new HashMap<Integer,String>();

	// Set<String> dom = new HashSet<String>();

	static Logger log = LogManager.getLogger(ParDisCoordinator.class);

	// for literal extention;

	Int2ObjectMap<Int2ObjectMap<String>> literCands = new Int2ObjectOpenHashMap<Int2ObjectMap<String>>();
	// var cand only for disconnected

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
	 * Sets the active worker set. currentLocalComputeTaskQueue
	 * 
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
	public Worker2Coordinator register(Worker worker, String workerID, int numWorkerThreads) throws RemoteException {

		log.debug("Worker " + workerID + " registered and ready to work!");
		totalWorkerThreads.getAndAdd(numWorkerThreads);
		ParDisWorkerProxy workerProxy = new ParDisWorkerProxy(worker, workerID, numWorkerThreads, this);
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
			workerProxy.setWorkerPartitionInfo(virtualVertexPartitionMap, partitionWorkerMap, workerMap);
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
		Params.RUN_MODE = Integer.parseInt(args[2].trim());

		// Integer.parseInt(args[2].trim());
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

		// this.activeWorkerSet.clear();
	}

	public void finishLocalCompute() throws RemoteException {

		Stat.getInstance().totalTime = (System.currentTimeMillis() - wholeStartTime) * 1.0 / 1000;
		getResult();
		gfdTree.writeToFile("gfdTree.dat");
		log.info("the total time  = " + Stat.getInstance().totalTime);

		this.shutdown();
	}

	private void sendPartitionInfo() throws RemoteException {
		for (Map.Entry<String, ParDisWorkerProxy> entry : workerProxyMap.entrySet()) {
			ParDisWorkerProxy workerProxy = entry.getValue();
			workerProxy.setWorkerPartitionInfo(null, partitionWorkerMap, workerMap);
		}
	}

	public void loadLiteralInfo() {
		String filename = KV.GRAPH_FILE_PATH + ".freq";
		try {
			File literFile = new File(filename);
			LineIterator it = FileUtils.lineIterator(literFile, "UTF-8");
			try {
				while (it.hasNext()) {
					String line = it.nextLine();
					String[] tmpt = line.split("\t");
					int pId = Integer.parseInt(tmpt[0].trim());
					int attrId = Integer.parseInt(tmpt[1].trim());
					if (!literCands.containsKey(pId)) {
						literCands.put(pId, new Int2ObjectOpenHashMap<String>());
					}
					literCands.get(pId).put(attrId, tmpt[2].trim());
					if (!attr_Map.containsKey(pId)) {
						attr_Map.put(pId, new IntArrayList());
					}
					attr_Map.get(pId).add(attrId);
				}
			} finally {
				LineIterator.closeQuietly(it);
			}
		} catch (IOException e) {
			log.error("load liter file failed.");
			e.printStackTrace();
		}
		return;

	}

	public void loadEdgePatternInfo() {

		String filename = KV.GRAPH_FILE_PATH + ".edge";
		try {
			File literFile = new File(filename);
			LineIterator it = FileUtils.lineIterator(literFile, "UTF-8");
			try {
				int i = 1;
				while (it.hasNext()) {
					String line = it.nextLine();
					String[] tmpt = line.split("\t");
					String s = tmpt[0].trim();
					DFS dfs = Fuc.getDfsFromString(s);
					// log.debug("begin to create id for edgePatterns");
					//
					// log.debug(dfs.toString());
					edgePattern.add(dfs);
					// dfs2Id.put(dfs, i);
					// id2Dfs.put(i, dfs);
					// i++;
				}

			} finally {
				LineIterator.closeQuietly(it);
			}
		} catch (IOException e) {
			log.error("load liter file failed.");
			e.printStackTrace();
		}

		Collections.sort(edgePattern);
		log.debug("begin to initial e" + "xtend gfd tree");

		gfdTree.extendRoot(edgePattern);

		log.debug("loaded freq-edge.size = " + gfdTree.dfs2Ids.size());
		edgePattern.clear();

		// gfdTree.dfs2Ids = dfs2Id;
		// log.debug("id2Dfs size" +id2Dfs.size());
		WorkUnit w = new WorkUnit(gfdTree.dfs2Ids);
		w.nodeAttr_Map = attr_Map;
		log.debug(attr_Map.size());
		workunits.add(w);
		// log.debug();

		return;

	}

	public void preProcess() throws RemoteException {

		wholeStartTime = System.currentTimeMillis();
		// read border nodes;

		// distributed partition;

		assignDistributedPartitions();
		sendPartitionInfo();

		Stat.getInstance().getInputFilesLocalAndDistributedTime = (System.currentTimeMillis() - wholeStartTime) * 1.0
				/ 1000;

		// List<GFD2> queries = readGFDFromDir();
		// log.info("load " + queries.size() + " gfds from: " +
		// KV.QUERY_DIR_PATH);

		this.workerAcknowledgementSet.clear();
		this.workerAcknowledgementSet.addAll(this.activeWorkerSet);
		loadLiteralInfo();
		loadEdgePatternInfo();

		assignWorkunitsToWorkers();
		nextLocalCompute();

		// process();

		// sendGFDs2Workers(queries);
	}

	@Override
	public void process() throws RemoteException {

		assignWorkunitsToWorkers();
		workunits.clear();
		nextLocalCompute();
	}

	public synchronized void receivePartialResults(String workerID, Map<Integer, Result> mapPartitionID2Result)
			throws RemoteException {

		workunits.clear();
		log.debug("current ack set = " + this.workerAcknowledgementSet.toString());
		log.debug("receive partitial results = " + workerID);

		for (Entry<Integer, Result> entry : mapPartitionID2Result.entrySet()) {

			SuppResult partialResult = (SuppResult) entry.getValue();
			log.debug("got from pid = " + entry.getKey() + ", pivot match size = " + partialResult.pivotMatchP.size());
			resultMap.put(entry.getKey(), entry.getValue());
		}

		this.workerAcknowledgementSet.remove(workerID);

		if (this.workerAcknowledgementSet.size() == 0) {

			/** receive all the partial results, assemble them. */
			log.info("assemble the result");
			SuppResult finalResult = new SuppResult();
			log.debug("begin to assembel result! Results size");
			// log.debug(finalResult.pivotMatchP.size());
			// log.debug(resultMap.values().size());
			
			finalResult.assemblePartialResults(resultMap.values());
			
			log.debug("total pivot match size = " + finalResult.pivotMatchP.size());
			log.debug("total pivot satisfied match size = " + finalResult.freqPattern.size());
			// log.debug(finalResult.toString());
			// log.debug(finalResult.pivotMatchP.size());
			// log.debug("graph node num : " +finalResult.nodeNum);
			log.debug("assembel done!");
			// ws.clear();
			extendAndDistributeWorkUnits(finalResult);
			// log.debug("has created the new workunit");

			try {
				log.debug("begin to process.");
				process();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	// put them into paraResult;
	HashMap<Integer, Set<String>> literDom;
	HashMap<Integer, IntSet> varDom;

	// List<DFS> edgePattern = new ArrayList<DFS>();
	// HashMap<String,List<WorkUnit>> ws = new HashMap<String,List<WorkUnit>>();
	// superstep 1;

	public Int2DoubleMap avgWork = new Int2DoubleOpenHashMap();
	// public boolean isEstimateBanlance = false;

	public void extendAndDistributeWorkUnits(SuppResult finalResult) throws RemoteException {
		log.debug("finalresult before expending = " + finalResult.freqPattern.size());
		workunits.clear();
		if (finalResult.checkGfd) {
			log.info("begin verify gfd and generate workunits");
			verifyGfdAndGenerateWorkUnits(finalResult);
			log.info("end verify gfd and generate workunits");
		}
		// patternCheck
		if (finalResult.extendPattern) {
			log.debug("begin: finalresult before expending = " + finalResult.freqPattern.size());
			log.info("begin verify pattern and generate workunits");
			avgWork.clear();
			for (Entry<Integer, Integer> entry : finalResult.patternMatchesNum.entrySet()) {
				int key = entry.getKey();
				int sum = entry.getValue();
				double avg = (double) sum / Params.N_PROCESSORS;
				avgWork.put(key, avg);
			}
			// isEstimateBanlance = true;
			
			verifyPatternAndGenerateWorkUnits(finalResult);
			log.info("end verify gfd and generate workunits");
		}
		if (finalResult.isIsoCheck) {
			log.info("begin verify isomorphism and generate workunits");
			isoCheckProcess(finalResult);
			generateWorkUnitsForPatternCheck(finalResult);
			log.info("end verify isomorphism and generate workunits");

		}

		if (!finalResult.isFirst && !finalResult.checkGfd && !finalResult.extendPattern && !finalResult.isIsoCheck) {
			finishLocalCompute();
		}

	}

	IntSet pIdKeep = new IntOpenHashSet();
	/*
	 * public void intialExtendAndGenerateWorkUnits(SuppResult finalResult){
	 * log.debug("begin compute support of edge pattern and extend condition y"
	 * ); for(int s :finalResult.pivotMatchP.keySet()){ //log.debug("node num"
	 * +finalResult.nodeNum); int supp = finalResult.pivotMatchP.get(s).size();
	 * log.debug("supp od rdgePattern"+ supp); if(supp >= Params.VAR_SUPP){
	 * //log.debug("supp value " + supp); /////////////////////////////////
	 * //revise; DFS dfs = id2Dfs.get(s); edgePattern.add(dfs); // log.debug(
	 * "edgePattern size" +edgePattern.size()); } }
	 * 
	 * 
	 * gfdTree.extendRoot(edgePattern, dfs2Id,finalResult); for(GfdNode gc :
	 * gfdTree.getRoot().children){ pIdKeep.add(gc.pId); gc.extend = true; }
	 * 
	 * log.debug("edgePattern size" + edgePattern.size());
	 * 
	 * log.debug("begin to extend dependencied empty->y and create " +
	 * "workunit for edge pattern with empty -> y ");
	 * 
	 * for(GfdNode g:gfdTree.getRoot().children){
	 * 
	 * g.ltree.extendNode(g.ltree.getRoot(), literCands);
	 * Int2ObjectMap<Condition> conditions = new
	 * Int2ObjectOpenHashMap<Condition>(); if(g.ltree.getRoot().children !=
	 * null|| !g.ltree.getRoot().children.isEmpty()){ for(LiterNode
	 * t:g.ltree.getRoot().children){ if(!t.dependency.isEmpty()){
	 * conditions.put(t.cId, t.dependency); log.debug("pattern = " + g.pId +
	 * "  Condition Id= " +t.cId ); int a =
	 * g.ltree.condition_Map.get(t.cId).supp; } } if(!conditions.isEmpty()){
	 * WorkUnit w = new WorkUnit(g.pId,conditions,true); workunits.add(w); } } }
	 * }
	 */

	List<SimP> extendPatterns = new ArrayList<SimP>();
	IntSet tmpPatternCheck = new IntOpenHashSet();

	public void verifyGfdAndGenerateWorkUnits(SuppResult finalResult) throws RemoteException {

		log.debug("begin to verify gfd and extend condition X and produce workunit for next step.");

		// begin to create workunit
		boolean flagExtend =false;
		if(Params.RUN_MODE == 1){
			verifyGfds1(finalResult);
			flagExtend = generateWorkUnitforGfdCheck1(finalResult);
		}
		else{
			verifyGfds(finalResult);
			flagExtend = generateWorkUnitforGfdCheck(finalResult);
		}
       
	      
		log.debug("flagextend = " + flagExtend);
		if (!flagExtend) {

			log.debug("begin: finalresult before expending = " + finalResult.freqPattern.size());

			log.debug("no condition more, begin to extend pattern");
			log.debug("begin to expend pattern: = " + pIdKeep.size());
			boolean flagExtendP = generateAndAssignWorkUnitforIsoCheck(finalResult);

			if (!flagExtendP) {
				log.debug("all process done!");
				finishLocalCompute();
			}
		}
	}

	private void addConenectedGfd(GfdNode g, LiterNode t) {
		GFD2 gfd = new GFD2(g.pattern, t.dependency);
		connectedGfds.add(gfd);
		t.isSat = true;
		if (g.parent != gfdTree.getRoot()) {
			if (Params.RUN_MODE == 1) {
				g.ltree.addNegCheck(t, -1);
			}
			
			else {
				int n = g.parent.ltree.condition_Map.size();
				g.ltree.addNegCheck(t, n);
			}
		} else {
			g.ltree.addNegCheck(t, -1);
		}

	}

	private void verifyGfds1(SuppResult finalResult) {
		if (!finalResult.pivotMatchGfd.isEmpty()) {
			for (Entry<Integer, Int2ObjectMap<IntSet>> entry : finalResult.pivotMatchGfd.entrySet()) {
				int pId = entry.getKey();
				if (!finalResult.pivotMatchGfd.get(pId).isEmpty()) {
					for (Entry<Integer, IntSet> entry2 : finalResult.pivotMatchGfd.get(pId).entrySet()) {
						// log.debug("pattern = " + pId);

						int cId = entry2.getKey();
						GfdNode g = gfdTree.patterns_Map.get(pId);
						LiterNode t = g.ltree.condition_Map.get(cId);
						int supp = entry2.getValue().size();
						// log.debug(supp);
						t.supp = supp;
						t.pivotMatch = entry2.getValue();
						// log.debug("supp value " + supp);//

						// log.debug("check negative gfd ");//
						if (t.negCheck) {
							t.extend = false;
							if (supp == 0) {
								GFD2 gfd = new GFD2(g.pattern, t.dependency);
								negGfds.add(gfd);
							}
						}

						if (!t.negCheck) {
							// is a minimum gfd; check negative, update liter
							// and var dom;
							t.extend = true;
							g.ltree.extendNode(t, literCands);
						
							if (supp >= Params.VAR_SUPP) {
								
								// log.debug("supp: " + supp);//
								if(finalResult.satCId.containsKey(pId)){
									if(finalResult.satCId.get(pId).containsKey(cId)){
									int unsat = finalResult.satCId.get(pId).get(cId).size();
									
										if (unsat <= Params.VAR_UNSAT) {
											addConenectedGfd(g, t);
										} 
									}
								}
								
							
						}
					

						}
					}
				}
			}
			}
	}
		

	

	private void verifyGfds(SuppResult finalResult) {
        log.debug("there are " + finalResult.freqGfd.size() +"patterns has frequnet gfd");
      
		for (Entry<Integer, IntSet> entry : finalResult.freqGfd.entrySet()) {
			int pId = entry.getKey();
			log.debug("the frequent gfd with pattern Id  = " + pId + ", size == "+ finalResult.freqGfd.get(pId).size());
			GfdNode g = gfdTree.patterns_Map.get(pId);
			log.debug("the unsatisfiable gfds with pattern Id  = " + pId + ", size == "+ finalResult.freqGfd.get(pId).size());
			for (int cId : entry.getValue()) {
				LiterNode t = g.ltree.condition_Map.get(cId);
				if (finalResult.unSatGfds.containsKey(pId)) {
					if (finalResult.unSatGfds.get(pId).contains(cId)) {
						t.extend = true;
						if (t.literNum < Params.VAR_LITERNUM) {
						
						
								//if (g.parent == gfdTree.getRoot()) {
									g.ltree.extendNode(t, literCands);
									/*
								} else {
									g.ltree.extendNodeForPrune(t, literCands);
								}*/
							
						}
					} else {
						if(finalResult.satCId.containsKey(pId)){
							if(finalResult.satCId.get(pId).containsKey(cId)){		
							
							log.debug("found connected gfds !");
							addConenectedGfd(g, t);}
						}
					}
						
					
				} else {
					log.debug("check negative gfd");
					if(finalResult.pivotMatchGfd.containsKey(pId)){
						if(finalResult.pivotMatchGfd.containsKey(cId)){
				
					if (finalResult.pivotMatchGfd.get(pId).get(cId).size() == 0) {
						if (t.negCheck) {
							GFD2 gfd = new GFD2(g.pattern, t.dependency);
							negGfds.add(gfd);
						}
						t.extend = false;
					}
						}
					}
				}

			}
		}

	}

	private boolean generateWorkUnitforGfdCheck1(SuppResult finalResult) throws RemoteException {
		workunits.clear();
		boolean flagExtend = false;
		// log.debug(flagExtend);
		for (int pId : finalResult.satCId.keySet()) {
			GfdNode g = gfdTree.patterns_Map.get(pId);
			Int2ObjectMap<Condition> conditions = new Int2ObjectOpenHashMap<Condition>();
			for (int cId : finalResult.pivotMatchGfd.get(pId).keySet()) {

				LiterNode t = g.ltree.condition_Map.get(cId);
				// log.debug(t.children.size());
				if (t.children != null) {
					if (t.children.size() != 0) {
						// log.debug("t chidrensize" + t.children.size());
						flagExtend = true;
						for (LiterNode tc : t.children) {
							conditions.put(tc.cId, tc.dependency);
						}

					}
				}
			}
			if (flagExtend && !conditions.isEmpty()) {
				WorkUnit w = new WorkUnit(g.pId, conditions, true);
				workunits.add(w);
			}
		}
		return flagExtend;
	}

	private boolean generateWorkUnitforGfdCheck(SuppResult finalResult) throws RemoteException {
		workunits.clear();
		boolean flagExtend = false;
        log.debug("has extend gfd done! begin to create workunits to check them");
		for (int pId : finalResult.freqGfd.keySet()) {
			GfdNode g = gfdTree.patterns_Map.get(pId);
			Int2ObjectMap<Condition> conditions = new Int2ObjectOpenHashMap<Condition>();
			for (int cId : finalResult.freqGfd.get(pId)) {

				LiterNode t = g.ltree.condition_Map.get(cId);
				// log.debug(t.children.size());
				if (t.children != null && t.extend) {
					if (t.children.size() != 0) {
						// log.debug("t chidrensize" + t.children.size());
						flagExtend = true;
						for (LiterNode tc : t.children) {
							conditions.put(tc.cId, tc.dependency);
						}

					}
				}
				log.debug("possible gfds for pattern " +pId +"size = " + conditions.size());
			}
			if (flagExtend && !conditions.isEmpty()) {
				WorkUnit w = new WorkUnit(g.pId, conditions, true);
				workunits.add(w);
			}
		}
		log.debug("workunits size ( gfds extend from fre-gfds, the fre-gfds size) = " + workunits.size() ); 
		return flagExtend;
	}

	private boolean generateAndAssignWorkUnitforIsoCheck(SuppResult finalResult) throws RemoteException {
		workunits.clear();
		boolean flagExtendP = false;
		IntSet layerGfds = new IntOpenHashSet();

		tmpPatternCheck.clear();
		// log.debug("pIdkeep size = "+ pIdKeep.size());
		for (int pId : pIdKeep) {
			GfdNode g = gfdTree.patterns_Map.get(pId);
			// log.debug(g.extend);
			// log.debug("pattern node num = " + g.nodeNum +"; pattern extend ="
			// +g.extend);
			if (g.nodeNum < Params.var_K && g.extend) {

				gfdTree.extendGeneral(edgePattern, g);
				
				// log.debug("pattern " + g.pId + "has children size = "
				// +g.children.size());
			}
			// log.debug("pattern " +g.pId + "children size= " +
			// g.children.size());
			if (g.children != null) {
				if (!g.children.isEmpty()) {
					flagExtendP = true;
					tmpPatternCheck.add(g.pId);
					for (GfdNode g1 : g.children) {
						// log.debug("pattern " +g.pId + "children pId = " +
						// g1.pId);
						layerGfds.add(g1.pId);
						g1.extend = true;
					}
				}
			}
		}
		log.debug("can extend pattern form these patterns, these pattern size = " + tmpPatternCheck.size());
		log.debug("next layer pattern have extended from pattern in tmpPatternCheck , the  size = " + layerGfds.size());
		if (flagExtendP == false) {
			return flagExtendP;
		}

		HashMap<String, IntSet> cluster1 = new HashMap<String, IntSet>();
		HashMap<String, IntSet> cluster = new HashMap<String, IntSet>();
		for (int i : layerGfds) {
			GfdNode layer =  gfdTree.patterns_Map.get(i);
			String s = gfdTree.patterns_Map.get(i).orderId;
			if (!cluster1.containsKey(s)) {
				cluster1.put(s, new IntOpenHashSet());
				cluster1.get(s).add(i);
			}
			else{
			   cluster1.get(s).add(i);
			  // layer.extend = false; // a trick need to revise
			}
		}

		for (Entry<String, IntSet> entry : cluster1.entrySet()) {
			if (entry.getValue().size() > 1) {
				cluster.put(entry.getKey(), entry.getValue());
			}
		}

		if (cluster.isEmpty()) {
			log.debug("no possible isomorphism patterns, begin to generate workunit to verify pattern!");
			generateWorkUnitsForPatternCheck(finalResult);
			// assignWorkunitsToWorkers();
		}

		else {
			// HashMap<Integer,Graph<VertexString, TypedEdge>> works = new
			// HashMap<Integer,Graph<VertexString, TypedEdge>>();
			log.debug("there are some possible isomorphism patterns, begin to generate workunit to verify ");
			workunits.clear();
			for (String s : cluster.keySet()) {
				for (int i : cluster.get(s)) {
					for (int j : cluster.get(s)) {
						if (i < j) {
							Pair<Graph<VertexString, TypedEdge>, Graph<VertexString, TypedEdge>> pair = new Pair<Graph<VertexString, TypedEdge>, Graph<VertexString, TypedEdge>>(
									gfdTree.patterns_Map.get(i).pattern, gfdTree.patterns_Map.get(j).pattern);
							Pair<Integer, Integer> pairId = new Pair<Integer, Integer>(i, j);
							WorkUnit w = new WorkUnit(pair, pairId);
							w.isIsoCheck = true;
							workunits.add(w);

						}
					}

				}
			}
		   log.debug("have produced isoWorkUnit, size == " +workunits.size());
			// assignIsoWOrkUnits();
		}
		return flagExtendP;

	}

	public void generateWorkUnitsForPatternCheck(SuppResult r) {
		workunits.clear();
		//log.debug(workunits.size());
		// isoCheckProcess(r);
		log.debug("begin to generate work units for extended pattern && and the "
				+ "pattern are extended by tmpPatternCheck, size =  "+ tmpPatternCheck.size());
		
		for (int pId : tmpPatternCheck) {
			GfdNode g = gfdTree.patterns_Map.get(pId);
			for (GfdNode t : g.children) {
				// log.debug("extend pattern pId = " + t.pId + " extend = " +
				// t.extend);
				if(t.extend){
				// t.extend = true;
				DFS dfs = t.edgePattern.findDFS();
				int id = gfdTree.dfs2Ids.get(dfs);
				WorkUnit w = new WorkUnit();
				Pair<Integer, Pair<Integer, Integer>> pair = new Pair<Integer, Pair<Integer, Integer>>(id, t.addNode);
				w.edgeIds.put(t.pId, pair);
				w.oriPatternId = pId;
				w.isPatternCheck = true;
				// log.debug(w.toString());
				workunits.add(w);
				}

			}

		}

		log.debug("finish generating pattern check working unit : pattern extended by tmpPatternChecknum , size =  " + workunits.size());

	}

	public void isoCheckProcess(SuppResult r) {
		int j = 0;
		for (Entry<Integer, IntSet> entry : r.isoResult.entrySet()) {
			GfdNode g = gfdTree.patterns_Map.get(entry.getKey());
			g.isopatterns = new IntOpenHashSet(entry.getValue());
			for (int i : entry.getValue()) {
				GfdNode g1 = gfdTree.patterns_Map.get(i);
				g1.extend = false;
			}
		}

	}
	public void verifyPatternAndGenerateWorkUnits(SuppResult finalResult) throws RemoteException {
		if(Params.RUN_MODE == 1){
			verifyPatternAndGenerateWorkUnits1(finalResult);
		}
		else{
			verifyPatternAndGenerateWorkUnit2(finalResult);
		}
	}

	public void verifyPatternAndGenerateWorkUnits1(SuppResult finalResult) throws RemoteException {
		workunits.clear();
		// extendPatterns.clear();
		pIdKeep.clear();
		boolean flagExtend = false;

		log.debug("begin to verify pattern support");
		for (int pId : finalResult.pivotMatchP.keySet()) {
			GfdNode g = gfdTree.patterns_Map.get(pId);
			int supp = finalResult.pivotMatchP.get(pId).size();
			g.supp = supp;

			if (supp == 0) {
				GFD2 gfd = new GFD2(g.pattern, null);
				negGfds.add(gfd);
			}
			
				if (g.parent == gfdTree.getRoot()) {

					edgePattern.add(g.edgePattern);
				}
				if(g.nodeNum <= Params.var_K){
					flagExtend = true;
					pIdKeep.add(pId);
					g.ltree.extendNode(g.ltree.getRoot(), literCands);
					Int2ObjectMap<Condition> conditions = new Int2ObjectOpenHashMap<Condition>();
					for (LiterNode tc : g.ltree.getRoot().children) {
						conditions.put(tc.cId, tc.dependency);
					}
					WorkUnit w = new WorkUnit(g.pId, conditions, true);
					if (Params.RUN_MODE != 0) {
						w.isAvg = true;
						w.avg = avgWork.get(pId);
					}
					workunits.add(w);
				}
		}


		// log.debug("flagExtend" + flagExtend);

		if (!flagExtend) {
			log.debug("all process done!");
			finishLocalCompute();
		}
	}
	
	
	

	public void verifyPatternAndGenerateWorkUnit2(SuppResult finalResult) throws RemoteException {
		workunits.clear();
		// extendPatterns.clear();
		pIdKeep.clear();
		boolean flagExtend = false;
		log.debug("begin to verify pattern, the frequnet pattern size " + finalResult.freqPattern.size());
		if (!finalResult.freqPattern.isEmpty()) {
			log.debug("begin to create y literal for fre-pattern");
			for (int pId : finalResult.freqPattern) {

				GfdNode g = gfdTree.patterns_Map.get(pId);
				// log.debug("patterns support" + g.supp);
			   
				if (g.parent == gfdTree.getRoot()) {

					edgePattern.add(g.edgePattern);
				}
				g.extend = true;
				flagExtend = true;
				pIdKeep.add(pId);
				
				g.ltree.extendNode(g.ltree.getRoot(), literCands);
				/*
				if (Params.RUN_MODE == 1) {
					
				}
				else {
					// log.debug(arg0);
					if (g.parent == gfdTree.getRoot()) {
						g.ltree.extendNode(g.ltree.getRoot(), literCands);
					} else {
						g.ltree.extendNodeForPrune(g.ltree.getRoot(), literCands);
					}
				}*/
				

				Int2ObjectMap<Condition> conditions = new Int2ObjectOpenHashMap<Condition>();
				for (LiterNode tc : g.ltree.getRoot().children) {
					conditions.put(tc.cId, tc.dependency);
				}
				
				log.debug("finish generate y literal for pattern pId = " + pId + ", y literal size = " +conditions.size());

				WorkUnit w = new WorkUnit(g.pId, conditions, true);
				if (Params.RUN_MODE != 0) {
					w.isAvg = true;
					w.avg = avgWork.get(pId);
				}
				workunits.add(w);
				
			}
			log.debug("finish cretw work unit for checking gfd y literal , the workload size == " +
					workunits.size()+ "it shoud be == pattern size  = " + finalResult.freqPattern.size());


		}
		for (int neg : finalResult.pivotMatchP.keySet()) {
			if (finalResult.pivotMatchP.get(neg).size() == 0) {
				GfdNode g = gfdTree.patterns_Map.get(neg);
				GFD2 gfd = new GFD2(g.pattern, null);
				negGfds.add(gfd);

			}

		}

		if (!flagExtend) {
			log.debug("there is no pattern is frequnet, all process done!");
			finishLocalCompute();
		}
	}

	private void assignWorkunitsToWorkers() throws RemoteException {
		log.debug("begin to assign workunit to workers");
		// for(WorkUnit s : workunits){
		// log.debug(s.toString());
		// }
		WorkUnit s = Fuc.getRandomWorkUnit(workunits);
		boolean isIsoCheck = s.isIsoCheck;
		if (isIsoCheck) {
			assignIsoWOrkUnits();
		} else {
			assignWorkunitsToWorkersAVG();
		}

	}

	private void assignWorkunitsToWorkersAVG() throws RemoteException {

		Stat.getInstance().sc2wcommunicationData += RamUsageEstimator.sizeOf(workunits);
		Stat.getInstance().totalWorkUnit += workunits.size();
		// log.debug("begin assign work units to workers.");
		// log.debug("workload size" + workunits.size());

		// if(isEstimateBanlance){
		// if(workunits.size() > 0){
		// for(WorkUnit w:workunits){
		// w.avgMatch = avgWork;
		// w.isAvg = true;
		// isIsoCheck = w.isIsoCheck;
		// break;
		// }
		// }
		/// }

		long assignStartTime = System.currentTimeMillis();

		int machineNum = workerProxyMap.size();
		// Stat.getInstance().totalWorkUnit = workunits.size();

		Int2ObjectMap<Set<WorkUnit>> assignment = new Int2ObjectOpenHashMap<Set<WorkUnit>>();
		// Int2ObjectMap<Int2ObjectMap<IntSet>> prefetchRequest = new
		// Int2ObjectOpenHashMap<Int2ObjectMap<IntSet>>();
		// Int2ObjectMap<Set<CrossingEdge>> crossingEdges = new
		// Int2ObjectOpenHashMap<Set<CrossingEdge>>();
		Random r = new Random();

		// log.debug("should be very quick");
		// for (WorkUnit wu : workunits) {
		for (int assignedMachine = 0; assignedMachine < machineNum; assignedMachine++) {
			assignment.put(assignedMachine, workunits);
		}
		// log.debug("job assignment finished. begin to dispatch.");

		for (int machineID : assignment.keySet()) {
			String workerID = partitionWorkerMap.get(machineID);
			ParDisWorkerProxy workerProxy = workerProxyMap.get(workerID);
			workerProxy.setWorkUnits(assignment.get(machineID));
		}
		localStartTime = System.currentTimeMillis();
		Stat.getInstance().jobAssignmentTime = (localStartTime - assignStartTime) * 1.0 / 1000;
	}

	private void assignIsoWOrkUnits() throws RemoteException {

		Queue<WorkUnit> workload = new LinkedList<WorkUnit>();
		int machineNum = workerProxyMap.size();
		for (WorkUnit w : workunits) {
			workload.add(w);
		}

		// Stat.getInstance().totalWorkUnit = workloads.size();

		Int2ObjectMap<Set<WorkUnit>> assignment = new Int2ObjectOpenHashMap<Set<WorkUnit>>();
		log.debug("finished assigment.");

		for (int assignedMachine = 0; assignedMachine < machineNum; assignedMachine++) {
			if (!workload.isEmpty()) {
				WorkUnit w = workload.poll();
				if (!assignment.containsKey(assignedMachine)) {
					assignment.put(assignedMachine, new HashSet<WorkUnit>());
				}
				assignment.get(assignedMachine).add(w);
			}
		}
		// for there is not enough work for all workers
		WorkUnit w = new WorkUnit();
		w.isIsoCheck = true;
		for (int assignedMachine = 0; assignedMachine < machineNum; assignedMachine++) {
			if (!assignment.containsKey(assignedMachine)) {
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
	// partition to check isomorphism;

	private void getResult() {

		String filename = KV.RESULT_FILE_PATH;

		// public IntSet negGfdP = new IntOpenHashSet();
		// public Set<GFD2> disConnectedGfds = new HashSet<GFD2>();
		// public Set<Pair<Integer,Condition>> negGfdXF = new
		// HashSet<Pair<Integer,Condition>>();
		log.debug("Write final result file ");

		PrintWriter writer;
		try {
			File file1 = new File(filename + ".neg");
			writer = new PrintWriter(file1);
			log.debug(negGfds.size());
			for (GFD2 gfd : negGfds) {

				writer.println(gfd.tofileC());
			}
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			File file1 = new File(filename + ".gfds");
			writer = new PrintWriter(file1);
			log.debug(connectedGfds.size());
			for (GFD2 gfd : connectedGfds) {
				writer.println(gfd.tofileC());
			}
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			File file1 = new File(filename + ".disconnected");
			writer = new PrintWriter(file1);
			for (GFD2 gfd : disConnectedGfds) {
				writer.println(gfd.tofileDC());
			}
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			File file1 = new File(filename + ".info");
			writer = new PrintWriter(file1);

			writer.println(Stat.getInstance().getInfo());

			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
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

	@Override
	public void localComputeCompleted(String workerID, Set<String> activeWorkerIDs) throws RemoteException {
		// TODO Auto-generated method stub
		log.info("receive acknowledgement from worker " + workerID + "\n saying activeWorkers: "
				+ activeWorkerIDs.toString());

		log.debug("current akg = " + this.workerAcknowledgementSet.size());
		if (isFirstPartialResult) {
			isFirstPartialResult = false;
			firstPartialResultArrivalTime = System.currentTimeMillis();
		}

		this.activeWorkerSet.addAll(activeWorkerIDs);
		this.workerAcknowledgementSet.remove(workerID);

		if (this.workerAcknowledgementSet.size() == 0) {
			
			log.debug("current akg already got 0");

			Stat.getInstance().finishGapTime = (System.currentTimeMillis() - firstPartialResultArrivalTime) * 1.0
					/ 1000;

			nextLocalCompute();

		}
	}

}
