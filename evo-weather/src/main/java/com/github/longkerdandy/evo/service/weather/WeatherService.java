package com.github.longkerdandy.evo.service.weather;

import com.github.longkerdandy.evo.service.weather.quartz.OpenWeatherJob;
import com.github.longkerdandy.evo.service.weather.tcp.TCPClient;
import com.github.longkerdandy.evo.service.weather.util.ExcelUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.quartz.*;
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
        // load area ids
        Set<String> areaIds = ExcelUtils.loadAreaIds();

        // start tcp client
        TCPClient tcp = new TCPClient("localhost", 1883, areaIds);
        Thread thread = new Thread(tcp);
        thread.start();

        // start quartz
        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        try {
            for (String areaId : areaIds) {
                Trigger trigger = newTrigger()
                        .withIdentity(areaId, "evo.service.weather")
                        .withSchedule(cronSchedule("0 15 11 * * ?"))
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
