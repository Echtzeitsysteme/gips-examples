package ihtcmetamodel.loader;

/**
 * Record that holds the result of a statics run, i.e., a name, maximum value,
 * minimum value, mean value, and stddev value.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public record StatisticsResult(String name, int max, int min, double mean, double stddev) {
	
}
