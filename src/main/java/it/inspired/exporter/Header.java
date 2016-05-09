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

package it.inspired.exporter;

import it.inspired.exporter.annotation.ExpoProperty;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 * The class used manage the information to write the header during the export process.
 * 
 * @author Massimo Romano
 *
 */
@SuppressWarnings("rawtypes")
public class Header
{
	/*
	 * The class referenced by the header
	 */
	private Class type;
	
	/*
	 * 
	 */
	private List<PropertyHeader> properties = new ArrayList<PropertyHeader>();
	
	public Header( Class type )
	{
		this.type = type;
	}
	
	public Class getType() {
		return type;
	}

	public void setType(Class type) {
		this.type = type;
	}

	public List<PropertyHeader> getProperties() {
		return properties;
	}

	public void setProperties(List<PropertyHeader> properties) {
		this.properties = properties;
	}
	
	public void addProperty(PropertyDescriptor property ) {
		this.properties.add( new PropertyHeader(property) );
	}
	
	public void addProperty(PropertyDescriptor property, ExpoProperty annotation) {
		this.properties.add( new PropertyHeader(property, annotation) );
	}
	
	public boolean isFor( Class clazz )
	{
		return this.type.equals( clazz );
	}
}
