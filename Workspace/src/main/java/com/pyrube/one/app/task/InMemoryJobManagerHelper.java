/*******************************************************************************
 * Copyright 2019, 2023 Aranjuez Poon.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.pyrube.one.app.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.pyrube.one.app.AppException;
import com.pyrube.one.app.logging.Logger;
import com.pyrube.one.lang.Strings;

/**
 * the <code>InMemoryJobManagerHelper</code> is non-persistent implementation for 
 * <code>JobManagerHelper</code>
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class InMemoryJobManagerHelper extends JobManagerHelper {

	/**
	 * logger
	 */
	private static Logger logger = Logger.getInstance(InMemoryJobManagerHelper.class.getName());
	
	/**
	 * job queue groups and their respective job queues
	 */
	private static List<JobQueueGroup> jobQueueGroups = new ArrayList<JobQueueGroup>();
	private static Map<String, List<JobQueue>> groupedJobQueues = new HashMap<String, List<JobQueue>>();

	/**
	 * @see com.pyrube.one.app.task.JobManagerHelper#init(java.util.Map)
	 */
	@Override
	public void init(Map<String, ?> params) { }

	/**
	 * @see com.pyrube.one.app.task.JobManagerHelper#findRootJobGroups()
	 */
	@Override
	public List<JobGroup> findRootJobGroups() throws AppException {
		Map<String, List<JobGroup>> timezoneJobGroups = TaskConfig.getTaskConfig().getTimezoneJobGroups();
		Set<String> timezoneIds = timezoneJobGroups.keySet();
		List<JobGroup> rootJobGroups = new ArrayList<JobGroup>();
		for (String timezoneId : timezoneIds) {
			List<JobGroup> jobGroups = timezoneJobGroups.get(timezoneId);
			for (JobGroup jobGroup : jobGroups) {
				if (jobGroup.dependsOnNothing()) rootJobGroups.add(jobGroup);
			}
		}
		return rootJobGroups;
	}
	
	/**
	 * @see com.pyrube.one.app.task.JobManagerHelper#findNextJobGroups(java.lang.String, java.util.Date)
	 */
	public List<JobGroup> findNextJobGroups(String jobQueueGroupId, Date runDate) throws AppException {
		JobQueueGroup jobQueueGroup = findJobQueueGroup(jobQueueGroupId);
		String jobGroupName = jobQueueGroup.getJobGroupName();
		String timezoneId = jobQueueGroup.getTimezoneId();
		Map<String, List<JobGroup>> timezoneJobGroups = TaskConfig.getTaskConfig().getTimezoneJobGroups();
		List<JobGroup> jobGroups = timezoneJobGroups.get(timezoneId);
		List<JobGroup> nextJobGroups = new ArrayList<JobGroup>();
		for (JobGroup jobGroup : jobGroups) {
			if (jobGroup.dependsOn(jobGroupName)) nextJobGroups.add(jobGroup);
		}
		return nextJobGroups;
	}

	/**
	 * @see com.pyrube.one.app.task.JobManagerHelper#findJobGroup(java.lang.String, java.lang.String)
	 */
	@Override
	public JobGroup findJobGroup(String jobGroupName, String timezoneId) throws AppException {
		Map<String, List<JobGroup>> timezoneJobGroups = TaskConfig.getTaskConfig().getTimezoneJobGroups();
		List<JobGroup> jobGroups = timezoneJobGroups.get(timezoneId);
		for (JobGroup jobGroup : jobGroups) {
			if (jobGroup.getName().equals(jobGroupName)) return(jobGroup);
		}
		return null;
	}

	/**
	 * @see com.pyrube.one.app.task.JobManagerHelper#startJobGroup(com.pyrube.one.app.task.JobGroup, java.util.Date, java.util.Date)
	 */
	@Override
	public String startJobGroup(JobGroup jobGroup, Date runDate, Date startTime) throws AppException {
		JobQueueGroup jobQueueGroup = new JobQueueGroup();
		jobQueueGroup.setId(UUID.randomUUID().toString());
		jobQueueGroup.setJobGroupName(jobGroup.getName());
		jobQueueGroup.setTimezoneId(jobGroup.getTimezoneId());
		jobQueueGroup.setRunDate(runDate);
		jobQueueGroup.setRunStatus("R");
		jobQueueGroup.setStartTime(startTime);
		
		List<JobQueue> jobQueues = new ArrayList<JobQueue>();
		for (Job job : jobGroup.getJobs()) {
			JobQueue jobQueue = new JobQueue();
			jobQueue.setId(UUID.randomUUID().toString());
			jobQueue.setGroupId(jobQueueGroup.getId());
			jobQueue.setJobGroupName(jobGroup.getName());
			jobQueue.setJobName(job.getName());
			jobQueue.setTimezoneId(job.getTimezoneId());
			jobQueue.setJobClass(job.getClassName());
			jobQueue.setJobParams(job.getParamsXml());
			jobQueue.setRunDate(runDate);
			jobQueue.setStartTime(startTime);
			jobQueue.setJobStatus("P");
			jobQueues.add(jobQueue);
		}
		jobQueueGroups.add(jobQueueGroup);
		groupedJobQueues.put(jobQueueGroup.getId(), jobQueues);
		return jobQueueGroup.getId();
	}

	/**
	 * end a given running job group
	 * @param jobQueueGroupId
	 * @throws AppException
	 */
	public void endJobGroup(String jobQueueGroupId) throws AppException {
		boolean hasError = false;
		List<JobQueue> jobQueues = groupedJobQueues.get(jobQueueGroupId);
		for (JobQueue jobQueue : jobQueues) {
			if ("E".equals(jobQueue.getJobStatus())) {
				hasError = true;
				break;
			}
		}
		JobQueueGroup jobQueueGroup = findJobQueueGroup(jobQueueGroupId);
		jobQueueGroup.setRunStatus(hasError ? "E" : "F");
	}

	/**
	 * @see com.pyrube.one.app.task.JobManagerHelper#findRootJobs(long)
	 */
	@Override
	public List<Job> findRootJobs(String jobQueueGroupId) throws AppException {
		List<Job> rootJobs = new ArrayList<Job>();
		List<JobQueue> jobQueues = groupedJobQueues.get(jobQueueGroupId);
		for (JobQueue jobQueue : jobQueues) {
			if (Strings.isEmpty(jobQueue.getDependJob()) && "P".equals(jobQueue.getJobStatus())) {
				Job job = buildJob(jobQueue);
				rootJobs.add(job);
			}
		}
		return rootJobs;
	}

	/**
	 * @see com.pyrube.one.app.task.JobManagerHelper#findNextJobs(String, String)
	 */
	@Override
	public List<Job> findNextJobs(String jobQueueId, String jobQueueGroupId) throws AppException {
		String jobName = this.findJobQueue(jobQueueId, jobQueueGroupId).getJobName();
		List<JobQueue> jobQueues = groupedJobQueues.get(jobQueueGroupId);
		List<Job> jobs = new ArrayList<Job>();
		for (JobQueue jobQueue : jobQueues) {
			if (jobName.equals(jobQueue.getDependJob()) && "P".equals(jobQueue.getJobStatus())) {
				Job job = buildJob(jobQueue);
				jobs.add(job);
			}
		}
		return jobs;
	}

	/**
	 * @see com.pyrube.one.app.task.JobManagerHelper#countUnfinishedJobs(String)
	 */
	@Override
	public int countUnfinishedJobs(String jobQueueGroupId) throws AppException {
		int count = 0;
		List<JobQueue> jobQueues = groupedJobQueues.get(jobQueueGroupId);
		for (JobQueue jobQueue : jobQueues) {
			if ("P".equals(jobQueue.getJobStatus()) || "R".equals(jobQueue.getJobStatus())) {
				count++;
			}
		}
		return count;
	}

	/**
	 * @see com.pyrube.one.app.task.JobManagerHelper#findSchedule(java.lang.String, java.lang.String)
	 */
	@Override
	public Schedule findSchedule(String jobGroupName, String timezoneId) throws AppException {
		JobGroup jobGroup = findJobGroup(jobGroupName, timezoneId);
		return (jobGroup != null ? jobGroup.getSchedule() : null);
	}
	
	/**
	 * @see com.pyrube.one.app.task.JobManagerHelper#findJobQueueGroup(java.lang.String)
	 */
	@Override
	public JobQueueGroup findJobQueueGroup(String jobQueueGroupId) throws AppException {
		for (JobQueueGroup jobQueueGroup : jobQueueGroups) {
			if (jobQueueGroupId.equals(jobQueueGroup.getId())) {
				return jobQueueGroup;
			}
		}
		return null;
	}

	/**
	 * @see com.pyrube.one.app.task.JobManagerHelper#findJobQueue(String, String)
	 */
	@Override
	public JobQueue findJobQueue(String jobQueueId, String jobQueueGroupId) throws AppException {
		List<JobQueue> jobQueues = groupedJobQueues.get(jobQueueGroupId);
		for (JobQueue jobQueue : jobQueues) {
			if (jobQueue.getGroupId().equals(jobQueueGroupId)) {
				return jobQueue;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.pyrube.one.app.task.JobManagerHelper#retrieveJobQueueResult(String)
	 */
	@Override
	public RunResult retrieveJobQueueResult(String jobQueueId) throws AppException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.pyrube.one.app.task.JobManagerHelper#logJobProgress(String, String, com.pyrube.one.app.task.RunResult)
	 */
	@Override
	public void logJobProgress(String jobQueueId, String jobQueueGroupId, RunResult rr) throws AppException {
		JobQueue jobQueue = this.findJobQueue(jobQueueId, jobQueueGroupId);
		if (jobQueue == null) return;
		jobQueue.setJobStatus(rr.getStatus());
		jobQueue.setNextStep(rr.getNextStep());
		jobQueue.addItemsTotal(rr.getMessage().getItemsTotal())
			.addItemsFinished(rr.getMessage().getItemsFinished())
			.addItemsError(rr.getMessage().getItemsError());
	}

}
