android-navigation_lib
======================
Easily integrate navigation into your Android application with this low-dependency library. Use any mapping library or routing service by implementing a few simple interfaces. To get started quickly, you can use the Google Directions API and Google Maps Android API v2, for which demo code has been provided. GMS is currently a dependency for our library (the only one!) - we use it for location handling - so unless you want to customize maps further, using the Google Maps Android API is the best option.

Getting started
---------------
TODO: On setting up GMS dependency, cloning repo.


Usage
-----
It is easy to get access to the Navigator object, simply create a NavigationFragment and call getNavigator(). Note that NavigationFragment.getNavigator() and operations on the Navigator itself can be called anytime in the fragment lifecycle, but navigation will only start once the view has been created and GPS signal found.
```java
NavigationFragment myNavigationFragment = NavigationFragment.newInstance(
  directionsFactory \** Implement IDirectionsFactory **\,
  mapFactory \** Implement IMapFactory **\,
  vehicleMarkerFactory \** Implement IVehicleMarkerFactory **\);
FragmentTransaction ft = getFragmentManager().beginTransaction();
ft.add(R.id.demo_nav_fragment_container, navigationFragment);
ft.commit();

Navigator navigator = myNavigationFragment.getNavigator()
navigator.go(new LatLng(-43.529333, 172.587279)); // Start navigating to this location!
```



### NavigationOptions
NavigationOptions provide easy customization of the NavigationFragment's behavior. NavigationOptions encapsulates groups of sub-options e.g. VehicleOptions, MapOptions, GpsOptions. Setting options on an options object can be chained. If a single option is not set, it will revert to its default value.
