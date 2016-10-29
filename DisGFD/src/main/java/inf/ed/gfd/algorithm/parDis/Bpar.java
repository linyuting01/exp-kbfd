package inf.ed.gfd.algorithm.parDis;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import inf.ed.gfd.structure.WorkUnit;
import inf.ed.graph.structure.adaptor.Pair;

public class Bpar{

	int NUM_MACHINES;
	int NUM_TASKS;

	static Logger log = LogManager.getLogger(Bpar.class);

	public Int2ObjectMap<Set<WorkUnit>> makespan(int NUM_MACHINES,
			PriorityQueue<WorkUnit> workload) {

		Int2ObjectMap<Set< WorkUnit>>  assignment = new Int2ObjectOpenHashMap<Set<WorkUnit>>();
		this.NUM_MACHINES = NUM_MACHINES;
		this.NUM_TASKS = workload.size();

		int[] completeTime = new int[NUM_MACHINES];
		int completeAllTime = 0;

		// init complete time array
		for (int i = 0; i < NUM_MACHINES; i++) {
			completeTime[i] = 0;
		}

		while (!workload.isEmpty()) {
			WorkUnit wu = workload.poll();
			int complete = Integer.MAX_VALUE;
			int assign = 0;
			for (int i = 0; i < NUM_MACHINES; i++) {
				int wouldComplete = completeTime[i] + wu.EtIsoWork();
				if (wouldComplete < complete) {
					complete = wouldComplete;
					assign = i;
				}
			}
			completeTime[assign] += wu.EtIsoWork();
			if (complete > completeAllTime) {
				completeAllTime = complete;
			}

			if (!assignment.containsKey(assign)) {
				assignment.put(assign, new HashSet<WorkUnit>());
			}
			
			assignment.get(assign).add(wu);
		}

		System.out.println("estimated longest time =" + completeAllTime);
		return assignment;
	}
}

