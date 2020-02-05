package chatBot.bots;
import java.util.Objects;

/**
 * The <code>EchoBot</code> class is an implementation of the {@link ChatBot} interface.
 * The <code>EchoBot</code> will simply echo back whatever the user says.
 * 
 * @see {@link ChatBot}
 * @author Mohammad Alali (Sanavesa)
 */
public class EchoBot implements ChatBot
{
	/** An auto-generated serial UID. */
	private static final long serialVersionUID = -772837032199041772L;
	
	@Override
	public String getName()
	{
		return "Echo";
	}
	
	@Override
	public String getImagePath()
	{
		return "res/EchoIcon.png";
	}
	
	/**
	 * Responds to a message that the user sent by sending it back verbatim.
	 * 
	 * @param input the message that the user sent
	 * @return the bot's response to the user
	 */
	@Override
	public String getReply(String input)
	{
		// Validity checks
		Objects.requireNonNull(input, "Parameter 'input' cannot be null.");
		
		// Reply verbatim
		return input;
	}
}