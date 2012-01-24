// Copyright 2012 MIT All rights reserved

package com.google.appinventor.components.runtime;

import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.DesignerProperty;
import com.google.appinventor.components.annotations.PropertyCategory;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.annotations.UsesPermissions;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.common.YaVersion;
import com.google.appinventor.components.runtime.util.ErrorMessages;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.List;

// This file is a dummy that contains the  (irrelevant)
// Location sensor code as a guide to what needs to go in for a component.
// All the specific location sensor stuff
// should be removed, and the NFC control code added.

/**
 * Controller for Near Field Communication
 *
 */
@DesignerComponent(version = YaVersion.NEARFIELD_COMPONENT_VERSION,
    description = "<p>Non-visible component to provide NFC capabilities." +
		   "INSERT DOCUMENTATION HERE</p>",
		   // TODO: Change category to SENSORS some day.  But we need to
		   // think about what to do with phones that do not provide NFC.
    category = ComponentCategory.INTERNAL,
    nonVisible = true,
    
    // the image nerField.png here is a dummy:  a copy of the image for location sensor.
    // you should find the image you want and replace the file. 
    
    iconName = "images/nearField.png")
@SimpleObject
@UsesPermissions(permissionNames =
		 // change these to the right permissions
                 "android.permission.ACCESS_FINE_LOCATION," +
                 "android.permission.ACCESS_COARSE_LOCATION," +
                 "android.permission.ACCESS_MOCK_LOCATION," +
                 "android.permission.ACCESS_LOCATION_EXTRA_COMMANDS")

