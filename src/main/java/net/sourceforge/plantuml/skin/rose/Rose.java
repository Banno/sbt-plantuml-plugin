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
package net.sourceforge.plantuml.skin.rose;

import net.sourceforge.plantuml.AlignParam;
import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.LineParam;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.graphic.SymbolContext;
import net.sourceforge.plantuml.skin.ArrowConfiguration;
import net.sourceforge.plantuml.skin.ArrowDirection;
import net.sourceforge.plantuml.skin.Component;
import net.sourceforge.plantuml.skin.ComponentType;
import net.sourceforge.plantuml.skin.Skin;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UStroke;

public class Rose implements Skin {

	final private double paddingX = 5;
	final private double paddingY = 5;

	public HtmlColor getFontColor(ISkinParam skin, FontParam fontParam) {
		return skin.getFontHtmlColor(fontParam, null);
	}

	public HtmlColor getHtmlColor(ISkinParam param, ColorParam color) {
		return getHtmlColor(param, color, null);
	}

	public HtmlColor getHtmlColor(ISkinParam param, ColorParam color, Stereotype stereotype) {
		HtmlColor result = param.getHtmlColor(color, stereotype, false);
		if (result == null) {
			result = color.getDefaultValue();
			if (result == null) {
				throw new IllegalArgumentException();
			}
		}
		return result;
	}

	private FontConfiguration getUFont2(ISkinParam param, FontParam fontParam) {
		final UFont font = param.getFont(fontParam, null, false);
		final HtmlColor fontColor = getFontColor(param, fontParam);
		return new FontConfiguration(font, fontColor, param.getHyperlinkColor(), param.useUnderlineForHyperlink());
	}

