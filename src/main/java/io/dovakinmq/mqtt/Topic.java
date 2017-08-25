package io.dovakinmq.mqtt;

import io.netty.util.internal.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuhuanchao on 2017/8/23.
 */
public class Topic {

    public static final String MULTI = "#";
    public static final String SINGLE = "+";

    private String topicName;
    private List<Element> elements;
    private int cursor = 0;

    public Topic(String topicName){
        this.topicName = topicName;
        this.elements = new ArrayList<Element>();
        parseElements();
    }

    public Element next(){
        if(cursor >= elements.size()) return null;
        return elements.get(cursor++);
    }

    public Topic moveToNext(){
        cursor++;
        return this;
    }

    public boolean hasNext(){
        return cursor < elements.size();
    }

    public void reset(){
        cursor = 0;
    }

    public Element getHeadElement(){
        return elements.get(0);
    }

    public String getTopicName() {
        return topicName;
    }

    public List<Element> getElements() {
        return elements;
    }

    private void parseElements(){
        String[] var1 = topicName.split("/");
        if (var1.length == 0){
            Element element = new Element(topicName);
            elements.add(element);
            return;
        }

        if(topicName.endsWith("/")){
            String[] var2 = new String[var1.length + 1];
            System.arraycopy(var1,0,var2,0,var1.length);
            var2[var1.length] = StringUtil.EMPTY_STRING;
            var1 = var2;
        }

        for(String var : var1){
            if(var.equals(StringUtil.EMPTY_STRING)) break;
            Element element = new Element(var);
            elements.add(element);
        }

        return;
    }

    public class Element{
        private String value;

        public Element(String value){
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public boolean equals(Object var) {
            if (var == null) return false;
            if (var.getClass() != getClass()) return false;
            final Element element = (Element) var;
            if((this.value == null) ?
                    (element.value != null) : !(this.value.equals(element.getValue()))) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }
}
