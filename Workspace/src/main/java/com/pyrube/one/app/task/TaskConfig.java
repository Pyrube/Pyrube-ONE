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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.pyrube.one.app.AppException;
import com.pyrube.one.app.Apps;
import com.pyrube.one.app.cache.CacheConfig;
import com.pyrube.one.app.config.ConfigManager;
import com.pyrube.one.app.config.Configurator;
import com.pyrube.one.app.logging.Logger;
import com.pyrube.one.lang.Strings;

/**
 * Task management configurator
 * <pre>
 * 		<TaskConfig>
 * 			<manager>
 * 				<helper class="com.pyrube.one.app.task.DefaultJobManagerHelper">
 * 				</helper>
 * 				<runnerPool>native</runnerPool>
 * 				<runningYears>10</runningYears>
 * 				<runningCount>50</runningCount>
 * 			</manager>
 * 			<timerTask>
 * 				<!-- global timer tasks will run at each timezone as below. -->
 * 				<global>
 * 					<!-- Start of Day -->
 * 					<jobGroup name="SOD" enabled="true" scheduleType="N" years="" months="" days="1-LAST" weekdays="" hours="0" minutes="1" period="" periodStart="2013-01-01" dependencies="">
 * 						<job name="SAMPLE" class="com.pyrube.one.app.task.DummyJobable" dependencies="" />
 * 					</jobGroup>
 * 					<!-- End of Day -->
 * 					<jobGroup name="EOD" enabled="true" scheduleType="N" years="" months="" days="1-LAST" weekdays="" hours="23" minutes="59" period="" periodStart="2013-01-01" dependencies="">
 * 						<job name="EXP_FOOD" class="com.pyrube.hoso.tasks.FoodExpiriedJobable" dependencies="" />
 * 					</jobGroup>
 * 				</global>
 * 				<!-- timer tasks will run at the respective timezone as below. -->
 * 				<timezone id="Asia/Shanghai"></timezone>
 * 				<timezone id="America/New_York"></timezone>
 * 			</timerTask>
 * 		</TaskConfig>
 * </pre>
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class TaskConfig extends Configurator {

	/**
	 * logger
	 */
	private static Logger logger = Apps.a.logger.named(TaskConfig.class.getName());

	/**
	 * the configurator
	 */
	private static TaskConfig taskConfig = null;

	/**
	 * job manager helper class
	 */
	private String managerHelperClass = null;
	private Map<String, ?> managerHelperParams = null;
	
	/**
	 * job runner pool name
	 */
	private String runnerPoolName = null;
	
	/**
	 * how many active running years
	 */
	private int runningYears = 10;
	
	/**
	 * how many active running count
	 */
	private int runningCount = 50;
	
	/**
	 * global job groups
	 */
	private List<JobGroup> globalJobGroups = null;
	
	/**
	 * job groups for each timezone. {key: timezoneId, value: list of job groups}
	 */
	private Map<String, List<JobGroup>> timezoneJobGroups = null;
	
	/**
	 * return the task configurator
	 */
	public static TaskConfig getTaskConfig() {
		if (taskConfig == null) {
			synchronized(CacheConfig.class) {
				if (taskConfig == null) {
					TaskConfig cfg = (TaskConfig) getInstance("TaskConfig");
					if (cfg == null) logger.warn("configurator named " + "TaskConfig" + " is not found. please check configuration file.");
					taskConfig = cfg;
				}
			}
		}
		return (taskConfig);
	}

	/**
	 * @return the helperClass
	 */
	public String getManagerHelperClass() {
		return managerHelperClass;
	}

	/**
	 * @return the helperParams
	 */
	public Map<String, ?> getManagerHelperParams() {
		return managerHelperParams;
	}

	/**
	 * @return the runnerPoolName
	 */
	public String getRunnerPoolName() {
		return runnerPoolName;
	}

	/**
	 * @return the runningYears
	 */
	public int getRunningYears() {
		return runningYears;
	}

	/**
	 * @return the runningCount
	 */
	public int getRunningCount() {
		return runningCount;
	}

	/**
	 * @return the globalJobGroups
	 */
	public List<JobGroup> getGlobalJobGroups() {
		return globalJobGroups;
	}

	/**
	 * @return the timezoneJobGroups
	 */
	public Map<String, List<JobGroup>> getTimezoneJobGroups() {
		return timezoneJobGroups;
	}
	
	/**
	 * return job groups for a given timezone id
	 * @param timezoneId
	 * @return
	 */
	public List<JobGroup> getTimezoneJobGroups(String timezoneId) {
		return timezoneJobGroups.get(timezoneId);
	}

	/**
	 * load cache configuration
	 * @param cfgName the config name
	 * @param cfgNode the configuration Node
	 * @see Configurator#loadConfig(String, Node)
	 */
	public void loadConfig(String cfgName, Node cfgNode) throws AppException {
		// manager
		obtainManager(ConfigManager.getNode(cfgNode, "manager"));
		
		// timer task
		obtainTimerTask(ConfigManager.getNode(cfgNode, "timerTask"));
		
	}
	
	/**
	 * obtain job manager
	 * @param ctx
	 * @throws AppException
	 */
	private void obtainManager(Node ctx) throws AppException {
		Element helperElem = (Element) ConfigManager.getNode(ctx, "helper");
		if (helperElem != null) {
			managerHelperClass = ConfigManager.getAttributeValue(helperElem, "class");
			managerHelperParams = ConfigManager.getDeepParams(helperElem);
		}
		
		runnerPoolName = ConfigManager.getSingleValue(ctx, "runnerPool");
		
		String tmp = ConfigManager.getSingleValue(ctx, "runningYears");
		if (!Strings.isEmpty(tmp)) {
			try {
				runningYears = Integer.parseInt(tmp);
			} catch (Exception e) {
				logger.warn("Invalid value (" + tmp + ") for runningYears. Default value will be used.");
			}
		}
		tmp = ConfigManager.getSingleValue(ctx, "runningCount");
		if (!Strings.isEmpty(tmp)) {
			try {
				runningCount = Integer.parseInt(tmp);
			} catch (Exception e) {
				logger.warn("Invalid value (" + tmp + ") for runningCount. Default value will be used.");
			}
		}
	}
	
	/**
	 * obtain timer task
	 * @param ctx
	 * @throws AppException
	 */
	private void obtainTimerTask(Node ctx) throws AppException {
		// global job group
		Element globalElem = (Element) ConfigManager.getNode(ctx, "global");
		NodeList globalJobGroupNl = null;
		if (globalElem != null) {
			globalJobGroupNl = ConfigManager.getNodeList(globalElem, "jobGroup");
			globalJobGroups = buildJobGroups(globalJobGroupNl, null, null);
		}
		// job groups at timezone
		NodeList timezoneNl = ConfigManager.getNodeList(ctx, "timezone");
		if (timezoneNl != null && timezoneNl.getLength() > 0) {
			timezoneJobGroups = new HashMap<String, List<JobGroup>>();
			for (int i = 0; i < timezoneNl.getLength(); ++i) {
				Element timezoneElem = (Element) timezoneNl.item(i);
				String timezoneId = ConfigManager.getAttributeValue(timezoneElem, "id");
				NodeList jobGroupNl = ConfigManager.getNodeList(globalElem, "jobGroup");
				List<JobGroup> jobGroups = buildJobGroups(jobGroupNl, timezoneId, null);
				Set<String> existingJobGroupNames = new TreeSet<String>();
				if (jobGroups != null && jobGroups.size() > 0) {
					for (JobGroup jobGroup : jobGroups) existingJobGroupNames.add(jobGroup.getName());
				}
				List<JobGroup> globalJobGroups = buildJobGroups(globalJobGroupNl, timezoneId, existingJobGroupNames);
				jobGroups.addAll(globalJobGroups);
				timezoneJobGroups.put(timezoneId, jobGroups);
			}
		}
	}
	
	/**
	 * build job groups
	 * @param jobGroupNl
	 * @param timezoneId
	 * @param existingJobGroupNames
	 * @return
	 */
	private List<JobGroup> buildJobGroups(NodeList jobGroupNl, String timezoneId, Set<String> existingJobGroupNames) {
		if (jobGroupNl == null || jobGroupNl.getLength() == 0) return null;
		if (existingJobGroupNames == null) existingJobGroupNames = new TreeSet<String>();
		List<JobGroup> jobGroups = new ArrayList<JobGroup>();
		for (int i = 0; i < jobGroupNl.getLength(); ++i) {
			Element jobGroupElem = (Element) jobGroupNl.item(i);
			String jobGroupName = ConfigManager.getAttributeValue(jobGroupElem, "name");
			if (!existingJobGroupNames.add(jobGroupName)) continue;
			//boolean enabled = Boolean.valueOf(ConfigManager.getAttributeValue(jobGroupElem, "enabled")).booleanValue();
			String scheduleType = ConfigManager.getAttributeValue(jobGroupElem, "scheduleType");
			String years = ConfigManager.getAttributeValue(jobGroupElem, "years");
			String months = ConfigManager.getAttributeValue(jobGroupElem, "months");
			String days = ConfigManager.getAttributeValue(jobGroupElem, "days");
			String weekdays = ConfigManager.getAttributeValue(jobGroupElem, "weekdays");
			String hours = ConfigManager.getAttributeValue(jobGroupElem, "hours");
			String minutes = ConfigManager.getAttributeValue(jobGroupElem, "minutes");
			String tmp = ConfigManager.getAttributeValue(jobGroupElem, "period");
			int period = 0;
			if (!Strings.isEmpty(tmp)) {
				try { period = Integer.parseInt(tmp); } catch (Exception e) {
					logger.warn("Invalid value (" + tmp + ") for period. Default value will be used.");
				}
			}
			String periodStart = ConfigManager.getAttributeValue(jobGroupElem, "periodStart");
			tmp = ConfigManager.getAttributeValue(jobGroupElem,  "dependencies");
			String[] dependencies = (Strings.isEmpty(tmp) ? null : Strings.toArray(tmp));
			JobGroup jobGroup = new JobGroup(jobGroupName, timezoneId);
			Schedule schedule = new Schedule(timezoneId, scheduleType, years, months, days, weekdays, hours, minutes, period, periodStart);
			jobGroup.setSchedule(schedule);
			jobGroup.setDependencies(dependencies);
			NodeList jobNl = ConfigManager.getNodeList(jobGroupElem, "job");
			if (jobNl != null && jobNl.getLength() > 0) {
				List<Job> jobs = new ArrayList<Job>();
				for (int j = 0; j < jobNl.getLength(); ++j) {
					Element jobElem = (Element) jobNl.item(j);
					String jobName = ConfigManager.getAttributeValue(jobElem, "name");
					String className = ConfigManager.getAttributeValue(jobElem, "class");
					Map<String, ?> params = ConfigManager.getDeepParams(jobElem);
					tmp = ConfigManager.getAttributeValue(jobElem,  "dependencies");
					dependencies = (Strings.isEmpty(tmp) ? null : Strings.toArray(tmp));
					Job job = new Job(null, jobName, timezoneId, null, null, null, className, params);
					job.setDependencies(dependencies);
					jobs.add(job);
				}
				jobGroup.setJobs(jobs);
			}
			jobGroups.add(jobGroup);
		}
		return jobGroups;
	}

}
