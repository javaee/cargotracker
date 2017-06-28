package fish.payara.tests.external.cargotracker.integration;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.hamcrest.CoreMatchers.is;
import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.MavenImporter;

/**
 * @author Fraser Savage
 * Tests that the pre-existing cargo can have their tracking info viewed through both the public interface and the admin interface.
 */
//@RunWith(Arquillian.class)
public class TrackExistingTest {
    private static final Logger log = Logger.getLogger(TrackExistingTest.class.getCanonicalName());
    private static final String webapp_src = "src/main/webapp";
    private static final String trackingId1 = "ABC123";
    private static final String trackingId2 = "JKL567";

    /**
     * Deploys the war to the application server.
     * @return
     */
    @Deployment
    public static WebArchive createDeployment() {

        WebArchive war = ShrinkWrap.create(MavenImporter.class)
                .loadPomFromFile("pom.xml").importBuildOutput().as(WebArchive.class);
        
        return war;
    }

    @ArquillianResource
    private URI deploymentUrl;

    @Rule
    public TestName testName = new TestName();

    private WebClient browser;

    private HtmlPage landingPageResponse;

    /**
     * Set up method run before each test to load up a web client and load the application landing page.
     */
    @Before
    @RunAsClient
    public void setUp() {
        try {
            browser = new WebClient();
            java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
            browser.getOptions().setThrowExceptionOnScriptError(false);
            landingPageResponse = browser.getPage(deploymentUrl.toString() + "index.xhtml");
            Assert.assertEquals("Could not load the application landing page.", "Cargo Tracker", landingPageResponse.getTitleText());
        } catch (IOException ex) {
            Assert.fail("An IOException was thrown during the test setup for class \"" + TrackExistingTest.class.getSimpleName() + "\" at method \"" + testName.getMethodName() + "\" with message: " + ex.getMessage());
        }
    }

//    /**
//     * Tests that tracking of the Id ABC123 works through the public interface.
//     */
//    @Test
//    @RunAsClient
//    @InSequence(1)
//    public void testPublicTrackingId1() {
//        
//        try {
//            
//            HtmlPage enterCargoIdPage = landingPageResponse.getElementById("publicLandingLink").click();
//            enterCargoIdPage.getElementById("trackingForm:trackingIdInput").setAttribute("value", trackingId1);
//            
//            HtmlPage trackingPage = enterCargoIdPage.getElementById("trackingForm:submitTrack").click();
//            Assert.assertTrue("Handling history did not show expected first event.", trackingPage.asText().contains("Received in Hong Kong, at 03/01/2014 12:00 AM"));
//            Assert.assertTrue("Handling history did not show expected second event.", trackingPage.asText().contains("Loaded onto voyage 0100S in Hong Kong, at 03/02/2014 12:00 AM"));
//            Assert.assertTrue("Handling history did not show expected third event.", trackingPage.asText().contains("Unloaded off voyage 0100S in New York, at 03/05/2014 12:00 AM"));
//        } catch (IOException ex) {
//            Assert.fail("An IOException was thrown during the test for class \"" + TrackExistingTest.class.getSimpleName() + "\" at method \"" + testName.getMethodName() + "\" with message: " + ex.getMessage());
//        }
//    }
//
//    /**
//     * Tests that the tracking of the Id JKL567 works through the public interface.
//     */
//    @Test
//    @RunAsClient
//    @InSequence(2)
//    public void testPublicTrackingId2() {
//        
//        try {
//            
//            HtmlPage enterCargoIdPage = landingPageResponse.getElementById("publicLandingLink").click();
//
//            enterCargoIdPage.getElementById("trackingForm:trackingIdInput").setAttribute("value", trackingId2);
//            
//            HtmlPage trackingPage = enterCargoIdPage.getElementById("trackingForm:submitTrack").click();
//            Assert.assertTrue("Tracker did not contain expected misdirection notice.", trackingPage.asText().contains("Cargo is misdirected"));
//            Assert.assertTrue("Handling history did not show expected first event.", trackingPage.asText().contains("Received in Hangzhou, at 03/01/2014 12:00 AM"));
//            Assert.assertTrue("Handling history did not show expected second event.", trackingPage.asText().contains("Loaded onto voyage 0100S in Hangzhou, at 03/03/2014 12:00 AM"));
//            Assert.assertTrue("Handling history did not show expected third event.", trackingPage.asText().contains("Unloaded off voyage 0100S in New York, at 03/05/2014 12:00 AM"));
//            Assert.assertTrue("Handling history did not show expected fourth event.", trackingPage.asText().contains("Loaded onto voyage 0100S in New York, at 03/06/2014 12:00 AM"));
//        } catch (IOException ex) {
//            Assert.fail("An IOException was thrown during the test for class \"" + TrackExistingTest.class.getSimpleName() + "\" at method \"" + testName.getMethodName() + "\" with message: " + ex.getMessage());
//        }
//    }
//
//    /**
//     * Tests that tracking Id ABC123 can be tracked through the Admin interface.
//     */
//    @Test
//    @RunAsClient
//    @InSequence(3)
//    public void testAdminTrackingId1(){
//        
//        try {
//            
//            HtmlPage adminDashboard = landingPageResponse.getElementById("adminLandingLink").click();
//            Assert.assertThat("Page title was not as expected for the admin dashboard. Expected \"Cargo Dashboard\" but actual was \"" + adminDashboard.getTitleText() + "\"." , adminDashboard.getTitleText(), is("Cargo Dashboard"));
//            HtmlPage enterCargoIdPage = adminDashboard.getElementById("adminTracking").click();
//            enterCargoIdPage.getElementById("trackingForm:trackingIdInput").setAttribute("value", trackingId1);
//            
//            HtmlPage trackingPage = enterCargoIdPage.getElementById("trackingForm:submitTrack").click();
//            Assert.assertTrue("Tracker did not contain expected next activity.", trackingPage.asText().contains("Next expected activity is to load cargo onto voyage 0200T in New York"));
//            Assert.assertTrue("Handling history did not show expected first event.", trackingPage.asText().contains("Received in Hong Kong, at 03/01/2014 12:00 AM"));
//            Assert.assertTrue("Handling history did not show expected second event.", trackingPage.asText().contains("Loaded onto voyage 0100S in Hong Kong, at 03/02/2014 12:00 AM"));
//            Assert.assertTrue("Handling history did not show expected third event.", trackingPage.asText().contains("Unloaded off voyage 0100S in New York, at 03/05/2014 12:00 AM"));
//        } catch (IOException ex) {
//            Assert.fail("An IOException was thrown during the test for class \"" + TrackExistingTest.class.getSimpleName() + "\" at method \"" + testName.getMethodName() + "\" with message: " + ex.getMessage());        }
//    }
//
//    /**
//     * Tests that the tracking Id JKL567 can be tracked through the admin interface.
//     */
//    @Test
//    @RunAsClient
//    @InSequence(4)
//    public void testAdminTrackingId2() {
//        
//        try {
//            
//            HtmlPage adminDashboard = landingPageResponse.getElementById("adminLandingLink").click();
//            Assert.assertThat("Page title was not as expected for the admin dashboard. Expected \"Cargo Dashboard\" but actual was \"" + adminDashboard.getTitleText() + "\"." , adminDashboard.getTitleText(), is("Cargo Dashboard"));
//            HtmlPage enterCargoIdPage = adminDashboard.getElementById("adminTracking").click();
//            enterCargoIdPage.getElementById("trackingForm:trackingIdInput").setAttribute("value", trackingId2);
//            
//            HtmlPage trackingPage = enterCargoIdPage.getElementById("trackingForm:submitTrack").click();
//            Assert.assertTrue("Tracker did not contain expected misdirection notice.", trackingPage.asText().contains("Cargo is misdirected"));
//            Assert.assertTrue("Handling history did not show expected first event.", trackingPage.asText().contains("Received in Hangzhou, at 03/01/2014 12:00 AM"));
//            Assert.assertTrue("Handling history did not show expected second event.", trackingPage.asText().contains("Loaded onto voyage 0100S in Hangzhou, at 03/03/2014 12:00 AM"));
//            Assert.assertTrue("Handling history did not show expected third event.", trackingPage.asText().contains("Unloaded off voyage 0100S in New York, at 03/05/2014 12:00 AM"));
//            Assert.assertTrue("Handling history did not show expected fourth event.", trackingPage.asText().contains("Loaded onto voyage 0100S in New York, at 03/06/2014 12:00 AM"));
//        } catch (IOException ex) {
//            Assert.fail("An IOException was thrown during the test for class \"" + TrackExistingTest.class.getSimpleName() + "\" at method \"" + testName.getMethodName() + "\" with message: " + ex.getMessage());        }
//    }
}
