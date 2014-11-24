package model.storage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import android.util.Log;

public class FileGraphData {

	/**
	 * Reads and returns the contents of the populated graph data text file, to
	 * be loaded into the motion graph
	 * 
	 * @return the contents of the graph data text file, else null
	 */

	public String getGraphData() {

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("graphData.txt"));
		} catch (FileNotFoundException e) {
			Log.e("graphData.txt", "File not found");

			return null;
		}
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
			String indicatorData = sb.toString();

			return indicatorData;

		} catch (IOException e) {
			Log.e("graphText.txt", "Input Output Exception");

			return null;
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				Log.e("graphText.txt", "Input Output Exception");
			}
		}
	}
}