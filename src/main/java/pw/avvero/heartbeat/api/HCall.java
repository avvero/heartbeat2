package pw.avvero.heartbeat.api;

import lombok.Data;

import java.util.Date;

@Data
public class HCall {

    public HApplication app;
    public Date started;
    public Date finished;
    public String result;

}
