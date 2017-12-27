package baroEmap;

import backProc.ConnPG;
import backProc.CoordTransTo4326;
import backProc.CoordTransTo5179;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.geotools.data.DataStore;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.json.simple.JSONObject;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jaypark on 2017. 9. 11..
 */
public class PoiDao {

    // 경위도 좌표 및 검색 반경을 입력하여 특정 type의 POI 데이터 추출
    public JSONObject getPois(String location, int rad, String type, String keyword) {

        JSONObject obj = new JSONObject();
        List<String[]> typeList = null;
        List<Poi> poiList = null;

        List<String> lat_lng = Arrays.asList(location.split(","));

        // Postgresql 연결
        ConnPG connPG = new ConnPG();
        DataStore dataStore = connPG.getDataStore();

        // 좌표 변환 (디바이스에서 얻게 되는 경위도 정보는 WGS84 기반이므로 UTM-K 좌표로 변환 필요)
        CoordTransTo5179 coordTrans = new CoordTransTo5179(Double.valueOf(lat_lng.get(1)), Double.valueOf(lat_lng.get(0)));
        Point2D.Double epsg_5179 = coordTrans.to_5179();

        try {
            if (type != null) {

                // "TN_TYPE" 테이블 추출
                SimpleFeatureSource typeSource = dataStore.getFeatureSource("TN_TYPE");

                // Filter 및 Query 생성
                Filter typeFilter = CQL.toFilter("TYPE = '" + type + "'");
                Query typeQuery = new Query("TN_TYPE", typeFilter, new String[]{"ASORTCD", "DSORTCD"});

                // 작성한 Query에 부합하는 데이터 추출
                SimpleFeatureCollection typeCollection = typeSource.getFeatures(typeQuery);
                SimpleFeatureIterator tIterator = typeCollection.features();

                try {
                    typeList = new ArrayList<>();

                    while (tIterator.hasNext()) {
                        SimpleFeature feature = tIterator.next();
                        String asortcd = feature.getAttribute("ASORTCD").toString();
                        String dsortcd = new String();
                        if (feature.getAttribute("DSORTCD") != null)
                            dsortcd = feature.getAttribute("DSORTCD").toString();

                        if (dsortcd != null)
                            typeList.add(new String[] {asortcd, dsortcd});
                        else
                            typeList.add(new String[] {asortcd, ""});
                    }
                } finally {
                    tIterator.close();
                }
//                dataStore.dispose();
            }

            // "TN_NPOIBASS" 테이블 추출
            SimpleFeatureSource poiSource = dataStore.getFeatureSource("TN_NPOIBASS");

            // Filter 및 Query 생성 (type 존재 유무에 따라 다름)
            Filter filter;
            String filterStr = "";
            if (typeList.size() != 0) {
                for (int i = 0; i < typeList.size(); i++) {
                    String[] typeArray = typeList.get(i);
                    filterStr += "(ASORTCD LIKE '" + typeArray[0] + "%' AND DSORTCD LIKE '" + typeArray[1] + "%')";

                    if (i == typeList.size() - 1)
                        break;
                    else
                        filterStr += " OR ";

//                    filter = CQL.toFilter("DWITHIN(geom, POINT(" + epsg_5179.getX() + " " + epsg_5179.getY() + "), " + rad + ", meters) AND ASORTCD LIKE '" + typeArray[0] + "%' AND DSORTCD LIKE '" + typeArray[1] + "%'");
                }
                filter = CQL.toFilter("DWITHIN(geom, POINT(" + epsg_5179.getX() + " " + epsg_5179.getY() + "), " + rad + ", meters) AND UPDTCD NOT LIKE 'D' AND (" + filterStr + ") AND FMYNM LIKE '%" + keyword + "%'");
            } else {
                filter = CQL.toFilter("DWITHIN(geom, POINT(" + epsg_5179.getX() + " " + epsg_5179.getY() + "), " + rad + ", meters) AND UPDTCD NOT LIKE 'D' AND FMYNM LIKE '%" + keyword + "%'");
            }
            Query query = new Query("TN_NPOIBASS", filter, new String[]{"POIID", "FMYNM", "XCOORD", "YCOORD"});

            // 작성한 Query에 부합하는 데이터 추출
            SimpleFeatureCollection poiCollection = poiSource.getFeatures(query);
            SimpleFeatureIterator iterator = poiCollection.features();

            try {
                poiList = new ArrayList<>();

                while (iterator.hasNext()) {
                    SimpleFeature feature = iterator.next();

                    // 좌표 변환
                    CoordTransTo4326 convert = new CoordTransTo4326(Double.valueOf(feature.getAttribute("XCOORD").toString()), Double.valueOf(feature.getAttribute("YCOORD").toString()));
                    Point2D.Double epsg_4326 = convert.to_4326();

                    poiList.add(new Poi(feature.getAttribute("POIID").toString(), feature.getAttribute("FMYNM").toString(), epsg_4326.getY(), epsg_4326.getX()));
                }
            } finally {
                iterator.close();
            }
            dataStore.dispose();
        } catch (CQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        obj.put("pois", poiList);
        return obj;

    }

}
