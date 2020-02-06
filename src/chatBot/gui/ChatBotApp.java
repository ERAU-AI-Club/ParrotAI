package chatBot.gui;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import chatBot.bots.BabyYodaBot;
import chatBot.bots.ChatBot;
import chatBot.bots.ParrotBot;
import chatBot.bots.RandomBot;
import chatBot.bots.EchoBot;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 * 
 * 
 * @author Mohammad Alali (Sanavesa)
 */
public class ChatBotApp extends Application
{
	/** The application's minimum possible width in pixels. */
	public static final int APP_MIN_WIDTH = 640;
	
	/** The application's minimum possible height in pixels. */
	public static final int APP_MIN_HEIGHT = 480;
	
	/** The path of the application's stylesheet file. */
	public static final String APP_STYLESHEET_PATH = "res/stylesheet.css";
	
	/** The path of the user's image file. */
	public static final String USER_IMAGE_PATH = "res/UserIcon.png";
	
	/** The minimum duration, in milliseconds, to wait between messages to add realism. */
	public static final int CHAT_BOT_MIN_DELAY = 500;
	
	/** The maximum duration, in milliseconds, to wait between messages to add realism. */
	public static final int CHAT_BOT_MAX_DELAY = 1500;
	
	/** The available chat bots that the user can select. */
	public static final List<Class<? extends ChatBot>> AVAILABLE_CHATBOTS = List.of(
			ParrotBot.class,
			EchoBot.class,
			RandomBot.class,
			BabyYodaBot.class);
	
	/** The executor is used to call methods after a period of time on another thread (its used to simulate delay between the bot's responses). */
	private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	
	/**
	 * A list used by the {@link #executor} of any pending tasks that haven't finished.
	 * Specifically, this exists so that when the user types to the bot and is awaiting a response,
	 * and in the meantime, changes the bot to something else, then the first bot will reply.
	 * To combat this bug, we store all scheduled tasks, and when the chat bot has changed, all of those tasks are suspended.
	 * */
	private final List<ScheduledFuture<?>> executorScheduledTasks = new ArrayList<>();
	
	/** This boolean property holds whether or not the bot is typing, to add realism between responses. */
	private final BooleanProperty isBotTyping = new SimpleBooleanProperty(false);
	
	/** The chatbot that the application is using. Will start with the first chatbot in {@link #AVAILABLE_CHATBOTS} */
	private ChatBot chatBot;
	
	/** The image of the bot, loaded in run-time. */
	private Image botImage;
	
	/** The image of the user, loaded in run-time. */
	private Image userImage;

	/** The chat box used in the app, created in run-time. */
	private ChatBox chatBox;

	/** The typing notification used for the bot. It is used in conjunction with {@link #isBotTyping} to show 'Bot is typing...'. Created in run-time. */
	private TypingNotificationMessage botTypingNotification;

	/** The root node of the application, created in run-time. */
	private BorderPane root;

	/** The scene of the application, created in run-time. */
	private Scene scene;
	
	/** The stage of the application, loaded in run-time. */
	private Stage stage;
	
	/**
	 * Called when JavaFX is ready to begin the program.
	 */
	@Override
	public void start(Stage stage) throws Exception
	{
		// Validation check
		Objects.requireNonNull(APP_STYLESHEET_PATH, "The 'APP_STYLESHEET' constant in ParrotApp cannot be a null value.");
		Objects.requireNonNull(USER_IMAGE_PATH, "The 'USER_IMAGE_PATH' constant in ParrotApp cannot be a null value.");
		Objects.requireNonNull(AVAILABLE_CHATBOTS.get(0), "The 'AVAILABLE_CHATBOTS' constant in ParrotApp must at least have one element, such as ParrotBot.");
		
		// Initialize essential layout
		this.stage = stage;
		root = new BorderPane();
		scene = new Scene(root);
		
		// Create the chatbot
		chatBot = AVAILABLE_CHATBOTS.get(0).getConstructor().newInstance();
		
		// Load the images of the user and the bot
		userImage = new Image(USER_IMAGE_PATH);
		botImage = new Image(chatBot.getImagePath());
		
		// Create a chatbox
		chatBox = new ChatBox();
		
		// Hook the function for when the sends a message
		chatBox.setOnSend(message -> onUserSentMessage(message));
		
		// Add a typing notification for the bot to add character :)
		botTypingNotification = new TypingNotificationMessage(ChatMessagePosition.Left, chatBot.getName() + " is typing...", botImage);
		chatBox.getScrollPaneContent().getChildren().add(botTypingNotification);
		
		// Style the 'X is typing' and make automatically hide when the parrot is not answering/typing
		botTypingNotification.setLabelCSSId("label_chatbot");
		botTypingNotification.visibleProperty().bind(isBotTyping);
		botTypingNotification.managedProperty().bind(isBotTyping);
		
		// Create a top menu bar to use for the app
		MenuBar menuBar = createAppMenuBar();
		
		// Finalizes the styling and layout of the application
		root.setCenter(chatBox);
		root.setTop(menuBar);
		scene.getStylesheets().add(APP_STYLESHEET_PATH);
		
		// Setup and show the stage/window
		stage.setMinWidth(APP_MIN_WIDTH);
		stage.setMinHeight(APP_MIN_HEIGHT);
		stage.setScene(scene);
		stage.setTitle(chatBot.getName() +  " AI");
		stage.getIcons().add(botImage);
		stage.setOnCloseRequest(e -> executor.shutdown());
		
		// Show the stage, no code can be written after this point.
		stage.show();
	}
	
