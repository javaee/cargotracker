package net.java.cargotracker.interfaces.handling.mobile;

//import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.flow.FlowHandler;
import javax.inject.Named;

/**
 *
 * @author davidd
 */
@Named
@SessionScoped
public class HolderBean implements Serializable{

    // TODO: this is really a workaround for now as viewaction can't invoke a faceflow directly!
    
    private String holder = "workaround";

    void setHolder(String holder) {
        this.holder = holder;
    }

    public String getHolder() {
        return holder;
    }

    public String initFlow() {
        FacesContext context = FacesContext.getCurrentInstance();
        FlowHandler handler = context.getApplication().getFlowHandler();
        handler.transition(context, null, handler.getFlow(context, "", "eventLogger"), null, "");
        return "eventLogger";
    }

}
