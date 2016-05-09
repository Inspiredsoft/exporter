/*******************************************************************************
* Inspired Model Exporter is a framework to export data from pojo class.
* Copyright (C) 2016 Inspired Soft
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.    
*******************************************************************************/

package it.inspired.exporter.annotation;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that defines how to export the annotated class.
 * 
 * @author Massimo Romano
 *
 */
@Target({TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExpoElement {
	/*
	 * Defines if the object is exportable or not,
	 * and the way the data are exported.
	 */
	public ExportType value() default ExportType.CLASS;
	
	/*
	 * List of property to export
	 */
	public ExpoProperty[] property() default {};
	
	/*
	 * Key used to find the label for the annotated type.
	 * The label is used as header for the exported data.  	
	 */
	public String labelKey() default "";
}
