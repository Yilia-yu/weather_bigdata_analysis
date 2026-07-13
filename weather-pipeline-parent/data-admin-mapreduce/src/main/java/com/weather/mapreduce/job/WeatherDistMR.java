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
 * Job2: 天气分布统计
 * 统计每种天气状况出现的次数
 */
public class WeatherDistMR {

    public static class WeatherMapper extends Mapper<Object, Text, Text, IntWritable> {
        private static final IntWritable ONE = new IntWritable(1);

        @Override
        protected void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {
            try {
                String json = value.toString();
                String weather = extractWeather(json);
                if (weather != null) {
                    context.write(new Text(weather), ONE);
                }
            } catch (Exception e) {}
        }

        private String extractWeather(String json) {
            int idx = json.indexOf("\"weather\":\"");
            if (idx < 0) return "unknown";
            int start = idx + 11;
            int end = json.indexOf("\"", start);
            return json.substring(start, end);
        }
    }

    public static class WeatherReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
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
        Job job = Job.getInstance(conf, "Weather Distribution");
        job.setJarByClass(WeatherDistMR.class);

        job.setMapperClass(WeatherMapper.class);
        job.setReducerClass(WeatherReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.addInputPath(job, new Path(inputPath));
        FileOutputFormat.setOutputPath(job, new Path(outputPath));

        boolean success = job.waitForCompletion(true);
        System.out.println("WeatherDistMR: " + (success ? "成功" : "失败"));
    }
}
