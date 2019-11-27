package xyz.kholdy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSlider;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class Controller implements Initializable{
	
	private String fileLocation, fileLocationApacheTika;
	private int counterTrek = 0;
	
	private Media audioFile;
	private MediaPlayer audioPlayer;
	private InputStream input;
	private ContentHandler handler;
	private Metadata metadata;
	private Parser  parser;
	private ParseContext parseCtx;
	private FileChooser fileChooser;
	List<File> selectedFiles;
	Alert messageError;
	
	@FXML
    private Label labArtists;
	
	@FXML
	private Label labTitle;
	
	@FXML
    private JFXButton btnPlay;
	
	@FXML
    private JFXButton btnNext;

    @FXML
    private JFXButton btnBack;
    
    @FXML
    private JFXButton btnAdd;
    
    @FXML
    private JFXSlider volumeSlider;
    
    @FXML
    private JFXListView<String> listViewPlayList;
	
    //***************************************************************************************************************************
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
	//***************************************************************************************************************************
	@FXML
	void btnAddAction(ActionEvent event) {
		
		try {
			fileChooser = new FileChooser();
			fileChooser.getExtensionFilters().addAll(new ExtensionFilter("mp3 files", "*.mp3"));
			
			selectedFiles = fileChooser.showOpenMultipleDialog(null);
			
			if(selectedFiles != null) {
				for(int i = 0; i < selectedFiles.size(); i++) {
					listViewPlayList.getItems().add(selectedFiles.get(i).getName());
				}
			}
			
			fileLocationApacheTika = selectedFiles.get(0).getAbsolutePath().toString();
	    	fileLocation = "file:///" + selectedFiles.get(0).getAbsolutePath().toString().replace('\\', '/');
	    	
	    	audioFile = new Media(fileLocation);
			audioPlayer = new MediaPlayer(audioFile);
			//**************************************************Volume***************************************************************
			volumeSlider.setValue(audioPlayer.getVolume() * 100);
			volumeSlider.valueProperty().addListener(new InvalidationListener() {
				
				@Override
				public void invalidated(Observable arg0) {
					// TODO Auto-generated method stub
					audioPlayer.setVolume(volumeSlider.getValue() / 100);
				}
			});
			//***********************************************************************************************************************
		} catch (Exception e) {
			// TODO: handle exception
			messageError = new Alert(AlertType.INFORMATION);
			messageError.setTitle("Infomation");
			messageError.setHeaderText("You did not select a track!");
			messageError.showAndWait();
		}
	}
	//***************************************************************************************************************************
	@FXML
    void btnPlayAction(ActionEvent event) {
		try {
			
			if(btnPlay.getText().equals("PLAY")) {
				
				mp3ParserTags();
				
				audioPlayer.play();
				btnPlay.setText("PAUSE");
				
			}else if(btnPlay.getText().equals("PAUSE")) {
				audioPlayer.pause();
				btnPlay.setText("PLAY");
			}	
		} catch (Exception e) {
			// TODO: handle exception
			messageError = new Alert(AlertType.INFORMATION);
			messageError.setTitle("Infomation");
			messageError.setHeaderText("Select track!");
			messageError.showAndWait();
		}
    }
	//***************************************************************************************************************************
	@FXML
    void btnNextAction(ActionEvent event) {
		boolean flag = false;
		
		if(btnPlay.getText().equals("PLAY")) {
			btnPlay.setText("PAUSE");
		}
		
		audioPlayer.stop();
		
		if(counterTrek == selectedFiles.size() - 1){
			counterTrek = 0;
			flag = true;
		}
			
		if(counterTrek <= selectedFiles.size() - 1) {
			counterTrek++;
			if(flag)
				counterTrek = 0;
			fileLocationApacheTika = selectedFiles.get(counterTrek).getAbsolutePath().toString();
		    fileLocation = "file:///" + selectedFiles.get(counterTrek).getAbsolutePath().toString().replace('\\', '/');
		    	
		    audioFile = new Media(fileLocation);
			audioPlayer = new MediaPlayer(audioFile);
				
			mp3ParserTags();
				
			audioPlayer.play();
		}
    }
	//***************************************************************************************************************************
	@FXML
    void btnBackAction(ActionEvent event) {
		boolean flag = false;
		
		if(btnPlay.getText().equals("PLAY")) {
			btnPlay.setText("PAUSE");
		}
		
		audioPlayer.stop();
			
		if(counterTrek == 0){
			counterTrek = selectedFiles.size() - 1;
			flag = true;
		}
		
		if(counterTrek >= 0){
			counterTrek--;
			if(flag)
				counterTrek = selectedFiles.size() - 1;
			fileLocationApacheTika = selectedFiles.get(counterTrek).getAbsolutePath().toString();
		   	fileLocation = "file:///" + selectedFiles.get(counterTrek).getAbsolutePath().toString().replace('\\', '/');
		    	
		    audioFile = new Media(fileLocation);
			audioPlayer = new MediaPlayer(audioFile);
			
			mp3ParserTags();
		}
			
		audioPlayer.play();
	}
	//***************************************************************************************************************************
	private void mp3ParserTags() {
		try {
			input = new FileInputStream(new File(fileLocationApacheTika));
				
			handler = new DefaultHandler();
			metadata = new Metadata();
			parser = new Mp3Parser();
			parseCtx = new ParseContext();
				
			parser.parse(input, handler, metadata, parseCtx);
			input.close();
				
			labTitle.setText("Title: " + metadata.get("title"));
			labArtists.setText("Artists: " + metadata.get("xmpDM:artist"));
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TikaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//***************************************************************************************************************************
}
