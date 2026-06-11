package com.alaya.course.controller;

import com.alaya.course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/teacher")
public class TeacherCourseController {

    @Autowired
    private CourseService courseService;

    // 教师的课程列表
    @GetMapping("/courses")
    public String listCourses(Model model, Principal principal,
                              @RequestParam(defaultValue = "0") int page) {
        var pageable = PageRequest.of(page, 10);
        var courses = courseService.getTeacherCourses(principal.getName(), pageable);
        model.addAttribute("courses", courses.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", courses.getTotalPages());
        return "teacher-courses";
    }

    // 显示创建课程表单
    @GetMapping("/courses/create")
    public String showCreateForm(Model model) {
        model.addAttribute("course", new com.alaya.course.domain.Course());
        return "course-form";
    }

    // 处理创建课程
    @PostMapping("/courses")
    public String createCourse(@RequestParam String name,
                               @RequestParam String description,
                               @RequestParam Integer credit,
                               @RequestParam Integer capacity,
                               @RequestParam String schedule,
                               Principal principal) {
        courseService.createCourse(name, description, credit, capacity, schedule, principal.getName());
        return "redirect:/teacher/courses";
    }

    // 显示编辑表单
    @GetMapping("/courses/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, Principal principal) {
        var course = courseService.getCourseById(id);
        // 额外校验归属，前端也可以，但service已有，这里先获取
        model.addAttribute("course", course);
        return "course-form";
    }

    // 处理编辑
    @PostMapping("/courses/{id}/edit")
    public String updateCourse(@PathVariable Long id,
                               @RequestParam String name,
                               @RequestParam String description,
                               @RequestParam Integer credit,
                               @RequestParam Integer capacity,
                               @RequestParam String schedule,
                               Principal principal) {
        courseService.updateCourse(id, name, description, credit, capacity, schedule, principal.getName());
        return "redirect:/teacher/courses";
    }

    // 删除课程
    @PostMapping("/courses/{id}/delete")
    public String deleteCourse(@PathVariable Long id, Principal principal) {
        courseService.deleteCourse(id, principal.getName());
        return "redirect:/teacher/courses";
    }
}