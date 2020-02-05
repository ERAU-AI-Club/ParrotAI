package chatBot.bots;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * The <code>ParrotBot</code> class is an implementation of the {@link ChatBot} interface.
 * The following simplistic model of a parrot should allow for complex behavior to emerge once in a while.
 * 
 * <p>
 * This class contains the necessary functionality to model a parrot chat bot, which includes:
 * <ul>
 * <li>Random squawking</li>
 * <li>Occasionally echoes back user's input</li>
 * <li>Ocassionally repeats previous user's input</li>
 * <li>Unpredictably become furious and capitalize reply</li>
 * </ul>
 * </p>
 * 
 * @see {@link ChatBot}
 * @author Mohammad Alali (Sanavesa)
 */
public class ParrotBot implements ChatBot
{
	/** A conversations set, that contains all previously seen user input with <i>no duplicates</i>. */
	private Set<String> conversations = new LinkedHashSet<>();
	
	/** An auto-generated serial UID. */
	private static final long serialVersionUID = -586595470909187255L;
	
	/** The probability that the parrot will echo back the user's input. Range is [0, 1]. */
	private static final double ECHO_PROBABILITY = 0.35;
	
	/** The probability that the parrot will squawk. Range is [0, 1]. */
	private static final double SQUAWK_PROBABILITY = 0.1;
	
	/** The probability that the parrot will reply angrily. Range is [0, 1]. */
	private static final double ANGER_PROBABILITY = 0.25;
	
	@Override
	public String getName()
	{
		return "Parrot";
	}
	
	@Override
	public String getImagePath()
	{
		return "res/ParrotIcon.png";
	}
	
	/**
	 * Responds to a message that the user sent and replies accordingly.
	 * 
	 * <p>
	 * The parrot chat bot's logic contains the following:
	 * <ul>
	 * <li>Random squawking</li>
	 * <li>Occasionally echoes back user's input</li>
	 * <li>Ocassionally repeats previous user's input</li>
	 * <li>Unpredictably become furious and capitalize reply</li>
	 * </ul>
	 * </p>
	 * 
	 * @param input the message that the user sent
	 * @return the parrot's response to the user
	 */
	@Override
	public String getReply(String input)
	{
		// Validity checks
		Objects.requireNonNull(input, "Parameter 'input' cannot be null.");
		
		// Store the given input to the conversations set, which ensures unique elements
		conversations.add(input);
		
		// For a specified probability, just squawk back
		if(Math.random() <= SQUAWK_PROBABILITY)
		{
			return "*SQUAWK*";
		}
		
		// For a specified probability, just echo back what the user said
		if(Math.random() <= ECHO_PROBABILITY)
		{
			// For a specified probability, reply angrily, otherwise reply verbatim
			if(Math.random() <= ANGER_PROBABILITY)
			{
				return addAnger(input);
			}
			else
			{
				return input;
			}
		}
		// Else, reply with a previously seen user's input
		else
		{
			// Retrieve randomly a user input from the conversations set
			int randomIndex = (int) (Math.random() * conversations.size());
			String reply = getFromPreviousConversations(randomIndex);
			
			// For a specified probability, reply angrily, otherwise reply normally
			if(Math.random() <= ANGER_PROBABILITY)
			{
				return addAnger(reply);
			}
			else
			{
				return reply;
			}
		}
	}
	
	/**
	 * Makes the given <code>text</code> angry, by capitalizing it and adding !!! at the end.
	 * @param text the text to make angry
	 * @return angry version of <code>text</code>
	 */
	private String addAnger(String text)
	{
		// Validity checks
		Objects.requireNonNull(text, "Parameter 'text' cannot be null.");
		
		return text.toUpperCase() + "!!!";
	}
	
	/**
     * Returns the element at the specified position in the conversation set.
     *
     * @param  index index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException if the index is out of range <code>(index < 0 || index >= conversations.size())</code>
     */
	private String getFromPreviousConversations(int index)
	{
		// Validity checks
		Objects.checkIndex(index, conversations.size());
		
		// Sequentially iterate through the conversations set to the specified index and return that element
		int i = 0;
		String reply = null;
		for(String convo : conversations)
		{
			if(i == index)
			{
				reply = convo;
				break;
			}
			i++;
		}
		
		return reply;
	}
}