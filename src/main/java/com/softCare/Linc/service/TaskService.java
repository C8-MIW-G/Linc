package com.softCare.Linc.service;

import com.softCare.Linc.Repository.TaskRepository;
import com.softCare.Linc.model.*;
import com.softCare.Linc.model.DTO.ShortTask;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskService implements TaskServiceInterface {


    public static final int DAYS_LEFT_NOTIFICATION = 3;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final CircleMemberServiceInterface circleMemberServiceInterface;
    private final CircleServiceInterface circleServiceInterface;

    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper, CircleMemberServiceInterface circleMemberServiceInterface, CircleServiceInterface circleServiceInterface) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.circleMemberServiceInterface = circleMemberServiceInterface;
        this.circleServiceInterface = circleServiceInterface;
    }

    @Override
    public Optional<Task> findById(Long taskId) {
        Optional<Task> task = taskRepository.findById(taskId);
        return task.map(taskMapper::taskToViewModel);
    }

    @Override
    public void save(Task task) {
        taskRepository.save(task);
    }

    @Override
    public void delete(Task task) {
        taskRepository.delete(task);
    }

    public Optional<List<Task>> findByCircle(Circle circle){
        return taskRepository.findByCircle(circle);
    }
    public Optional<List<Task>> findAllTasksToDoInCircle(Circle circle){
        Optional<List<Task>> allTasks = taskRepository.findByCircle(circle);
        List<Task> tasksToDo = new ArrayList<>();
        if (allTasks.isPresent()){
            for (Task allTask : allTasks.get()) {
                if (!allTask.isTaskDone()){
                    tasksToDo.add(allTask);
                }
            }

        }
        return Optional.of(tasksToDo.stream().sorted((o1, o2) ->o1.getDueDate().compareTo(o2.getDueDate())).collect(Collectors.toList()));
    }

    public Optional<List<ShortTask>> findAllShortTasksToDoInCircle(Circle circle){
        Optional<List<Task>> allTasks = taskRepository.findByCircle(circle);
        List<Task> tasksToDo = new ArrayList<>();
        if (allTasks.isPresent()){
            for (Task allTask : allTasks.get()) {
                if (!allTask.isTaskDone()){
                    tasksToDo.add(allTask);
                }
            }

        }
        return Optional.of(taskMapper.taskToShortTask((tasksToDo.stream().sorted((o1, o2) ->o1.getDueDate().compareTo(o2.getDueDate())).collect(Collectors.toList()))));
    }

    public Object findAllTasksToDoAndToClaimInCircle(Circle circle) {
        Optional<List<Task>> allTasks = taskRepository.findByCircle(circle);
        List<Task> tasksToDo = new ArrayList<>();
        if (allTasks.isPresent()) {
            for (Task allTask : allTasks.get()) {
                if (!allTask.isTaskDone() & allTask.getUser() == null){
                    tasksToDo.add(allTask);
                }
            }
        }
        return tasksToDo;
    }

    @Override
    public Object findAllDoneTasksInCircle(Circle circle) {
        List<Task> allTasks = taskRepository.findByCircle(circle).get();
        List<Task> doneTasks = new ArrayList<>();
        for (Task allTask : allTasks) {
            if (allTask.isTaskDone()){
                doneTasks.add(allTask);
            }
        }
        return doneTasks;
    }

    public Object findAllClaimedTasksForUser(User user) {
        List<Task> allTasks = taskRepository.findAll();
        List<Task> claimedTasks = new ArrayList<>();
        for (Task task : allTasks) {
            if (task.getUser() != null) {
                claimedTasks.add(task);
            }
        }
        return claimedTasks;
    }

    public List<Task> findAllTasksPerUser(User user) {
        User currentUser = user;
        List<Task> allTasks = taskRepository.findAll();
        List<Circle> allCircles = circleMemberServiceInterface.findAllCirclesWhereMemberOf(user);

        List<Task> tasksPerUser = new ArrayList<>();

        for (Task task : allTasks) {
            for (Circle circle : allCircles) {
                if(Objects.equals(task.getCircle().getCircleId(), circle.getCircleId()) && !task.isTaskDone()) {
                    tasksPerUser.add(task);
                }
            }
        }
        return tasksPerUser.stream().sorted((o1, o2) ->o1.getDueDate().compareTo(o2.getDueDate())).collect(Collectors.toList());
    }

    @Override
    public List<Task> findAllTasksPerUserByCategory(User user, String category) {
        List<Task> allTasksPerUser = findAllTasksPerUser(user);
        List<Task> filteredByCategory = new ArrayList<>();
        for (Task task : allTasksPerUser) {
            if (task.getCategory().equals(category)){
                filteredByCategory.add(task);
            }
        }
        return filteredByCategory;
    }


    @Override
    public Optional<Set<Notification>> dueDateNotificationsPerCircle(List<Circle> circleList) {
        Set<Notification> notificationSet = new HashSet<>();

        //start iterating every circle
        for (Circle circle : circleList) {
            int nrNotifications = 0;
            List<Task> taskList = circle.getTasks();

            // start iteration tasks of that circle
            for (Task task : taskList) {
                LocalDate dueDate = task.getDueDate();
                LocalDate today = LocalDate.now();

                // if task is soon due, and not done yet, add nrNotifications
                long daysLeft = ChronoUnit.DAYS.between(today,dueDate);
                if ((daysLeft< DAYS_LEFT_NOTIFICATION) && !task.isTaskDone()){
                    nrNotifications++;
                }
            }
            //make 1 notification per circle that has a due task
            Notification notification = new Notification(circle,nrNotifications);
            notificationSet.add(notification);
        }

        return Optional.of(notificationSet);
    }

    public Optional<Set<Notification>> dueDateNotificationsPerTask(List<Task> taskList){
        Set<Notification> notificationSet = new HashSet<>();

        //start iteration task
        for (Task task : taskList) {
            LocalDate dueDate = task.getDueDate();
            LocalDate today = LocalDate.now();
            long daysLeft = ChronoUnit.DAYS.between(today,dueDate);

            //if task is almost due, add it to the list
            if (daysLeft< DAYS_LEFT_NOTIFICATION && !task.isTaskDone()){
                Notification notification = new Notification(task,1);
                notificationSet.add(notification);
            }
        }
        return Optional.of(notificationSet);
    }


}
