package fish.payara.cargotracker.integration;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDateInput;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import java.io.IOException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.MavenImporter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Assert;
import org.junit.Before;

/**
 * @author Fraser Savage
 * This test class is used to automate testing that books a new cargo journey, views the details and itinerary and changes the destination.
 */
@RunWith(Arquillian.class)
public class BookingTest {
    private static final Logger log = Logger.getLogger(BookingTest.class.getCanonicalName());
    
    private static String newCargoId;

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
            Assert.fail("An IOException was thrown during the test setup for class \"" + BookingTest.class.getSimpleName() + "\" at method \"" + testName.getMethodName() + "\" with message: " + ex.getMessage());
        }
    }
    
    // TODO Create test to book new cargo through admin interface.
    @Test
    @RunAsClient
    @InSequence(1)
    public void testBookNewCargo() {
        
        try {
            //book cargo and set the new cargo ID as the value of newCargoId
            
            HtmlPage admin = landingPageResponse.getElementById("adminLandingLink").click();
            Assert.assertThat("Page title was not as expected for the admin dashboard. Expected \"Cargo Dashboard\" but actual was \"" + admin.getTitleText() + "\"." , admin.getTitleText(), is("Cargo Dashboard"));
            HtmlPage makeBooking = admin.getElementById("adminBooking").click();
            Assert.assertThat("Page title was not as expected for the admin dashboard. Expected \"Cargo Administration\" but actual was \"" + makeBooking.getTitleText() + "\"." , makeBooking.getTitleText(), is("Cargo Administration"));
            HtmlSelect getDestinations = makeBooking.getElementByName("registrationForm:j_idt17");
            HtmlOption selectDestination = getDestinations.getOptionByText("Tokyo (JNTKO)");
            getDestinations.setSelectedAttribute(selectDestination, true);
            HtmlDateInput dateInput = makeBooking.getElementByName("registrationForm:j_idt20");
            dateInput.setValueAttribute("2016-06-06");
            HtmlPage confirmationPage = makeBooking.getElementByName("registrationForm:j_idt22").click();
            Assert.assertTrue("", confirmationPage.asText().contains("Chicago (USCHI)") );
            Assert.assertTrue("", confirmationPage.asText().contains("Tokyo (JNTKO)") );
            List<?> getID = confirmationPage.getByXPath("//span[@class='success label']/text()");
            Object cargoIDPhrase = getID.get(0);
            String [] fragments = cargoIDPhrase.toString().split(" ");
            newCargoId = fragments[3];
            
            //route cargo + routing breaks as shrinkwrap renames the war with _DEFAULT__DEFAULT
            HtmlAnchor anchor = confirmationPage.getAnchorByHref("/cargo-tracker/admin/selectItinerary.xhtml?trackingId="+newCargoId);
            HtmlPage router = anchor.click();
           // HtmlPage route = router.getElementByName("j_idt16:0:j_idt17:j_idt22").click();
        }
        catch(IOException ie) {
            Assert.fail("An IOException was thrown during the test for class \"" + BookingTest.class.getSimpleName() + "\" at method \"" + testName.getMethodName() + "\" with message: " + ie.getMessage());

        }
            
        log.log(Level.INFO, "Successfully booked new cargo with Id \"" + newCargoId + "\".");
    }

    // TODO Create test to track new cargo through public interface.
    @Test
    @RunAsClient
    @InSequence(2)
    public void testPublicTrackNewCargo() {
        
        try {
            
            HtmlPage enterCargoIdPage = landingPageResponse.getElementById("publicLandingLink").click();
            enterCargoIdPage.getElementById("trackingForm:trackingIdInput").setAttribute("value", newCargoId);
            System.out.println(enterCargoIdPage.getElementById("trackingForm:trackingIdInput").getAttribute("value"));
            HtmlPage trackingPage = enterCargoIdPage.getElementById("trackingForm:submitTrack").click();
            Assert.assertTrue("Handling history did not show expected first event.", trackingPage.asText().contains("Cargo "+newCargoId+" is now: Not received"));
            Assert.assertTrue("Handling history did not show expected second event.", trackingPage.asText().contains("Estimated time of arrival in Tokyo"));
            //Assert.assertTrue("Handling history did not show expected third event.", trackingPage.asText().contains("Next expected activity is to receive cargo in Chicago"));

        } catch (IOException ex) {
            Assert.fail("An IOException was thrown during the test for class \"" + BookingTest.class.getSimpleName() + "\" at method \"" + testName.getMethodName() + "\" with message: " + ex.getMessage());
        }
    
    }

    // TODO Create test to track new cargo through admin interface.
    @Test
    @RunAsClient
    @InSequence(3)
    public void testAdminTrackNewCargo() {
        
        try {
            
            HtmlPage adminDashboard = landingPageResponse.getElementById("adminLandingLink").click();
            Assert.assertThat("Page title was not as expected for the admin dashboard. Expected \"Cargo Dashboard\" but actual was \"" + adminDashboard.getTitleText() + "\"." , adminDashboard.getTitleText(), is("Cargo Dashboard"));
            HtmlPage enterCargoIdPage = adminDashboard.getElementById("adminTracking").click();
            enterCargoIdPage.getElementById("trackingForm:trackingIdInput").setAttribute("value", newCargoId);
            System.out.println(enterCargoIdPage.getElementById("trackingForm:trackingIdInput").getAttribute("value"));
            HtmlPage trackingPage = enterCargoIdPage.getElementById("trackingForm:submitTrack").click();
            Assert.assertTrue("Handling history did not show expected first event.", trackingPage.asText().contains("Cargo "+newCargoId+" is now: Not received"));
            Assert.assertTrue("Handling history did not show expected second event.", trackingPage.asText().contains("Estimated time of arrival in Tokyo"));
            //Assert.assertTrue("Handling history did not show expected third event.", trackingPage.asText().contains("Next expected activity is to receive cargo in Chicago"));
        } catch (IOException ex) {
            Assert.fail("An IOException was thrown during the test for class \"" + BookingTest.class.getSimpleName() + "\" at method \"" + testName.getMethodName() + "\" with message: " + ex.getMessage());        }
    
    
    }

    // TODO Create test to view details of new cargo through the admin interface.
    @Test
    @RunAsClient
    @InSequence(4)
    public void testViewDetailsNewCargo() {
        
        try {
            
            //Stores the adminDashboard as a HtmlPage object.
            HtmlPage adminDashboard = landingPageResponse.getElementById("adminLandingLink").click();
            Assert.assertThat("Page title was not as expected for the admin dashboard. Expected \"Cargo Dashboard\" but actual was \"" + adminDashboard.getTitleText() + "\"." , adminDashboard.getTitleText(), is("Cargo Dashboard"));
            //Stores the details page as a HtmlPage object.           
            HtmlPage detailsPage = adminDashboard.getAnchorByText(newCargoId).click();
            

            Assert.assertTrue("Origin was not as expected", detailsPage.asText().contains("Origin	Chicago (USCHI)"));
            Assert.assertTrue("Destination was not as expected", detailsPage.asText().contains("Destination	Tokyo (JNTKO)"));
        } catch (IOException ex) {
            Assert.fail("An IOException was thrown during the test for class \"" + BookingTest.class.getSimpleName() + "\" at method \"" + testName.getMethodName() + "\" with message: " + ex.getMessage());
        }
    }

    // TODO Create test to change the destination of the new cargo through the admin interface.
    @Test
    @RunAsClient
    @InSequence(5)
    public void testChangeEndNewCargo() {
        
        
        
        try {
            
            HtmlPage adminDashboard = landingPageResponse.getElementById("adminLandingLink").click();
            Assert.assertThat("Page title was not as expected for the admin dashboard. Expected \"Cargo Dashboard\" but actual was \"" + adminDashboard.getTitleText() + "\"." , adminDashboard.getTitleText(), is("Cargo Dashboard"));
            HtmlPage detailsPage = adminDashboard.getAnchorByText(newCargoId).click();
            Assert.assertThat("Page title was not as expected for the admin dashboard. Expected \"Cargo Administration\" but actual was \"" + detailsPage.getTitleText() + "\".", detailsPage.getTitleText(), is("Cargo Administration"));
            HtmlPage destinationPage = detailsPage.getAnchorByText("Change destination").click();
        
            HtmlSelect getDestinations = destinationPage.getElementByName("j_idt14:j_idt16");
            HtmlOption selectDestination = getDestinations.getOptionByText("Stockholm (SESTO)");
            getDestinations.setSelectedAttribute(selectDestination, true);
            HtmlPage confirmationPage = destinationPage.getElementByName("j_idt14:j_idt19").click();
            Assert.assertTrue("Destination was not as expected", confirmationPage.asText().contains("Destination	Stockholm (SESTO)"));
        } catch (IOException ex) {
            Assert.fail("An IOException was thrown during the test for class \"" + BookingTest.class.getSimpleName() + "\" at method \"" + testName.getMethodName() + "\" with message: " + ex.getMessage());
        }
        
    }
}
