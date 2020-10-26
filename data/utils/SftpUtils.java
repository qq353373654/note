package com.sunfintech.common.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.sunfintech.common.constant.StringConstant;

/**
 * sftp上传工具类
 * @author chenglj
 *
 */
public class SftpUtils {

	private static final Logger log = LoggerFactory.getLogger(SftpUtils.class);
	private ChannelSftp sftp;

	private Session session;
	/** FTP 登录用户名 */
	private String username;
	/** FTP 登录密码 */
	private String password;
	/** 私钥文件所在的完整路径 */
	private String privateKey;
	/** 生成私钥的时候填写的密码 */
	private String passphrase;
	/** FTP 服务器地址IP地址 */
	private String host;
	/** FTP 端口 */
	private int port;
	
	// handleTypeOfSame 下载时处理本地相同名称文件-覆盖-默认
	private static final String COVER = "COVER";
	// handleTypeOfSame 下载时处理本地相同名称文件-跳过
	private static final String CONTINUE = "CONTINUE";
	// handleTypeOfSame 下载时处理本地相同名称文件-本地文件重命名 newname_UUID
	private static final String BACKUP = "BACKUP";

	public SftpUtils() {
	}

	/**
	 * 构造基于密码认证的sftp对象
	 * 
	 * @param username
	 * @param password
	 * @param host
	 * @param port
	 */
	public SftpUtils(String username, String password, String host, int port) {
		this.username = username;
		this.password = password;
		this.host = host;
		this.port = port;
	}

	/**
	 * 构造基于秘钥认证的sftp对象
	 * 
	 * @param username
	 * @param host
	 * @param port
	 * @param privateKey
	 * @param passphrase
	 */
	public SftpUtils(String username, String host, int port, String privateKey, String passphrase) {
		this.username = username;
		this.host = host;
		this.port = port;
		this.privateKey = privateKey;
		this.passphrase = passphrase;
	}

	/**
	 * 连接sftp服务器
	 * 
	 */
	public void login() {
		try {
			JSch jsch = new JSch();

			if (privateKey != null && !StringConstant.EMPTY.equals(privateKey)) {
				if (passphrase != null && !StringConstant.EMPTY.equals(passphrase)) {
					jsch.addIdentity(privateKey, passphrase);
				} else {
					jsch.addIdentity(privateKey);
				}
				log.info("sftp connect,path of private key file：{}", privateKey);
			}

			log.info("sftp connect by host:{} username:{}", host, username);

			session = jsch.getSession(username, host, port);
			log.info("Session is build");
			if (password != null) {
				session.setPassword(password);
			}
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");

			session.setConfig(config);
			session.connect();
			log.info("Session is connected");

			Channel channel = session.openChannel("sftp");
			channel.connect();
			log.info("channel is connected");

			sftp = (ChannelSftp) channel;
			log.info(String.format("sftp server host:[%s] port:[%s] is connect successfull", host, port));
		} catch (JSchException e) {
			e.printStackTrace();
			log.error("Cannot connect to specified sftp server : {}:{} \n Exception message is: {}",
					new Object[] { host, port, e.getMessage() });
		}
	}

	/**
	 * 关闭连接 server
	 */
	public void logout() {
		if (sftp != null) {
			if (sftp.isConnected()) {
				sftp.disconnect();
				log.info("sftp is closed already");
			}
		}
		if (session != null) {
			if (session.isConnected()) {
				session.disconnect();
				log.info("sshSession is closed already");
			}
		}
	}

	/**
	 * 创建文件夹目录
	 * @param directory
	 * @throws SftpException
	 */
	public void mkdirs(String directory) throws SftpException {
    	mkdirs(directory, null);
    }
    
