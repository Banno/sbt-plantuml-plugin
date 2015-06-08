/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2014, Arnaud Roques
 *
 * Project Info:  http://plantuml.sourceforge.net
 * 
 * This file is part of PlantUML.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Original Author:  Arnaud Roques
 */
package net.sourceforge.plantuml.statediagram.command;

import net.sourceforge.plantuml.Direction;
import net.sourceforge.plantuml.classdiagram.command.CommandLinkClass;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.cucadiagram.Code;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.cucadiagram.LeafType;
import net.sourceforge.plantuml.cucadiagram.Link;
import net.sourceforge.plantuml.cucadiagram.LinkDecor;
import net.sourceforge.plantuml.cucadiagram.LinkType;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.statediagram.StateDiagram;
import net.sourceforge.plantuml.StringUtils;

public class CommandLinkState extends SingleLineCommand2<StateDiagram> {

	public CommandLinkState() {
		super(getRegex());
	}

	static RegexConcat getRegex() {
		return new RegexConcat(new RegexLeaf("^"), //
				getStatePattern("ENT1"), //
				new RegexLeaf("[%s]*"), //
				new RegexConcat(
						//
						new RegexLeaf("ARROW_CROSS_START", "(x)?"), //
						new RegexLeaf("ARROW_BODY1", "(-+)"), //
						new RegexLeaf("ARROW_STYLE1",
								"(?:\\[((?:#\\w+|dotted|dashed|bold|hidden)(?:,#\\w+|,dotted|,dashed|,bold|,hidden)*)\\])?"), //
						new RegexLeaf("ARROW_DIRECTION", "(left|right|up|down|le?|ri?|up?|do?)?"), //
						new RegexLeaf("ARROW_STYLE2",
								"(?:\\[((?:#\\w+|dotted|dashed|bold|hidden)(?:,#\\w+|,dotted|,dashed|,bold|,hidden)*)\\])?"), //
						new RegexLeaf("ARROW_BODY2", "(-*)"), //
						new RegexLeaf("\\>"), //
						new RegexLeaf("ARROW_CIRCLE_END", "(o[%s]+)?")), //
				new RegexLeaf("[%s]*"), //
				getStatePattern("ENT2"), //
				new RegexLeaf("[%s]*"), //
				new RegexLeaf("LABEL", "(?::[%s]*([^%g]+))?"), //
				new RegexLeaf("$"));
	}

	private static RegexLeaf getStatePattern(String name) {
		return new RegexLeaf(
				name,
				"([\\p{L}0-9_.]+|[\\p{L}0-9_.]+\\[H\\]|\\[\\*\\]|\\[H\\]|(?:==+)(?:[\\p{L}0-9_.]+)(?:==+))[%s]*(\\<\\<.*\\>\\>)?[%s]*(#\\w+)?");
	}

