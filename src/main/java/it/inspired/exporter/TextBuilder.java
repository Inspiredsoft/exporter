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

import java.util.HashMap;
import java.util.Map;

public class TextBuilder 
{	
	private Integer maxRow  = 0;
	private Integer maxColl = 0;
	private String separator = ";";
	private Map<String, String> text = new HashMap<String,String>();
	
	private String key( Integer row, Integer coll ) {
		return row + ":" + coll;
	}
	
	//---------------------------------------------------------------------------------
	
	public void setSeparator( String separator ) {
		this.separator = separator;
	}
	
	public void add( Integer row, Integer coll, String value ) {
		String key = key(row,coll);
		text.put( key, value );
		maxRow  = Math.max( maxRow, row );
		maxColl = Math.max( maxColl, coll );
	}
	
	public String get( Integer row, Integer coll ) {
		String key = key(row,coll);
		if ( text.containsKey( key ) ) {
			return text.get(key);
		}
		return "";
	}
	
	public String toString() {
		StringBuilder str = new StringBuilder();
		
		for ( int row = 0; row <= maxRow; row ++ ) {
			for ( int coll = 0; coll <= maxColl; coll++ ) {
				str.append( get( row, coll ) );
				if ( coll != maxColl ) {
					str.append( separator );
				}
			}
			str.append( System.getProperty("line.separator") );
		}
		
		return str.toString();
	}

}
