package io.dovakinmq.validator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liuhuanchao on 2017/8/17.
 */
public class ValidateTreeNode {
    private String nodeName;
    private ValidateTreeNode preNode;
    private ConcurrentHashMap<String, ValidateTreeNode> nextNodes;
    private List<String> tagList;
    private int childPointer;

    private Method method;

    public ValidateTreeNode(ValidateTreeNode preNode){
        this.preNode = preNode;
        this.nextNodes = new ConcurrentHashMap<String, ValidateTreeNode>();
        this.tagList = new ArrayList<String>();
        this.childPointer = 0;
    }

    public ValidateTreeNode nextNode(){
        String tag = tagList.get(childPointer++);
        return nextNodes.get(tag);
    }

    public void resetChildPointer(){
        childPointer = 0;
    }

    public void addChild(String nodeName, ValidateTreeNode childNode){
        tagList.add(nodeName);
        nextNodes.putIfAbsent(nodeName,childNode);
    }

    public void addChild(String nodeName, ValidateTreeNode childNode, int index){
        tagList.add(index,nodeName);
        nextNodes.putIfAbsent(nodeName, childNode);
    }

    public ValidateTreeNode moveToRoot(){
        return moveToRoot(this);
    }


    private ValidateTreeNode moveToRoot(ValidateTreeNode node){
        if(node.isRoot()) return node;
        return moveToRoot(node.preNode);
    }

    public boolean isRoot(){
        return preNode == null ? true : false;
    }

    public boolean hasChild(){
        return nextNodes.size() == 0 ? false : true;
    }
}
