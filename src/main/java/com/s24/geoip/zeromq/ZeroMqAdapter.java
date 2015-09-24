package com.s24.geoip.zeromq;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;

/**
 * Listens to zeromq messages and delegates them to the registered message
 * handlers
 * 
 * @author Shopping24 GmbH, Torsten Bøgh Köster (@tboeghk)
 */
public class ZeroMqAdapter implements InitializingBean, DisposableBean {

   private final Logger logger = LoggerFactory.getLogger(getClass());
   
   @Autowired
   private Collection<MessageHandler> handlers;

   private Context context;
   private ExecutorService executorService;
   
   @Override
   public void afterPropertiesSet() throws Exception {
      checkNotNull(handlers, "Pre-condition violated: handlers must not be null.");
      checkArgument(!handlers.isEmpty(), "Pre-condition violated: expression !handlers.isEmpty() must be true.");
      
      // create zmq context
      this.context = ZMQ.context(1);
      
      // create executor pool
      this.executorService = Executors.newFixedThreadPool(handlers.size());
      
      for (MessageHandler handler : handlers) {
         executorService.submit(new MessageHandlerAdapter(handler, context));
      }
   }
   
   /**
    * Shuts down zero mq receptor pool and context
    */
   @Override
   public void destroy() throws Exception {
      logger.info("Shutting down ømq executor ...");
      executorService.shutdownNow();
      logger.info("Done ...");
   }
}
