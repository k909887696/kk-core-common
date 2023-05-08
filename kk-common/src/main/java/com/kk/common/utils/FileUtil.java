package com.kk.common.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public  class FileUtil {
    /**
     * 判断文件是否存在，不存在就创建
     * @param file
     */
    public static void createFile(File file) {
        if (file.exists()) {
            System.out.println("File exists");
        } else {
            System.out.println("File not exists, create it ...");
            //getParentFile() 获取上级目录(包含文件名时无法直接创建目录的)
            if (!file.getParentFile().exists()) {
                System.out.println("not exists");
                //创建上级目录
                file.getParentFile().mkdirs();
            }
            try {
                //在上级目录里创建文件
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createAndWriteTxtFile(String filePath,String content) {

        FileWriter fw = null;
        try
        {
            File file = new File(filePath);
            createFile(file);
            fw = new FileWriter(filePath);
            fw.write(content);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                fw.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
    }
}
