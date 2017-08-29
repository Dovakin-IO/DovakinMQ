package io.dovakinmq.cache;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liuhuanchao on 2017/8/28.
 */
public class MessageIdentifier {
    private static final ConcurrentHashMap<Integer , Boolean> ids;
    private static int MAX_ID = 65535;
    private static int MIN_ID = 1;
    private static Random random;

    static {
        ids = new ConcurrentHashMap<>();
        random = new Random();
    }

    public static String getId(){
        int id;
        do{
            id = (random.nextInt(MAX_ID)
                    % (MAX_ID - MIN_ID + 1) + MIN_ID);
        }while(ids.get(id) != null && ids.get(id));
        return id + "";
    }

    public static void releaseId(String idStr){
        int id = Integer.parseInt(idStr);
        if(ids.containsKey(id) && ids.get(id)){
            ids.putIfAbsent(id, false);
        }
    }
}