	public Component createComponent(ComponentType type, ArrowConfiguration config, ISkinParam param,
			Display stringsToDisplay) {
		final UFont fontGrouping = param.getFont(FontParam.SEQUENCE_GROUP, null, false);

		final UFont newFontForStereotype = param.getFont(FontParam.SEQUENCE_STEREOTYPE, null, false);

		if (type.isArrow()) {
			// if (param.maxMessageSize() > 0) {
			// final FontConfiguration fc = new FontConfiguration(fontArrow, HtmlColorUtils.BLACK);
			// stringsToDisplay = DisplayUtils.breakLines(stringsToDisplay, fc, param, param.maxMessageSize());
			// }
			final HtmlColor sequenceArrow = config.getColor() == null ? getHtmlColor(param, ColorParam.sequenceArrow)
					: config.getColor();
			if (config.getArrowDirection() == ArrowDirection.SELF) {
				return new ComponentRoseSelfArrow(sequenceArrow, getUFont2(param, FontParam.SEQUENCE_ARROW),
						stringsToDisplay, config, param, param.maxMessageSize(), param.strictUmlStyle() == false);
			}
			final HorizontalAlignment messageHorizontalAlignment = param
					.getHorizontalAlignment(AlignParam.SEQUENCE_MESSAGE_ALIGN);
			final HorizontalAlignment textHorizontalAlignment = param
					.getHorizontalAlignment(AlignParam.SEQUENCE_MESSAGETEXT_ALIGN);
			return new ComponentRoseArrow(sequenceArrow, getUFont2(param, FontParam.SEQUENCE_ARROW), stringsToDisplay,
					config, messageHorizontalAlignment, param, textHorizontalAlignment, param.maxMessageSize(),
					param.strictUmlStyle() == false);
		}
		if (type == ComponentType.PARTICIPANT_HEAD) {
			return new ComponentRoseParticipant(getSymbolContext(param, ColorParam.participantBorder), getUFont2(param,
					FontParam.PARTICIPANT), stringsToDisplay, param, param.getRoundCorner(), newFontForStereotype,
					getFontColor(param, FontParam.SEQUENCE_STEREOTYPE));
		}
		if (type == ComponentType.PARTICIPANT_TAIL) {
			return new ComponentRoseParticipant(getSymbolContext(param, ColorParam.participantBorder), getUFont2(param,
					FontParam.PARTICIPANT), stringsToDisplay, param, param.getRoundCorner(), newFontForStereotype,
					getFontColor(param, FontParam.SEQUENCE_STEREOTYPE));
		}
		if (type == ComponentType.PARTICIPANT_LINE) {
			final HtmlColor borderColor = getHtmlColor(param, ColorParam.sequenceLifeLineBorder);
			return new ComponentRoseLine(borderColor, false, getStroke(param, LineParam.sequenceLifeLineBorder, 1));
		}
		if (type == ComponentType.CONTINUE_LINE) {
			final HtmlColor borderColor = getHtmlColor(param, ColorParam.sequenceLifeLineBorder);
			return new ComponentRoseLine(borderColor, true, getStroke(param, LineParam.sequenceLifeLineBorder, 1.5));
		}
		if (type == ComponentType.ACTOR_HEAD) {
			return new ComponentRoseActor(getSymbolContext(param, ColorParam.actorBorder), getUFont2(param,
					FontParam.ACTOR), stringsToDisplay, true, param, newFontForStereotype, getFontColor(param,
					FontParam.SEQUENCE_STEREOTYPE));
		}
		if (type == ComponentType.ACTOR_TAIL) {
			return new ComponentRoseActor(getSymbolContext(param, ColorParam.actorBorder), getUFont2(param,
					FontParam.ACTOR), stringsToDisplay, false, param, newFontForStereotype, getFontColor(param,
					FontParam.SEQUENCE_STEREOTYPE));
		}
		if (type == ComponentType.BOUNDARY_HEAD) {
			return new ComponentRoseBoundary(getSymbolContext(param, ColorParam.actorBorder), getUFont2(param,
					FontParam.ACTOR), stringsToDisplay, true, param, newFontForStereotype, getFontColor(param,
					FontParam.SEQUENCE_STEREOTYPE));
		}
		if (type == ComponentType.BOUNDARY_TAIL) {
			return new ComponentRoseBoundary(getSymbolContext(param, ColorParam.actorBorder), getUFont2(param,
					FontParam.ACTOR), stringsToDisplay, false, param, newFontForStereotype, getFontColor(param,
					FontParam.SEQUENCE_STEREOTYPE));
		}
		if (type == ComponentType.CONTROL_HEAD) {
			return new ComponentRoseControl(getSymbolContext(param, ColorParam.actorBorder), getUFont2(param,
					FontParam.ACTOR), stringsToDisplay, true, param, newFontForStereotype, getFontColor(param,
					FontParam.SEQUENCE_STEREOTYPE));
		}
		if (type == ComponentType.CONTROL_TAIL) {
			return new ComponentRoseControl(getSymbolContext(param, ColorParam.actorBorder), getUFont2(param,
					FontParam.ACTOR), stringsToDisplay, false, param, newFontForStereotype, getFontColor(param,
					FontParam.SEQUENCE_STEREOTYPE));
		}
		if (type == ComponentType.ENTITY_HEAD) {
			return new ComponentRoseEntity(getSymbolContext(param, ColorParam.actorBorder), getUFont2(param,
					FontParam.ACTOR), stringsToDisplay, true, param, newFontForStereotype, getFontColor(param,
					FontParam.SEQUENCE_STEREOTYPE));
		}
		if (type == ComponentType.ENTITY_TAIL) {
			return new ComponentRoseEntity(getSymbolContext(param, ColorParam.actorBorder), getUFont2(param,
					FontParam.ACTOR), stringsToDisplay, false, param, newFontForStereotype, getFontColor(param,
					FontParam.SEQUENCE_STEREOTYPE));
		}
		if (type == ComponentType.DATABASE_HEAD) {
			return new ComponentRoseDatabase(getSymbolContext(param, ColorParam.actorBorder), getUFont2(param,
					FontParam.ACTOR), stringsToDisplay, true, param, newFontForStereotype, getFontColor(param,
					FontParam.SEQUENCE_STEREOTYPE));
		}
		if (type == ComponentType.DATABASE_TAIL) {
			return new ComponentRoseDatabase(getSymbolContext(param, ColorParam.actorBorder), getUFont2(param,
					FontParam.ACTOR), stringsToDisplay, false, param, newFontForStereotype, getFontColor(param,
					FontParam.SEQUENCE_STEREOTYPE));
		}
		if (type == ComponentType.NOTE) {
			return new ComponentRoseNote(getSymbolContext(param, ColorParam.noteBorder), getUFont2(param,
					FontParam.NOTE), stringsToDisplay, paddingX, paddingY, param);
		}
		if (type == ComponentType.NOTE_HEXAGONAL) {
			return new ComponentRoseNoteHexagonal(getSymbolContext(param, ColorParam.noteBorder), getUFont2(param,
					FontParam.NOTE), stringsToDisplay, param);
		}
		if (type == ComponentType.NOTE_BOX) {
			return new ComponentRoseNoteBox(getSymbolContext(param, ColorParam.noteBorder), getUFont2(param,
					FontParam.NOTE), stringsToDisplay, param);
		}
		if (type == ComponentType.GROUPING_HEADER) {
			return new ComponentRoseGroupingHeader(param.getBackgroundColor(), getSymbolContext(param,
					ColorParam.sequenceGroupBorder), getUFont2(param, FontParam.SEQUENCE_GROUP_HEADER), fontGrouping,
					stringsToDisplay, param);
		}
		if (type == ComponentType.GROUPING_ELSE) {
			return new ComponentRoseGroupingElse(getHtmlColor(param, ColorParam.sequenceGroupBorder), getUFont2(param,
					FontParam.SEQUENCE_GROUP), stringsToDisplay.get(0), param, param.getBackgroundColor());
		}
		if (type == ComponentType.GROUPING_SPACE) {
			return new ComponentRoseGroupingSpace(7);
		}
		if (type == ComponentType.ALIVE_BOX_CLOSE_CLOSE) {
			return new ComponentRoseActiveLine(getSymbolContext(param, ColorParam.sequenceLifeLineBorder), true, true);
		}
		if (type == ComponentType.ALIVE_BOX_CLOSE_OPEN) {
			return new ComponentRoseActiveLine(getSymbolContext(param, ColorParam.sequenceLifeLineBorder), true, false);
		}
		if (type == ComponentType.ALIVE_BOX_OPEN_CLOSE) {
			return new ComponentRoseActiveLine(getSymbolContext(param, ColorParam.sequenceLifeLineBorder), false, true);
		}
		if (type == ComponentType.ALIVE_BOX_OPEN_OPEN) {
			return new ComponentRoseActiveLine(getSymbolContext(param, ColorParam.sequenceLifeLineBorder), false, false);
		}
		if (type == ComponentType.DELAY_LINE) {
			return new ComponentRoseDelayLine(getHtmlColor(param, ColorParam.sequenceLifeLineBorder));
		}
		if (type == ComponentType.DELAY_TEXT) {
			return new ComponentRoseDelayText(getUFont2(param, FontParam.SEQUENCE_DELAY), stringsToDisplay, param);
		}
		if (type == ComponentType.DESTROY) {
			return new ComponentRoseDestroy(getHtmlColor(param, ColorParam.sequenceLifeLineBorder));
		}
		if (type == ComponentType.NEWPAGE) {
			return new ComponentRoseNewpage(getFontColor(param, FontParam.SEQUENCE_GROUP));
		}
		if (type == ComponentType.DIVIDER) {
			return new ComponentRoseDivider(getUFont2(param, FontParam.SEQUENCE_DIVIDER), getHtmlColor(param,
					ColorParam.sequenceDividerBackground), stringsToDisplay, param, deltaShadow(param) > 0, getStroke(
					param, LineParam.sequenceDividerBorder, 2));
		}
		if (type == ComponentType.REFERENCE) {
			return new ComponentRoseReference(getUFont2(param, FontParam.SEQUENCE_REFERENCE), getSymbolContext(param,
					ColorParam.sequenceReferenceBorder), getUFont2(param, FontParam.SEQUENCE_GROUP_HEADER),
					stringsToDisplay, param.getHorizontalAlignment(AlignParam.SEQUENCE_REFERENCE_ALIGN), param,
					getHtmlColor(param, ColorParam.sequenceReferenceBackground));
		}
		if (type == ComponentType.TITLE) {
			return new ComponentRoseTitle(getUFont2(param, FontParam.SEQUENCE_TITLE), stringsToDisplay, param);
		}
		if (type == ComponentType.SIGNATURE) {
			return new ComponentRoseTitle(fontGrouping.toFont2(HtmlColorUtils.BLACK, param.useUnderlineForHyperlink(),
					param.getHyperlinkColor()), Display.create("This skin was created ", "in April 2009."), param);
		}
		if (type == ComponentType.ENGLOBER) {
			return new ComponentRoseEnglober(getSymbolContext(param, ColorParam.sequenceBoxBorder), stringsToDisplay,
					getUFont2(param, FontParam.SEQUENCE_BOX), param);
		}

		return null;
	}

