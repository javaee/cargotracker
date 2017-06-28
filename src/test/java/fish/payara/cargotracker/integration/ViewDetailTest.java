package fish.payara.cargotracker.integration;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import net.java.cargotracker.application.internal.DefaultBookingService;
import net.java.cargotracker.application.util.DateUtil;
import net.java.cargotracker.application.util.JsonMoxyConfigurationContextResolver;
import net.java.cargotracker.domain.model.cargo.*;
import net.java.cargotracker.domain.model.handling.*;
import net.java.cargotracker.domain.model.location.Location;
import net.java.cargotracker.domain.model.location.LocationRepository;
import net.java.cargotracker.domain.model.location.SampleLocations;
import net.java.cargotracker.domain.model.location.UnLocode;
import net.java.cargotracker.domain.model.voyage.*;
import net.java.cargotracker.domain.service.RoutingService;
import net.java.cargotracker.domain.shared.*;
import net.java.cargotracker.infrastructure.persistence.jpa.JpaCargoRepository;
import net.java.cargotracker.infrastructure.persistence.jpa.JpaHandlingEventRepository;
import net.java.cargotracker.infrastructure.persistence.jpa.JpaLocationRepository;
import net.java.cargotracker.infrastructure.persistence.jpa.JpaVoyageRepository;
import net.java.cargotracker.infrastructure.routing.ExternalRoutingService;
import net.java.pathfinder.api.GraphTraversalService;
import net.java.pathfinder.api.TransitEdge;
import net.java.pathfinder.api.TransitPath;
import net.java.pathfinder.internal.GraphDao;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.MavenImporter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.hamcrest.CoreMatchers.is;

/**
 * @author Fraser Savage
 * Automation of testing that details and itineraries can be viewed for all pre-existing cargo.
 */
@RunWith(Arquillian.class)
public class ViewDetailTest {
    private static final Logger log = Logger.getLogger(ViewDetailTest.class.getCanonicalName());
    private static final String trackingId1 = "ABC123";
    private static final String trackingId2 = "JKL567";
    private static final String trackingId3 = "MNO456";
    private static final String trackingId4 = "DEF789";

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
    private URL deploymentUrl;

    @Rule
    public TestName testName = new TestName();

    private WebClient browser;

    private HtmlPage landingPageResponse;

    /**
     * Set up method run before each test to create a new Web client and load the application's landing page.
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
            Assert.fail("An IOException was thrown during the test setup for class \"" + ViewDetailTest.class.getSimpleName() + "\" at method \"" + testName.getMethodName() + "\" with message: " + ex.getMessage());
        }
    }

    /**
     * Tests that the details of cargo with tracking Id ABC123 can be viewed through the admin interface.
     */
    @Test
    @RunAsClient
    public void testViewDetailsId1() {
        
        try {
            
            //Stores the adminDashboard as a HtmlPage object.
            HtmlPage adminDashboard = landingPageResponse.getElementById("adminLandingLink").click();
            Assert.assertThat("Page title was not as expected for the admin dashboard. Expected \"Cargo Dashboard\" but actual was \"" + adminDashboard.getTitleText() + "\"." , adminDashboard.getTitleText(), is("Cargo Dashboard"));
            //Stores the details page as a HtmlPage object.
            HtmlPage detailsPage = adminDashboard.getAnchorByText(trackingId1).click();
            Assert.assertTrue("Handling history did not show expected first voyage number.", detailsPage.asText().contains("0100S"));
            Assert.assertTrue("Handling history did not show expected second voyage number.", detailsPage.asText().contains("0200T"));
            Assert.assertTrue("Handling history did not show expected third voyage number.", detailsPage.asText().contains("0300A"));
        } catch (IOException ex) {
            Assert.fail("An IOException was thrown during the test for class \"" + ViewDetailTest.class.getSimpleName() + "\" at method \"" + testName.getMethodName() + "\" with message: " + ex.getMessage());
        }
    }

    /**
     * Tests that the details of cargo with tracking Id JKL567 can be viewed through the admin interface.
     */
    @Test
    @RunAsClient
    public void testViewDetailsId2() {
        
        try {
            
            //Stores the adminDashboard as a HtmlPage object.
            HtmlPage adminDashboard = landingPageResponse.getElementById("adminLandingLink").click();
            Assert.assertThat("Page title was not as expected for the admin dashboard. Expected \"Cargo Dashboard\" but actual was \"" + adminDashboard.getTitleText() + "\"." , adminDashboard.getTitleText(), is("Cargo Dashboard"));
            //Stores the details page as a HtmlPage object.
            HtmlPage detailsPage = adminDashboard.getAnchorByText(trackingId2).click();
            Assert.assertTrue("Handling history did not show expected first voyage number.", detailsPage.asText().contains("0100S"));
            Assert.assertTrue("Handling history did not show expected second voyage number.", detailsPage.asText().contains("0200T"));
            Assert.assertTrue("Handling history did not show expected third voyage number.", detailsPage.asText().contains("0300A"));
        } catch (IOException ex) {
            Assert.fail("An IOException was thrown during the test for class \"" + ViewDetailTest.class.getSimpleName() + "\" at method \"" + testName.getMethodName() + "\" with message: " + ex.getMessage());
        }
    }

    /**
     * Tests that the details of cargo with tracking Id MNO456 can be viewed through the admin interface.
     */
    @Test
    @RunAsClient
    public void testViewDetailsId3() {
        
        try {
            
            //Stores the adminDashboard as a HtmlPage object.
            HtmlPage adminDashboard = landingPageResponse.getElementById("adminLandingLink").click();
            Assert.assertThat("Page title was not as expected for the admin dashboard. Expected \"Cargo Dashboard\" but actual was \"" + adminDashboard.getTitleText() + "\"." , adminDashboard.getTitleText(), is("Cargo Dashboard"));
            //Stores the details page as a HtmlPage object.
            HtmlPage detailsPage = adminDashboard.getAnchorByText(trackingId3).click();
            Assert.assertTrue("Handling history did not show expected first voyage number.", detailsPage.asText().contains("0200T"));
        } catch (IOException ex) {
            Assert.fail("An IOException was thrown during the test for class \"" + ViewDetailTest.class.getSimpleName() + "\" at method \"" + testName.getMethodName() + "\" with message: " + ex.getMessage());
        }
    }

    /**
     * Tests that the details of cargo with tracking Id DEF789 can be viewed through the admin interface.
     */
    @Test
    @RunAsClient
    public void testViewDetailsId4() {
        
        try {
            
            //Stores the adminDashboard as a HtmlPage object.
            HtmlPage adminDashboard = landingPageResponse.getElementById("adminLandingLink").click();
            Assert.assertThat("Page title was not as expected for the admin dashboard. Expected \"Cargo Dashboard\" but actual was \"" + adminDashboard.getTitleText() + "\"." , adminDashboard.getTitleText(), is("Cargo Dashboard"));
            //Stores the details page as a HtmlPage object.
            HtmlPage detailsPage = adminDashboard.getAnchorByText(trackingId4).click();
            Assert.assertTrue("Expected \"Not routed\" message was not found.", detailsPage.asText().contains("Not routed"));
        } catch (IOException ex) {
            Assert.fail("An IOException was thrown during the test for class \"" + ViewDetailTest.class.getSimpleName() + "\" at method \"" + testName.getMethodName() + "\" with message: " + ex.getMessage());
        }
    }

}
