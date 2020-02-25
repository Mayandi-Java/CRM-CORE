package com.sevael.lgtool.dao.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.transaction.annotation.Transactional;

public class MyTaskExecutor {

	ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

	AgeingEsclationThreadImp ageingEsclationThreadImp;

	volatile boolean isStopIssued;

	@Transactional
	public void startExecutionAt(int targetHour, int targetMin, int targetSec) {
//		ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
//		ageingEsclationThreadImp = (AgeingEsclationThreadImp) ctx.getBean("ageingEsclationThreadImp");

		System.out.println("Execution started!!!" + ageingEsclationThreadImp);
		Runnable taskWrapper = new Runnable() {
			@Override
			public void run() {
				ageingEsclationThreadImp.getAgingCount();
				startExecutionAt(targetHour, targetMin, targetSec);
			}
		};
		long delay = computeNextDelay(targetHour, targetMin, targetSec);
		executorService.schedule(taskWrapper, delay, TimeUnit.SECONDS);
	}

	private long computeNextDelay(int targetHour, int targetMin, int targetSec) {
		LocalDateTime localNow = LocalDateTime.now();
		ZoneId currentZone = ZoneId.systemDefault();
		/*
		 * Calendar cal = Calendar.getInstance(); boolean isFriday =
		 * cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY; if (isFriday) { try {
		 * signoffDaoImp.getOverallSignOffReport();
		 * System.out.println("Weekly PDF Execution"); } catch (Exception e) {
		 * e.printStackTrace(); } }
		 */
		ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
		ZonedDateTime zonedNextTarget = zonedNow.withHour(targetHour).withMinute(targetMin).withSecond(targetSec);
		if (zonedNow.compareTo(zonedNextTarget) > 0)
			zonedNextTarget = zonedNextTarget.plusDays(1);
		System.out
				.println("next target --> " + zonedNextTarget.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
		Duration duration = Duration.between(zonedNow, zonedNextTarget);
		return duration.getSeconds();
	}

	public void stop() {
		executorService.shutdown();
		try {
			executorService.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException ex) {
		}
	}
}