package com.bjpowernode;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws DocumentException {

        System.out.println( "Hello World!" );

        String stringXml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><bookstore><book><title lang=\"eng\">Harry Potter</title><price>29.99</price></book><book><title lang=\"eng\">Learning XML</title><price>39.95</price></book></bookstore>";

       /* Document document = DocumentHelper.parseText(stringXml);
        *//*Node node = document.selectSingleNode("/bookstore/book[1]/title");
        System.out.println(node.getName());*//*

        Node node1 = document.selectSingleNode("/bookstore/book[1]");
        System.out.println(node1.getName());*/

        String webXml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><web-app><servlet><servlet-name>UserController</servlet-name><servlet-class>com.bjpowernode.crm.settings.web.controller.UserController</servlet-class></servlet><servlet-mapping><servlet-name>UserController</servlet-name><url-pattern>/settings/user/login.do</url-pattern></servlet-mapping><servlet-mapping><servlet-name>UserController</servlet-name><url-pattern>/settings/user/loginTmp.do</url-pattern></servlet-mapping><servlet><servlet-name>ActivityController</servlet-name><servlet-class>com.bjpowernode.crm.workbench.web.controller.ActivityController</servlet-class></servlet><servlet-mapping><servlet-name>ActivityController</servlet-name><url-pattern>/workbench/activity/getUserList.do</url-pattern><url-pattern>/workbench/activity/save.do</url-pattern><url-pattern>/workbench/activity/pageList.do</url-pattern><url-pattern>/workbench/activity/delete.do</url-pattern><url-pattern>/workbench/activity/getUserListAndActivity.do</url-pattern><url-pattern>/workbench/activity/update.do</url-pattern><url-pattern>/workbench/activity/detail.do</url-pattern><url-pattern>/workbench/activity/getRemarkListByAid.do</url-pattern><url-pattern>/workbench/activity/deleteRemark.do</url-pattern><url-pattern>/workbench/activity/saveRemark.do</url-pattern><url-pattern>/workbench/activity/updateRemark.do</url-pattern></servlet-mapping></web-app>";

        String xx = "<?xml version=\"1.0\" encoding=\"utf-8\" ?><returnsms>\n <returnstatus>Success</returnstatus>\n <message>ok</message>\n <remainpoint>-885551</remainpoint>\n <taskID>93011235</taskID>\n <successCounts>1</successCounts></returnsms>";
        Document webXmlDocument1 = DocumentHelper.parseText(xx);
        Node returnstatus = webXmlDocument1.selectSingleNode("//returnstatus");
        if("Success".equals(returnstatus.getText())){
            System.out.println("hhhhhhhhhhhhhhh");
        }

        Document webXmlDocument = DocumentHelper.parseText(webXml);
        List<Node> nodeList = webXmlDocument.selectNodes("//servlet/servlet-name");
        List<String> servletNameList = new ArrayList<>();
        HashMap<Object,Object> resMap = new HashMap<>();

        for (int i = 0; i < nodeList.size(); i++) {
            String servletName = nodeList.get(i).getText();
            servletNameList.add(servletName);
        }

        for (int i = 0; i < nodeList.size(); i++) {
            List<Node> nodes = webXmlDocument.selectNodes("//servlet-mapping/servlet-name");
            for (Node node : nodes) {
                List<String> urlPatternList = new ArrayList<>();
                //servlet-name 匹配
                if(servletNameList.get(i).equals(node.getText())){
                    List<Node> nodesUrlPattern = node.getParent().selectNodes("url-pattern");
                    List<String> urlPatternStringList = new ArrayList<>();
                    for (Node urlPattern : nodesUrlPattern) {

                        urlPatternStringList.add(urlPattern.getText());
                    }
                    Node nodeServletClass = nodeList.get(i).getParent().selectSingleNode("servlet-class");
                    resMap.put(urlPatternStringList, nodeServletClass.getText());
                }
            }
        }
        //System.out.println(servletNameList);
        //System.out.println(resMap);
        HashMap<String,String> tmpMap = new HashMap<>();
        for (Map.Entry<Object, Object> entry : resMap.entrySet()) {
            for (String url : (List<String>) (entry.getKey())) {
                tmpMap.put(url, (String) entry.getValue());
            }
        }
        //System.out.println(tmpMap);
        for (Map.Entry<String, String> entry : tmpMap.entrySet()) {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }


    }
}
