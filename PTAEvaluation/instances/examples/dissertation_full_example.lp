\ LP format - for model browsing. Use MPS format to capture full model detail.
Minimize
  0.003 aom#2 + 100 aom#2->hours + 150 aom#2->overTime + 0.002 aom#3
   + 30 aom#3->hours + 65 aom#3->overTime + 0.001 aom#0 + 65 aom#0->hours
   + 100 aom#0->overTime + 0.002 aom#1 + 100 aom#1->hours
   + 150 aom#1->overTime + 0.002 aom#6 + 30 aom#6->hours
   + 65 aom#6->overTime + 0.003 aom#7 + 100 aom#7->hours
   + 150 aom#7->overTime + 0.002 aom#4 + 100 aom#4->hours
   + 150 aom#4->overTime + 0.001 aom#5 + 65 aom#5->hours
   + 100 aom#5->overTime + 0.002 aom#8 + 30 aom#8->hours
   + 65 aom#8->overTime
Subject To
 MappingConstraint7Onaom_GEQ1_0: aom#6
   - 10000 aom#6->DisjunctMappingConstraint5Onaom_symbolic1#13 <= 0.9999
 MappingConstraint7Onaom_GEQ1_1: aom#8
   - 10000 aom#8->DisjunctMappingConstraint5Onaom_symbolic1#17 <= 0.9999
 MappingConstraint7Onaom_GEQ1_2: aom#2
   - 10000 aom#2->DisjunctMappingConstraint5Onaom_symbolic1#9 <= 0.9999
 MappingConstraint7Onaom_GEQ1_3: aom#3
   - 10000 aom#3->DisjunctMappingConstraint5Onaom_symbolic1#10 <= 0.9999
 MappingConstraint7Onaom_GEQ1_4: aom#1
   - 10000 aom#1->DisjunctMappingConstraint5Onaom_symbolic1#12 <= 0.9999
 MappingConstraint7Onaom_GEQ1_5: aom#5
   - 10000 aom#5->DisjunctMappingConstraint5Onaom_symbolic1#16 <= 0.9999
 MappingConstraint7Onaom_GEQ1_6: aom#7
   - 10000 aom#7->DisjunctMappingConstraint5Onaom_symbolic1#14 <= 0.9999
 MappingConstraint7Onaom_GEQ1_7: aom#4
   - 10000 aom#4->DisjunctMappingConstraint5Onaom_symbolic1#15 <= 0.9999
 MappingConstraint7Onaom_GEQ1_8: aom#0
   - 10000 aom#0->DisjunctMappingConstraint5Onaom_symbolic1#11 <= 0.9999
 MappingConstraint7Onaom_GEQ0_0: aom#6
   - 10000 aom#6->DisjunctMappingConstraint5Onaom_symbolic1#13 >= -9999
 MappingConstraint7Onaom_GEQ0_1: aom#8
   - 10000 aom#8->DisjunctMappingConstraint5Onaom_symbolic1#17 >= -9999
 MappingConstraint7Onaom_GEQ0_2: aom#2
   - 10000 aom#2->DisjunctMappingConstraint5Onaom_symbolic1#9 >= -9999
 MappingConstraint7Onaom_GEQ0_3: aom#3
   - 10000 aom#3->DisjunctMappingConstraint5Onaom_symbolic1#10 >= -9999
 MappingConstraint7Onaom_GEQ0_4: aom#1
   - 10000 aom#1->DisjunctMappingConstraint5Onaom_symbolic1#12 >= -9999
 MappingConstraint7Onaom_GEQ0_5: aom#5
   - 10000 aom#5->DisjunctMappingConstraint5Onaom_symbolic1#16 >= -9999
 MappingConstraint7Onaom_GEQ0_6: aom#7
   - 10000 aom#7->DisjunctMappingConstraint5Onaom_symbolic1#14 >= -9999
 MappingConstraint7Onaom_GEQ0_7: aom#4
   - 10000 aom#4->DisjunctMappingConstraint5Onaom_symbolic1#15 >= -9999
 MappingConstraint7Onaom_GEQ0_8: aom#0
   - 10000 aom#0->DisjunctMappingConstraint5Onaom_symbolic1#11 >= -9999
 DisjunctPatternConstraint13OntaskSequence_0:
   - taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic1#6
   + taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic0#0
   >= 0
 DisjunctPatternConstraint13OntaskSequence_1:
   taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic0#1
   - taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic1#7
   >= 0
 DisjunctPatternConstraint13OntaskSequence_2:
   taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic0#2
   - taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic1#8
   >= 0
 DisjunctPatternConstraint13OntaskSequence_3:
   - taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic1#9
   + taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic0#3
   >= 0
 DisjunctPatternConstraint13OntaskSequence_4:
   taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic0#4
   - taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic1#10
   >= 0
 DisjunctPatternConstraint13OntaskSequence_5:
   - taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic1#11
   + taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic0#5
   >= 0
 DisjunctMappingConstraint8Onaom_0:
   - aom#6->DisjunctMappingConstraint8Onaom_symbolic1#13
   + aom#6->DisjunctMappingConstraint8Onaom_symbolic0#4 >= 0
 DisjunctMappingConstraint8Onaom_1:
   - aom#8->DisjunctMappingConstraint8Onaom_symbolic1#17
   + aom#8->DisjunctMappingConstraint8Onaom_symbolic0#8 >= 0
 DisjunctMappingConstraint8Onaom_2:
   - aom#2->DisjunctMappingConstraint8Onaom_symbolic1#9
   + aom#2->DisjunctMappingConstraint8Onaom_symbolic0#0 >= 0
 DisjunctMappingConstraint8Onaom_3:
   - aom#3->DisjunctMappingConstraint8Onaom_symbolic1#10
   + aom#3->DisjunctMappingConstraint8Onaom_symbolic0#1 >= 0
 DisjunctMappingConstraint8Onaom_4:
   - aom#1->DisjunctMappingConstraint8Onaom_symbolic1#12
   + aom#1->DisjunctMappingConstraint8Onaom_symbolic0#3 >= 0
 DisjunctMappingConstraint8Onaom_5:
   - aom#5->DisjunctMappingConstraint8Onaom_symbolic1#16
   + aom#5->DisjunctMappingConstraint8Onaom_symbolic0#7 >= 0
 DisjunctMappingConstraint8Onaom_6:
   aom#7->DisjunctMappingConstraint8Onaom_symbolic0#5
   - aom#7->DisjunctMappingConstraint8Onaom_symbolic1#14 >= 0
 DisjunctMappingConstraint8Onaom_7:
   aom#4->DisjunctMappingConstraint8Onaom_symbolic0#6
   - aom#4->DisjunctMappingConstraint8Onaom_symbolic1#15 >= 0
 DisjunctMappingConstraint8Onaom_8:
   aom#0->DisjunctMappingConstraint8Onaom_symbolic0#2
   - aom#0->DisjunctMappingConstraint8Onaom_symbolic1#11 >= 0
 PatternConstraint18OnfinalTaskSequence_GEQ1_0: aom#7
   - 10000 finalTaskSequence->DisjunctPatternConstraint16OnfinalTaskSequence_symbolic1#3
   <= 0.9999
 PatternConstraint18OnfinalTaskSequence_GEQ1_1: aom#4
   - 10000 finalTaskSequence->DisjunctPatternConstraint16OnfinalTaskSequence_symbolic1#4
   <= 0.9999
 PatternConstraint18OnfinalTaskSequence_GEQ1_2:
   - 10000 finalTaskSequence->DisjunctPatternConstraint16OnfinalTaskSequence_symbolic1#5
   <= 0.9999
 MappingConstraint3OnprojectCost_0: projectCost#0 >= 1
 DisjunctMappingConstraint5Onaom_0:
   aom#6->DisjunctMappingConstraint5Onaom_symbolic1#13
   - aom#6->DisjunctMappingConstraint5Onaom_symbolic0#4 >= 0
 DisjunctMappingConstraint5Onaom_1:
   aom#8->DisjunctMappingConstraint5Onaom_symbolic1#17
   - aom#8->DisjunctMappingConstraint5Onaom_symbolic0#8 >= 0
 DisjunctMappingConstraint5Onaom_2:
   aom#2->DisjunctMappingConstraint5Onaom_symbolic1#9
   - aom#2->DisjunctMappingConstraint5Onaom_symbolic0#0 >= 0
 DisjunctMappingConstraint5Onaom_3:
   aom#3->DisjunctMappingConstraint5Onaom_symbolic1#10
   - aom#3->DisjunctMappingConstraint5Onaom_symbolic0#1 >= 0
 DisjunctMappingConstraint5Onaom_4:
   aom#1->DisjunctMappingConstraint5Onaom_symbolic1#12
   - aom#1->DisjunctMappingConstraint5Onaom_symbolic0#3 >= 0
 DisjunctMappingConstraint5Onaom_5:
   aom#5->DisjunctMappingConstraint5Onaom_symbolic1#16
   - aom#5->DisjunctMappingConstraint5Onaom_symbolic0#7 >= 0
 DisjunctMappingConstraint5Onaom_6:
   aom#7->DisjunctMappingConstraint5Onaom_symbolic1#14
   - aom#7->DisjunctMappingConstraint5Onaom_symbolic0#5 >= 0
 DisjunctMappingConstraint5Onaom_7:
   aom#4->DisjunctMappingConstraint5Onaom_symbolic1#15
   - aom#4->DisjunctMappingConstraint5Onaom_symbolic0#6 >= 0
 DisjunctMappingConstraint5Onaom_8:
   aom#0->DisjunctMappingConstraint5Onaom_symbolic1#11
   - aom#0->DisjunctMappingConstraint5Onaom_symbolic0#2 >= 0
 PatternConstraint15OntaskSequence_GEQ0_0: aom#2
   - 10000 taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic1#6
   >= -9999
 PatternConstraint15OntaskSequence_GEQ0_1: aom#0 + aom#5
   - 10000 taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic1#7
   >= -9999
 PatternConstraint15OntaskSequence_GEQ0_2:
   - 10000 taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic1#8
   >= -9999
 PatternConstraint15OntaskSequence_GEQ0_3: aom#3 + aom#1 + aom#6 + aom#8
   - 10000 taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic1#9
   >= -9999
 PatternConstraint15OntaskSequence_GEQ0_4:
   - 10000 taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic1#10
   >= -9999
 PatternConstraint15OntaskSequence_GEQ0_5:
   - 10000 taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic1#11
   >= -9999
 PatternConstraint15OntaskSequence_GEQ1_0: aom#2
   - 10000 taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic1#6
   <= 0.9999
 PatternConstraint15OntaskSequence_GEQ1_1: aom#0 + aom#5
   - 10000 taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic1#7
   <= 0.9999
 PatternConstraint15OntaskSequence_GEQ1_2:
   - 10000 taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic1#8
   <= 0.9999
 PatternConstraint15OntaskSequence_GEQ1_3: aom#3 + aom#1 + aom#6 + aom#8
   - 10000 taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic1#9
   <= 0.9999
 PatternConstraint15OntaskSequence_GEQ1_4:
   - 10000 taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic1#10
   <= 0.9999
 PatternConstraint15OntaskSequence_GEQ1_5:
   - 10000 taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic1#11
   <= 0.9999
 MappingConstraint9Onaom_GEQ0_0: aom#6->hours + aom#6->overTime
   - 10000 aom#6->DisjunctMappingConstraint8Onaom_symbolic0#4 >= -9999
 MappingConstraint9Onaom_GEQ0_1: aom#8->hours + aom#8->overTime
   - 10000 aom#8->DisjunctMappingConstraint8Onaom_symbolic0#8 >= -9999
 MappingConstraint9Onaom_GEQ0_2: aom#2->hours + aom#2->overTime
   - 10000 aom#2->DisjunctMappingConstraint8Onaom_symbolic0#0 >= -9999
 MappingConstraint9Onaom_GEQ0_3: aom#3->hours + aom#3->overTime
   - 10000 aom#3->DisjunctMappingConstraint8Onaom_symbolic0#1 >= -9999
 MappingConstraint9Onaom_GEQ0_4: aom#1->hours + aom#1->overTime
   - 10000 aom#1->DisjunctMappingConstraint8Onaom_symbolic0#3 >= -9999
 MappingConstraint9Onaom_GEQ0_5: aom#5->hours + aom#5->overTime
   - 10000 aom#5->DisjunctMappingConstraint8Onaom_symbolic0#7 >= -9999
 MappingConstraint9Onaom_GEQ0_6: aom#7->hours + aom#7->overTime
   - 10000 aom#7->DisjunctMappingConstraint8Onaom_symbolic0#5 >= -9999
 MappingConstraint9Onaom_GEQ0_7: aom#4->hours + aom#4->overTime
   - 10000 aom#4->DisjunctMappingConstraint8Onaom_symbolic0#6 >= -9999
 MappingConstraint9Onaom_GEQ0_8: aom#0->hours + aom#0->overTime
   - 10000 aom#0->DisjunctMappingConstraint8Onaom_symbolic0#2 >= -9999
 MappingConstraint9Onaom_GEQ1_0: aom#6->hours + aom#6->overTime
   - 10000 aom#6->DisjunctMappingConstraint8Onaom_symbolic0#4 <= 0.9999
 MappingConstraint9Onaom_GEQ1_1: aom#8->hours + aom#8->overTime
   - 10000 aom#8->DisjunctMappingConstraint8Onaom_symbolic0#8 <= 0.9999
 MappingConstraint9Onaom_GEQ1_2: aom#2->hours + aom#2->overTime
   - 10000 aom#2->DisjunctMappingConstraint8Onaom_symbolic0#0 <= 0.9999
 MappingConstraint9Onaom_GEQ1_3: aom#3->hours + aom#3->overTime
   - 10000 aom#3->DisjunctMappingConstraint8Onaom_symbolic0#1 <= 0.9999
 MappingConstraint9Onaom_GEQ1_4: aom#1->hours + aom#1->overTime
   - 10000 aom#1->DisjunctMappingConstraint8Onaom_symbolic0#3 <= 0.9999
 MappingConstraint9Onaom_GEQ1_5: aom#5->hours + aom#5->overTime
   - 10000 aom#5->DisjunctMappingConstraint8Onaom_symbolic0#7 <= 0.9999
 MappingConstraint9Onaom_GEQ1_6: aom#7->hours + aom#7->overTime
   - 10000 aom#7->DisjunctMappingConstraint8Onaom_symbolic0#5 <= 0.9999
 MappingConstraint9Onaom_GEQ1_7: aom#4->hours + aom#4->overTime
   - 10000 aom#4->DisjunctMappingConstraint8Onaom_symbolic0#6 <= 0.9999
 MappingConstraint9Onaom_GEQ1_8: aom#0->hours + aom#0->overTime
   - 10000 aom#0->DisjunctMappingConstraint8Onaom_symbolic0#2 <= 0.9999
 PatternConstraint18OnfinalTaskSequence_GEQ0_0: aom#7
   - 10000 finalTaskSequence->DisjunctPatternConstraint16OnfinalTaskSequence_symbolic1#3
   >= -9999
 PatternConstraint18OnfinalTaskSequence_GEQ0_1: aom#4
   - 10000 finalTaskSequence->DisjunctPatternConstraint16OnfinalTaskSequence_symbolic1#4
   >= -9999
 PatternConstraint18OnfinalTaskSequence_GEQ0_2:
   - 10000 finalTaskSequence->DisjunctPatternConstraint16OnfinalTaskSequence_symbolic1#5
   >= -9999
 MappingConstraint6Onaom_GEQ0_0: aom#6->hours + aom#6->overTime
   - 10000 aom#6->DisjunctMappingConstraint5Onaom_symbolic0#4 >= -9999
 MappingConstraint6Onaom_GEQ0_1: aom#8->hours + aom#8->overTime
   - 10000 aom#8->DisjunctMappingConstraint5Onaom_symbolic0#8 >= -9999
 MappingConstraint6Onaom_GEQ0_2: aom#2->hours + aom#2->overTime
   - 10000 aom#2->DisjunctMappingConstraint5Onaom_symbolic0#0 >= -9999
 MappingConstraint6Onaom_GEQ0_3: aom#3->hours + aom#3->overTime
   - 10000 aom#3->DisjunctMappingConstraint5Onaom_symbolic0#1 >= -9999
 MappingConstraint6Onaom_GEQ0_4: aom#1->hours + aom#1->overTime
   - 10000 aom#1->DisjunctMappingConstraint5Onaom_symbolic0#3 >= -9999
 MappingConstraint6Onaom_GEQ0_5: aom#5->hours + aom#5->overTime
   - 10000 aom#5->DisjunctMappingConstraint5Onaom_symbolic0#7 >= -9999
 MappingConstraint6Onaom_GEQ0_6: aom#7->hours + aom#7->overTime
   - 10000 aom#7->DisjunctMappingConstraint5Onaom_symbolic0#5 >= -9999
 MappingConstraint6Onaom_GEQ0_7: aom#4->hours + aom#4->overTime
   - 10000 aom#4->DisjunctMappingConstraint5Onaom_symbolic0#6 >= -9999
 MappingConstraint6Onaom_GEQ0_8: aom#0->hours + aom#0->overTime
   - 10000 aom#0->DisjunctMappingConstraint5Onaom_symbolic0#2 >= -9999
 TypeConstraint0OnRequirement_0: aom#3->hours + aom#3->overTime
   + aom#6->hours + aom#6->overTime + aom#8->hours + aom#8->overTime >= 16
 TypeConstraint0OnRequirement_1: aom#7->hours + aom#7->overTime
   + aom#4->hours + aom#4->overTime >= 16
 TypeConstraint0OnRequirement_2: aom#2->hours + aom#2->overTime
   + aom#1->hours + aom#1->overTime >= 8
 TypeConstraint0OnRequirement_3: aom#0->hours + aom#0->overTime
   + aom#5->hours + aom#5->overTime >= 8
 PatternConstraint4OnpersonToOffer_0: aom#2->hours + aom#2->overTime
   + aom#7->hours + aom#7->overTime <= 57
 PatternConstraint4OnpersonToOffer_1: aom#3->hours + aom#3->overTime
   <= 6.6
 PatternConstraint4OnpersonToOffer_2: aom#6->hours + aom#6->overTime
   <= 6.6
 PatternConstraint4OnpersonToOffer_3: aom#5->hours + aom#5->overTime
   <= 23.75
 PatternConstraint4OnpersonToOffer_4: aom#0->hours + aom#0->overTime
   <= 23.75
 PatternConstraint4OnpersonToOffer_5: aom#8->hours + aom#8->overTime
   <= 6.6
 PatternConstraint4OnpersonToOffer_6: aom#1->hours + aom#1->overTime
   + aom#4->hours + aom#4->overTime <= 57
 MappingConstraint10Onaom_GEQ1_0: aom#6
   - 10000 aom#6->DisjunctMappingConstraint8Onaom_symbolic1#13 <= 0.9999
 MappingConstraint10Onaom_GEQ1_1: aom#8
   - 10000 aom#8->DisjunctMappingConstraint8Onaom_symbolic1#17 <= 0.9999
 MappingConstraint10Onaom_GEQ1_2: aom#2
   - 10000 aom#2->DisjunctMappingConstraint8Onaom_symbolic1#9 <= 0.9999
 MappingConstraint10Onaom_GEQ1_3: aom#3
   - 10000 aom#3->DisjunctMappingConstraint8Onaom_symbolic1#10 <= 0.9999
 MappingConstraint10Onaom_GEQ1_4: aom#1
   - 10000 aom#1->DisjunctMappingConstraint8Onaom_symbolic1#12 <= 0.9999
 MappingConstraint10Onaom_GEQ1_5: aom#5
   - 10000 aom#5->DisjunctMappingConstraint8Onaom_symbolic1#16 <= 0.9999
 MappingConstraint10Onaom_GEQ1_6: aom#7
   - 10000 aom#7->DisjunctMappingConstraint8Onaom_symbolic1#14 <= 0.9999
 MappingConstraint10Onaom_GEQ1_7: aom#4
   - 10000 aom#4->DisjunctMappingConstraint8Onaom_symbolic1#15 <= 0.9999
 MappingConstraint10Onaom_GEQ1_8: aom#0
   - 10000 aom#0->DisjunctMappingConstraint8Onaom_symbolic1#11 <= 0.9999
 MappingConstraint10Onaom_GEQ0_0: aom#6
   - 10000 aom#6->DisjunctMappingConstraint8Onaom_symbolic1#13 >= -9999
 MappingConstraint10Onaom_GEQ0_1: aom#8
   - 10000 aom#8->DisjunctMappingConstraint8Onaom_symbolic1#17 >= -9999
 MappingConstraint10Onaom_GEQ0_2: aom#2
   - 10000 aom#2->DisjunctMappingConstraint8Onaom_symbolic1#9 >= -9999
 MappingConstraint10Onaom_GEQ0_3: aom#3
   - 10000 aom#3->DisjunctMappingConstraint8Onaom_symbolic1#10 >= -9999
 MappingConstraint10Onaom_GEQ0_4: aom#1
   - 10000 aom#1->DisjunctMappingConstraint8Onaom_symbolic1#12 >= -9999
 MappingConstraint10Onaom_GEQ0_5: aom#5
   - 10000 aom#5->DisjunctMappingConstraint8Onaom_symbolic1#16 >= -9999
 MappingConstraint10Onaom_GEQ0_6: aom#7
   - 10000 aom#7->DisjunctMappingConstraint8Onaom_symbolic1#14 >= -9999
 MappingConstraint10Onaom_GEQ0_7: aom#4
   - 10000 aom#4->DisjunctMappingConstraint8Onaom_symbolic1#15 >= -9999
 MappingConstraint10Onaom_GEQ0_8: aom#0
   - 10000 aom#0->DisjunctMappingConstraint8Onaom_symbolic1#11 >= -9999
 MappingConstraint2OnprojectCost_0: projectCost#0->salary >= 0
 TypeConstraint1OnProject_0: - projectCost#0->salary + 100 aom#2->hours
   + 150 aom#2->overTime + 30 aom#3->hours + 65 aom#3->overTime
   + 65 aom#0->hours + 100 aom#0->overTime + 100 aom#1->hours
   + 150 aom#1->overTime + 30 aom#6->hours + 65 aom#6->overTime
   + 100 aom#7->hours + 150 aom#7->overTime + 100 aom#4->hours
   + 150 aom#4->overTime + 65 aom#5->hours + 100 aom#5->overTime
   + 30 aom#8->hours + 65 aom#8->overTime = 0
 MappingConstraint12Onaom_0: aom#6->overTime >= 0
 MappingConstraint12Onaom_1: aom#8->overTime >= 0
 MappingConstraint12Onaom_2: aom#2->overTime >= 0
 MappingConstraint12Onaom_3: aom#3->overTime >= 0
 MappingConstraint12Onaom_4: aom#1->overTime >= 0
 MappingConstraint12Onaom_5: aom#5->overTime >= 0
 MappingConstraint12Onaom_6: aom#7->overTime >= 0
 MappingConstraint12Onaom_7: aom#4->overTime >= 0
 MappingConstraint12Onaom_8: aom#0->overTime >= 0
 MappingConstraint6Onaom_GEQ1_0: aom#6->hours + aom#6->overTime
   - 10000 aom#6->DisjunctMappingConstraint5Onaom_symbolic0#4 <= 0.9999
 MappingConstraint6Onaom_GEQ1_1: aom#8->hours + aom#8->overTime
   - 10000 aom#8->DisjunctMappingConstraint5Onaom_symbolic0#8 <= 0.9999
 MappingConstraint6Onaom_GEQ1_2: aom#2->hours + aom#2->overTime
   - 10000 aom#2->DisjunctMappingConstraint5Onaom_symbolic0#0 <= 0.9999
 MappingConstraint6Onaom_GEQ1_3: aom#3->hours + aom#3->overTime
   - 10000 aom#3->DisjunctMappingConstraint5Onaom_symbolic0#1 <= 0.9999
 MappingConstraint6Onaom_GEQ1_4: aom#1->hours + aom#1->overTime
   - 10000 aom#1->DisjunctMappingConstraint5Onaom_symbolic0#3 <= 0.9999
 MappingConstraint6Onaom_GEQ1_5: aom#5->hours + aom#5->overTime
   - 10000 aom#5->DisjunctMappingConstraint5Onaom_symbolic0#7 <= 0.9999
 MappingConstraint6Onaom_GEQ1_6: aom#7->hours + aom#7->overTime
   - 10000 aom#7->DisjunctMappingConstraint5Onaom_symbolic0#5 <= 0.9999
 MappingConstraint6Onaom_GEQ1_7: aom#4->hours + aom#4->overTime
   - 10000 aom#4->DisjunctMappingConstraint5Onaom_symbolic0#6 <= 0.9999
 MappingConstraint6Onaom_GEQ1_8: aom#0->hours + aom#0->overTime
   - 10000 aom#0->DisjunctMappingConstraint5Onaom_symbolic0#2 <= 0.9999
 PatternConstraint17OnfinalTaskSequence_LEQ1_0: aom#2
   + 10000 finalTaskSequence->DisjunctPatternConstraint16OnfinalTaskSequence_symbolic0#0
   <= 10000
 PatternConstraint17OnfinalTaskSequence_LEQ1_1: aom#2 + aom#3 + aom#1
   + aom#6 + aom#8
   + 10000 finalTaskSequence->DisjunctPatternConstraint16OnfinalTaskSequence_symbolic0#1
   <= 10000
 PatternConstraint17OnfinalTaskSequence_LEQ1_2: aom#2 + aom#3 + aom#1
   + aom#6 + aom#8
   + 10000 finalTaskSequence->DisjunctPatternConstraint16OnfinalTaskSequence_symbolic0#2
   <= 10000
 PatternConstraint17OnfinalTaskSequence_LEQ0_0: aom#2
   + 10000 finalTaskSequence->DisjunctPatternConstraint16OnfinalTaskSequence_symbolic0#0
   >= 0.0001
 PatternConstraint17OnfinalTaskSequence_LEQ0_1: aom#2 + aom#3 + aom#1
   + aom#6 + aom#8
   + 10000 finalTaskSequence->DisjunctPatternConstraint16OnfinalTaskSequence_symbolic0#1
   >= 0.0001
 PatternConstraint17OnfinalTaskSequence_LEQ0_2: aom#2 + aom#3 + aom#1
   + aom#6 + aom#8
   + 10000 finalTaskSequence->DisjunctPatternConstraint16OnfinalTaskSequence_symbolic0#2
   >= 0.0001
 MappingConstraint11Onaom_0: aom#6->hours >= 0
 MappingConstraint11Onaom_1: aom#8->hours >= 0
 MappingConstraint11Onaom_2: aom#2->hours >= 0
 MappingConstraint11Onaom_3: aom#3->hours >= 0
 MappingConstraint11Onaom_4: aom#1->hours >= 0
 MappingConstraint11Onaom_5: aom#5->hours >= 0
 MappingConstraint11Onaom_6: aom#7->hours >= 0
 MappingConstraint11Onaom_7: aom#4->hours >= 0
 MappingConstraint11Onaom_8: aom#0->hours >= 0
 DisjunctPatternConstraint16OnfinalTaskSequence_0:
   - finalTaskSequence->DisjunctPatternConstraint16OnfinalTaskSequence_symbolic1#3
   + finalTaskSequence->DisjunctPatternConstraint16OnfinalTaskSequence_symbolic0#0
   >= 0
 DisjunctPatternConstraint16OnfinalTaskSequence_1:
   - finalTaskSequence->DisjunctPatternConstraint16OnfinalTaskSequence_symbolic1#4
   + finalTaskSequence->DisjunctPatternConstraint16OnfinalTaskSequence_symbolic0#1
   >= 0
 DisjunctPatternConstraint16OnfinalTaskSequence_2:
   - finalTaskSequence->DisjunctPatternConstraint16OnfinalTaskSequence_symbolic1#5
   + finalTaskSequence->DisjunctPatternConstraint16OnfinalTaskSequence_symbolic0#2
   >= 0
 PatternConstraint14OntaskSequence_LEQ1_0: aom#7 + aom#4
   + 10000 taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic0#0
   <= 10000
 PatternConstraint14OntaskSequence_LEQ1_1:
   10000 taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic0#1
   <= 10000
 PatternConstraint14OntaskSequence_LEQ1_2:
   10000 taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic0#2
   <= 10000
 PatternConstraint14OntaskSequence_LEQ1_3: aom#4
   + 10000 taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic0#3
   <= 10000
 PatternConstraint14OntaskSequence_LEQ1_4: aom#2 + aom#3 + aom#1 + aom#6
   + aom#8
   + 10000 taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic0#4
   <= 10000
 PatternConstraint14OntaskSequence_LEQ1_5: aom#3 + aom#1 + aom#6 + aom#8
   + 10000 taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic0#5
   <= 10000
 PatternConstraint14OntaskSequence_LEQ0_0: aom#7 + aom#4
   + 10000 taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic0#0
   >= 0.0001
 PatternConstraint14OntaskSequence_LEQ0_1:
   10000 taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic0#1
   >= 0.0001
 PatternConstraint14OntaskSequence_LEQ0_2:
   10000 taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic0#2
   >= 0.0001
 PatternConstraint14OntaskSequence_LEQ0_3: aom#4
   + 10000 taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic0#3
   >= 0.0001
 PatternConstraint14OntaskSequence_LEQ0_4: aom#2 + aom#3 + aom#1 + aom#6
   + aom#8
   + 10000 taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic0#4
   >= 0.0001
 PatternConstraint14OntaskSequence_LEQ0_5: aom#3 + aom#1 + aom#6 + aom#8
   + 10000 taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic0#5
   >= 0.0001
