package com.kk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.kk.common.utils.*;
import org.junit.Test;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    @Test
    public void testRetryTimeMapping() {
        int[] expectedIntervals = {10, 20, 30, 40, 50, 50, 100, 100, 200, 200, 600, 600, 600};
        for (int retryCount = 1; retryCount <= expectedIntervals.length; retryCount++) {
            assertEquals("重试次数：" + retryCount,
                    expectedIntervals[retryCount - 1], RetryTimeUtil.mqRetryTime(retryCount));
        }
    }

    @Test
    public void testRetryTimeDefault() {
        int[] retryCounts = {Integer.MIN_VALUE, -1, 0, 14, Integer.MAX_VALUE};
        for (int retryCount : retryCounts) {
            assertEquals(1200, RetryTimeUtil.mqRetryTime(retryCount));
        }
    }

    @Test(expected = NullPointerException.class)
    public void testRetryTimeNull() {
        RetryTimeUtil.mqRetryTime(null);
    }

    @Test
    public void testDagRetryNextRunTimeUsesDateUtil() {
        int[] retryCounts = {0, 1, 2, 8, 20};
        int[] expectedSeconds = {10, 10, 20, 1280, 1800};
        for (int i = 0; i < retryCounts.length; i++) {
            Date earliest = DateUtil.addDate(new Date(), Calendar.SECOND, expectedSeconds[i]);
            Date actual = DagRetryIntervalUtil.calculateNextRunTime(retryCounts[i]);
            Date latest = DateUtil.addDate(new Date(), Calendar.SECOND, expectedSeconds[i]);
            assertTrue("下次执行时间不能早于预期间隔", !actual.before(earliest));
            assertTrue("下次执行时间不能晚于预期间隔", !actual.after(latest));
        }
    }

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
