package item

import static net.grinder.script.Grinder.grinder
import static org.junit.Assert.*
import static org.hamcrest.Matchers.*
import net.grinder.script.GTest
import net.grinder.script.Grinder
import net.grinder.scriptengine.groovy.junit.GrinderRunner
import net.grinder.scriptengine.groovy.junit.annotation.BeforeProcess
import net.grinder.scriptengine.groovy.junit.annotation.BeforeThread

// import static net.grinder.util.GrinderUtils.* // You can use this if you're using nGrinder after 3.2.3
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

import org.ngrinder.http.HTTPRequest
import org.ngrinder.http.HTTPRequestControl
import org.ngrinder.http.HTTPResponse

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


@RunWith(GrinderRunner)
class GetItem {

    public static GTest test
    public static HTTPRequest request
    public static Map<String, String> headers = [:]
    public static Map<String, Object> params = [:]

    // 데이터베이스 연결 구성
    public static dbUrl = System.getenv("DB_URL");
    public static dbUser = System.getenv("DB_USER");
    public static dbPw = System.getenv("DB_PASSWORD");
    def connection = DriverManager.getConnection(dbUrl, dbUser, dbPw)

    public static NGRINDER_HOSTNAME = System.getenv("NGRINDER_HOSTNAME");


    @BeforeClass
    public static void beforeClass() {
        // JDBC 드라이버 자동 로딩 및 데이터베이스 연결
        try {
            Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPw);
            // 연결된 데이터베이스 사용
            // ...
            connection.close(); // 연결 닫기
        } catch (SQLException e) {
            // 연결 실패 또는 오류 처리
            e.printStackTrace();
        }
    }

    @BeforeProcess
    public static void beforeProcess() {
        HTTPRequestControl.setConnectionTimeout(300000)
        test = new GTest(1, "GTest1")
        request = new HTTPRequest()
        grinder.logger.info("before process.")
    }

    @BeforeThread
    public void beforeThread() {
        test.record(this, "test")
        grinder.statistics.delayReports = true
        grinder.logger.info("before thread.")
    }

    @Before
    public void before() {
        request.setHeaders(headers)
        grinder.logger.info("before. init headers")
    }

    @Test
    public void test() {
        HTTPResponse response = request.GET("http://${NGRINDER_HOSTNAME}:1010/tour-ranger/items/1")

        if (response.statusCode == 301 || response.statusCode == 302) {
            grinder.logger.warn("Warning. The response may not be correct. The response code was {}.", response.statusCode)
        } else {
            assertThat(response.statusCode, is(200))
        }
    }
}
