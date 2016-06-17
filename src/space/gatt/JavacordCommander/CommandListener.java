package space.gatt.JavacordCommander;

import com.google.common.base.Splitter;
import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.Server;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.permissions.Role;
import de.btobastian.javacord.listener.message.MessageCreateListener;
import space.gatt.JavacordCommander.annotations.CommandSettings;
import space.gatt.JavacordCommander.annotations.Permissions;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * You don't need to access this :)
 */
public class CommandListener implements MessageCreateListener {

	private HashMap<User, Long> commandCooldown = new HashMap<>();
	/**
	 * @param api API
	 * @param message Message
	 */
	@Override
	public void onMessageCreate(DiscordAPI api, Message message) {
		String msgEdit = message.getContent();
		boolean commandCheck = false;
		if (message.getContent().startsWith(Settings.getCommandStarter())) {
			msgEdit = msgEdit.replaceFirst(Settings.getCommandStarter(), "");
			commandCheck = true;
		}
		if (message.getContent().startsWith(Settings.getAltCommandStarter())) {
			msgEdit = msgEdit.replaceFirst(Settings.getAltCommandStarter(), "");
			commandCheck = true;
		}
		String[] args = msgEdit.split(" ");
		String cmd = args[0].toLowerCase();
		if (JavacordCommander.getInstance().getCommandList().contains(cmd) && commandCheck) {
			String msg = Settings.getMsgStarter() + "Error. No response given by command.";
			Class<?> enclosingClass = JavacordCommander.getInstance().getCommandRegistrar().get(cmd);
			args = Arrays.copyOfRange(args, 1, args.length);
			if (enclosingClass != null) {
				boolean adminOnly = false;
				boolean deleteMsg = false;
				boolean sendPM = message.isPrivateMessage();
				boolean requiresPM = false;
				String[] ranks = new String[]{};

				for (Annotation a : enclosingClass.getAnnotations()){
					if (a instanceof Permissions){
						ranks = ((Permissions)a).value();
						adminOnly = ((Permissions)a).adminOnly();
					}
					if (a instanceof CommandSettings){
						deleteMsg = ((CommandSettings)a).deleteInitatingMsg();
						sendPM = ((CommandSettings)a).sendResponseViaPM();
						requiresPM = ((CommandSettings)a).requiresPM();
					}
				}
				
				if (message.isPrivateMessage()){
					sendPM = true;
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

					if (JavacordCommander.getInstance().allowAdminBypass()){
						hasRank = JavacordCommander.getInstance().getAdminUsers().contains(message.getAuthor().getId());
					}

					for (String rank : ranks){
						if (JavacordCommander.getInstance().hasRole(message.getAuthor(), message.getChannelReceiver().getServer(), rank, false)){
							hasRank = true;
						}
					}
					if (!hasRank){
						String reply = Settings.getMsgStarter() + "You do not have one of the following ranks:";
						for (String r : ranks){
							reply = reply + " `" + r + "`";
						}
						if (sendPM){
							message.getAuthor().sendMessage(reply);
						}else{
							MessageManager.sendMessage(message.getChannelReceiver(), reply);
						}
						return;
					}
				}

				Method method;

				Class<?> clz = JavacordCommander.getInstance().getCommandRegistrar().get(cmd);
				String methodName = JavacordCommander.getInstance().getMethodRegistrar().get(cmd).getName();

				String[] messages = null;
				File file = null;


				try {
					method = clz.getDeclaredMethod(methodName, DiscordAPI.class, Message.class, User.class, String[].class);
					Object value = method.invoke(this, api, message, message.getAuthor(), args);
					if (value instanceof String) {
						msg = (String) value;
					}else if (value instanceof String[]){
						messages = (String[])value;
					}else if (value instanceof List){
						messages = ((List<String>) value).toArray(new String[((List<String>) value).size()]);
					}else if (value instanceof File){
						file = (File)value;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (file != null){
					if (sendPM){
						message.getAuthor().sendFile(file);
					}else{
						message.replyFile(file);
					}
					return;
				}
				if (messages != null){

					if (sendPM){
						for (String s : messages){
							MessageManager.sendMessage(message.getAuthor(), s);
						}
					}else{
						for (String s : messages){
							MessageManager.sendMessage(message.getChannelReceiver(), s);
						}
					}
					return;
				}

				if (msg.length() > 1999){
					if (sendPM){
						for (String sMsg: Splitter.fixedLength(1999).split(msg)) {
							MessageManager.sendMessage(message.getAuthor(), sMsg);
						}
					}else{
						for (String sMsg: Splitter.fixedLength(1999).split(msg)) {
							MessageManager.sendMessage(message.getChannelReceiver(), sMsg);
						}
					}
					return;
				}

				if (sendPM){
					message.getAuthor().sendMessage(msg);
				}else{
					MessageManager.sendMessage(message.getChannelReceiver(), msg);
				}
			}
		}

	}
}
