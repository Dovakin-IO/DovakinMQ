package io.dovakinmq.manager;

/**
 * Created by Link on 2017/8/15.
 */
public class ClientIdentifier {
    private String id;

    public ClientIdentifier(String id){
        this.id = id;
    }

    public String value() {
        return id;
    }

}
