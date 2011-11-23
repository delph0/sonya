package com.nhn.socialanalytics.twitter.collect;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.nhn.socialanalytics.common.JobLogger;
import com.nhn.socialanalytics.common.util.DateUtil;

public class TwitterDataCollectorJob implements Job {
	// logger
	private static JobLogger logger = JobLogger.getLogger(TwitterDataCollectorJob.class, "twitter-collect.log");
	
	public TwitterDataCollectorJob() { }
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			// get job data map
			JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();	
			String param = jobDataMap.getString("key");
			
			logger.info("@PARAM[key] == " + param);
			
			// start time
    		Date startTime = new Date();
			
			// This job simply prints out its job name and the
			// date and time that it is running
			String jobName = context.getJobDetail().getFullName();
			System.out.println("Quartz says: " + jobName + " executing at " + startTime);
			logger.info("Quartz says: " + jobName + " executing at " + startTime);
			
			String createStartDate = DateUtil.convertDateToString("yyyy-MM-dd", DateUtil.addDay(new Date(), -1));

			TwitterDataCollector collector = new TwitterDataCollector();
			
			// objects to collect
			Map<String, String> searchMap = new HashMap<String, String>();
			searchMap.put("gameshutdown", "게임셧다운제");
			searchMap.put("fta", "한미FTA OR ISD");
			searchMap.put("galaxynote", "갤럭시노트");
						
			for (Map.Entry<String, String> entry : searchMap.entrySet()) {
				String objectId = entry.getKey();
				String query = entry.getValue();
				
				List<twitter4j.Tweet> tweets = collector.searchTweets(objectId, query, createStartDate, null, 5);
				
				try {
					collector.writeOutput(objectId, tweets);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage(), e);
				}
			}
	    	
			// end time
			Date endTime = new Date();
			System.out.println("Quartz says: " + jobName + " finished at "	+ endTime);
			System.out.println("Collecting for twitter data from : " + startTime + " to: " + endTime);
			
			logger.info("Quartz says: " + jobName + " finished at "	+ endTime);
			logger.jobSummary("Collecting for twitter data", startTime, endTime);
			
	 	} catch (Exception e) {
	    	e.printStackTrace();
	    } 
	}	
}
