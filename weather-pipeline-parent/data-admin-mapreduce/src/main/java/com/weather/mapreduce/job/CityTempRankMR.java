package com.weather.mapreduce.job;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;
import java.util.*;

/**
 * Job3: 城市温度排名（Top 10 最热城市）
 */
public class CityTempRankMR {

    public static class TempMapper extends Mapper<Object, Text, Text, DoubleWritable> {
        @Override
        protected void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {
            try {
                String json = value.toString();
                String city = extractCity(json);
                String temp = extractTemp(json);
                if (city != null && temp != null) {
                    context.write(new Text(city), new DoubleWritable(Double.parseDouble(temp)));
                }
            } catch (Exception e) {}
        }

        private String extractCity(String json) {
            int idx = json.indexOf("\"city\":\"");
            if (idx < 0) return null;
            int start = idx + 8;
            int end = json.indexOf("\"", start);
            return json.substring(start, end);
        }

        private String extractTemp(String json) {
            int idx = json.indexOf("\"temp\":");
            if (idx < 0) return null;
            int start = idx + 7;
            int end = json.indexOf(",", start);
            if (end < 0) end = json.indexOf("}", start);
            return json.substring(start, end);
        }
    }

    public static class TempReducer extends Reducer<Text, DoubleWritable, DoubleWritable, Text> {
        private TreeMap<Double, String> topMap = new TreeMap<>(Collections.reverseOrder());

        @Override
        protected void reduce(Text key, Iterable<DoubleWritable> values, Context context)
                throws IOException, InterruptedException {
            double maxTemp = -100;
            for (DoubleWritable val : values) {
                maxTemp = Math.max(maxTemp, val.get());
            }
            topMap.put(maxTemp, key.toString());
            if (topMap.size() > 10) {
                topMap.remove(topMap.lastKey());
            }
        }

        @Override
        protected void cleanup(Context context)
                throws IOException, InterruptedException {
            for (Map.Entry<Double, String> entry : topMap.entrySet()) {
                context.write(new DoubleWritable(entry.getKey()), new Text(entry.getValue()));
            }
        }
    }

    public static void run(String inputPath, String outputPath) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "City Temperature Ranking");
        job.setJarByClass(CityTempRankMR.class);

        job.setMapperClass(TempMapper.class);
        job.setReducerClass(TempReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(DoubleWritable.class);
        job.setOutputKeyClass(DoubleWritable.class);
        job.setOutputValueClass(Text.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        job.setNumReduceTasks(1);

        FileInputFormat.addInputPath(job, new Path(inputPath));
        FileOutputFormat.setOutputPath(job, new Path(outputPath));

        boolean success = job.waitForCompletion(true);
        System.out.println("CityTempRankMR: " + (success ? "成功" : "失败"));
    }
}
