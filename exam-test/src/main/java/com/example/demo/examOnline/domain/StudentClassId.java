package com.example.demo.examOnline.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentClassId implements Serializable {
    private Integer studentId;
    private Integer classId;

    // equals & hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof StudentClassId))
            return false;
        StudentClassId that = (StudentClassId) o;
        return Objects.equals(studentId, that.studentId) &&
                Objects.equals(classId, that.classId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, classId);
    }
}