	private double deltaShadow(ISkinParam param) {
		return param.shadowing() ? 4.0 : 0;
	}

	private SymbolContext getSymbolContext(ISkinParam param, ColorParam color) {
		if (color == ColorParam.participantBorder) {
			return new SymbolContext(getHtmlColor(param, ColorParam.participantBackground), getHtmlColor(param,
					ColorParam.participantBorder)).withStroke(
					getStroke(param, LineParam.sequenceParticipantBorder, 1.5)).withDeltaShadow(deltaShadow(param));
		}
		if (color == ColorParam.actorBorder) {
			return new SymbolContext(getHtmlColor(param, ColorParam.actorBackground), getHtmlColor(param,
					ColorParam.actorBorder)).withStroke(getStroke(param, LineParam.sequenceActorBorder, 2))
					.withDeltaShadow(deltaShadow(param));
		}
		if (color == ColorParam.sequenceLifeLineBorder) {
			return new SymbolContext(getHtmlColor(param, ColorParam.sequenceLifeLineBackground), getHtmlColor(param,
					ColorParam.sequenceLifeLineBorder)).withDeltaShadow(deltaShadow(param));
		}
		if (color == ColorParam.noteBorder) {
			return new SymbolContext(getHtmlColor(param, ColorParam.noteBackground), getHtmlColor(param,
					ColorParam.noteBorder)).withStroke(getStroke(param, LineParam.noteBorder, 1)).withDeltaShadow(
					deltaShadow(param));
		}
		if (color == ColorParam.sequenceGroupBorder) {
			return new SymbolContext(getHtmlColor(param, ColorParam.sequenceGroupBackground), getHtmlColor(param,
					ColorParam.sequenceGroupBorder)).withStroke(getStroke(param, LineParam.sequenceGroupBorder, 2))
					.withDeltaShadow(deltaShadow(param));
		}
		if (color == ColorParam.sequenceBoxBorder) {
			return new SymbolContext(getHtmlColor(param, ColorParam.sequenceBoxBackground), getHtmlColor(param,
					ColorParam.sequenceBoxBorder));
		}
		if (color == ColorParam.sequenceReferenceBorder) {
			return new SymbolContext(getHtmlColor(param, ColorParam.sequenceReferenceHeaderBackground), getHtmlColor(
					param, ColorParam.sequenceReferenceBorder)).withStroke(
					getStroke(param, LineParam.sequenceReferenceBorder, 2)).withDeltaShadow(deltaShadow(param));
		}
		throw new IllegalArgumentException();
	}

	static public UStroke getStroke(ISkinParam param, LineParam lineParam, double defaultValue) {
		final UStroke result = param.getThickness(lineParam, null);
		if (result == null) {
			return new UStroke(defaultValue);
		}
		return result;
	}

	public Object getProtocolVersion() {
		return 1;
	}

}
