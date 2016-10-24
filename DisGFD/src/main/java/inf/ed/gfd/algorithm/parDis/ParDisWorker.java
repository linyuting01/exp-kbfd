package inf.ed.gfd.algorithm.parDis;

import inf.ed.gfd.algorithm.sequential.EdgePattern;
import inf.ed.gfd.structure.Ball;
import inf.ed.gfd.structure.CrossingEdge;
import inf.ed.gfd.structure.GFD2;
import inf.ed.gfd.structure.GfdMsg;
import inf.ed.gfd.structure.Partition;
import inf.ed.gfd.structure.WorkUnit;
import inf.ed.gfd.util.Dev;
import inf.ed.gfd.util.KV;
import inf.ed.gfd.util.Params;
import inf.ed.gfd.util.Stat;
import inf.ed.grape.communicate.Worker;
import inf.ed.grape.communicate.Worker2Coordinator;
import inf.ed.grape.communicate.Worker2WorkerProxy;
import inf.ed.grape.interfaces.LocalComputeTask;
import inf.ed.grape.interfaces.Message;
import inf.ed.grape.interfaces.Result;
import inf.ed.graph.structure.Graph;
import inf.ed.graph.structure.OrthogonalEdge;
import inf.ed.graph.structure.adaptor.Pair;
import inf.ed.graph.structure.adaptor.VertexOString;
import inf.ed.graph.structure.adaptor.VertexString;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.carrotsearch.sizeof.RamUsageEstimator;

/**
 * Represents the computation node.
 * 
 * @author Yecol
 */

public class ParDisWorker extends UnicastRemoteObject implements Worker {

	private static final long serialVersionUID = 1L;

	/** The number of threads. */
	private int numThreads;

	/** The total partitions assigned. */
	private int totalPartitionsAssigned;

	/** The queue of partitions in the current super step. */
	private BlockingQueue<LocalComputeTask> currentLocalComputeTaskQueue;

	/** The queue of partitions in the next super step. */
	private BlockingQueue<LocalComputeTask> nextLocalComputeTasksQueue;

	/** hosting partitions */
	private Map<Integer, Partition> partitions;

	/** Host name of the node with time stamp information. */
	private String workerID;

	/** Coordinator Proxy object to interact with Master. */
	private Worker2Coordinator coordinatorProxy ;

	/** VertexID 2 PartitionID Map */
	// private Map<Integer, Integer> mapVertexIdToPartitionId;

	/** PartitionID to WorkerID Map. */
	private Map<Integer, String> mapPartitionIdToWorkerId;

	/** Worker2WorkerProxy Object. */
	private Worker2WorkerProxy worker2WorkerProxy;

	/** Worker to Outgoing Messages Map. */
	private Map<String, List<Message<?>>> outgoingMessages;

	/** PartitionID to Outgoing Results Map. */
	private Map<Integer, Result> partialResults;

	/** partitionId to Previous Incoming messages - Used in current Super Step. */
	private Map<Integer, List<Message<?>>> previousIncomingMessages;

	/** partitionId to Current Incoming messages - used in next Super Step. */
	private Map<Integer, List<Message<?>>> currentIncomingMessages;

	/**
	 * boolean variable indicating whether the partitions can be worked upon by
	 * the workers in each super step.
	 **/
	private boolean flagLocalCompute = false;
	/**
	 * boolean variable to determine if a Worker can send messages to other
	 * Workers and to Master. It is set to true when a Worker is sending
	 * messages to other Workers.
	 */
	private boolean stopSendingMessage;

	private boolean flagLastStep = true;

	/** The super step counter. */
	private long superstep = 0;

	/** These are for GFD project **/
	private int holdingPartitionID = 0;
	
	private ParDisWorkUnit localComputeTask = new ParDisWorkUnit();

	private boolean flagSetWorkUnit = true;
	
	
	
	//public HashMap<String, Integer> labelId = new HashMap<String, Integer>();
	
	  // here String denotes the pattern string P previous, N id the extended 
	 // public HashMap<String, List<Int2IntMap> > patternNodeMatchesP =  new HashMap<String, List<Int2IntMap>>();
	  //the ith layer , now ;
	 // public HashMap<String, List<Int2IntMap> > patternNodeMatchesN =  new HashMap<String, List<Int2IntMap>>();
	  
	  
	 //public HashMap<String, List<Pair<Integer,Integer>>> edgePatternNodeMatch = new HashMap<String, List<Pair<Integer,Integer>>>();
	  