Bounds
 -1e+06 <= projectCost#0->salary <= 1e+06
 -10000 <= aom#2->hours <= 10000
 -10000 <= aom#2->overTime <= 10000
 -10000 <= aom#3->hours <= 10000
 -10000 <= aom#3->overTime <= 10000
 -10000 <= aom#0->hours <= 10000
 -10000 <= aom#0->overTime <= 10000
 -10000 <= aom#1->hours <= 10000
 -10000 <= aom#1->overTime <= 10000
 -10000 <= aom#6->hours <= 10000
 -10000 <= aom#6->overTime <= 10000
 -10000 <= aom#7->hours <= 10000
 -10000 <= aom#7->overTime <= 10000
 -10000 <= aom#4->hours <= 10000
 -10000 <= aom#4->overTime <= 10000
 -10000 <= aom#5->hours <= 10000
 -10000 <= aom#5->overTime <= 10000
 -10000 <= aom#8->hours <= 10000
 -10000 <= aom#8->overTime <= 10000
Binaries
 projectCost#0 aom#2 aom#3 aom#0 aom#1 aom#6 aom#7 aom#4 aom#5 aom#8
 aom#6->DisjunctMappingConstraint5Onaom_symbolic1#13
 aom#8->DisjunctMappingConstraint5Onaom_symbolic1#17
 aom#2->DisjunctMappingConstraint5Onaom_symbolic1#9
 aom#3->DisjunctMappingConstraint5Onaom_symbolic1#10
 aom#1->DisjunctMappingConstraint5Onaom_symbolic1#12
 aom#5->DisjunctMappingConstraint5Onaom_symbolic1#16
 aom#7->DisjunctMappingConstraint5Onaom_symbolic1#14
 aom#4->DisjunctMappingConstraint5Onaom_symbolic1#15
 aom#0->DisjunctMappingConstraint5Onaom_symbolic1#11
 taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic0#2
 taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic0#1
 taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic0#4
 taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic1#10
 taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic1#11
 taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic1#7
 taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic0#5
 taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic1#9
 taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic1#6
 taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic1#8
 taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic0#3
 taskSequence->DisjunctPatternConstraint13OntaskSequence_symbolic0#0
 aom#6->DisjunctMappingConstraint8Onaom_symbolic1#13
 aom#8->DisjunctMappingConstraint8Onaom_symbolic1#17
 aom#2->DisjunctMappingConstraint8Onaom_symbolic1#9
 aom#2->DisjunctMappingConstraint8Onaom_symbolic0#0
 aom#7->DisjunctMappingConstraint8Onaom_symbolic0#5
 aom#5->DisjunctMappingConstraint8Onaom_symbolic1#16
 aom#7->DisjunctMappingConstraint8Onaom_symbolic1#14
 aom#3->DisjunctMappingConstraint8Onaom_symbolic1#10
 aom#4->DisjunctMappingConstraint8Onaom_symbolic0#6
 aom#4->DisjunctMappingConstraint8Onaom_symbolic1#15
 aom#0->DisjunctMappingConstraint8Onaom_symbolic0#2
 aom#5->DisjunctMappingConstraint8Onaom_symbolic0#7
 aom#0->DisjunctMappingConstraint8Onaom_symbolic1#11
 aom#1->DisjunctMappingConstraint8Onaom_symbolic1#12
 aom#8->DisjunctMappingConstraint8Onaom_symbolic0#8
 aom#1->DisjunctMappingConstraint8Onaom_symbolic0#3
 aom#3->DisjunctMappingConstraint8Onaom_symbolic0#1
 aom#6->DisjunctMappingConstraint8Onaom_symbolic0#4
 finalTaskSequence->DisjunctPatternConstraint16OnfinalTaskSequence_symbolic1#3
 finalTaskSequence->DisjunctPatternConstraint16OnfinalTaskSequence_symbolic1#4
 finalTaskSequence->DisjunctPatternConstraint16OnfinalTaskSequence_symbolic1#5
 aom#2->DisjunctMappingConstraint5Onaom_symbolic0#0
 aom#0->DisjunctMappingConstraint5Onaom_symbolic0#2
 aom#4->DisjunctMappingConstraint5Onaom_symbolic0#6
 aom#8->DisjunctMappingConstraint5Onaom_symbolic0#8
 aom#1->DisjunctMappingConstraint5Onaom_symbolic0#3
 aom#5->DisjunctMappingConstraint5Onaom_symbolic0#7
 aom#7->DisjunctMappingConstraint5Onaom_symbolic0#5
 aom#6->DisjunctMappingConstraint5Onaom_symbolic0#4
 aom#3->DisjunctMappingConstraint5Onaom_symbolic0#1
 finalTaskSequence->DisjunctPatternConstraint16OnfinalTaskSequence_symbolic0#0
 finalTaskSequence->DisjunctPatternConstraint16OnfinalTaskSequence_symbolic0#1
 finalTaskSequence->DisjunctPatternConstraint16OnfinalTaskSequence_symbolic0#2
Generals
 aom#2->hours aom#2->overTime aom#3->hours aom#3->overTime aom#0->hours
 aom#0->overTime aom#1->hours aom#1->overTime aom#6->hours aom#6->overTime
 aom#7->hours aom#7->overTime aom#4->hours aom#4->overTime aom#5->hours
 aom#5->overTime aom#8->hours aom#8->overTime
End
