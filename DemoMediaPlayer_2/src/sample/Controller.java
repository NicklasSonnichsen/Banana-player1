package sample;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.media.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javax.swing.text.View;
import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private MediaView mediaV;
    private MediaPlayer mp;
    private Media me;
    private String[] songUrls;
    private int nextSongIndex;
    private String[] songPaths;

    @FXML
    private TextField textfieldSearch;
    @FXML
    private TextField textfieldCurrentlyPlaying;
    @FXML
    private ListView<String> newListview;
    @FXML
    private  ListView<String> listView;


    /**
     * This method is invoked automatically in the beginning. Used for initializing, loading data etc.
     *
     * @param location
     * @param resources
     */
    public void initialize(URL location, ResourceBundle resources){
        // Build the path to the location of the media file
        String path = new File("src/sample/media/1.mp3").getAbsolutePath();
        String path2 = new File("src/sample/media/2.mp3").getAbsolutePath();
        String path3 = new File("src/sample/media/3.mp3").getAbsolutePath();
        String path4 = new File("src/sample/media/4.mp3").getAbsolutePath();
        String path5 = new File("src/sample/media/5.mp3").getAbsolutePath();
        String path6 = new File("src/sample/media/6.mp3").getAbsolutePath();
        String path7 = new File("src/sample/media/7.mp3").getAbsolutePath();
        String path8 = new File("src/sample/media/8.mp3").getAbsolutePath();
        String path9 = new File("src/sample/media/9.mp3").getAbsolutePath();

        songUrls = new String[9];
        songUrls[0] = path;
        songUrls[1] = path2;
        songUrls[2] = path3;
        songUrls[3] = path4;
        songUrls[4] = path5;
        songUrls[5] = path6;
        songUrls[6] = path7;
        songUrls[7] = path8;
        songUrls[8] = path9;

        songPaths = new String[9];
        songPaths[0]= "src/sample/media/1.mp3";
        songPaths[1]= "src/sample/media/2.mp3";
        songPaths[2]= "src/sample/media/3.mp3";
        songPaths[3]= "src/sample/media/4.mp3";
        songPaths[4]= "src/sample/media/5.mp3";
        songPaths[5]= "src/sample/media/6.mp3";
        songPaths[6]= "src/sample/media/7.mp3";
        songPaths[7]= "src/sample/media/8.mp3";
        songPaths[8]= "src/sample/media/9.mp3";

        // adds all our songs to a list view
        ObservableList<String> list = FXCollections.observableArrayList("Grim_Reaper","On & On","Fig Leaf Times Two","Iron Horse","Merry Go","Royal Banana","Royalty Metal","Feel Good","Villainous Treachery");

        newListview.setItems(list);
        newListview.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // adds all our Playlist's to a list view
        DB.selectSQL("select * from tblPlaylist ");

        ObservableList<String> list2 = FXCollections.observableArrayList();

        do {
            String data = DB.getData();
            if (data.equals(DB.NOMOREDATA)) {
                break;
            } else {
                list2.add(data);
            }
        } while (true);

        listView.setItems(list2);
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Create new Media object (the actual media content)
        me = new Media(new File(songUrls[nextSongIndex++]).toURI().toString());

        // Create new MediaPlayer and attach the media to be played
        mp = new MediaPlayer(me);
        //
        mediaV.setMediaPlayer(mp);
        // mp.setAutoPlay(true);
        // If autoplay is turned of the method play(), stop(), pause() etc controls how/when medias are played
        mp.setAutoPlay(false);

        displaySong();


        // plays the next song in our "playlist". by calling the playNextSong method when the media ends
        // and creates a new media, we got some help to use the lambda
        mp.setOnEndOfMedia(() -> {
            playNextSong();
        });
    }

    /**
     *
     * this method makes it possible to play the next when a song ends
     */
    private void playNextSong(){
        mediaV.setMediaPlayer(new MediaPlayer(new Media(new File(songUrls[nextSongIndex++]).toURI().toString())));
        mediaV.getMediaPlayer().play();

    }

    /**
     * displays the currently song playing
     */
    private void displaySong()
    {
        mediaV.mediaPlayerProperty().addListener(new ChangeListener<MediaPlayer>() {
            @Override
            public void changed(ObservableValue<? extends MediaPlayer> observable, MediaPlayer oldValue, MediaPlayer newValue) {

                ObservableList<String> songNames;
                songNames= newListview.getSelectionModel().getSelectedItems();

                textfieldCurrentlyPlaying.setText(songNames.toString());
            }
        });
    }

    @FXML
    /**
     * Handler for the play button
     */
    private void handlePlay()
    {
        mediaV.getMediaPlayer().play();
    }

    @FXML
    /**
     * handler for the pause button
     */
    private void handlePause()
    {
        mediaV.getMediaPlayer().pause();
    }
    @FXML
    /**
     * handler for the stop button
     */
    private void handleStop()
    {
        mediaV.getMediaPlayer().stop();
    }
    @FXML
    private void handleSkip()
    {
        mediaV.getMediaPlayer().stop();
        // select the next song to play
        mediaV.setMediaPlayer(new MediaPlayer(new Media(new File(songUrls[nextSongIndex]).toURI().toString())));
        mediaV.getMediaPlayer().setOnEndOfMedia(() -> {
            playNextSong();

        });
        mediaV.getMediaPlayer().play();

        // this selects the song title and displays it.
        DB.selectSQL("select fldSongTitle from tblSongs where fldFilePath = '"+songPaths[nextSongIndex++]+"' ");

        String data = DB.getData();

        textfieldCurrentlyPlaying.setText(data);
        // this is to avoid not choosing a song that is not in songpaths
        if(nextSongIndex>=songPaths.length)
        {
            nextSongIndex=0;
        }

    }
    @FXML
    /**
     * handles backSkip button
     * it resets the currently song playing
     */
    private void handleBackSkip()
    {
        mediaV.getMediaPlayer().seek(mediaV.getMediaPlayer().getStartTime());
        mediaV.getMediaPlayer().stop();
        mediaV.getMediaPlayer().play();
        if(nextSongIndex<0)
        {
            nextSongIndex=0;
        }

    }
    @FXML
    /**
     * Handles search button
     */
    private void handleSearch()
    {
        // gets the input from our textfield
        String searcher = textfieldSearch.getText();
        // selects a song title that contains whats put in the textfield
        DB.selectSQL("select fldSongTitle from tblSongs where fldSongTitle LIKE '%"+searcher+"%'");

        // this makes it possible to see multiple search results, shows them in the list view
        ObservableList<String> list = FXCollections.observableArrayList();
        do {
            String data = DB.getData();
            if (data.equals(DB.NOMOREDATA)) {
                break;
            } else {
                list.add(data);
            }
        } while (true);
        newListview.setItems(list);
        newListview.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    }


    @FXML
    /**
     * Handler for the create new Playlist button
     */
    private void handleNewPlaylist()
    {
        // makes a "pop-up" so you can make a new playlist
        TextInputDialog dialog = new TextInputDialog("MyPlaylist");
        dialog.setTitle("User input required");
        dialog.setHeaderText("Create a new Playlist");
        dialog.setContentText("Please enter name of playlist:");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            String playList = result.get();
            System.out.println(playList);
            // inserts the new playlist name created in our table tblPlaylist
            DB.insertSQL("Insert into tblPlaylist values('"+playList+"');");

            // to reload the list view so you can see the new playlist made
            DB.selectSQL("select * from tblPlaylist ");
            ObservableList<String> list2 = FXCollections.observableArrayList();
            do {
                String data = DB.getData();
                if (data.equals(DB.NOMOREDATA)) {
                    break;
                } else {
                    list2.add(data);
                }
            } while (true);

            listView.setItems(list2);
            listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        }
    }
    @FXML
    /**
     * handler for the add to playlist button
     */
    private void handleAddToPlaylist()
    {
        // to get the name of the song select in our song list view
        ObservableList<String> songNames;
        songNames= newListview.getSelectionModel().getSelectedItems();

        // this is to get all of the play lists made
        DB.selectSQL("select * from tblPlaylist ");

        ObservableList<String> list2 = FXCollections.observableArrayList();
        // puts the playlist's into a list called list2
        do {
            String data = DB.getData();
            if (data.equals(DB.NOMOREDATA)) {
                break;
            } else {
                list2.add(data);
            }
        } while (true);

        listView.setItems(list2);
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // creates a "pop-up" where you can select which playlist to add the song to
                ChoiceDialog<String> dialog = new ChoiceDialog<>("", list2);
        dialog.setTitle("Add to playlist");
        dialog.setHeaderText("Look, a Add to playlist function");
        dialog.setContentText("Choose your Playlist:");

        // to get the result
        Optional<String> result = dialog.showAndWait();
        // selects the filePath to the song where song title contains the songNames
        DB.selectSQL("select fldFilePath from tblSongs where fldSongTitle LIKE '%"+songNames+"%'");
        String filePath = DB.getData();
        // selects Artist where songTitle is songNames
        DB.selectSQL("select fldArtist from tblSongs where fldSongTitle = '"+songNames+"'");
        String artist = DB.getData();
        // this getData is to not get the pending data error
        DB.getData();
        // inserts the information into our tblMusic in the database
        DB.insertSQL("insert into tblMusic values('"+songNames+"','"+artist+"','"+result.get()+"','"+filePath+"');");

    }

    @FXML
    private void handleEditPlaylist()
    {
        // selects everything from tblPlaylist
        DB.selectSQL("select * from tblPlaylist ");
        // creating a list
        ObservableList<String> list2 = FXCollections.observableArrayList();
        // Inserts the data from the sql select statement into the list
        do {
            String data = DB.getData();
            if (data.equals(DB.NOMOREDATA)) {
                break;
            } else {
                list2.add(data);
            }
        } while (true);

        listView.setItems(list2);
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        // creates a "pop-up" where you can choose a playlist from the list we have created
        ChoiceDialog<String> dialog2 = new ChoiceDialog<>("", list2);
        dialog2.setTitle("Edit Playlist");
        dialog2.setHeaderText("Edit Playlist");
        dialog2.setContentText("Choose your Playlist");

        // to get the result
        Optional<String> result2 = dialog2.showAndWait();

        // to choose if you want to delete or change the name of your playlist
        ChoiceDialog<String> dialog3 = new ChoiceDialog<>("", "Delete Playlist","Change Name of playlist");
        dialog3.setTitle("Edit Playlist");
        dialog3.setHeaderText("Edit Playlist");
        dialog3.setContentText("Choose");

        // to get the result
        Optional<String> result3 = dialog3.showAndWait();

        String resultFromChoice = result3.get();


        // changes the name of a playlist
        if(resultFromChoice.equals("Change Name of playlist")) {
            TextInputDialog dialog = new TextInputDialog("MyPlaylist");
            dialog.setTitle("User input required");
            dialog.setHeaderText("Edit Playlist");
            dialog.setContentText("Please enter new name for playlist");

            // to get the result
            Optional<String> result = dialog.showAndWait();
            // updates/changes the name that the user inputs
            DB.updateSQL("update tblPlaylist set fldPlaylistName ='" + result.get() + "' where fldPlaylistName ='" + result2.get() + "'; ");
            // this is to reload/update the list view where we show the playlist's
            DB.selectSQL("select * from tblPlaylist ");

            ObservableList<String> list3 = FXCollections.observableArrayList();

            do {
                String data = DB.getData();
                if (data.equals(DB.NOMOREDATA)) {
                    break;
                } else {
                    list3.add(data);
                }
            } while (true);

            listView.setItems(list3);
            listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        }

        // deletes a chosen playlist

        else if(resultFromChoice.equals("Delete Playlist"))
        {
            /*
            * to make this delete statement we had to add a ON DELETE CASCADE in sql.
             * because there is a relationship between tblPlaylist and our tblMusic*/
            DB.deleteSQL("delete from tblPlaylist where fldPlaylistName = '"+result2.get()+"'");
            // this is to reload/update the list view where we show the playlist's
            DB.selectSQL("select * from tblPlaylist ");

            ObservableList<String> list4 = FXCollections.observableArrayList();

            do {
                String data = DB.getData();
                if (data.equals(DB.NOMOREDATA)) {
                    break;
                } else {
                    list4.add(data);
                }
            } while (true);

            listView.setItems(list4);
            listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        }
    }

    @FXML
    /**
     * handler for when you click a song and it plays
     * so when you click a song in the gui it plays
     */
    private void handleClickPlay()
    {
            ObservableList<String> songNames;
            songNames= newListview.getSelectionModel().getSelectedItems();
        for (String songName : songNames)
        {
            DB.selectSQL("select fldFilePath from tblSongs where fldSongTitle LIKE '%"+songName+"%'");

            String data2 = DB.getData();
            mediaV.getMediaPlayer().stop();
            mediaV.setMediaPlayer(new MediaPlayer(new Media(new File(data2).toURI().toString())));
            mediaV.getMediaPlayer().setOnEndOfMedia(() -> {
                playNextSong();
            });

            mediaV.getMediaPlayer().play();
        }

    }

    @FXML
    /**
     * handles when you click a playlist it plays the songs in the chosen playlist
     */
    private void handlePlaylistPlay()
    {
        ObservableList<String> songNames;
        songNames= listView.getSelectionModel().getSelectedItems();
        for (String songName : songNames)
        {
            DB.selectSQL("select fldFilePath from tblMusic where fldPlaylistName LIKE '%"+songName+"%'");

            String data2 = DB.getData();
            mediaV.getMediaPlayer().stop();
            mediaV.setMediaPlayer(new MediaPlayer(new Media(new File(data2).toURI().toString())));
            DB.selectSQL("select fldSongTitle from tblSongs where fldFilePath = '"+data2+"' ");

            String data = DB.getData();

            textfieldCurrentlyPlaying.setText(data);

            mediaV.getMediaPlayer().setOnEndOfMedia(() -> {
                playNextSong();

                DB.selectSQL("select fldSongTitle from tblSongs where fldFilePath = '"+data2+"' ");

                textfieldCurrentlyPlaying.setText(data);
            });

            mediaV.getMediaPlayer().play();
        }



    }









}