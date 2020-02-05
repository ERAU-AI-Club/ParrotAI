package chatBot.gui;

import java.util.Objects;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * The <code>ChatBox</code> class represents a chat box used for 2-way communications.
 * 
 * <p>
 * The {@link #setOnSend(OnSendCallback)} and {@link #getOnSend()} methods are used to control the input field's interaction with the chat box.
 * </p>
 * 
 * <p>
 * Furthermore, a helper method, {@link #addMessage(ChatMessage)} is used to add messages to the chat box.
 * </p>
 * 
 * @see {@link ChatMessage}
 * @see {@link OnSendCallback}
 * @author Mohammad Alali (Sanavesa)
 */
public class ChatBox extends BorderPane
{
	/** A scroll pane that contains {@link #scrollPaneContent}. */
	protected final ScrollPane messagesScrollPane;
	
	/** Contains the content in the scroll pane, namely the {@link #messagesVBox}. */
	protected final VBox scrollPaneContent;
	
	/** A vbox that contains all messages in the chat box. */
	protected final VBox messagesVBox;
	
	/** A textfield that contains the user input for the chat box. */
	protected final TextField inputTextField;
	
	/** A button that will send the user input to the chat box. */
	protected final Button sendButton;
	
	/** A hbox that contains {@link #inputTextField} and {@link #sendButton}. */
	protected final HBox userInputHBox;
	
	/** A callback that is fired when the user pressed the send button (or ENTER). */
	protected OnSendCallback onSendCallback;
	
	/** The vertical spacing, in pixels, between each chat message. */
	public static final int MESSAGES_VERTICAL_SPACING = 15;
	
	/** The padding, in pixels, for the message vbox. */
	public static final int MESSAGES_PADDING = 10;
	
	/** The horizontal spacing, in pixels, between each the user text field and the send button. */
	public static final int USER_INPUT_HORIZONTAL_SPACING = 10;
	
	/** The padding, in pixels, for the user input hbox. */
	public static final int USER_INPUT_PADDING = 10;
	
	/**
	 * Constructs a new chat box for 2-way communications.
	 */
	public ChatBox()
	{
		messagesVBox = new VBox();
		scrollPaneContent = new VBox(messagesVBox);
		messagesScrollPane = new ScrollPane(scrollPaneContent);
		inputTextField = new TextField();
		sendButton = new Button("SEND");
		userInputHBox = new HBox(USER_INPUT_HORIZONTAL_SPACING, inputTextField, sendButton);
		onSendCallback = null;
		
		initializeGUI();
	}
	
	/**
	 * Fully initializes and sets up the GUI elements in the chat box.
	 */
	protected void initializeGUI()
	{
		// Make the scroll pane always fill the available space
		messagesScrollPane.setFitToWidth(true);
		messagesScrollPane.setFitToHeight(true);
		
		// Setup styling and layout of the scroll pane's content vbox
		scrollPaneContent.setSpacing(MESSAGES_VERTICAL_SPACING);
		scrollPaneContent.setId("chat_background");
		scrollPaneContent.setPadding(new Insets(MESSAGES_PADDING));
				
		// Setup styling and layout of the messages vbox
		messagesVBox.setSpacing(MESSAGES_VERTICAL_SPACING);
		messagesVBox.setId("chat_background");
		
		// Scroll down the scroll pane whenever a new message is added
		messagesScrollPane.vvalueProperty().bind(messagesVBox.heightProperty());
		
		// Set a prompt text for the input text field when its empty
		inputTextField.setPromptText("Write something...");
		
		// Sends the user input when the ENTER key has been pressed
		inputTextField.addEventHandler(KeyEvent.KEY_PRESSED, e ->
		{
			boolean isBlank = inputTextField.getText().trim().isEmpty();
			if(e.getCode() == KeyCode.ENTER && !isBlank)
			{
				sendButton.fire();
			}
		});
		
		// Sets the send button to be enabled/disabled on whether or not the text field has text or not
		sendButton.disableProperty().bind(Bindings.createBooleanBinding(() -> inputTextField.getText().trim().isEmpty(), inputTextField.textProperty()));
		
		// When the send button is clicked, a callback is fired and the input text field is cleared
		sendButton.setOnAction(e ->
		{
			if(onSendCallback != null)
			{
				onSendCallback.onSend(inputTextField.getText());
			}
			inputTextField.setText("");
		});
		
		// Setup styling and laying of the user input hbox
		userInputHBox.setId("input_background");
		userInputHBox.setPadding(new Insets(USER_INPUT_PADDING));
		
		// Allow the input text field to fill all available horizontal space
		HBox.setHgrow(inputTextField, Priority.ALWAYS);
		
		// Setup the layout of the chat box
		// 		center - list of messages
		//		bottom - input field and send button
		setCenter(messagesScrollPane);
		setBottom(userInputHBox);
	}
	
	
	public VBox getScrollPaneContent()
	{
		return scrollPaneContent;
	}

	public void showLeftIsTyping(boolean show, String text)
	{
		
	}

	/**
	 * Adds a message to the end of the chat box.
	 * 
	 * @param chatMessage message to add
	 */
	public void addMessage(ChatMessage chatMessage)
	{
		// Validation check
		Objects.requireNonNull(chatMessage, "Parameter 'chatMessage' cannot be null.");
		
		// Add message to the end of the vbox (bottom)
		messagesVBox.getChildren().add(chatMessage);
	}
	
	/**
	 * Return the property to represent the send button's action, which is invoked whenever the send button is fired.
	 * @return the callback attached to the send button
	 */
	public OnSendCallback getOnSend()
	{
		return onSendCallback;
	}

	/**
	 * Sets the send button's action to the specified callback, which is invoked whenever the send button is fired.
	 * 
	 * <p>
	 * Note, <code>onSendCallback</code> can be <code>null</code> to remove any actions.
	 * </p>
	 * 
	 * @param onSendCallback the callback to attach to the send button
	 */
	public void setOnSend(OnSendCallback onSendCallback)
	{
		this.onSendCallback = onSendCallback;
	}

	public void clearMessages()
	{
		messagesVBox.getChildren().clear();
	}
	
	/**
	 * Returns the number of messages in the chat box.
	 * @return number of messages
	 */
	public int getMessagesSize()
	{
		return messagesVBox.getChildren().size();
	}
}