package org.louie.api.youtube;

import com.google.api.client.util.Key;

/**
 * This class is comment.
 * 
 * @author Younggue Bae
 */
public class Comment extends Item {

	@Key("content")
	String content;
	
	@Key("id")
	String id;
	
	@Key("author")
	Author author;
}
