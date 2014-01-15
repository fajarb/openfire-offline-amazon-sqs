<%@ page import="java.util.*,
                 org.jivesoftware.openfire.XMPPServer,
                 org.jivesoftware.util.*,
                 com.uniqapp.openfire.OfflineAmazonSQSPlugin"
    errorPage="error.jsp"
%>

<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>

<%-- Define Administration Bean --%>
<jsp:useBean id="admin" class="org.jivesoftware.util.WebManager"  />
<c:set var="admin" value="${admin.manager}" />
<% admin.init(request, response, session, application, out ); %>

<%  // Get parameters
    boolean save = request.getParameter("save") != null;
    boolean success = request.getParameter("success") != null;
    String awsAccessKey = ParamUtils.getParameter(request, "aws_access_key");
    String awsSecretKey = ParamUtils.getParameter(request, "aws_secret_key");
    boolean enabled = ParamUtils.getBooleanParameter(request, "enabled");

    // OfflineAmazonSQSPlugin plugin = (OfflineAmazonSQSPlugin) XMPPServer.getInstance().getPluginManager().getPlugin("offlineAmazonSQS");
    OfflineAmazonSQSPlugin plugin = (OfflineAmazonSQSPlugin) XMPPServer.getInstance().getPluginManager().getPlugin("offlineamazonsqs");

    // Handle a save
    Map errors = new HashMap();
    if (save) {
       if (errors.size() == 0) {
           plugin.setEnabled(enabled);
           plugin.setAWSAccessKey(awsAccessKey);
           plugin.setAWSSecretKey(awsSecretKey);
           response.sendRedirect("offline-amazon-sqs.jsp?success=true");
           return;
       }
    }

    awsAccessKey = plugin.getAWSAccessKey();
    awsSecretKey = plugin.getAWSSecretKey();
    enabled = plugin.isEnabled();
%>

<html>
    <head>
        <title>Offline Amazon SQS Properties</title>
        <meta name="pageID" content="offline-amazon-sqs"/>
    </head>
    <body>

<%  if (success) { %>

    <div class="jive-success">
    <table cellpadding="0" cellspacing="0" border="0">
    <tbody>
        <tr><td class="jive-icon"><img src="images/success-16x16.gif" width="16" height="16" border="0"></td>
        <td class="jive-icon-label">
            Amazon SQS properties edited successfully.
        </td></tr>
    </tbody>
    </table>
    </div><br>
<% } %>

<form action="offline-amazon-sqs.jsp?save" method="post">

<fieldset>
    <legend>Offline Amazon SQS</legend>
    <div>
    <p>Say what you want to say</p>

    <ul>
        <input type="radio" name="enabled" value="true" id="rb01"
        <%= ((enabled) ? "checked" : "") %>>
        <label for="rb01"><b>Enabled</b> - Offline messages will be sent to Amazon SQS.</label>
        <br>
        <input type="radio" name="enabled" value="false" id="rb02"
         <%= ((!enabled) ? "checked" : "") %>>
        <label for="rb02"><b>Disabled</b> - Offline messages will have default behaviour.</label>
        <br><br>

        <label for="text_secret">AWS Access Key:</label>
        <input type="text" name="aws_access_key" value="<%= awsAccessKey %>" id="text_access" size="50">
        <br><br>

        <label for="text_secret">AWS Secret Key:</label>
        <input type="text" name="aws_secret_key" value="<%= awsSecretKey %>" id="text_secret" size="50">
        <br><br>

    </ul>
    </div>
</fieldset>

<br><br>

<input type="submit" value="Save Settings">
</form>


</body>
</html>