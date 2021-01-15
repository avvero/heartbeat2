package pw.avvero.heartbeat.api;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class HComponent {

    public String url;

    public HApplication app;
    public Date lastUpdated;
    public String error;

    public List<HCall> calls;

}
