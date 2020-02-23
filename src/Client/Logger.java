package Client;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class Logger {
	public static void writeLog(String log, String file) throws IOException {
		FileWriter write = new FileWriter(file, true);
		Date date = new Date();
		write.append(date.toString() + " " + log);
		write.append("\r\n");
	    write.close();
	}
}
