package cn.vko.eduorder.utils;

import cn.sinojy.front.util.FrontCrypt;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.SocketException;
import java.util.Map;

public class FTPClientUtils {
    private static Logger logger = LoggerFactory.getLogger(FTPClientUtils.class);


    //测试
    public static String hostname = "39.106.254.228";
    public static String username = "rechdebug";
    public static String password = "kjfh049uJHdskjdh2ui";


    //ftp服务器端口号默认为21
    public static Integer port = 50021 ;

    public static FTPClient ftpClient = null;

    /**
     * 初始化ftp服务器
     */
    public static void initFtpClient() {
        ftpClient = new FTPClient();
        ftpClient.setControlEncoding("utf-8");
        try {
            System.out.println("connecting...ftp服务器:"+hostname+":"+port);
            ftpClient.connect(hostname, port); //连接ftp服务器
            ftpClient.login(username, password); //登录ftp服务器
            int replyCode = ftpClient.getReplyCode(); //是否成功登录服务器
            if(!FTPReply.isPositiveCompletion(replyCode)){
                System.out.println("connect failed...ftp服务器:"+hostname+":"+port);
                logger.error("connect failed..ftp服务器:"+hostname+":"+port+":"+username+":"+password);
            }
            System.out.println("connect successfu...ftp服务器:"+hostname+":"+port);
            logger.error("connect failed..ftp服务器:"+hostname+":"+port+":"+username+":"+password);
        }catch (Exception e) {
            logger.error("初始化ftp服务器异常:"+hostname+":"+port+":"+username+":"+password,e);
        }
    }

