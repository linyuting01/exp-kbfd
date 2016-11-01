package inf.ed.gfd.util;

public class Stat {

	private static Stat instance = null;

	// read graph files.
	public double totalTime = 0.0;

	public String setting;

	// read graph files.
	public double getInputFilesLocalAndDistributedTime = 0.0;

	// job assignment time.
	public double jobAssignmentTime = 0.0;

	// local detect time.
	public double localDetectViolationTime = 0.0;

	// communication cost.
	//public double communicationData = 0.0;

	// communication cost.
	//public double crossingEdgeData = 0.0;

	// finish gap between first and last
	public double finishGapTime = 0.0;

	// find all the candidates time
	//public double findCandidatesTime = 0.0;

	public int totalGfdCount = 0;
	//public int totalViolation = 0;
	public int totalWorkUnit = 0;

	public int sc2wcommunicationData = 0;

	public int w2SCcommunicationData = 0 ;

	public int w2wcommunicationData = 0;

	public double findGfdsTime = 0.0;

	public double findDisConnectedGfdsTime = 0.0;

	public double extendLiteralTime = 0.0;

	public double extendPatternTime = 0.0;

	public double findEdgePatternTime = 0.0;

	public double loaclVarifyGfdTime = 0.0;

	public double localVerifyPatternTime = 0.0;

	protected Stat() {
	}

	public static synchronized Stat getInstance() {
		if (instance == null) {
			instance = new Stat();
		}
		return instance;
	}

	public static double df(double number) {
		number = Math.round(number * 1000);
		number = number / 1000;
		return number;
	}

	public String getInfo() {

		String RUN_MODE = "";
		if (Params.RUN_MODE == Params.VAR_NP) {
			RUN_MODE = "base";
		}
		if (Params.RUN_MODE == Params.VAR_NB) {
			RUN_MODE = "random";
		}
		if (Params.RUN_MODE == Params.VAR_OPT) {
			RUN_MODE = "optimized";
		}

		String ret = "PLOT_DATA ********************************************\n";
		ret += "PLOT_DATA description: " + setting + ", " + KV.DATASET + ", n="
				+ Params.N_PROCESSORS + ", run_mode=" + RUN_MODE + ", VAR_K" + 
				Params.var_K + ", VAR_SUPP" + Params.VAR_SUPP + "\n";
		ret += "PLOT_DATA totalTime: " + df(totalTime) + "s.\n";
		ret += "PLOT_DATA readInputTime: " + df(getInputFilesLocalAndDistributedTime) + "s.\n";
		ret += "PLOT_DATA findEdgePatternTime: " + df(findEdgePatternTime) + "s.\n";
		ret += "PLOT_DATA findGfdsTime: " + df(findGfdsTime) + "s.\n";
		ret += "PLOT_DATA findDisConnectedGfdsTime: " + df(findDisConnectedGfdsTime) + "s.\n";
		ret += "PLOT_DATA extendLiteralTime: " + df(extendLiteralTime) + "s.\n";
		ret += "PLOT_DATA extendPatternTime: " + df(extendPatternTime) + "s.\n";
		ret += "PLOT_DATA loaclVarifyGfdTime: " + df(loaclVarifyGfdTime) + "s.\n";
		ret += "PLOT_DATA localVerifyPatternTime: " + df(localVerifyPatternTime) + "s.\n";
		ret += "PLOT_DATA worker2workerCommunicatiaonData: " + df(w2wcommunicationData / (1024 * 1024)) + "M.\n";
		ret += "PLOT_DATA Cordidator2WorkerCommunicationData: " + df(sc2wcommunicationData / (1024 * 1024)) + "M.\n";
		ret += "PLOT_DATA Worker2CordinatorCommunicationData: " + df(w2SCcommunicationData / (1024 * 1024)) + "M.\n";
		ret += "PLOT_DATA totalGFD/workunit: " +  totalGfdCount
				+ "/" + totalWorkUnit;

		return ret;
	}

}
