package com.alaya.course.controller;

import com.alaya.course.domain.Enrollment;
import com.alaya.course.dto.EnrollmentGradeDTO;
import com.alaya.course.service.GradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class GradeController {

    @Autowired
    private GradeService gradeService;

    // 教师录入/修改成绩
    @PostMapping("/teacher/enrollments/{id}/grade")
    public String saveGrade(@PathVariable Long id,
                            @RequestParam Integer score,
                            @RequestParam(required = false) String comment,
                            Principal principal,
                            RedirectAttributes redirectAttributes) {
        try {
            Enrollment enrollment = gradeService.saveOrUpdateGrade(id, score, comment, principal.getName());
            Long courseId = enrollment.getCourse().getId();
            redirectAttributes.addFlashAttribute("successMessage",
                    enrollment.isGraded() ? "成绩修改成功" : "成绩录入成功");
            return "redirect:/teacher/courses/" + courseId + "/students";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            // 失败时重定向到教师课程列表（或原课程列表，这里简单处理）
            return "redirect:/teacher/courses";
        }
    }

    // 教师查看某课程的学生名单及成绩统计
    @GetMapping("/teacher/courses/{id}/students")
    public String courseStudents(@PathVariable Long id,
                                 Principal principal,
                                 Model model) {
        try {
            List<Enrollment> enrollments = gradeService.getEnrollmentsByCourse(id, principal.getName());
            List<EnrollmentGradeDTO> dtos = enrollments.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            model.addAttribute("enrollments", dtos);
            model.addAttribute("courseId", id);
            // 计算统计信息
            addGradeStatistics(model, dtos);
            return "teacher/course-students";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("enrollments", Collections.emptyList());
            return "teacher/course-students";
        }
    }

    // 学生查看个人成绩
    @GetMapping("/student/grades")
    public String myGrades(Principal principal, Model model) {
        List<Enrollment> enrollments = gradeService.getEnrollmentsByStudent(principal.getName());
        List<EnrollmentGradeDTO> dtos = enrollments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        model.addAttribute("enrollments", dtos);
        return "student/grades";
    }

    // 转换实体到DTO
    private EnrollmentGradeDTO convertToDTO(Enrollment enrollment) {
        EnrollmentGradeDTO dto = new EnrollmentGradeDTO();
        dto.setId(enrollment.getId());
        dto.setCourseName(enrollment.getCourse().getName());
        dto.setStudentUsername(enrollment.getStudent().getUsername());
        dto.setScore(enrollment.getScore());
        dto.setGradeComment(enrollment.getGradeComment());
        dto.setGradedAt(enrollment.getGradedAt());
        dto.setStatus(enrollment.isGraded() ? "已录入" : "未录入");
        return dto;
    }

    // 计算成绩统计
    private void addGradeStatistics(Model model, List<EnrollmentGradeDTO> dtos) {
        List<EnrollmentGradeDTO> graded = dtos.stream()
                .filter(d -> d.getScore() != null)
                .collect(Collectors.toList());
        if (!graded.isEmpty()) {
            double avg = graded.stream().mapToInt(EnrollmentGradeDTO::getScore).average().orElse(0);
            int max = graded.stream().mapToInt(EnrollmentGradeDTO::getScore).max().orElse(0);
            int min = graded.stream().mapToInt(EnrollmentGradeDTO::getScore).min().orElse(0);
            model.addAttribute("averageScore", Math.round(avg * 10.0) / 10.0);
            model.addAttribute("maxScore", max);
            model.addAttribute("minScore", min);
            model.addAttribute("gradedCount", graded.size());
        }
        // 如果没有已录入成绩，不添加统计属性，模板通过th:if判断
    }
}
