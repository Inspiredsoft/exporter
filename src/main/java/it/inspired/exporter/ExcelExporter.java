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
import java.math.BigDecimal;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

public class ExcelExporter extends Exporter
{
	private String dateFormat = "dd/MM/yyyy HH:mm";
	
	private Workbook 	workbook 	= null;
	private Sheet 		sheet  		= null;
	
	private CellStyle bigDecimalStyle 	= null;
	private CellStyle doubleStyle 		= null;
	private CellStyle dateStyle 		= null;
	private CellStyle integerStyle 		= null;
	
	//----------------------------------------------------------------------------------------------------------
	
	@Override
	public void init() 
	{
		super.init();

		// 2 rows are left for the header
		super.currentRow = 0;
		if ( super.isEnabledHeader() ) 
		{
			super.currentRow = 2;
		}
		workbook = new HSSFWorkbook();
		sheet = workbook.createSheet( "export" );
		
		// BigDecimal Style
		DataFormat formatbd = workbook.createDataFormat();
		bigDecimalStyle = workbook.createCellStyle();
		bigDecimalStyle.setDataFormat( formatbd.getFormat("#,##0.0000") );
		
		
		// Double Style
		DataFormat formatdb = workbook.createDataFormat();
		doubleStyle = workbook.createCellStyle();
		doubleStyle.setDataFormat( formatdb.getFormat("0.00") );
		
		// Date Style
		dateStyle = workbook.createCellStyle();
		CreationHelper helper = workbook.getCreationHelper();
		dateStyle.setDataFormat( helper.createDataFormat().getFormat( dateFormat ) );
		
		// Integer Style
		DataFormat formatint = workbook.createDataFormat();
		integerStyle = workbook.createCellStyle();
		integerStyle.setDataFormat( formatint.getFormat("0") );
		
	}
		
	//----------------------------------------------------------------------------------------------------------
	
	@Override
	protected void writeHeader() 
	{
		Row row0 = sheet.createRow( 0 );
		Row row1 = sheet.createRow( 1 );
		
		int coll = 0;
		for ( Header  header: headers )
		{
			Cell cell0 = row0.createCell( coll );
			//cell0.setCellValue( StringUtils.capitalizeMethodName( header.getType().getSimpleName() ) );
			cell0.setCellValue( super.getHeaderName( header ) );
					
			CellRangeAddress region = new CellRangeAddress(0, 0, coll, coll + header.getProperties().size() - 1 );
			sheet.addMergedRegion( region );
		
			CellStyle style = workbook.createCellStyle();
			style.setAlignment( CellStyle.ALIGN_CENTER );
			cell0.setCellStyle( style );
			
			for ( PropertyHeader ph : header.getProperties() )
			{
				String collName = null;
				
				Cell cell1 = row1.createCell( coll );
				
				// Check if property labekKey is overrided
				if ( ph.isOverrided() ) {
					String key = AnnotationHelper.getLabelKey( ph.getOverridingEpoProperty() );
					if ( key != null ) {
						collName = getText( key );
					}
				}
				
				if ( collName == null ) 
				{
					collName = super.getPropertyHeaderName( ph.getProperty() );
				}
				
				cell1.setCellValue( collName );
				coll++;
			}
		}
	}

	//----------------------------------------------------------------------------------------------------------
	
	@Override
	protected void writeValue(int row, int coll, Object value) 
	{
		Row exrow = sheet.getRow( row );
		if ( exrow == null )
		{
			exrow = sheet.createRow( row );
		}
		Cell cell = exrow.createCell( coll );
		setCell( cell, value );
	}

	//----------------------------------------------------------------------------------------------------------
	
	@Override
	public void finalyze() {
		if ( super.isEnabledHeader() )
		{
			writeHeader();
		}
	}

	//----------------------------------------------------------------------------------------------------------

	@Override
	public void write(OutputStream outputStream) throws IOException {
		workbook.write( outputStream );
	}
	
	//----------------------------------------------------------------------------------------------------------
	
	private void setCell( Cell cell, Object obj )
	{
		if ( obj == null )
		{
			cell.setCellValue("");
		}
		else if ( obj instanceof Date )
		{
			cell.setCellValue( (Date) obj );
			cell.setCellStyle( dateStyle );
		}
		else if ( obj instanceof Boolean )
		{
			cell.setCellValue( (Boolean) obj );
		}
		else if ( obj instanceof Integer || obj instanceof Long )
		{
			cell.setCellValue( Double.parseDouble( obj.toString() ) );
			cell.setCellStyle( integerStyle );
		}
		else if ( obj instanceof Double )
		{
			cell.setCellValue( Double.parseDouble( obj.toString() ) );
			cell.setCellStyle( doubleStyle );
		}
		else if ( obj instanceof BigDecimal )
		{
			cell.setCellValue( Double.parseDouble( obj.toString() ) );
			cell.setCellStyle( bigDecimalStyle );
		}
		else
		{
			CreationHelper helper = workbook.getCreationHelper();
			cell.setCellValue( helper.createRichTextString( obj.toString() ) );
		}
	}
}
