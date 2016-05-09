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
import it.inspired.exporter.comparator.ExpoPropertyComparator;
import it.inspired.exporter.comparator.PropertyDescriptionComparator;
import it.inspired.exporter.utils.BeanUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.proxy.HibernateProxy;

/**
 * The abstract class that are extended to implements the 
 * two types of exporter provided: Excel and TXT.
 * 
 * @author Massimo Romano
 *
 */
public abstract class Exporter 
{
	private static final Logger log = Logger.getLogger(Exporter.class);
	
	// Object exported in a row
	private List<Object> exportedInRow = new ArrayList<Object>();
	
	protected List<Header> headers = new ArrayList<Header>();
	
	protected int currentRow = 0;
	
	protected boolean enabledHeader = true;
	
	//-------------------------------------------------------------------------------------------------
	// Abstract Methods
	//-------------------------------------------------------------------------------------------------
	
	/**
	 * Used to write the header columns
	 */
	protected abstract void writeHeader();
	
	protected abstract void writeValue( int row, int coll, Object value );
	
	public abstract void finalyze();
	
	public abstract void write( OutputStream outputStream ) throws IOException;
	
	//-------------------------------------------------------------------------------------------------
	// Protected Methods
	//-------------------------------------------------------------------------------------------------
	
	public void init() {
		currentRow = 0;
	}
	
	//--------------------------------------------------------------------------------------
	
	/**
	 * Overriding this method is possible to exclude some properties from the export process.
	 * @return A list of properties to exclude.
	 */
	protected List<String> getExcludedProperties() {
		return new ArrayList<String>();
	}
	
	//--------------------------------------------------------------------------------------
	
	/**
	 * This method is used basically for  internationalization to convert a key into a specific text
	 * @param key A string key to convert
	 * @return The message returned
	 */
	protected String getText( String key ) {
		throw new UnsupportedOperationException("getText(String key) must be implemented to use key converter");
	}
	
	//--------------------------------------------------------------------------------------
	
	/**
	 * Return the capitalized name of the class or the name specified using the labelKey
	 * @param header
	 * @return
	 */
	protected String getHeaderName( Header header ) 
	{
		String name = BeanUtils.capitalizeMethodName( header.getType().getSimpleName() );
		String annoname = AnnotationHelper.getLabelKey( header.getType() );
		if ( annoname != null ) {
			name = getText( annoname );
		}
		return name;
	}
	
	protected String getPropertyHeaderName( PropertyDescriptor property ) 
	{
		String name = BeanUtils.capitalizeMethodName( property.getName() );
		String annoname = AnnotationHelper.getLabelKey( property );
		if ( annoname != null ) {
			name = getText( annoname );
		}
		return name;
	}
	
	//--------------------------------------------------------------------------------------
	
	/**
	 * Used to declare the header text of the exported file
	 * @param col Column position of the header text
	 * @param info Bean Information containing the property
	 * @param property Property describing the column information
	 */
	protected void addHeader( Integer col, BeanInfo info,  PropertyDescriptor property, ExpoProperty annotation )
	{
		Header header = null;
		// Searching for the header associated to the bean information
		for ( Header head : headers )
		{
			if ( head.isFor( info.getBeanDescriptor().getBeanClass() ) )
			{
				header = head;
				break;
			}
		}
		if ( header == null )
		{
			header = new Header( info.getBeanDescriptor().getBeanClass() );
			headers.add( header );
		}
		// Searching for the header property, if exist return
		for ( PropertyHeader ph : header.getProperties() )
		{
			if ( ph.getProperty().equals( property ) )
			{
				return;
			}
		}
		
		// Add the header
		if ( annotation == null ) {
			header.addProperty( property );
		} else {
			header.addProperty(property, annotation);
		}
	}
		
	//--------------------------------------------------------------------------------------
	// Private methods.
	//--------------------------------------------------------------------------------------
	
