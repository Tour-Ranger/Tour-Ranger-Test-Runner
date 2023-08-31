package front

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

import java.net.URLEncoder
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


@RunWith(GrinderRunner)
class TourItemPageFindByName {
    public static GTest test
    public static HTTPRequest request
    public static Map<String, String> headers = [:]
    public static Map<String, String> params = ["itemName": "★[오전출발]도쿄 에어텔 / 2박3일 / [5144] 뉴 스타 이케부쿠로 / 티웨이항공★"]

    public static NGRINDER_HOSTNAME = System.getenv("NGRINDER_HOSTNAME");

    @BeforeProcess
    public static void beforeProcess() {
        HTTPRequestControl.setConnectionTimeout(3000) // item(20) + image(100) = 120 ms
        test = new GTest(1, "TourItemPage-FindByName")
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
        def paramsKeySet = params.keySet()
        def paramsValue =params.get(paramsKeySet[0])
        def encodedString = URLEncoder.encode(paramsValue, "UTF-8")

        HTTPResponse response1 = request.GET("http://${NGRINDER_HOSTNAME}:1010/tour-ranger/front/items?${paramsKeySet[0]}=${encodedString}")

        if (response1.statusCode == 301 || response1.statusCode == 302) {
            grinder.logger.warn("Warning. The response1 may not be correct. The response1 code was {}.", response1.statusCode)
        } else {
            assertThat(response1.statusCode, is(200))
        }

        // grinder.logger.info("body: {}", response1.bodyText);
        grinder.logger.info("Test End")
    }
}