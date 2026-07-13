package com.weather.mapreduce.job;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
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
 * Job4: 告警统计
 * 统计各城市告警次数（温度 > 35°C 或 < -5°C）
 */
public class AlertStatMR {

    public static class AlertMapper extends Mapper<Object, Text, Text, IntWritable> {
        private static final IntWritable ONE = new IntWritable(1);

        @Override
        protected void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {
            try {
                String json = value.toString();
                String city = extractCity(json);
                String temp = extractTemp(json);
                if (city != null && temp != null) {
                    double t = Double.parseDouble(temp);
                    if (t > 35 || t < -5) {
                        context.write(new Text(city + "_高温" + (t > 35 ? "预警" : "预警")), ONE);
                    }
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

    public static class AlertReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            context.write(key, new IntWritable(sum));
        }
    }

    public static void run(String inputPath, String outputPath) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Alert Statistics");
        job.setJarByClass(AlertStatMR.class);

        job.setMapperClass(AlertMapper.class);
        job.setReducerClass(AlertReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.addInputPath(job, new Path(inputPath));
        FileOutputFormat.setOutputPath(job, new Path(outputPath));

        boolean success = job.waitForCompletion(true);
        System.out.println("AlertStatMR: " + (success ? "成功" : "失败"));
    }
}