    public void mkdirs(String directory, String directoryParent) throws SftpException {
    	try {    
            sftp.cd(directory);
            log.info("文件夹已存在!directory:{}", directory);
        } catch (SftpException e) { 
        	log.info("文件夹不存在,即将重新创建文件夹:{}", directory);
        	try {    
        		sftp.mkdir(directory);
        		log.info("文件夹创建成功!");
        	} catch (Exception e1) {
        		int lastIndexOf = directory.lastIndexOf("/");
        		if ("0".equals(String.valueOf(lastIndexOf)) || "-1".equals(String.valueOf(lastIndexOf))) {
					log.info("Error-创建文件夹失败-directory:{}", directory);
					return;
				}
        		String directoryExist = directory.substring(0, lastIndexOf);
        		log.info("Info-创建文件夹失败-重新创建文件夹-directoryExist:{},directory:{}", directoryExist, directory);
    			mkdirs(directoryExist, directory);
        	} finally {
        		try {    
                    sftp.cd(directoryParent);  
                    log.info("文件夹已存在!directoryParent:{}", directoryParent);
                } catch (SftpException e1) {
                	if (directoryParent != null) {
                		try {
                			sftp.mkdir(directoryParent);
                			log.info("创建文件夹成功!directoryParent:{}", directoryParent);
                		} catch (Exception e2) {
                			log.info("创建文件夹失败!directoryParent:{}", directoryParent);
                		}
					}
                }
			}
        }
    }
	
	/**
	 * 下载文件
	 * 
	 * @param directory    下载目录
	 * @param downloadFile 下载的文件
	 * @param saveFile     存在本地的路径
	 * @throws SftpException
	 * @throws FileNotFoundException
	 */
	public void downloadFile(String directory, String downloadFile, String saveFile)
			throws SftpException, FileNotFoundException {
		if (directory != null && !StringConstant.EMPTY.equals(directory)) {
			sftp.cd(directory);
		}
		File file = new File(saveFile);
		sftp.get(downloadFile, new FileOutputStream(file));
		log.info("file:{} is download successful", downloadFile);
	}

	/**
	 * 
	 * @param directory
	 * @param downloadFile
	 * @return
	 * @throws SftpException
	 * @throws FileNotFoundException
	 */
	public BufferedInputStream downloadFile(String directory, String downloadFile)
			throws SftpException, FileNotFoundException {
		if (directory != null && !StringConstant.EMPTY.equals(directory)) {
			sftp.cd(directory);
		}
		BufferedInputStream bis = new BufferedInputStream(sftp.get(downloadFile));
		log.info("BufferedInputStream:{} is download successful", downloadFile);
		return bis;
	}

