package chatBot.gui;
import java.util.Objects;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;

/**
 * The <code>ChatMessage</code> class represents a chat message that contains a label and an icon in a chat box.
 * The chat message can be aligned to the left or the right of a chatbox via the {@link ChatMessagePosition} parameter.
 * 
 * 
 * @see {@link ChatBox}
 * @see {@link ChatMessagePosition}
 * @author Mohammad Alali (Sanavesa)
 */
public class ChatMessage extends HBox
{
	/** The label with the chat message content. */
	protected final Label messageLabel;
	
	/** The icon in the chat message. */
	protected final ImageView iconImageView; 
	
	/** The position of the chat message in a chat box (Left / Right). */
	protected final ChatMessagePosition messagePosition;
	
	/** The size, in pixels, of the icon in the chat message. */
	public static final int ICON_SIZE = 32;
	
	/** The horizontal spacing, in pixels, between the icon and the chat message. */
	public static final int CHAT_MESSAGE_HORIZONTAL_SPACING = 10;
	
	/**
	 * Constructs a new chat message instance with the specified arguments.
	 * 
	 * @param messagePosition the position of the chat message in a chat box
	 * @param message the chat message content
	 * @param iconImage the icon in the chat message
	 */
	public ChatMessage(ChatMessagePosition messagePosition, String message, Image iconImage)
	{
		// Validation checks
		Objects.requireNonNull(messagePosition, "Parameter 'messagePosition' cannot be null.");
		Objects.requireNonNull(message, "Parameter 'message' cannot be null.");
		Objects.requireNonNull(iconImage, "Parameter 'iconImage' cannot be null.");
		
		messageLabel = new Label(message);
		iconImageView = new ImageView(iconImage);
		this.messagePosition = messagePosition;
		
		initializeGUI();
	}
	
	/**
	 * Fully initializes and sets up the GUI elements in the chat message.
	 */
	protected void initializeGUI()
	{
		// Allow message label to overrun and wrap
		messageLabel.setTextOverrun(OverrunStyle.CLIP);
		messageLabel.setWrapText(true);
		messageLabel.setMinHeight(Region.USE_PREF_SIZE);

		// Setup auto resizing for the message label
		ChangeListener<? super Number> sceneWidthChangeListener = (args, oldWidth, newWidth) ->
		{
			double width = newWidth.doubleValue() * 0.4;
			messageLabel.setMaxWidth(width);
		};
		
		// Attach the auto resizing to whenever the scene changes
		sceneProperty().addListener((args, oldScene, newScene) ->
		{
			// Remove listener from previous scene, to prevent memory leaks
			if(oldScene != null)
			{
				oldScene.widthProperty().removeListener(sceneWidthChangeListener);
			}
			
			// Attach a new listener to then new listener
			if(newScene != null)
			{
				newScene.widthProperty().addListener(sceneWidthChangeListener);
				
				// Since the listener wont fire yet, lets calculate and set the width manually
				double width = newScene.getWidth() * 0.40;
				messageLabel.setMaxWidth(width);
			}
		});
		
		// Fit the icon to what we specified
		iconImageView.setFitHeight(ICON_SIZE);
		iconImageView.setPreserveRatio(true);
		
		// Clip the icon by a circle to add that profile picture feeling
		Circle clipCircle = new Circle(ICON_SIZE / 2, ICON_SIZE / 2, ICON_SIZE / 2);
		iconImageView.setClip(clipCircle);
		
		// Align the message and icon to the specified position
		if(messagePosition == ChatMessagePosition.Left)
		{
			// If our message is rendered at the left, we want the following format: (icon) Message
			getChildren().addAll(iconImageView, messageLabel);
			setAlignment(Pos.CENTER_LEFT);
		}
		else if(messagePosition == ChatMessagePosition.Right)
		{
			// If our message is rendered at the right, we want the following format: Message (icon)
			getChildren().addAll(messageLabel, iconImageView);
			setAlignment(Pos.CENTER_RIGHT);
		}
		
		// Spacing between the message label and the icon
		setSpacing(CHAT_MESSAGE_HORIZONTAL_SPACING);
	}
	
	/**
	 * Sets the chatMessage's label to the specified message.
	 * @param message the new chat message
	 */
	public void setMessage(String message)
	{
		// Validation check
		Objects.requireNonNull(message, "Parameter 'message' cannot be null.");
		
		messageLabel.setText(message);
	}
	
	/**
	 * Sets the chatMessage's icon to the specified icon.
	 * @param image the new chat icon
	 */
	public void setIcon(Image image)
	{
		// Validation check
		Objects.requireNonNull(image, "Parameter 'image' cannot be null.");
				
		iconImageView.setImage(image);
	}
	
	/**
	 * Sets the styling id for the chat message's label. This is analogous to the "id" attribute on an HTML element
     * (<a href="http://www.w3.org/TR/CSS21/syndata.html#value-def-identifier">CSS ID Specification</a>).
	 * @param id 
	 */
	public void setLabelCSSId(String id)
	{
		// Validation check
		Objects.requireNonNull(id, "Parameter 'id' cannot be null.");
		
		messageLabel.setId(id);
	}
}