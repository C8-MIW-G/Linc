package com.softCare.Linc.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static javax.persistence.CascadeType.ALL;

@Entity @Getter @Setter @AllArgsConstructor
public class Task {

    @Id
    @Column(name = "task_id", nullable = false)
    @GeneratedValue
    private Long taskId;
    private String taskName;
    private String taskDescription;
    private boolean taskDone;

    private String category;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dueDate;

    @ManyToOne
    @JoinColumn(name="circle_id", nullable=false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Circle circle;

    private String circleName;

    @ManyToOne
    @JoinColumn(name = "user_user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne
    @JoinColumn(name = "author_user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User author;

    private String claimedUserName;
    private int duration;

    public Task(String taskName, String taskDescription, boolean taskDone, LocalDate dueDate, Circle circle, String circleName, User user, String claimedUserName, int duration,User author,String category) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskDone = taskDone;
        this.dueDate = dueDate;
        this.circle = circle;
        this.circleName = circleName;
        this.user = user;
        this.claimedUserName = claimedUserName;
        this.duration = duration;
        this.author = author;
        this.category = category;
    }

    public Task(String taskName, String taskDescription, boolean taskDone, Circle circle, String circleName, LocalDate dueDate, int duration) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskDone = taskDone;
        this.circle = circle;
        this.circleName = circleName;
        this.dueDate = dueDate;
        this.duration = duration;
    }

    public Task(String taskName, String taskDescription, boolean taskDone, Circle circle, String circleName, LocalDate dueDate, int duration, User author,String category) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskDone = taskDone;
        this.circle = circle;
        this.circleName = circleName;
        this.dueDate = dueDate;
        this.duration = duration;
        this.author = author;
        this.category = category;
    }

    public Task() {
    }


}
