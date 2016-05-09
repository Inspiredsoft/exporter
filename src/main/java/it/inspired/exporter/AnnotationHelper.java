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


import it.inspired.exporter.annotation.ExpoElement;
import it.inspired.exporter.annotation.ExpoProperties;
import it.inspired.exporter.annotation.ExpoProperty;
import it.inspired.exporter.annotation.ExportType;
import it.inspired.exporter.annotation.Unexportable;
import it.inspired.exporter.utils.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Helper class containing common methods to manage the framework annotation.
 * 
 * @author Massimo Romano
 *
 */
public class AnnotationHelper
{
	/**
	 * Check if the object class has the {@link Unexportable} annotation.
	 * @param obj Object to check.
	 * @return True if the class is annotated as {@link Unexportable}.
	 */
	public static boolean isUnexportable(Object obj) 
	{
		if ( obj != null )
		{
			Unexportable anno = obj.getClass().getAnnotation( Unexportable.class );
			return ( anno != null );
		}
		return false;
	}
	
	/**
	 * Check if the method has the Unexportable annotation.
	 * @param method Method to check.
	 * @return True if the class is annotated as {@link Unexportable}.
	 */
	public static boolean isUnexportable(Method method) 
	{
		Unexportable anno = method.getAnnotation( Unexportable.class );
		return ( anno != null );
	}
	
	//--------------------------------------------------------------------------------------
	
	/**
	 * Return the annotation {@link ExportType} defined in the class of the given object.
	 * @param obj The object to check.
	 * @return The {@link ExportType} or null if not annotated.
	 */
	public static ExportType getExportType( Object obj )
	{
		ExpoElement anno = obj.getClass().getAnnotation( ExpoElement.class );
		return ( anno != null ? anno.value() : null );
	}
	
	//--------------------------------------------------------------------------------------
	
	/**
	 * Check if the object should be ignored and not exported.
	 * @param obj The object to check.
	 * @return True if the object is marked to be ignored.
	 */
	public static boolean isExportIgnored( Object obj )
	{
		ExpoElement anno = obj.getClass().getAnnotation( ExpoElement.class );
		return ( anno != null ? anno.value().equals( ExportType.IGNORE ) : false );
	}
	
	//--------------------------------------------------------------------------------------
	
	/**
	 * Check if the object should be ignored but not the methods of the superclass.
	 * @param obj The object to check.
	 * @return True if the object is marked as superclass.
	 */
	public static boolean isExportSuperclass( Object obj )
	{
		ExpoElement anno = obj.getClass().getAnnotation( ExpoElement.class );
		return ( anno != null ? anno.value().equals( ExportType.SUPERCLASS ) : false );
	}
	
	//--------------------------------------------------------------------------------------
	
	/**
	 * Check if the class of the given object is annotated with {@link ExpoElement}.
	 * @param obj The object to check.
	 * @return True if the object is annotated with {@link ExpoElement}.
	 */
	public static boolean hasExpoElement( Object obj )
	{
		ExpoElement anno = obj.getClass().getAnnotation( ExpoElement.class );
		return ( anno != null );
	}
	
	/**
	 * Check if the class is annotated with {@link ExpoElement}.
	 * @param clazz The class to check.
	 * @return True if the class is annotated with {@link ExpoElement}.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static boolean hasExpoElement( Class clazz )
	{
		ExpoElement anno = (ExpoElement) clazz.getAnnotation( ExpoElement.class );
		return ( anno != null );
	}
	
	//--------------------------------------------------------------------------------------
	
	/**
	 * Return a list of {@link ExpoProperty} defined in the {@link ExpoElement}
	 * that annotate the class of the given object.
	 * 
	 * @param obj The object to check.
	 * @return The list of {@link ExpoProperty} if defined.
	 */
	public static List<ExpoProperty> getExportProperty( Object obj )
	{
		ExpoElement e = obj.getClass().getAnnotation( ExpoElement.class );
		if ( e != null )
		{
			return Arrays.asList( e.property() );
		}
		return null;
	}
	
	/**
	 * Return a list of {@link ExpoProperty} defined in the {@link ExpoProperties}
	 * or in {@link ExpoProperty} that annotate given method.
	 * 
	 * @param method The method to check.
	 * @return The list of {@link ExpoProperty} if defined.
	 */
	public static List<ExpoProperty> getExportProperty( Method method )
	{
		ExpoProperties e = method.getAnnotation( ExpoProperties.class );
		if ( e != null )
		{
			return Arrays.asList( e.property() );
		}
		ExpoProperty ep = method.getAnnotation( ExpoProperty.class );
		if ( ep != null )
		{
			return Arrays.asList( ep );
		}
 		return null;
	}
	
