package org.geoconme.gps.model;

import javax.microedition.location.Criteria;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.Orientation;

import org.geoconme.gps.ui.ProviderQueryUI;


/**
 * Model class that handles location providers search.
 */
public class ConfigurationProvider
{
    private static ConfigurationProvider INSTANCE = null;

    /** Array of free Criterias. */
    private static Criteria[] freeCriterias = null;

    /** String array of free criteria names. */
    private static String[] freeCriteriaNames = null;

    /** Array of Criterias that may cause costs */
    private static Criteria[] costCriterias = null;

    /** String array of non-free criteria names. */
    private static String[] costCriteriaNames = null;

    /** Reference to ProviderQueryUI viewer class. */
    private ProviderQueryUI queryUI = null;

    /** Selected criteria */
    private Criteria criteria = null;

    /** Selected location provider */
    private LocationProvider provider = null;

    static
    {
        // 1. Create pre-defined free criterias

        freeCriterias = new Criteria[2];
        freeCriteriaNames = new String[2];

        Criteria crit1 = new Criteria();
        crit1.setHorizontalAccuracy(25); // 25m
        crit1.setVerticalAccuracy(25); // 25m
        crit1.setPreferredResponseTime(Criteria.NO_REQUIREMENT);
        crit1.setPreferredPowerConsumption(Criteria.POWER_USAGE_HIGH);
        crit1.setCostAllowed(false);
        crit1.setSpeedAndCourseRequired(true);
        crit1.setAltitudeRequired(true);
        crit1.setAddressInfoRequired(true);

        freeCriterias[0] = crit1;
        freeCriteriaNames[0] = "High details, cost not allowed";

        Criteria crit2 = new Criteria();
        crit2.setHorizontalAccuracy(Criteria.NO_REQUIREMENT);
        crit2.setVerticalAccuracy(Criteria.NO_REQUIREMENT);
        crit2.setPreferredResponseTime(Criteria.NO_REQUIREMENT);
        crit2.setPreferredPowerConsumption(Criteria.POWER_USAGE_HIGH);
        crit2.setCostAllowed(false); // allowed to cost
        crit2.setSpeedAndCourseRequired(false);
        crit2.setAltitudeRequired(false);
        crit2.setAddressInfoRequired(false);

        freeCriterias[1] = crit2;
        freeCriteriaNames[1] = "Low details and power consumption, cost not allowed";

        // 2. Create pre-defined cost criterias

        costCriterias = new Criteria[3];
        costCriteriaNames = new String[3];

        Criteria crit3 = new Criteria();
        crit3.setHorizontalAccuracy(25); // 25m
        crit3.setVerticalAccuracy(25); // 25m
        crit3.setPreferredResponseTime(Criteria.NO_REQUIREMENT);
        crit3.setPreferredPowerConsumption(Criteria.NO_REQUIREMENT);
        crit3.setCostAllowed(true);
        crit3.setSpeedAndCourseRequired(true);
        crit3.setAltitudeRequired(true);
        crit3.setAddressInfoRequired(true);

        costCriterias[0] = crit3;
        costCriteriaNames[0] = "High details, cost allowed";

        Criteria crit4 = new Criteria();
        crit4.setHorizontalAccuracy(500); // 500m
        crit4.setVerticalAccuracy(Criteria.NO_REQUIREMENT);
        crit4.setPreferredResponseTime(Criteria.NO_REQUIREMENT);
        crit4.setPreferredPowerConsumption(Criteria.NO_REQUIREMENT);
        crit4.setCostAllowed(true);
        crit4.setSpeedAndCourseRequired(true);
        crit4.setAltitudeRequired(true);
        crit4.setAddressInfoRequired(false);

        costCriterias[1] = crit4;
        costCriteriaNames[1] = "Medium details, cost allowed";

        // Least restrictive criteria (with default values)
        Criteria crit5 = null;

        costCriterias[2] = crit5;
        costCriteriaNames[2] = "Least restrictive criteria";
    }

    /**
     * Private constructor to force using getInstance() method.
     */
    private ConfigurationProvider()
    {
        queryUI = new ProviderQueryUI();
        
    }

    /**
     * Provide singleton instance of this class.
     * 
     * @return static instance of this class.
     */
    public static ConfigurationProvider getInstance()
    {
        if (INSTANCE == null)
        {
            // Enable use of this class when Location API is supported.
            if (isLocationApiSupported())
            {
                INSTANCE = new ConfigurationProvider();
            }
            else
            {
                INSTANCE = null;
            }
        }

        return INSTANCE;
    }

    /**
     * Checks whether Location API is supported.
     * 
     * @return a boolean indicating is Location API supported.
     */
    public static boolean isLocationApiSupported()
    {
        String version = System.getProperty("microedition.location.version");
        return (version != null && !version.equals("")) ? true : false;
    }

    public LocationProvider getSelectedProvider()
    {
        return provider;
    }

    /**
     * Search location provider by using pre-defined free and cost criterias.
     * 
     * @param listener - 
     *          a event listener that listens ProviderStatusLisneter events.
     */
    public void autoSearch(ProviderStatusListener listener)
    {
    	
    	
    	
    	System.out.println("-2");
        try
        {
        	System.out.println("-1");
            for (int i = 0; i < freeCriterias.length; i++)
            {
            	System.out.println(i);
                criteria = freeCriterias[i];

                provider = LocationProvider.getInstance(criteria);
                if (provider != null)
                {
                	System.out.println("A");
                	// Location provider found, send a selection event.
                    listener.providerSelectedEvent();
                    
                    return;
                }else{
                	System.out.println("Provider is null");
                }
            }

            if (queryUI.confirmCostProvider())
            {
            	System.out.println("B");
            	
                for (int i = 0; i < costCriterias.length; i++)
                {
                    criteria = costCriterias[i];

                    provider = LocationProvider.getInstance(criteria);
                    if (provider != null)
                    {
                    	System.out.println("C");
                        // Location provider found, send a selection event.
                        listener.providerSelectedEvent();
                        return;
                    }
                }
            }
            else
            {
            	System.out.println("D");
                //queryUI.showNoFreeServiceFound();
            }
        }
        catch (LocationException le)
        {
        	System.out.println("E");
            //queryUI.showOutOfService();
        }
    }

    public Orientation getOrientation()
    {
        try
        {
            return Orientation.getOrientation();
        }
        catch (LocationException e)
        {
            return null;
        }
    }

    /**
     * Tells whether orientation is supported.
     * 
     * @return a boolean indicating is orientation supported.
     */
    public boolean isOrientationSupported()
    {
        try
        {
            // Test whether Orientation instance can be obtained.
            Orientation.getOrientation();
            return true;
        }
        catch (LocationException e)
        {
            return false;
        }
    }
}
