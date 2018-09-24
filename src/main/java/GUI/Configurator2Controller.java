package GUI;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class Configurator2Controller implements Initializable
{

    @FXML
    private AnchorPane page3;

    @FXML
    private ListView<String> PoEsList;

    @FXML
    private AnchorPane page1;

    @FXML
    private AnchorPane page2;
    @FXML
    private RadioButton rCustom;

    @FXML
    private Text label_path_install;

    @FXML
    private AnchorPane page4;

    @FXML
    private TextArea txtDump;

    private int curr_page = 1;

    @FXML
    void nextPage()
    {
        switch (curr_page)
        {
            case 1 : page2_show(); break;
            case 2 : page3_show(); break;
            case 3 : page4_show(); break;
            case 4 : launch_launcher(); break;
        }
    }

    private void launch_launcher()
    {
        // Save Settings
        Data2.getINSTANCE().setPoe_paths(poe_paths);
        try
        {
            Data2.getINSTANCE().setData(new launcher_data(install_dir, Configurator.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()));
        }
        catch (URISyntaxException e)
        {
            System.err.println("Can't find launcher location");
            Data2.getINSTANCE().setData(new launcher_data(install_dir, "0"));
        }
        Data2.getINSTANCE().saveLauncherData();
        Data2.getINSTANCE().savePoEpaths();
        Data2.getINSTANCE().createFolders();

        Configurator.stage.close();
        LauncherUI launcherUI = new LauncherUI();
        try
        {
            launcherUI.start(new Stage());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void page4_show()
    {
        if (poe_paths.size() > 0)
        {
            showAndHide(page3, page4);
            txtDump.setText(genTextDump());
            curr_page++;
        }
    }

    private String genTextDump()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("This data will be saved:\n\nInstall Directory: ");
        stringBuilder.append(install_dir);
        stringBuilder.append("\n\n");
        stringBuilder.append("PoE Executables:\n");
        for (String s : poe_paths)
        {
            stringBuilder.append(s);
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    private void page3_show()
    {
        if (rCustom.isSelected())
        {
            if (label_path_install.getText().equals("Directory has not been set!"))
            {
                label_path_install.setVisible(true);
                return;
            }
        }
        showAndHide(page2, page3);
        curr_page++;
    }

    private void page2_show()
    {
        Platform.runLater(() -> showAndHide(page1, page2));
        curr_page++;
    }

    private void showAndHide(AnchorPane from, AnchorPane to)
    {
        from.setVisible(false);
        to.setVisible(true);
    }

    /**
     * Method for Opening the DirectoryChooser.
     */
    public File browse(String title)
    {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(title);
        return directoryChooser.showDialog(Configurator.stage);
    }

    private String install_dir = System.getenv("LOCALAPPDATA") + File.separator + "PAL";

    public void browseCustom()
    {
        File f = browse("Select a folder to install PAL to.");
        if (f.exists())
        {
            if (f.canWrite())
            {
                install_dir = f.getPath();
                Platform.runLater(() -> label_path_install.setText(install_dir));
                label_path_install.setVisible(true);
            }
            else
            {
                System.err.println("ERROR: CAN'T WRITE IN THIS FOLDER");
            }
        }
    }

    public void remove()
    {
        String selected = PoEsList.getSelectionModel().getSelectedItem();
        assert selected != null;

        PoEsList.getItems().remove(selected);
    }

    public void add()
    {
        File dir = browse("Select a Path of Exile Folder");
        if (dir.exists())
        {
            if (dir.isDirectory())
            {
                File[] filesInDir = dir.listFiles();
                checkDir(filesInDir);
            }
        }
    }

    private void checkDir(File[] filesInDir)
    {
        for (File f : filesInDir)
        {
            final String filename = f.getName();
            if (filename.equals("PathOfExile_x64Steam.exe"))
            {
                addToList(f.getPath());
            }
            else if (filename.equals("PathOfExile_x64.exe"))
            {
                addToList(f.getPath());
            }
            else if (filename.contains(".exe") && filename.contains("PathOfExile"))
            {
                addToList(f.getPath());
            }
            else
            {
                // No PoE Dir.
            }
        }
    }

    private ObservableList<String> poe_paths = FXCollections.observableArrayList();

    private void addToList(String path)
    {
        //Concat
        poe_paths.add(path);
        PoEsList.refresh();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        PoEsList.setItems(poe_paths);
    }
}