package fxControllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.*;
import utils.AlertDialog;

public class ForumPage {

    @FXML
    public ListView<Forum> listForum;
    @FXML
    public TreeView<Comment> commentTree;
    @FXML
    public TextArea commentBody;
    @FXML
    public MenuItem updateTitleForum;
    @FXML
    public MenuItem addNewTitleForum;
    @FXML
    public MenuItem deleteTitleForum;

    private Comment selectedComment = null;

    public void updateTitlesOnForum() {
        int selectedIndex = listForum.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            Forum selectedForum = listForum.getItems().get(selectedIndex);
            String newTitle = commentBody.getText();
            selectedForum.updateTitle(newTitle);
            listForum.refresh();
        }
    }


    public void addNewTitlesOnForum() {
        if (commentBody.getText().isEmpty()){
            AlertDialog.throwAlert("Creating title error", "You cannot leave field empty");
            return;
        }
        Forum title = new Forum(commentBody.getText());
        System.out.println(listForum.getItems().add(title));
    }

    public void deleteTitlesOnForum() {
        int selectedTitle = listForum.getSelectionModel().getSelectedIndex();
        listForum.getItems().remove(selectedTitle);
    }

    public void addComment() {
        if (commentBody.getText().isEmpty()){
            AlertDialog.throwAlert("Creating comment error", "You cannot leave field empty");
            return;
        }
        Forum selectedForum = listForum.getSelectionModel().getSelectedItem();
        if (selectedForum == null) {
            return;
        }
        TreeItem<Comment> selectedCommentTreeItem = commentTree.getSelectionModel().getSelectedItem();
        if (selectedCommentTreeItem != null) {
            Comment parentComment = selectedCommentTreeItem.getValue();
            Comment childComment = new Comment(commentBody.getText());
            parentComment.addChildComment(childComment);
            selectedCommentTreeItem.getChildren().add(new TreeItem<>(childComment));
            System.out.println(childComment);
        } else {
            Comment newComment = new Comment(commentBody.getText());
            selectedForum.getCommentList().add(newComment);
            System.out.println(newComment);
            refreshCommentTree(selectedForum);
        }
    }

    public void goToComments() {
        Forum selectedForum = listForum.getSelectionModel().getSelectedItem();
        if (selectedForum != null) {
            refreshCommentTree(selectedForum);
        }
    }

    public void selectComment() {
        selectedComment = commentTree.getSelectionModel().getSelectedItem().getValue();
    }

    private void refreshCommentTree(Forum forum) {
        if (forum == null) {
            return;
        }
        TreeItem<Comment> root = new TreeItem<>(new Comment("Comments for " + forum.getTitle()));
        ObservableList<Comment> comments = forum.getCommentList();
        for (Comment comment : comments) {
            TreeItem<Comment> commentNode = new TreeItem<>(comment);
            for (Comment childComment : comment.getReplies()) {
                TreeItem<Comment> childCommentNode = new TreeItem<>(childComment);
                commentNode.getChildren().add(childCommentNode);
            }
            root.getChildren().add(commentNode);
        }
        commentTree.setRoot(root);
    }

    public void updateComment() {
        TreeItem<Comment> selectedCommentTreeItem = commentTree.getSelectionModel().getSelectedItem();
        if (selectedCommentTreeItem != null) {
            selectedCommentTreeItem.setValue(new Comment(commentBody.getText()));
            commentTree.refresh();
        }
    }

    public void deleteComment() {
        ObservableList<Integer> selectedCommentIndices = commentTree.getSelectionModel().getSelectedIndices();
        for (int i = selectedCommentIndices.size() - 1; i >= 0; i--) {
            int index = selectedCommentIndices.get(i);
            TreeItem<Comment> item = commentTree.getTreeItem(index);
            if (item != null) {
                if (item.getParent() == null) {
                    deleteComment(item);
                } else {
                    item.getParent().getChildren().remove(item);
                }
            }
        }
        commentTree.refresh();
    }

    private void deleteComment(TreeItem<Comment> comment) {
        if (comment == null) {
            return;
        }
        if (comment.getParent() != null) {
            for (TreeItem<Comment> child : comment.getChildren()) {
                deleteComment(child);
            }
            comment.getParent().getChildren().remove(comment);
        } else {
            commentTree.getRoot().getChildren().remove(comment);
        }
        commentTree.refresh();
    }
}

