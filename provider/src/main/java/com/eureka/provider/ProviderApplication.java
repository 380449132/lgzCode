package com.eureka.provider;
import com.alibaba.fastjson.JSONObject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;




@RestController
@EnableEurekaClient
@SpringBootApplication


public class ProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProviderApplication.class, args);
	}

	@Value("${myInfo.requestMappingName0}")
    private  String requestMappingName0;

	@Value("${myInfo.requestUrl0}")
    private  String requestUrl0;

    @Value("${myInfo.macAdd}")
    private String macAdd;

    @Value("${myInfo.forServer}")
    private String forServer;

    /**
     * 假如这个客户端要提供一个getUser的方法
     * @return
     */
//    @GetMapping(value = "/getUser")
//    @ResponseBody
//    public Map<String,Object> getUser(@RequestBody Map map){
//        Map<String,Object> data = new HashMap<>();
//        String id =(String) map.get("id");
//        data.put("id",id);
//        data.put("userName","admin");
//        data.put("from","provider-A");
//        return data;
//    }


    private String httpURLGETCase(String targetUrl) {
        System.out.println("业务服务地址:"+ targetUrl);
        String methodUrl = targetUrl;
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        String line = null;
        String outString = null;
        try {
            URL url = new URL(methodUrl);
            connection = (HttpURLConnection) url.openConnection();// 根据URL生成HttpURLConnection
            connection.setRequestMethod("GET");// 默认GET请求
            connection.connect();// 建立TCP连接
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));// 发送http请求
                StringBuilder result = new StringBuilder();
                // 循环读取流
                while ((line = reader.readLine()) != null) {
                    result.append(line).append(System.getProperty("line.separator"));// "\n"
                }
                outString = result.toString();
                System.out.println(result.toString());
                System.out.println(url);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            connection.disconnect();
            return outString;
        }
    }



    public static String sendPost(String urlStr, String dataStr) throws UnsupportedEncodingException {
        System.out.println("业务服务地址:"+ urlStr);
        String result = "";
        try {

            // 创建url资源
            URL url = new URL(urlStr);
            // 建立http连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 设置允许输入输出
            conn.setDoOutput(true);
            conn.setDoInput(true);

            // 设置不用缓存
            conn.setUseCaches(false);
            // 设置传递方式
            conn.setRequestMethod("POST");
            // 设置维持长连接
            conn.setRequestProperty("Connection", "Keep-Alive");
            // 设置文件字符集:
            conn.setRequestProperty("Charset", "UTF-8");
            //转换为字节数组
            byte[] data = dataStr.getBytes("UTF-8");
            // 设置文件长度
            conn.setRequestProperty("Content-Length", String.valueOf(data.length));
            // 设置文件类型:
            //conn.setRequestProperty("Content-Type", "text/xml");// 开始连接请求
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

            conn.connect();
            OutputStream out = conn.getOutputStream();
            // 写入请求的字符串
            out.write(data);
            out.flush();
            out.close();

            System.out.println(conn.getResponseCode());

            // 请求返回的状态
            if (conn.getResponseCode() == 200) {
                System.out.println("连接成功");
                // 请求返回的数据
                InputStream in = conn.getInputStream();try {
                    byte[] data1 = new byte[in.available()];
                    in.read(data1);
                    result = new String(data1,"UTF-8");
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            } else {
                result = String.valueOf(conn.getResponseCode());
                System.out.println("no++");
            }

        } catch (Exception e) {

        }



        return result;
    }


    @GetMapping(value = "/getUser")
    @ResponseBody
    public Map<String,Object> getUser(@RequestParam Integer id){
        Map<String,Object> data = new HashMap<>();
        //String id =(String) map.get("id");
        data.put("id",id);
        data.put("userName","admin");
        data.put("from","provider-A");
        return data;
    }

    @RequestMapping(value = "${myInfo.requestMappingName0}",   method = RequestMethod.POST)
    //@RequestMapping(value = "/testUser",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> requestMappingName0(@RequestBody Map map) throws UnsupportedEncodingException {
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("userName","admin");
        data.put("from","provider-A");

        data.put("myInfo.requestMappingName0",requestMappingName0);
        data.put("myInfo.requestUrl0",requestUrl0);

        //String targetUrl = "http://127.0.0.1:8080/demo/getUser?name=2";
        //String res = httpURLGETCase(targetUrl);

        //String param="name="+ URLEncoder.encode("丁丁","UTF-8");
        //String param="name=8888&&password=88888";
        //String param="{\"name\":\"pppp\",\"password\":\"pppp\"}";
        //JSONObject json = new JSONObject(map);
        String param = JSONObject.toJSONString(map);
        String res = sendPost(requestUrl0,param);

        data.put("res",res);

        return data;
    }

    @RequestMapping(value = "/micService001",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService001(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService001";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService002",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService002(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService002";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService003",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService003(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService003";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService004",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService004(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService004";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService005",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService005(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService005";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService006",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService006(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService006";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService007",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService007(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService007";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService008",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService008(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService008";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService009",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService009(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService009";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService010",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService010(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService010";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService011",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService011(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService011";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService012",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService012(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService012";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService013",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService013(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService013";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService014",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService014(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService014";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService015",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService015(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService015";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService016",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService016(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService016";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService017",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService017(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService017";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService018",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService018(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService018";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService019",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService019(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService019";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService020",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService020(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService020";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService021",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService021(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService021";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService022",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService022(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService022";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService023",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService023(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService023";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService024",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService024(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService024";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService025",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService025(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService025";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService026",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService026(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService026";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService027",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService027(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService027";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService028",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService028(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService028";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService029",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService029(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService029";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService030",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService030(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService030";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }
    @RequestMapping(value = "/micService031",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService031(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService031";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }
    @RequestMapping(value = "/micService032",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService032(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService032";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }
    @RequestMapping(value = "/micService033",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService033(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService033";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService034",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService034(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService034";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService035",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService035(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService035";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService036",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService036(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService036";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService037",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService037(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService037";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService038",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService038(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService038";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService039",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService039(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService039";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService040",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService040(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService040";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService041",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService041(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService041";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService042",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService042(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService042";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService043",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService043(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService043";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService044",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService044(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService044";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService045",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService045(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService045";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService046",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService046(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService046";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService047",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService047(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService047";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService048",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService048(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService048";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService049",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService049(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService049";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService050",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService050(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService050";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService051",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService051(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService051";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService052",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService052(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService052";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService053",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService053(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService053";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService054",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService054(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService054";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService055",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService055(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService055";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService056",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService056(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService056";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService057",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService057(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService057";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService058",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService058(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService058";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService059",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService059(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService059";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService060",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService060(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService060";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService061",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService061(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService061";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService062",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService062(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService062";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService063",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService063(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService063";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService064",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService064(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService064";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService065",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService065(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService065";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService066",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService066(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService066";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService067",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService067(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService067";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService068",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService068(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService068";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService069",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService069(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService069";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService070",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService070(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService070";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService071",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService071(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService071";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService072",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService072(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService072";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService073",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService073(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService073";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService074",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService074(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService074";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService075",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService075(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService075";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService076",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService076(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService076";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService077",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService077(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService077";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService078",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService078(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService078";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService079",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService079(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService079";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService080",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService080(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService080";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService081",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService081(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService081";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService082",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService082(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService082";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService083",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService083(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService083";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService084",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService084(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService084";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService085",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService085(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService085";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService086",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService086(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService086";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService087",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService087(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService087";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService088",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService088(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService088";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService089",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService089(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService089";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService090",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService090(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService090";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService091",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService091(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService091";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService092",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService092(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService092";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService093",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService093(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService093";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

    @RequestMapping(value = "/micService094",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService094(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService094";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }
    @RequestMapping(value = "/micService095",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService095(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService095";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }
    @RequestMapping(value = "/micService096",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService096(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService096";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }
    @RequestMapping(value = "/micService097",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService097(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService097";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }
    @RequestMapping(value = "/micService098",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService098(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService098";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }
    @RequestMapping(value = "/micService099",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService099(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService099";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }
    @RequestMapping(value = "/micService100",   method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> micService100(@RequestBody Map map) throws UnsupportedEncodingException {
        System.out.print("入参:"+JSONObject.toJSONString(map));
        Map<String,Object> data = new HashMap<>();
        String id =(String) map.get("id");
        data.put("id",id);
        data.put("from",macAdd);
        String param = JSONObject.toJSONString(map);
        String toUrl = forServer+"micService100";
        String busData = sendPost(toUrl,param);
        data.put("toUrl",toUrl);
        data.put("busData",busData);
        System.out.print("入参:"+JSONObject.toJSONString(map)+"出参:"+ JSONObject.toJSONString(data));
        return data;
    }

}
