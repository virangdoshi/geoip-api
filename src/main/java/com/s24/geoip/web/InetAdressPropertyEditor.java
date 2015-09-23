package com.s24.geoip.web;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.PropertyEditorSupport;

import com.google.common.net.InetAddresses;

public class InetAdressPropertyEditor extends PropertyEditorSupport {

   @Override
   public void setAsText(String text) throws IllegalArgumentException {
      checkNotNull(text, "Pre-condition violated: text must not be null.");

      setValue(InetAddresses.forString(text));
   }

}
