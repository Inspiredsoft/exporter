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

package it.inspired.exporter.comparator;

import it.inspired.exporter.AnnotationHelper;
import it.inspired.exporter.annotation.ExpoProperty;

import java.util.Comparator;

/**
 * Compares the position defined for the given two {@Link ExpoProperty}
 * 
 * @author Massimo Romano
 *
 */
public class ExpoPropertyComparator implements Comparator<ExpoProperty>{

	public int compare(ExpoProperty pd1, ExpoProperty pd2) 
	{	
		int pos1 = AnnotationHelper.getPosition( pd1 );
		int pos2 = AnnotationHelper.getPosition( pd2 );
		return Integer.valueOf( pos1 ).compareTo( Integer.valueOf( pos2 ) );
	}

}
