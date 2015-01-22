package net.java.cargotracker.infrastructure.messaging.jms;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import net.java.cargotracker.application.CargoInspectionService;
import net.java.cargotracker.domain.model.cargo.TrackingId;

/**
 * Consumes JMS messages and delegates notification of misdirected cargo to the
 * tracking service.
 *
 * This is a programmatic hook into the JMS infrastructure to make cargo
 * inspection message-driven.
 */
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", 
        propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "destinationLookup", 
        propertyValue = "java:global/jms/CargoHandledQueue")
})
public class CargoHandledConsumer implements MessageListener {

    @Inject
    private CargoInspectionService cargoInspectionService;
    private static final Logger logger = Logger.getLogger(
            CargoHandledConsumer.class.getName());

    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage) message;
            String trackingIdString = textMessage.getText();

            cargoInspectionService.inspectCargo(new TrackingId(trackingIdString));
        } catch (JMSException e) {
            logger.log(Level.SEVERE, "Error procesing JMS message", e);
        }
    }
}