    /**
     * 上传文件
     * @param pathname ftp服务保存地址
     * @param fileName 上传到ftp的文件名
     *  @param originfilename 待上传文件的名称（绝对地址） *
     * @return
     */
    public boolean uploadFile( String pathname, String fileName,String originfilename){
        boolean flag = false;
        InputStream inputStream = null;
        try{
            System.out.println("开始上传文件");
            inputStream = new FileInputStream(new File(originfilename));
            initFtpClient();
            ftpClient.setFileType(ftpClient.BINARY_FILE_TYPE);
            CreateDirecroty(pathname);
            ftpClient.makeDirectory(pathname);
            ftpClient.changeWorkingDirectory(pathname);
            ftpClient.enterLocalPassiveMode();//被动模式
            //ftpClient.enterLocalActiveMode();// //主动模式
            ftpClient.storeFile(fileName, inputStream);
            inputStream.close();
            ftpClient.logout();
            flag = true;
            System.out.println("上传文件成功");
        }catch (Exception e) {
            System.out.println("上传文件失败");
            logger.error("上传文件失败",e);
            e.printStackTrace();
        }finally{
            if(ftpClient.isConnected()){
                try{
                    ftpClient.disconnect();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
            if(null != inputStream){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
    /**
     * 上传文件
     * @param pathname ftp服务保存地址
     * @param fileName 上传到ftp的文件名
     * @param inputStream 输入文件流
     * @return
     */
    public boolean uploadFile( String pathname, String fileName,InputStream inputStream){
        boolean flag = false;
        try{
            System.out.println("开始上传文件");
            initFtpClient();
            ftpClient.setFileType(ftpClient.BINARY_FILE_TYPE);
            CreateDirecroty(pathname);
            ftpClient.makeDirectory(pathname);
            ftpClient.changeWorkingDirectory(pathname);
            ftpClient.enterLocalPassiveMode();//被动模式
            //ftpClient.enterLocalActiveMode();// //主动模式
            ftpClient.storeFile(fileName, inputStream);
            inputStream.close();
            ftpClient.logout();
            flag = true;
            System.out.println("上传文件成功");
        }catch (Exception e) {
            System.out.println("上传文件失败");
            logger.error("上传文件失败",e);
        }finally{
            if(ftpClient.isConnected()){
                try{
                    ftpClient.disconnect();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
            if(null != inputStream){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
    //改变目录路径
    public boolean changeWorkingDirectory(String directory) {
        boolean flag = true;
        try {
            flag = ftpClient.changeWorkingDirectory(directory);
            if (flag) {
                System.out.println("进入文件夹" + directory + " 成功！");

            } else {
                System.out.println("进入文件夹" + directory + " 失败！开始创建文件夹");
                logger.error("进入文件夹" + directory + " 失败！开始创建文件夹");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return flag;
    }

    //创建多层目录文件，如果有ftp服务器已存在该文件，则不创建，如果无，则创建
    public boolean CreateDirecroty(String remote) throws IOException {
        logger.error("创建文件夹的方法remote=" + remote);
        boolean success = true;
        String directory = remote + "/";
        logger.error("创建文件夹的方法directory=" + directory);
        // 如果远程目录不存在，则递归创建远程服务器目录
        if (!directory.equalsIgnoreCase("/") && !changeWorkingDirectory(new String(directory))) {
            logger.error("创建文件夹的方法判断完目录");
            int start = 0;
            int end = 0;
            if (directory.startsWith("/")) {
                start = 1;
            } else {
                start = 0;
            }
            end = directory.indexOf("/", start);
            String path = "";
            String paths = "";
            for (int i = 1; i < 10; i++) {
                logger.error("创建文件夹的方法" + i + ",subDirectory=" + remote.substring(start, end));
                String subDirectory = new String(remote.substring(start, end).getBytes("GBK"), "iso-8859-1");
                logger.error("创建文件夹的方法" + i + ",subDirectory=" + subDirectory);
                subDirectory = remote.substring(start, end);
                path = path + "/" + subDirectory;
                logger.error("创建文件夹的方法" + i + ",path=" + path);
                //logger.error("创建文件夹的方法" + i + ",existFile=" + existFile(path));
                //if (!existFile(path)) {
                if (makeDirectory(subDirectory)) {
                    changeWorkingDirectory(subDirectory);
                } else {
                    System.out.println("创建目录[" + subDirectory + "]失败");
                    logger.error("创建文件夹的方法" + i + ",创建目录[" + subDirectory + "]失败");
                    changeWorkingDirectory(subDirectory);
                }
//                    } else {
//                        changeWorkingDirectory(subDirectory);
//                    }

                paths = paths + "/" + subDirectory;
                logger.error("创建文件夹的方法" + i + ",paths" + paths);
                start = end + 1;
                end = directory.indexOf("/", start);
                // 检查所有目录是否创建完毕
                if (end <= start) {
                    break;
                }
                if (i == 9) {
                    logger.error("创建文件夹的方法死循环");
                }
            }
        }
        return success;
    }

    //判断ftp服务器文件是否存在
    public boolean existFile(String path) throws IOException {
        boolean flag = false;
        FTPFile[] ftpFileArr = ftpClient.listFiles(path);
        if (ftpFileArr.length > 0) {
            flag = true;
        }
        return flag;
    }
    //创建目录
    public boolean makeDirectory(String dir) {
        boolean flag = true;
        try {
            flag = ftpClient.makeDirectory(dir);
            if (flag) {
                System.out.println("创建文件夹" + dir + " 成功！");
                logger.error("创建文件夹" + dir + " 成功！");
            } else {
                System.out.println("创建文件夹" + dir + " 失败！");
                logger.error("创建文件夹" + dir + " 失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /** * 下载文件 *
     * @param pathname FTP服务器文件目录 *
     * @param filename 文件名称 *
     * @param localpath 下载后的文件路径 *
     * @return */
    public  static boolean downloadFile(String pathname, String filename, String localpath){
        boolean flag = false;
        OutputStream os=null;
        try {

            initFtpClient();
            ftpClient.enterLocalPassiveMode();//被动模式
//            ftpClient.enterLocalActiveMode();//主动模式
            //切换FTP目录
            ftpClient.changeWorkingDirectory(pathname);
            logger.error("*****************开始获取该目录下 "+pathname+" 有多少文件********************");
            System.out.println("*****************开始获取该目录下 "+pathname+" 有多少文件********************");

            FTPFile[] ftpFiles = ftpClient.listFiles();//如果目录不存在，则会获取根目录的文件或目录

            logger.error("该下载目录："+pathname+" 有"+ftpFiles.length+"个文件");
            System.out.println("该下载目录："+pathname+" 有"+ftpFiles.length+"个文件");
            for(FTPFile file : ftpFiles){
                if(filename.equalsIgnoreCase(file.getName())){
                    File localFile = new File(localpath + "/" + DateUtil.getDayDateString(0)+"_"+file.getName());
                    os = new FileOutputStream(localFile);
                    ftpClient.retrieveFile(file.getName(), os);
                    os.close();
                }
            }
            ftpClient.logout();
            flag = true;
            System.out.println("下载文件成功");
        } catch (Exception e) {
            System.out.println("下载文件失败");
            logger.error("下载文件失败",e);
            e.printStackTrace();
        } finally{
            if(ftpClient.isConnected()){
                try{
                    ftpClient.disconnect();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
            if(null != os){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }
    public String  downloadFileq(String path,String ftpName,File localFile) {
        boolean flag=true;
        initFtpClient();
        //保存至Ftp
        try {

            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            //ftpClient.enterLocalPassiveMode(); // 用被动模式传输,解决linux服务长时间等待，导致超时问题
            ftpClient.setBufferSize(1024*1024);//设置缓存区，默认缓冲区大小是1024，也就是1K
            //切换目录，目录不存在创建目录
            ftpClient.changeWorkingDirectory(path);
            OutputStream os = new FileOutputStream(localFile);
            flag = ftpClient.retrieveFile(ftpName, os);
            //关闭流
            os.flush();
            os.close();
            //关闭连接
            ftpClient.logout();
            ftpClient.disconnect();
            System.out.println("******************downloadFileq下载成功：——"+ftpName+"*******************************");
            logger.error("******************downloadFileq下载成功——"+ftpName+"*******************************");
        } catch (SocketException e) {
            System.out.println("*****************downloadFileq-SocketException下载失败："+e);
            logger.error("*****************downloadFileq-SocketException下载失败："+e);
            return "SocketNotSuccess-SocketException";
        } catch (IOException e) {
            System.out.println("*****************downloadFileq-IOException下载失败："+e);
            logger.error("*****************downloadFileq-IOException下载失败下载失败："+e);
            return "SocketNotSuccess-IOException";
        }
        return "************downloadFileq下载成功******************";
    }


    public static String readFileContent(String filePath){
        StringBuffer buffer = new StringBuffer();
        try {
//            String filePath = "D:/111261_144_20210805.txt";
            FileInputStream fin = new FileInputStream(filePath);
            InputStreamReader reader = new InputStreamReader(fin);
            BufferedReader buffReader = new BufferedReader(reader);
            String strTmp = "";
            while((strTmp = buffReader.readLine())!=null){
                buffer.append(strTmp);
            }
            buffReader.close();
            return buffer.toString();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /** * 删除文件 *
     * @param pathname FTP服务器保存目录 *
     * @param filename 要删除的文件名称 *
     * @return */
    public boolean deleteFile(String pathname, String filename){
        boolean flag = false;
        try {
            System.out.println("开始删除文件");
            initFtpClient();
            //切换FTP目录
            ftpClient.changeWorkingDirectory(pathname);
            ftpClient.dele(filename);
            ftpClient.logout();
            flag = true;
            System.out.println("删除文件成功");
        } catch (Exception e) {
            System.out.println("删除文件失败");
            logger.error("删除文件失败",e);
            e.printStackTrace();
        } finally {
            if(ftpClient.isConnected()){
                try{
                    ftpClient.disconnect();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

    public static void main(String[] args) {

        //生成对账加密数据
        StringBuffer buffer = new StringBuffer();
        buffer.append("2"+"|"+"2"+"\\n");
        buffer.append("19012345678999903420"+"|"+"20210913121218"+"|"+"450858627436"+"|"+"123"+"|"+"20"+"|"+"S");
        String encrypt = FrontCrypt.encrypt(buffer.toString(), "12345678");
        System.out.println(encrypt);


//        FTPClientUtils ftp =new FTPClientUtils();
//        //ftp.uploadFile("/US", "te111.png", "D://download//test111.png");
//        ftp.downloadFile("111261/20210805/", "111261_144_20210805.txt", "D://");

//			File f=new File("D:/bbb.jpg");
//			System.out.println(ftp.downloadFileq("/AS/20190730/12345678/", "1.jpg", f));


        readFileContent("D:/"+DateUtil.getDayDateString(0)+"_"+"111261_144_20210805.txt");
    }
}
