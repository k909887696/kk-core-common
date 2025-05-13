package com.kk;

import static org.junit.Assert.assertTrue;

import com.kk.common.utils.*;
import org.junit.Test;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        conceptVo vo = new  conceptVo();
        vo.setSrc("ts");
        Map<String, Object> params = new HashMap<>();
        params.put("api_name","concept");
        params.put("token","43b07975e999aded7228d2989951b45bab0acd3bdb92d30ca6d5e5aa");
        params.put("params",vo);
        params.put("fields","code,name,src");
        String res = httpUtil.httpRestRequest(params,"http://api.waditu.com",String.class);
    }

    /**
     * Rigorous Test :-)
     */
    @Test
    public void testLixi()
    {
        double benjin = 110000,yuehuan=19081.33,lixi=748;
        int qishu =6;
        double lilv =0;
        for(int i=0;i<qishu;i++)
        {
            double temp = lixi/(benjin-yuehuan*i);
            System.out.println("第"+(i+1)+"期的利息："+temp);
            lilv+=temp;
        }
        System.out.println("总利息："+lilv);
        System.out.println("年利率："+lilv/qishu);
    }

    @Test
    public void testDownLoad() throws Exception {
        String url = "https://t00img.yangkeduo.com/goods/images/2020-03-29/564a8ae0-2ea4-4ca3-a32e-30e2f630895a.jpg";
        String newFileName=url.substring(url.lastIndexOf("/")+1,url.lastIndexOf(".")-1);
        String newFileSuffix=url.substring(url.lastIndexOf(".")+1);
        String savePath ="E:\\淘宝\\picture\\商品资料\\"+ DateUtil.date2String(new Date(),DateUtil.PATTERN_STANDARD08W) +"\\" +newFileName+"."+newFileSuffix;
        FileUtil.createFile( new File(savePath));
        httpUtil.download(url,savePath);
        System.out.println("savePath："+savePath.substring(0,20));

    }

    @Test
    public  void testRegUtil()
    {
        String password ="dsfdsfsdfsdfsdfsd";
        System.out.println(JsonUtil.getJSONString( RegUtil.passwordValid(password)));
    }
    @Test
    public void testReadExcel() throws Exception {
        String filePath = "F:\\log\\Query (2)(9).xlsx";
        List<Map<String,Object>> excelData = ExcelUtils.readExcel(filePath,null,0);
        for(Map<String,Object> map : excelData)
        {
            System.out.println(map);
        }
    }
    @Test
    public void testRsa() throws Exception {
        String input = "-random";
        String  encryptRSAStr = RSAUtil.encryptRSAStr(input);
        System.out.println(encryptRSAStr);
        System.out.println(RSAUtil.decryptRSAStr(encryptRSAStr));
    }
    @Test
    public void testSHA()
    {
        String input = DateUtil.date2String(new Date(),DateUtil.PATTERN_STANDARD17W);
        System.out.println(input +"LR2025000000003");
        System.out.println(SHAUtil.getSHA1(input));
    }
}

class conceptVo{

    private String src;

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }
}
