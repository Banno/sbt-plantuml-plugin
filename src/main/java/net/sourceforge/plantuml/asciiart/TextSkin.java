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
package net.sourceforge.plantuml.asciiart;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.skin.ArrowConfiguration;
import net.sourceforge.plantuml.skin.ArrowDirection;
import net.sourceforge.plantuml.skin.Component;
import net.sourceforge.plantuml.skin.ComponentType;
import net.sourceforge.plantuml.skin.Skin;
import net.sourceforge.plantuml.skin.rose.ComponentRoseGroupingSpace;

public class TextSkin implements Skin {

	private final FileFormat fileFormat;

	public TextSkin(FileFormat fileFormat) {
		this.fileFormat = fileFormat;
	}

	public Component createComponent(ComponentType type, ArrowConfiguration config, ISkinParam param,
			Display stringsToDisplay) {
		if (type == ComponentType.PARTICIPANT_HEAD || type == ComponentType.PARTICIPANT_TAIL) {
			return new ComponentTextParticipant(type, stringsToDisplay, fileFormat);
		}
		if (type == ComponentType.ACTOR_HEAD || type == ComponentType.ACTOR_TAIL) {
			return new ComponentTextActor(type, stringsToDisplay, fileFormat);
		}
		if (type.isArrow()
				&& ((config.getArrowDirection() == ArrowDirection.LEFT_TO_RIGHT_NORMAL) || (config.getArrowDirection() == ArrowDirection.RIGHT_TO_LEFT_REVERSE))) {
			return new ComponentTextArrow(type, config, stringsToDisplay, fileFormat);
		}
		if (type.isArrow() && config.isSelfArrow()) {
			return new ComponentTextSelfArrow(type, config, stringsToDisplay, fileFormat);
		}
		if (type == ComponentType.PARTICIPANT_LINE) {
			return new ComponentTextLine(fileFormat);
		}
		if (type == ComponentType.CONTINUE_LINE) {
			return new ComponentTextLine(fileFormat);
		}
		if (type == ComponentType.DELAY_LINE) {
			return new ComponentTextLine(fileFormat);
		}
		if (type == ComponentType.ALIVE_BOX_CLOSE_CLOSE) {
			return new ComponentTextActiveLine(fileFormat);
		}
		if (type == ComponentType.ALIVE_BOX_CLOSE_OPEN) {
			return new ComponentTextActiveLine(fileFormat);
		}
		if (type == ComponentType.ALIVE_BOX_OPEN_CLOSE) {
			return new ComponentTextActiveLine(fileFormat);
		}
		if (type == ComponentType.ALIVE_BOX_OPEN_OPEN) {
			return new ComponentTextActiveLine(fileFormat);
		}
		if (type == ComponentType.NOTE) {
			return new ComponentTextNote(type, stringsToDisplay, fileFormat);
		}
		if (type == ComponentType.DIVIDER) {
			return new ComponentTextDivider(type, stringsToDisplay, fileFormat);
		}
		if (type == ComponentType.GROUPING_HEADER) {
			return new ComponentTextGroupingHeader(type, stringsToDisplay, fileFormat);
		}
		if (type == ComponentType.GROUPING_SPACE) {
			return new ComponentRoseGroupingSpace(1);
		}
		if (type == ComponentType.GROUPING_ELSE) {
			return new ComponentTextGroupingElse(type, stringsToDisplay, fileFormat);
		}
		if (type == ComponentType.NEWPAGE) {
			return new ComponentTextNewpage(fileFormat);
		}
		throw new UnsupportedOperationException(type.toString());
	}

	public Object getProtocolVersion() {
		return 1;
	}

}
