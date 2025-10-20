/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;

/**
 * SocialNetwork provides methods that operate on a social network.
 *
 * A social network is represented by a Map<String, Set<String>> where map[A] is
 * the set of people that person A follows on Twitter, and all people are
 * represented by their Twitter usernames. Users can't follow themselves. If A
 * doesn't follow anybody, then map[A] may be the empty set, or A may not even exist
 * as a key in the map; this is true even if A is followed by other people in the network.
 * Twitter usernames are not case sensitive, so "ernie" is the same as "ERNie".
 * A username should appear at most once as a key in the map or in any given
 * map[A] set.
 *
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class SocialNetwork {

    /**
     * Guess who might follow whom, from evidence found in tweets.
     *
     * @param tweets
     *            a list of tweets providing the evidence, not modified by this
     *            method.
     * @return a social network (as defined above) in which a user follows another
     *         if they @-mention them in a tweet.
     */
    public static Map<String, Set<String>> guessFollowsGraph(List<Tweet> tweets) {
        Map<String, Set<String>> graph = new HashMap<>();

        for (Tweet tweet : tweets) {
            String author = tweet.getAuthor().toLowerCase();
            Set<String> mentions = extractMentions(tweet.getText());

            // Remove self-mention if present
            mentions.remove(author);

            if (!mentions.isEmpty()) {
                graph.putIfAbsent(author, new HashSet<>());
                graph.get(author).addAll(mentions);
            }
        }

        return graph;
    }

    /**
     * Helper method to extract all @-mentioned usernames from a tweet text.
     * Converts usernames to lowercase.
     */
    private static Set<String> extractMentions(String text) {
        Set<String> mentions = new HashSet<>();
        String[] words = text.split("\\s+");

        for (String word : words) {
            if (word.startsWith("@") && word.length() > 1) {
                // Keep only alphanumeric and underscore usernames
                String mention = word.substring(1).replaceAll("[^a-zA-Z0-9_]", "").toLowerCase();
                if (!mention.isEmpty()) {
                    mentions.add(mention);
                }
            }
        }

        return mentions;
    }

    /**
     * Find the people in a social network who have the greatest influence, in
     * the sense that they have the most followers.
     *
     * @param followsGraph
     *            a social network (as defined above)
     * @return a list of all distinct Twitter usernames in followsGraph, in
     *         descending order of follower count.
     */
    public static List<String> influencers(Map<String, Set<String>> followsGraph) {
        Map<String, Integer> followerCount = new HashMap<>();

        // Count followers for each user
        for (Set<String> followedUsers : followsGraph.values()) {
            for (String user : followedUsers) {
                followerCount.put(user, followerCount.getOrDefault(user, 0) + 1);
            }
        }

        // Ensure all users appear in the count (even if they follow nobody)
        for (String user : followsGraph.keySet()) {
            followerCount.putIfAbsent(user, 0);
        }

        // Create a list and sort by follower count descending
        List<String> sortedUsers = new ArrayList<>(followerCount.keySet());
        sortedUsers.sort((a, b) -> followerCount.get(b) - followerCount.get(a));

        return sortedUsers;
    }
}
