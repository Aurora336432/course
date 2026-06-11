package com.alaya.course.service;

import com.alaya.course.domain.Course;
import com.alaya.course.domain.Enrollment;
import com.alaya.course.domain.User;
import com.alaya.course.repository.CourseRepository;
import com.alaya.course.repository.EnrollmentRepository;
import com.alaya.course.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class GradeService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private UserRepository userRepository;

    // 录入或修改成绩
    @Transactional
    public Enrollment saveOrUpdateGrade(Long enrollmentId, Integer score, String comment, String teacherUsername) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("选课记录不存在"));

        // 1. 校验选课记录是否有效（未退课）
        if (!enrollment.isActive()) {
            throw new RuntimeException("该学生已退课，无法录入成绩");
        }

        // 2. 校验课程归属：教师只能给自己课程的学生打分
        if (!enrollment.getCourse().getTeacher().getUsername().equals(teacherUsername)) {
            throw new RuntimeException("无权限操作此课程的成绩");
        }

        // 3. 校验成绩范围
        if (score == null || score < 0 || score > 100) {
            throw new RuntimeException("成绩必须在0-100之间");
        }

        // 4. 更新成绩字段
        enrollment.setScore(score);
        enrollment.setGradeComment(comment);
        enrollment.setGradedAt(LocalDateTime.now());
        return enrollmentRepository.save(enrollment);
    }

    // 教师查看某课程的所有有效选课（含学生信息）
    public List<Enrollment> getEnrollmentsByCourse(Long courseId, String teacherUsername) {
        User teacher = userRepository.findByUsername(teacherUsername)
                .orElseThrow(() -> new RuntimeException("教师不存在"));
        Course course = courseRepository.findByIdAndTeacher(courseId, teacher)
                .orElseThrow(() -> new RuntimeException("课程不存在或无权限"));
        return enrollmentRepository.findByCourseAndActiveTrueOrderByStudentUsernameAsc(course);
    }

    // 学生查看自己的所有有效选课（含成绩）
    public List<Enrollment> getEnrollmentsByStudent(String studentUsername) {
        User student = userRepository.findByUsername(studentUsername)
                .orElseThrow(() -> new RuntimeException("学生不存在"));
        return enrollmentRepository.findByStudentAndActiveTrueOrderByEnrolledAtDesc(student);
    }
}
