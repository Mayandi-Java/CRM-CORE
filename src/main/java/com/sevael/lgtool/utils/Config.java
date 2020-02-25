package com.sevael.lgtool.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class Config implements UtilConstants {
    private static Config singleton = new Config();
    private Map<String,Properties> propMap = new HashMap<String,Properties>(5);
    private String globalPropertiesFile = null;

    /**
     * Returns the Config instance
     *
     * @return  The Config
     */
    public static Config instance() {
        return singleton;
    }


    /**
     * Private constructor for singleton
     *   Starts the notification timer
     */
    private Config() {
    	try	{
			// Determine global properties file name.
			globalPropertiesFile = System.getProperty("PROFMANGlobalPropertiesFile" );
			if( globalPropertiesFile == null || globalPropertiesFile.trim().length() <= 0 )	{
				System.out.println("ERROR : Please specify PROFMANGlobalPropertiesFile correctly.");
				globalPropertiesFile="../conf/profman_global.properties";
			}
		} catch(Exception e)	{
			e.printStackTrace();
		}
	}

	/**
     * Returns a value from the global property file
     *
     * @param theParam The property key to return the value for
     * @return Returns the property for the specified key, if found.
     *         If not found returns null.
     */
    public String getGlobalProperty(String theParam) {
        String strReturn=getProperty(theParam,"");
        if(strReturn==null || strReturn.trim().length()<=0) return null;
        else return strReturn;
    }

    /**
     * Returns a value from the global property file
     *
     * @param theParam The property key to return the value for
     * @param theDefault The default value to return
     * @return Returns the property for the specified key, if found.
     *         If not found returns the default value.
     */
    public String getGlobalProperty(String theParam,String theDefault) {
        String strReturn=getProperty(theParam,theDefault);
        if(strReturn==null || strReturn.trim().length()<=0) return null;
        else return strReturn;
    }

    /**
     * Returns a value from the global properties value specified
     * in the system property "propertyfile"
     *
     * @param theKey The property key to return the value for
     * @param theDefault The default value to return
     * @return Returns the property for the specified key, if found.
     *         If not found returns the default value.
     */
    public String getProperty(String theKey, String theDefault) {
        String filename=globalPropertiesFile;
        return getPropertyFromFile(filename,theKey,theDefault);
    }

    /**
     * Returns a value from the properties file that is specified
     * by the property <I>theFileProperty</I>.  Common
     * files have a constant in the Config class
     *
     * @param theFileProperty
     * @param theKey
     * @param theDefault
     * @return Returns the value for the property
     */
    public String getProperty(String theFileProperty, String theKey, String theDefault)     {
        String filename = Config.instance().getProperty(theFileProperty, globalPropertiesFile);
        return getPropertyFromFile(filename,theKey,theDefault);
    }

    /**
     * Retrieves a property from the property file specified by the
     * filename.  These files are cached in the propMap variable
     * and are reloaded when the timer which is created in the
     * contructor notifies this class to do so
     */
    private String getPropertyFromFile(String theFileName, String theKey, String theDefault) {
    	
        if (theFileName == null) throw new IllegalArgumentException("The file name cannot be null.");
        if (theKey == null) throw new IllegalArgumentException("The key cannot be null.");
        String retVal = null;
        Properties propFile = null;

        synchronized (this) {
            propFile = (Properties) propMap.get(theFileName);
            if (propFile == null) {
                propFile = new Properties();
                loadProperties(theFileName,propFile);
                propMap.put(theFileName,propFile);
            }
        }

        if ( propFile != null ) {
            retVal = (String) propFile.getProperty(theKey);
        }

        if (retVal != null) {
            //System.out.println("Got value for property: [" + theKey + "]  value: [" + retVal + "]...");
            //check property value for path substitution tags
            retVal = transformPath(retVal);
        }
        else {
            //System.out.println("Could not get value for property: [" + theKey +"], using the default value [" + theDefault + "]...");
            retVal = theDefault;
        }
        return retVal;
    }


    /**
    * Returns an enumeration of all the keys in this property list, including the keys in the default property list
    */
    public Enumeration<?> propertyNames(String theFileProperty)     {
        String filename = Config.instance().getProperty(theFileProperty,globalPropertiesFile);
        return propertyNamesFromFile(filename);
    }

    private Enumeration<?> propertyNamesFromFile(String theFileName)     {
        if (theFileName == null) throw new IllegalArgumentException("The file name cannot be null.");
        Enumeration<?> retVal = null;
        Properties propFile = null;

        synchronized (this) {
            propFile = (Properties) propMap.get(theFileName);
            if (propFile == null) {
                propFile = new Properties();
                loadProperties(theFileName,propFile);
                propMap.put(theFileName,propFile);
            }
        }

        if ( propFile != null )         {
            retVal = propFile.propertyNames();
        }
        return retVal;
    }

    /**
     * Searches the property for a substitution tag, and if found will
     * substitute the property that the tag specifies.  For example:<BR>
     * <BR>
     * If this is the property: ${app.path}/log
     *  then this method will lookup the value of the app.path property
     *  ( assume the value of this property is "/weblogic" )
     *  and substitute resulting in: /weblogic/log
     */
    private String transformPath(String theProp) {
        String beginTag = "${";
        String endTag = "}";
        int endIndex = -1;
        String retStr = theProp;

        int begIndex = theProp.indexOf(beginTag);
        if (begIndex > -1) {
            StringBuffer buff = new StringBuffer(theProp.length());
            int startPos = begIndex + beginTag.length();
            buff.append( theProp.substring(0,begIndex) );
            endIndex = theProp.indexOf(endTag,startPos);
            String substPropKey = theProp.substring(startPos,endIndex);
            String substPropVal = getProperty(substPropKey,null);
            if ( substPropVal == null ) 	{
            	throw new RuntimeException("Could not locate the substitution " + "value for property [" + substPropKey + "]...");
            }
            buff.append(substPropVal);
            buff.append( theProp.substring(endIndex + endTag.length()) );
            retStr = buff.toString();
        }
        return retStr;
    }


    /**
     * Reloads the property files.  Also updates the timer refresh interval
     * in case it has been updated.
     */
    public void reloadProps() {
        synchronized (this) {
            Set<?> keys = propMap.keySet();
            if (keys != null) {
                Iterator<?> iter = keys.iterator();
                while (iter.hasNext()) {
                    String filename = (String) iter.next();
                    Properties props = (Properties) propMap.get(filename);
                    loadProperties(filename,props);
                }
            }
        }
    }

    /**
     * Load a properties file for a filename
     */
    private void loadProperties(String theFileName, Properties theProps) {
        //load properties file
        //System.out.println("loading property file [" + theFileName + "]...");
        try {
            FileInputStream fis = new FileInputStream(theFileName);
            BufferedInputStream bis = new BufferedInputStream(fis);
            /*
             * Remove the old properties from memory first
             */
            theProps.clear();
            theProps.load(bis);
            fis.close();
            bis.close();
        }
        catch (FileNotFoundException fnfe) {
            System.out.println("The properties file:" + theFileName +" was not found!");
        }
        catch (IOException ioe) {
            System.out.println("An IOException occurred trying to load the " +"properties file: " + theFileName);
        }
    }
}

