package abcdef.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

/**
 * 操作文件压缩,解压的工具
 * 目前支持:
 * 压缩:.tar .gz
 * 解压:.tar .gz
 * @author wuhuan 2018-10-29
 */
public class CompressUtil {
	
	/**
     * 归档 压缩为.tar
     * @param entry
     * @throws IOException
     * @author yutao
     * @return 
     * @date 2017年5月27日下午1:48:23
     */
    public static String archive(String entry) throws IOException {
        File file = new File(entry);

        TarArchiveOutputStream tos = new TarArchiveOutputStream(new FileOutputStream(file.getAbsolutePath() + ".tar"));
        String base = file.getName();
        if(file.isDirectory()){
            archiveDir(file, tos, base);
        }else{
            archiveHandle(tos, file, base);
        }

        tos.close();
        return file.getAbsolutePath() + ".tar";
    }

    /**
     * 递归处理，准备好路径
     * @param file
     * @param tos
     * @param base 
     * @throws IOException
     * @author yutao
     * @date 2017年5月27日下午1:48:40
     */
    private static void archiveDir(File file, TarArchiveOutputStream tos, String basePath) throws IOException {
        File[] listFiles = file.listFiles();
        for(File fi : listFiles){
            if(fi.isDirectory()){
                archiveDir(fi, tos, basePath + File.separator + fi.getName());
            }else{
                archiveHandle(tos, fi, basePath);
            }
        }
    }

    /**
     * 具体归档处理（文件）
     * @param tos
     * @param fi
     * @param base
     * @throws IOException
     * @author yutao
     * @date 2017年5月27日下午1:48:56
     */
    private static void archiveHandle(TarArchiveOutputStream tos, File fi, String basePath) throws IOException {
        TarArchiveEntry tEntry = new TarArchiveEntry(basePath + File.separator + fi.getName());
        tEntry.setSize(fi.length());

        tos.putArchiveEntry(tEntry);

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fi));

        byte[] buffer = new byte[1024];
        int read = -1;
        while((read = bis.read(buffer)) != -1){
            tos.write(buffer, 0 , read);
        }
        bis.close();
        tos.closeArchiveEntry();//这里必须写，否则会失败
    }
    
    /**
     * 把tar包压缩成gz
     * @param path
     * @throws IOException
     * @author yutao
     * @return 
     * @date 2017年5月27日下午2:08:37
     */
    public static String compressArchive(String path) throws IOException{
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));

        GzipCompressorOutputStream gcos = new GzipCompressorOutputStream(new BufferedOutputStream(new FileOutputStream(path + ".gz")));

        byte[] buffer = new byte[1024];
        int read = -1;
        while((read = bis.read(buffer)) != -1){
            gcos.write(buffer, 0, read);
        }
        gcos.close();
        bis.close();
        return path + ".gz";
    }
	
	/**
     * 解压.gz
     * @param archive
     * @author yutao
     * @throws IOException 
     * @date 2017年5月27日下午4:03:29
     */
    public static void unCompressArchiveGz(String archive) throws IOException {

        File file = new File(archive);

        BufferedInputStream bis = 
        new BufferedInputStream(new FileInputStream(file));

        String fileName = 
        file.getName().substring(0, file.getName().lastIndexOf("."));

        String finalName = file.getParent() + File.separator + fileName;

        BufferedOutputStream bos = 
        new BufferedOutputStream(new FileOutputStream(finalName));

        GzipCompressorInputStream gcis = 
        new GzipCompressorInputStream(bis);

        byte[] buffer = new byte[1024];
        int read = -1;
        while((read = gcis.read(buffer)) != -1){
            bos.write(buffer, 0, read);
        }
        gcis.close();
        bos.close();

//        unCompressTar(finalName);
    }
    
    /**
     * 解压tar
     * @param finalName
     * @author yutao
     * @throws IOException 
     * @date 2017年5月27日下午4:34:41
     */
    public static void unCompressTar(String finalName) throws IOException {

        File file = new File(finalName);
        String parentPath = file.getParent();
        TarArchiveInputStream tais = 
        new TarArchiveInputStream(new FileInputStream(file));

        TarArchiveEntry tarArchiveEntry = null;

        while((tarArchiveEntry = tais.getNextTarEntry()) != null){
            String name = tarArchiveEntry.getName();
            File tarFile = new File(parentPath, name);
            if(!tarFile.getParentFile().exists()){
                tarFile.getParentFile().mkdirs();
            }

            BufferedOutputStream bos = 
            new BufferedOutputStream(new FileOutputStream(tarFile));

            int read = -1;
            byte[] buffer = new byte[1024];
            while((read = tais.read(buffer)) != -1){
                bos.write(buffer, 0, read);
            }
            bos.close();
        }
        tais.close();
        file.delete();//删除tar文件
    }
    
