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
import inf.ed.graph.structure.adaptor.Pair;
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
	//private Int2ObjectMap<IntSet> allBorderVertices = new Int2ObjectOpenHashMap<IntSet>();
	//private Int2IntMap borderBallSize = new Int2IntOpenHashMap();
	private Set<WorkUnit> workunits = new HashSet<WorkUnit>();
	//private Int2ObjectMap<String> allVertices = new Int2ObjectOpenHashMap<String>();
	//private Int2ObjectMap<Set<CrossingEdge>> mapBorderNodesAsSource = new Int2ObjectOpenHashMap<Set<CrossingEdge>>();
	//private Int2ObjectMap<Set<CrossingEdge>> mapBorderNodesAsTarget = new Int2ObjectOpenHashMap<Set<CrossingEdge>>();
	//private Int2IntMap allborderBallSize = new Int2IntOpenHashMap();

	// private Graph<VertexOString, OrthogonalEdge> KB;
	
	public Set<Pair<String,String>> negGfdXCands = new HashSet<Pair<String,String>>();
	GfdTree gfdTree = new GfdTree();
	
	
	//HashMap<String, HashMap<String,IntSet>> gfdPMatch = new HashMap<String,HashMap<String,IntSet>>();
	//HashMap<String, HashMap<String,Boolean>> satCId = new HashMap<String,HashMap<String,Boolean>>();
	//HashMap<String,IntSet> pivotMatchP = new HashMap<String,IntSet>();
	//HashMap<String, Set<String>> cIds  = new HashMap<String, Set<String>>();
	//boolean flagP;
	
	List<DFS> edgePattern = new ArrayList<DFS>();
	HashMap<String,List<WorkUnit>> ws = new HashMap<String,List<WorkUnit>>();
	
	
	public HashMap<String, Integer> labelId = new HashMap<String, Integer>();
	
	DisconnectedTree distree = new DisconnectedTree();
	
	
	
	Set<Pair<String,String>> gfdResults = new HashSet<Pair<String,String>>();
	//for negative gfds;
	public Set<String> negCands = new HashSet<String>();
	public Set<String> negGfdP = new HashSet<String>();
	public Set<Pair<String,Condition>> negGfdXF = new HashSet<Pair<String,Condition>>();
	
	
	
	
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
		assignWorkunitsToWorkers();
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
				ws.clear();
				if(superstep ==1){
					intialExtendAndGenerateWorkUnits(finalResult);
				}else{
					extendAndGenerateWorkUnits(finalResult);
				}
				log.debug("has created the new workunit");
				
				
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
	public void intialExtendAndGenerateWorkUnits(SuppResult finalResult){
		log.debug("begin compute support of edge pattern and extend condition y");
		for(String s :finalResult.pivotMatchP.keySet()){	
			double supp = ((double) finalResult.pivotMatchP.get(s).size())/finalResult.nodeNum;
			if(supp >= Params.VAR_SUPP){
				log.debug("supp value " + supp);
				DFS dfs = Fuc.getDfsFromString(s);
				edgePattern.add(dfs);
				log.debug("edgePattern size" +edgePattern.size());
			}		
		}
		
		gfdTree.extendRoot(edgePattern, finalResult);
		
		log.debug("begin to extend dependencied empty->y and create "
				+ "workunit for edge pattern with empty -> y ");
		
		for(GfdNode g:gfdTree.getRoot().children){
			g.ltree.extendNode(g.ltree.getRoot());
			for(LiterNode t:g.ltree.getRoot().children){
				WorkUnit w = new WorkUnit(g.key,t.dependency,true,true);
				if(!ws.containsKey(g.key)){
					ws.put(g.key, new ArrayList<WorkUnit>());
				}
				ws.get(g.key).add(w);
			}
				
		}
	}
	
	
	public void extendAndGenerateWorkUnits(SuppResult finalResult) throws RemoteException{
		if(finalResult.extendPattern == false){
			verifyGfdAndGenerateWorkUnits(finalResult);
		}else{
			verifyPatternAndGenerateWorkUnits(finalResult);
		}
	}
	
	List<SimP> extendPatterns = new ArrayList<SimP>();
	public void verifyGfdAndGenerateWorkUnits(SuppResult finalResult) throws RemoteException{
            boolean  disConnectedRevoke = false;
			log.debug("begin to verify gfd and extend condition X and produce workunit for next step.");
			for(Entry<String, HashMap<String,IntSet>> entry: finalResult.pivotMatchGfd.entrySet()){
				String pId = entry.getKey();
				for(Entry<String,IntSet> entry2 :finalResult.pivotMatchGfd.get(pId).entrySet()){
					String cId = entry2.getKey();
					GfdNode g = gfdTree.pattern_Map.get(pId);
					LiterNode t = g.ltree.condition_Map.get(cId);
					
					double supp = ((double) entry2.getValue().size())/finalResult.nodeNum;
					t.supp = supp;
					t.pivotMatch = entry2.getValue();
					log.debug("supp value " + supp);
					
					log.debug("check negative gfd ");
					if(supp == 0 && t.negCheck){
						negGfdXF.add(new Pair<String,Condition>(pId,t.dependency));
						t.extend = false;
					}
					
					if(!t.negCheck){
						//is a minimum gfd; check negative, update liter and var dom;
						if(supp >= Params.VAR_SUPP){
							if(finalResult.satCId.get(pId).get(cId)){
								Pair<String,String> gfd = new Pair<String,String>(pId,cId);
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
									for(String s: g.literDom.get(p.x)){
									  if(s.equals(p.y)){
										  g.literDom.get(p.x).remove(s);
									  }
									}
								}
								else{
									Pair<Integer,Integer> p = t.dependency.YEqualsVariable;
									for(int s: g.varDom.get(p.x)){
									  if(s == p.y){
										  g.varDom.get(p.x).remove(s);
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
			for(String pId: finalResult.pivotMatchGfd.keySet()){
				for(String cId: finalResult.pivotMatchGfd.get(pId).keySet()){
					GfdNode g = gfdTree.pattern_Map.get(pId);
					LiterNode t = g.ltree.condition_Map.get(cId);
					if(t.children!=null ){
						if(!t.children.isEmpty()){
							flagExtend = true;
							for(LiterNode tc : t.children){
								WorkUnit w = new WorkUnit(pId,tc.key,true,true);
								if(!ws.containsKey(pId)){
									ws.put(pId, new ArrayList<WorkUnit>());
								}
								ws.get(pId).add(w);
							}
					}
				}
			}
	    	}
			
			if(flagExtend == false){
				log.debug("no condition more, begin to extend pattern");
				boolean flagExtendP = false;
				//no more extend of literNode
				for(String pId:finalResult.pivotMatchGfd.keySet()){
					GfdNode g = gfdTree.pattern_Map.get(pId);
					gfdTree.extendNodeGeneral(g);
					if(g.children != null){
						if(!g.children.isEmpty()){
							flagExtendP = true;
							for(GfdNode t: g.children ){
								// pattern workunit
								if(!ws.containsKey(pId)){
									ws.put(pId, new ArrayList<WorkUnit>());
								}
								t.wC2Wp.oriPatternId = g.parent.key;
								t.wC2Wp.isGfdCheck = false;
								ws.get(pId).add(t.wC2Wp);//revise
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
				
	}
	}
		
	public void verifyPatternAndGenerateWorkUnits(SuppResult finalResult) throws RemoteException{
		           // extendPatterns.clear();
					boolean flagExtend = false;
					
					log.debug("begin to verify pattern support");
					for(String pId :finalResult.pivotMatchP.keySet()){
						GfdNode g = gfdTree.pattern_Map.get(pId);
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
							
							for(LiterNode t :g.ltree.getRoot().children){
								//create WorkUnit
								WorkUnit w = new WorkUnit(pId,t.key,true,false);
								if(!ws.containsKey(pId)){
									ws.put(pId, new ArrayList<WorkUnit>());
								}
								ws.get(pId).add(w);
							}
							
							
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
	
	/*
	 public void processNegativePCands(){
		  
		   List<DFS> pattern = new ArrayList<DFS>();
		   List<String> pCands = new ArrayList<String>(); 
		   if(!negCands.isEmpty()){
			   for(String pId: negCands){
				   GfdNode g = gfdTree.pattern_Map.get(pId);
				   pCands.clear();
				   pattern.clear();
				   //find all possible parent
				   while(g!= gfdTree.getRoot()){
					   pattern.add(g.edgePattern);
					   g = g.parent;
				   }
				   for(int i=0;i< pattern.size();i++){
					   List<DFS> newPattern = new ArrayList<DFS>();
					   for(int j = pattern.size()-1;j!= i && j>=0;j--){
						  newPattern.add(pattern.get(j));
					   }
					   StringBuffer sb = new StringBuffer();
					   for(DFS dfs : newPattern){
						   sb.append(dfs.toString());
					   }
					   String ppId = sb.toString();
					   if(gfdTree.pattern_Map.containsKey(ppId)){
						   GfdNode nt = gfdTree.pattern_Map.get(ppId);
						   if(nt.supp>=Params.var_K){
							   negGfdP.add(ppId);
							   break;
						   }
					   }
				   }
			   }
		   }
		   
		   
	   }	*/		

//public Set<String> negGfdP = new HashSet<String>();
	/*
   public void processNegativePCands(){
	  
	   List<DFS> pattern = new ArrayList<DFS>();
	   List<String> pCands = new ArrayList<String>(); 
	   if(!negCands.isEmpty()){
		   for(String pId: negCands){
			   GfdNode g = gfdTree.pattern_Map.get(pId);
			   pCands.clear();
			   pattern.clear();
			   //find all possible parent
			   while(g!= gfdTree.getRoot()){
				   pattern.add(g.edgePattern);
				   g = g.parent;
			   }
			   for(int i=0;i< pattern.size();i++){
				   List<DFS> newPattern = new ArrayList<DFS>();
				   for(int j = pattern.size()-1;j!= i && j>=0;j--){
					  newPattern.add(pattern.get(j));
				   }
				   StringBuffer sb = new StringBuffer();
				   for(DFS dfs : newPattern){
					   sb.append(dfs.toString());
				   }
				   String ppId = sb.toString();
				   if(gfdTree.pattern_Map.containsKey(ppId)){
					   GfdNode nt = gfdTree.pattern_Map.get(ppId);
					   if(nt.supp>=Params.var_K){
						   negGfdP.add(ppId);
						   break;
					   }
				   }
			   }
		   }
	   }
	   
	   
   }
   //suppose no order;
   //HashMap<Integer,String> simDependency();
   
   public void processNegGfdXFCands(){
	   if(!negGfdXCands.isEmpty()){
		   for(Pair<String,String> p : negGfdXCands){
			   GfdNode g = gfdTree.pattern_Map.get(p.x);
			   LiterNode t = g.ltree.condition_Map.get(p.y);
			   if(t.dependency.isLiteral){
				   Pair<Integer,String> yl = t.dependency.YEqualsLiteral;
				   if(t.dependency.XEqualsLiteral.containsKey(yl.x)){
					   if(t.dependency.XEqualsLiteral.get(yl.x) == yl.y){
						   getNegCond(g, t);
					    }
			       }
		       }
			   else{
				   Pair<Integer,Integer> yv = t.dependency.YEqualsVariable;
				   if(t.dependency.XEqualsVariable.containsKey(yv.x)){
					   if(t.dependency.XEqualsVariable.get(yv.x).contains(yv.y)){ 
						   getNegCond(g, t);
					    }
				   }
			   }
		   }
	   }
   }
	 
			   
    public void getNegCond(GfdNode g, LiterNode t){
    	boolean flag = false;
		  for(Entry<Integer,String> entry1 :t.dependency.XEqualsLiteral.entrySet()){
			  Condition c = (Condition) t.dependency.clone();
			  c.XEqualsLiteral.remove(entry1.getKey());
			  flag = verifyNeg(g,c);
			  if(flag){
				  break;
			  }
		  }
		  if(flag == false){
			  boolean loop = true;
				  for(Entry<Integer,IntSet> entry2 :t.dependency.XEqualsVariable.entrySet()){
					if(loop){
					  for(int a : entry2.getValue()){
						  Condition c = (Condition) t.dependency.clone();
						  c.XEqualsVariable.get(entry2.getKey()).remove(c);
						  
						  flag = verifyNeg(g,c); 
						  if(flag){
							  loop = false;
							  break;
						  }
					  }
				  }
		     }
	     }
    }
	public boolean  verifyNeg(GfdNode g,Condition c){
		 String newcId = c.toString();
		  if(g.ltree.condition_Map.containsKey(newcId)){
			  if(g.ltree.condition_Map.get(newcId).supp >= Params.var_K  &&
					  g.ltree.condition_Map.get(newcId).isSat){
				  negGfdXF.add(new Pair<String,Condition>(g.key,c));
				  return true;
			  }
		  }
		  return false;
	}*/
	
	//HashMap<String,List<WorkUnit>> ws = new HashMap<String,List<WorkUnit>>();
	
	private void assignWorkunitsToWorkers() throws RemoteException {

		log.debug("begin assign work units to workers.");
		log.debug("workload size" + ws.size());

		long assignStartTime = System.currentTimeMillis();

		int machineNum = workerProxyMap.size();
		//Stat.getInstance().totalWorkUnit = workunits.size();

		Int2ObjectMap<HashMap<String,List<WorkUnit>>> assignment = new 
				Int2ObjectOpenHashMap<HashMap<String,List<WorkUnit>>>();
		//Int2ObjectMap<Int2ObjectMap<IntSet>> prefetchRequest = new Int2ObjectOpenHashMap<Int2ObjectMap<IntSet>>();
		//Int2ObjectMap<Set<CrossingEdge>> crossingEdges = new Int2ObjectOpenHashMap<Set<CrossingEdge>>();
		Random r = new Random();

		//log.debug("should be very quick");
		for (Entry<String,List<WorkUnit>> wu : ws.entrySet()) {
			for(int assignedMachine = 0; assignedMachine  < machineNum ;assignedMachine ++)
				assignment.put(assignedMachine, ws);
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
