package space.gatt.JavacordCommander;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.Server;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.permissions.Role;
import de.btobastian.javacord.listener.message.MessageCreateListener;
import javassist.compiler.Javac;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Zach G on 06-Apr-16.
 */
public class CommandListener implements MessageCreateListener {

	private HashMap<User, Long> commandCooldown = new HashMap<>();

	private boolean hasRole(User user, Server server, String roleName, boolean caseSensitive){
		if (caseSensitive) {
			for (Role r : user.getRoles(server)) {
				if (r.getName().equalsIgnoreCase(roleName)) {
					return true;
				}
			}
			return false;
		}else{
			return hasRole(user, server, roleName);
		}
	}

	private boolean hasRole(User user, Server server, String roleName){
		for (Role r : user.getRoles(server)){
			if (r.getName().equals(roleName)){
				return true;
			}
		}
		return false;
	}

	@Override
	public void onMessageCreate(DiscordAPI api, Message message) {
		if (message.getContent().startsWith(Settings.getCommandStarter())) {

			String[] args = message.getContent().split(" ");
			args[0] = args[0].replaceFirst(Settings.getCommandStarter(), "");
			if (JavacordCommander.getInstance().getCommandList().contains(args[0])) {
				String msg = Settings.getMsgStarter() + "Error. No response given by command.";
				Class<?> enclosingClass = JavacordCommander.getInstance().getCommandRegistrar().get(args[0]);
				String cmd = args[0];
				args = Arrays.copyOfRange(args, 1, args.length);
				if (enclosingClass != null) {
					boolean adminOnly = false;
					boolean deleteMsg = false;
					boolean sendPM = false;
					boolean requiresPM = false;
					String[] ranks = new String[]{};

					for (Annotation a : enclosingClass.getAnnotations()){
						if (a instanceof Permissions){
							ranks = ((Permissions)a).ranks();
							adminOnly = ((Permissions)a).adminOnly();
						}
						if (a instanceof CommandSettings){
							deleteMsg = ((CommandSettings)a).deleteInitatingMsg();
							sendPM = ((CommandSettings)a).sendResponseViaPM();
							requiresPM = ((CommandSettings)a).requiresPM();
						}
					}

					if (requiresPM){
						if (!message.isPrivateMessage()){
							return;
						}
					}

					if (deleteMsg){
						message.delete();
					}

					if (adminOnly){
						if (!(JavacordCommander.getInstance().getAdminUsers().contains(message.getAuthor().getId()))){
							String reply = Settings.getMsgStarter() + "You are not one on my Admin List! Sorry!";
							if (sendPM){
								message.getAuthor().sendMessage(reply);
							}else{
								message.reply(reply);
							}
							return;
						}
					}
					if (ranks.length > 0 && !ranks[0].equals("null")){
						boolean hasRank = false;
						for (String rank : ranks){
							if (hasRole(message.getAuthor(), message.getChannelReceiver().getServer(), rank, false)){
								hasRank = true;
							}
						}
						if (!hasRank){
							String reply = Settings.getMsgStarter() + "You do not have one of the following ranks:";
							for (String r : ranks){
								reply = reply + " `" + r + "`";
							}
							reply = reply + "`";
							if (sendPM){
								message.getAuthor().sendMessage(reply);
							}else{
								message.reply(reply);
							}
							return;
						}
					}

					Method method;

					Class<?> clz = JavacordCommander.getInstance().getCommandRegistrar().get(cmd);
					String methodName = JavacordCommander.getInstance().getMethodRegistrar().get(cmd).getName();
					try {
						method = clz.getDeclaredMethod(methodName, DiscordAPI.class, Message.class, User.class, String[].class);
						Object value = method.invoke(this, api, message, message.getAuthor(), args);
						msg = (String) value;
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (sendPM){
						message.getAuthor().sendMessage(msg);
					}else{
						message.reply(msg);
					}
				}
			}
		}
	}
}
