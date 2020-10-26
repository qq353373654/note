package ehh.util;

import ctd.util.JSONUtils;
import ehh.his.HisConfig;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.log4j.Logger;
import org.codehaus.xfire.client.Client;

import javax.xml.namespace.QName;
import java.net.HttpURLConnection;
import java.net.URL;


public class WebServiceUtil {
	public static final Logger logger = Logger.getLogger(WebServiceUtil.class);
	public  static String callWBS(String reqJson,String serviceName){
		System.out.print("参数======"+reqJson);
		logger.info("参数======"+reqJson);
		Service service = new Service();
		try {
			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(new java.net.URL(HisConfig.HOST));//地址
			call.setOperationName(new QName("http://tempuri.org/",serviceName));//命名空间   targetNamespace="http://tempurl.org">  <s:element name="appointment">
			logger.info("serviceName:" + serviceName);
			call.setSOAPActionURI("http://tempuri.org/"+serviceName+"/");//命名空间+路径
			call.addParameter(new QName("http://tempurl.org","as_param"), org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);//入参  IN
			call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);// 返回 类型 TYPE
			call.setTimeout(10000);
			logger.info("111111111");
			String result = (String) call.invoke(new Object[]{reqJson});
			System.out.print("222222222信息webservice结果："+result);
			logger.info("222222222信息webservice结果："+result);
			return  result;
		}
		catch (Exception e) {
			System.out.print("调用失败"+e.toString());
			logger.error("调用失败"+e.toString());
			return "";
		}
	}

	public  static String callWBS(String reqJson){



		logger.info("参数======"+reqJson);
		Service service = new Service();
		try {
			Call call = (Call) service.createCall();
			logger.info("调用 WBS  URL ："+ HisConfig.HOST);

			call.setTargetEndpointAddress(new java.net.URL(HisConfig.HOST));
			call.setOperationName(new QName("http://tempuri.org","querydoctorscheduling"));//
			call.setSOAPActionURI("http://tempurl.org/querydoctorscheduling");

			call.addParameter(new QName("http://tempurl.org/","as_param"), org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);//
			call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);//
			call.setTimeout(10000);
			logger.info("111111111");
			String result = (String) call.invoke(new Object[]{reqJson});
			logger.info("222222222信息webservice结果："+result);

			return  result;
		}
		catch (Exception e) {
			logger.error("调用失败"+e.toString());
			return "";
		}

	}


	public static String callWeb2(String reqJson,String serviceName) {
		String URL = "http://localhost:5354/wnapp_webservice/n_wnapp_webservice.asmx";
		String NameSpace = "http://tempuri.org";

		logger.info("webservice参数======" + reqJson);
		Service service = new Service();
		try {
			Call call = (Call) service.createCall();
			logger.info("调用 WBS  URL ：" + URL);
			call.setTargetEndpointAddress(new java.net.URL(URL));
			call.setOperationName(new QName(NameSpace, serviceName));//
			call.setSOAPActionURI(NameSpace + "/" + serviceName+"/");
			call.addParameter(new QName(NameSpace, "as_param"), org.apache.axis.encoding.XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN);
			call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);//
			call.setTimeout(10000);
			logger.info("111111111");
			//
			Object r = call.invoke(new Object[]{reqJson});
			if (r != null) {
				System.out.println(r.toString());
				return r.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("调用失败" + e.toString());
		}
		return null;
	}

	public static String domytest(String request, String serviceName){
		logger.info("参数===="+ request);
		logger.info("HOST_WSDL===="+ HisConfig.HOST);
		try {
			Client c = new Client(new URL(HisConfig.HOST));
			Object[] results = c.invoke(serviceName, new String[]{request});
			return (String) results[0];
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("返回错误信息=======》》》"+e.getMessage());
			return null;
		}
	}

	public static String testWeb(String request){
		try{
			String wsdl = "http://60.191.20.72:20001/XHLISService.asmx?wsdl";
			URL url = new URL(wsdl);
			HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
			httpCon.connect();
			Client client = new Client(httpCon.getInputStream(),null);
			Object[] results = client.invoke("PatientReportFiles", new String[]{request});
			return (String) results[0];
		}catch (Exception e){
			e.printStackTrace();
			logger.error("返回错误信息-----》》》"+ e.getMessage());
		}
        return null;

	}

	public static void main(String [] args){
//		WebServiceUtil.callWBS("{\n" +
//				"\"serviceId\":\"Appointment\",\n" +
//				"\"orgCode\":\"1000423\",\n" +
//				"\"branchId\":\"1000423\",\n" +
//				"\"data\":{\n" +
//				"\"id\":\"111\",\n" +
//				"\"patientName\":\"张宪强\",\n" +
//				"\"schedulingID\":\"1111\",\n" +
//				"\"sourceLevel\":1,\n" +
//				"\"patientType\":\"01\",\n" +
//				"\"departName\":\"心血管内科\",\n" +
//				"\"doctorID\":\"2301\",\n" +
//				"\"doctorName\":\"颜景信\",\n" +
//				"\"price\":10.0,\n" +
//				"\"workType\":\"1\",\n" +
//				"\"patientSex\":\"1\",\n" +
//				"\"workDate\":\"2016-09-30 00:00:00\",\n" +
//				"\"orderNum\":1,\"departCode\":\"205\",\n" +
//				"\"startTime\":\"2016-09-30 08:00:00\",\n" +
//				"\"certID\":\"330481198909072210\",\n" +
//				"\"endTime\":\"2016-09-30 08:30:00\",\n" +
//				"\"mobile\":\"18868744478\",\n" +
//				"\"appointRoad\":0}}\n","appointment");


		/*WebServiceUtil.callWBS("{\n" +
				"    \"serviceId\": \"CancelAppointment\",\n" +
				"    \"orgCode\": \"1000423\",\n" +
				"    \"branchId\": \"1000423\",\n" +
				"    \"systemId\": \"ngari\",\n" +
				"    \"tradeTime\": \"2016-09-28\",\n" +
				"    \"data\": {\n" +
				"        \"appointId\": \"1709611\",\n" +
				"        \"cancelReason\": \"测试原因\",\n" +

				"        \"operateUser\": \"1000\",\n" +

				"    }\n" +
				"}","cancelappointment");*/

		String request = "{\"serviceId\":\"QueryDoctorScheduling\",\"orgCode\":\"1000420\",\"systemId\":\"1\",\"tradeTime\":\"2016-12-02\",\"data\":{\"OrganId\":\"1000420\",\"EndTime\":\"2016-12-09\",\"StartTime\":\"2016-12-02\"}}";
		String serviceName = "PatientReportFilesResponse";
		String message = WebServiceUtil.testWeb(request);
		System.out.println(message);
	}
	
}
