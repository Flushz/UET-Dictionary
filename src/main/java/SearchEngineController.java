package main.java;

import com.voicerss.tts.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import main.java.model.Dictionary;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.io.*;
import java.util.Map;
import java.util.Random;

public class SearchEngineController extends Dictionary {

    private static final String API_KEY = "cc4d6af20685439b9b77544383b558fc";
    private static final String APPLICATION_ICON_PATH = "icon.png";
    private static final String APPLICATION_NAME = "UET-Dictionary";
    private static final String AUDIO_OUTPUT_FILE_LOCATION = "src/main/resources/word_pronunciation.wav";
    private static final String BOOKMARKED_COLOR = "-fx-fill: #fce877";
    private static final String EMPTY_STRING = "";
    private static final String HISTORY_FILE_PATH = "src/main/resources/history.txt";
    private static final String NON_BOOKMARKED_COLOR = "-fx-fill: #9e9e9e";
    private static final String SEARCH_ENGINE_FILE_PATH = "view/fxml/SearchEngine.fxml";

    ObservableList<String> relatedWords = FXCollections.observableArrayList();

    @FXML
    private Label warningMessageLabel;
    @FXML
    private FontAwesomeIconView warningIcon;
    @FXML
    private FontAwesomeIconView pronunciationIcon;
    @FXML
    private FontAwesomeIconView bookmarkIcon;
    @FXML
    private TextField wordToSearchField;
    @FXML
    private TextField wordTargetField;
    @FXML
    private ListView<String> relatedWordList;
    @FXML
    private ListView<String> dictionaryList;
    @FXML
    private ListView<String> historyList;
    @FXML
    private ListView<String> bookmarkedWordList;
    @FXML
    private WebView wordDefinitionView;
    @FXML
    private WebView amazingWordView;
    @FXML
    private static AnchorPane rootLayout;

