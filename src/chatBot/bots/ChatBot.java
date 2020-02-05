package chatBot.bots;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * The <code>ChatBot</code> interface defines the necessary functionality that all chat bots require to implement.
 * 
 * <p>
 * Mainly, it has 3 methods:
 * <ul>
 * <li>{@link #getName()} - returns the name of the chat bot - to add a touch of personality</li>
 * <li>{@link #getName()} - returns the file path of the chat bot's image, also to add a touch of personality</li>
 * <li>{@link #getReply(String)} - responds to a message that the user sent</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Also, there are 2 utility methods to ease development:
 * <ul>
 * <li>{@link #save(ChatBot, String)} - saves the chat bot to the specified <code>path</code></li>
 * <li>{@link #load(String)} - loads a chat bot from the specified <code>path</code></li>
 * </ul>
 * </p>
 * 
 * @author Mohammad Alali (Sanavesa)
 */
public interface ChatBot extends Serializable
{
	/**
	 * Returns the name of the chat bot, to add a touch of personality.
	 * @return chat bot's name
	 */
	String getName();
	
	/**
	 * Returns the file path of the chat bot's image, also to add a touch of personality.
	 * @return chat bot's image
	 */
	String getImagePath();
	
	/**
	 * Responds to a message that the user sent and replies accordingly.
	 * <b>This is where the logic of the AI/ChatBot lies.</b>
	 * @param input the message that the user sent
	 * @return the chat bot's response to the user
	 */
	String getReply(String input);
	
	/**
	 * Saves the chat bot to the specified <code>path</code>.
	 * @param path the file path to save to
	 * @return the operation's status; true if successful, false otherwise
	 */
	static boolean save(ChatBot chatBot, String path)
	{
		try(FileOutputStream fos = new FileOutputStream(new File(path));
			ObjectOutputStream oos = new ObjectOutputStream(fos))
		{
			oos.writeObject(chatBot);
			System.out.println("Successfuly saved " + chatBot.getName() + ".");
			return true;
		}
		catch (Exception e)
		{
			System.err.println("Failed to save " + chatBot.getName() + ". Reason: " + e.getMessage());
			return false;
		}
	}
	
	/**
	 * Loads a chat bot from the specified <code>path</code>.
	 * @param path the file path to load from
	 * @return the loaded chat bot, or <code>null</code> if failed to load
	 */
	static ChatBot load(String path)
	{
		try(FileInputStream fis = new FileInputStream(new File(path));
			ObjectInputStream ois = new ObjectInputStream(fis))
		{
			ChatBot chatBot = (ChatBot) ois.readObject();
			System.out.println("Successfully loaded " + chatBot.getName() + ".");
			return chatBot;
		}
		catch (Exception e)
		{
			System.err.println("Failed to load chat bot. Reason: " + e.getMessage());
			return null;
		}
	}
}