package com.masstudio.selmy.tmc.Utils;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tech lap on 14/03/2017.
 */

public class Constants {
    private DatabaseReference databaseReference;
    public static final String API_KEY_DIRECTION_MATRIX = "AIzaSyCnaNTeOVLNVt8fH6MVK65E2ikX1CUeF6A";
    public static final String API_KEY_DIRECTIONS = "AIzaSyD4tWwgtDUjsuhYh84wdBmsUfeFzeuqjY0";

    public static final String SPONSOR_KEY="sponsor";
    public static final String CREW_KEY="crew";
    public static final String EVENT_KEY="event";
    public static final String SLIDER_PICS_KEY="SliderPics";

    public static final String INTERSECTION_2_SIGNAL_1 = "2.1";
    public static final String INTERSECTION_2_SIGNAL_2 = "2.2";
    public static final String INTERSECTION_2_SIGNAL_3 = "2.3";

    public static final String INTERSECTION_6_SIGNAL_1 = "6.1";
    public static final String INTERSECTION_6_SIGNAL_2 = "6.2";
    public static final String INTERSECTION_6_SIGNAL_3 = "6.3";

    public static final String INTERSECTION_4_SIGNAL_1 = "4.1";
    public static final String INTERSECTION_4_SIGNAL_2 = "4.2";
    public static final String INTERSECTION_4_SIGNAL_3 = "4.3";
    public static final String INTERSECTION_4_SIGNAL_4 = "4.4";

    public static final String INTERSECTION_3_SIGNAL_1 = "3.1";
    public static final String INTERSECTION_3_SIGNAL_2 = "3.2";
    public static final String INTERSECTION_3_SIGNAL_3 = "3.3";

    public static final String INTERSECTION_7_SIGNAL_1 = "7.1";
    public static final String INTERSECTION_7_SIGNAL_2 = "7.2";
    public static final String INTERSECTION_7_SIGNAL_3 = "7.3";

    public static final String INTERSECTION_8_SIGNAL_1 = "8.1";
    public static final String INTERSECTION_8_SIGNAL_2 = "8.2";
    public static final String INTERSECTION_8_SIGNAL_3 = "8.3";
    public static final String INTERSECTION_8_SIGNAL_4 = "8.4";

    public static final String INTERSECTION_9_SIGNAL_1 = "9.1";
    public static final String INTERSECTION_9_SIGNAL_2 = "9.2";
    public static final String INTERSECTION_9_SIGNAL_3 = "9.3";

    public static final String INTERSECTION_10_SIGNAL_1 = "10.1";
    public static final String INTERSECTION_10_SIGNAL_2 = "10.2";

    public static  String SEGMENTS_PATHES_ARRAY[] = {"_xmvDw{o~DrAfVfArSlA`VE?","mpmvDqvm~DrFxeA",
            "uhmvDsnk~Dx@jPbBnZvAvWE@","y`mvD}fi~DVhDZlJNrCf@vIh@lL",
            "mzlvDcyg~Dm@_Ma@wHYoDaAyQ","y_mvDeii~DyC{h@mAyVMiC",
            "sgmvDspk~Dk@{KcCcc@mAgU","womvDqym~DgAoUy@mReAqNWkG"
    };

    public static  String SEGMENTS_NAMES_ARRAY [] = {"Mahdy Arafa - Africa Emtedad Mostafa El Nahaas",
            "Abou Daowud Al Zaheri - Africa Emtedad Mostafa El Nahaas",
            "Hassan Ma'moon - Africa Emtedad Mostafa El Nahaas",
            "Makkram Ebeid - Mostafa El Nahaas",
            "Mostafa El Nahaas- Abbas El akkad",
            "Mostafa El Nahaas-Makkram Ebeid",
            "Africa Emtedad Mostafa El Nahaas - Hassan Ma'moon",
            "Africa Emtedad Mostafa El Nahaas - Abou Daowud Al Zaheri"};
    public static  String SEGMENTS_REROTE_MSG_ARRAY [] ={
            "This route will take too long due to second row parking, you might want to consider rerouting",
            "This route will take too long due to congestion at the U turn, you might want to consider rerouting",
            "This route will take too long due to second row parking, you might want to consider rerouting",
            "This route will take too long due to congestion at the U turn, you might want to consider rerouting",
            "This route will take too long due to second row parking, you might want to consider rerouting",
            "This route will take too long due to second row parking, you might want to consider rerouting",
            "null",
            "null"
    };
    public static List<List<LatLng>> decodeSegments(){
        List<List<LatLng>> list = new ArrayList<>();
        for (String code : SEGMENTS_PATHES_ARRAY)
            list.add(PolyUtil.decode(code));
        return list;
    }
    public static List<LatLng> decodeSegment(String code){
        List<LatLng> list = PolyUtil.decode(code);
        return list;
    }
    public static LatLng getStart(String code){
        return decodeSegment(code).get(0);
    }
    public static LatLng getEnd(String code){
        List<LatLng> list = PolyUtil.decode(code);
        return list.get(list.size() - 1);
    }