//    public static void main(String[] args) throws IOException {
//    	// 压缩为.tar
////    	archive("C:\\Users\\wuhuan\\Desktop\\baidu-temp\\123");
//    	// 压缩为.gz
////    	compressArchive("C:\\Users\\wuhuan\\Desktop\\baidu-temp\\123.tar");
//    	// 解压.gz
//    	unCompressArchiveGz("C:\\Users\\wuhuan\\Desktop\\baidu-temp\\123.tar.gz");
//    	// 解压.tar
//    	unCompressTar("C:\\Users\\wuhuan\\Desktop\\baidu-temp\\123.tar");
//    	
//    	// 压缩/解压.tar.gz文件
//		String[] files = new String[5];
//		files[0] = "123";
//		files[1] = "C:\\Users\\v_wuhuanhuan_dxm\\Desktop\\baidu-temp\\dxm-sunlink\\文档集合\\研发文档";
//		files[2] = "C:\\Users\\v_wuhuanhuan_dxm\\Desktop\\baidu-temp\\dxm-sunlink\\文档集合\\研发文档\\度小满助贷平台接入系统接口联调测试用例_20180907.xlsx";
//		files[3] = "C:\\Users\\v_wuhuanhuan_dxm\\Desktop\\baidu-temp\\dxm-sunlink\\文档集合\\研发文档\\接口文档&日终文件\\百度助贷机构业务对账文件说明v0.9-标准版.xlsx";
//		files[4] = "";
//		System.out.println("-:"+Arrays.toString(files));
//		String destDir = "C:\\Users\\v_wuhuanhuan_dxm\\Desktop\\1234";
//		String tarFileName = "1234.tar.gz";
////		doTarGZip(files, destDir, tarFileName);
//		doUnTarGZip(destDir, destDir+"321", tarFileName);
//	}

	/**
     * 将指定全限定名文件列表压缩到指定目录下的.tar.gz格式文件
     * @param sourceFileQualifiedNames 全限定名文件名称数组
     * @param destDirStr 目标目录
     * @param tarFileName 目标文件名称(包括后缀,所以建议.tar.gz结尾)
     * @throws IOException
     */
    public static void doTarGZip(String[] sourceFileQualifiedNames, String destDirStr, String tarFileName) throws IOException {
        try {
        	Path destDirPath = Paths.get(destDirStr);
        	// 创建文件夹
        	destDirPath.toFile().mkdirs();
        	TarArchiveOutputStream tar = new TarArchiveOutputStream(
                    new GzipCompressorOutputStream(
                            new BufferedOutputStream(
                                    new FileOutputStream(Paths.get(destDirStr, tarFileName).toFile()))));
            // 加密文件打包
            File file;
            TarArchiveEntry tarEntry;
            for (String qualifiedName : sourceFileQualifiedNames) {
            	Path qualifiedNamePath = Paths.get(qualifiedName);
            	file = qualifiedNamePath.toFile();
                if (file.isFile()) {
                	tarEntry = new TarArchiveEntry(file.getName());
                	tarEntry.setSize(file.length());
                	tar.putArchiveEntry(tarEntry);
                	Files.copy(qualifiedNamePath, tar);
                	tar.closeArchiveEntry();
				}
            }
            tar.close();
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * 解包加密的文件,默认不删除压缩包.
     * 将.tar.gz的文件解压缩到指定目录
     */
    public static void doUnTarGZip(String sourceDirStr, String destDirStr, String tarFile) throws IOException {
        try {
        	doUnTarGZip(sourceDirStr, destDirStr, tarFile, false);
        } catch (IOException e) {
            throw  e;
        }
    }
	
    /**
     * 解包加密的文件.
     * 将.tar.gz的文件解压缩到指定目录
     * @param sourceDirStr 压缩包所在目录
     * @param destDirStr 解压目录
     * @param tarFile .tar.gz文件全名称
     * @param isDel 是否删除压缩包
     * @throws IOException
     */
    public static void doUnTarGZip(String sourceDirStr, String destDirStr, String tarFile, boolean isDel) throws IOException {
    	try {
    		TarArchiveInputStream tar = new TarArchiveInputStream(
    				new GzipCompressorInputStream(
    						new BufferedInputStream(
    								new FileInputStream(new File(sourceDirStr + File.separator + tarFile)))));
    		TarArchiveEntry entry;
    		// 创建文件夹
    		Path destDirPath = Paths.get(destDirStr);
    		// LinkOption.NOFOLLOW_LINKS表明不追踪符号链接。
    		if (Files.notExists(destDirPath, LinkOption.NOFOLLOW_LINKS)) {
    			Files.createDirectory(destDirPath);
			}
    		while ((entry = (TarArchiveEntry) tar.getNextEntry()) != null) {
    			int count;
    			byte[] data = new byte[0x400];
    			OutputStream fos = new FileOutputStream(Paths.get(destDirStr, entry.getName()).toFile());
    			while ((count = tar.read(data, 0, 0x400)) != -1) {
    				fos.write(data, 0, count);
    			}
    			fos.close();
    		}
    		tar.close();
    		// 解压后是否将.tar.gz文件删除
    		if (isDel) {
    			new File(sourceDirStr, tarFile).delete();
    		}
    	} catch (IOException e) {
    		throw  e;
    	}
    }
    
}
