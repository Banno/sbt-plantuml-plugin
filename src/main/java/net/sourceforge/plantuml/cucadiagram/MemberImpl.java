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
package net.sourceforge.plantuml.cucadiagram;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.UrlBuilder;
import net.sourceforge.plantuml.UrlBuilder.ModeUrl;
import net.sourceforge.plantuml.command.regex.MyPattern;
import net.sourceforge.plantuml.skin.VisibilityModifier;

public class MemberImpl implements Member {

	private final String display;
	private final boolean staticModifier;
	private final boolean abstractModifier;
	private final Url url;
	private final boolean hasUrl;

	private final VisibilityModifier visibilityModifier;

	public MemberImpl(String tmpDisplay, boolean isMethod, boolean manageModifier, boolean manageUrl) {
		if (manageModifier) {
			this.hasUrl = new UrlBuilder(null, ModeUrl.ANYWHERE).getUrl(tmpDisplay) != null;
			final Pattern pstart = MyPattern.cmpile("^(" + UrlBuilder.getRegexp() + ")([^\\[\\]]+)$");
			final Matcher mstart = pstart.matcher(tmpDisplay);

			if (mstart.matches()) {
				if (mstart.groupCount() != 5) {
					throw new IllegalStateException();
				}
				final UrlBuilder urlBuilder = new UrlBuilder(null, ModeUrl.AT_START);
				this.url = urlBuilder.getUrl(mstart.group(1));
				this.url.setMember(true);
				tmpDisplay = /* mstart.group(1).trim() + */StringUtils.trin(mstart.group(mstart.groupCount()));
			} else {
				final Pattern pend = MyPattern.cmpile("^((?:[^\\[\\]]|\\[[^\\[\\]]*\\])+)(" + UrlBuilder.getRegexp()
						+ ")$");
				final Matcher mend = pend.matcher(tmpDisplay);

				if (mend.matches()) {
					if (mend.groupCount() != 5) {
						throw new IllegalStateException();
					}
					final UrlBuilder urlBuilder = new UrlBuilder(null, ModeUrl.AT_END);
					this.url = urlBuilder.getUrl(mend.group(2));
					this.url.setMember(true);
					tmpDisplay = StringUtils.trin(mend.group(1));
				} else {
					this.url = null;
				}
			}
		} else {
			this.url = null;
			this.hasUrl = false;
		}

		final String lower = StringUtils.goLowerCase(tmpDisplay);

		if (manageModifier) {
			this.staticModifier = lower.contains("{static}") || lower.contains("{classifier}");
			this.abstractModifier = lower.contains("{abstract}");
			String displayClean = tmpDisplay.replaceAll("(?i)\\{(static|classifier|abstract)\\}\\s*", "");
			if (displayClean.length() == 0) {
				displayClean = " ";
			}

			if (VisibilityModifier.isVisibilityCharacter(displayClean.charAt(0))) {
				visibilityModifier = VisibilityModifier.getVisibilityModifier(displayClean.charAt(0), isMethod == false);
				this.display = StringUtils.trin(StringUtils.manageGuillemet(displayClean.substring(1)));
			} else {
				this.display = StringUtils.manageGuillemet(displayClean);
				visibilityModifier = null;
			}
		} else {
			this.staticModifier = false;
			this.visibilityModifier = null;
			this.abstractModifier = false;
			tmpDisplay = StringUtils.trin(tmpDisplay);
			this.display = tmpDisplay.length() == 0 ? " " : StringUtils.manageGuillemet(StringUtils.trin(tmpDisplay));
		}
	}

	public String getDisplay(boolean withVisibilityChar) {
		if (withVisibilityChar) {
			return getDisplayWithVisibilityChar();
		}
		return getDisplayWithoutVisibilityChar();
	}

	public String getDisplayWithoutVisibilityChar() {
		// assert display.length() == 0 || VisibilityModifier.isVisibilityCharacter(display.charAt(0)) == false;
		return display;
	}

	public String getDisplayWithVisibilityChar() {
		if (isPrivate()) {
			return "-" + display;
		}
		if (isPublic()) {
			return "+" + display;
		}
		if (isPackagePrivate()) {
			return "~" + display;
		}
		if (isProtected()) {
			return "#" + display;
		}
		return display;
	}

	@Override
	public boolean equals(Object obj) {
		final MemberImpl other = (MemberImpl) obj;
		return this.display.equals(other.display);
	}

	@Override
	public int hashCode() {
		return display.hashCode();
	}

	public final boolean isStatic() {
		return staticModifier;
	}

	public final boolean isAbstract() {
		return abstractModifier;
	}

	private boolean isPrivate() {
		return visibilityModifier == VisibilityModifier.PRIVATE_FIELD
				|| visibilityModifier == VisibilityModifier.PRIVATE_METHOD;
	}

	private boolean isProtected() {
		return visibilityModifier == VisibilityModifier.PROTECTED_FIELD
				|| visibilityModifier == VisibilityModifier.PROTECTED_METHOD;
	}

	private boolean isPublic() {
		return visibilityModifier == VisibilityModifier.PUBLIC_FIELD
				|| visibilityModifier == VisibilityModifier.PUBLIC_METHOD;
	}

	private boolean isPackagePrivate() {
		return visibilityModifier == VisibilityModifier.PACKAGE_PRIVATE_FIELD
				|| visibilityModifier == VisibilityModifier.PACKAGE_PRIVATE_METHOD;
	}

	public final VisibilityModifier getVisibilityModifier() {
		return visibilityModifier;
	}

	public final Url getUrl() {
		return url;
	}

	public boolean hasUrl() {
		return hasUrl;
	}

}