	  //public GfdMsg gfdMsg = new GfdMsg();
	  
	  //HashMap<String,List<Int2IntMap>> boderMatch = new HashMap<String,List<Int2IntMap>> ();
	  
	// public HashMap<String,IntSet> pivotPMatch  = new HashMap<String,IntSet>();
	  
	  //IntSet borderNodes = new IntOpenHashSet();
	  
	  //the ith layer , now ;
	 // HashMap<String, List<Int2IntMap> > patternNodeMatchesN =  new HashMap<String, List<Int2IntMap>>();


	static Logger log = LogManager.getLogger(ParDisWorker.class);

	/**
	 * Instantiates a new worker.
	 * 
	 * @throws RemoteException
	 *             the remote exception
	 */
	public ParDisWorker() throws RemoteException {
		InetAddress address = null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMdd.HHmmss.SSS");
		String timestamp = simpleDateFormat.format(new Date());
		String hostName = new String();
		try {
			address = InetAddress.getLocalHost();
			hostName = address.getHostName();
		} catch (UnknownHostException e) {
			hostName = "UnKnownHost";
			log.error(e);
		}

		this.workerID = "sync_" + hostName + "_" + timestamp;
		this.partitions = new HashMap<Integer, Partition>();
		this.currentLocalComputeTaskQueue = new LinkedBlockingDeque<LocalComputeTask>();
		this.nextLocalComputeTasksQueue = new LinkedBlockingQueue<LocalComputeTask>();
		this.currentIncomingMessages = new HashMap<Integer, List<Message<?>>>();
		this.partialResults = new HashMap<Integer, Result>();
		this.previousIncomingMessages = new HashMap<Integer, List<Message<?>>>();
		this.outgoingMessages = new HashMap<String, List<Message<?>>>();
		this.numThreads = 1;
		this.stopSendingMessage = false;
		//this.coordinatorProxy = new ParDisWorkerProxy();
		/////////////////////////////////////////////////////
		//this.localComputeTask.
		//this.currentLocalComputeTaskQueue.add(localComputeTask);

		//this.mapBorderVertex2Ball = new Int2ObjectOpenHashMap<Ball>();
		//this.mapBorderVertex2BallSize = new Int2IntOpenHashMap();

		for (int i = 0; i < numThreads; i++) {
			log.debug("Starting syncThread " + (i + 1));
			WorkerThread workerThread = new WorkerThread();
			workerThread.setName("Worker");
			workerThread.start();
		}

	}

	/**
	 * Adds the partition to be assigned to the worker.
	 * 
	 * @param partition
	 *            the partition to be assigned
	 * @throws RemoteException
	 *             the remote exception
	 */
	// @Override
	public void addPartition(Partition partition) throws RemoteException {
		throw new IllegalArgumentException("No partition in distributed setting 1.");
	}

	@Override
	public void addPartitionID(int partitionID) throws RemoteException {
		String filename = KV.GRAPH_FILE_PATH + ".p" + partitionID;
		Partition partition = new Partition(partitionID);
		partition.loadPartitionDataFromEVFile(filename.trim());
		Params.GRAPHNODENUM = partition.getGraph().vertexSize();
		//partition.loadBorderVerticesFromFile(KV.GRAPH_FILE_PATH);
		this.partitions.put(partitionID, partition);
		totalPartitionsAssigned = 1;
		this.holdingPartitionID = partitionID;
		log.info("loaded the ParDis graph: " + filename + ", part " + partitionID);
		log.debug(Dev.currentRuntimeState());
	}

	/**
	 * Gets the num threads.
	 * 
	 * @return the num threads
	 */
	@Override
	public int getNumThreads() {
		return numThreads;
	}

	/**
	 * Gets the worker id.
	 * 
	 * @return the worker id
	 */
	@Override
	public String getWorkerID() {
		return workerID;
	}

