package ihtcvirtualgipssolution.api.gips.constraint;		

import org.emoflon.gips.core.GipsEngine;
import org.emoflon.gips.core.GipsMapper;
import org.emoflon.gips.core.GlobalMappingIndexer;
import org.emoflon.gips.core.MappingIndexer;
import org.emoflon.gips.core.milp.model.Constraint;
import ihtcvirtualgipssolution.api.gips.mapping.SelectedOperationDayMapping;
import org.emoflon.gips.intermediate.GipsIntermediate.RuleConstraint;
import ihtcvirtualgipssolution.api.gips.IhtcvirtualgipssolutionGipsAPI;
import ihtcvirtualgipssolution.api.matches.SelectShiftToFirstWorkloadMatch;
import org.emoflon.gips.core.milp.model.Term;
import java.util.stream.Collectors;
import org.emoflon.gips.core.gt.GipsRuleConstraint;
import java.util.List;
import java.util.Set;

import ihtcvirtualgipssolution.api.rules.SelectShiftToFirstWorkloadRule;
import java.util.LinkedList;
import java.util.Collections;
import ihtcvirtualgipssolution.api.gips.mapping.SelectedShiftToFirstWorkloadMapping;

public class RuleConstraint15OnselectShiftToFirstWorkload extends GipsRuleConstraint<IhtcvirtualgipssolutionGipsAPI, SelectShiftToFirstWorkloadMatch, SelectShiftToFirstWorkloadRule>{
	public RuleConstraint15OnselectShiftToFirstWorkload(final IhtcvirtualgipssolutionGipsAPI engine, final RuleConstraint constraint, final SelectShiftToFirstWorkloadRule rule) {
		super(engine, constraint, rule);
	}
	
	@Override
	protected double buildConstantRhs(final SelectShiftToFirstWorkloadMatch context) {
		return 0.0;
	}
		
	@Override
	protected List<Term> buildVariableLhs(final SelectShiftToFirstWorkloadMatch context) {
		List<Term> terms = Collections.synchronizedList(new LinkedList<>());
		builder_0(terms, context);
		builder_1(terms, context);
		return terms;
	}
	
	@Override
	protected double buildConstantLhs(final SelectShiftToFirstWorkloadMatch context) {
		throw new UnsupportedOperationException("Constraint has an lhs that contains ilp variables.");
	}
	
	@Override
	protected boolean buildConstantExpression(final SelectShiftToFirstWorkloadMatch context) {
		throw new UnsupportedOperationException("Constraint has no constant boolean expression.");
	}
		
	@Override
	protected List<Constraint> buildAdditionalConstraints(final SelectShiftToFirstWorkloadMatch context) {
		throw new UnsupportedOperationException("Constraint has no depending or substitute constraints.");
	}
	protected void builder_0(final List<Term> terms, final SelectShiftToFirstWorkloadMatch context) {
		final GipsMapper<?> mapper = engine.getMapper("selectedShiftToFirstWorkload");
		final GlobalMappingIndexer globalIndexer = GlobalMappingIndexer.getInstance();
		globalIndexer.createIndexer(mapper);
		final MappingIndexer indexer = globalIndexer.getIndexer(mapper);
		if (!indexer.isInitialized()) {
			mapper.getMappings().values().parallelStream()
					.map(mapping -> (SelectedShiftToFirstWorkloadMapping) mapping).forEach(elt -> {
						indexer.putMapping(elt.getVsw(), elt);
						indexer.putMapping(elt.getVwc(), elt);
						//
						indexer.putMapping(elt.getP(), elt);
						indexer.putMapping(elt.getW(), elt);
					});
		}

		indexer.getMappingsOfNodes(Set.of(context.getVsw(), context.getVwc())).parallelStream()
				.map(mapping -> (SelectedShiftToFirstWorkloadMapping) mapping).forEach(elt -> {
					terms.add(new Term(elt, (double) 1.0));
				});

		// Old generated code
//		engine.getMapper("selectedShiftToFirstWorkload").getMappings().values().parallelStream()
//					.map(mapping -> (SelectedShiftToFirstWorkloadMapping) mapping)
//		.filter(elt -> elt.getVsw().equals(context.getVsw()) && elt.getVwc().equals(context.getVwc()))
//		.forEach(elt -> {
//			terms.add(new Term(elt, (double)1.0));
//		});
	}

	protected void builder_1(final List<Term> terms, final SelectShiftToFirstWorkloadMatch context) {
		final GipsMapper<?> mapper = engine.getMapper("selectedOperationDay");
		final GlobalMappingIndexer globalIndexer = GlobalMappingIndexer.getInstance();
		globalIndexer.createIndexer(mapper);
		final MappingIndexer indexer = globalIndexer.getIndexer(mapper);
		if (!indexer.isInitialized()) {
			mapper.getMappings().values().parallelStream()
					.map(mapping -> (SelectedOperationDayMapping) mapping).forEach(elt -> {
						indexer.putMapping(elt.getVwc(), elt);
						//
						indexer.putMapping(elt.getP(), elt);
						indexer.putMapping(elt.getC(), elt);
						indexer.putMapping(elt.getOpTime(), elt);
						indexer.putMapping(elt.getS(), elt);
						indexer.putMapping(elt.getVopc(), elt);
						indexer.putMapping(elt.getVwop(), elt);
						indexer.putMapping(elt.getW(), elt);
					});
		}

		indexer.getMappingsOfNode(context.getVwc()).parallelStream()
				.map(mapping -> (SelectedOperationDayMapping) mapping)
				.filter(elt -> elt.getVopc().getCapacity().getDay() == (context.getVsw().getShift().getShiftNo()) / (3))
				.forEach(elt -> {
					terms.add(new Term(elt, (double) (-1.0) * (1.0)));
				});

		// Old generated code
//		engine.getMapper("selectedOperationDay").getMappings().values().parallelStream()
//				.map(mapping -> (SelectedOperationDayMapping) mapping)
//				.filter(elt -> elt.getVwc().equals(context.getVwc())
//						&& elt.getVopc().getCapacity().getDay() == (context.getVsw().getShift().getShiftNo()) / (3))
//				.forEach(elt -> {
//					terms.add(new Term(elt, (double) (-1.0) * (1.0)));
//				});
	}
}
