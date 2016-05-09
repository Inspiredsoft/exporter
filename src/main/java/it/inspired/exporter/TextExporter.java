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

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The TXT export implementation 
 * @author Massimo Romano
 *
 */
public class TextExporter extends Exporter
{
	TextBuilder 	text 	= null;
	
	private String separator = ";";
	private String enclosure = "\"";
	private String dateFormat = "dd/MM/yyyy HH:mm";
	private SimpleDateFormat sdf = new SimpleDateFormat( dateFormat );
	
	@Override
	protected void writeHeader() {
		Integer coll = 0;
		for ( Header  header: headers )
		{
			for ( PropertyHeader ph : header.getProperties() )
			{
				String collName = null;
				
				// Check if property labekKey is overrided
				if ( ph.isOverrided() ) {
					String key = AnnotationHelper.getLabelKey( ph.getOverridingEpoProperty() );
					if ( key != null ) {
						collName  = getText( key );
					}
				}
				
				if ( collName == null ) 
				{
					collName = super.getPropertyHeaderName( ph.getProperty() );
				}
				text.add( 0, coll, collName );
				
				coll++;
			}
		}
	}

	@Override
	protected void writeValue(int row, int coll, Object value) {
		if ( value != null )
		{
			if ( value instanceof Date ) {
				text.add( row, coll, enclosure + sdf.format( value ) + enclosure );
			}
			else 
			{
				text.add( row, coll, enclosure + value.toString() + enclosure);
			}
		}
		else
		{
			text.add( row, coll, "" );
		}
	}

	@Override
	public void init() {
		super.currentRow = 0;
		if ( super.isEnabledHeader() )
		{
			super.currentRow = 1;
		}
		text = new TextBuilder();
		text.setSeparator(separator);
	}

	@Override
	public void finalyze() 
	{
		if ( super.isEnabledHeader() )
		{
			writeHeader();
		}
	}

	@Override
	public void write(OutputStream outputStream) throws IOException 
	{
	    outputStream.write( text.toString().getBytes() );	
	}

}
