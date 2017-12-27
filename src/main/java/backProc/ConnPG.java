package backProc;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jaypark on 2017. 9. 11..
 */
public class ConnPG {

    private DataStore dataStore;

    public ConnPG() {

        // Postgresql DB 연결
        Map<String, Object> params = new HashMap();
        params.put( "dbtype", "postgis" );

        // DB가 저장되어있는 host 주소 입력
        params.put( "host", "192.168.1.***" );
        // port 번호 입력
        params.put( "port", 0000 );
        // PostgreSQL 내 접근해야 할 database 이름 입력
        params.put( "database", "***" );
        // 해당 database 내 schema 이름 입력
        params.put( "schema", "***" );
        // user id 입력
        params.put( "user", "***" );
        // password 입력
        params.put( "passwd", "***" );

        try {
            dataStore = DataStoreFinder.getDataStore(params);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public DataStore getDataStore() { return dataStore; }

}