    public static void launchMainInterface() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource(SEARCH_ENGINE_FILE_PATH));
            rootLayout = loader.load();
            Scene scene = new Scene(rootLayout);
            Stage mainWindow = new Stage();
            mainWindow.getIcons().add(new Image(APPLICATION_ICON_PATH));
            mainWindow.setTitle(APPLICATION_NAME);
            mainWindow.setScene(scene);
            mainWindow.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setIdComponents() {
        wordToSearchField.setId("wordToSearch");
        rootLayout.setId("rootLayout");
    }

    /**
     * Main search engine when press Enter.
     */
    public void searchForWord() {
        showRelatedWordList();
    }

    /**
     * Show searched word if there is one in dictionary.
     */
    public void search() {
        if (dictionary.containsKey(wordToSearchField.getText())) {
            wordTargetField.setText(wordToSearchField.getText());
            wordDefinitionView.getEngine().loadContent(dictionary.get(wordToSearchField.getText()));
            pronunciationIcon.setVisible(true);
            bookmarkIcon.setVisible(true);
            updateBookmarkIconColor();
            if (!searchedWords.contains(wordToSearchField.getText())){
                addToHistory();
            }
        }
    }

    /**
     * Show related word list.
     */
    public void showRelatedWordList() {
        String pattern = wordToSearchField.getText();
        if (!pattern.equals("")) {
            relatedWords.clear();
            boolean isExisted = false;
            for (Map.Entry<String, String> word : dictionary.entrySet()) {
                if (word.getKey().startsWith(pattern)) {
                    relatedWords.add(word.getKey());
                    isExisted = true;
                }
            }
            warningIcon.setVisible(!isExisted);
            warningMessageLabel.setVisible(!isExisted);
            relatedWordList.getItems().clear();
            relatedWordList.getItems().addAll(relatedWords);
            relatedWordList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        }
    }

    /**
     * Show definition from related word list.
     */
    public void getDefinitionFromRelatedWordList() {
        String selectedWord = relatedWordList.getSelectionModel().getSelectedItem();
        if (selectedWord != null) {
            wordTargetField.setText(selectedWord);
            wordDefinitionView.getEngine().loadContent(dictionary.get(selectedWord));
            pronunciationIcon.setVisible(true);
            bookmarkIcon.setVisible(true);
            updateBookmarkIconColor();
            addToHistory();
        }
    }

    public void getDefinitionFromHistoryList() {
        String selectedWord = historyList.getSelectionModel().getSelectedItem();
        if (selectedWord != null) {
            wordTargetField.setText(selectedWord);
            wordDefinitionView.getEngine().loadContent(dictionary.get(selectedWord));
            pronunciationIcon.setVisible(true);
            bookmarkIcon.setVisible(true);
            updateBookmarkIconColor();
            addToHistory();
        }
    }

    public void getDefinitionFromBookmarkList() {
        String selectedWord = bookmarkedWordList.getSelectionModel().getSelectedItem();
        if (selectedWord != null) {
            wordTargetField.setText(selectedWord);
            wordDefinitionView.getEngine().loadContent(dictionary.get(selectedWord));
            pronunciationIcon.setVisible(true);
            bookmarkIcon.setVisible(true);
            updateBookmarkIconColor();
            addToHistory();
        }
    }

    public void getDefinitionFromDictionaryList() {
        String selectedWord = dictionaryList.getSelectionModel().getSelectedItem();
        if (selectedWord != null) {
            wordTargetField.setText(selectedWord);
            wordDefinitionView.getEngine().loadContent(dictionary.get(selectedWord));
            pronunciationIcon.setVisible(true);
            bookmarkIcon.setVisible(true);
            updateBookmarkIconColor();
            addToHistory();
        }
    }

    public void clearTextField() {
        if (canBeDeleted()) {
            wordToSearchField.clear();
        }
    }

    public void pronounceWord() throws Exception {
        textToSpeech();
        playPronunciationFile();
    }

    public void textToSpeech() throws Exception {
        String wordTarget = relatedWordList.getSelectionModel().getSelectedItem();
        if (wordTarget == null) {
            wordTarget = wordToSearchField.getText();
        }
        VoiceProvider tts = new VoiceProvider(API_KEY);

        VoiceParameters params = new VoiceParameters(wordTarget, Languages.English_UnitedStates);
        params.setCodec(AudioCodec.WAV);
        params.setFormat(AudioFormat.Format_44KHZ.AF_44khz_16bit_stereo);
        params.setBase64(false);
        params.setSSML(false);
        params.setRate(0);

        byte[] voice = tts.speech(params);

        FileOutputStream fos = new FileOutputStream(AUDIO_OUTPUT_FILE_LOCATION);
        fos.write(voice, 0, voice.length);
        fos.flush();
        fos.close();
    }

    public void playPronunciationFile() throws IOException {
        InputStream in = new FileInputStream(AUDIO_OUTPUT_FILE_LOCATION);
        AudioStream sound = new AudioStream(in);
        AudioPlayer.player.start(sound);
    }

    public void addToHistory() {
        try {
            String selectedWord = wordTargetField.getText();
            if (!searchedWords.contains(selectedWord)) {
                searchedWords.add(selectedWord);
                FileWriter fileWriter = new FileWriter(HISTORY_FILE_PATH, true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(selectedWord+"\n");
                bufferedWriter.close();
            }
        } catch(IOException exception) {
            exception.printStackTrace();
        }
    }

    public void showHistorySearch() {
        ObservableList<String> searchedWordObservableList = FXCollections.observableArrayList();
        searchedWordObservableList.addAll(searchedWords);
        historyList.getItems().clear();
        historyList.getItems().addAll(searchedWordObservableList);
        historyList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    public void generateAmazingWord() {
        Random randomIndex = new Random();
        int upperbound = virtualDictionary.size();
        int randomWordIndex = randomIndex.nextInt(upperbound);
        String amazingWord = virtualDictionary.get(randomWordIndex);
        amazingWordView.getEngine().loadContent(dictionary.get(amazingWord));
    }

    public void showDictionary() {
        ObservableList<String> dictionaryObservableList = FXCollections.observableArrayList();
        dictionaryObservableList.addAll(virtualDictionary);
        dictionaryList.getItems().clear();
        dictionaryList.getItems().addAll(dictionaryObservableList);
        dictionaryList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    public void updateBookmarkIconColor() {
        if (bookmarkedWords.contains(wordTargetField.getText())) {
            bookmarkIcon.setStyle(BOOKMARKED_COLOR);
        } else {
            bookmarkIcon.setStyle(NON_BOOKMARKED_COLOR);
        }
    }

    public void updateBookmarkedWords() {
        if (bookmarkedWords.contains(wordTargetField.getText())) {
            bookmarkedWords.remove(wordTargetField.getText());
        } else {
            bookmarkedWords.add(wordTargetField.getText());
        }
        updateBookmarkIconColor();
        updateBookmark();
    }

    public void showBookmark() {
        ObservableList <String> bookmarkedWordObservableList = FXCollections.observableArrayList();
        bookmarkedWordObservableList.addAll(bookmarkedWords);
        bookmarkedWordList.getItems().clear();
        bookmarkedWordList.getItems().addAll(bookmarkedWordObservableList);
        bookmarkedWordList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    public void openAddNewWordWindow() {
        NewWordBoxController.openNewWordBox();
    }

    public void openEditWordWindow() {
        if (!wordToSearchField.getText().equals("")){
            EditBoxController.openEditBox(wordToSearchField.getText());
        }
    }

    public void openDeleteWordWindow() {
        DeleteWordController.openDeleteWordWindow();
    }

    public void openAboutUsWindow() {
        AboutUsController.openAboutUsWindow();
    }

    public boolean canBeDeleted() {
        return !wordToSearchField.getText().equals(EMPTY_STRING);
    }
}