    public static int onSegments(LatLng latLng){
        // isLocationOnPath(LatLng point, java.util.List<LatLng> polyline, boolean geodesic, double tolerance)
        int i = 0;
        List<List<LatLng>> list = decodeSegments();
        for (List<LatLng> segment : list){
            Boolean found = PolyUtil.isLocationOnPath(latLng,segment,false,0); // segments : true > circles
            if (found)
                return i;
            i++;
        }
        return -1;
    }


    public static final String AFRICA_MAKRAM [] = {
            "30.058380, 31.379770",
            "30.058347, 31.379523",
            "30.058310, 31.379158",
            "30.058285, 31.378871",
            "30.058238, 31.378447",
            "30.058169, 31.377846",
            "30.058124, 31.37752",
            "30.058097, 31.377197",
            "30.058039, 31.376765",
            "30.057976, 31.376237",
            "30.057933, 31.375775",
            "30.057852, 31.375142",
            "30.057814, 31.374578",
            "30.057754, 31.374050",
            "30.057696, 31.373551",
            "30.057626, 31.373049",
            "30.057503, 31.371775",
            "30.057438, 31.371099",
            "30.057382, 31.370656",
            "30.057306, 31.369940",
            "30.057220, 31.369137",
            "30.057113, 31.368168",
            "30.057017, 31.367275",
            "30.056938, 31.366540",
            "30.056865, 31.365923",
            "30.056803, 31.365282",
            "30.056734, 31.364694",
            "30.056734, 31.364694",
            "30.056554, 31.363067",
            "30.056476, 31.362231",
            "30.056392, 31.361434",
            "30.056291, 31.360521",
            "30.056198, 31.359681",
            "30.056088, 31.358598",
            "30.056002, 31.357856",
            "30.055902, 31.356907",
            "30.055839, 31.356201",
            "30.055785, 31.355720",
            "30.055694, 31.354840",
            "30.055595, 31.354065",
            "30.055521, 31.353394",
            "30.055521, 31.353394",
            "30.055299, 31.351435",
            "30.055127, 31.349874",
            "30.055023, 31.348873",
            "30.054887, 31.347709",
            "30.054603, 31.345307",
            "30.054222, 31.341772",
            "30.054038, 31.339284",
            "30.053894, 31.337281",
            "30.053592, 31.335946",
    };

    public static final String TAGMOOA_ARRAY [] = {
            "30.037886,31.439338",
            "30.037935,31.438875",
            "30.038291,31.438680",
            "30.038648,31.438974",
            "30.038584,31.439473",
            "30.038166,31.439623",
    };

