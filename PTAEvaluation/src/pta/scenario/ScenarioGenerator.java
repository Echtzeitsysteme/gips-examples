package pta.scenario;

import java.util.Random;

import pta.generator.PTAModelGenerator;

public class ScenarioGenerator extends PTAModelGenerator{
	final public static String[] firstNames = { //
			"Michael", //
			"Christopher", //
			"Jessica", //
			"Matthew", //
			"Ashley", //
			"Jennifer", //
			"Joshua", //
			"Amanda", //
			"Daniel", //
			"David", //
			"James", //
			"Robert", //
			"John", //
			"Joseph", //
			"Andrew", //
			"Ryan", //
			"Brandon", //
			"Jason", //
			"Justin", //
			"Sarah", //
			"William", //
			"Jonathan", //
			"Stephanie", //
			"Brian", //
			"Nicole", //
			"Nicholas", //
			"Anthony", //
			"Heather", //
			"Eric", //
			"Elizabeth", //
			"Adam", //
			"Megan", //
			"Melissa", //
			"Kevin", //
			"Steven", //
			"Thomas", //
			"Timothy", //
			"Christina", //
			"Kyle", //
			"Rachel", //
			"Laura", //
			"Lauren", //
			"Amber", //
			"Brittany", //
			"Danielle", //
			"Richard", //
			"Kimberly", //
			"Jeffrey", //
			"Amy", //
			"Crystal", //
			"Michelle", //
			"Tiffany", //
			"Jeremy", //
			"Benjamin", //
			"Mark", //
			"Emily", //
			"Aaron", //
			"Charles", //
			"Rebecca", //
			"Jacob", //
			"Stephen", //
			"Patrick", //
			"Sean", //
			"Erin", //
			"Zachary", //
			"Jamie", //
			"Kelly", //
			"Samantha", //
			"Nathan", //
			"Sara", //
			"Dustin", //
			"Paul", //
			"Angela", //
			"Tyler", //
			"Scott", //
			"Katherine", //
			"Andrea", //
			"Gregory", //
			"Erica", //
			"Mary", //
			"Travis", //
			"Lisa", //
			"Kenneth", //
			"Bryan", //
			"Lindsey", //
			"Kristen", //
			"Jose", //
			"Alexander", //
			"Jesse", //
			"Katie", //
			"Lindsay", //
			"Shannon", //
			"Vanessa", //
			"Courtney", //
			"Christine", //
			"Alicia", //
			"Cody", //
			"Allison", //
			"Bradley", //
			"Samuel" //
	};

	final public static String[] lastNames = { "Chung", //
			"Chen", //
			"Melton", //
			"Hill", //
			"Puckett", //
			"Song", //
			"Hamilton", //
			"Bender", //
			"Wagner", //
			"McLaughlin", //
			"McNamara", //
			"Raynor", //
			"Moon", //
			"Woodard", //
			"Desai", //
			"Wallace", //
			"Lawrence", //
			"Griffin", //
			"Dougherty", //
			"Powers", //
			"May", //
			"Steele", //
			"Teague", //
			"Vick", //
			"Gallagher", //
			"Solomon", //
			"Walsh", //
			"Monroe", //
			"Connolly", //
			"Hawkins", //
			"Middleton", //
			"Goldstein", //
			"Watts", //
			"Johnston", //
			"Weeks", //
			"Wilkerson", //
			"Barton", //
			"Walton", //
			"Hall", //
			"Ross", //
			"Woods", //
			"Mangum", //
			"Joseph", //
			"Rosenthal", //
			"Bowden", //
			"Underwood", //
			"Jones", //
			"Baker", //
			"Merritt", //
			"Cross", //
			"Cooper", //
			"Holmes", //
			"Sharpe", //
			"Morgan", //
			"Hoyle", //
			"Allen", //
			"Rich", //
			"Grant", //
			"Proctor", //
			"Diaz", //
			"Graham", //
			"Watkins", //
			"Hinton", //
			"Marsh", //
			"Hewitt", //
			"Branch", //
			"O'Brien", //
			"Case", //
			"Christensen", //
			"Parks", //
			"Hardin", //
			"Lucas", //
			"Eason", //
			"Davidson", //
			"Whitehead", //
			"Rose", //
			"Sparks", //
			"Moore", //
			"Pearson", //
			"Rodgers", //
			"Graves", //
			"Scarborough", //
			"Sutton", //
			"Sinclair", //
			"Bowman", //
			"Olsen", //
			"Love", //
			"McLean", //
			"Christian", //
			"Lamb", //
			"James", //
			"Chandler", //
			"Stout", //
			"Cowan", //
			"Golden", //
			"Bowling", //
			"Beasley", //
			"Clapp", //
			"Abrams", //
			"Tilley" //
	};

	final public static double WORKHOURS_PER_DAY = 8;
	final public static int WORKDAYS_PER_WEEK = 5;

	final public static double HOURS_APPRENTICE = 20;
	final public static double HOURS_JOURNEYMAN = 38;
	final public static double HOURS_MASTER = 38;

	final public static double FLEX_APPRENTICE = 1.1;
	final public static double FLEX_JOURNEYMAN = 1.25;
	final public static double FLEX_MASTER = 1.5;

	final public static double SALARY_APPRENTICE = 30;
	final public static double SALARY_JOURNEYMAN = 65;
	final public static double SALARY_MASTER = 100;

	final public static double BONUS_APPRENTICE = 65;
	final public static double BONUS_JOURNEYMAN = 100;
	final public static double BONUS_MASTER = 150;

	final public static int SKILL_APPRENTICE = 1;
	final public static int SKILL_JOURNEYMAN = 2;
	final public static int SKILL_MASTER = 3;
	
	final protected Random rnd;
	
	public ScenarioGenerator(final int seed) {
		rnd = new Random(seed);
	}
}
