package complexScenario

import org.junit.rules.TestName

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
class PurchaseItemAtTourItemPage {
    public static GTest test1
    public static GTest test2
    public static HTTPRequest request
    public static Map<String, String> headers = [:]

    // file
    public static String requestFile = "src/test/groovy/resources/userEmail.txt" // intelliJ test
    // public static String requestFile = "resources/userEmail.txt" // ngrinder test
    public static List<String> requests = new ArrayList<>()
    public static String body = ""

    public static TOURRANGER_HOSTNAME = System.getenv("TOURRANGER_HOSTNAME");

    @BeforeProcess
    public static void beforeProcess() {
        grinder.logger.info("before process.")

        grinder.logger.info("request file")
        requests = Files.readAllLines(Paths.get(requestFile))

        HTTPRequestControl.setConnectionTimeout(300) // 300 ms
        test1 = new GTest(1, "GET /tour-ranger/front/items/{itemId}")
        test2 = new GTest(2, "POST /tour-ranger/purchases/{itemId}")
        request = new HTTPRequest()

        // Set header data
        headers.put("Content-Type", "application/json")
    }

    @BeforeThread
    public void beforeThread() {
        test1.record(this, "test1_getTourItemPage")
        test2.record(this, "test2_postPurchaseItem")
        grinder.statistics.delayReports = true
        grinder.logger.info("before thread.")
    }

    @Before
    public void before() {
        grinder.logger.info("before. init headers")
        request.setHeaders(headers)
        grinder.logger.info("before. get request")
        body = "{\n\"email\":\"" + requests.get(ThreadLocalRandom.current().nextInt(requests.size())) + "\"}"
        print(body)
    }

    @Test
    public void test1_getTourItemPage() {
        print("test: getTourItemPage")

        HTTPResponse response = request.GET("${TOURRANGER_HOSTNAME}/tour-ranger/front/items/1")
        if (response.statusCode == 301 || response.statusCode == 302) {
            grinder.logger.warn("Warning. The response may not be correct. The response code was {}.", response.statusCode)
        } else {
            assertThat(response.statusCode, is(200))
        }
        print("조회 성공")
    }

    @Test
    public void test2_postPurchaseItem() {
        print("test: postPurchaseItem")

        HTTPResponse response = request.POST("${TOURRANGER_HOSTNAME}/tour-ranger/purchases/1", body.getBytes())
        if (response.statusCode == 301 || response.statusCode == 302) {
            grinder.logger.warn("Warning. The response may not be correct. The response code was {}.", response.statusCode)
        } else {
            assertThat(response.statusCode, is(200))
        }
    }
}
