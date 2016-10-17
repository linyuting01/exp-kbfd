package inf.ed.grape.communicate;

import inf.ed.gfd.structure.Partition;
import inf.ed.grape.interfaces.Message;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface Worker extends Remote {

	public String getWorkerID() throws RemoteException;

	public int getNumThreads() throws RemoteException;

	public void setCoordinatorProxy(Worker2Coordinator coordinatorProxy) throws RemoteException;

	//public void setQuery(Query query) throws RemoteException;

	public void addPartition(Partition partition) throws RemoteException;

	public void addPartitionID(int partitionID) throws RemoteException;

	public void addPartitionList(List<Partition> workerPartitions) throws RemoteException;

	public void addPartitionIDList(List<Integer> workerPartitionIDs) throws RemoteException;

	public void setWorkerPartitionInfo(int totalPartitionsAssigned,
			Map<Integer, Integer> mapVertexIdToPartitionId,
			Map<Integer, String> mapPartitionIdToWorkerId, Map<String, Worker> mapWorkerIdToWorker)
			throws RemoteException;

	public void halt() throws RemoteException;

	public void receiveMessage(List<Message<?>> incomingMessages) throws RemoteException;

	public void nextLocalCompute(long superstep) throws RemoteException;

	public void processPartialResult() throws RemoteException;

	public void vote2halt() throws RemoteException;

	public void shutdown() throws RemoteException;

	public void voteAgain() throws RemoteException;

	public boolean isHalt() throws RemoteException;

	public boolean loadWholeGraph(int partitionID) throws RemoteException;

}