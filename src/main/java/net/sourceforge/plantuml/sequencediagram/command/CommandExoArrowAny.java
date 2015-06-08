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
package net.sourceforge.plantuml.sequencediagram.command;

import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.sequencediagram.MessageExo;
import net.sourceforge.plantuml.sequencediagram.MessageExoType;
import net.sourceforge.plantuml.sequencediagram.Participant;
import net.sourceforge.plantuml.sequencediagram.SequenceDiagram;
import net.sourceforge.plantuml.skin.ArrowConfiguration;
import net.sourceforge.plantuml.skin.ArrowDecoration;
import net.sourceforge.plantuml.skin.ArrowHead;
import net.sourceforge.plantuml.skin.ArrowPart;
import net.sourceforge.plantuml.StringUtils;

abstract class CommandExoArrowAny extends SingleLineCommand2<SequenceDiagram> {

	public CommandExoArrowAny(RegexConcat pattern) {
		super(pattern);
	}

	@Override
	final protected CommandExecutionResult executeArg(SequenceDiagram sequenceDiagram, RegexResult arg2) {
		final String body = arg2.getLazzy("ARROW_BODYA", 0) + arg2.getLazzy("ARROW_BODYB", 0);
		final String dressing = arg2.getLazzy("ARROW_DRESSING", 0);
		final Participant p = sequenceDiagram.getOrCreateParticipant(StringUtils
				.eventuallyRemoveStartingAndEndingDoubleQuote(arg2.get("PARTICIPANT", 0)));

		final boolean sync = dressing.length() == 2;
		final boolean dotted = body.contains("--");

		final Display labels;
		if (arg2.get("LABEL", 0) == null) {
			labels = Display.create("");
		} else {
			labels = Display.getWithNewlines(arg2.get("LABEL", 0));
		}

		final boolean bothDirection = arg2.get("ARROW_BOTHDRESSING", 0) != null;

		ArrowConfiguration config = bothDirection ? ArrowConfiguration.withDirectionBoth() : ArrowConfiguration
				.withDirectionNormal();
		if (dotted) {
			config = config.withDotted();
		}
		if (sync) {
			config = config.withHead(ArrowHead.ASYNC);
		}
		config = config.withPart(getArrowPart(dressing));
		config = CommandArrow.applyStyle(arg2.getLazzy("ARROW_STYLE", 0), config);
		final MessageExoType messageExoType = getMessageExoType(arg2);

		if (messageExoType == MessageExoType.TO_RIGHT || messageExoType == MessageExoType.TO_LEFT) {
			if (containsSymbolExterior(arg2, "o")) {
				config = config.withDecoration2(ArrowDecoration.CIRCLE);
			}
			if (containsSymbol(arg2, "o")) {
				config = config.withDecoration1(ArrowDecoration.CIRCLE);
			}
		} else {
			if (containsSymbolExterior(arg2, "o")) {
				config = config.withDecoration1(ArrowDecoration.CIRCLE);
			}
			if (containsSymbol(arg2, "o")) {
				config = config.withDecoration2(ArrowDecoration.CIRCLE);
			}
		}

		if (containsSymbolExterior(arg2, "x") || containsSymbol(arg2, "x")) {
			config = config.withHead2(ArrowHead.CROSSX);
		}
//		if (messageExoType.getDirection() == 1) {
//			if (containsSymbolExterior(arg2, "x") || containsSymbol(arg2, "x")) {
//				config = config.withHead2(ArrowHead.CROSSX);
//			}
//		} else {
//			if (containsSymbolExterior(arg2, "x") || containsSymbol(arg2, "x")) {
//				config = config.withHead2(ArrowHead.CROSSX);
//			}
//		}

		final String error = sequenceDiagram.addMessage(new MessageExo(p, messageExoType, labels, config,
				sequenceDiagram.getNextMessageNumber(), isShortArrow(arg2)));
		if (error != null) {
			return CommandExecutionResult.error(error);
		}
		return CommandExecutionResult.ok();
	}

	private ArrowPart getArrowPart(String dressing) {
		if (dressing.contains("/")) {
			return ArrowPart.BOTTOM_PART;
		}
		if (dressing.contains("\\")) {
			return ArrowPart.TOP_PART;
		}
		return ArrowPart.FULL;
	}

	abstract MessageExoType getMessageExoType(RegexResult arg2);

	private boolean isShortArrow(RegexResult arg2) {
		final String s = arg2.get("SHORT", 0);
		if (s != null && s.contains("?")) {
			return true;
		}
		return false;
	}

	private boolean containsSymbolExterior(RegexResult arg2, String symbol) {
		final String s = arg2.get("SHORT", 0);
		if (s != null && s.contains(symbol)) {
			return true;
		}
		return false;
	}

	private boolean containsSymbol(RegexResult arg2, String symbol) {
		final String s = arg2.get("ARROW_SUPPCIRCLE", 0);
		if (s != null && s.contains(symbol)) {
			return true;
		}
		return false;
	}

}
