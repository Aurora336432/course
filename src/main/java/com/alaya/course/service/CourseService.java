package com.alaya.course.service;

import com.alaya.course.domain.Course;
import com.alaya.course.domain.User;
import com.alaya.course.repository.CourseRepository;
import com.alaya.course.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private UserRepository userRepository;

    // 创建课程
    public Course createCourse(String name, String description, Integer credit, Integer capacity, String schedule, String teacherUsername) {
        User teacher = userRepository.findByUsername(teacherUsername)
                .orElseThrow(() -> new RuntimeException("教师不存在"));
        if (capacity <= 0) {
            throw new IllegalArgumentException("课程容量必须大于0");
        }
        Course course = new Course();
        course.setName(name);
        course.setDescription(description);
        course.setCredit(credit);
        course.setCapacity(capacity);
        course.setSchedule(schedule);
        course.setTeacher(teacher);
        course.setCreatedAt(java.time.LocalDateTime.now());
        return courseRepository.save(course);
    }

    // 编辑课程（需校验归属）
    public Course updateCourse(Long id, String name, String description, Integer credit, Integer capacity, String schedule, String currentUsername) {
        User teacher = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Course course = courseRepository.findByIdAndTeacher(id, teacher)
                .orElseThrow(() -> new RuntimeException("课程不存在或无权限"));
        course.setName(name);
        course.setDescription(description);
        course.setCredit(credit);
        course.setCapacity(capacity);
        course.setSchedule(schedule);
        return courseRepository.save(course);
    }

    // 删除课程
    public void deleteCourse(Long id, String currentUsername) {
        User teacher = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Course course = courseRepository.findByIdAndTeacher(id, teacher)
                .orElseThrow(() -> new RuntimeException("课程不存在或无权限"));
        courseRepository.delete(course);
    }

    // 教师查看自己的课程（分页）
    public Page<Course> getTeacherCourses(String username, Pageable pageable) {
        User teacher = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("教师不存在"));
        return courseRepository.findByTeacher(teacher, pageable);
    }

    // 学生浏览课程（支持关键词搜索，分页）
    public Page<Course> searchCourses(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return courseRepository.findAll(pageable);
        }
        return courseRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }

    // 根据ID获取课程（学生查看详情，无需权限）
    public Course getCourseById(Long id) {
        return courseRepository.findById(id).orElseThrow(() -> new RuntimeException("课程不存在"));
    }
}