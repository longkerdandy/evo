package com.github.longkerdandy.evo.service.weather;

import com.github.longkerdandy.evo.service.weather.quartz.OpenWeatherJob;
import com.github.longkerdandy.evo.service.weather.tcp.TCPClient;
import com.github.longkerdandy.evo.service.weather.util.ExcelUtils;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Weather Service
 */
public class WeatherService {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(OpenWeatherJob.class);

    public static void main(String args[]) throws Exception {
        // load config
        String f = args.length >= 1 ? args[0] : "config/tcp.properties";
        PropertiesConfiguration config = new PropertiesConfiguration(f);

        // load area ids
        Set<String> areaIds = ExcelUtils.loadAreaIds();

        // start tcp client
        TCPClient tcp = new TCPClient(config.getString("tcp.host"), config.getInt("tcp.port"), areaIds);
        TCPClient.setInstance(tcp);
        Thread thread = new Thread(tcp);
        thread.start();

        // start quartz
        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        try {
            for (String areaId : areaIds) {
                Trigger trigger = newTrigger()
                        .withIdentity(areaId, "evo.service.weather")
                        .withSchedule(cronSchedule(config.getString("scheduler.cron")))
                        .build();
                JobDetail job = newJob(OpenWeatherJob.class)
                        .withIdentity(areaId, "evo.service.weather")
                        .usingJobData("AreaId", areaId)
                        .build();
                scheduler.scheduleJob(job, trigger);
            }
            scheduler.start();
        } catch (SchedulerException e) {
            logger.error("Error when adding Open Weather job to Quartz scheduler: {}", ExceptionUtils.getMessage(e));
        }
    }
}
