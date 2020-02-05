package chatBot.bots;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The <code>RandomBot</code> class is an implementation of the {@link ChatBot} interface.
 * The <code>RandomBot</code> will randomize the word order of whatever the user says.
 * 
 * @see {@link ChatBot}
 * @author Mohammad Alali (Sanavesa)
 */
public class RandomBot implements ChatBot
{
	/** An auto-generated serial UID. */
	private static final long serialVersionUID = 900937006793263841L;
	
	@Override
	public String getName()
	{
		return "Random";
	}
	
	@Override
	public String getImagePath()
	{
		return "res/RandomIcon.png";
	}
	
	/**
	 * Responds to a message that the user sent by randomize the order of the words.
	 * 
	 * @param input the message that the user sent
	 * @return the bot's response to the user
	 */
	@Override
	public synchronized String getReply(String input)
	{
		// Validity checks
		Objects.requireNonNull(input, "Parameter 'input' cannot be null.");
		
		// Split by space
		List<String> words = Arrays.asList(input.trim().split("\\s+"));
		
		// Shuffle randomly
		Collections.shuffle(words);

		// Construct output string
		String output = "";
		for(int i = 0; i < words.size(); i++)
		{
			// Add the word
			output += words.get(i);
			
			// Add space after the word, except for the last word.
			if(i != words.size() - 1)
			{
				output += " ";
			}
		}
		
		return output;
	}
}