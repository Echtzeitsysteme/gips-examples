package ihtcvirtualgipssolution.api.gips.constraint;		

import org.emoflon.gips.core.GipsEngine;
import org.emoflon.gips.core.GipsMapper;
import org.emoflon.gips.core.milp.model.Constraint;
import ihtcvirtualgipssolution.api.gips.IhtcvirtualgipssolutionGipsAPI;
import org.eclipse.emf.ecore.EObject;
import org.emoflon.gips.core.GipsTypeConstraint;
import org.emoflon.gips.core.GlobalMappingIndexer;
import org.emoflon.gips.core.MappingIndexer;
import org.emoflon.gips.core.milp.model.Term;
import org.emoflon.gips.intermediate.GipsIntermediate.TypeConstraint;
import java.util.stream.Collectors;
import org.emoflon.gips.core.GipsMapping;
import java.util.List;
import org.eclipse.emf.ecore.EClass;
import ihtcvirtualmetamodel.Patient;
import java.util.LinkedList;
import java.util.Collections;
import ihtcvirtualgipssolution.api.gips.mapping.SelectedShiftToFirstWorkloadMapping;

public class TypeConstraint13OnPatient extends GipsTypeConstraint<IhtcvirtualgipssolutionGipsAPI, Patient>{
	public TypeConstraint13OnPatient(final IhtcvirtualgipssolutionGipsAPI engine, final TypeConstraint constraint) {
		super(engine, constraint);
	}
	
	@Override
	protected double buildConstantRhs(final Patient context) {
		return 1;
	}
		
	@Override
	protected List<Term> buildVariableLhs(final Patient context) {
		List<Term> terms = Collections.synchronizedList(new LinkedList<>());
		builder_0(terms, context);
		return terms;
	}
	
	@Override
	protected double buildConstantLhs(final Patient context) {
		throw new UnsupportedOperationException("Constraint has an lhs that contains ilp variables.");
	}
	
	@Override
	protected boolean buildConstantExpression(final Patient context) {
		throw new UnsupportedOperationException("Constraint has no constant boolean expression.");
	}
		
	@Override
	protected List<Constraint> buildAdditionalConstraints(final Patient context) {
		throw new UnsupportedOperationException("Constraint has no depending or substitute constraints.");
	}
	protected void builder_0(final List<Term> terms, final Patient context) {
		final GipsMapper<?> mapper = engine.getMapper("selectedShiftToFirstWorkload");
		final GlobalMappingIndexer globalIndexer = GlobalMappingIndexer.getInstance();
		globalIndexer.createIndexer(mapper);
		final MappingIndexer indexer = globalIndexer.getIndexer(mapper);
		if (!indexer.isInitialized()) {
			mapper.getMappings().values().parallelStream()
					.map(mapping -> (SelectedShiftToFirstWorkloadMapping) mapping).forEach(elt -> {
						indexer.putMapping(elt.getP(), elt);
						//
						indexer.putMapping(elt.getVsw(), elt);
						indexer.putMapping(elt.getVwc(), elt);
						indexer.putMapping(elt.getW(), elt);
					});
		}
		
		indexer.getMappingsOfNode(context).parallelStream()
				.map(mapping -> (SelectedShiftToFirstWorkloadMapping) mapping)
				.filter(elt -> elt.getP().equals(context))
				.forEach(elt -> {
					terms.add(new Term(elt, (double)1.0));
				});
		
			
		// Old generated code
//		engine.getMapper("selectedShiftToFirstWorkload").getMappings().values().parallelStream()
//					.map(mapping -> (SelectedShiftToFirstWorkloadMapping) mapping)
//		.filter(elt -> elt.getP().equals(context))
//		.forEach(elt -> {
//			terms.add(new Term(elt, (double)1.0));
//		});
	}
}
