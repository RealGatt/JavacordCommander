package space.gatt.JavacordCommander;

import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.message.Message;

import java.util.*;

/**
 * Created by Zach G on 24-Apr-16.
 */
public class MessageManager {

	private static HashMap<Channel, List<Message>> cleanerHash = new HashMap<>();

	private static HashMap<Channel, List<String>> messagesToSend = new HashMap<>();

	/**
	 *
	 * @param c Channel
	 * @param m Message to Delete
	 */
	public static void deleteMessage(Channel c, Message m){
		List<Message> msgList = new ArrayList<>();
		if (cleanerHash.containsKey(c)){
			msgList = cleanerHash.get(c);
		}
		msgList.add(m);
		cleanerHash.put(c, msgList);
	}

	/**
	 *
	 * @param c Channel to check
	 * @return List of messages
	 */
	public static List<Message> getMessagesToDelete(Channel c){
		if (cleanerHash.containsKey(c)) return cleanerHash.get(c);
		return null;
	}

	/**
	 *
	 * @param c Channel
	 * @param m Message to send
	 */
	public static void sendMessage(Channel c, String m){
		List<String> msgList = new ArrayList<>();
		if (messagesToSend.containsKey(c)){
			msgList = messagesToSend.get(c);
		}
		msgList.add(m);
		messagesToSend.put(c, msgList);
	}

	/**
	 *
	 * @param c Channel
	 * @return Messages still to send
	 */
	public static List<String> getMessagesToSend(Channel c){
		if (messagesToSend.containsKey(c)) return messagesToSend.get(c);
		return null;
	}

	/**
	 * @param interval The interval in milliseconds to update
	 */
	public synchronized static void startManager(int interval){
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				for (Channel c : cleanerHash.keySet()){
					if (cleanerHash.get(c).size() > 0){
						Message m = cleanerHash.get(c).get(0);
						m.delete();
						cleanerHash.get(c).remove(m);
					}
				}
				for (Channel c : messagesToSend.keySet()){
					if (messagesToSend.get(c).size() > 0){
						String m = messagesToSend.get(c).get(0);
						c.sendMessage(m);
						messagesToSend.get(c).remove(0);
					}
				}
			}
		};

		Timer timer = new Timer();
		long delay = 0;

		// schedules the task to be run in an interval
		timer.scheduleAtFixedRate(task, delay, interval);
	}

}
