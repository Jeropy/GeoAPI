package baroEmap;

import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jaypark on 2017. 9. 11..
 */
@RestController
public class PoiFunction {

    PoiDao poiDao = new PoiDao();

    // RESTful API 기능 제공을 위한 URL 생성
    @RequestMapping("/nearbysearch")
    public JSONObject nearPois(
            @RequestParam(value = "location") String location,
            @RequestParam(value = "radius") int rad,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "keyword", required = false) String keyword) {

        if (type != null) {
            if (keyword != null)
                return poiDao.getPois(location, rad, type, keyword);
            else
                return poiDao.getPois(location, rad, type, "");
        } else {
            if (keyword != null)
                return poiDao.getPois(location, rad, "", keyword);
            else
                return poiDao.getPois(location, rad, "", "");
        }

    }

}
