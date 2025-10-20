/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class SocialNetworkTest {

    // --- Testing strategies ---
    /*
     * guessFollowsGraph() tests:
     * 1. Empty List of Tweets
     * 2. Tweets Without Mentions
     * 3. Single Mention
     * 4. Multiple Mentions
     * 5. Multiple Tweets from One User
     *
     * influencers() tests:
     * 6. Empty Graph
     * 7. Single User Without Followers
     * 8. Single Influencer
     * 9. Multiple Influencers
     * 10. Tied Influence
     */

    @Test(expected = AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // ensure assertions are enabled
    }

    // --- guessFollowsGraph() tests ---

    @Test
    public void testGuessFollowsGraphEmpty() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>());
        assertTrue("expected empty graph", followsGraph.isEmpty());
    }

    @Test
    public void testGuessFollowsGraphNoMentions() {
        List<Tweet> tweets = new ArrayList<>();
        tweets.add(new Tweet(1, "alice", "Hello world", Instant.now()));
        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(tweets);
        assertTrue("expected empty graph for tweets without mentions", graph.isEmpty());
    }

    @Test
    public void testGuessFollowsGraphSingleMention() {
        List<Tweet> tweets = new ArrayList<>();
        tweets.add(new Tweet(1, "alice", "@bob How are you?", Instant.now()));
        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(tweets);
        assertTrue(graph.containsKey("alice"));
        assertTrue(graph.get("alice").contains("bob"));
        assertEquals(1, graph.get("alice").size());
    }

    @Test
    public void testGuessFollowsGraphMultipleMentions() {
        List<Tweet> tweets = new ArrayList<>();
        tweets.add(new Tweet(1, "alice", "@bob @charlie Let's meet", Instant.now()));
        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(tweets);
        assertTrue(graph.containsKey("alice"));
        Set<String> follows = graph.get("alice");
        assertTrue(follows.contains("bob"));
        assertTrue(follows.contains("charlie"));
        assertEquals(2, follows.size());
    }

    @Test
    public void testGuessFollowsGraphMultipleTweetsSameUser() {
        List<Tweet> tweets = new ArrayList<>();
        tweets.add(new Tweet(1, "alice", "@bob How are you?", Instant.now()));
        tweets.add(new Tweet(2, "alice", "@charlie Hello!", Instant.now()));
        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(tweets);
        Set<String> follows = graph.get("alice");
        assertTrue(follows.contains("bob"));
        assertTrue(follows.contains("charlie"));
        assertEquals(2, follows.size());
    }

    // --- influencers() tests ---

    @Test
    public void testInfluencersEmpty() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        assertTrue("expected empty influencer list", influencers.isEmpty());
    }

    @Test
    public void testInfluencersSingleUserNoFollowers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("alice", new HashSet<>());
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        assertEquals(1, influencers.size());
        assertEquals("alice", influencers.get(0));
    }

    @Test
    public void testInfluencersSingleInfluencer() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        Set<String> aliceFollows = new HashSet<>();
        aliceFollows.add("bob");
        followsGraph.put("alice", aliceFollows);
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        assertEquals("bob should be first influencer", "bob", influencers.get(0));
    }

    @Test
    public void testInfluencersMultipleInfluencers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("alice", Set.of("bob", "charlie"));
        followsGraph.put("dave", Set.of("charlie"));
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        assertEquals("charlie should be first influencer", "charlie", influencers.get(0));
        assertEquals("bob should be second influencer", "bob", influencers.get(1));
    }

    @Test
    public void testInfluencersTiedInfluence() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("alice", Set.of("bob", "charlie"));
        followsGraph.put("dave", Set.of("bob", "charlie"));
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        assertTrue(influencers.contains("bob"));
        assertTrue(influencers.contains("charlie"));
        assertEquals(2, influencers.size());
    }
}
