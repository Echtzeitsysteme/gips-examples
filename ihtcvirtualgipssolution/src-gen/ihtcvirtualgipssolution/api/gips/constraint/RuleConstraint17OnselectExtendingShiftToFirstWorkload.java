package ihtcvirtualgipssolution.api.gips.constraint;		

import ihtcvirtualgipssolution.api.gips.mapping.SelectedExtendingShiftToFirstWorkloadMapping;
import org.emoflon.gips.core.GipsEngine;
import org.emoflon.gips.core.GipsMapper;
import org.emoflon.gips.core.GlobalMappingIndexer;
import org.emoflon.gips.core.MappingIndexer;
import org.emoflon.gips.core.milp.model.Constraint;
import org.emoflon.gips.intermediate.GipsIntermediate.RuleConstraint;
import ihtcvirtualgipssolution.api.matches.SelectExtendingShiftToFirstWorkloadMatch;
import ihtcvirtualgipssolution.api.gips.IhtcvirtualgipssolutionGipsAPI;
import org.emoflon.gips.core.milp.model.Term;
import java.util.stream.Collectors;
import org.emoflon.gips.core.gt.GipsRuleConstraint;
import java.util.List;
import ihtcvirtualgipssolution.api.rules.SelectExtendingShiftToFirstWorkloadRule;
import java.util.LinkedList;
import java.util.Collections;
import ihtcvirtualgipssolution.api.gips.mapping.SelectedShiftToFirstWorkloadMapping;

public class RuleConstraint17OnselectExtendingShiftToFirstWorkload extends GipsRuleConstraint<IhtcvirtualgipssolutionGipsAPI, SelectExtendingShiftToFirstWorkloadMatch, SelectExtendingShiftToFirstWorkloadRule>{
	public RuleConstraint17OnselectExtendingShiftToFirstWorkload(final IhtcvirtualgipssolutionGipsAPI engine, final RuleConstraint constraint, final SelectExtendingShiftToFirstWorkloadRule rule) {
		super(engine, constraint, rule);
	}
	
	@Override
	protected double buildConstantRhs(final SelectExtendingShiftToFirstWorkloadMatch context) {
		return 0.0;
	}
		
	@Override
	protected List<Term> buildVariableLhs(final SelectExtendingShiftToFirstWorkloadMatch context) {
		List<Term> terms = Collections.synchronizedList(new LinkedList<>());
		builder_0(terms, context);
		builder_1(terms, context);
		builder_2(terms, context);
		return terms;
	}
	
	@Override
	protected double buildConstantLhs(final SelectExtendingShiftToFirstWorkloadMatch context) {
		throw new UnsupportedOperationException("Constraint has an lhs that contains ilp variables.");
	}
	
	@Override
	protected boolean buildConstantExpression(final SelectExtendingShiftToFirstWorkloadMatch context) {
		throw new UnsupportedOperationException("Constraint has no constant boolean expression.");
	}
		