	/**
	 * Invoked when the user sends a message. Occurs when the user presses the ENTER key or clicks on the Send button.
	 * 
	 * <p>
	 * This will also simulate a response from the chat bot, by adding a small delay before seeing the response.
	 * </p>
	 * @param message the message that the user composed
	 */
	private void onUserSentMessage(String message)
	{
		// Add the user's message
		ChatMessage userMessage = new ChatMessage(ChatMessagePosition.Right, message, userImage);
		userMessage.setLabelCSSId("label_user");
		chatBox.addMessage(userMessage);
		
		// ChatBot response will have a small time delay (0.5-1.5s)
		long delay = (long) (CHAT_BOT_MIN_DELAY + Math.random() * (CHAT_BOT_MAX_DELAY - CHAT_BOT_MIN_DELAY));
		
		// Show the 'X is typing' notification
		isBotTyping.set(true);
		
		// Run a function after some delay to add the ChatBot's reponse
		ScheduledFuture<?> future = executor.schedule(() ->
		{
			// Hide the 'X is typing' notification
			isBotTyping.set(false);
			
			// Platform.runLater is needed since we are accessing JavaFX from another thread
			Platform.runLater(() ->
			{
				// Retrieve a reply from the ChatBot's AI
				String reply = chatBot.getReply(message);
				
				// Add the ChatBot's reply message
				ChatMessage botMessage = new ChatMessage(ChatMessagePosition.Left, reply, botImage);
				botMessage.setLabelCSSId("label_chatbot");
				chatBox.addMessage(botMessage);
			});
		}, delay, TimeUnit.MILLISECONDS);
		
		// Add it to the scheduled tasks
		executorScheduledTasks.add(future);
		
		// Also, filter out all completed schedules
		executorScheduledTasks.removeIf(task -> task.isDone());
	}
	
	/***
	 * Called automatically when the chat bot has changed. Takes care of updating all text and image residuals. 
	 * 
	 * <p>
	 * This can occur in two scenarios:
	 * <ol>
	 * <li>When the user has loaded a chat bot from a file.</li>
	 * <li>When the user using the menus, select a differet chat bot.</li>
	 * </ol>
	 * </p>
	 */
	private void onChatBotChanged()
	{
		// Clear chat
		chatBox.clearMessages();
		
		// Reload the bot image
		botImage = new Image(chatBot.getImagePath());
		
		// Adjust the message of the bot is typing notification
		isBotTyping.set(false);
		botTypingNotification.setMessage(chatBot.getName() + " is typing...");
		botTypingNotification.setIcon(botImage);
		
		// Modify the stage title and icon to match the name of the loaded bot
		stage.setTitle(chatBot.getName() + " AI");
		stage.getIcons().set(0, botImage);
		
		// Reset the executor incase we had pending messages coming from the chat bot
		for(ScheduledFuture<?> future : executorScheduledTasks)
		{
			future.cancel(true);
		}
		executorScheduledTasks.clear();
	}
	
	/**
	 * Called automatically when the user clicks on 'Save Chatbot' in the upper menus.
	 */
	private void onSaveBotClicked()
	{
		// Shows a file chooser for saving
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save Chat Bot");
		fileChooser.setInitialFileName("ChatBot_" + chatBot.getName() + ".chatbot");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("ChatBot File", "*.chatbot"));
		
		File desktopDirectory = new File(System.getProperty("user.home"), "Desktop");
		fileChooser.setInitialDirectory(desktopDirectory);
		
