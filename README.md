# JavacordCommander
A wrapper that allows for easy creation of bot commands via the use of Javacord

# Installing JavacordCommander

## Maven

Add the following to your pom.xml file;

```xml
<repositories>
	...
	<repository>
	    <id>javacordcommander-repo</id>
	    <url>http://dev.gatt.space/maven/</url>
	</repository>
	...
</repositories>
<dependencies>
	...
	<dependency>
            <groupId>space.gatt.javacordcommander</groupId>
            <artifactId>javacordcommander</artifactId>
            <version>1.0.1</version>
        </dependency>
	...
</dependencies>
```

**The current version of Javacord Commander is:** ```1.0.1```

If you don't use Maven, you can download the latest .JAR file from the Maven Repository here: http://dev.gatt.space/maven/space/gatt/javacordcommander/javacordcommander/

# Registering commands

```java
DiscordAPI api = new DiscordAPI(...);
JavacordCommander jcc = new JavacordCommander(api);
jcc.enableSnooper("your.package.here");
```
or
```java
DiscordAPI api = new DiscordAPI(...);
JavacordCommander jcc = new JavacordCommander(api, "your.package.here");
```
By using the second method, the ```enableSnooper()``` method is automatically called.

# Creating Commands

The following is an example command from my own bot "Gasai Bot"

```java
@Command("botinfo")
@Syntax("botinfo")
@Usage("botinfo")
@Permissions()
@Description("Returns info about the Bot")
@Group("Admin")
@CommandSettings(deleteInitatingMsg = true, sendResponseViaPM = true)
public class BotInfo {
	@IMethod
	public static String command(DiscordAPI discordAPI, Message message, User user, String[] args) {
		MessageBuilder builder = new MessageBuilder();

		builder.append(Settings.getMsgStarter() + " My current name: `" + discordAPI.getYourself().getName() + "`").appendNewLine();
		builder.append(Settings.getMsgStarter() + " My current game: `" + discordAPI.getGame() + "`").appendNewLine();
		builder.append(Settings.getMsgStarter() + " My current profile picture: `" + discordAPI.getYourself().getAvatarUrl()+"`").appendNewLine();
		builder.append(Settings.getMsgStarter() + " Admin Rank Name: `Bot Commander`").appendNewLine();
		builder.append(Settings.getMsgStarter() + " Servers joined: `" + discordAPI.getServers().size()+"`").appendNewLine();

		builder.append( Settings.getMsgStarter() + " Users in Cache: `" + Main.userCache.keySet().size() + "`").appendNewLine();
		message.getAuthor().sendMessage(builder.build());
		builder = new MessageBuilder();
		builder.append(Settings.getMsgStarter() + "I've PM'd you my information, ").appendUser(message.getAuthor());
		return builder.build();
	}
}
```

Commands have to have it's own class, but there can technically be multiple methods (all though I haven't tested this). The Method can be called anything you want.

The @Command annotation defines the actual command. For example, I'd type ```/!log``` to run this comand.
Syntax, Usage and Description are (for now) just for the Help Message Builder.
CommandSettings has two options. deleteInitatingMsg and sendResponseViaPM. They're pretty self-explanatory.
The @Group tag defines what set of commands it will be a part of. This is mainly for the help message builder. If it's set to "hidden", it will not be added to the Help Message.
@Permissions has two values. Value is a String array, which defines the possible ranks a user must have to run the command. Currently, they only need to have one of the possible ranks. There is also a boolean called "adminOnly", which requires that the user is added to the "adminUsers" list within the JavacordCommander class.


**This current version of the README is a quick-writeup for people to get the idea of JavacordCommander, I'll write up a proper version later.

*Last Updated: 19/4/16 10:00PM AEST*
