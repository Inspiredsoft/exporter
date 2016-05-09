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

package it.inspired.exporter.utils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Utility class for bean operation.
 * 
 * @author Massimo Romano
 *
 */
public class BeanUtils {
	
	/**
	 * Check if the class has the given property 
	 * 
	 * @param clazz The class to check
	 * @param propertyName The name of the property to find
	 * @return True if the class has the property
	 */
	@SuppressWarnings("rawtypes")
	public static boolean hasProperty( Class clazz, String propertyName )
	{
		try 
		{
			new PropertyDescriptor( propertyName, clazz );
		}
		catch (IntrospectionException e) 
		{
			return false;
		}
		return true;
	}
	
	//--------------------------------------------------------------------------------------
	
	/**
	 * Return the {@link PropertyDescriptor} for the propertyName in the given class.
	 * The property coud have or not the "get" prefix.
	 * 
	 * @param clazz The class where to find the property
	 * @param propertyName The property name to find
	 * @return The {@link PropertyDescriptor} required if exist
	 */
	@SuppressWarnings("rawtypes")
	public static PropertyDescriptor getPropertyDescriptor( Class clazz, String propertyName )
	{
		try 
		{
			return new PropertyDescriptor( propertyName, clazz );
		}
		catch (IntrospectionException e) 
		{
			try 
			{
				return new PropertyDescriptor( propertyName, clazz, getPropertyGetterName( propertyName ), null  );
			} 
			catch (IntrospectionException e1) 
			{
				return null;
			}
		}
	}
	
	//--------------------------------------------------------------------------------------
	
	/**
	 * Check if the property is primitive including the wrapped types like Boolean, Double, String, etc.
	 * @param property The property to check
	 * @return True id the given property is primitive
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isPrimitive( PropertyDescriptor property )
	{
		Class type = property.getPropertyType();
		return type.isPrimitive() || isPrimitive( type );
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean isPrimitive( Class type )
	{
		return ( type == Integer.class 	 || type == Double.class  || type == String.class     || 
  				 type == Date.class 	 || type == Boolean.class || type == BigDecimal.class ||
  				 type == Timestamp.class || type == Long.class );
	}
	
	//--------------------------------------------------------------------------------------

	/**
     * Retrieve a value from a property using
     * 
     * @param obj The object who's property you want to fetch
     * @param property The property name
     * @param defaultValue A default value to be returned if either a) The property is
     *  not found or b) if the property is found but the value is null
     * @return THe value of the property
     */
    @SuppressWarnings("unchecked")
	public static <T> T getProperty(Object obj, String property, T defaultValue) {

        T returnValue = (T) getProperty(obj, property);
        if (returnValue == null) {
            returnValue = defaultValue;
        }

        return returnValue;

    }

    /**
     * Fetch a property from an object. For example of you wanted to get the foo
     * property on a bar object you would normally call {@code bar.getFoo()}. This
     * method lets you call it like {@code BeanUtil.getProperty(bar, "foo")}
     * 
     * @param obj The object who's property you want to fetch
     * @param property The property name
     * @return The value of the property or null if it does not exist.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object getProperty(Object obj, String property) {
        Object returnValue = null;

        try {
            String methodName = "get" + property.substring(0, 1).toUpperCase() + property.substring(1, property.length());
            Class clazz = obj.getClass();
            Method method = clazz.getMethod(methodName, null);
            returnValue = method.invoke(obj, null);
        }
        catch (Exception e) {
            // Do nothing, we'll return the default value
        }

        return returnValue;
    }
    
    //------------------------------------------------------------------------------------
    
    /**
     * Return the name of the method adding a space before each uppercase character.
     * Eg. the method name countNumerOfChar is returned as Count NUmber Of Character.
     * 
     * @param methodName The name of the method to transform
     * @return The capitalized method name
     */
    public static String capitalizeMethodName( String methodName )
	{
		String res = "";
		if ( !StringUtils.isEmpty( methodName ) )
		{
			for ( int i = 0; i<methodName.length(); i++ )
			{
				Character c = methodName.charAt( i );
				if ( i == 0 )
				{
					res += Character.toUpperCase( c );
				}
				else if ( Character.isUpperCase(c) )
				{
					res += " " + c;
				}
				else
				{
					res += c;
				}
			}
		}
		return res;
	}
    
    //------------------------------------------------------------------------------------
    
    public static String getPropertyGetterName( String property ) {
    	return "get" + property.charAt( 0 ) + property.substring( 1 );
    }
}
