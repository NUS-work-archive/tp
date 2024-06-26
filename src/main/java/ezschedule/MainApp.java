package ezschedule;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import ezschedule.commons.core.Config;
import ezschedule.commons.core.LogsCenter;
import ezschedule.commons.core.Version;
import ezschedule.commons.exceptions.DataConversionException;
import ezschedule.commons.util.ConfigUtil;
import ezschedule.commons.util.StringUtil;
import ezschedule.logic.Logic;
import ezschedule.logic.LogicManager;
import ezschedule.model.Model;
import ezschedule.model.ModelManager;
import ezschedule.model.ReadOnlyScheduler;
import ezschedule.model.ReadOnlyUserPrefs;
import ezschedule.model.Scheduler;
import ezschedule.model.UserPrefs;
import ezschedule.model.util.SampleDataUtil;
import ezschedule.storage.JsonSchedulerStorage;
import ezschedule.storage.JsonUserPrefsStorage;
import ezschedule.storage.SchedulerStorage;
import ezschedule.storage.Storage;
import ezschedule.storage.StorageManager;
import ezschedule.storage.UserPrefsStorage;
import ezschedule.ui.Ui;
import ezschedule.ui.UiManager;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Runs the application.
 */
public class MainApp extends Application {

    public static final Version VERSION = new Version(1, 4, 0, true);

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);

    protected Ui ui;
    protected Logic logic;
    protected Storage storage;
    protected Model model;
    protected Config config;

    @Override
    public void init() throws Exception {
        logger.info("=============================[ Initializing Scheduler ]===========================");
        super.init();

        AppParameters appParameters = AppParameters.parse(getParameters());
        config = initConfig(appParameters.getConfigPath());

        UserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(config.getUserPrefsFilePath());
        UserPrefs userPrefs = initPrefs(userPrefsStorage);
        SchedulerStorage schedulerStorage = new JsonSchedulerStorage(userPrefs.getSchedulerFilePath());
        storage = new StorageManager(schedulerStorage, userPrefsStorage);

        initLogging(config);

        model = initModelManager(storage, userPrefs);
        logic = new LogicManager(model, storage);
        ui = new UiManager(logic);
    }

    /**
     * Returns a {@code ModelManager} with the data from {@code storage}'s scheduler and {@code userPrefs}. <br>
     * The data from the sample scheduler will be used instead if {@code storage}'s scheduler is not found,
     * or an empty scheduler will be used instead if errors occur when reading {@code storage}'s scheduler.
     */
    private Model initModelManager(Storage storage, ReadOnlyUserPrefs userPrefs) {
        Optional<ReadOnlyScheduler> schedulerOptional;
        ReadOnlyScheduler initialData;
        try {
            schedulerOptional = storage.readScheduler();
            if (!schedulerOptional.isPresent()) {
                logger.info("Data file not found. Will be starting with a sample Scheduler");
            }
            initialData = schedulerOptional.orElseGet(SampleDataUtil::getSampleScheduler);
        } catch (DataConversionException e) {
            logger.warning("Data file not in the correct format. Will be starting with an empty Scheduler");
            initialData = new Scheduler();
        } catch (IOException e) {
            logger.warning("Problem while reading from the file. Will be starting with an empty Scheduler");
            initialData = new Scheduler();
        }

        return new ModelManager(initialData, userPrefs);
    }

    private void initLogging(Config config) {
        LogsCenter.init(config);
    }

    /**
     * Returns a {@code Config} using the file at {@code configFilePath}. <br>
     * The default file path {@code Config#DEFAULT_CONFIG_FILE} will be used instead
     * if {@code configFilePath} is null.
     */
    protected Config initConfig(Path configFilePath) {
        Config initializedConfig;
        Path configFilePathUsed;

        configFilePathUsed = Config.DEFAULT_CONFIG_FILE;

        if (configFilePath != null) {
            logger.info("Custom Config file specified " + configFilePath);
            configFilePathUsed = configFilePath;
        }

        logger.info("Using config file : " + configFilePathUsed);

        try {
            Optional<Config> configOptional = ConfigUtil.readConfig(configFilePathUsed);
            initializedConfig = configOptional.orElse(new Config());
        } catch (DataConversionException e) {
            logger.warning("Config file at " + configFilePathUsed + " is not in the correct format. "
                    + "Using default config properties");
            initializedConfig = new Config();
        }

        //Update config file in case it was missing to begin with or there are new/unused fields
        try {
            ConfigUtil.saveConfig(initializedConfig, configFilePathUsed);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }
        return initializedConfig;
    }

    /**
     * Returns a {@code UserPrefs} using the file at {@code storage}'s user prefs file path,
     * or a new {@code UserPrefs} with default configuration if errors occur when
     * reading from the file.
     */
    protected UserPrefs initPrefs(UserPrefsStorage storage) {
        Path prefsFilePath = storage.getUserPrefsFilePath();
        logger.info("Using prefs file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataConversionException e) {
            logger.warning("UserPrefs file at " + prefsFilePath + " is not in the correct format. "
                    + "Using default user prefs");
            initializedPrefs = new UserPrefs();
        } catch (IOException e) {
            logger.warning("Problem while reading from the file. Will be starting with an empty Scheduler");
            initializedPrefs = new UserPrefs();
        }

        // Update prefs file in case it was missing to begin with or there are new/unused fields
        try {
            storage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }

        return initializedPrefs;
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting Scheduler " + ezschedule.MainApp.VERSION);
        primaryStage.setMaximized(true);
        ui.start(primaryStage);
    }

    @Override
    public void stop() {
        logger.info("============================ [ Stopping Scheduler ] =============================");
        try {
            storage.saveUserPrefs(model.getUserPrefs());
        } catch (IOException e) {
            logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
        }
    }
}
