package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private int id;
    private String commentText;
    private User user;
    private LocalDate dateCreated;
    private LocalDate dateModified;
    private List<Comment> replies;

    public String toString() {
        return commentText;
    }


    public Comment(String commentText, User user) {
        this.commentText = commentText;
        this.user = user;
        this.dateCreated = LocalDate.now();
    }

    public Comment(String commentText) {
        this.commentText = commentText;
        this.replies = new ArrayList<>();
    }

    public List<Comment> getReplies() {
        return replies;
    }

    public void addChildComment(Comment childComment) {
        replies.add(childComment);
    }


}