package ihtcvirtualpreprocessing;

import java.util.Objects;

public class Tuple<X, Y> {
	public final X x;
	public final Y y;

	public Tuple(final X x, final Y y) {
		Objects.requireNonNull(x);
		Objects.requireNonNull(y);
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean equals(final Object o) {
		Objects.requireNonNull(o);

		// If the other object is the same as this object, return true.
		if (o == this) {
			return true;
		}

		// Check if o is an instance of Tuple. If this is not given, return false.
		if (!(o instanceof Tuple)) {
			return false;
		}

		@SuppressWarnings("unchecked")
		final Tuple<X, Y> other = (Tuple<X, Y>) o;

		return this.x.equals(other.x) && this.y.equals(other.y);
	}
}
