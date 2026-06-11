package com.alaya.course.repository;

import com.alaya.course.domain.Course;
import com.alaya.course.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    // 根据授课教师分页查询（教师查看自己的课程）
    Page<Course> findByTeacher(User teacher, Pageable pageable);

    // 根据课程名模糊搜索（不区分大小写），支持分页
    Page<Course> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // 根据ID和教师查询（用于权限校验：教师只能操作自己的课程）
    Optional<Course> findByIdAndTeacher(Long id, User teacher);
}