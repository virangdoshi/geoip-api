package com.s24.geoip.zeromq;

/**
 * Answers messages. Fast.
 * 
 * @author Shopping24 GmbH, Torsten Bøgh Köster (@tboeghk)
 */
public interface MessageHandler {

   String getBinding();
   
   String reply(String message);
   
}
