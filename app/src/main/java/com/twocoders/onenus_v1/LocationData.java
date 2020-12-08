//package com.twocoders.onenus_v1;
//
//import android.app.Activity;
//import android.app.Application;
//
//import java.util.HashMap;
//import com.google.android.gms.maps.model.LatLng;
//
//public class LocationData extends Application {
//    HashMap<String, LatLng>  administrativeOfficeHashMap, banksATMSHashMap,
//                                    busStopsHashMap, carParksHashMap,
//                                    convenienceStoresSupermarketsHashMap, culturalFacilitiesHashMap,
//                                    facultiesSchoolsHashMap, foodCourtsHashMap,
//                                    healthCareAmenitiesHashMap, lectureTheatresHashMap,
//                                    nusLibrariesStudyAreasHashMap, printingServicesHashMap,
//                                    residentialAreasHashMap, restaurantsCafesHashMap,
//                                    sportsFacilitiesHashMap;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        administrativeOfficeHashMap = new HashMap<>();
//                banksATMSHashMap = new HashMap<>();
//                busStopsHashMap = new HashMap<>();
//                carParksHashMap = new HashMap<>();
//                convenienceStoresSupermarketsHashMap = new HashMap<>();
//                culturalFacilitiesHashMap = new HashMap<>();
//                facultiesSchoolsHashMap = new HashMap<>();
//                foodCourtsHashMap = new HashMap<>();
//                healthCareAmenitiesHashMap = new HashMap<>();
//                lectureTheatresHashMap = new HashMap<>();
//                nusLibrariesStudyAreasHashMap = new HashMap<>();
//                printingServicesHashMap = new HashMap<>();
//                residentialAreasHashMap = new HashMap<>();
//                restaurantsCafesHashMap = new HashMap<>();
//                sportsFacilitiesHashMap = new HashMap<>();
//    }
//
//    public static LocationData getLocationData(Activity activity) {
//        return (LocationData) activity.getApplication();
//    }
//
//    public void iniHashMap () {
//
//        if(administrativeOfficeHashMap != null && administrativeOfficeHashMap.isEmpty()) {
//            // Administrative Offices
//
//            // Banks/ ATMs (array > hashmap)
//            banksATMSHashMap.put("OCBC Branch @ YIH", new LatLng(1.298620, 103.774986));
//            banksATMSHashMap.put("DBS ATM", new LatLng(1.294439, 103.774055));
//
//            // Bus Stops (only for Map filter)
//
//            // Car Parks
//            carParksHashMap.put("NUS Car Park 11", new LatLng(1.294402, 103.775136));
//            carParksHashMap.put("NUS Car Park 2A", new LatLng(1.300771, 103.771666));
//
//            // Convenience Stores/ Supermarkets (array > hashmap)
//            convenienceStoresSupermarketsHashMap.put("UTown Fairprice Express", new LatLng(1.304863, 103.772552));
//            convenienceStoresSupermarketsHashMap.put("7-11 @ YiH", new LatLng(1.298834, 103.775102));
//
//            // Cultural Facilities
//
//            // Faculties/ Schools
//
//            // Food Courts
//            foodCourtsHashMap.put("Deck", new LatLng(1.297186, 103.777529));
//
//            // Health Care Amenities
//
//            // Lecture Theatres
//
//            // NUS Libraries/ Study Areas
//
//            // Printing Services
//
//            // Residential Areas
//
//            // Restaurants/ Cafes
//            restaurantsCafesHashMap.put("Swee Heng", new LatLng(1.304774, 103.772660));
//
//            // Sports Facilities
//
//        }
//    }
//
//    public HashMap<String, LatLng> getAdministrativeOfficeHashMap() {
//        return administrativeOfficeHashMap;
//    }
//
//    public HashMap<String, LatLng> getBanksATMSHashMap() {
//        return banksATMSHashMap;
//    }
//
//    public HashMap<String, LatLng> getBusStopsHashMap() {
//        return busStopsHashMap;
//    }
//
//    public HashMap<String, LatLng> getCarParksHashMap() {
//        return carParksHashMap;
//    }
//
//    public HashMap<String, LatLng> getConvenienceStoresSupermarketsHashMap() {
//        return convenienceStoresSupermarketsHashMap;
//    }
//
//    public HashMap<String, LatLng> getCulturalFacilitiesHashMap() {
//        return culturalFacilitiesHashMap;
//    }
//
//    public HashMap<String, LatLng> getFacultiesSchoolsHashMap() {
//        return facultiesSchoolsHashMap;
//    }
//
//    public HashMap<String, LatLng> getFoodCourtsHashMap() {
//        return foodCourtsHashMap;
//    }
//
//    public HashMap<String, LatLng> getHealthCareAmenitiesHashMap() {
//        return healthCareAmenitiesHashMap;
//    }
//
//    public HashMap<String, LatLng> getLectureTheatresHashMap() {
//        return lectureTheatresHashMap;
//    }
//
//    public HashMap<String, LatLng> getNusLibrariesStudyAreasHashMap() {
//        return nusLibrariesStudyAreasHashMap;
//    }
//
//    public HashMap<String, LatLng> getPrintingServicesHashMap() {
//        return printingServicesHashMap;
//    }
//
//    public HashMap<String, LatLng> getResidentialAreasHashMap() {
//        return residentialAreasHashMap;
//    }
//
//    public HashMap<String, LatLng> getRestaurantsCafesHashMap() {
//        return restaurantsCafesHashMap;
//    }
//
//    public HashMap<String, LatLng> getSportsFacilitiesHashMap() {
//        return sportsFacilitiesHashMap;
//    }
//}
