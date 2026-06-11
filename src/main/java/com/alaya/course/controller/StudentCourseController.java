package com.alaya.course.controller;

import com.alaya.course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/student")
public class StudentCourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/courses")
    public String listCourses(Model model,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(required = false) String keyword) {
        var pageable = PageRequest.of(page, 10);
        var courses = courseService.searchCourses(keyword, pageable);
        model.addAttribute("courses", courses.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", courses.getTotalPages());
        model.addAttribute("keyword", keyword != null ? keyword : "");
        return "course-list";
    }

    @GetMapping("/courses/{id}")
    public String courseDetail(@PathVariable Long id, Model model) {
        var course = courseService.getCourseById(id);
        model.addAttribute("course", course);
        return "course-detail";
    }
}