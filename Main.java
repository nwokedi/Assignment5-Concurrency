package ca.nl.cna.java2.concurrency.rssfeedexample;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

//The Main class serves as the entry point for the RSS feed checker application.
// It reads RSS feed URLs from the user, starts multiple RSSFeedChecker threads to periodically check the feeds,
// and runs the program for a specified duration before shutting down gracefully.
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<String> feedUrls = new ArrayList<>();
        final String end = "done";
        String url = "";

        System.out.println("Enter RSS feed URLs (type 'done' to finish):");
        while (!url.equals(end)) {
            url = scanner.nextLine();
            if (!url.equalsIgnoreCase(end)) {
                feedUrls.add(url);
            }
        }
        //scanner.close();

        RSSFeedManager feedManager = new RSSFeedManager();//RSSFeedManager instance to manage the feed queue.
        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<RSSFeedChecker> checkers = new ArrayList<>();//Initializes an empty list checkers to store RSSFeedChecker instances

        for (int i = 0; i < 5; i++) {
            //Loops 5 times to create RSSFeedChecker objects
            // and add them to the checkers list.
            RSSFeedChecker checker = new RSSFeedChecker(feedManager);
            //checker.checkFeed();
            checkers.add(checker);
            executor.submit(checker);
        }

        // Add feeds to the manager
        for (String feedUrl : feedUrls) {
            try{
                feedManager.addFeed(feedUrl);
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Run checkers for 5 minutes
        try{
            Thread.sleep(300000);
            //Thread.sleep(120000);// 1 minute is 60,000
        }catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Loops through the checkers list and calls the stop
        //method on each checker
        for (RSSFeedChecker checker : checkers) {
            checker.stop();
        }
        //Shuts down the executor service
        executor.shutdown();
        //Wait for 60 seconds for all tasks in the thread pool to finish
        //If tasks are still running after 60 seconds, the service
        // is forcibly shut down using
        try{
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) { // If any tasks are still running after 60 seconds, the executor service is forced to shut down
                executor.shutdownNow();
            }
        }catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}
