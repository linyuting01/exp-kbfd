package inf.ed.gfd.algorithm.parDis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import inf.ed.gfd.algorithm.sequential.EdgePattern;
import inf.ed.gfd.structure.DFS;
import inf.ed.gfd.structure.DFSs;
import inf.ed.gfd.structure.GfdMsg;
import inf.ed.gfd.structure.Partition;
import inf.ed.gfd.structure.SuppResult;
import inf.ed.gfd.structure.WorkUnit;
import inf.ed.grape.interfaces.LocalComputeTask;
import inf.ed.grape.interfaces.Message;
import inf.ed.graph.structure.Graph;
import inf.ed.graph.structure.OrthogonalEdge;
import inf.ed.graph.structure.adaptor.Pair;
import inf.ed.graph.structure.adaptor.VertexOString;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;

public class ParDisWorkUnit extends LocalComputeTask {
	
	private Set<WorkUnit> workload;
	private Map<String, WorkUnit> assembledWorkload;

	
	static Logger log = LogManager.getLogger(ParDisWorkUnit.class);
	public HashMap<String,IntSet> pivotPMatch  = new HashMap<String,IntSet>();
	public HashMap<String, List<Pair<Integer,Integer>>> edgePatternNodeMatch = new HashMap<String, List<Pair<Integer,Integer>>>();
	public HashMap<String, List<Int2IntMap> > patternNodeMatchesN =  new HashMap<String, List<Int2IntMap>>();
	public void setWorkUnit(Set<WorkUnit> workload) {
		this.workload = workload;
	}
	
	

	public ParDisWorkUnit() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void compute(Partition partition) {
		
		// TODO Auto-generated method stub
		
		log.debug("begin local compute current super step = " + this.getSuperstep());
		if (this.getSuperstep() == 0) {
			//generate edge patterns.
			EdgePattern eP = new EdgePattern();
			List<DFS> edgePattern = eP.edgePattern( partition.getGraph(), pivotPMatch,
					edgePatternNodeMatch,patternNodeMatchesN);
		    SuppResult w = new SuppResult(pivotPMatch);
		    //superstep++;
		}
		else{
			
			
		}

			
			
			for (int destinationPID : prefetchRequest.keySet()) {
				GfdMsg content = new GfdMsg();
				content.requestedBorderNodes = prefetchRequest.get(destinationPID);
				Message<GfdMsg> m = new Message<GfdMsg>(partition.getPartitionID(), destinationPID,
						content);
				this.generatedMessages.add(m);
			}
		}

	}

	@Override
	public void incrementalCompute(Partition partition, List<Message<?>> incomingMessages) {
		// TODO Auto-generated method stub
		    log.info("now incremental compute, got incomming message size = ");

		

			log.info("superstep = 1, got prefetch request and send graph data as response.");
			sendTransferData(partition, incomingMessages);

		

			log.info("super step = 2, got prefetched request data and parse them into graph.");
			receiveTransferedGraphData(partition, incomingMessages);
			log.info("all the edges and vertex are added into partition.");
		}

	}
	
	private void sendTransferData(Partition partition,
			List<Message<?>> incomingMessages) {

		Int2ObjectMap<GfdMsg> newMessageContents = new Int2ObjectOpenHashMap<GfdMsg>();

			for (int targetPartitionID : Params.) {
				Message<GfdMsg> nMsg = new Message<GfdMsg>(partition.getPartitionID(),
						targetPartitionID, newMessageContents.get(targetPartitionID));
				this.generatedMessages.add(nMsg);
			}
		}
	}

	private void receiveTransferedGraphData(int workerNum, List<Message<?>> incomingMessages) {

		if (incomingMessages != null) {
			for (Message<?> recvMsg : incomingMessages) {

				log.debug(recvMsg.toString());

				GfdMsg recvContent = (GfdMsg) recvMsg.getContent();
				System.out.println("before add balls, " + partition.getPartitionInfo());
				for (Ball ball : recvContent.transferingGraphData) {
					System.out.println(ball.getInfo());
					partition.addTransferedGraph(ball);
				}
				System.out.println("after add balls, " + partition.getPartitionInfo());
			}
		}

		LocalViolationEnumInspector detector = new LocalViolationEnumInspector(workunits,
				partition.getGraph());
		List<Int2IntMap> partialViolations = detector.findIsomorphic();

		ViolationResult vr = (ViolationResult) this.generatedResult;
		vr.addViolations(partialViolations);
		log.debug("locally find " + partialViolations.size() + " violations");

	}
	private void assembleWorkUnit() {
		assembledWorkload = new HashMap<String, WorkUnit>();
		for (ReplicatedGWorkUnit wu : workload) {
			if (!assembledWorkload.containsKey(wu.getCategory())) {
				assembledWorkload.put(wu.getCategory(), new LinkedList<ReplicatedGWorkUnit>());
			}
			assembledWorkload.get(wu.getCategory()).add(wu);
		}
	}
	
	
	private void assembleMsg(){
		assembledWorkload = new HashMap<String, List<WorkUnit>>();
		
	}


	@Override
	public void prepareResult(Partition partition) {
		// TODO Auto-generated method stub
	

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}



	public void setWorkUnits(HashMap<String, List<WorkUnit>> workload2) {
		// TODO Auto-generated method stub
		
	}

}
