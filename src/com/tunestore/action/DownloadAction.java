package com.tunestore.action;

import java.io.InputStream;
import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.DynaActionForm;

import com.tunestore.util.DBUtil;

import com.tunestore.beans.CD;

import antlr.collections.List;

public class DownloadAction extends Action {
  private static final Log log = LogFactory.getLog(DownloadAction.class);

  private DataSource dataSource;

  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public ActionForward execute(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    DynaActionForm daf = (DynaActionForm)form;
    ActionForward retval = mapping.findForward("success");
    ActionMessages errors = getErrors(request);
    ActionMessages messages = getMessages(request);

    //Check to see the user is logged in
    if (request.getSession(true).getAttribute("USERNAME") == null) {
        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("buy.login"));
        saveErrors(request, errors);
        return mapping.findForward("error");
      }
    //Check to see if the user made the request (XSRF)
    else if (!request.getSession(true).getAttribute("downloadToken").equals(request.getParameter("downloadToken"))) {
      	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("xsrf.badRequest"));
          saveErrors(request, errors);
          return mapping.findForward("error");
      }
    //Check if the user actually bought the CD!
    ///need to check the values
    String msg = "";
    try {
        log.info("Getting CD's");
        List results = DBUtil.getCDsForUser((String)request.getSession(true).getAttribute("USERNAME"), (String)request.getSession(true).getAttribute("USERNAME"));
        boolean owned = false;
        for(int i=0; i<results.size(); i++) {
        	CD cd = (CD)results.get(i);
        	msg=request.getParameter("cd");
        	if(cd.getBits().equals(request.getParameter("cd"))) {
        		owned=true;
        		break;
        	}
        }
        if(owned==false) {
        	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(msg));
            saveErrors(request, errors);
            return mapping.findForward("error");
        }
      } catch (Exception e) {
        e.printStackTrace(response.getWriter());
        throw e;
      }


    try {
      // Try to open the stream first - if there's a goof, it'll be here
      InputStream is = this.getServlet().getServletContext().getResourceAsStream("/WEB-INF/bits/" + request.getParameter("cd"));

      if (is != null) {
        response.setContentType("audio/mpeg");
        response.setHeader("Content-disposition", "attachment; filename=" + daf.getString("cd"));
        byte[] buff = new byte[4096];
        int bread = 0;
        while ((bread = is.read(buff)) >= 0) {
          response.getOutputStream().write(buff, 0, bread);
        }
      } else {
        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("download.error"));
        saveErrors(request, errors);
        return mapping.findForward("error");
      }
    } catch (Exception e) {
      e.printStackTrace();
      errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("download.error"));
      saveErrors(request, errors);
      return mapping.findForward("error");
    }

    return null;
  }
}
