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
 * Job5: 地域统计
 * 按地域（华北/华东/华南/华中/西南/西北/东北）统计平均温度
 */
public class RegionStatMR {

    private static final Map<String, String> CITY_REGION = new HashMap<>();
    static {
        // 华北
        CITY_REGION.put("北京", "华北"); CITY_REGION.put("天津", "华北");
        CITY_REGION.put("石家庄", "华北"); CITY_REGION.put("唐山", "华北");
        CITY_REGION.put("保定", "华北"); CITY_REGION.put("太原", "华北");
        CITY_REGION.put("大同", "华北"); CITY_REGION.put("呼和浩特", "华北");
        CITY_REGION.put("包头", "华北");
        // 东北
        CITY_REGION.put("沈阳", "东北"); CITY_REGION.put("大连", "东北");
        CITY_REGION.put("长春", "东北"); CITY_REGION.put("吉林", "东北");
        CITY_REGION.put("哈尔滨", "东北"); CITY_REGION.put("大庆", "东北");
        // 华东
        CITY_REGION.put("上海", "华东"); CITY_REGION.put("南京", "华东");
        CITY_REGION.put("苏州", "华东"); CITY_REGION.put("无锡", "华东");
        CITY_REGION.put("杭州", "华东"); CITY_REGION.put("宁波", "华东");
        CITY_REGION.put("温州", "华东"); CITY_REGION.put("合肥", "华东");
        CITY_REGION.put("芜湖", "华东"); CITY_REGION.put("福州", "华东");
        CITY_REGION.put("厦门", "华东"); CITY_REGION.put("泉州", "华东");
        CITY_REGION.put("南昌", "华东"); CITY_REGION.put("九江", "华东");
        CITY_REGION.put("赣州", "华东"); CITY_REGION.put("济南", "华东");
        CITY_REGION.put("青岛", "华东"); CITY_REGION.put("烟台", "华东");
        // 华南
        CITY_REGION.put("广州", "华南"); CITY_REGION.put("深圳", "华南");
        CITY_REGION.put("珠海", "华南"); CITY_REGION.put("南宁", "华南");
        CITY_REGION.put("桂林", "华南"); CITY_REGION.put("北海", "华南");
        CITY_REGION.put("海口", "华南"); CITY_REGION.put("三亚", "华南");
        // 华中
        CITY_REGION.put("郑州", "华中"); CITY_REGION.put("洛阳", "华中");
        CITY_REGION.put("南阳", "华中"); CITY_REGION.put("武汉", "华中");
        CITY_REGION.put("宜昌", "华中"); CITY_REGION.put("襄阳", "华中");
        CITY_REGION.put("长沙", "华中"); CITY_REGION.put("岳阳", "华中");
        CITY_REGION.put("张家界", "华中");
        // 西南
        CITY_REGION.put("重庆", "西南"); CITY_REGION.put("成都", "西南");
        CITY_REGION.put("绵阳", "西南"); CITY_REGION.put("乐山", "西南");
        CITY_REGION.put("贵阳", "西南"); CITY_REGION.put("遵义", "西南");
        CITY_REGION.put("昆明", "西南"); CITY_REGION.put("丽江", "西南");
        CITY_REGION.put("大理", "西南"); CITY_REGION.put("拉萨", "西南");
        CITY_REGION.put("日喀则", "西南");
        // 西北
        CITY_REGION.put("西安", "西北"); CITY_REGION.put("咸阳", "西北");
        CITY_REGION.put("延安", "西北"); CITY_REGION.put("兰州", "西北");
        CITY_REGION.put("天水", "西北"); CITY_REGION.put("敦煌", "西北");
        CITY_REGION.put("西宁", "西北"); CITY_REGION.put("海东", "西北");
        CITY_REGION.put("银川", "西北"); CITY_REGION.put("石嘴山", "西北");
        CITY_REGION.put("乌鲁木齐", "西北"); CITY_REGION.put("克拉玛依", "西北");
        CITY_REGION.put("喀什", "西北"); CITY_REGION.put("伊犁", "西北");
    }

    public static class RegionMapper extends Mapper<Object, Text, Text, DoubleWritable> {
        @Override
        protected void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {
            try {
                String json = value.toString();
                String city = extractCity(json);
                String temp = extractTemp(json);
                if (city != null && temp != null) {
                    String region = CITY_REGION.getOrDefault(city, "其他");
                    context.write(new Text(region), new DoubleWritable(Double.parseDouble(temp)));
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

    public static class RegionReducer extends Reducer<Text, DoubleWritable, Text, Text> {
        @Override
        protected void reduce(Text key, Iterable<DoubleWritable> values, Context context)
                throws IOException, InterruptedException {
            double sum = 0;
            int count = 0;
            double maxTemp = -100;
            double minTemp = 100;

            for (DoubleWritable val : values) {
                double t = val.get();
                sum += t;
                count++;
                maxTemp = Math.max(maxTemp, t);
                minTemp = Math.min(minTemp, t);
            }

            if (count > 0) {
                double avg = sum / count;
                String result = String.format("平均:%.1f°C  最高:%.1f°C  最低:%.1f°C  城市数:%d",
                        avg, maxTemp, minTemp, count);
                context.write(key, new Text(result));
            }
        }
    }

    public static void run(String inputPath, String outputPath) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Region Statistics");
        job.setJarByClass(RegionStatMR.class);

        job.setMapperClass(RegionMapper.class);
        job.setReducerClass(RegionReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(DoubleWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.addInputPath(job, new Path(inputPath));
        FileOutputFormat.setOutputPath(job, new Path(outputPath));

        boolean success = job.waitForCompletion(true);
        System.out.println("RegionStatMR: " + (success ? "成功" : "失败"));
    }
}
