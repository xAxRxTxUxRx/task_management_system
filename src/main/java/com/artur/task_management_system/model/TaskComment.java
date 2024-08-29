package com.artur.task_management_system.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "task_comment_table")
public class TaskComment {
    @Id
    @SequenceGenerator(
            name = "task_comment_id",
            sequenceName = "task_comment_id",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "task_comment_id",
            strategy = GenerationType.SEQUENCE
    )
    private Long id;

    private LocalDateTime creationDate;

    @Column(nullable = false)
    private String text;

    @ManyToOne(optional = false)
    @JoinColumn(name = "from_id")
    @JsonBackReference
    private User from;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;

    @Version
    private Integer version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskComment)) return false;
        TaskComment taskComment = (TaskComment) o;
        return Objects.equals(id, taskComment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
