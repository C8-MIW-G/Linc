package com.softCare.Linc.controller;

import com.softCare.Linc.model.Circle;
import com.softCare.Linc.model.CircleMember;
import com.softCare.Linc.model.User;
import com.softCare.Linc.service.CircleMemberServiceInterface;
import com.softCare.Linc.service.CircleServiceInterface;
import com.softCare.Linc.service.LincUserDetailServiceInterface;
import com.softCare.Linc.service.TaskServiceInterface;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Optional;


@Controller
public class CircleController {


    public static final String CIRCLE_NAME_MUST_BE_FILLED_IN = "A new Circle has to have a name";
    private final CircleServiceInterface circleServiceInterface;
    private final TaskServiceInterface taskServiceInterface;
    private final CircleMemberServiceInterface circleMemberInterface;

    private final LincUserDetailServiceInterface userService;
    public Circle currentCircle;

    public CircleController(CircleServiceInterface circleServiceInterface, TaskServiceInterface taskServiceInterface, CircleMemberServiceInterface circleMemberInterface, CircleMemberServiceInterface circleMemberInterface1, LincUserDetailServiceInterface userService) {
        this.circleServiceInterface = circleServiceInterface;
        this.taskServiceInterface = taskServiceInterface;
        this.circleMemberInterface = circleMemberInterface;
        this.userService = userService;
    }

    @GetMapping("/circle/{circleId}")
    protected String showCircleDetails(@PathVariable("circleId") Long circleId, Model model,@AuthenticationPrincipal User user) {
        Optional<Circle> circle = circleServiceInterface.findById(circleId);
        if (circle.isPresent()) {
            if (circleMemberInterface.isMember(user, circle.get())) {
                boolean isAdmin = circleMemberInterface.isAdmin(user,circle.get());
                currentCircle = circle.get();
                model.addAttribute("circle", circle.get());
                model.addAttribute("tasksToDo", taskServiceInterface.findAllTasksToDoInCircle(currentCircle));
                model.addAttribute("doneTasks", taskServiceInterface.findAllDoneTasksInCircle(currentCircle));
                model.addAttribute("users",circleMemberInterface.findAllMembers(circle.get()));
                model.addAttribute("currentUser",user.getUsername());
                model.addAttribute("isAdmin",isAdmin);
                model.addAttribute("newMemberUser", new User());
                model.addAttribute("userPermissions",circleMemberInterface.findCircleMembers(circle.get()).get());
                return "circleDetail";
            }
            //TODO: add 'no access' error page
            return "redirect:/dashboard";
        }else return "redirect:/dashboard";

    }

    @GetMapping({"/circle/new"})
    protected String newCircle(Model model) {
        model.addAttribute("circle", new Circle());
        return "circleForm";
    }

    @PostMapping("/circle/new")
    protected String saveCircle(@ModelAttribute("circle") Circle circle, @RequestParam(value = "clientCheckbox", required = false) String clientCheckbox, BindingResult result, @AuthenticationPrincipal User user, RedirectAttributes redirectAttributes) {
        if (!result.hasErrors() && !circle.getCircleName().equals("")) {
            circleServiceInterface.save(circle);

            boolean isClient = false;
            if(clientCheckbox != null) {
                isClient = true;
            }

            CircleMember circleMember = new CircleMember(user, circle, isClient, true);

            Optional<User> admin = userService.findByUsername("Admin");
                if (admin.isPresent() && !user.getUserId().equals(admin.get().getUserId())) {
                    circleMemberInterface.save(new CircleMember(admin.get(), circle, true, true));
                }

            circleMemberInterface.save(circleMember);
            return "redirect:/dashboard";
        }
        redirectAttributes.addFlashAttribute("errorMessage", CIRCLE_NAME_MUST_BE_FILLED_IN);
        return "redirect:/dashboard";
    }

    @GetMapping("/circle/delete")
    protected String deleteCircle() {
        circleServiceInterface.delete(currentCircle);
        return "redirect:/dashboard";
    }






}