	/**
	 * The Class SyncWorkerThread.
	 */
	private class WorkerThread extends Thread {

		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				
				
				
				while (flagLocalCompute) {
					//log.debug(this + "superstep loop start for superstep " + superstep);
					try {
						if(superstep == 0){
							flagLocalCompute = false;
							//log.debug(holdingPartitionID);
							localComputeTask.init(holdingPartitionID);
							Partition workingPartition = partitions.get(holdingPartitionID);
							localComputeTask.compute(workingPartition);
						}
						else {
								LocalComputeTask localComputeTask = currentLocalComputeTaskQueue.take();
	
								Partition workingPartition = partitions.get(localComputeTask
										.getPartitionID());

								/** not begin step. incremental compute */
								boolean isGfdCheck = localComputeTask.incrementalCompute(workingPartition);
								if(!isGfdCheck){
									checkAndSendMessage();	
									List<Message<?>> messageForWorkingPartition = previousIncomingMessages
											.get(localComputeTask.getPartitionID());
									localComputeTask.incrementalCompute(workingPartition,
											messageForWorkingPartition);
								}
							}
						
						partialResults.put(localComputeTask.getPartitionID(),
									localComputeTask.getPartialResult());
						//nextLocalComputeTasksQueue.add(localComputeTask);
						checkAndSendPartialResult();
						log.debug("send suppreslut done" );
						flagSetWorkUnit = false;
					
				}catch (Exception e) {
					e.printStackTrace();
				}
			 }
			}
		}

	}

	
	private synchronized void checkAndSendPartialResult() {
		//log.debug("synchronized checkAndSendPartialResult!");
		//log.debug("send partital result to coordinator for assemble");
		flagLocalCompute = false;
		
		try {
			log.debug("patilResult size"+ partialResults.size());
			coordinatorProxy.sendPartialResult(workerID, partialResults);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		//log.info("round"+ this.superstep+ "Done!");
		Set<String> activeWorkerSet = new HashSet<String>();
		activeWorkerSet.add(workerID);
		
		// Send a message to the Master saying that this superstep
		// has
		// been completed.
		try {
			coordinatorProxy.localComputeCompleted(workerID, activeWorkerSet);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
		

	private synchronized void checkAndSendMessage() {
		/**
		 * Check and send message. Notice: this is a critical code area, which
		 * should put outside of the thread code.
		 * 
		 * @throws RemoteException
		 */

		log.debug("synchronized checkAndSendMessage!");

		//log.debug("nextQueueSize:" + nextLocalComputeTasksQueue.size() + " == partitionAssigned:"
				//+ totalPartitionsAssigned);
		if ((!stopSendingMessage) ) {
			log.debug("sendMessage!");

			   stopSendingMessage = true;

				log.debug(" Worker: Superstep " + superstep + " pattern checking :begin send infomation .");

				flagLocalCompute = false;

				for (Entry<String, List<Message<?>>> entry : outgoingMessages.entrySet()) {
					try {
						log.info("+++++try to send message to " + entry.getKey() + ", message = "
								+ entry.getValue());
						worker2WorkerProxy.sendMessage(entry.getKey(), entry.getValue());
					} catch (RemoteException e) {
						System.out.println("Can't send message to Worker " + entry.getKey()
								+ " which is down");
						e.printStackTrace();
					}
				}
		}
	}

	/**
	 * Halts the run for this application and prints the output in a file.
	 * 
	 * @throws RemoteException
	 *             the remote exception
	 */
	@Override
	public void halt() throws RemoteException {
		System.out.println("Worker Machine " + workerID + " halts");
		this.restoreInitialState();
	}

	/**
	 * Restore the worker to the initial state
	 */
	private void restoreInitialState() {
		// this.partitionQueue.clear();
		this.currentIncomingMessages.clear();
		this.outgoingMessages.clear();
		this.mapPartitionIdToWorkerId.clear();
		// this.currentPartitionQueue.clear();
		this.previousIncomingMessages.clear();
		this.stopSendingMessage = false;
		this.flagLocalCompute = false;
		this.totalPartitionsAssigned = 0;
	}

	/**
	 * Updates the outgoing messages for every super step.
	 * 
	 * @param messagesFromCompute
	 *            Represents the map of destination vertex and its associated
	 *            message to be send
	 */
	private void updateOutgoingMessages(List<Message<?>> messagesFromCompute) {
		log.debug("updateOutgoingMessages.size = " + messagesFromCompute.size());

		String workerID = null;
		int partitionID = -1;
		List<Message<?>> workerMessages = null;

		for (Message<?> message : messagesFromCompute) {

			partitionID = message.getDestinationPartitionID();
			workerID = mapPartitionIdToWorkerId.get(partitionID);

			if (workerID.equals(this.workerID)) {

				/** for gfd, discard these message */
				// updateIncomingMessages(partitionID, message);
			} else {
				if (outgoingMessages.containsKey(workerID)) {
					outgoingMessages.get(workerID).add(message);
				} else {
					workerMessages = new ArrayList<Message<?>>();
					workerMessages.add(message);
					outgoingMessages.put(workerID, workerMessages);
				}
			}
		}
	}

	/**
	 * Sets the worker partition info.
	 * 
	 * @param totalPartitionsAssigned
	 *            the total partitions assigned
	 * @param mapVertexIdToPartitionId
	 *            the map vertex id to partition id
	 * @param mapPartitionIdToWorkerId
	 *            the map partition id id to worker id
	 * @param mapWorkerIdToWorker
	 *            the map worker id to worker
	 * @throws RemoteException
	 */
	@Override
	public void setWorkerPartitionInfo(int totalPartitionsAssigned,
			Map<Integer, Integer> mapVertexIdToPartitionId,
			Map<Integer, String> mapPartitionIdToWorkerId, Map<String, Worker> mapWorkerIdToWorker)
			throws RemoteException {
		this.totalPartitionsAssigned = totalPartitionsAssigned;
		this.mapPartitionIdToWorkerId = mapPartitionIdToWorkerId;
		this.worker2WorkerProxy = new Worker2WorkerProxy(mapWorkerIdToWorker);
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
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		if (args.length < 4) {
			System.out.println("paras: config-file, n, opt");
			System.exit(0);
		}

		try {
			String coordinatorMachineName = args[0];
			Params.CONFIG_FILENAME = args[1].trim();
			Params.N_PROCESSORS = Integer.parseInt(args[2].trim());
			Params.RUN_MODE = Integer.parseInt(args[3].trim());
			log.debug("Connected to Sc: " + coordinatorMachineName);
			log.debug("PARAM_CONFIG_FILE = " + Params.CONFIG_FILENAME);
			log.debug("PARAM_N = " + Params.N_PROCESSORS);
			log.debug("PARAM_RUN_MODE = " + Params.RUN_MODE);
			log.debug("Processing ParDis graph = " + KV.DATASET);

			String masterURL = "//" + coordinatorMachineName + "/" + KV.COORDINATOR_SERVICE_NAME;
			Worker2Coordinator worker2Coordinator = (Worker2Coordinator) Naming.lookup(masterURL);
			Worker worker = new ParDisWorker();
			Worker2Coordinator coordinatorProxy = worker2Coordinator.register(worker,
					worker.getWorkerID(), worker.getNumThreads());

			worker.setCoordinatorProxy(coordinatorProxy);
			log.info("Worker is bound and ready for computations ");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets the master proxy.
	 * 
	 * @param masterProxy
	 *            the new master proxy
	 */
	@Override
	public void setCoordinatorProxy(Worker2Coordinator coordinatorProxy) {
		this.coordinatorProxy = coordinatorProxy;
	}

	/**
	 * Receive message.
	 * 
	 * @param incomingMessages
	 *            the incoming messages
	 * @throws RemoteException
	 *             the remote exception
	 */
	@Override
	public void receiveMessage(List<Message<?>> incomingMessages) throws RemoteException {

		Stat.getInstance().communicationData += RamUsageEstimator.sizeOf(incomingMessages);

		/** partitionID to message list */
		List<Message<?>> partitionMessages = null;

		int partitionID = -1;

		for (Message<?> message : incomingMessages) {
			partitionID = message.getDestinationPartitionID();
			if (currentIncomingMessages.containsKey(partitionID)) {
				currentIncomingMessages.get(partitionID).add(message);
			} else {
				partitionMessages = new ArrayList<Message<?>>();
				partitionMessages.add(message);
				currentIncomingMessages.put(partitionID, partitionMessages);
			}
		}
	}

	/**
	 * Receives the messages sent by all the vertices in the same node and
	 * updates the current incoming message queue.
	 * 
	 * @param destinationVertex
	 *            Represents the destination vertex to which the message has to
	 *            be sent
	 * @param incomingMessage
	 *            Represents the incoming message for the destination vertex
	 */
	public void updateIncomingMessages(int partitionID, Message<?> incomingMessage) {
		List<Message<?>> partitionMessages = null;
		if (currentIncomingMessages.containsKey(partitionID)) {
			currentIncomingMessages.get(partitionID).add(incomingMessage);
		} else {
			partitionMessages = new ArrayList<Message<?>>();
			partitionMessages.add(incomingMessage);
			currentIncomingMessages.put(partitionID, partitionMessages);
		}
	}

	/** shutdown the worker */
	@Override
	public void shutdown() throws RemoteException {
		java.util.Date date = new java.util.Date();
		log.info("Worker" + workerID + " goes down now at :" + new Timestamp(date.getTime()));
		System.exit(0);
	}

	@Override
	public void nextLocalCompute(long superstep) throws RemoteException {

		/**
		 * Next local compute. No generated new local compute tasks. Transit
		 * compute task and status from the last step.
		 * */

		this.superstep = superstep;

		// Put all elements in current incoming queue to previous incoming queue
		// and clear the current incoming queue.
		this.previousIncomingMessages.clear();
		this.previousIncomingMessages.putAll(this.currentIncomingMessages);
		this.currentIncomingMessages.clear();

		this.stopSendingMessage = false;
		if(flagSetWorkUnit == true){
			this.flagLocalCompute = true;
		}

		this.outgoingMessages.clear();

		// Put all local compute tasks in current task queue.
		// clear the completed partitions.
		// Note: To avoid concurrency issues, it is very important that
		// completed partitions is cleared before the Worker threads start to
		// operate on the partition queue in the next super step
		BlockingQueue<LocalComputeTask> temp = new LinkedBlockingDeque<LocalComputeTask>(
				nextLocalComputeTasksQueue);
		this.nextLocalComputeTasksQueue.clear();
		this.currentLocalComputeTaskQueue.addAll(temp);

	}

	@Override
	public void processPartialResult() throws RemoteException {
		BlockingQueue<LocalComputeTask> temp = new LinkedBlockingDeque<LocalComputeTask>(
				nextLocalComputeTasksQueue);
		this.nextLocalComputeTasksQueue.clear();
		this.currentLocalComputeTaskQueue.addAll(temp);

		//this.flagLastStep = true;
		this.stopSendingMessage = false;

	}

	@Override
	public void vote2halt() throws RemoteException {
		throw new IllegalArgumentException("this mothod doesn't support in synchronised model.");
	}

	@Override
	public void voteAgain() throws RemoteException {
		throw new IllegalArgumentException("this mothod doesn't support in synchronised model.");
	}

	@Override
	public boolean isHalt() {
		throw new IllegalArgumentException("this mothod doesn't support in synchronised model.");
	}

	
	
	@Override
	public void addPartitionList(List<Partition> workerPartitions) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addPartitionIDList(List<Integer> workerPartitionIDs) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean loadWholeGraph(int partitionID) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setWorkUnits(HashMap<String, List<WorkUnit>> workload)throws RemoteException {
		log.info("Get " + workload.size() + " work units from coordinator ");
        log.debug("this.partitions's size" + this.partitions.size());
		for (Entry<Integer, Partition> entry : this.partitions.entrySet()) {

			try {
				localComputeTask.workload.clear();
				
				localComputeTask.init(entry.getKey());
				localComputeTask.setWorkUnits(workload);
				localComputeTask.patternNodeMatchesP.clear();
				localComputeTask.patternNodeMatchesP = new HashMap<String,List<Int2IntMap>>(localComputeTask.patternNodeMatchesN);
				// add fetch
				//localComputeTask.setPrefetchQuest(prefetchRequests);
				//localComputeTask.setMapBorderVertex2Ball(mapBorderVertex2Ball);
				localComputeTask.patternNodeMatchesN.clear();
				localComputeTask.pivotPMatch.clear();
				this.nextLocalComputeTasksQueue.add(localComputeTask);
				this.flagSetWorkUnit = true;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.flagLocalCompute = true;
		
		
	}
	
}
