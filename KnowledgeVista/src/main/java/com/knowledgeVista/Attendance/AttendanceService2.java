package com.knowledgeVista.Attendance;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knowledgeVista.Attendance.Repo.AttendanceRepo;
import com.knowledgeVista.Batch.Batch;
import com.knowledgeVista.Batch.Repo.BatchRepository;
import com.knowledgeVista.Course.Repository.CourseDetailRepository;
import com.knowledgeVista.Meeting.SaveMeetDataService;
import com.knowledgeVista.Meeting.ZoomTokenService;
import com.knowledgeVista.Meeting.zoomclass.Meeting;
import com.knowledgeVista.Meeting.zoomclass.repo.InviteeRepo;
import com.knowledgeVista.Meeting.zoomclass.repo.Meetrepo;
import com.knowledgeVista.Meeting.zoomclass.repo.OccurancesRepo;
import com.knowledgeVista.Meeting.zoomclass.repo.VirtualmeetmapRepo;
import com.knowledgeVista.Notification.Repositories.NotificationDetailsRepo;
import com.knowledgeVista.Notification.Repositories.NotificationUserRepo;
import com.knowledgeVista.Settings.Controller.SettingsController;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;
import com.knowledgeVista.zoomJar.ZoomMethods;

import jakarta.transaction.Transactional;

@Service
@EnableScheduling
public class AttendanceService2 {
   
    @Autowired
    private Meetrepo meetrepo;
   
    @Autowired
    private BatchRepository batchrepo;
   
    @Autowired
    private AttendanceRepo attendanceRepo;
	 private final TaskScheduler taskScheduler;
	    private final ConcurrentHashMap<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

	     public AttendanceService2() {
	        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
	        scheduler.setPoolSize(5);
	        scheduler.initialize();
	        this.taskScheduler = scheduler;
	    }

	    public void scheduleMarkAbsent(Long meetId, long delayMinutes) {
	        Instant executionTime = Instant.now().plusSeconds(delayMinutes * 60);

	        ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(() -> {
	            markRemainingAsAbsent(meetId);
	        }, executionTime);

	        scheduledTasks.put(meetId, scheduledFuture);
	    }

	    @Transactional
	    public void markRemainingAsAbsent(Long meetid) {
	        try {
	            System.out.println("Entered in markRemainingAsAbsent");
	            List<Long> presentUsers = attendanceRepo.finduserIdsByMeetingIdAndPRESENT(meetid, LocalDate.now());
	            System.out.println("Present Users: " + presentUsers);

	            Optional<Meeting> opmeet = meetrepo.FindByMeetingIdwithbatch(meetid);
	            if (opmeet.isPresent()) {
	                Meeting meet = opmeet.get();
	                meet.getBatches().size(); // Load batches
	                
	                List<Muser> allUsers = new ArrayList<>();
	                for (Batch batch : meet.getBatches()) {
	                    allUsers.addAll(batchrepo.findAllusersByBatchId(batch.getId()));
	                }

	                for (Muser user : allUsers) {
	                    if (!presentUsers.contains(user.getUserId())) {
	                        Attendancedetails attendance = new Attendancedetails();
	                        attendance.setUserId(user.getUserId());
	                        attendance.setMeeting(meet);
	                        attendance.setDate(LocalDate.now());
	                        attendance.setStatus("ABSENT");
	                        attendanceRepo.save(attendance);
	                    }
	                }
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	}

