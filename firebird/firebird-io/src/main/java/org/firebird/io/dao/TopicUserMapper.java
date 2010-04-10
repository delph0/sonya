/*
 * Copyright (c) 2009-2010, Young-Gue Bae
 * All rights reserved.
 */
package org.firebird.io.dao;

import java.util.List;

import org.firebird.common.ibatis.GenericMapper;
import org.firebird.io.model.TopicUser;

/**
 * A interface for topic user mapper.
 * 
 * @author Young-Gue Bae
 */
public interface TopicUserMapper extends GenericMapper {

    /**
     * Selects topic users.
     *
     * @param param the topic user
     * @return List<TopicUser> the list of topic user
     */
	public List<TopicUser> selectUsers(TopicUser param);
	
	/**
     * Inserts a topic user.
     *
     * @param topicUser the topic user
     */
	public void insertUser(TopicUser topicUser);
	
	/**
     * Deletes topic users.
     *
     * @param websiteId the website id
     */
	public void deleteUsers(int websiteId);
	
	/**
     * Selects topic users in the specific cluster.
     *
     * @param param the topic user
     * @return List<TopicUser> the list of topic user
     */
	public List<TopicUser> selectUsersInCluster(TopicUser param);
	
}
