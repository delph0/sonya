/*
 * Copyright (c) 2009-2010, Young-Gue Bae
 * All rights reserved.
 */
package org.firebird.collector.twitter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.firebird.io.model.Edge;
import org.firebird.io.model.UserBlogEntry;
import org.firebird.io.model.Vertex;
import org.firebird.io.service.EdgeManager;
import org.firebird.io.service.UserBlogEntryManager;
import org.firebird.io.service.VertexManager;
import org.firebird.io.service.impl.EdgeManagerImpl;
import org.firebird.io.service.impl.UserBlogEntryManagerImpl;
import org.firebird.io.service.impl.VertexManagerImpl;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

/**
 * A collector for twitter data by twitter4j open API.
 * 
 * @author Young-Gue Bae
 */
public class TwitterDataCollector {

	private int myId = 50900875;
	private Twitter twitter = null;
	private boolean dbStorageMode = false;
	private boolean collectFriend = false;
	private boolean collectFollower = false;
	private boolean collectUserBlogEntry = false;
	private int limitLevel = 1;
	private int limitPeople = 999999999;
	private int limitDegree = 1;
	private int numFriends = 0;
	private int numFollowers = 0;

	private HashMap<String, Vertex> vertices = new HashMap<String, Vertex>();
	private HashMap<String, Edge> edges = new HashMap<String, Edge>();

	private VertexManager vertexManager;
	private EdgeManager edgeManager;
	private UserBlogEntryManager userBlogEntryManager;
	
	private String baseUrl = null;

	/**
	 * Constructor.
	 * 
	 */
	public TwitterDataCollector() throws Exception {
		TwitterOAuthSupport twitterSupport = new TwitterOAuthSupport();
		twitter = twitterSupport.access(myId);
		baseUrl = twitter.getBaseURL();
		vertexManager = new VertexManagerImpl();
		edgeManager = new EdgeManagerImpl();
		userBlogEntryManager = new UserBlogEntryManagerImpl();
	}

    /**
     * Sets true if stores data into database.
     *
     * @param dbStorageMode true if stores into database
     */
	public void setDBStorageMode(boolean dbStorageMode) {
		this.dbStorageMode = dbStorageMode;
	}
	
    /**
     * Sets if collects the friend(following) relationship or not.
     *
     * @param collectFriend true if collects
     */
	public void setCollectFriendRelationship(boolean collectFriend) {
		this.collectFriend = collectFriend;
	}

    /**
     * Sets if collects the follower relationship or not.
     *
     * @param collectFollower true if collects
     */
	public void setCollectFollowerRelationship(boolean collectFollower) {
		this.collectFollower = collectFollower;
	}
	
    /**
     * Sets if collects the user blog entries or not.
     *
     * @param collectUserBlogEntry true if collects
     */
	public void setCollectUserBlogEntry(boolean collectUserBlogEntry) {
		this.collectUserBlogEntry = collectUserBlogEntry;
	}

    /**
     * Sets the limit level.
     *
     * @param limitLevel the limit level
     */
	public void setLevelLimit(int limitLevel) {
		this.limitLevel = limitLevel;
	}

    /**
     * Sets the limit number of people.
     *
     * @param limitPeople the number of people
     */
	public void setPeopleLimit(int limitPeople) {
		this.limitPeople = limitPeople;
	}

    /**
     * Sets the limit degree.
     *
     * @param limitDegree the limit degree
     */
	public void setDegreeLimit(int limitDegree) {
		this.limitDegree = limitDegree;
	}

    /**
     * Collects the social network data from twitter.
     *
     * @param screenName the twitter user's screenName
     */
	public void collectSocialNetwork(String screenName) {
		System.out.println(">>>>>>>>>>>>>> collector option : ");
		System.out.println("database storage mode == " + dbStorageMode);
		System.out.println("collect friend(following) relationship == " + collectFriend);
		System.out.println("collect follower relationship == " + collectFriend);
		System.out.println("level limit == " + limitLevel);
		System.out.println("people limit == " + limitPeople);

		User user = null;

		try {
			user = twitter.showUser(screenName);

			System.out.println("rate limit remaining  == " + user.getRateLimitRemaining() + " / " + user.getRateLimitLimit());
			System.out.println(">>>>>>>>>>>>>> start");

			if (collectFriend)
				this.collectFriendsOfUser(user, 0);

			if (collectFollower)
				this.collectFollowersOfUser(user, 0);
		} catch (TwitterException te) {
			te.printStackTrace();
		}

		System.out.println(">>>>>>>>>>>>>> result");
		System.out.println("rate limit remaining  == " + user.getRateLimitRemaining());
		System.out.println("friends == " + numFriends);
		System.out.println("followers == " + numFollowers);
		System.out.println("vertices == " + vertices.size());
		System.out.println("edges == " + edges.size());
	}
	
