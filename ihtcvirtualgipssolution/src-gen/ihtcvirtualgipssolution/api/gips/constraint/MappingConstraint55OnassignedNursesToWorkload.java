package ihtcvirtualgipssolution.api.gips.constraint;		

import org.emoflon.gips.core.GipsEngine;
import org.emoflon.gips.core.GipsMapper;
import org.emoflon.gips.core.milp.model.Constraint;
import org.emoflon.gips.core.GipsMappingConstraint;
import org.emoflon.gips.core.GlobalMappingIndexer;
import org.emoflon.gips.core.MappingIndexer;

import ihtcvirtualgipssolution.api.gips.IhtcvirtualgipssolutionGipsAPI;
import org.emoflon.gips.intermediate.GipsIntermediate.MappingConstraint;
import org.emoflon.gips.core.milp.model.Term;
import java.util.stream.Collectors;
import ihtcvirtualgipssolution.api.gips.mapping.AssignedNursesToWorkloadMapping;
import java.util.List;
import java.util.Set;

import ihtcvirtualgipssolution.api.gips.mapping.AssignedPatientsToRoomMapping;
import ihtcvirtualgipssolution.api.gips.mapping.SelectedShiftToRosterMapping;
import java.util.LinkedList;
import java.util.Collections;

public class MappingConstraint55OnassignedNursesToWorkload extends GipsMappingConstraint<IhtcvirtualgipssolutionGipsAPI, AssignedNursesToWorkloadMapping>{
	public MappingConstraint55OnassignedNursesToWorkload(final IhtcvirtualgipssolutionGipsAPI engine, final MappingConstraint constraint) {
		super(engine, constraint);
	}
	
	@Override
	protected double buildConstantRhs(final AssignedNursesToWorkloadMapping context) {
		return 0.0 - ((-1.0) * ((-1.0) * (1)));
	}
		
	@Override
	protected List<Term> buildVariableLhs(final AssignedNursesToWorkloadMapping context) {
		List<Term> terms = Collections.synchronizedList(new LinkedList<>());
		terms.add(new Term(context, 1.0));
		builder_0(terms, context);
		builder_1(terms, context);
		return terms;
	}
	
	@Override
	protected double buildConstantLhs(final AssignedNursesToWorkloadMapping context) {
		throw new UnsupportedOperationException("Constraint has an lhs that contains ilp variables.");
	}
	
	@Override
	protected boolean buildConstantExpression(final AssignedNursesToWorkloadMapping context) {
		throw new UnsupportedOperationException("Constraint has no constant boolean expression.");
	}
		
	@Override
	protected List<Constraint> buildAdditionalConstraints(final AssignedNursesToWorkloadMapping context) {
		throw new UnsupportedOperationException("Constraint has no depending or substitute constraints.");
	}
	protected void builder_1(final List<Term> terms, final AssignedNursesToWorkloadMapping context) {
		final GipsMapper<?> mapper = engine.getMapper("selectedShiftToRoster");
		final GlobalMappingIndexer globalIndexer = GlobalMappingIndexer.getInstance();
		globalIndexer.createIndexer(mapper);
		final MappingIndexer indexer = globalIndexer.getIndexer(mapper);
		if (!indexer.isInitialized()) {
			mapper.getMappings().values().parallelStream()
					.map(mapping -> (SelectedShiftToRosterMapping) mapping).forEach(elt -> {
						indexer.putMapping(elt.getVsr(), elt);
					});
		}
		
		indexer.getMappingsOfNode(context.getVsr()).parallelStream()
				.map(mapping -> (SelectedShiftToRosterMapping) mapping)
				.filter(elt -> elt.getVsr().equals(context.getVsr()))
				.forEach(elt -> {
					terms.add(new Term(elt, (double)(-1.0) * (1.0)));
				});
		
		// Old generated code
//		engine.getMapper("selectedShiftToRoster").getMappings().values().parallelStream()
//					.map(mapping -> (SelectedShiftToRosterMapping) mapping)
//		.filter(elt -> elt.getVsr().equals(context.getVsr()))
//		.forEach(elt -> {
//			terms.add(new Term(elt, (double)(-1.0) * (1.0)));
//		});
	}
	protected void builder_0(final List<Term> terms, final AssignedNursesToWorkloadMapping context) {
        final GipsMapper<?> mapper = engine.getMapper("assignedPatientsToRoom");
		final GlobalMappingIndexer globalIndexer = GlobalMappingIndexer.getInstance();
		globalIndexer.createIndexer(mapper);
		final MappingIndexer indexer = globalIndexer.getIndexer(mapper);
		if (!indexer.isInitialized()) {
			mapper.getMappings().values().parallelStream()
					.map(mapping -> (AssignedPatientsToRoomMapping) mapping).forEach(elt -> {
						indexer.putMapping(elt.getP(), elt);
						indexer.putMapping(elt.getR(), elt);
						indexer.putMapping(elt.getS(), elt);
						indexer.putMapping(elt.getVsw(), elt);
						indexer.putMapping(elt.getW(), elt);
					});
		}
		
		indexer.getMappingsOfNode(context.getVsw()).parallelStream()
				.map(mapping -> (AssignedPatientsToRoomMapping) mapping)
				.filter(elt -> elt.getVsw().equals(context.getVsw()))
				.forEach(elt -> {
					terms.add(new Term(elt, (double)(-1.0) * (1.0)));
				});
		
		// Old generated code
//		engine.getMapper("assignedPatientsToRoom").getMappings().values().parallelStream()
//					.map(mapping -> (AssignedPatientsToRoomMapping) mapping)
//		.filter(elt -> elt.getVsw().equals(context.getVsw()))
//		.forEach(elt -> {
//			terms.add(new Term(elt, (double)(-1.0) * (1.0)));
//		});
	}
}
