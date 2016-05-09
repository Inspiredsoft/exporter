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

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines the name of the property to export from the object
 * returned by the annotated method.
 * 
 * @author Massimo Romano
 *
 */
@Target({METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExpoProperty {
	/*
	 * Name of the property to export from the object
	 */
	public String value() default "";
	
	/*
	 * Name of the prefix key to use for converting primitive type using messages.
	 * eg. for int value 1 and prefix key "obj.type." the messages key "obj.type.1" is returned
	 */
	public String prefixKey() default "";
	
	/*
	 * Used to order the propertie during exportation
	 */
	public int position() default Integer.MAX_VALUE;
	
	/*
	 * Used to format value
	 */
	public String format() default "";
	
	/*
	 * Key used to get the label for the property	
	 */
	public String labelKey() default "";
}
