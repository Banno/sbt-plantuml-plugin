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
package net.sourceforge.plantuml.graph;

import net.sourceforge.plantuml.cucadiagram.IEntity;

public class EntityImageFactory {

	public AbstractEntityImage createEntityImage(IEntity entity) {
		throw new UnsupportedOperationException();
//		if (entity.getEntityType() == LeafType.CLASS || entity.getEntityType() == LeafType.ANNOTATION
//				|| entity.getEntityType() == LeafType.ABSTRACT_CLASS || entity.getEntityType() == LeafType.INTERFACE
//				|| entity.getEntityType() == LeafType.ENUM) {
//			return new EntityImageClass(entity);
//		}
//		if (entity.getEntityType() == LeafType.ACTIVITY) {
//			return new EntityImageActivity(entity);
//		}
//		if (entity.getEntityType() == LeafType.NOTE) {
//			return new EntityImageNote(entity);
//		}
//		if (entity.getEntityType() == LeafType.POINT_FOR_ASSOCIATION) {
//			return new EntityImageActivityCircle(entity, 4, 4);
//		}
//		if (entity.getEntityType() == LeafType.CIRCLE_START) {
//			return new EntityImageActivityCircle(entity, 18, 18);
//		}
//		if (entity.getEntityType() == LeafType.CIRCLE_END) {
//			return new EntityImageActivityCircle(entity, 18, 11);
//		}
//		if (entity.getEntityType() == LeafType.BRANCH) {
//			return new EntityImageActivityBranch(entity);
//		}
//		if (entity.getEntityType() == LeafType.SYNCHRO_BAR) {
//			return new EntityImageActivityBar(entity);
//		}
//		if (entity.getEntityType() == LeafType.USECASE) {
//			return new EntityImageUsecase(entity);
//		}
//		if (entity.getEntityType() == LeafType.ACTOR) {
//			return new EntityImageActor(entity);
//		}
//		if (entity.getEntityType() == LeafType.CIRCLE_INTERFACE) {
//			return new EntityImageCircleInterface(entity);
//		}
//		if (entity.getEntityType() == LeafType.COMPONENT) {
//			return new EntityImageComponent(entity);
//		}
//		return new EntityImageDefault(entity);
	}

}
