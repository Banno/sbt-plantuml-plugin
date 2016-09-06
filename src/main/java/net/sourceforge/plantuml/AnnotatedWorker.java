/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2017, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
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
package net.sourceforge.plantuml;

import net.sourceforge.plantuml.activitydiagram3.ftile.EntityImageLegend;
import net.sourceforge.plantuml.cucadiagram.DisplayPositionned;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.svek.DecorateEntityImage;
import net.sourceforge.plantuml.svek.TextBlockBackcolored;

public class AnnotatedWorker {

	private final Annotated annotated;
	private final ISkinParam skinParam;

	public AnnotatedWorker(Annotated annotated, ISkinParam skinParam) {
		this.annotated = annotated;
		this.skinParam = skinParam;

	}

	public TextBlockBackcolored addAdd(TextBlock result) {
		result = addLegend(result);
		result = addTitle(result);
		result = addCaption(result);
		result = addHeaderAndFooter(result);
		return (TextBlockBackcolored) result;
	}

	private TextBlock addLegend(TextBlock original) {
		if (DisplayPositionned.isNull(annotated.getLegend())) {
			return original;
		}
		final TextBlock text = EntityImageLegend.create(annotated.getLegend().getDisplay(), getSkinParam());

		return DecorateEntityImage.add(original, text, annotated.getLegend().getHorizontalAlignment(), annotated
				.getLegend().getVerticalAlignment());
	}

	private ISkinParam getSkinParam() {
		return skinParam;
	}

	private TextBlock addCaption(TextBlock original) {
		if (DisplayPositionned.isNull(annotated.getCaption())) {
			return original;
		}
		final TextBlock text = getCaption();

		return DecorateEntityImage.addBottom(original, text, HorizontalAlignment.CENTER);
	}

	public TextBlock getCaption() {
		if (DisplayPositionned.isNull(annotated.getCaption())) {
			return TextBlockUtils.empty(0, 0);
		}
		return annotated
				.getCaption()
				.getDisplay()
				.create(new FontConfiguration(getSkinParam(), FontParam.CAPTION, null), HorizontalAlignment.CENTER,
						getSkinParam());
	}

	private TextBlock addTitle(TextBlock original) {
		if (DisplayPositionned.isNull(annotated.getTitle())) {
			return original;
		}
		final TextBlock text = annotated
				.getTitle()
				.getDisplay()
				.create(new FontConfiguration(getSkinParam(), FontParam.TITLE, null), HorizontalAlignment.CENTER,
						getSkinParam());

		return DecorateEntityImage.addTop(original, text, HorizontalAlignment.CENTER);
		// return new DecorateTextBlock(original, text, HorizontalAlignment.CENTER);
	}

	private TextBlock addHeaderAndFooter(TextBlock original) {
		if (DisplayPositionned.isNull(annotated.getFooter()) && DisplayPositionned.isNull(annotated.getHeader())) {
			return original;
		}
		final TextBlock textFooter = DisplayPositionned.isNull(annotated.getFooter()) ? null : annotated
				.getFooter()
				.getDisplay()
				.create(new FontConfiguration(getSkinParam(), FontParam.FOOTER, null),
						annotated.getFooter().getHorizontalAlignment(), getSkinParam());
		final TextBlock textHeader = DisplayPositionned.isNull(annotated.getHeader()) ? null : annotated
				.getHeader()
				.getDisplay()
				.create(new FontConfiguration(getSkinParam(), FontParam.HEADER, null),
						annotated.getHeader().getHorizontalAlignment(), getSkinParam());

		// return new DecorateTextBlock(original, textHeader, annotated.getHeader().getHorizontalAlignment(),
		// textFooter,
		// annotated.getFooter().getHorizontalAlignment());
		return new DecorateEntityImage(original, textHeader, annotated.getHeader().getHorizontalAlignment(),
				textFooter, annotated.getFooter().getHorizontalAlignment());
	}

}
