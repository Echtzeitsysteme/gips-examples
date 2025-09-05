package lsmodel.generator;

import java.util.Random;

/*	Parameter order:
 *  IF 		dist == CONST
 *  	1. const value
 *  
 *  ELSE IF dist == EXP
 *  	2. lambda (<- aka. rate)
 *  
 *  ELSE IF dist == UNI
 *      2. lower bound
 *      3. upper bound
 *      
 *  ELSE IF dist == normal
 *  	2. mean
 *  	3. deviation
 */
public record GenParameter(GenDistribution dist, double[] parameters) {
	public GenParameter(GenDistribution dist, double... parameters) {
		this.dist = dist;
		this.parameters = parameters;
	}

	double getParam(final Random rnd) {
		return switch (dist) {
		case CONST -> {
			if (parameters.length != 1)
				throw new IllegalArgumentException("Expected parameters size 1, got " + parameters.length + ".");

			yield parameters[0];
		}
		case EXP -> {
			if (parameters.length != 1)
				throw new IllegalArgumentException("Expected parameters size 1, got " + parameters.length + ".");

			yield Math.log(1 - rnd.nextDouble() / -parameters[0]);
		}
		case N -> {
			if (parameters.length != 2)
				throw new IllegalArgumentException("Expected parameters size 2, got " + parameters.length + ".");

			yield rnd.nextGaussian(parameters[0], parameters[1]);
		}
		case UNI -> {
			if (parameters.length != 2)
				throw new IllegalArgumentException("Expected parameters size 2, got " + parameters.length + ".");

			yield rnd.nextDouble(parameters[0], parameters[1]);
		}
		default -> {
			throw new IllegalArgumentException("Unknown enumerator.");
		}
		};
	}
}
