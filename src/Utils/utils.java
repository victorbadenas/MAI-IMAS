package Utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

public static List<String> readFile(String fileName) {
    BufferedReader br = new BufferedReader(new FileReader("file.txt"));
    List<String> inputs = new List<String>();

    String line = br.readLine();
    line = br.readLine();
    while (line != null) {
      inputs.append(line);
      line = br.readLine();
    }
    br.close();
    return inputs;
  }