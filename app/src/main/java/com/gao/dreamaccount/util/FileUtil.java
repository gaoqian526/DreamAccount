package com.gao.dreamaccount.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件工具类
 *
 * @author shimiso
 */
public class FileUtil {
    private static final String TAG = "FileUtil";

    /**
     * 拷贝文件
     *
     * @param fromFile
     * @param toFile
     * @throws java.io.IOException
     */
    public static void copyFile(File fromFile, String toFile) throws IOException {

        FileInputStream from = null;
        FileOutputStream to = null;
        try {
            from = new FileInputStream(fromFile);
            to = new FileOutputStream(toFile);
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = from.read(buffer)) != -1)
                to.write(buffer, 0, bytesRead); // write
        } finally {
            if (from != null)
                try {
                    from.close();
                } catch (IOException e) {
                    LogUtil.e(TAG, "", e);
                }
            if (to != null)
                try {
                    to.close();
                } catch (IOException e) {
                    LogUtil.e(TAG, "", e);
                }
        }
    }

    /**
     * 创建文件
     *
     * @param file
     * @return
     */
    public static File createNewFile(File file) {

        try {
            if (file.exists()) {
                return file;
            }
            File dir = file.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            Log.e("", e.getMessage());
            return null;
        }
        return file;
    }

    /**
     * 创建文件
     *
     * @param path
     */
    public static File createNewFile(String path) {
        File file = new File(path);
        return createNewFile(file);
    }// end method createText()

    /**
     * 删除文件
     *
     * @param path
     */
    public static void deleteFile(String path) {
        File file = new File(path);
        deleteFile(file);
    }

    /**
     * 删除文件
     *
     * @param file
     */
    public static void deleteFile(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isFile()) {
            file.delete();
        } else if (file.isDirectory()) {
            File files[] = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteFile(files[i]);
            }
        }
        file.delete();
    }

    /**
     * 向Text文件中写入内容
     *
     * @param path
     * @param content
     * @return
     */
    public static boolean write(String path, String content) {
        return write(path, content, false);
    }

    public static boolean write(String path, String content, boolean append) {
        return write(new File(path), content, append);
    }

    public static boolean write(File file, String content) {
        return write(file, content, false);
    }

    public static boolean write(File file, String content, boolean append) {
        if (file == null || TextUtils.isEmpty(content)) {
            return false;
        }
        if (!file.exists()) {
            file = createNewFile(file);
        }
        FileOutputStream ops = null;
        try {
            ops = new FileOutputStream(file, append);
            ops.write(content.getBytes());
        } catch (Exception e) {
            LogUtil.e(TAG, "", e);
            return false;
        } finally {
            try {
                ops.close();
            } catch (Exception e) {
                LogUtil.e(TAG, "", e);
            }
            ops = null;
        }

        return true;
    }

    /**
     * 获得文件名
     *
     * @param path
     * @return
     */
    public static String getFileName(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        File f = new File(path);
        String name = f.getName();
        f = null;
        return name;
    }

    /**
     * 读取文件内容，从第startLine行开始，读取lineCount行
     *
     * @param file
     * @param startLine
     * @param lineCount
     * @return 读到文字的list, 如果list.size<lineCount则说明读到文件末尾了
     */
    public static List<String> readFile(File file, int startLine, int lineCount) {
        if (file == null || startLine < 1 || lineCount < 1) {
            return null;
        }
        if (!file.exists()) {
            return null;
        }
        FileReader fileReader = null;
        List<String> list = null;
        try {
            list = new ArrayList<String>();
            fileReader = new FileReader(file);
            LineNumberReader lnr = new LineNumberReader(fileReader);
            boolean end = false;
            for (int i = 1; i < startLine; i++) {
                if (lnr.readLine() == null) {
                    end = true;
                    break;
                }
            }
            if (end == false) {
                for (int i = startLine; i < startLine + lineCount; i++) {
                    String line = lnr.readLine();
                    if (line == null) {
                        break;
                    }
                    list.add(line);

                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "read log error!", e);
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    /**
     * 创建文件夹
     *
     * @param dir
     * @return
     */
    public static boolean createDir(File dir) {
        try {
            if (!dir.exists()) {
                dir.mkdirs();
            }
            return true;
        } catch (Exception e) {
            LogUtil.e(TAG, "create dir error", e);
            return false;
        }
    }

    /**
     * 在SD卡上创建目录
     *
     * @param dirName
     */
    public static File creatSDDir(String dirName) {
        File dir = new File(dirName);
        dir.mkdir();
        return dir;
    }

    /**
     * 判断SD卡上的文件是否存在
     */
    public static boolean isFileExist(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    /**
     * 将一个InputStream里面的数据写入到SD卡中
     */
    public static File write2SDFromInput(String path, String fileName, InputStream input) {
        File file = null;
        OutputStream output = null;
        try {
            creatSDDir(path);
            file = createNewFile(path + "/" + fileName);
            output = new FileOutputStream(file);
            byte buffer[] = new byte[1024];
            int len = -1;
            while ((len = input.read(buffer)) != -1) {
                output.write(buffer, 0, len);
            }
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    // 从文件中一行一行的读取文件
    public static String readFile(File file) {
        Reader read = null;
        String content = "";
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            read = new FileReader(file);
            br = new BufferedReader(read);
            while ((content = br.readLine().toString().trim()) != null) {
                sb.append(content);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                read.close();
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * 读取数据流
     *
     * @param in 数据流
     * @return 读取的数据串
     * @throws java.io.IOException
     */
    public static String read(InputStream in) {
        BufferedReader reader = null;
        StringBuilder sb = null;
        try {
            sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(in), 1000);
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
            try {
                if (in != null)
                    in.close();
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    public static String getRealPath(Context mContext, Uri uri) {
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor actualimagecursor = mContext.getContentResolver().query(uri, proj, null, null, null);
            int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            actualimagecursor.moveToFirst();
            String img_path = actualimagecursor.getString(actual_image_column_index);
            File file = new File(img_path);
            return file.getAbsolutePath();
        } catch (Exception e) {
            return null;
        }
    }
}