public final class NearField extends AndroidNonvisibleComponent
    implements Component, OnStopListener, OnResumeListener, Deleteable {

 
  // **********************************
  // everything between here and the next line of stars can
  // be deleted,one you figure out what methods and events and
  // properties you want.
  
  // this class definition is junk.  It's here only to let things compile for now.
  // when you edit this, you'll get rid of the variable myLocationListener below
  // and get rid of this class definition
  private class MyLocationListener implements LocationListener {

    @Override
    public void onLocationChanged(Location arg0) {
      // TODO Auto-generated method stub

    }
    @Override
    public void onProviderDisabled(String arg0) {
      // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String arg0) {
      // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
      // TODO Auto-generated method stub

    }

  }
    
  
  /**
   * Constant returned by {@link #Longitude()}, {@link #Latitude()}, and
   * {@link #Altitude()} if no value could be obtained for them.  The client
   * can find this out directly by calling {@link #HasLongitudeLatitude()} or
   * {@link #HasAltitude()}.
   */
  public static final int UNKNOWN_VALUE = 0;

  /**
   * Minimum time in milliseconds between location checks. The documentation for
   * {@link android.location.LocationManager#requestLocationUpdates}
   * does not recommend using a location lower than 60,000 (60 seconds) because
   * of power consumption.
   */
  public static final long MIN_TIME_INTERVAL = 60000;

  /**
   * Minimum distance in meters to be reported
   */
  public static final long MIN_DISTANCE_INTERVAL = 5;  // 5 meters

  // These variables contain information related to the LocationProvider.
  private final Criteria locationCriteria = new Criteria();

  private final Handler handler;

  private boolean providerLocked = false; // if true we can't change providerName
  private String providerName;
    // Invariant: providerLocked => providerName is non-empty

  private MyLocationListener myLocationListener = new MyLocationListener();

  private LocationProvider locationProvider;
  private boolean listening = false;
    // Invariant: listening <=> a myLocationListener is registered with locationManager
    // Invariant: !listening <=> locationProvider == null

  //This holds all the providers available when we last chose providerName.
  //The reported best provider is first, possibly duplicated.
  private List<String> allProviders;

  // These location-related values are set in MyLocationListener.onLocationChanged().
  private Location lastLocation;
  private double longitude = UNKNOWN_VALUE;
  private double latitude = UNKNOWN_VALUE;
  private double altitude = UNKNOWN_VALUE;
  private boolean hasLocationData = false;
  private boolean hasAltitude = false;

  // This is used in reverse geocoding.
  private Geocoder geocoder;

  // User-settable properties
  private boolean enabled = true;  // the default value is true

 
  
  // ****************************
  // start looking from here down.   You'll create new @SimpleFunctions and
  // events and properties for the NFC, and delete these location sensor ones.
  // You'll need some variables and constants, the you'll define above, and delete t
  
  
  /**
   * Creates a new NearField component.
   *
   * @param container  ignored (because this is a non-visible component)
   */
  public NearField(ComponentContainer container) {
    super(container.$form());
    handler = new Handler();
    // Set up listener
    form.registerForOnResume(this);
    form.registerForOnStop(this);

    // Initialize location-related fields
    Context context = container.$context();
    
   // here's where you put the stuff for initializing the
    // nfc on startup


  }

  // Events

  // Here are examples of how to define events.  You'll delete these and
  // replace them with the NSF events.
  
  /**
   * Indicates that a new location has been detected.
   */
  @SimpleEvent
  public void LocationChanged(double latitude, double longitude, double altitude) {
    if (enabled) {
      EventDispatcher.dispatchEvent(this, "LocationChanged", latitude, longitude, altitude);
    }
  }

  /**
   * Indicates that the status of the provider has changed.
   */
  @SimpleEvent
  public void StatusChanged(String provider, String status) {
    if (enabled) {
      EventDispatcher.dispatchEvent(this, "StatusChanged", provider, status);
    }
  }

  // Properties

  //Here is how you define properties.  What properties should the NFC have?
  
  /**
   * Indicates the source of the location information.  If there is no provider, the
   * string "NO PROVIDER" is returned.  This is useful primarily for debugging.
   */
  @SimpleProperty(category = PropertyCategory.BEHAVIOR)
  public String ProviderName() {
    if (providerName == null) {
      return "NO PROVIDER";
    } else {
      return providerName;
    }
  }

 

  @SimpleProperty(category = PropertyCategory.BEHAVIOR)
  public boolean ProviderLocked() {
    return providerLocked;
  }

  /**
   * Indicates whether the sensor should listen for location changes
   * and raise the corresponding events.
   */
  @DesignerProperty(editorType = DesignerProperty.PROPERTY_TYPE_BOOLEAN,
      defaultValue = "False")
  @SimpleProperty
  public void ProviderLocked(boolean lock) {
      providerLocked = lock;
  }

  /**
   * Indicates whether longitude and latitude information is available.  (It is
   * always the case that either both or neither are.)
   */
  @SimpleProperty(category = PropertyCategory.BEHAVIOR)
  public boolean HasLongitudeLatitude() {
    return hasLocationData && enabled;
  }

  /**
   * Indicates whether altitude information is available.
   */
  @SimpleProperty(category = PropertyCategory.BEHAVIOR)
  public boolean HasAltitude() {
    return hasAltitude && enabled;
  }

  /**
   * Indicates whether information about location accuracy is available.
   */
  @SimpleProperty(category = PropertyCategory.BEHAVIOR)
  public boolean HasAccuracy() {
    return Accuracy() != UNKNOWN_VALUE && enabled;
  }

  /**
   * The most recent available longitude value.  If no value is available,
   * 0 will be returned.
   */
  @SimpleProperty(category = PropertyCategory.BEHAVIOR)
  public double Longitude() {
    return longitude;
  }

  /**
   * The most recently available latitude value.  If no value is available,
   * 0 will be returned.
   */
  @SimpleProperty(category = PropertyCategory.BEHAVIOR)
  public double Latitude() {
      return latitude;
  }

  /**
   * The most recently available altitude value, in meters.  If no value is
   * available, 0 will be returned.
   */
  @SimpleProperty(category = PropertyCategory.BEHAVIOR)
  public double Altitude() {
    return altitude;
  }

  /**
   * The most recent measure of accuracy, in meters.  If no value is available,
   * 0 will be returned.
   */
  @SimpleProperty(category = PropertyCategory.BEHAVIOR)
  public double Accuracy() {
    if (lastLocation != null && lastLocation.hasAccuracy()) {
      return lastLocation.getAccuracy();
    } else if (locationProvider != null) {
      return locationProvider.getAccuracy();
    } else {
      return UNKNOWN_VALUE;
    }
  }

  /**
   * Indicates whether the user has specified that the sensor should
   * listen for location changes and raise the corresponding events.
   */
  @SimpleProperty(category = PropertyCategory.BEHAVIOR)
  public boolean Enabled() {
    return enabled;
  }

  /**
   * Indicates whether the sensor should listen for location chagnes
   * and raise the corresponding events.
   */
  @DesignerProperty(editorType = DesignerProperty.PROPERTY_TYPE_BOOLEAN,
      defaultValue = "True")
  @SimpleProperty
  public void Enabled(boolean enabled) {
    this.enabled = enabled;
    if (!enabled) {
      stopListening();
    } else {
      // ???
    }
  }

  /**
   * Provides a textual representation of the current address or
   * "No address available".
   */
  @SimpleProperty(category = PropertyCategory.BEHAVIOR)
  public String CurrentAddress() {
    if (hasLocationData &&
        latitude <= 90 && latitude >= -90 &&
        longitude <= 180 || longitude >= -180) {
      try {
        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
        if (addresses != null && addresses.size() == 1) {
          Address address = addresses.get(0);
          if (address != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
              sb.append(address.getAddressLine(i));
              sb.append("\n");
            }
            return sb.toString();
          }
        }
      } catch (IOException e) {
        Log.e("LocationSensor",
                           "Exception thrown by getFromLocation() " + e.getMessage());
      }
    }
    return "No address available";
  }

  
  // here are examples of methods.  What should the NFC methods be?
  //  When you define these functions the blocks will be created automatically.
  
  /**
   * Derives Latitude from Address
   *
   * @param locationName  human-readable address
   *
   * @return latitude in degrees, 0 if not found.
   */
  @SimpleFunction(description = "Derives latitude of given address")
  public double LatitudeFromAddress(String locationName) {
    try {
      List<Address> addressObjs = geocoder.getFromLocationName(locationName, 1);
      if (addressObjs == null) {
        throw new IOException("");
      }
      return addressObjs.get(0).getLatitude();
    } catch (IOException e) {
      form.dispatchErrorOccurredEvent(this, "LatitudeFromAddress",
          ErrorMessages.ERROR_LOCATION_SENSOR_LATITUDE_NOT_FOUND, locationName);
      return 0;
    }
  }

  /**
   * Derives Longitude from Address
   * @param locationName  human-readable address
   *
   * @return longitude in degrees, 0 if not found.
   */
  @SimpleFunction(description = "Derives longitude of given address")
  public double LongitudeFromAddress(String locationName) {
    try {
      List<Address> addressObjs = geocoder.getFromLocationName(locationName, 1);
      if (addressObjs == null) {
        throw new IOException("");
      }
      return addressObjs.get(0).getLongitude();
    } catch (IOException e) {
      form.dispatchErrorOccurredEvent(this, "LongitudeFromAddress",
          ErrorMessages.ERROR_LOCATION_SENSOR_LONGITUDE_NOT_FOUND, locationName);
      return 0;
    }
  }

  @SimpleProperty(category = PropertyCategory.BEHAVIOR)
  public List<String> AvailableProviders () {
    return allProviders;
  }



  /* Start listening to ProviderName.
   * Return true iff successful.
   */
  private boolean startProvider(String providerName) {
    this.providerName = providerName;
    if (locationProvider == null) {
      Log.d("LocationSensor", "getProvider(" + providerName + ") returned null");
      return false;
    }
    stopListening();
    listening = true;
    return true;
  }

  /**
   * This unregisters {@link #myLocationListener} as a listener to location
   * updates.  It is safe to call this even if no listener had been registered,
   * in which case it has no effect.  This also sets the value of
   * {@link #locationProvider} to {@code null} and sets {@link #listening}
   * to {@code false}.
   */
  private void stopListening() {
    if (listening) {
      locationProvider = null;
      listening = false;
    }
  }


  // OnResumeListener implementation

  @Override
  public void onResume() {
    if (enabled) {
      // what to do when the app resumes
    }
  }

  // OnStopListener implementation

  @Override
  public void onStop() {
    stopListening();
  }

  // Deleteable implementation

  @Override
  public void onDelete() {
    stopListening();
  }

}
