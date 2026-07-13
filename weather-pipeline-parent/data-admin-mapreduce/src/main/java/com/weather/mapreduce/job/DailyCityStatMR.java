package com.weather.mapreduce.job;

import com.weather.mapreduce.entity.WeatherRecord;
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

/**
 * Job1: 每日各城市天气统计
 * 输入: weather_raw 数据 (JSON 格式)
 * 输出: 每个城市每天的 平均/最高/最低 温度
 */
public class DailyCityStatMR {

    public static class StatMapper extends Mapper<Object, Text, Text, Text> {
        @Override
        protected void map(Object key, Text value, Context context) 
                throws IOException, InterruptedException {
            try {
                String line = value.toString();
                // 解析 JSON (简单解析)
                String city = extractCity(line);
                String temp = extractTemp(line);
                String humidity = extractHumidity(line);
                String date = extractDate(line);

                if (city != null && temp != null) {
                    String outputKey = city + "_" + date;
                    String outputValue = temp + "," + humidity;
                    context.write(new Text(outputKey), new Text(outputValue));
                }
            } catch (Exception e) {
                // 跳过解析失败的记录
            }
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

        private String extractHumidity(String json) {
            int idx = json.indexOf("\"humidity\":");
            if (idx < 0) return "0";
            int start = idx + 11;
            int end = json.indexOf(",", start);
            if (end < 0) end = json.indexOf("}", start);
            return json.substring(start, end);
        }

        private String extractDate(String json) {
            int idx = json.indexOf("\"updateTime\":\"");
            if (idx < 0) return "1970-01-01";
            int start = idx + 13;
            int end = json.indexOf("\"", start);
            String ts = json.substring(start, end);
            try {
                long timestamp = Long.parseLong(ts);
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                return sdf.format(new java.util.Date(timestamp * 1000));
            } catch (Exception e) {
                return "1970-01-01";
            }
        }
    }

    public static class StatReducer extends Reducer<Text, Text, Text, Text> {
        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            double sumTemp = 0;
            double sumHumidity = 0;
            double maxTemp = -100;
            double minTemp = 100;
            int count = 0;

            for (Text val : values) {
                String[] parts = val.toString().split(",");
                try {
                    double temp = Double.parseDouble(parts[0]);
                    double humidity = Double.parseDouble(parts[1]);
                    sumTemp += temp;
                    sumHumidity += humidity;
                    maxTemp = Math.max(maxTemp, temp);
                    minTemp = Math.min(minTemp, temp);
                    count++;
                } catch (Exception e) {}
            }

            if (count > 0) {
                double avgTemp = sumTemp / count;
                double avgHumidity = sumHumidity / count;
                String result = String.format("%.1f\t%.1f\t%.1f\t%.1f\t%d",
                        avgTemp, maxTemp, minTemp, avgHumidity, count);
                context.write(key, new Text(result));
            }
        }
    }

    public static void run(String inputPath, String outputPath) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Daily City Weather Stat");
        job.setJarByClass(DailyCityStatMR.class);

        job.setMapperClass(StatMapper.class);
        job.setReducerClass(StatReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.addInputPath(job, new Path(inputPath));
        FileOutputFormat.setOutputPath(job, new Path(outputPath));

        boolean success = job.waitForCompletion(true);
        System.out.println("DailyCityStatMR: " + (success ? "成功" : "失败"));
    }
}
