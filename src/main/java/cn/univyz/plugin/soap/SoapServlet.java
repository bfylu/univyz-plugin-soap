package cn.univyz.plugin.soap;

import cn.univyz.framework.helper.BeanHelper;
import cn.univyz.framework.helper.ClassHelper;
import cn.univyz.framework.util.CollectionUtil;
import cn.univyz.framework.util.StringUtil;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;

import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import java.util.Set;

/**
 * SOAP Servlet
 *
 * @author bfy
 * @version 1.0.0
 */
@WebServlet(urlPatterns = SoapConstant.SERVLET_URL, loadOnStartup = 0)
public class SoapServlet extends CXFNonSpringServlet {
    @Override
    protected void loadBus(ServletConfig sc) {
        //初始化CXF总线
        super.loadBus(sc);
        Bus bus = getBus();
        BusFactory.setDefaultBus(bus);
        //发布SOAP服务
        publishSoapService();
    }

    private void publishSoapService() {
        //遍历所有标注了SOAP注解的类
        Set<Class<?>> soapClassSet = ClassHelper.getClassSetByAnnotation(Soap.class);
        if (CollectionUtil.isNotEmpty(soapClassSet)) {
            for (Class<?> soapClass : soapClassSet) {
                //获取SOAP地址
                String address = getAddress(soapClass);
                //获取SOAP类的接口
                Class<?> soapInterfaceClass = getSoapInterfaceClass(soapClass);
                //获取SOAP类的实例
                Object soapInstance = BeanHelper.getBean(soapClass);
                //发布SOAP服务
                SoapHelper.publishService(address, soapInterfaceClass, soapInstance);
            }
        }
    }

    private Class<?> getSoapInterfaceClass(Class<?> soapClass) {
        //获取SOAP实现第一个接口作为SOAP服务接口
        return soapClass.getInterfaces()[0];
    }

    private String getAddress(Class<?> soapClass) {
        String address;
        //若SOAP注解的value属性不为空，则获取当前值，否则获取类名
        String soapValue = soapClass.getAnnotation(Soap.class).value();
        if (StringUtil.isNotEmpty(soapValue)) {
            address = soapValue;
        } else {
            address = getSoapInterfaceClass(soapClass).getSimpleName();
        }
        //确保最前面只有一个 /
        if (!address.startsWith("/")) {
            address = "/" + address;
        }
        address = address.replaceAll("\\/+", "/");
        return address;
    }
}
