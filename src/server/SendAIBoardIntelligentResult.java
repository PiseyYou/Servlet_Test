package server;

import Decoder.BASE64Decoder;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

@WebServlet("/aiboardservice/api/video.SendAIBoardIntelligentResult")
public class SendAIBoardIntelligentResult extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");

        String Addr = request.getRemoteAddr();
        String sendURI = request.getRequestURI();
        System.out.println("URL:" + Addr + sendURI);

        StringBuilder jsonString = new StringBuilder();
        BufferedReader reader = request.getReader();
        try {
            String line;
            while ((line = reader.readLine())!= null) {
                jsonString.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("接收图片异常");
        }
        finally {
            reader.close();
        }


        //对AI事件进行分类保存
        HashMap<String, String> Tasklist = new HashMap<String, String>();
//        Tasklist.put("1","CarPlate");
//        Tasklist.put("2","TrafficJam");
        Tasklist.put("3","HighwayPerson");
        Tasklist.put("4","MaintenanceEvents");
//        Tasklist.put("5","AnimalDetector");
//        Tasklist.put("6","FlowStatistics");
//        Tasklist.put("7","TrafficAccident");

        //创建对应文件夹
        String basePath = "D:\\picSave\\";
        File basedir = new File(basePath);
        Set<String> keys = Tasklist.keySet();
        for (String key : keys) {
//            System.out.println("keys的值为:"+key);
            String value = Tasklist.get(key);
            File TaskSaveDir = new File(basePath+value);
            System.out.println(TaskSaveDir);
            if(!TaskSaveDir.exists()) {
                TaskSaveDir.mkdir();
            }
        }

        //对字符串进行序列化
        JSONObject jsonObject = JSON.parseObject(jsonString.toString());
        System.out.println("获取出client端接收的内容为:" + jsonObject);

        //获取json对应值对
        JSONArray mrl = jsonObject.getJSONArray("result_list");
        for(int j=0; j<mrl.size(); j++) {
            JSONObject srl = mrl.getJSONObject(j);
            JSONArray ssrl = srl.getJSONArray("result_list");
            String AITask_No = srl.getString("AITask_No");

//            JSONArray ssrl = mrl.getJSONObject(i).getJSONArray("result_list");
//            System.out.println(mrl.getJSONObject(i).get("result_list"));
            System.out.println("解析出result_list的ssrl的内容为:"+ssrl);

            for (int i =0; i<ssrl.size();i++) {
                JSONObject event = ssrl.getJSONObject(i);
                String pic = event.getString("pic");
//            System.out.println("pic:" +pic);
//            System.out.println("pic的长度为:"+pic.length());
//
                String filename = event.getString("event");
                String time = event.getString("time");
                System.out.println("时间为:"+time +"的图片string大小为"+pic.length());
////            FileOutputStream fos = new FileOutputStream("/home/pisey/Downloads/picsave/" + filename + "_" +time +".png");
//            String picSave = "/home/pisey/Downloads/picsave/" + filename + "_" +time +".png";


                //对base64传输的图片进行解码
                BASE64Decoder decoder = new BASE64Decoder();
                byte[] bytes = decoder.decodeBuffer(pic);
                int k = 0;
                if (null == bytes || 0 == bytes.length)
                    System.out.println("获取到图片信息为0"); ;
                for (; k < bytes.length; k++) {
                    if (bytes[k] < 0)
                        bytes[k] +=256;
                }
                System.out.println("获取到的图片的长度为:" + k);

                Date date=new Date(Long.parseLong(time+"000"));
                SimpleDateFormat myFmt = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
                String htime = myFmt.format(date);
//            System.out.println("序列化后的时间为:" + htime);
//            System.out.println(basePath + Tasklist.get(filename) + "\\" + htime +".png");

                File AITask_No_Dir = new File(basePath + Tasklist.get(filename) + "\\" + AITask_No);
                if(!AITask_No_Dir.exists()) {
                    AITask_No_Dir.mkdir();
                }
//                FileOutputStream fos = new FileOutputStream(basePath + Tasklist.get(filename) + "\\" + AITask_No + "_" + htime +".png");
                FileOutputStream fos = new FileOutputStream(basePath + Tasklist.get(filename) + "\\" + AITask_No + "\\" + AITask_No + "_" + htime +".png");
                System.out.println("保存的路径为:"+basePath + Tasklist.get(filename) + "\\" + AITask_No + "\\" + AITask_No + "_" + htime +".png");
                fos.write(bytes);
            }

        }
//        JSONObject srl = mrl.getJSONObject(0);
//        JSONArray ssrl = srl.getJSONArray("result_list");
//        List<MyClass> list = JSONObject.parseArray(jsonStr,  MyClass.class);
//        System.out.println("解析出result_list的ssrl的内容为:"+ssrl);







        Feedback fmsg = new Feedback(200, "指令处理成功");
        JSONObject jsonObj = (JSONObject) JSON.toJSON(fmsg);
//
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(jsonObj.toString());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request,response);
    }
}