    static public List<PointMap> getAfricaMakram(){
        List<PointMap> ptsAfricaMakram = new ArrayList<>();
        for (String point : Constants.TAGMOOA_ARRAY){
            point = "*,*,"+ point+",85,41,Green,44,Yellow,80,Red,85,Green";
            PointMap pointMap = toLatling(point);
            ptsAfricaMakram.add(pointMap);
        }
        return ptsAfricaMakram;
    }
    public static PointMap toLatling(String point){
        String string = point;
        String[] parts = string.split(",");
        String name = parts[0];
        String lat = parts[2]; // 004
        String lon = parts[3]; // 034556
        Log.d("MATRIX_S**","" +parts.length);
        PointMap pointMap = new PointMap();
        pointMap.name=name;
        pointMap.latLng=new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));
        pointMap.intersection = parts[1];
        pointMap.periods = new int[]{Integer.parseInt(parts[5]),Integer.parseInt(parts[7]),Integer.parseInt(parts[9]),Integer.parseInt(parts[11])};
        pointMap.status = new String[]{parts[6],parts[8],parts[10],parts[12]};
        pointMap.total_period = Integer.parseInt(parts[4]);
        Log.d("Points**",pointMap.latLng.toString());
        return pointMap;
    }
    public static List<PointMap> setSignals(){
        List<String> signalsMain = new ArrayList<>();
        List<PointMap> ptsMain;
        ptsMain = new ArrayList<>();

        signalsMain.add(Constants.INTERSECTION_2_SIGNAL_2 + ",2,30.057729,31.376029,85,41,Green,44,Yellow,80,Red,85,Green");
        signalsMain.add(Constants.INTERSECTION_2_SIGNAL_1 + ",2,30.057989,31.376291,79,20,Red,60,Green,63,Yellow,79,Red");
        signalsMain.add(Constants.INTERSECTION_2_SIGNAL_3 + ",2,30.057699,31.376062,85,35,Red,66,Green,69,Yellow,85,Red");

        signalsMain.add(Constants.INTERSECTION_6_SIGNAL_2 + ",6,30.055749,31.357249,120,1,Green,4,Yellow,45,Red,120,Green");
        signalsMain.add(Constants.INTERSECTION_6_SIGNAL_1 + ",6,30.055967,31.357528,120,1,Green,4,Yellow,45,Red,120,Green");
        signalsMain.add(Constants.INTERSECTION_6_SIGNAL_3 + ",6,30.055966,31.357304,120,10,Red,46,Green,49,Yellow,120,Red");

        signalsMain.add(Constants.INTERSECTION_4_SIGNAL_1 + ",4,30.057238,31.369283,-85,2,Yellow,40,Red,84,Green,85,Yellow");
        signalsMain.add(Constants.INTERSECTION_4_SIGNAL_2 + ",4,30.057252,31.369147,-85,2,Yellow,40,Red,84,Green,85,Yellow");
        signalsMain.add(Constants.INTERSECTION_4_SIGNAL_3 + ",4,30.057051,31.369143,-85,2,Yellow,40,Red,84,Green,85,Yellow");
        signalsMain.add(Constants.INTERSECTION_4_SIGNAL_4 + ",4,30.057038,31.369286,-85,2,Yellow,40,Red,84,Green,85,Yellow");

        signalsMain.add(Constants.INTERSECTION_3_SIGNAL_1 + ",3,30.057553,31.374258,85,2,Yellow,40,Red,84,Green,85,Yellow");
        signalsMain.add(Constants.INTERSECTION_3_SIGNAL_2 + ",3,30.057809,31.374673,85,39,Green,42,Yellow,80,Red,85,Green");
        signalsMain.add(Constants.INTERSECTION_3_SIGNAL_3 + ",3,30.057874,31.374639,85,37,Red,40,Green,74,Yellow,85,Red");

        signalsMain.add(Constants.INTERSECTION_7_SIGNAL_1 + ",7,30.054731,31.346274,-85,2,Yellow,40,Red,84,Green,85,Yellow");
        signalsMain.add(Constants.INTERSECTION_7_SIGNAL_2 + ",7,30.054492,31.346041,-85,39,Green,42,Yellow,80,Red,85,Green");
        signalsMain.add(Constants.INTERSECTION_7_SIGNAL_3 + ",7,30.054496,31.346276,-85,37,Red,40,Green,74,Yellow,85,Red");
        //
        signalsMain.add(Constants.INTERSECTION_8_SIGNAL_1 + ",8,30.053935,31.338584,-85,2,Yellow,40,Red,84,Green,85,Yellow");
        signalsMain.add(Constants.INTERSECTION_8_SIGNAL_2 + ",8,30.053937,31.338342,-85,2,Yellow,40,Red,84,Green,85,Yellow");
        signalsMain.add(Constants.INTERSECTION_8_SIGNAL_3 + ",8,30.053643,31.338359,-85,2,Yellow,40,Red,84,Green,85,Yellow");
        signalsMain.add(Constants.INTERSECTION_8_SIGNAL_4 + ",8,30.053644,31.338579,-85,2,Yellow,40,Red,84,Green,85,Yellow");
        //
        signalsMain.add(Constants.INTERSECTION_9_SIGNAL_1 + ",9,30.037977,31.439558,68,30,Green,30,Yellow,65,Red,68,Green");
        signalsMain.add(Constants.INTERSECTION_9_SIGNAL_2 + ",9,30.038706,31.439290,68,6,Red,39,Green,39,Yellow,68,Red");
        signalsMain.add(Constants.INTERSECTION_9_SIGNAL_3 + ",9,30.038088,31.438630,68,10,Green,10,Yellow,45,Red,68,Green");
        //
        signalsMain.add(Constants.INTERSECTION_10_SIGNAL_1 + ",10,30.035931,31.475037,93,27,Green,27,Yellow,67,Red,93,Green");
        signalsMain.add(Constants.INTERSECTION_10_SIGNAL_2 + ",10,30.037018,31.474565,94,28,Green,28,Yellow,69,Red,94,Green");

        for ( String string:signalsMain) {
            PointMap pointMap = Constants.toLatling(string);
            ptsMain.add(pointMap);
        }
      return  ptsMain;
    }
    public static int findNearestPoint(PointMap org,List<PointMap> points){
        float distance = distanceTo(org.latLng,points.get(0).latLng);
        int min =0;
        for (int j=0;j<points.size();j++){
            if (distance > distanceTo(points.get(j).latLng,org.latLng)){
                min = j;
                distance = distanceTo(points.get(j).latLng,org.latLng);
            }
        }
        return min;
    }
    public static float distanceTo(LatLng latLng1,LatLng latLng2){
        Location location = new Location("P0");
        location.setLatitude(latLng1.latitude);
        location.setLongitude(latLng1.longitude);
        Location location1 = new Location("P1");
        location1.setLatitude(latLng2.latitude);
        location1.setLongitude(latLng2.longitude);
        return location1.distanceTo(location);
    }


}
