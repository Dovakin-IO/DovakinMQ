package io.dovakinmq.server;

import io.dovakinmq.cache.MqttSession;
import io.dovakinmq.cache.MqttSessionCache;
import io.dovakinmq.mqtt.QoSMessagePack;

import java.util.concurrent.*;

/**
 * Created by liuhuanchao on 2017/8/26.
 */
public class MessageExecutor {
    private static ExecutorService fixThreadPool;
    private static BlockingQueue<QoSMessagePack> messageQueue;

    private static final long INTERVAL = 10000L;
    private static final long THREAD_INTERVAL = 2000L;

    static {
        fixThreadPool = Executors.newFixedThreadPool(4);
        messageQueue = new LinkedBlockingDeque<>();
        fixThreadPool.execute(() ->  {
                while (true){
                    try {
                        QoSMessagePack messagePack = messageQueue.take();
                        fixThreadPool.execute(new MessagePackRunnable(messagePack));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        });

        fixThreadPool.execute(() -> {
            while(true){
                MqttSessionCache.cache.forEach((id, session)
                        -> session.getMessageList().forEach((packId, messageTask)
                        -> {
                                if (messageTask.getState() != QoSMessagePack.STATE.DONE &&
                                    System.currentTimeMillis() - messageTask.getLastExecuteTime()
                                    > INTERVAL){
                                    try {
                                        messageQueue.put(messageTask);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                            }
                }));

                try {
                    Thread.sleep(THREAD_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static class MessagePackRunnable implements Runnable{
        private QoSMessagePack messagePack;

        public MessagePackRunnable(QoSMessagePack messagePack){
            this.messagePack = messagePack;
        }

        @Override
        public void run() {
            messagePack.process();
            if(messagePack.getState() == QoSMessagePack.STATE.DONE)
                return;
            MqttSession session
                    = MqttSessionCache.get(messagePack.getClientId().value());
            if(session != null){
                session.addMessage(messagePack);
            }
        }
    }

    public static void put(final QoSMessagePack messagePack){
        fixThreadPool.execute(() -> {
            try {
                messageQueue.put(messagePack);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
