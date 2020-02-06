package chatBot.bots;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * The <code>BabyYodaBot</code> class is an implementation of the {@link ChatBot} interface.
 * The <code>BabyYodaBot</code> will reply back the yodafied version of what the user said.
 * This class relies on an online service, the <a href="https://github.com/richchurcher/yoda-api">Yoda API</a>.
 * 
 * <p>
 * Credits to github user, <b>richchurcher</b>, for providing the Yoda API.
 * </p>
 * 
 * @see {@link ChatBot}
 * @author Mohammad Alali (Sanavesa)
 */
public class BabyYodaBot implements ChatBot
{
	/** An auto-generated serial UID. */
	private static final long serialVersionUID = -3065724357709971088L;
	
	/** An HTTP client used to ping the Yodafy API. */
	private final HttpClient client;
	
	/** Initialize the baby yoda bot. */
	public BabyYodaBot()
	{
		client = HttpClient.newBuilder()
				.version(Version.HTTP_2)
				.build();
	}
	
	@Override
	public String getName()
	{
		return "Baby Yoda";
	}
	
	@Override
	public String getImagePath()
	{
		return "res/BabyYodaIcon.png";
	}
	
	/**
	 * Responds to a message that the user sent by using a Yodafy API through HTTP-GET requests.
	 * 
	 * @param input the message that the user sent
	 * @return the bot's response to the user
	 */
	@Override
	public String getReply(String input)
	{
		// Validity checks
		Objects.requireNonNull(input, "Parameter 'input' cannot be null.");
		
		// Try to ping the Yoda API using HTTP
		try
		{
			// Create a HTTP request
			HttpRequest request = HttpRequest.newBuilder()
					.GET()
					.uri(URI.create("http://yoda-api.appspot.com/api/v1/yodish?text=" + URLEncoder.encode(input, "UTF-8")))
					.build();
			
			// Try to ping the server for a response
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			
			// Parse the response which is in JSON format
			JSONObject obj = (JSONObject) new JSONParser().parse(response.body());
			
			// Reply with the yodafied message
			return (String) obj.get("yodish");
		}
		// If any error occurs, reply that yoda is sleeping lol
		catch(Exception e)
		{
			return "Baby yoda is sleeping. ZzzZzzZzz...";
		}
	}
}