    /**
     * Gets the vertices from the memory storage.
     *
     * @return List<Vertex> the vertex list
     */
	public List<Vertex> getVertices() {
		Collection<Vertex> c = vertices.values();
		List<Vertex> v = new ArrayList<Vertex>(c);
		return v;
	}
	
    /**
     * Gets the edges from the memory storage.
     *
     * @return List<Edge> the edge list
     */
	public List<Edge> getEdges() {
		Collection<Edge> c = edges.values();
		List<Edge> e = new ArrayList<Edge>(c);
		return e;
	}

	private void collectFriendsOfUser(User user, int level) {
		try {
			if (level < limitLevel && numFriends <= limitPeople) {
				// add vertex
				addVertex(user);

				List<User> friends = twitter.getFriendsStatuses(String.valueOf(user.getId()));
				
				level++;
				for (int i = 0; i < friends.size(); i++) {
					// level > 1 -> collect everyone for an initial user whatever degree limit
					if (level > 1 && i == limitDegree)	
						break;

					numFriends++;
					User friend = (User) friends.get(i);

					// add vertex
					addVertex(friend);

					// add edge
					addEdge(user, friend);

					// recursive call
					if (collectFriend)
						this.collectFriendsOfUser(friend, level);
					if (collectFollower)
						this.collectFollowersOfUser(friend, level);
				}
			}
		} catch (TwitterException te) {
			te.printStackTrace();
		}
	}

	private void collectFollowersOfUser(User user, int level) {
		try {
			if (level < limitLevel && numFollowers <= limitPeople) {
				// add vertex
				addVertex(user);

				List<User> followers = twitter.getFollowersStatuses(String.valueOf(user.getId()));

				level++;
				for (int i = 0; i < followers.size(); i++) {
					// level > 1 -> collect everyone for an initial user whatever degree limit
					if (level > 1 && i == limitDegree)
						break;

					numFollowers++;
					User follower = (User) followers.get(i);

					// add vertex
					addVertex(follower);

					// add edge
					addEdge(follower, user);

					// recursive call
					if (collectFriend)
						this.collectFriendsOfUser(follower, level);
					if (collectFollower)
						this.collectFollowersOfUser(follower, level);
				}				
			}
		} catch (TwitterException te) {
			te.printStackTrace();
		}
	}

	private void addVertex(User user) {
		String key = String.valueOf(user.getId());

		if (!vertices.containsKey(key)) {
			Vertex vertex = this.makeVertex(user);
			vertices.put(key, vertex);
			
			if (dbStorageMode == true) {
				vertexManager.deleteVertex(vertex);
				vertexManager.addVertex(vertex);
			
				// collect user blog entries
				if (collectUserBlogEntry) {
					try {
						this.addUserBlogEntries(twitter.getUserTimeline(user.getScreenName()));
					} catch (TwitterException te) {
						te.printStackTrace();
					}				
				}
			}
		}
	}

	private void addEdge(User user1, User user2) {
		String key = String.valueOf(user1.getId()) + "->" + String.valueOf(user2.getId());

		if (!edges.containsKey(key)) {
			Edge edge = this.makeEdge(user1, user2);
			edges.put(key, edge);
			System.out.println(user1.getScreenName() + " -> " + user2.getScreenName());
			
			if (dbStorageMode == true) {
				edgeManager.deleteEdge(edge);
				edgeManager.addEdge(edge);
			}
		}
	}
	