	@Override
	protected List<Constraint> buildAdditionalConstraints(final SelectExtendingShiftToFirstWorkloadMatch context) {
		throw new UnsupportedOperationException("Constraint has no depending or substitute constraints.");
	}
	protected void builder_2(final List<Term> terms, final SelectExtendingShiftToFirstWorkloadMatch context) {
		final GipsMapper<?> mapper = engine.getMapper("selectedShiftToFirstWorkload");
		final GlobalMappingIndexer globalIndexer = GlobalMappingIndexer.getInstance();
		globalIndexer.createIndexer(mapper);
		final MappingIndexer indexer = globalIndexer.getIndexer(mapper);
		if (!indexer.isInitialized()) {
			mapper.getMappings().values().parallelStream()
					.map(mapping -> (SelectedShiftToFirstWorkloadMapping) mapping).forEach(elt -> {
						indexer.putMapping(elt.getVsw(), elt);
						//
						indexer.putMapping(elt.getP(), elt);
						indexer.putMapping(elt.getVwc(), elt);
						indexer.putMapping(elt.getW(), elt);
					});
		}
		
		indexer.getMappingsOfNode(context.getPrevvsw()).parallelStream()
				.map(mapping -> (SelectedShiftToFirstWorkloadMapping) mapping)
				.forEach(elt -> {
					terms.add(new Term(elt, (double)(-1.0) * (1.0)));
				});
		
		// Old generated code
//		engine.getMapper("selectedShiftToFirstWorkload").getMappings().values().parallelStream()
//					.map(mapping -> (SelectedShiftToFirstWorkloadMapping) mapping)
//		.filter(elt -> elt.getVsw().equals(context.getPrevvsw()))
//		.forEach(elt -> {
//			terms.add(new Term(elt, (double)(-1.0) * (1.0)));
//		});
	}
	protected void builder_1(final List<Term> terms, final SelectExtendingShiftToFirstWorkloadMatch context) {
		final GipsMapper<?> mapper = engine.getMapper("selectedExtendingShiftToFirstWorkload");
		final GlobalMappingIndexer globalIndexer = GlobalMappingIndexer.getInstance();
		globalIndexer.createIndexer(mapper);
		final MappingIndexer indexer = globalIndexer.getIndexer(mapper);
		if (!indexer.isInitialized()) {
			mapper.getMappings().values().parallelStream()
					.map(mapping -> (SelectedExtendingShiftToFirstWorkloadMapping) mapping).forEach(elt -> {
						indexer.putMapping(elt.getNextvsw(), elt);
						//
						indexer.putMapping(elt.getPrevvsw(), elt);
					});
		}
		
		indexer.getMappingsOfNode(context.getPrevvsw()).parallelStream()
				.map(mapping -> (SelectedExtendingShiftToFirstWorkloadMapping) mapping)
				.forEach(elt -> {
					terms.add(new Term(elt, (double)(-1.0) * (1.0)));
				});
		
		// Old generated code
//		engine.getMapper("selectedExtendingShiftToFirstWorkload").getMappings().values().parallelStream()
//					.map(mapping -> (SelectedExtendingShiftToFirstWorkloadMapping) mapping)
//		.filter(elt -> elt.getNextvsw().equals(context.getPrevvsw()))
//		.forEach(elt -> {
//			terms.add(new Term(elt, (double)(-1.0) * (1.0)));
//		});
	}
	protected void builder_0(final List<Term> terms, final SelectExtendingShiftToFirstWorkloadMatch context) {
		final GipsMapper<?> mapper = engine.getMapper("selectedExtendingShiftToFirstWorkload");
		final GlobalMappingIndexer globalIndexer = GlobalMappingIndexer.getInstance();
		globalIndexer.createIndexer(mapper);
		final MappingIndexer indexer = globalIndexer.getIndexer(mapper);
		if (!indexer.isInitialized()) {
			mapper.getMappings().values().parallelStream()
					.map(mapping -> (SelectedExtendingShiftToFirstWorkloadMapping) mapping).forEach(elt -> {
						indexer.putMapping(elt.getNextvsw(), elt);
						//
						indexer.putMapping(elt.getPrevvsw(), elt);
					});
		}
		
		indexer.getMappingsOfNode(context.getNextvsw()).parallelStream()
				.map(mapping -> (SelectedExtendingShiftToFirstWorkloadMapping) mapping)
				.forEach(elt -> {
					terms.add(new Term(elt, (double)1.0));
				});
		
//		// Old generated code
//		engine.getMapper("selectedExtendingShiftToFirstWorkload").getMappings().values().parallelStream()
//					.map(mapping -> (SelectedExtendingShiftToFirstWorkloadMapping) mapping)
//		.filter(elt -> elt.getNextvsw().equals(context.getNextvsw()))
//		.forEach(elt -> {
//			terms.add(new Term(elt, (double)1.0));
//		});
	}
}
