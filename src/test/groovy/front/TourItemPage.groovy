package front

import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.ThreadLocalRandom

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
class TourItemPage {
    public static GTest test
    public static HTTPRequest request
    public static Map<String, String> headers = [:]
    public static Map<String, Object> params = [:]

    // system env.
    public static NGRINDER_HOSTNAME = System.getenv("NGRINDER_HOSTNAME");
    // random
    def randomNum = 0
    public static final TABLE_COUNT = 10485749;

    @BeforeProcess
    public static void beforeProcess() {
        grinder.logger.info("before process.")

        HTTPRequestControl.setConnectionTimeout(3000) // 3000ms = 3초
        test = new GTest(1, "TourItemPage-FindByName")
        request = new HTTPRequest()
    }

    @BeforeThread
    public void beforeThread() {
        test.record(this, "test")
        grinder.statistics.delayReports = true
        grinder.logger.info("before thread.")
    }

    @Before
    public void before() {
        grinder.logger.info("before. init headers")
        request.setHeaders(headers)
        grinder.logger.info("before. randomNum")
        randomNum = new Random().nextInt(TABLE_COUNT) + 1
    }

    @Test
    public void test() {
        HTTPResponse response = request.GET("http://localhost:1010/tour-ranger/front/items/${randomNum}")

        if (response.statusCode == 301 || response.statusCode == 302) {
            grinder.logger.warn("Warning. The response may not be correct. The response code was {}.", response.statusCode)
        } else {
            assertThat(response.statusCode, is(200))
        }
    }
}

