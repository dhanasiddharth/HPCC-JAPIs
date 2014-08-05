package org.hpccsystems.ws.client.utils;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.axis.client.Stub;
import org.apache.axis.encoding.Base64;

public class Connection
{
    public final static String protDelimiter          = "://";
    public final static char   portDelimiter          = ':';
    public final static char   pathDelimiter          = '/';
    public final static char   firstOptDelimiter      = '?';
    public final static char   subsequentOptDelimiter = '&';

    public final static String protHttp               = "http";
    public final static String protHttps              = "https";

    private String             protocol;
    private String             host;
    private String             port;
    private String             path;
    private String[]           options;
    private String             userName;
    private String             password;
    private boolean            isHttps                = false;
    private boolean            allowInvalidCerts      = false;

    private StringBuffer       baseUrl;

    public Connection(String protocol, String host, String port)
    {
        this(protocol, host, port, null, null);
    }

    public Connection(String protocol, String host, String port, String path)
    {
        this(protocol, host, port, path, null);
    }

    public Connection(String protocol_, String host_, String port_,
            String path_, String[] options_)
    {
        if (protocol_ != null && protocol_.length() > 0)
        {
            protocol = protocol_;
        }
        else
        {
            isHttps = true;
            protocol = protHttp;
        }

        host = host_;
        setPort(port_);
        setURIPath(path_);

        options = options_;

        constructUrl();
    }

    private void setPort(String port_)
    {
        if (port_ != null && port_.length() > 0)
            port = port_;
        else
            port = "";

    }

    private void setURIPath(String path)
    {
        if (path != null && path.length() > 0)
        {
            if (path.charAt(0) == pathDelimiter)
                this.path = path;
            else
                this.path = pathDelimiter + path;
        }
        else
            this.path = "";
    }

    private void constructUrl()
    {
        baseUrl = new StringBuffer();
        baseUrl.append(protocol).append(protDelimiter);
        baseUrl.append(host);
        baseUrl.append(port.length() > 0 ? portDelimiter + port : "");
        baseUrl.append(path);

        if (options != null)
        {
            for (int i = 0; i < options.length; i++)
            {
                if (i == 0)
                    baseUrl.append(firstOptDelimiter);
                else
                    baseUrl.append(subsequentOptDelimiter);

                try
                {
                    baseUrl.append(URLEncoder.encode(options[i], "UTF-8"));
                }
                catch (UnsupportedEncodingException e)
                {
                    System.err.println("Warning: could not encode URL option: "
                            + options[i]);
                    baseUrl.append(options[i]);
                }
            }
        }
    }

    public String getUrl()
    {
        return baseUrl.toString();
    }

    public boolean hasCredentials()
    {
        return (userName != null && password != null);
    }

    public String getBasicAuthString()
    {
        String userpass = userName+":"+password;
        return "Basic " + new String(Base64.encode(userpass.getBytes()));
    }

    public String getHost()
    {
        return this.host;
    }

    public String getPort()
    {
        return this.port;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getProtocol()
    {
        return protocol;
    }

    public Boolean getIsHttps()
    {
        return isHttps;
    }

    public boolean getAllowInvalidCerts()
    {
        return allowInvalidCerts;
    }

    public void setAllowInvalidCerts(boolean allowInvalidCerts)
    {
        this.allowInvalidCerts = allowInvalidCerts;
    }

    public void setCredentials(String username, String password)
    {
        this.userName = username;
        this.password = password;
    }

    /**
     *
     */
    public URLConnection createConnection() throws java.io.IOException
    {
        return createConnection(new URL(baseUrl.toString()));
    }

    /**
     * Creates a new <code>java.net.URLConnection</code> object from the
     * specified <code>java.net.URL</code>. This is a convenience method which
     * will set the <code>doInput</code>, <code>doOutput</code>,
     * <code>useCaches</code> and <code>defaultUseCaches</code> fields to the
     * appropriate settings in the correct order.
     *
     */
    public static URLConnection createConnection(URL url)
            throws java.io.IOException
    {

        URLConnection urlConn = url.openConnection();
        if (urlConn instanceof HttpURLConnection)
        {
            HttpURLConnection httpConn = (HttpURLConnection) urlConn;
            httpConn.setRequestMethod("POST");
            HttpURLConnection.setFollowRedirects(false);
        }

        urlConn.setDoOutput(true);
        urlConn.setDoInput(true);
        urlConn.setUseCaches(false);
        urlConn.setDefaultUseCaches(false);
        urlConn.setRequestProperty("Connection", "Keep-Alive");
        urlConn.setRequestProperty("DNT", "1");
        urlConn.setRequestProperty("Accept",  "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        urlConn.setRequestProperty("ENCTYPE", "multipart/form-data");
        urlConn.setAllowUserInteraction(false);

        return urlConn;
    }

    static public String buildUrl(String protocol, String host, String port)
    {
        return buildUrl(protocol, host, port, null, null);
    }

    static public String buildUrl(String protocol, String host, String port, String path)
    {
        return buildUrl(protocol, host, port, path, null);
    }

    static public String buildUrl(String protocol, String host, String port, String path, String[] options)
    {
        StringBuffer url = new StringBuffer();
        url.append((protocol != null && protocol.length() > 0) ? protocol : protHttp).append(protDelimiter);
        url.append(host);
        url.append((port != null && port.length() > 0) ? portDelimiter + port : "");
        url.append((path != null && path.length() > 0) ? (path.charAt(0) == pathDelimiter) ? path : pathDelimiter + path : "");

        if (options != null)
        {
            for (int i = 0; i < options.length; i++)
            {
                if (i == 0)
                    url.append(firstOptDelimiter);
                else
                    url.append(subsequentOptDelimiter);

                try
                {
                    url.append(URLEncoder.encode(options[i], "UTF-8"));
                }
                catch (UnsupportedEncodingException e)
                {
                    System.err.println("Warning: could not encode URL option: " + options[i]);
                    url.append(options[i]);
                }
            }
        }

        return url.toString();
    }

    private static final int        DEFAULT_TIMEOUT             = 180 * 1000;
    private static final boolean    DEFAULT_MAINTAIN_SESSION    = true;
    public static void initStub(Stub stub, String user, String password)
    {
        initStub(stub, user, password, DEFAULT_TIMEOUT);
    }

    public static void initStub(Stub stub, String user, String password, int timeout)
    {
        initStub(stub, user, password, DEFAULT_TIMEOUT, DEFAULT_MAINTAIN_SESSION);
    }

    public static void initStub(Stub stub, String user, String password, int timeout, boolean maintainsession)
    {
        stub.setUsername(user);
        stub.setPassword(password);
        stub.setTimeout(timeout);
        stub.setMaintainSession(maintainsession);
    }
}