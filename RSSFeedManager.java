package ca.nl.cna.java2.concurrency.rssfeedexample;

import java.util.LinkedList;
import java.util.Queue;

//The RSSFeedManager class manages a queue of RSS feeds
//The class ensures thread safety through synchronization
public class RSSFeedManager {
    private static final int MAX_FEED_SIZE = 100; //constant defining the maximum number of feeds the queue can hold.
    private final Queue<String> feedQueue = new LinkedList<String>();//A LinkedList used as a queue to store RSS feeds.

    // Adds a feed to the queue.
    //addFeed uses wait() when the queue is full.
    //This allows the thread to wait until another thread
    //removes a feed from the queue, freeing up space.
    //notifyAll() to wake up all waiting threads after adding feed.
    public synchronized void addFeed(String feed) throws InterruptedException {
        if (feed == null || feed.isEmpty()) {
            throw new IllegalArgumentException("Feed cannot be null or empty");
        }
        while (feedQueue.size() == MAX_FEED_SIZE) {
            wait(); //Waits if the queue is full
        }
        feedQueue.add(feed);
        notifyAll();
    }

    //Gets a feed from the queue
    //getFeed uses wait() when the queue is empty.
    // This makes the thread wait until another
    // thread adds a feed to the queue.
    //notifyAll() to wake up all waiting threads after adding or removing a feed.
    public synchronized String getFeed() throws InterruptedException {
        //while (feedQueue.size() == 0) {
        while (feedQueue.isEmpty()) {
            wait();//Causes the current thread to wait until another thread invokes the notifyAll()
        }
        String feed = feedQueue.poll();//Removes and returns the first feed from the queue.
        notifyAll();//Wakes up all threads that are waiting on the same object.
        return feed;
        }

}
