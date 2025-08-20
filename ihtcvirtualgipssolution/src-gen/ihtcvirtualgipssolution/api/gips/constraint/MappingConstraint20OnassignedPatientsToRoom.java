package ihtcvirtualgipssolution.api.gips.constraint;		

import ihtcvirtualgipssolution.api.gips.mapping.SelectedExtendingShiftToFirstWorkloadMapping;
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
import ihtcvirtualgipssolution.api.gips.mapping.SelectedOccupantNodesMapping;
import java.util.List;
import ihtcvirtualgipssolution.api.gips.mapping.AssignedPatientsToRoomMapping;
import java.util.LinkedList;
import java.util.Collections;
import ihtcvirtualgipssolution.api.gips.mapping.SelectedShiftToFirstWorkloadMapping;

public class MappingConstraint20OnassignedPatientsToRoom extends GipsMappingConstraint<IhtcvirtualgipssolutionGipsAPI, AssignedPatientsToRoomMapping>{
	public MappingConstraint20OnassignedPatientsToRoom(final IhtcvirtualgipssolutionGipsAPI engine, final MappingConstraint constraint) {
		super(engine, constraint);
	}
	
	@Override
	protected double buildConstantRhs(final AssignedPatientsToRoomMapping context) {
		return 0.0;
	}
		
	@Override
	protected List<Term> buildVariableLhs(final AssignedPatientsToRoomMapping context) {
		List<Term> terms = Collections.synchronizedList(new LinkedList<>());
		terms.add(new Term(context, 1.0));
		builder_0(terms, context);
		builder_1(terms, context);
		builder_2(terms, context);
		return terms;
	}
	
	@Override
	protected double buildConstantLhs(final AssignedPatientsToRoomMapping context) {
		throw new UnsupportedOperationException("Constraint has an lhs that contains ilp variables.");
	}
	
	@Override
	protected boolean buildConstantExpression(final AssignedPatientsToRoomMapping context) {
		throw new UnsupportedOperationException("Constraint has no constant boolean expression.");
	}
		
	@Override
	protected List<Constraint> buildAdditionalConstraints(final AssignedPatientsToRoomMapping context) {
		throw new UnsupportedOperationException("Constraint has no depending or substitute constraints.");
	}
	protected void builder_2(final List<Term> terms, final AssignedPatientsToRoomMapping context) {
		final GipsMapper<?> mapper = engine.getMapper("selectedOccupantNodes");
		final GlobalMappingIndexer globalIndexer = GlobalMappingIndexer.getInstance();
		globalIndexer.createIndexer(mapper);
		final MappingIndexer indexer = globalIndexer.getIndexer(mapper);
		if (!indexer.isInitialized()) {
			mapper.getMappings().values().parallelStream()
					.map(mapping -> (SelectedOccupantNodesMapping) mapping).forEach(elt -> {
						indexer.putMapping(elt.getVsw(), elt);
					});
		}
		
		indexer.getMappingsOfNode(context.getVsw()).parallelStream()
				.map(mapping -> (SelectedOccupantNodesMapping) mapping).forEach(elt -> {
					terms.add(new Term(elt, (double)(-1.0) * (1.0)));
				});
		
		// Old generated code
//		engine.getMapper("selectedOccupantNodes").getMappings().values().parallelStream()
//					.map(mapping -> (SelectedOccupantNodesMapping) mapping)
//		.filter(elt -> elt.getVsw().equals(context.getVsw()))
//		.forEach(elt -> {
//			terms.add(new Term(elt, (double)(-1.0) * (1.0)));
//		});
	}
	protected void builder_0(final List<Term> terms, final AssignedPatientsToRoomMapping context) {
		final GipsMapper<?> mapper = engine.getMapper("selectedShiftToFirstWorkload");
		final GlobalMappingIndexer globalIndexer = GlobalMappingIndexer.getInstance();
		globalIndexer.createIndexer(mapper);
		final MappingIndexer indexer = globalIndexer.getIndexer(mapper);
		if (!indexer.isInitialized()) {
			mapper.getMappings().values().parallelStream()
					.map(mapping -> (SelectedShiftToFirstWorkloadMapping) mapping).forEach(elt -> {
						indexer.putMapping(elt.getVsw(), elt);
					});
		}
		
		indexer.getMappingsOfNode(context.getVsw()).parallelStream()
				.map(mapping -> (SelectedShiftToFirstWorkloadMapping) mapping).forEach(elt -> {
					terms.add(new Term(elt, (double)(-1.0) * (1.0)));
				});
		
		// Old generated code
//		engine.getMapper("selectedShiftToFirstWorkload").getMappings().values().parallelStream()
//					.map(mapping -> (SelectedShiftToFirstWorkloadMapping) mapping)
//		.filter(elt -> elt.getVsw().equals(context.getVsw()))
//		.forEach(elt -> {
//			terms.add(new Term(elt, (double)(-1.0) * (1.0)));
//		});
	}
	protected void builder_1(final List<Term> terms, final AssignedPatientsToRoomMapping context) {
		final GipsMapper<?> mapper = engine.getMapper("selectedExtendingShiftToFirstWorkload");
		final GlobalMappingIndexer globalIndexer = GlobalMappingIndexer.getInstance();
		globalIndexer.createIndexer(mapper);
		final MappingIndexer indexer = globalIndexer.getIndexer(mapper);
		if (!indexer.isInitialized()) {
			mapper.getMappings().values().parallelStream()
					.map(mapping -> (SelectedExtendingShiftToFirstWorkloadMapping) mapping).forEach(elt -> {
						indexer.putMapping(elt.getNextvsw(), elt);
					});
		}
		
		indexer.getMappingsOfNode(context.getVsw()).parallelStream()
				.map(mapping -> (SelectedExtendingShiftToFirstWorkloadMapping) mapping)
				.forEach(elt -> {
					terms.add(new Term(elt, (double)(-1.0) * (1.0)));
				});
		
		// Old generated code
//		engine.getMapper("selectedExtendingShiftToFirstWorkload").getMappings().values().parallelStream()
//					.map(mapping -> (SelectedExtendingShiftToFirstWorkloadMapping) mapping)
//		.filter(elt -> elt.getNextvsw().equals(context.getVsw()))
//		.forEach(elt -> {
//			terms.add(new Term(elt, (double)(-1.0) * (1.0)));
//		});
	}
}
