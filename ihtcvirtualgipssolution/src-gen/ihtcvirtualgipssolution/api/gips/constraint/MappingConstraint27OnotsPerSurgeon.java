package ihtcvirtualgipssolution.api.gips.constraint;		

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.emoflon.gips.core.GipsMapper;
import org.emoflon.gips.core.GipsMappingConstraint;
import org.emoflon.gips.core.GlobalMappingIndexer;
import org.emoflon.gips.core.MappingIndexer;
import org.emoflon.gips.core.milp.model.Constraint;
import org.emoflon.gips.core.milp.model.Term;
import org.emoflon.gips.intermediate.GipsIntermediate.MappingConstraint;

import ihtcvirtualgipssolution.api.gips.IhtcvirtualgipssolutionGipsAPI;
import ihtcvirtualgipssolution.api.gips.mapping.AssignedPatientsToRoomMapping;
import ihtcvirtualgipssolution.api.gips.mapping.OtForSurgeonMapping;
import ihtcvirtualgipssolution.api.gips.mapping.OtsPerSurgeonMapping;

public class MappingConstraint27OnotsPerSurgeon extends GipsMappingConstraint<IhtcvirtualgipssolutionGipsAPI, OtsPerSurgeonMapping>{
	public MappingConstraint27OnotsPerSurgeon(final IhtcvirtualgipssolutionGipsAPI engine, final MappingConstraint constraint) {
		super(engine, constraint);
	}
	
	@Override
	protected double buildConstantRhs(final OtsPerSurgeonMapping context) {
		return 0.0;
	}
		
	@Override
	protected List<Term> buildVariableLhs(final OtsPerSurgeonMapping context) {
		List<Term> terms = Collections.synchronizedList(new LinkedList<>());
		terms.add(new Term(engine.getNonMappingVariable(context, "otCount"), 1.0));
		builder_0(terms, context);
		return terms;
	}
	
	@Override
	protected double buildConstantLhs(final OtsPerSurgeonMapping context) {
		throw new UnsupportedOperationException("Constraint has an lhs that contains ilp variables.");
	}
	
	@Override
	protected boolean buildConstantExpression(final OtsPerSurgeonMapping context) {
		throw new UnsupportedOperationException("Constraint has no constant boolean expression.");
	}
		
	@Override
	protected List<Constraint> buildAdditionalConstraints(final OtsPerSurgeonMapping context) {
		throw new UnsupportedOperationException("Constraint has no depending or substitute constraints.");
	}
	protected void builder_0(final List<Term> terms, final OtsPerSurgeonMapping context) {
        final GipsMapper<?> mapper = engine.getMapper("otForSurgeon");
		final GlobalMappingIndexer globalIndexer = GlobalMappingIndexer.getInstance();
		globalIndexer.createIndexer(mapper);
		final MappingIndexer indexer = globalIndexer.getIndexer(mapper);
		if (!indexer.isInitialized()) {
			mapper.getMappings().values().parallelStream()
					.map(mapping -> (OtForSurgeonMapping) mapping).forEach(elt -> {
						indexer.putMapping(elt.getOp(), elt);
						indexer.putMapping(elt.getOt(), elt);
						indexer.putMapping(elt.getS(), elt);
					});
		}
		
		indexer.getMappingsOfNode(context.getOp()).parallelStream()
				.map(mapping -> (OtForSurgeonMapping) mapping)
				.filter(elt -> elt.getOp().equals(context.getOp()))
				.forEach(elt -> {
					terms.add(new Term(elt, (double)(-1.0) * (1.0)));
				});
		
		// Old generated code
//		engine.getMapper("otForSurgeon").getMappings().values().parallelStream()
//					.map(mapping -> (OtForSurgeonMapping) mapping)
//		.filter(elt -> elt.getOp().equals(context.getOp()))
//		.forEach(elt -> {
//			terms.add(new Term(elt, (double)(-1.0) * (1.0)));
//		});
	}
}