	@Override
	protected CommandExecutionResult executeArg(StateDiagram diagram, RegexResult arg) {
		final String ent1 = arg.get("ENT1", 0);
		final String ent2 = arg.get("ENT2", 0);

		final IEntity cl1 = getEntityStart(diagram, ent1);
		if (cl1 == null) {
			return CommandExecutionResult.error("The state " + ent1
					+ " has been created in a concurrent state : it cannot be used here.");
		}
		final IEntity cl2 = getEntityEnd(diagram, ent2);
		if (cl2 == null) {
			return CommandExecutionResult.error("The state " + ent2
					+ " has been created in a concurrent state : it cannot be used here.");
		}

		if (arg.get("ENT1", 1) != null) {
			cl1.setStereotype(new Stereotype(arg.get("ENT1", 1)));
		}
		if (arg.get("ENT1", 2) != null) {
			cl1.setSpecificBackcolor(diagram.getSkinParam().getIHtmlColorSet().getColorIfValid(arg.get("ENT1", 2)));
		}
		if (arg.get("ENT2", 1) != null) {
			cl2.setStereotype(new Stereotype(arg.get("ENT2", 1)));
		}
		if (arg.get("ENT2", 2) != null) {
			cl2.setSpecificBackcolor(diagram.getSkinParam().getIHtmlColorSet().getColorIfValid(arg.get("ENT2", 2)));
		}

		String queue = arg.get("ARROW_BODY1", 0) + arg.get("ARROW_BODY2", 0);
		final Direction dir = getDirection(arg);

		if (dir == Direction.LEFT || dir == Direction.RIGHT) {
			queue = "-";
		}

		final int lenght = queue.length();

		final boolean crossStart = arg.get("ARROW_CROSS_START", 0) != null;
		final boolean circleEnd = arg.get("ARROW_CIRCLE_END", 0) != null;
		final LinkType linkType = new LinkType(circleEnd ? LinkDecor.ARROW_AND_CIRCLE : LinkDecor.ARROW,
				crossStart ? LinkDecor.CIRCLE_CROSS : LinkDecor.NONE);

		Link link = new Link(cl1, cl2, linkType, Display.getWithNewlines(arg.get("LABEL", 0)), lenght);
		if (dir == Direction.LEFT || dir == Direction.UP) {
			link = link.getInv();
		}
		CommandLinkClass.applyStyle(arg.getLazzy("ARROW_STYLE", 0), link);
		diagram.addLink(link);

		return CommandExecutionResult.ok();
	}

	// public static void applyStyle(String arrowStyle, Link link) {
	// if (arrowStyle == null) {
	// return;
	// }
	// final StringTokenizer st = new StringTokenizer(arrowStyle, ",");
	// while (st.hasMoreTokens()) {
	// final String s = st.nextToken();
	// if (s.equalsIgnoreCase("dashed")) {
	// link.goDashed();
	// } else if (s.equalsIgnoreCase("bold")) {
	// link.goBold();
	// } else if (s.equalsIgnoreCase("dotted")) {
	// link.goDotted();
	// } else if (s.equalsIgnoreCase("hidden")) {
	// link.goHidden();
	// } else {
	// link.setSpecificColor(s);
	// }
	// }
	// }

	private Direction getDirection(RegexResult arg) {
		final String arrowDirection = arg.get("ARROW_DIRECTION", 0);
		if (arrowDirection != null) {
			return StringUtils.getQueueDirection(arrowDirection);
		}
		return null;
	}

	private IEntity getEntityStart(StateDiagram system, String code) {
		if (code.startsWith("[*]")) {
			return system.getStart();
		}
		if (code.equalsIgnoreCase("[H]")) {
			return system.getHistorical();
		}
		if (code.endsWith("[H]")) {
			return system.getHistorical(Code.of(code.substring(0, code.length() - 3)));
		}
		if (code.startsWith("=") && code.endsWith("=")) {
			code = removeEquals(code);
			return system.getOrCreateLeaf(Code.of(code), LeafType.SYNCHRO_BAR, null);
		}
		if (system.checkConcurrentStateOk(Code.of(code)) == false) {
			return null;
		}
		return system.getOrCreateLeaf(Code.of(code), null, null);
	}

	private String removeEquals(String code) {
		while (code.startsWith("=")) {
			code = code.substring(1);
		}
		while (code.endsWith("=")) {
			code = code.substring(0, code.length() - 1);
		}
		return code;
	}

	private IEntity getEntityEnd(StateDiagram system, String code) {
		if (code.startsWith("[*]")) {
			return system.getEnd();
		}
		if (code.endsWith("[H]")) {
			return system.getHistorical(Code.of(code.substring(0, code.length() - 3)));
		}
		if (code.startsWith("=") && code.endsWith("=")) {
			code = removeEquals(code);
			return system.getOrCreateLeaf(Code.of(code), LeafType.SYNCHRO_BAR, null);
		}
		if (system.checkConcurrentStateOk(Code.of(code)) == false) {
			return null;
		}
		return system.getOrCreateLeaf(Code.of(code), null, null);
	}

}
