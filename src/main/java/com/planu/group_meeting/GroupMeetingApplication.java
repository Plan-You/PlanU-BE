package com.planu.group_meeting;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan({"com.planu.group_meeting.dao", "com.planu.group_meeting.chat.dao"})
public class GroupMeetingApplication {

	public static void main(String[] args) {
		SpringApplication.run(GroupMeetingApplication.class, args);
	}

}
