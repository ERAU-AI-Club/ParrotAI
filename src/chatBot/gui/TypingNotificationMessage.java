package chatBot.gui;
import javafx.scene.image.Image;

/**
 * The <code>TypingNotificationMessage</code> class represents a typing notification message that contains a label and an icon in a chat box.
 * The chat message can be aligned to the left or the right of a chatbox via the {@link ChatMessagePosition} parameter.
 * 
 * <p>
 * This class is very similar to {@link ChatMessage}, but only differs in its styling.
 * </p>
 * 
 * @see {@link ChatBox}
 * @see {@link ChatMessage}
 * @see {@link ChatMessagePosition}
 * @author Mohammad Alali (Sanavesa)
 */
public class TypingNotificationMessage extends ChatMessage
{
	/**
	 * Constructs a new chat message instance with the specified arguments.
	 * 
	 * @param messagePosition the position of the chat message in a chat box
	 * @param message the chat message content
	 * @param iconImage the icon in the chat message
	 */
	public TypingNotificationMessage(ChatMessagePosition messagePosition, String message, Image iconImage)
	{
		super(messagePosition, message, iconImage);
	}

	@Override
	protected void initializeGUI()
	{
		// Initialize the message same as ChatMessage superclass
		super.initializeGUI();
		
		// Add extra styling
		messageLabel.setStyle("-fx-font-style: italic");
	}
}