	/**
	 * Used to deproxy an hibernate object
	 * @param proxy The object to deproxy
	 * @return The deproxed object
	 */
	private static Object deproxy(Object proxy) 
	{
		if ( proxy instanceof HibernateProxy ) 
		{
			return ( ( HibernateProxy ) proxy ).getHibernateLazyInitializer()
					 .getImplementation();
		}
		else 
		{
			return proxy;
		}
	}
	 
	//--------------------------------------------------------------------------------------
	
	/**
	 * Check if a property is included in the list of the excluded properties.
	 * @param property The property to check.
	 * @return True if exportable.
	 */
	private boolean isExportable( PropertyDescriptor property )
	{
		return !getExcludedProperties().contains( property.getName() );
	}
	
	//--------------------------------------------------------------------------------------
	
	/**
	 * Start the export of the object to the given row starting from column zero.
	 * @param row The row number where to export the object.
	 * @param obj The object to export.
	 * @return The number of the column where the export ends.
	 * 
	 * @throws IntrospectionException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private int export( int row, Object obj ) throws IntrospectionException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		exportedInRow.clear();
		int col = export( row, 0, obj );
		log.debug( "Used columns: " + col );
		return col;
	}
	
	//--------------------------------------------------------------------------------------

	/**
	 * Export the given object starting from the row and column specified.
	 * @param row The row number to start.
	 * @param coll The column number to start.
	 * @param obj The object to export.
	 * @return The number of the column where the export ends.
	 * 
	 * @throws IntrospectionException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private int export( int row, int coll, Object obj ) throws IntrospectionException, IllegalArgumentException, IllegalAccessException, InvocationTargetException 
	{
		List<Object> expQueue = new ArrayList<Object>();
		
		obj = deproxy( obj );
		
		// Check if it is exportable
		if ( obj == null || AnnotationHelper.isUnexportable( obj ) || AnnotationHelper.isExportIgnored ( obj ) || !AnnotationHelper.hasExpoElement( obj ) )
		{
			return coll;
		}
		
		BeanInfo info = null;
		if ( AnnotationHelper.isExportSuperclass( obj ) )
		{
			// Only the properties of the superclass are exported if they exist
			if ( obj.getClass().getSuperclass() == null )
			{
				return coll;
			}
			info = Introspector.getBeanInfo( obj.getClass().getSuperclass() );
		}
		else // Export Class
		{
			info = Introspector.getBeanInfo( obj.getClass() );
		}
		
		log.debug( "Exporting object " + obj.getClass().getName() );
		
		exportedInRow.add( obj );
		
		// Get the list of properties to export for the given object if specified
		List<String> oprops = AnnotationHelper.getProperty( obj );
		
		// Gets all the properties from the objects and sort them
		PropertyDescriptor[] pds = info.getPropertyDescriptors();
		Arrays.sort( pds, new PropertyDescriptionComparator() );
		
		 // If the object has an identifier, it is placed at the beginning
		if( BeanUtils.hasProperty( obj.getClass(), "id" ) )
		{
			PropertyDescriptor pid = BeanUtils.getPropertyDescriptor( obj.getClass(), "id" );
			if ( !AnnotationHelper.isUnexportable( pid.getReadMethod() ) )
			{
				addHeader( coll, info, pid, null );
				writeValue( row, coll, pid.getReadMethod().invoke(obj) );
				coll++;
			}
		}
		
		// For each property the export rules are evaluated
		for (PropertyDescriptor property : pds)
		{  
			// Id is ignored and any other property not included in the list of properties specified
			// in the ExpoProperty annotation if defined
			if ( property.getName().equals("id") || ( oprops != null && !oprops.contains( property.getName() ) ) )
			{
				continue;
			}
			
			// Gets the method to get the property
			Method propertyGetter = property.getReadMethod();
			
			// Check if the getter is marked as unexportable
			if ( AnnotationHelper.isUnexportable( propertyGetter ) )
			{
				log.debug( "Property " + property.getName() + " has Unxportable annotation" );
				continue;
			}
			
			// Check if the property is declased unexportable (eg. version)
            if ( !isExportable( property ) )
            {
            	log.debug( "Exluded property " + property.getName() + " of type " + property.getPropertyType().getSimpleName() );
            	continue;
            }
            
        	if ( BeanUtils.isPrimitive( property ) )
        	{
        		log.debug( "Exporting property " + property.getName() + " of type " + property.getPropertyType().getSimpleName() );
     
            	addHeader( coll, info, property, null );
            	
            	// Get the prefix used to convert the value to a message
            	String prefix = AnnotationHelper.getPrefixKey( obj, property.getName() );	
            	if ( prefix == null )
            	{
            		prefix = AnnotationHelper.getPrefixKey( propertyGetter, property.getName() );
            	}
            	
            	// Get the value and convert it if there is a prefix
            	Object ovalue = propertyGetter.invoke(obj);	
    			if ( prefix == null || ovalue == null )
    			{
    				writeValue( row, coll, ovalue );
    			}
    			else
    			{
    				writeValue( row, coll, getText( prefix + ovalue ) );
    			}
    			coll++;
    		}	     
        	else
        	{
        		// Oggetto non primitivo, verifico se e' indicata la proprieta' da esportare
        		List<ExpoProperty> eprops = AnnotationHelper.getExportProperty( propertyGetter );
        		if ( eprops == null )
        		{
        			// Se la propieta' non e' indicata si esporta tutto l'oggetto come entita' a se
        			Object value = propertyGetter.invoke(obj);
        			expQueue.add( value );
        		}
        		else
        		{
        			Collections.sort( eprops, new ExpoPropertyComparator() );
        			// Viene restituita la sola proprieta' indicata
        			Object value = propertyGetter.invoke(obj);
        			for ( ExpoProperty eprop : eprops )
        			{
            			Object pvalue = BeanUtils.getProperty( value, eprop.value() );
            			if ( pvalue == null || BeanUtils.isPrimitive( pvalue.getClass() ) )
                    	{    				
            				log.debug( "Exporting property " + eprop + " of type " + info.getBeanDescriptor().getBeanClass().getSimpleName() );
            			     
        	            	addHeader( coll, info, BeanUtils.getPropertyDescriptor( property.getPropertyType(), eprop.value() ), eprop  );
        	            	
        	            	String prefix = AnnotationHelper.getPrefixKey( propertyGetter, eprop.value() );
                			if ( prefix == null )
                			{
                				writeValue( row, coll, pvalue );
                			}
                			else
                			{
                				writeValue( row, coll, getText( prefix + pvalue ) );
                			}
                			coll++;
                    	}
            			else
            			{
            				expQueue.add( pvalue );
            			}
        			}
        		}
        	}
    	}
		if ( !expQueue.isEmpty() )
		{
			for ( Object value : expQueue )
			{
    			if ( !exportedInRow.contains( value ) )
    			{
    				coll = export( row, coll, value );
    			}
			}
		}
		return coll;
	}
	
	//-------------------------------------------------------------------------------------------------
	// Public Methods
	//-------------------------------------------------------------------------------------------------
	
	/**
	 * Start the export process to the given list of object.
	 * @param list The list of object to export.
	 * 
	 * @throws IllegalArgumentException
	 * @throws IntrospectionException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@SuppressWarnings("rawtypes")
	public void export( List list ) throws IllegalArgumentException, IntrospectionException, IllegalAccessException, InvocationTargetException
	{
		for ( Object item : list )
		{
			export( currentRow++, item );
		}
	}

	/**
	 * Check if the header has to be added to the exported file.
	 * @return True if the header is enabled.
	 */
	public boolean isEnabledHeader() {
		return enabledHeader;
	}

	/**
	 * Set the enable header option.
	 * @param enabledHeader The option to set.
	 */
	public void setEnabledHeader(boolean enabledHeader) {
		this.enabledHeader = enabledHeader;
	}
}
