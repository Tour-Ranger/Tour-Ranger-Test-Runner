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

import java.net.URLEncoder
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


@RunWith(GrinderRunner)
class TourItemPageFindByName {
    public static GTest test
    public static HTTPRequest request
    public static Map<String, String> headers = [:]
    public static Map<String, String> params = [:]

    // system env.
    public static NGRINDER_HOSTNAME = System.getenv("NGRINDER_HOSTNAME");

    // file
    public static String requestFile = "src/test/resources/tourItemPage_findByName.txt"
    public static List<String> requests = new ArrayList<>()
    public static String body = ""

    @BeforeProcess
    public static void beforeProcess() {
        grinder.logger.info("before process.")

        grinder.logger.info("request file")
        requests = Files.readAllLines(Paths.get(requestFile))

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
        grinder.logger.info("before. get request")
        body = requests.get(ThreadLocalRandom.current().nextInt(requests.size()))
        print(body)
    }

    @Test
    public void test() {
        def encodedUrl = URLEncoder.encode(body, "UTF-8")
        HTTPResponse response1 = request.GET("http://${NGRINDER_HOSTNAME}:1010/tour-ranger/front/items?itemName=${encodedUrl}")

        if (response1.statusCode == 301 || response1.statusCode == 302) {
            grinder.logger.warn("Warning. The response1 may not be correct. The response1 code was {}.", response1.statusCode)
        } else {
            assertThat(response1.statusCode, is(200))
        }
        // grinder.logger.info("body: {}", response1.bodyText);
        grinder.logger.info("Test End")
    }
}