package com.alaya.course.controller;

import com.alaya.course.domain.Enrollment;
import com.alaya.course.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    // 学生选课
    @PostMapping("/student/courses/{id}/enroll")
    public String enroll(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        try {
            enrollmentService.enroll(id, username);
            redirectAttributes.addFlashAttribute("successMessage", "选课成功");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/student/courses";
    }

    // 学生退课
    @PostMapping("/student/enrollments/{id}/drop")
    public String drop(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        try {
            enrollmentService.dropCourse(id, username);
            redirectAttributes.addFlashAttribute("successMessage", "退课成功");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/student/schedule";
    }

    // 学生查看个人课表
    @GetMapping("/student/schedule")
    public String schedule(Principal principal, Model model) {
        String username = principal.getName();
        List<Enrollment> enrollments = enrollmentService.getStudentSchedule(username);
        model.addAttribute("enrollments", enrollments);
        return "my-schedule";
    }

    // 注意：教师查看学生名单及成绩管理的功能已移至 GradeController
    // 原 /teacher/courses/{id}/students 方法已删除，避免与 GradeController 冲突
}