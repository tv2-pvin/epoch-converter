package com.example;

import io.micronaut.configuration.picocli.PicocliRunner;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "epoch-converter", description = "...",
    mixinStandardHelpOptions = true)
public class EpochConverterCommand implements Runnable {

  @Option(names = {"-i",
      "--in-file"}, required = true, description = "The path to the file to read")
  String inFile;
  @Option(names = {"-o", "--out-file"}, description = "The path to the file to write")
  String outFile;

  @Option(names = {"-k",
      "--keys"}, required = true, description = "The key to convert", split = ",")
  List<String> keys;

  @Option(names = {"-z", "--zone"}, description = "The time zone", defaultValue = "UTC")
  String zone;

  public static void main(String[] args) throws Exception {
    PicocliRunner.run(EpochConverterCommand.class, args);
  }

  public void run() {
    if (inFile == null) {
      System.out.println("Please specify an input file");
      return;
    }

    if (!inFile.endsWith(".json")) {
      System.out.println("Unsupported file type - only JSON files are supported");
      return;
    }

    if (outFile == null) {
      System.out.println(
          "Output path has not been specified, using input path with postfix \"-converted\"");
      outFile = inFile.replaceAll("\\.json", "") + "-converted.json";
    }

    var inFile = Paths.get(this.inFile);
    try {
      var json = Files.readAllLines(inFile).stream()
          .map(String::trim)
          .reduce(String::concat)
          .map(JSONObject::new)
          .map(this::convertEpochs)
          .orElseThrow();

      Files.writeString(Paths.get(outFile), json.toString(2));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private JSONObject convertEpochs(JSONObject jsonObject) {
    jsonObject.keySet().forEach(key -> {
      var value = jsonObject.get(key);

      if (value instanceof JSONArray jsonArray) {
        jsonArray.forEach(item -> convertEpochs((JSONObject) item));
      }

      if (value instanceof JSONObject jo) {
        jsonObject.put(key, convertEpochs(jo));
      } else if (keys.contains(key) && value instanceof Long l) {
        jsonObject.put(key, convertEpoch(l));
      }

    });
    return jsonObject;
  }

  private ZonedDateTime convertEpoch(Long value) {
    if (value == null || value < 0) {
      return null;
    }

    return ZonedDateTime.ofInstant(
        java.time.Instant.ofEpochMilli(value),
        ZoneId.of(zone)
    );
  }
}