	/**
	 * 下载单个文件
	 * 
	 * @param remotePath：远程下载目录(以路径符号结束)
	 * @param remoteFileName：下载文件名
	 * @param localPath：本地保存目录(以路径符号结束)
	 * @param localFileName：保存文件名
	 * @return
	 */
	public boolean downloadFile(String remotePath, String remoteFileName, String localPath, String localFileName) {
		FileOutputStream fieloutput = null;
		try {
			FileUtil.mkdir(localPath + "/" + localFileName);
			fieloutput = new FileOutputStream(localPath + "/" + localFileName);
			sftp.get(remotePath + "/" + remoteFileName, fieloutput);
			log.info("file:{} is download successful" + remoteFileName);
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SftpException e) {
			e.printStackTrace();
		} finally {
			if (null != fieloutput) {
				try {
					fieloutput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	/**
	 * 批量下载文件
	 * @param remotePath：远程下载目录(以路径符号结束,可以为相对路径eg:/assess/sftp/jiesuan_2/2014/)
	 * @param localPath：本地保存目录(以路径符号结束,D:\Duansha\sftp\)
	 * @param fileFormat：下载文件格式(以特定字符开头,为空不做检验)
	 * @param fileEndFormat：下载文件格式(文件格式)
	 * @return
	 */
	public List<String> batchDownLoadFile(String remotePath, String localPath, String fileFormat,
			String fileEndFormat, String handleTypeOfSame) {
		List<String> filePaths = new ArrayList<String>();
		try {
			login();
			Vector<?> v = listFtpFiles(remotePath);
			if (v.size() > 0) {
				Iterator<?> it = v.iterator();
				while (it.hasNext()) {
					LsEntry entry = (LsEntry) it.next();
					String filename = entry.getFilename();
					SftpATTRS attrs = entry.getAttrs();
					if (!attrs.isDir()) {
						boolean flag = false;
						String localFileName = localPath + "/" + filename;
						// 本地存在此文件, 检查处理类型
						File localFile = new File(localFileName);
						if (localFile.exists()) {
							log.info("本地文件存在,处理类型是-handleTypeOfSame:{}", handleTypeOfSame);
							if (Objects.equals(CONTINUE, handleTypeOfSame)) {
								log.info("跳过此文件下载.");
								continue;
							} else if (Objects.equals(BACKUP, handleTypeOfSame)) {
								log.info("重命名本地文件并下载新文件.");
								boolean isSuccess = FileUtil.renameTo(localFile);
								while (!isSuccess) {
									log.info("本地文件重命名失败,再次重命名本地文件.");
									isSuccess = FileUtil.renameTo(localFile);
								}
							} else {
								handleTypeOfSame = COVER;
							}
							if (Objects.equals(COVER, handleTypeOfSame)) {
								log.info("下载时覆盖本地文件.");
							}
						}
						fileFormat = fileFormat == null ? StringConstant.EMPTY : fileFormat.trim();
						fileEndFormat = fileEndFormat == null ? StringConstant.EMPTY : fileEndFormat.trim();
						// 三种情况
						if (fileFormat.length() > 0 && fileEndFormat.length() > 0) {
							if (filename.startsWith(fileFormat) && filename.endsWith(fileEndFormat)) {
								flag = downloadFile(remotePath, filename, localPath, filename);
								if (flag) {
									filePaths.add(localFileName);
								}
							}
						} else if (fileFormat.length() > 0 && StringConstant.EMPTY.equals(fileEndFormat)) {
							if (filename.startsWith(fileFormat)) {
								flag = downloadFile(remotePath, filename, localPath, filename);
								if (flag) {
									filePaths.add(localFileName);
								}
							}
						} else if (fileEndFormat.length() > 0 && StringConstant.EMPTY.equals(fileFormat)) {
							if (filename.endsWith(fileEndFormat)) {
								flag = downloadFile(remotePath, filename, localPath, filename);
								if (flag) {
									filePaths.add(localFileName);
								}
							}
						} else {
							flag = downloadFile(remotePath, filename, localPath, filename);
							if (flag) {
								filePaths.add(localFileName);
							}
						}
					}
				}
			}
			if (log.isInfoEnabled()) {
				log.info("download file is success:remotePath=" + remotePath + "and localPath=" + localPath
						+ ",file size is:" + filePaths.size());
			}
		} catch (SftpException e) {
			e.printStackTrace();
		} finally {
			this.logout();
		}
		return filePaths;
	}

	public static void main(String[] args) throws SftpException, IOException {
		// 密码登录测试
		// SFTPUtil sftp = new SFTPUtil("sftpuser", "123456", "192.168.100.180", 22);
		/*
		 * sftp.login(); File file = new File("E:/错误.txt"); InputStream is = new
		 * FileInputStream(file); sftp.upload("/test", "错误.txt", is);
		 * sftp.download("/test", "错误.txt", "F:/错误.txt");
		 */

		// 私钥登录测试
		String privateKey = "E:/freeSSHd/sftpuser.ppk"; // 私钥文件所在的路径

		SftpUtils sftp = new SftpUtils("sftpuser", "192.168.100.182", 22, privateKey, "5644082");
		sftp.login();
		File file = new File("E:/错误.txt");
		InputStream is = new FileInputStream(file);
		sftp.uploadFile("/test", "错误.txt", is);
		sftp.logout();
	}

	/**
	 * 上传单个文件
	 * 
	 * @param remotePath：远程保存目录
	 * @param remoteFileName：保存文件名
	 * @param localPath：本地上传目录(以路径符号结束)
	 * @param localFileName：上传的文件名
	 * @return
	 */
	public void uploadFile(String remotePath, String remoteFileName, String localPath, String localFileName)
			throws Exception {
		FileInputStream in = null;
		try {
			mkdirs(remotePath);
			sftp.cd(remotePath);
			File file = new File(localPath + localFileName);
			in = new FileInputStream(file);
			sftp.put(in, remoteFileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SftpException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 将输入流的数据上传到sftp作为文件
	 * 
	 * @param directory    上传到该目录
	 * @param sftpFileName sftp端文件名
	 * @param input        输入流
	 * @throws SftpException
	 * @throws IOException
	 */
	public void uploadFile(String directory, String sftpFileName, InputStream input) throws SftpException, IOException {
		try {
			sftp.cd(directory);
		} catch (SftpException e) {
			log.warn("directory is not exist");
			mkdirs(directory);
			sftp.cd(directory);
		}
		sftp.put(input, sftpFileName);
		input.close();
		log.info("file:{} is upload successful", sftpFileName);
	}

	/**
	 * 将输入流的数据以追加模式上传到sftp作为文件
	 * 
	 * @param directory    上传到该目录
	 * @param sftpFileName sftp端文件名
	 * @throws SftpException
	 * @throws IOException
	 */
	public void appendUploadFile(String directory, String sftpFileName, String filePath) throws SftpException, IOException {
		try {
			sftp.cd(directory);
		} catch (SftpException e) {
			log.warn("directory is not exist");
			sftp.mkdir(directory);
			sftp.cd(directory);
		}
		sftp.put(filePath, sftpFileName, ChannelSftp.APPEND);
		// input.close();
		log.info("file:{} is upload successful", sftpFileName);
	}
	
	/**
	 * 批量上传文件
	 * 
	 * @param remotePath：远程上传目录(以路径符号结束,可以为相对路径eg:/assess/sftp/jiesuan_2/2014/)
	 * @param localPath：本地保存目录(以路径符号结束,D:\Duansha\sftp\)
	 * @param del：下载后是否删除本地文件
	 * @return
	 */
	public void batchUpLoadFile(String remotePath, String localPath, boolean del) throws Exception {
		String fileName = StringConstant.EMPTY;
		try {
			this.login();
			File file = new File(localPath);
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				fileName = files[i].getName();
				this.uploadFile(remotePath, fileName, localPath, files[i].getName());
				if (del) {
					this.deleteLocalFile(localPath, fileName);
				}
			}
		} catch (Exception e) {
			log.error("批量上传文件失败，当前文件名：" + fileName, e);
		} finally {
			this.logout();
		}
	}

	/**
	 * 删除本地文件
	 * 
	 * @param directory  要删除文件所在目录
	 * @param deleteFile 要删除的文件
	 * @throws Exception
	 */
	public void deleteLocalFile(String directory, String deleteFile) throws Exception {
		File file = new File(directory + deleteFile);
		if (!file.exists()) {
			return;
		}

		if (!file.isFile()) {
			return;
		}
		file.delete();
	}
	
	/**
	 * 删除文件
	 * 
	 * @param directory  要删除文件所在目录
	 * @param deleteFile 要删除的文件
	 * @throws SftpException
	 */
	public void deleteFtpFile(String directory, String deleteFile) throws SftpException {
		sftp.cd(directory);
		sftp.rm(deleteFile);
	}

	/**
	 * 列出目录下的文件
	 * 
	 * @param directory 要列出的目录
	 * @return
	 * @throws SftpException
	 */
	public Vector<?> listFtpFiles(String directory) throws SftpException {
		return sftp.ls(directory);
	}

}
