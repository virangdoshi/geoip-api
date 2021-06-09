package com.s24.geoip.web;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.InetAddress;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

import com.google.common.net.InetAddresses;

@Provider
public class InetAdressConverterProvider implements ParamConverterProvider {

   public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
      if(rawType == InetAddress.class) {
         return (ParamConverter<T>) new InetAdressConverter();
     }
     return null;
   }

   static class InetAdressConverter implements ParamConverter<InetAddress> {

      public InetAddress fromString(String value) {
         return InetAddresses.forString(value);
      }

      public String toString(InetAddress value) {
         return value.toString();
      }

   }
}
