package backProc;

import com.jhlabs.map.proj.Projection;
import com.jhlabs.map.proj.ProjectionFactory;

import java.awt.geom.Point2D;

/**
 * Created by jaypark on 2017. 9. 11..
 */
public class CoordTransTo4326 {

    private double xcoord, ycoord;

    // import Longitude & Latitude
    public CoordTransTo4326(double xcoord, double ycoord) {
        this.xcoord = xcoord;
        this.ycoord = ycoord;
    }

    // convert to EPSG:4326
    public Point2D.Double to_4326() {

        String[] proj_5179 = new String[] {
                "+proj=tmerc",
                "+lat_0=38",
                "+lon_0=127.5",
                "+k=0.9996",
                "+x_0=1000000",
                "+y_0=2000000",
                "+ellps=GRS80",
                "+towgs84=0,0,0,0,0,0,0",
                "+units=m",
                "+no_defs"
        };

        Point2D.Double epsg_5179 = null;
        Point2D.Double epsg_4326 = null;
        Projection proj = ProjectionFactory.fromPROJ4Specification(proj_5179);

        // UTM-K -> WGS84
        epsg_5179 = new Point2D.Double(xcoord, ycoord);
        epsg_4326 = proj.inverseTransform(epsg_5179, new Point2D.Double());

        return epsg_4326;

    }

}
