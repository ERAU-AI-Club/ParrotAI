package chatBot.gui;

/**
 * The <code>ChatMessagePosition</code> enumeration contains possible values for the position of chat messages in a chat box.
 * This is used to simulate two-way communicate by assigning sides to each party, left and right.
 * 
 * @see {@link ChatBox}
 * @see {@link ChatMessage}
 * @author Mohammad Alali (Sanavesa)
 */
public enum ChatMessagePosition
{
	/** Align the chat message to the left of the chat box. */
	Left,
	
	/** Align the chat message to the right of the chat box. */
	Right;
}