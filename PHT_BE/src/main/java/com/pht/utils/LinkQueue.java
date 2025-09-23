package com.pht.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class LinkQueue<T>
{
    private static final Logger logger = LoggerFactory.getLogger(LinkQueue.class);
    private int maxQueueSize = 1000000;
    private ConcurrentLinkedQueue<T> queueData = new ConcurrentLinkedQueue<T>();
    private AtomicInteger miQueueSize = new AtomicInteger();
    private Object mutex;
    private String mstrQueueName = "Link Queue";
    private Set<T> enqueuedItems = new HashSet<>();

    private boolean mbLocked = false;

    public LinkQueue()
    {
        mutex = this;
    }

    public LinkQueue(int maxSize,String strQueueName)
    {
        mstrQueueName = strQueueName;
        maxQueueSize = maxSize;
        mutex = this;
    }
    /**
     *
     * Appends an element to the end of the queue. If the queue
     *
     * has set limit on maximum elements and there is already specified
     *
     * max count of elements in the queue throws IndexOutOfBoundsException.
     *
     * notify to all waiting object
     */
    public void enqueueNotify(T objMsg)
    {
        if (!mbLocked)
        {
            if ((maxQueueSize > 0) && (miQueueSize.intValue() >= maxQueueSize))
            {
                System.out.println("Queue is full. Element not added. Queue name ("+mstrQueueName+")");
                return;
            }

            synchronized (queueData)
            {
                queueData.add(objMsg);
                enqueuedItems.add(objMsg);
//          if(objMsg instanceof Subscriber)
//          System.out.println("CommandId in Queue :"+((Subscriber)objMsg).getCommandId());
            }
            miQueueSize.incrementAndGet();

            synchronized (mutex)
            {
                mutex.notifyAll();
            }
        }
    }

    public T dequeueWait(int iSecondTimeout)
    {
        T objMsg = objMsg = poolFirstSync();

        if (objMsg == null)
        {
            for (int i = 0; i < iSecondTimeout; i++)
            {
                synchronized (mutex)
                {
                    try
                    {
                        mutex.wait(20);
                    }
                    catch (InterruptedException e)
                    {
                        Thread.currentThread().interrupt();
                    }
                }
                objMsg = poolFirstSync();

                if (objMsg != null)
                {
                    miQueueSize.decrementAndGet();
                    return objMsg;
                }
            }
        }
        if (objMsg != null)
        {
            miQueueSize.decrementAndGet();
        }

        return objMsg;
    }


    private T poolFirstSync()
    {
        synchronized (queueData)
        {
            try
            {
                return queueData.poll();
            }
            catch (Exception ex)
            {
                logger.error(ex.getMessage(), ex);
                return null;
            }
        }
    }

    /**
     *
     * Current count of the elements in the queue.
     */
    public int getSize()
    {
        return miQueueSize.intValue();
    }

    /**
     *
     * If there is no element in the queue.
     */
    public boolean isEmpty()
    {
        synchronized (mutex)
        {
            return queueData.isEmpty();
        }
    }

    public void notify2Closed()
    {
        synchronized (mutex)
        {
            mutex.notifyAll();
        }
    }

    public void setLock(boolean bLocked)
    {
        mbLocked = bLocked;
    }

    public void clear()
    {
        synchronized (queueData)
        {
            queueData.clear();
            miQueueSize.set(0);
        }
    }
    public boolean isEnqueued(T objMsg) {
        return enqueuedItems.contains(objMsg);
    }
}
