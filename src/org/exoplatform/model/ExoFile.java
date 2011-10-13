package org.exoplatform.model;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.exoplatform.singleton.AccountSetting;
import org.exoplatform.utils.ExoConnectionUtils;
import org.exoplatform.utils.WebdavMethod;

import android.util.Log;

//	File info
public class ExoFile {

  public String  urlStr;     // File URL

  public String  fileName;   // File name

  public String  contentType; // File content type

  public boolean isFolder;   // is folder

  // Default constructors
//  public ExoFile() {
//    urlStr = null;
//    fileName = null;
//    contentType = null;
//  }

  // Construtor
  public ExoFile(String urlString, String file_Name) {
    
    try {
      
      urlStr = urlString;
      fileName = file_Name;
      
      HttpResponse response;
      
      WebdavMethod webDav = new WebdavMethod("PROPFIND", urlStr);
      response = ExoConnectionUtils.httpClient.execute(webDav);
      
      String strResponse = ExoConnectionUtils.convertStreamToString(response.getEntity().getContent());
      int indexOfCollectionType = strResponse.indexOf("<D:collection/>");
      if(indexOfCollectionType > 0)
        isFolder = true;
      else
      {
        isFolder = false;
        int indexOfContentTypeOpen = strResponse.indexOf("D:getcontenttype");
        int indexOfContentTypeClose = strResponse.indexOf("/D:getcontenttype");
        if(indexOfContentTypeOpen > 0 && indexOfContentTypeClose > 0)
          contentType = strResponse.substring(indexOfContentTypeOpen, indexOfContentTypeClose);
        
      }

    } catch (ClientProtocolException e) {
      e.getMessage();
    } catch (IOException e) {
      
    }

  }

}
