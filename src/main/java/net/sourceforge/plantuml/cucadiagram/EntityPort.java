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
package net.sourceforge.plantuml.cucadiagram;

public class EntityPort {

	private final String uid;
	private final String portName;

	public EntityPort(String uid, String portName) {
		this.uid = uid;
		this.portName = portName;
	}

	public String getFullString() {
		if (portName != null) {
			return uid + ":" + portName;
		}
		return uid;
	}

	private boolean isShielded() {
		return uid.endsWith(":h");
	}

	public String getPrefix() {
		if (isShielded()) {
			return uid.substring(0, uid.length() - 2);
		}
		return uid;
	}

	public boolean startsWith(String centerId) {
		return uid.startsWith(centerId);
	}

	public boolean equalsId(EntityPort other) {
		return this.uid.equals(other.uid);
	}

	public String getPortName() {
		return portName;
	}

}