	private void addUserBlogEntries(List<Status> statuses) {
		for (int i = 0; i < statuses.size(); i++) {
			Status status = (Status)statuses.get(i);
			UserBlogEntry userBlogEntry = makeUserBlogEntry(status);
			
			// delete already-exist user blog entries
			userBlogEntryManager.deleteUserBlogEntry(userBlogEntry.getWebsiteId(), userBlogEntry.getUserId());
			// add user blog entry
			userBlogEntryManager.addUserBlogEntry(userBlogEntry);
		}		
	}

	private Vertex makeVertex(User user) {
		Vertex vertex = new Vertex();

		vertex.setWebsiteId(1);
		vertex.setId(user.getScreenName());
		vertex.setNo(user.getId());
		vertex.setName(user.getName());
		//vertex.setColor();
		//vertex.setShape();
		//vertex.setSize();
		//vertex.setOpacity();
		vertex.setImageFile((user.getProfileImageURL() == null) ? null : user
				.getProfileImageURL().toString());
		vertex.setInDegree(user.getFollowersCount());
		vertex.setOutDegree(user.getFriendsCount());
		//vertex.setBetweenessCentrality();
		//vertex.setClosenessCentrality();
		//vertex.setEigenvectorCentrality();
		//vertex.setClusteringCoefficient();
		vertex.setFriendsCount(user.getFriendsCount());
		vertex.setFollowersCount(user.getFollowersCount());
		vertex.setUserNo(user.getId());
		vertex.setUserId(user.getScreenName());
		vertex.setUserName(user.getName());
		vertex.setUserUrl(baseUrl + user.getScreenName());
		vertex.setBlogEntryCount(user.getStatusesCount());
		vertex.setLastBlogEntryId(String.valueOf(user.getStatusId()));
		vertex.setLastBlogEntryBody(user.getStatusText());
		vertex.setLastBlogEntryType("1");
		vertex.setLastBlogEntryCreateDate(user.getStatusCreatedAt());
		vertex.setLastBlogEntryReplyTo(user.getStatusInReplyToScreenName());
		//vertex.setLastBlogEntryDmTo();
		//vertex.setLastBlogEntryReferFrom();
		vertex.setCreateDate(user.getCreatedAt());
		vertex.setLastUpdateDate(user.getCreatedAt());

		return vertex;
	}
	
	private Edge makeEdge(User user1, User user2) {
		Edge edge = new Edge();
		
		edge.setWebsiteId1(1);
		edge.setWebsiteId2(1);
		edge.setVertex1(user1.getScreenName());
		edge.setVertex2(user2.getScreenName());
		edge.setVertexNo1(user1.getId());
		edge.setVertexNo2(user2.getId());
		//edge.setColor();
		//edge.setWidth();
		//edge.setOpacity();
		edge.setDirected(true);
		edge.setRelationship("1");
		//edge.setCloseness();
		//edge.setReplyCount();
		//edge.setDmCount();
		//edge.setReferCount();
		//edge.setLastReplyDate();
		//edge.setLastDmDate();
		//edge.setLastReferDate();
		//edge.setCreateDate();
		//edge.setLastUpdateDate();
		
		return edge; 
	}	 
	
	private UserBlogEntry makeUserBlogEntry(Status status) {
		UserBlogEntry userBlogEntry = new UserBlogEntry();

		userBlogEntry.setWebsiteId(1);
		userBlogEntry.setUserId(status.getUser().getScreenName());
		userBlogEntry.setUserNo(status.getUser().getId());
		userBlogEntry.setBlogEntryId(String.valueOf(status.getId()));
		userBlogEntry.setTitle(status.getSource());
		userBlogEntry.setBody(status.getText());
		userBlogEntry.setSourceWebsiteId(1);
		userBlogEntry.setBlogEntryType("1");
		userBlogEntry.setPermaLinkUrl(null);
		userBlogEntry.setUserLinkUrl(null);
		userBlogEntry.setReplyTo(status.getInReplyToScreenName());
		userBlogEntry.setDmTo(null);
		userBlogEntry.setReferFrom(null);
		userBlogEntry.setCreateDate(status.getCreatedAt());
		userBlogEntry.setLastUpdateDate(status.getCreatedAt());
		
		return userBlogEntry;
	}
}
