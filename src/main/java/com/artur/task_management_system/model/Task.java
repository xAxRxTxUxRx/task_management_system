package com.artur.task_management_system.model;

import com.artur.task_management_system.model.attributes.TaskPriority;
import com.artur.task_management_system.model.attributes.TaskStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "task_table")
public class Task {
    @Id
    @SequenceGenerator(
            name = "task_id",
            sequenceName = "task_id",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "task_id",
            strategy = GenerationType.SEQUENCE
    )
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority priority;

    @Column(nullable = false)
    private LocalDateTime creationDate;

    @Column(nullable = false)
    private LocalDateTime deadLineDate;

    @ManyToOne(cascade = CascadeType.DETACH, optional = false)
    @JoinColumn(name = "author_id")
    @JsonBackReference
    private User author;

    @ManyToMany(mappedBy = "assignedTasks")
    @JsonBackReference
    private Set<User> performers = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "task_id")
    @JsonManagedReference
    private Set<TaskComment> comments = new HashSet<>();

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;

    @Version
    private Integer version;

    public void addPerformer(User performer){
        performers.add(performer);
    }

    public void addComment(TaskComment comment){
        comments.add(comment);
    }

    public void removePerformer(User user) {
        performers.remove(user);
    }

    public boolean isAssignedTo(Long loggedInUserId) {
        for (User performer : performers){
            if (Objects.equals(performer.getId(), loggedInUserId)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}