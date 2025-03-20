package ihtcgips.patterns;

import java.util.Collection;
import java.util.HashSet;

import ihtcmetamodel.Day;
import ihtcmetamodel.Gender;
import ihtcmetamodel.Hospital;
import ihtcmetamodel.OperatingTheater;
import ihtcmetamodel.Room;
import ihtcmetamodel.Shift;
import ihtcmetamodel.Surgeon;

public class Patterns {

	public Collection<Tuple<Day, Room>> dayRoomTuple(final Hospital model) {
		final Collection<Tuple<Day, Room>> tuples = new HashSet<Tuple<Day, Room>>();

		model.getDays().forEach(d -> {
			model.getRooms().forEach(r -> {
				tuples.add(new Tuple<Day, Room>(d, r));
			});
		});

		return tuples;
	}

	public Collection<Tuple<Day, Surgeon>> daySurgeonTuple(final Hospital model) {
		final Collection<Tuple<Day, Surgeon>> tuples = new HashSet<Tuple<Day, Surgeon>>();

		model.getDays().forEach(d -> {
			model.getSurgeons().forEach(s -> {
				tuples.add(new Tuple<Day, Surgeon>(d, s));
			});
		});

		return tuples;
	}

	public Collection<Tuple<Day, OperatingTheater>> dayOperatingTheaterTuple(final Hospital model) {
		final Collection<Tuple<Day, OperatingTheater>> tuples = new HashSet<Tuple<Day, OperatingTheater>>();

		model.getDays().forEach(d -> {
			model.getOperatingTheaters().forEach(ot -> {
				tuples.add(new Tuple<Day, OperatingTheater>(d, ot));
			});
		});

		return tuples;
	}

	public Collection<Tuple<Room, Shift>> roomShiftTuple(final Hospital model) {
		final Collection<Tuple<Room, Shift>> tuples = new HashSet<Tuple<Room, Shift>>();

		model.getRooms().forEach(r -> {
			model.getShifts().forEach(s -> {
				tuples.add(new Tuple<Room, Shift>(r, s));
			});
		});

		return tuples;
	}

	public Collection<Triple<Day, Room, Gender>> dayRoomGenderTruple(final Hospital model) {
		final Collection<Triple<Day, Room, Gender>> triples = new HashSet<Triple<Day, Room, Gender>>();

		model.getDays().forEach(d -> {
			model.getRooms().forEach(r -> {
				model.getGenders().forEach(g -> {
					triples.add(new Triple<Day, Room, Gender>(d, r, g));
				});
			});
		});

		return triples;
	}

	//
	// Helper classes
	//

	public class Tuple<L, R> {
		private final L left;
		private final R right;

		public Tuple(final L left, final R right) {
			this.left = left;
			this.right = right;
		}

		public L getLeft() {
			return left;
		}

		public R getRight() {
			return right;
		}

	}

	public class Triple<L, M, R> {
		private final L left;
		private final M middle;
		private final R right;

		public Triple(final L left, final M middle, final R right) {
			this.left = left;
			this.middle = middle;
			this.right = right;
		}

		public L getLeft() {
			return left;
		}

		public M getMiddle() {
			return middle;
		}

		public R getRight() {
			return right;
		}

	}

}
