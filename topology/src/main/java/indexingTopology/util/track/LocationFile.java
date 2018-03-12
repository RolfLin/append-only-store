package indexingTopology.util.track;

import cn.binarywang.tools.generator.ChineseNameGenerator;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.Assert.assertNotNull;

/**
 * Created by billlin on 2018/3/4
 */
public class LocationFile {
    public ArrayList<String> read2(String filePath){
        System.out.println("------Read File-------");
        ArrayList<String> arrayList = new ArrayList<String>();
        File file = new File(filePath);
        if(file.exists()){
            try {
                FileReader fileReader = new FileReader(file);
                BufferedReader br = new BufferedReader(fileReader);
                String lineContent = null;
                while((lineContent = br.readLine())!=null){
                    String location[] = lineContent.split("//");
                    arrayList.add(lineContent);
//                    System.out.println(lineContent);
//                    System.out.println(location[1]);
//

//                    String locationValue = location[2].split(" ")[1];
//                    System.out.println(locationValue);
//                    Pattern p = null;
//                    p = Pattern.compile("^\\-?[0-9]+\\.?[0-9]*+\\,\\-?[0-9]+\\.?[0-9]*");
//                    boolean b1 = p.matcher(locationValue).matches();
//                    if (b1) {
//                        arrayList.add(locationValue);
//                    }
//                    double leftTop_x = Double.parseDouble(locationValue.split(",")[0]);
//                    double leftTop_y = Double.parseDouble(locationValue.split(",")[1]);
//                    System.out.println(leftTop_x + " " + leftTop_y);


                }
                br.close();
                fileReader.close();
            } catch (FileNotFoundException e) {
                System.out.println("no this file");
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("io exception");
                e.printStackTrace();
            }
            return arrayList;
        }
        return null;
    }

    public ArrayList<String> getNameList(ArrayList<String> carPlateList){
        ArrayList<String> nameList = new ArrayList<String>();
        for(int i = 0;i < carPlateList.size(); i ++){
            boolean sameValue = false;
            for(int j = 0;j < i; j ++){
                if(carPlateList.get(j).equals(carPlateList.get(i))){
                    nameList.add(nameList.get(j));
                    sameValue = true;
                    break;
                }
            }
            if(sameValue == false){
                String generatedName = ChineseNameGenerator.getInstance().generate();
                nameList.add(generatedName);
            }
        }
        return nameList;
    }


    public static void main(String[] args) {
        String generatedName = ChineseNameGenerator.getInstance().generate();
        assertNotNull(generatedName);
        System.err.println(generatedName);

        LocationFile locationFile = new LocationFile();
//        locationFile.read2("201701-06localtion.txt");

//        ArrayList<String> carDetailList = locationFile.read2("20170201.txt");
//        ArrayList<String> carPlateList = new ArrayList<String>();
//        for(String record : carDetailList){
//            carPlateList.add(record.split("//")[2]);
//        }
//        ArrayList<String> nameList = locationFile.getNameList(carPlateList);
//        for(int i = 0;i < nameList.size(); i++){
//            System.out.println(carPlateList.get(i) + " " + nameList.get(i));
//        }
        String jsonStr = "{\"devid\":1,\"city\":[\"11\",\"22\"]}";
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);

        System.out.println(jsonObject.containsKey("city"));
        LineItem user = JSON.parseObject(jsonStr,LineItem.class);
        String city[] = user.getCity();
        System.out.println(city[1]);
        JSON.parse(jsonStr);
//        JSONArray items = JSONArray.
        System.out.println(jsonObject);
        System.out.println(jsonObject.get("name"));
        String a = "11";
        Object b = "22";
        if(a == b){
            System.out.println("success");
        }
        String citytest[] = {"4406"};
        LineItem lineItem = new LineItem(citytest,1,"123",null);
        System.out.println(lineItem.checkConform("4406",1,"123"));
    }
}
