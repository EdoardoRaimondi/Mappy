# Mappy


Mappy is an Android application based on the so famous Google Maps and Google Places APIs. Designed to search interesting places nearby your device using Google Maps backend (GPS is required for high accuracy). Research radius is adjustable according to your taste. The most awsome place is a click beside you.

  - Find in the list the place you want to search
  - Save your home in case you forgot it
  - Save your favourite places
  - Get local help phone numbers in an emergency

### Tech

Mappy uses:

* [Android](https://en.wikipedia.org/wiki/Android_(operating_system)) - as OS on your device
* [Java](https://www.java.com/en/) - the programming language we used in Android Studio
* [Google Places API](https://cloud.google.com/maps-platform/places?hl=it) backend
* [Google Maps API](https://cloud.google.com/maps-platform) backend


### Test

Mappy APP was tested on:
- Physical devices: LG-G4 (LGE LG-H815 Android 6.0), rockchip tablet (rk312x Android 4.4.4)
- Emulators: Pixel 2 (Android R), Nexus 5 (Android R)


### Support

Since our APP uses Google backends, limitations comes in especially on the number of Place search queries. If you often incur in the message that notifies a lot of requests, please modify the following line containing the Google API key in ```app/src/release/res/values/google_maps_api.xml``` file
```xml
<string 
        name="google_maps_key" 
        templateMergeStrategy="preserve"
        translatable="false"
        >
        AIzaSyDO5W0b3EqbJgPm0yg3AOpflgxhc8hsTPM
</string>
```
and in ```app/src/debug/res/values/google_maps_api.xml``` file
```xml
<string 
        name="google_maps_key" 
        templateMergeStrategy="preserve"
        translatable="false"
        >
        AIzaSyDO5W0b3EqbJgPm0yg3AOpflgxhc8hsTPM
</string>
```
with one of the following key
```xml
        AIzaSyDlmOO2hQUKeM6jghktDSVvsOsXvoFLv3k
```
```xml
        AIzaSyCIN8HCmGWXf5lzta5Rv2nu8VdIUV4Jp7s
```
NOTE: keys in the two file should be the same
