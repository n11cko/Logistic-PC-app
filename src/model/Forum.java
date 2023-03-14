package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Forum {
    private int id;
    private String title;
    private ObservableList<Comment> commentList;

    public String toString() {
        return title.toUpperCase();
    }

    public Forum(String title) {
        this.title = title;
        this.commentList = FXCollections.observableArrayList();
    }

    public void updateTitle(String newTitle) {
        this.title = newTitle;
        this.commentList = FXCollections.observableArrayList();
    }

}
