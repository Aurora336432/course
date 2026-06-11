package com.alaya.course.repository;

import com.alaya.course.domain.Course;
import com.alaya.course.domain.Enrollment;
import com.alaya.course.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    // ========== 迭代3已有方法 ==========
    // 查询学生的所有有效选课（未退课）
    List<Enrollment> findByStudentAndActiveTrue(User student);

    // 查询课程的所有有效选课（教师查看名单）
    List<Enrollment> findByCourseAndActiveTrue(Course course);

    // 检查学生是否已选某门有效课程
    boolean existsByStudentAndCourseAndActiveTrue(User student, Course course);

    // 根据ID查询有效选课记录（用于退课）
    Optional<Enrollment> findByIdAndActiveTrue(Long id);

    // ========== 迭代4新增方法（成绩管理） ==========
    // 学生查看自己的成绩：按选课时间倒序
    List<Enrollment> findByStudentAndActiveTrueOrderByEnrolledAtDesc(User student);

    // 教师查看某课程的学生名单：按学生用户名升序
    List<Enrollment> findByCourseAndActiveTrueOrderByStudentUsernameAsc(Course course);

    // 查询某课程已录入成绩的选课记录（用于统计，score不为null）
    List<Enrollment> findByCourseAndActiveTrueAndScoreIsNotNull(Course course);
}