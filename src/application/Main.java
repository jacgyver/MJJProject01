package application;
	
import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import org.executequery.ExecuteQuery;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			final SwingNode swingNode = new SwingNode();
	        createSwingContent(swingNode);
	        
			Group root = new Group();
			Scene scene = new Scene(root,400,400);
			
			Button btn = new Button();
			btn.setText("Hello");	
			btn.setLayoutX(100);
			btn.setLayoutY(100);
			//root.getChildren().add(swingNode);
			//primaryStage.setScene(scene);
			//primaryStage.show();
			new ExecuteQuery();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void createSwingContent(SwingNode swingNode) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                swingNode.setContent(new JButton("Click me!"));
            }
        });
		
	}

	public static void main(String[] args) {
		launch(args);
	}
}
