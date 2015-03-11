//
//  ========================================================================
//  Copyright (c) 1995-2015 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//


package org.eclipse.jetty.server.session;

import org.eclipse.jetty.server.SessionIdManager;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.session.infinispan.InfinispanSessionIdManager;
import org.eclipse.jetty.session.infinispan.InfinispanSessionManager;
import org.infinispan.Cache;
import org.infinispan.commons.util.CloseableIteratorSet;

public class InfinispanTestSessionServer extends AbstractTestServer
{
    static int __workers=0;
    


    
    
    public InfinispanTestSessionServer(int port, Cache config)
    {
        this(port, 30, 10, config);
    }
    
  
    
    public InfinispanTestSessionServer(int port, int maxInactivePeriod, int scavengePeriod, Cache config)
    {
        super(port, maxInactivePeriod, scavengePeriod, config);
    }
    
    

    @Override
    public SessionIdManager newSessionIdManager(Object config)
    {
        InfinispanSessionIdManager idManager = new InfinispanSessionIdManager(getServer());
        idManager.setWorkerName("w"+(__workers++));
        idManager.setCache((Cache)config);
        return idManager;
    }

    @Override
    public SessionManager newSessionManager()
    {
        InfinispanSessionManager sessionManager = new InfinispanSessionManager();
        sessionManager.setSessionIdManager((InfinispanSessionIdManager)_sessionIdManager);
        sessionManager.setCache(((InfinispanSessionIdManager)_sessionIdManager).getCache());
        sessionManager.setStaleIntervalSec(1);
        sessionManager.setScavengeInterval(_scavengePeriod);
        
        return sessionManager;
    }

    @Override
    public SessionHandler newSessionHandler(SessionManager sessionManager)
    {
        return new SessionHandler(sessionManager);
    }


    public void dumpCache ()
    {
        Cache cache = ((InfinispanSessionIdManager)_sessionIdManager).getCache();
        if (cache != null)
        {
            System.err.println(cache.getName()+" contains "+cache.size()+" entries");
            CloseableIteratorSet<String> keys = cache.keySet();
            for (String key:keys)
                System.err.println(key + " "+cache.get(key));
        }
    }

    public void clearCache ()
    { 
        Cache cache = ((InfinispanSessionIdManager)_sessionIdManager).getCache();
        if (cache != null)
            cache.clear();
    }

}