	//--------------------------------------------------------------------------------------
	
	/**
	 * Return the list of properties declared to be exported from the object
	 * using the {@link ExpoProperty} annotation.
	 * 
	 * @param obj The object to inspect.
	 * @return The list of properties to export.
	 */
	public static List<String> getProperty( Object obj )
	{
		List<String> props = new ArrayList<String>();
		List<ExpoProperty> eps = getExportProperty( obj );
		if ( eps != null )
		{
			for ( ExpoProperty ep : eps )
			{
				if ( !StringUtils.isEmpty( ep.value() ) )
				{
					props.add( ep.value()  );
				}
			}
		}
		return ( props.isEmpty() ? null : props );
	}
	
	/**
	 * Return the list of properties declared to be exported from the method
	 * using the {@link ExpoProperty} annotation.
	 * 
	 * @param method The method to inspect.
	 * @return The list of properties to export.
	 */
	public static List<String> getProperty( Method method )
	{
		List<String> props = new ArrayList<String>();
		List<ExpoProperty> eps = getExportProperty( method );
		if ( eps != null )
		{
			for ( ExpoProperty ep : eps )
			{
				if ( !StringUtils.isEmpty( ep.value() ) )
				{
					props.add( ep.value()  );
				}
			}
		}
		return  ( props.isEmpty() ? null : props );
	}
	
	//--------------------------------------------------------------------------------------
	
	public static String getPrefixKey( Object obj, String property )
	{
		List<ExpoProperty> eps = getExportProperty( obj );
		if ( eps != null )
		{
			for ( ExpoProperty ep : eps )
			{
				if ( StringUtils.isEmpty( ep.value() ) || ep.value().equals( property ) )
				{
					return ( StringUtils.isEmpty( ep.prefixKey() ) ? null : ep.prefixKey() );
				}
			}
		}
		return null;
	}
	
	public static String getPrefixKey( Method method, String property )
	{
		List<ExpoProperty> eps = getExportProperty( method );
		if ( eps != null )
		{
			for ( ExpoProperty ep : eps )
			{
				if ( StringUtils.isEmpty( ep.value() ) || property.equals( ep.value() ) )
				{
					return ( StringUtils.isEmpty( ep.prefixKey() ) ? null : ep.prefixKey() );
				}
			}
		}
		return null;
	}
	
	//--------------------------------------------------------------------------------------
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String getLabelKey( Class clazz )
	{
		ExpoElement ee = (ExpoElement) clazz.getAnnotation( ExpoElement.class );
		return ( ee != null && !StringUtils.isEmpty( ee.labelKey() ) ? ee.labelKey() : null );
	}
	
	public static String getLabelKey( PropertyDescriptor property )
	{
		Method method = property.getReadMethod();
		
		ExpoProperties eps = method.getAnnotation( ExpoProperties.class );
		
		if ( eps != null && !StringUtils.isEmpty( eps.labelKey() ) )
		{
			return eps.labelKey();
		}
		
		return getLabelKey( method.getAnnotation( ExpoProperty.class ) );
	}
	
	public static String getLabelKey( ExpoProperty ep ) {
		if ( ep != null && !StringUtils.isEmpty( ep.labelKey() ) )
		{
			return ep.labelKey();
		}
		return null;
	}
	
	//--------------------------------------------------------------------------------------
	
	/**
	 * Return the position defined in the {@link ExpoProperty} or max integer value if null.
	 * @param ep The {@link ExpoProperty} to check.
	 * @return The position defined or the max integer value.
	 */
	public static int getPosition( ExpoProperty ep ) 
	{
		if ( ep != null )
		{
			return ep.position();
		}
		return Integer.MAX_VALUE;
	}
	
	/**
	 * Return the position defined in the {@link ExpoProperty} or {@link ExpoProperties} 
	 * annotating hte given method or max integer value if they are not defined.
	 * @param method The method to check.
	 * @return The position defined or the max integer value.
	 */
	public static int getPosition( Method method )
	{
		ExpoProperty ep = method.getAnnotation( ExpoProperty.class );
		if ( ep != null )
		{
			return ep.position();
		}
		ExpoProperties eps = method.getAnnotation( ExpoProperties.class );
		if ( eps != null )
		{
			return eps.position();
		}
 		return Integer.MAX_VALUE;
	}
}