		File saveFile = fileChooser.showSaveDialog(stage);
		if(saveFile != null)
		{
			// Attempt to save, otherwise show error dialog
			boolean successful = ChatBot.save(chatBot, saveFile.getAbsolutePath());
			if(!successful)
			{
				Alert errorAlert = new Alert(AlertType.ERROR);
				errorAlert.setContentText("Failed to save chat bot.");
				errorAlert.setHeaderText("Error!");
				errorAlert.setTitle("Chat Bot Alert");
				errorAlert.showAndWait();
			}
		}
	}
	
	/**
	 * Called automatically when the user clicks on 'Open Chatbot' in the upper menus.
	 */
	private void onOpenBotClicked()
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Chat Bot");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("ChatBot File", "*.chatbot"));
		
		File desktopDirectory = new File(System.getProperty("user.home"), "Desktop");
		fileChooser.setInitialDirectory(desktopDirectory);
		
		File openFile = fileChooser.showOpenDialog(stage);
		if(openFile != null)
		{
			// Attempt to load, otherwise show error dialog
			ChatBot loaded = ChatBot.load(openFile.getAbsolutePath());
			if(loaded != null)
			{
				chatBot = loaded;
				onChatBotChanged();
			}
			else
			{
				Alert errorAlert = new Alert(AlertType.ERROR);
				errorAlert.setContentText("Failed to load chat bot.");
				errorAlert.setHeaderText("Error!");
				errorAlert.setTitle("Chat Bot Alert");
				errorAlert.showAndWait();
			}
		}
	}
	
	private MenuBar createAppMenuBar()
	{
		// Create the menu bar
		MenuBar menuBar = new MenuBar();
		
		// The only menu beneath the menu bar is Chat Bot, just like 'File', 'Edit'..
		Menu chatBotMenu = new Menu("Chat Bot");
		
		// In our menu, we have a 'Clear Chat' option that clears the chatbox
		MenuItem clearChatMenuItem = new MenuItem("Clear Chat");
		clearChatMenuItem.setGraphic(new ImageView("res/Clear.png"));
		clearChatMenuItem.setOnAction(e -> chatBox.clearMessages());
		
		// In our menu, we have a 'Save Bot' option that saves our chat bot to local disk
		MenuItem saveBotMenuItem = new MenuItem("Save Bot");
		saveBotMenuItem.setGraphic(new ImageView("res/Save.png"));
		saveBotMenuItem.setOnAction(e -> onSaveBotClicked());
		
		// In our menu, we have a 'Open Bot' option that opens a chat bot from local disk
		MenuItem openBotMenuItem = new MenuItem("Open Bot");
		openBotMenuItem.setGraphic(new ImageView("res/Open.png"));
		openBotMenuItem.setOnAction(e -> onOpenBotClicked());
		
		// In our menu, we have a 'Change Bot' submenu that shows all available chat bots to select from
		Menu changeBotMenu = new Menu("Change Bot");
		changeBotMenu.setGraphic(new ImageView("res/Edit.png"));
		
		// Dynamically create all menu items for the available chat bots
		List<RadioMenuItem> choices = new ArrayList<>();
		for(Class<? extends ChatBot> clazz : AVAILABLE_CHATBOTS)
		{
			// Create an item for selection for each chat bot class, that once clicked will load it.
			RadioMenuItem choice = new RadioMenuItem(clazz.getSimpleName());
			choices.add(choice);
			
			// Once this option is clicked, load the assigned chat bot class
			choice.setOnAction(e ->
			{
				// Create the chatbot, or show error if failed
				try
				{
					ChatBot loaded = clazz.getConstructor().newInstance();
					if(loaded != null)
					{
						chatBot = loaded;
						onChatBotChanged();
					}
				}
				catch (Exception e2)
				{
					Alert errorAlert = new Alert(AlertType.ERROR);
					errorAlert.setContentText(e2.getMessage());
					errorAlert.setHeaderText("Error!");
					errorAlert.setTitle("Chat Bot Alert");
					errorAlert.showAndWait();
				}
			});
		}
		
		// By default, load the first chat bot class
		choices.get(0).setSelected(true);
		
		// Create a toggle group for all the classes, so that we can only select 1 option from all those radio menu items
		ToggleGroup chatBotsToggleGroup = new ToggleGroup();
		chatBotsToggleGroup.getToggles().addAll(choices);
		
		// Add all of those choices to the 'Change Bot' submenu
		changeBotMenu.getItems().addAll(choices);
		
		// Add all options under the 'Chat Bot' menu such as 'Clear Chat', 'Save Bot', 'Open Bot', 'Change Bot'
		// Note, I added separators between them to make it visually pleasing
		chatBotMenu.getItems().addAll(
				clearChatMenuItem, new SeparatorMenuItem(),
				saveBotMenuItem, openBotMenuItem, new SeparatorMenuItem(),
				changeBotMenu);
		
		// Add a single menu, 'Chat Bot', to the menu bar
		menuBar.getMenus().addAll(chatBotMenu);
		
		return menuBar;
	}
}