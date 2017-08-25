package io.dovakinmq.constant;

import io.dovakinmq.manager.ClientIdentifier;

/**
 * Created by liuhuanchao on 2017/8/24.
 */
public class ChannelInfo {
    private ClientIdentifier identifier;

    public ChannelInfo(String id){
        this.identifier = new ClientIdentifier(id);
    }

    public ClientIdentifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(ClientIdentifier identifier) {
        this.identifier = identifier;
    }
}
